package com.sky.md5.com.sky.redis;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @projectName: super-takeout
 * @package: com.sky.md5.com.sky.redis
 * @className: RedisConnTest
 * @author: 749291
 * @description: TODO
 * @date: 4/28/2024 19:40
 * @version: 1.0
 */

@SpringBootTest
public class RedisConnTest {
    @Autowired
    private RedisTemplate redisTemplate;
//    @Test
//    void testConn() {
//        redisTemplate.opsForValue().set("test", "test");
//        System.out.println(redisTemplate.opsForValue().get("test"));
//    }
}
