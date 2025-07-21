package com.example.controller;

import com.example.common.CaptchaUtils;
import com.example.common.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/captcha")
public class CaptchaController {

    // 模拟存储验证码，实际应用可考虑使用 Redis 等缓存
    private static final Map<String, String> captchaStore = new HashMap<>();

    // 验证码有效期（毫秒）
    private static final long CAPTCHA_EXPIRATION = 5 * 60 * 1000; // 5分钟

    // 存储验证码生成时间 (sessionId -> timestamp)
    private static final Map<String, Long> captchaTimestamps = new ConcurrentHashMap<>();

    /**
     * 生成验证码
     */
    @GetMapping("")
    public Result generateCaptcha() {
        // 生成唯一的 sessionId（替代传统的 HttpSession）
        String sessionId = UUID.randomUUID().toString();

        // 生成验证码
        String captcha = CaptchaUtils.generateTextCaptcha();

        // 存储验证码及其生成时间
        captchaStore.put(sessionId, captcha);
        captchaTimestamps.put(sessionId, System.currentTimeMillis());

        // 返回 sessionId 和验证码图片（实际项目中应返回图片URL或Base64）
        Map<String, Object> result = new HashMap<>();
        result.put("sessionId", sessionId);
        // 开发阶段可返回明文验证码用于调试，上线前必须移除
        result.put("captcha", captcha);

        return Result.success(result);
    }

    /**
     * 验证验证码
     */
    @PostMapping("/verify")
    public Result verifyCaptcha(@RequestBody Map<String, String> requestMap) {
        try {
            // 获取请求中的 sessionId 和用户输入的验证码
            String sessionId = requestMap.get("sessionId");
            String inputCaptcha = requestMap.get("captcha");

            // 验证参数是否存在
            if (sessionId == null || inputCaptcha == null) {
                return Result.error("参数缺失");
            }

            // 检查验证码是否存在且未过期
            String storedCaptcha = captchaStore.get(sessionId);
            Long timestamp = captchaTimestamps.get(sessionId);

            if (storedCaptcha == null || timestamp == null) {
                return Result.error("验证码已过期，请重新获取");
            }

            // 检查验证码是否过期（5分钟有效期）
            if (System.currentTimeMillis() - timestamp > CAPTCHA_EXPIRATION) {
                captchaStore.remove(sessionId);
                captchaTimestamps.remove(sessionId);
                return Result.error("验证码已过期，请重新获取");
            }

            // 验证验证码（忽略大小写）
            if (!storedCaptcha.equalsIgnoreCase(inputCaptcha)) {
                return Result.error("验证码错误");
            }

            // 验证通过后，立即删除验证码，防止重复使用
            captchaStore.remove(sessionId);
            captchaTimestamps.remove(sessionId);

            return Result.success();
        } catch (Exception e) {
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
