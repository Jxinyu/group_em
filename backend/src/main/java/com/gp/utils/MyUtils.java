package com.gp.utils;

import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;

/**
 * @author ljb
 * @create 2023/6/16
 */
public class MyUtils {
    public static String PWD(String name, String pwd){
        ByteSource salt = ByteSource.Util.bytes(name);
        SimpleHash encrypt_pwd = new SimpleHash("md5", pwd, salt, 1024);
        return String.valueOf(encrypt_pwd);
    }
}
