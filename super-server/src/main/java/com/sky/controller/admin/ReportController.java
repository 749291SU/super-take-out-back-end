package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.ReportService;
import com.sky.service.impl.ReportServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @projectName: super-takeout
 * @package: com.sky.controller.admin
 * @className: ReportController
 * @author: 749291
 * @description: TODO
 * @date: 5/13/2024 20:56
 * @version: 1.0
 */

@Api(tags = "报表管理")
@RestController
@RequestMapping("/admin/report")
@Slf4j
public class ReportController {

    @Autowired
    private ReportService reportService;
    /**
     * 获取一定时时间段内的营业额
     */
    @ApiOperation("获取一定时时间段内的营业额")
    @GetMapping("turnoverStatistics")
    public Result turnOverStatistics(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        log.info("营业额统计: begin: {}, end: {}", begin, end);

        return Result.success(reportService.turnOverStatistics(begin, end));
    }


    /**
     * 用户统计
     */

}
