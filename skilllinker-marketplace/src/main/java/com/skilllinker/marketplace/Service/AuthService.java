package com.skilllinker.marketplace.Service;

import com.skilllinker.marketplace.DTO.UserLoginRequest;
import com.skilllinker.marketplace.DTO.UserRegisterRequest;
import com.skilllinker.marketplace.DTO.UserResponse;

public interface AuthService {

    UserResponse register(UserRegisterRequest request);
    String login(UserLoginRequest request);
}
