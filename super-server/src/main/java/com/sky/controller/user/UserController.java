package com.sky.controller.user;

import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.properties.JwtProperties;
import com.sky.result.Result;
import com.sky.service.UserService;
import com.sky.utils.JwtUtil;
import com.sky.vo.UserLoginVO;
import io.jsonwebtoken.Claims;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;

/**
 * @projectName: super-takeout
 * @package: com.sky.controller.user
 * @className: UserController
 * @author: 749291
 * @description: TODO
 * @date: 5/1/2024 19:46
 * @version: 1.0
 */

@Api(tags = "用户模块")
@RestController
@RequestMapping("/user/user")
@Slf4j
public class UserController {
    @Resource
    private UserService userService;
    @Resource
    private JwtProperties jwtProperties;
    /**
     * 用户登录
     */
    @ApiOperation(value = "用户登录")
    @PostMapping("/login")
    public Result login(@RequestBody UserLoginDTO userLoginDTO) {
        log.info("用户登录：{}", userLoginDTO);
        User user = userService.login(userLoginDTO);

        // 将user转换为UserLoginVO
        UserLoginVO userLoginVO = new UserLoginVO();
        BeanUtils.copyProperties(user, userLoginVO);

        // 生成jwt
        HashMap<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        String token = JwtUtil.createJWT(jwtProperties.getUserSecretKey(), jwtProperties.getUserTtl(), claims);
        userLoginVO.setToken(token);

        return Result.success(userLoginVO);
    }
}
