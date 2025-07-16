package com.example.controller;

import com.example.common.CaptchaUtils;
import com.example.common.Result;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/captcha")
public class CaptchaController {

    /**
     * 生成验证码
     */
    @GetMapping
    public Result generateCaptcha(HttpServletRequest request) {
        HttpSession session = request.getSession();
        String captcha = CaptchaUtils.generateTextCaptcha();
        session.setAttribute("captcha", captcha);
        Map<String, String> result = new HashMap<>();
        result.put("captcha", captcha); // 这里为了方便测试直接返回，实际应用中可返回图片
        return Result.success(result);
    }

    /**
     * 验证验证码
     */
    @PostMapping("/verify")
    public Result verifyCaptcha(@RequestBody Map<String, String> requestMap, HttpServletRequest request) {
        HttpSession session = request.getSession();
        String captcha = (String) session.getAttribute("captcha");
        String inputCaptcha = requestMap.get("captcha");
        String username = requestMap.get("username");
        if (captcha != null && captcha.equals(inputCaptcha)) {
            // 验证用户名是否存在，这里需要根据实际情况添加验证逻辑
            // 示例代码中简单返回成功
            return Result.success();
        } else {
            return Result.error("验证码错误");
        }
    }

    /**
     * 重置密码
     */
    @PostMapping("/resetPassword")
    public Result resetPassword(@RequestBody Map<String, String> requestMap) {
        String username = requestMap.get("username");
        String newPassword = requestMap.get("newPassword");
        // 根据用户名更新密码，这里需要根据实际情况添加更新逻辑
        // 示例代码中简单返回成功
        return Result.success();
    }
}