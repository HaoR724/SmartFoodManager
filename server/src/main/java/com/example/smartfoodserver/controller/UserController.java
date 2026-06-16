package com.example.smartfoodserver.controller;

import com.example.smartfoodserver.dto.UserRequest;
import com.example.smartfoodserver.dto.UserResponse;
import com.example.smartfoodserver.entity.UserAccount;
import com.example.smartfoodserver.repository.UserAccountRepository;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserAccountRepository userRepository;

    public UserController(UserAccountRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public UserResponse register(@RequestBody UserRequest request) {
        String username = safeTrim(request.getUsername());
        String password = safeTrim(request.getPassword());
        String nickname = safeTrim(request.getNickname());
        if (username.isEmpty() || password.isEmpty()) {
            return new UserResponse(false, "用户名和密码不能为空");
        }
        if (userRepository.existsByUsername(username)) {
            return new UserResponse(false, "用户名已存在");
        }

        UserAccount user = new UserAccount();
        user.setUsername(username);
        // 课程设计演示中明文保存；真实项目应使用 BCrypt 等加密方案。
        user.setPassword(password);
        user.setNickname(nickname.isEmpty() ? username : nickname);
        user.setCreatedAt(LocalDateTime.now());
        UserAccount saved = userRepository.save(user);
        return toSuccess("注册成功", saved);
    }

    @PostMapping("/login")
    public UserResponse login(@RequestBody UserRequest request) {
        String username = safeTrim(request.getUsername());
        String password = safeTrim(request.getPassword());
        if (username.isEmpty() || password.isEmpty()) {
            return new UserResponse(false, "用户名和密码不能为空");
        }

        Optional<UserAccount> userOptional = userRepository.findByUsername(username);
        if (!userOptional.isPresent() || !password.equals(userOptional.get().getPassword())) {
            return new UserResponse(false, "用户名或密码错误");
        }
        return toSuccess("登录成功", userOptional.get());
    }

    private UserResponse toSuccess(String message, UserAccount user) {
        return new UserResponse(true, message, user.getId(), user.getUsername(), user.getNickname());
    }

    private String safeTrim(String value) {
        return value == null ? "" : value.trim();
    }
}
