package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * @projectName: super-takeout
 * @package: com.sky.service.impl
 * @className: UserServiceImpl
 * @author: 749291
 * @description: TODO
 * @date: 5/1/2024 19:48
 * @version: 1.0
 */

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private static final String WX_LOGIN_URL = "https://api.weixin.qq.com/sns/jscode2session";
    @Resource
    private UserMapper userMapper;
    @Resource
    private WeChatProperties weChatProperties;

    @Override
    public User login(UserLoginDTO userLoginDTO) {
        String code = userLoginDTO.getCode();

        // 通过HttpClient发送请求
        String openid = getOpenid(code);

        if (openid == null) {
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }

        User user = userMapper.selectByOpenid(openid);
        if (user == null) {
            user = User.builder()
                    .openid(openid)
                    .createTime(LocalDateTime.now())
                    .build();
            log.info("注册新用户：{}", user);
            userMapper.insert(user);
        }
        return user;
    }

    private String getOpenid(String code) {
        Map<String, String> params = new HashMap<>();
        params.put("appid", weChatProperties.getAppid());
        params.put("secret", weChatProperties.getSecret());
        params.put("js_code", code);
        params.put("grant_type", "authorization_code");

        String response = HttpClientUtil.doGet(WX_LOGIN_URL, params);

        JSONObject jsonObject = (JSONObject) JSON.parse(response);

        String openid = (String) jsonObject.get("openid");
        return openid;
    }
}
