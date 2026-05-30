package com.gp.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.gp.pojo.UserInf;
import com.gp.mapper.UserInfMapper;
import com.gp.service.UserInfService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author gp
 * @since 2023-06-01
 */
@Service
public class UserInfServiceImpl extends ServiceImpl<UserInfMapper, UserInf> implements UserInfService {

    @Resource
    private UserInfMapper userInfMapper;

    @Override
    public PageInfo<UserInf> queryOneUser(UserInf userInf, Integer size, Integer currentPage) {
        PageHelper.startPage(currentPage, size);
        List<UserInf> userInfs = userInfMapper.queryOneUser(userInf);
        return new PageInfo<>(userInfs);
    }

    @Override
    public int addUser(UserInf userInf) {
        return userInfMapper.addUser(userInf);
    }

    @Override
    public int updateUser(UserInf userInf) {
        return userInfMapper.updateUser(userInf);
    }

    @Override
    public int deleteUser(Integer id) {
        return userInfMapper.deleteUser(id);
    }
}
