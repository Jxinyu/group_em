package com.gp.filter;

import com.gp.utils.JWTToken;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.springframework.context.annotation.Configuration;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;

/**
 * jwtfilter
 *
 * @author 15751083927
 * @date 2023/07/03
 */
@Slf4j
@Configuration
public class JWTFilter extends BasicHttpAuthenticationFilter {

    public static final String TOKE_NAME = "AUTH-TOKEN";

    /**
     * 如果带有 token，则对 token 进行检查，否则不通过
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws UnauthorizedException {
        log.info("isAccessAllowed  如果带有 token，则对 token 进行检查，否则不通过");
        //判断请求的请求头是否带上 "Token"
        if (isLoginAttempt(request, response)) {
            //如果存在，则进入 executeLogin 方法执行登入，检查 token 是否正确
            try {
                executeLogin(request, response);
                return true;
            } catch (Exception e) {
                //token 错误
                e.printStackTrace();
                responseError(response, e.getMessage());
            }
        }
        //没有token无法通过过滤器。如果是登录接口应该在ShiroConfig那块放行不经过过滤器
        return false;
    }

    /**
     * 判断用户是否想要登入。
     * 检测 header 里面是否包含 Token 字段
     */
    @Override
    protected boolean isLoginAttempt(ServletRequest request, ServletResponse response) {
        log.info("判断用户是否想要登入。");
        HttpServletRequest req = (HttpServletRequest) request;
        String token = req.getHeader(TOKE_NAME);
        return token != null;
    }

    /**
     * 执行登陆操作
     */
    @Override
    protected boolean executeLogin(ServletRequest request, ServletResponse response) {
        log.info("执行登陆操作");
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String token = httpServletRequest.getHeader(TOKE_NAME);
        JWTToken jwtToken = new JWTToken(token);
        // 提交给realm进行登入，如果错误他会抛出异常并被捕获
        getSubject(request, response).login(jwtToken);
        // 如果没有抛出异常则代表登入成功，返回true
        return true;
    }

    /**
     * 将非法请求跳转到 /unauthorized/**
     */
    private void responseError(ServletResponse response, String message) {
        try {
            HttpServletResponse httpServletResponse = (HttpServletResponse) response;
            //设置编码，否则中文字符在重定向时会变为空字符串
            message = URLEncoder.encode(message, "UTF-8");
            //System.out.println(message);
            log.info("非法请求");
            httpServletResponse.sendRedirect("/unauthenticated/400");
        } catch (IOException e) {
            throw new RuntimeException("将非法请求跳转到");
        }
    }
}
