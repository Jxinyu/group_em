package com.gp.controller;

import com.gp.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;


/**
 * 身份验证异常控制器
 * shiro
 *
 * @author ljb
 */
@RestControllerAdvice
@Slf4j
public class ExceptionController {
    /**
     * 登录认证异常
     */
    @ExceptionHandler({UnauthenticatedException.class})
    public R authenticationException() {
        log.warn("登录认证异常");
        return R.error(400, "登录认证失败，请重新登录");
    }

    /**
     * 权限异常
     */
    @ExceptionHandler({AuthorizationException.class})
    public R authorizationException() {
        log.warn("权限异常");
        return R.error(400, "权限不足");
    }

    /**
     * 方法参数无效异常
     */
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public R methodArgumentNotValidException(MethodArgumentNotValidException methodArgumentNotValidException) {
        List<FieldError> fieldErrors = methodArgumentNotValidException.getFieldErrors();
        StringBuilder res = new StringBuilder();
        for (FieldError fieldError : fieldErrors) {
            res.append(fieldError.getDefaultMessage()).append(" and ");
        }
        res.delete(res.length() - 5, res.length());
        return R.error(400, String.valueOf(res));
    }

}
