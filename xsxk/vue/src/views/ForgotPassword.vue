<template>
  <div class="forgot-password-container">
    <div class="forgot-password-box">
      <div class="forgot-password-title">
        <span class="system-name">忘记密码</span>
      </div>
      <el-form
          :model="data.form"
          ref="formRef"
          :rules="data.rules"
          class="forgot-password-form"
      >
        <!-- 用户名输入 -->
        <el-form-item prop="username">
          <el-input
              :prefix-icon="User"
              size="large"
              v-model="data.form.username"
              placeholder="请输入账号"
              class="custom-input"
          />
        </el-form-item>

        <!-- 验证码输入及获取 -->
        <el-form-item prop="captcha">
          <el-input
              :prefix-icon="Document"
              size="large"
              v-model="data.form.captcha"
              placeholder="请输入验证码"
              class="custom-input"
          />
          <!-- 验证码卡片（点击刷新） -->
          <el-card
              class="captcha-card"
              shadow="hover"
              :body-style="{ padding: '0px', 'text-align': 'center' }"
              @click="refreshCaptcha"
              :loading="isLoading"
          >
            <span class="captcha-text">{{ captchaText || '点击获取' }}</span>
          </el-card>
        </el-form-item>

        <!-- 验证按钮 -->
        <el-form-item>
          <el-button
              size="large"
              type="primary"
              class="forgot-password-button"
              @click="verifyCaptcha"
          >验证</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from "vue";
import { User, Document } from "@element-plus/icons-vue";
import request from "@/utils/request";
import { ElMessage } from "element-plus";
import router from "@/router";

// 组件名称定义
defineOptions({
  name: "ForgotPassword"
});

// 响应式数据
const data = reactive({
  form: {
    username: '',      // 用户名
    captcha: '',       // 用户输入的验证码
    sessionId: ''      // 存储后端返回的唯一sessionId（关键：用于匹配验证码）
  },
  rules: {
    username: [
      { required: true, message: '请输入账号', trigger: 'blur' },
    ],
    captcha: [
      { required: true, message: '请输入验证码', trigger: 'blur' },
    ]
  }
});

// 其他状态
const formRef = ref();       // 表单引用
const captchaText = ref(''); // 显示的验证码文本（后端返回）
const isLoading = ref(false); // 加载状态

// 页面加载时自动获取验证码
onMounted(() => {
  refreshCaptcha();
});

/**
 * 获取验证码（核心：从后端获取验证码和sessionId）
 */
const refreshCaptcha = async () => {
  isLoading.value = true;
  try {
    // 调用后端生成验证码接口
    const res = await request.get('/captcha');

    // 验证后端响应（适配数字/字符串状态码）
    if (res.code === 200 || res.code === '200') {
      // 存储后端返回的sessionId（用于后续验证）
      data.form.sessionId = res.data.sessionId;
      // 显示验证码文本（仅前端显示，实际验证以后端存储为准）
      captchaText.value = res.data.captcha;
      ElMessage.success('验证码已刷新');
    } else {
      ElMessage.error(res.msg || '获取验证码失败');
      captchaText.value = '获取失败';
    }
  } catch (error) {
    console.error('获取验证码出错:', error);
    captchaText.value = '点击重试';
    ElMessage.error(
        error.response?.data?.msg || '网络错误，请稍后再试'
    );
  } finally {
    isLoading.value = false;
  }
};

/**
 * 验证验证码（核心：携带sessionId到后端验证）
 */
const verifyCaptcha = () => {
  // 先验证表单格式
  formRef.value.validate(async (valid) => {
    if (valid) {
      isLoading.value = true;
      try {
        // 携带sessionId、用户名、输入的验证码到后端验证
        const res = await request.post('/captcha/verify', {
          sessionId: data.form.sessionId, // 关键：传递获取验证码时的sessionId
          username: data.form.username,
          captcha: data.form.captcha
        });

        if (res.code === 200 || res.code === '200') {
          ElMessage.success("验证成功，即将跳转");
          // 验证成功，携带用户名跳转到重置密码页
          router.push({
            name: 'ResetPassword',
            query: { username: data.form.username }
          });
        } else {
          // 验证失败（如验证码错误/过期）
          ElMessage.error(res.msg || '验证失败');
          // 自动刷新验证码，避免重复使用
          refreshCaptcha();
        }
      } catch (error) {
        console.error('验证验证码出错:', error);
        ElMessage.error(
            error.response?.data?.msg || '验证请求失败'
        );
      } finally {
        isLoading.value = false;
      }
    }
  });
};
</script>

<style scoped>
.forgot-password-container {
  height: 100vh;
  overflow: hidden;
  display: flex;
  justify-content: center;
  align-items: center;
  background-image: url("@/assets/imgs/2.png");
  background-size: cover;
  position: relative;
}

.forgot-password-box {
  position: relative;
  width: 420px;
  padding: 40px 35px;
  border-radius: 1px;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.15);
  background-color: rgba(255, 255, 255, 1);
  backdrop-filter: blur(8px);
  z-index: 2;
  animation: fadeIn 0.6s ease-out;
  transition: all 0.3s ease;
}

.forgot-password-box:hover {
  transform: translateY(-5px);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.2);
}

.forgot-password-title {
  text-align: center;
  margin-bottom: 35px;
}

.system-name {
  display: block;
  font-size: 30px;
  font-weight: bold;
  color: #000000;
  margin-bottom: 8px;
  letter-spacing: 1px;
}

.forgot-password-form {
  margin-bottom: 20px;
}

.custom-input :deep(.el-input__wrapper) {
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  padding: 2px 12px;
  transition: all 0.3s;
}

/* 验证码卡片样式 */
.captcha-card {
  width: 120px;
  height: 40px;
  margin-left: 10px;
  display: inline-block;
  vertical-align: middle;
  cursor: pointer;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.captcha-text {
  display: block;
  line-height: 40px;
  font-family: Arial, sans-serif;
  font-size: 18px;
  font-weight: bold;
  color: #303133;
  letter-spacing: 2px;
  user-select: none; /* 禁止选中验证码文本 */
}

.forgot-password-button {
  width: 100%;
}
</style>
