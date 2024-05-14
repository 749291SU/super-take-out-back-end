package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.*;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.service.OrderService;
import com.sky.service.ReportService;
import com.sky.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @projectName: super-takeout
 * @package: com.sky.service.impl
 * @className: DishServiceImpl
 * @author: 749291
 * @description: TODO
 * @date: 4/27/2024 15:36
 * @version: 1.0
 */

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Override
    public TurnoverReportVO turnOverStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        List<Double> turnoverList = new ArrayList<>();
        for (LocalDate date = begin; ; date = date.plusDays(1)) {
            dateList.add(date);
            HashMap<String, Object> params = new HashMap<>();
            params.put("begin", LocalDateTime.of(date, LocalTime.MIN));
            params.put("end", LocalDateTime.of(date, LocalTime.MAX));
            params.put("status", Orders.COMPLETED);

            // 根据当天的起始时间点和结束时间点来查询完成了的订单总金额
            Double turnover = orderMapper.getTurnoverByMap(params);
            turnover = turnover == null ? 0.0 : turnover;

            turnoverList.add(turnover);
            if (date.equals(end)) {
                break;
            }
        }
        // 全部转化为String
        TurnoverReportVO turnoverReportVO = new TurnoverReportVO(
                StringUtils.join(dateList, ","),
                StringUtils.join(turnoverList, ",")
        );
        return turnoverReportVO;
    }

    @Override
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        List<Integer> totalUserList = new ArrayList<>();
        List<Integer> newUserList = new ArrayList<>();

        for (LocalDate date = begin; ; date = date.plusDays(1)) {
            dateList.add(date);
            HashMap<String, Object> params1 = new HashMap<>();
            params1.put("begin", LocalDateTime.of(date, LocalTime.MIN));
            params1.put("end", LocalDateTime.of(date, LocalTime.MAX));
            // 查询当天新增用户数
            Integer newUser = userMapper.getUserAmountByMap(params1);
            newUser = newUser == null ? 0 : newUser;
            newUserList.add(newUser);

            HashMap<String, Object> params2 = new HashMap<>();
            params2.put("end", LocalDateTime.of(date, LocalTime.MAX));
            // 查询当天总用户数
            Integer totalUser = userMapper.getUserAmountByMap(params2);
            totalUser = totalUser == null ? 0 : totalUser;
            totalUserList.add(totalUser);
            if (date.equals(end)) {
                break;
            }
        }

        return new UserReportVO(
                StringUtils.join(dateList, ","),
                StringUtils.join(totalUserList, ","),
                StringUtils.join(newUserList, ",")
        );
    }

    @Override
    public OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        List<Integer> orderCountList = new ArrayList<>();
        List<Integer> validOrderCountList = new ArrayList<>();
        Integer totalOrderCount = 0;
        Integer validOrderCount = 0;

        for (LocalDate date = begin; ; date = date.plusDays(1)) {
            dateList.add(date);
            HashMap<String, Object> params = new HashMap<>();
            params.put("begin", LocalDateTime.of(date, LocalTime.MIN));
            params.put("end", LocalDateTime.of(date, LocalTime.MAX));
            // 查询当天订单总数
            Integer orderCount = orderMapper.getOrderNumberByMap(params);
            orderCount = orderCount == null ? 0 : orderCount;
            orderCountList.add(orderCount);
            totalOrderCount += orderCount;

            // 查询当天有效订单数
            params.put("status", Orders.COMPLETED);
            Integer validOrderCountToday = orderMapper.getOrderNumberByMap(params);
            validOrderCountToday = validOrderCountToday == null ? 0 : validOrderCountToday;
            validOrderCountList.add(validOrderCountToday);
            validOrderCount += validOrderCountToday;

            if (date.equals(end)) {
                break;
            }
        }

        Double orderCompletionRate = totalOrderCount == 0 ? 0 : (double) validOrderCount / totalOrderCount;

        return new OrderReportVO(
                StringUtils.join(dateList, ","),
                StringUtils.join(orderCountList, ","),
                StringUtils.join(validOrderCountList, ","),
                totalOrderCount,
                validOrderCount,
                orderCompletionRate
        );
    }

    @Override
    public SalesTop10ReportVO getTop10(LocalDate begin, LocalDate end) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("begin", LocalDateTime.of(begin, LocalTime.MIN));
        params.put("end", LocalDateTime.of(end, LocalTime.MAX));
        params.put("status", Orders.COMPLETED);
        List<OrderDetail> orderDetails = orderDetailMapper.selectByMap(params);

        HashMap<String, Integer> dishSalesMap = new HashMap<>();
        for (OrderDetail orderDetail : orderDetails) {
            if (orderDetail == null) {
                continue;
            }
            String name = orderDetail.getName();
            Integer number = orderDetail.getNumber();
            dishSalesMap.put(orderDetail.getName(), dishSalesMap.getOrDefault(name, 0) + number);
        }

        List<String> nameList = new ArrayList<>();
        List<Integer> numberList = new ArrayList<>();
        dishSalesMap.entrySet().stream()
                .sorted((o1, o2) -> o2.getValue().compareTo(o1.getValue()))
                .limit(10)
                .forEach(entry -> {
                    nameList.add(entry.getKey());
                    numberList.add(entry.getValue());
                });

        return new SalesTop10ReportVO(
                StringUtils.join(nameList, ","),
                StringUtils.join(numberList, ",")
        );
    }
}