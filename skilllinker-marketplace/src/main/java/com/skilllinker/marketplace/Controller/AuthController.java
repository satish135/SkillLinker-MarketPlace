package com.skilllinker.marketplace.Controller;


import com.skilllinker.marketplace.DTO.UserLoginRequest;
import com.skilllinker.marketplace.DTO.UserRegisterRequest;
import com.skilllinker.marketplace.DTO.UserResponse;
import com.skilllinker.marketplace.Service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody UserRegisterRequest request){
            return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserLoginRequest request){
        return ResponseEntity.ok(authService.login(request));
    }
}
