package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

/**
 * @projectName: super-takeout
 * @package: com.sky.controller.admin
 * @className: CommonController
 * @author: 749291
 * @description: TODO
 * @date: 4/27/2024 11:49
 * @version: 1.0
 */

@RestController
@Slf4j
@RequestMapping("/admin/common")
@Api(tags = "公共模块")
public class CommonController {
    @Autowired
    private AliOssUtil aliOssUtil;
    /**
     * 文件上传
     */
    @ApiOperation(value = "文件上传")
    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file)  {
        log.info("文件开始上传: {}", file);

        try {
            String originalFilename = file.getOriginalFilename();
            // 获取后缀名
            String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
            String objectName = UUID.randomUUID().toString() + suffix;

            // 上传文件
            String filePath = aliOssUtil.upload(file.getBytes(), objectName);
            log.info("文件上传成功: {}", filePath);

            return Result.success(filePath);
        } catch (IOException e) {
            log.error("文件上传失败: {}", e.getMessage());
            return Result.error(MessageConstant.UPLOAD_FAILED);
        }
    }

}
