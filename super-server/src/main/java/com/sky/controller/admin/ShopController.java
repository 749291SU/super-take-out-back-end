package com.sky.controller.admin;

/**
 * @projectName: super-takeout
 * @package: com.sky.controller.admin
 * @className: ShopController
 * @author: 749291
 * @description: TODO
 * @date: 4/28/2024 19:48
 * @version: 1.0
 */

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

/**
 *
 */
@Api(tags = "商家管理")
@RestController("adminShopController")
@RequestMapping("/admin/shop")
@Slf4j
public class ShopController {
    @Autowired
    private RedisTemplate redisTemplate;
    private final String KEY = "SHOP_STATUS";
    /**
     * 查询status
     */
    @ApiOperation(value = "查询status")
    @GetMapping("/status")
    public Result<Integer> status() {
        Integer status = (Integer) redisTemplate.opsForValue().get(KEY);
        log.info("查询status: {}", status);
        return Result.success(status);
    }


    /**
     * 修改status
     */
    @ApiOperation(value = "修改status")
    @PutMapping("/{status}")
    public Result updateStatus(@PathVariable("status") Integer status) {
        log.info("修改status: {}", status);

        redisTemplate.opsForValue().set(KEY, status);

        return Result.success();
    }

}
