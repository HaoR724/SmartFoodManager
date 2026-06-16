package com.example.smartfoodserver;

import com.example.smartfoodserver.entity.UserAccount;
import com.example.smartfoodserver.repository.UserAccountRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;

@SpringBootApplication
public class SmartFoodServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(SmartFoodServerApplication.class, args);
    }

    @Bean
    public CommandLineRunner seedDefaultUser(UserAccountRepository userRepository) {
        return args -> {
            if (!userRepository.existsByUsername("admin")) {
                UserAccount admin = new UserAccount();
                admin.setUsername("admin");
                // 课程设计演示中明文保存；真实项目应使用 BCrypt 等加密方案。
                admin.setPassword("123456");
                admin.setNickname("测试用户");
                admin.setCreatedAt(LocalDateTime.now());
                userRepository.save(admin);
            }
        };
    }
}
