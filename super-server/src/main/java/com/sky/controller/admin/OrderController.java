package com.sky.controller.admin;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @projectName: super-takeout
 * @package: com.sky.controller.admin
 * @className: OrderController
 * @author: 749291
 * @description: TODO
 * @date: 5/9/2024 17:10
 * @version: 1.0
 */

@Api(tags = "管理员订单模块")
@RestController("adminOrderController")
@RequestMapping("/admin/order")
@Slf4j
public class OrderController {
}
