package com.gp;

import com.gp.mapper.DocumentInfMapper;
import com.gp.pojo.DocumentInf;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
class GroupEmApplicationTests {

    @Resource
    DocumentInfMapper documentInfMapper;


    @Test
    void contextLoads() {
        ByteSource salt = ByteSource.Util.bytes("123123123");
        SimpleHash encrypt_pwd = new SimpleHash("md5", "123123123", salt, 1024);
        System.out.println(encrypt_pwd);
    }

}
