package com.aitalking.service;

import com.aitalking.dto.LoginRequest;
import com.aitalking.dto.RegisterRequest;
import com.aitalking.mapper.UserMapper;
import com.aitalking.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.DigestUtils;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserMapper userMapper;

    @Test
    public void testLogin() {
        // 测试登录
        LoginRequest request = new LoginRequest();
        request.setUsername("admin");
        request.setPassword("123456");

        try {
            User user = authService.login(request);
            assertNotNull(user);
            assertEquals("admin", user.getUsername());
            System.out.println("登录成功: " + user.getUsername());
        } catch (Exception e) {
            fail("登录失败: " + e.getMessage());
        }
    }

    @Test
    public void testPasswordEncryption() {
        // 测试密码加密
        String password = "123456";
        String encryptedPassword = DigestUtils.md5DigestAsHex(password.getBytes());
        System.out.println("密码加密结果: " + encryptedPassword);
        assertEquals("e10adc3949ba59abbe56e057f20f883e", encryptedPassword);
    }

    @Test
    public void testUserExists() {
        // 测试用户是否存在
        User user = userMapper.selectByUsername("admin");
        assertNotNull(user);
        System.out.println("用户存在: " + user.getUsername());
        System.out.println("用户密码: " + user.getPassword());
    }
}
