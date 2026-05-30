package com.gp.config;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gp.mapper.UserInfMapper;
import com.gp.pojo.UserInf;
import com.gp.utils.JWTToken;
import com.gp.utils.JWTUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.Set;
/**
 * @author 10854539
 */
@Component
@Slf4j
public class CustomRealm extends AuthorizingRealm {
    @Resource
    private UserInfMapper userInfMapper;

    private final QueryWrapper<UserInf> wrapper = new QueryWrapper<>();

    /**
     * 必须重写此方法，不然会报错
     */
    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JWTToken;
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        log.info("————权限认证————");
        String username = JWTUtil.getUsername(principals.toString());
        SimpleAuthorizationInfo  info = new SimpleAuthorizationInfo();
        //获得该用户角色也是权限，没有做分别
        wrapper.eq("Loginname", username);
        String role = String.valueOf(userInfMapper.selectOne(wrapper).getStatus());
        //每个角色拥有默认的权限
        String rolePermission = "2";
        //每个用户可以设置新的权限
        Set<String> roleSet = new HashSet<>();
        Set<String> permissionSet = new HashSet<>();
        //需要将 role, permission 封装到 Set 作为 info.setRoles(), info.setStringPermissions() 的参数
        roleSet.add(role);
        permissionSet.add(rolePermission);
        permissionSet.add(role);
        //设置该用户拥有的角色和权限
        info.setRoles(roleSet);
        info.setStringPermissions(permissionSet);
        return info;
    }
    /**
     * 获取身份验证信息
     * Shiro中，最终是通过 Realm 来获取应用程序中的用户、角色及权限信息的。
     *
     * @param authenticationToken 用户身份信息 token
     * @return 返回封装了用户信息的 AuthenticationInfo 实例
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        log.info("————身份认证方法————");
        // 获取token
        String token = (String)authenticationToken.getCredentials();
        //获取用户名
        String userName= JWTUtil.getUsername(token);
        //检验用户名和token认证
        if(userName==null || !JWTUtil.verify(token, userName)){
            throw new AuthenticationException("token认证失败！");
        }
        // 从数据库获取对应用户名密码的用户
        wrapper.clear();
        wrapper.eq("Loginname", userName);
        String password =userInfMapper.selectOne(wrapper).getPassword();
        if (null == password) {
            throw new AuthenticationException("该用户不存在！");
        }
        // int ban =userMapper.checkUserBanStatus(userName);
        // if (ban == 1) {
        //     throw new AuthenticationException("该用户已被封号！");
        // }
        return new SimpleAuthenticationInfo(token, token, "MyRealm");
    }

    /**
     * 是允许
     *
     * @param principals 校长
     * @param permission 许可
     * @return boolean
     */
    @Override
    public boolean isPermitted(PrincipalCollection principals, String permission) {
        return super.isPermitted(principals, permission);
    }
}
