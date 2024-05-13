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
import com.sky.vo.DishVO;
import com.sky.vo.TurnoverReportVO;
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
    @Override
    public TurnoverReportVO turnOverStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        List<BigDecimal> turnoverList = new ArrayList<>();
        for (LocalDate date = begin; ; date = date.plusDays(1)) {
            dateList.add(date);
            HashMap<String, Object> params = new HashMap<>();
            params.put("begin", LocalDateTime.of(date, LocalTime.MIN));
            params.put("end", LocalDateTime.of(date, LocalTime.MAX));
            params.put("status", Orders.COMPLETED);

            // 根据当天的起始时间点和结束时间点来查询完成了的订单总金额
            BigDecimal turnover = orderMapper.getTurnoverByMap(params);
            turnover = turnover == null ? BigDecimal.ZERO : turnover;

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
}