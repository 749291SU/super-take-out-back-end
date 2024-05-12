package com.sky.task.order;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @projectName: super-takeout
 * @package: com.sky.task.order
 * @className: OrderTask
 * @author: 749291
 * @description: TODO
 * @date: 5/12/2024 15:45
 * @version: 1.0
 */

@Component
@Slf4j
public class OrderTask {
    @Autowired
    private OrderMapper orderMapper;
    /**
     * 每隔1分钟处理超时未支付订单
     */
    @Scheduled(cron = "0 * * * * ?")
    public void processTimeoutOrder() {
        log.info("处理超时未支付订单", LocalDateTime.now());

        // 获取超时了的订单
        List<Orders> list = orderMapper.selectByEntity(Orders.builder().status(Orders.PENDING_PAYMENT).build())
                .stream()
                .filter(order -> order.getOrderTime().plusMinutes(15).isBefore(LocalDateTime.now()))
                .collect(Collectors.toList());

        // 更新订单状态
        list.forEach(order -> {
            order.setStatus(Orders.CANCELLED);
            order.setCancelReason("订单超时未支付");
            orderMapper.updateById(order);
            log.info("订单号：{}，超时未支付，取消订单", order.getNumber());
        });
    }

    /**
     * 处理一直处于派送中状态的订单(每天凌晨1点执行一次)
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void processDeliveryOrder() {
        log.info("处理一直处于派送中状态的订单", LocalDateTime.now());

        // 获取超时了的订单
        List<Orders> list = orderMapper.selectByEntity(Orders.builder().status(Orders.DELIVERY_IN_PROGRESS).build())
                .stream()
                .filter(order -> order.getOrderTime().plusHours(1).isBefore(LocalDateTime.now()))
                .collect(Collectors.toList());

        // 更新订单状态
        list.forEach(order -> {
            order.setStatus(Orders.COMPLETED);
            orderMapper.updateById(order);
            log.info("订单号：{}，派送中状态超过1天，自动完成订单", order.getNumber());
        });
    }
}
