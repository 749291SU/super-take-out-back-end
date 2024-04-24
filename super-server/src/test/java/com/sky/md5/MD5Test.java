package com.sky.md5;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.DigestUtils;

/**
 * @projectName: super-takeout
 * @package: com.sky.md5
 * @className: MD5Test
 * @author: 749291
 * @description: TODO
 * @date: 4/16/2024 16:18
 * @version: 1.0
 */

@SpringBootTest
public class MD5Test {
    @Test
    public void testMD5() {
        String password = "123456";
        System.out.println(DigestUtils.md5DigestAsHex(password.getBytes()));
    }
}
