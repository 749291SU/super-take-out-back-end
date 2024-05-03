package com.sky.service;

import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.vo.UserLoginVO;

import java.io.IOException;

public interface UserService {
    User login(UserLoginDTO userLoginDTO);
}
