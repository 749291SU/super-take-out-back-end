package com.sky.controller.user;

import com.sky.dto.ShoppingCartDTO;
import com.sky.result.Result;
import com.sky.service.ShoppingCartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @projectName: super-takeout
 * @package: com.sky.controller.user
 * @className: ShoppingCartController
 * @author: 749291
 * @description: TODO
 * @date: 5/8/2024 19:11
 * @version: 1.0
 */

@Api(tags = "用户购物车相关接口")
@RestController
@RequestMapping("/user/shoppingCart")
@Slf4j
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;
    /**
     * 添加菜品或者套餐到购物车
     */
    @ApiOperation(value = "添加菜品或者套餐到购物车")
    @PostMapping("/add")
    public Result add(@RequestBody ShoppingCartDTO shoppingCartDTO) {
        log.info("添加菜品或者套餐到购物车,shoppingCartDTO:{}", shoppingCartDTO);
        shoppingCartService.add(shoppingCartDTO);
        return Result.success();
    }


    /**
     * 列出购物出所有菜品和套餐
     */
//    @ApiOperation(value = "列出购物出所有菜品和套餐")
//    @GetMapping("/list")
//    public Result list() {
//        log.info("列出购物出所有菜品和套餐");
//        return Result.success(shoppingCartService.list());
//    }
}
