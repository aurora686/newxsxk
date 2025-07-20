package com.example.controller;

import com.example.common.CaptchaUtils;
import com.example.common.Result;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/captcha")
public class CaptchaController {

    // 模拟存储验证码，实际应用可考虑使用 Redis 等缓存
    private static final Map<String, String> captchaStore = new HashMap<>();

    /**
     * 生成验证码
     */
    @GetMapping("/captcha")
    public Result generateCaptcha(HttpServletRequest request) {
        HttpSession session = request.getSession();
        // 使用 session 存储验证码
        String captcha = CaptchaUtils.generateTextCaptcha();
        session.setAttribute("captcha", captcha);

        return Result.success(captcha);
    }

    /**
     * 验证验证码
     */
    @PostMapping("/verify")
    public Result verifyCaptcha(@RequestBody Map<String, String> requestMap) {
        try {
            // 获取 sessionId 和用户输入的验证码
            String sessionId = requestMap.get("sessionId");
            String inputCaptcha = requestMap.get("captcha");
            String username = requestMap.get("username");

            // 从模拟存储中获取存储的验证码
            String captcha = captchaStore.get(sessionId);

            // 检查验证码是否为空
            if (captcha == null) {
                return Result.error("验证码已过期，请重新获取");
            }

            // 检查用户输入的验证码是否正确
            if (captcha.equals(inputCaptcha)) {
                // 验证用户名是否存在，这里需要根据实际情况添加验证逻辑
                // 示例代码中简单返回成功
                // 可以在这里添加数据库查询逻辑验证用户名
                return Result.success();
            } else {
                return Result.error("验证码错误");
            }
        } catch (Exception e) {
            // 打印异常信息，方便排查问题
            e.printStackTrace();
            return Result.error("验证码验证失败，请稍后再试");
        }
    }

    /**
     * 重置密码
     */
    @PostMapping("/resetPassword")
    public Result resetPassword(@RequestBody Map<String, String> requestMap) {
        try {
            // 获取用户名
            String username = requestMap.get("username");
            // 获取新密码
            String newPassword = requestMap.get("newPassword");

            // 根据用户名更新密码，这里需要根据实际情况添加更新逻辑
            // 示例代码中简单返回成功
            // 可以在这里添加数据库更新逻辑
            return Result.success();
        } catch (Exception e) {
            // 打印异常信息，方便排查问题
            e.printStackTrace();
            return Result.error("重置密码失败，请稍后再试");
        }
    }
}
