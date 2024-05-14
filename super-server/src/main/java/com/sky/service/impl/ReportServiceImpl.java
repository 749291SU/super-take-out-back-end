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
import com.sky.service.WorkspaceService;
import com.sky.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
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
    @Autowired
    private WorkspaceService workspaceService;

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

    @Override
    public void export(HttpServletResponse httpServletResponse) {
        // 查询营业数据(最近30天)
        BusinessDataVO businessData = workspaceService.getBusinessData(
                LocalDateTime.now().minusDays(30).with(LocalTime.MIN),
                LocalDateTime.now().minusDays(1).with(LocalTime.MAX)
        );


        // 将数据通过POI写入到Excel中
        try {
            // 读取模板
            XSSFWorkbook sheets = new XSSFWorkbook(this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx"));

            // 写入时间段
            sheets.getSheetAt(0).getRow(1).getCell(1).setCellValue("时间：" + LocalDateTime.now().minusDays(30).toLocalDate().toString()
                            + "至" + LocalDateTime.now().minusDays(1).toLocalDate().toString());
            // 写入数据
            // 概览数据
            // 营业额
            sheets.getSheetAt(0).getRow(3).getCell(2).setCellValue(businessData.getTurnover());
            // 订单完成率
            sheets.getSheetAt(0).getRow(3).getCell(4).setCellValue(businessData.getOrderCompletionRate());
            // 新增用户数
            sheets.getSheetAt(0).getRow(3).getCell(6).setCellValue(businessData.getNewUsers());
            // 有效订单数
            sheets.getSheetAt(0).getRow(4).getCell(2).setCellValue(businessData.getValidOrderCount());
            // 平均客单价
            sheets.getSheetAt(0).getRow(4).getCell(4).setCellValue(businessData.getUnitPrice());

            // 明细数据
            // 获取查询时间段每一天的营业额，有效订单数，订单完成率，平均客单价，新增用户数
            // 日期列表
            List<LocalDate> dateList = new ArrayList<>();
            // BusinessVoList
            List<BusinessDataVO> businessDataList = new ArrayList<>();
            for (LocalDate date = LocalDate.now().minusDays(30); ; date = date.plusDays(1)) {
                dateList.add(date);
                BusinessDataVO businessDataVO = workspaceService.getBusinessData(
                        LocalDateTime.of(date, LocalTime.MIN),
                        LocalDateTime.of(date, LocalTime.MAX)
                );
                businessDataList.add(businessDataVO);
                if (date.equals(LocalDate.now().minusDays(1))) {
                    break;
                }
            }

            // 写入明细数据
            int curRow = 7;
            for (int i = 0; i < businessDataList.size(); i++) {
                BusinessDataVO businessDataVO = businessDataList.get(i);
                sheets.getSheetAt(0).getRow(curRow).getCell(1).setCellValue(dateList.get(i).toString());
                sheets.getSheetAt(0).getRow(curRow).getCell(2).setCellValue(businessDataVO.getTurnover());
                sheets.getSheetAt(0).getRow(curRow).getCell(3).setCellValue(businessDataVO.getValidOrderCount());
                sheets.getSheetAt(0).getRow(curRow).getCell(4).setCellValue(businessDataVO.getOrderCompletionRate());
                sheets.getSheetAt(0).getRow(curRow).getCell(5).setCellValue(businessDataVO.getUnitPrice());
                sheets.getSheetAt(0).getRow(curRow).getCell(6).setCellValue(businessDataVO.getNewUsers());
                curRow++;
            }

            // 写入到输出流
            sheets.write(httpServletResponse.getOutputStream());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}