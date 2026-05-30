package com.gp.utils;

import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * r
 * 数据返回类封装
 */

public class R extends HashMap<String, Object> {

    public R() {
        put("code", 200);
        put("message", "success");
    }


    public static R error() {
        return error(HttpStatus.NOT_FOUND.value(), "未知异常，请联系管理员");
    }

    public static R error(String msg) {
        return error(HttpStatus.NOT_FOUND.value(), msg);
    }

    public static R error(int code, String msg) {
        R r = new R();
        r.put("code", code);
        r.put("message", msg);
        return r;
    }

    public static R ok(String msg) {
        R r = new R();
        r.put("message", msg);
        return r;
    }

    public static R ok(Map<String, Object> map) {
        R r = new R();
        r.put("data", map);
        return r;
    }

    public static R ok(Integer code, String msg, String data) {
        R r = new R();
        r.put("code", code);
        r.put("message", msg);
        r.put("data", data);
        return r;
    }

    public static R ok() {
        return new R();
    }

    public R put(String key, Object value) {
        super.put(key, value);
        return this;
    }
}