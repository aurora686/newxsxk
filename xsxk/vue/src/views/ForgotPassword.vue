<template>
  <div class="forgot-password-container">
    <div class="forgot-password-box">
      <div class="forgot-password-title">
        <span class="system-name">忘记密码</span>
      </div>
      <el-form :model="data.form" ref="formRef" :rules="data.rules" class="forgot-password-form">
        <el-form-item prop="username">
          <el-input
              :prefix-icon="User"
              size="large"
              v-model="data.form.username"
              placeholder="请输入账号"
              class="custom-input"
          />
        </el-form-item>
        <el-form-item prop="captcha">
          <el-input
              :prefix-icon="Document"
              size="large"
              v-model="data.form.captcha"
              placeholder="请输入验证码"
              class="custom-input"
          />
          <!-- 使用卡片显示文本验证码 -->
          <el-card
              class="captcha-card"
              shadow="hover"
              :body-style="{ padding: '0px', 'text-align': 'center' }"
              @click="refreshCaptcha"
              :loading="isLoading"
          >
            <span class="captcha-text">{{ captchaText }}</span>
          </el-card>
        </el-form-item>
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
})

const data = reactive({
  form: {
    username: '',
    captcha: ''
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

const formRef = ref();
const captchaText = ref('点击获取验证码'); // 存储验证码文本
const isLoading = ref(false); // 加载状态

// 页面加载时获取验证码
onMounted(() => {
  refreshCaptcha();
});

// 获取验证码
const refreshCaptcha = async () => {
  isLoading.value = true;

  try {
    // 发送请求获取验证码
    const res = await request.get('/captcha');

    if (res.code === '200') {
      // 从响应中获取验证码文本
      captchaText.value = res.data.captcha;
      ElMessage.success('验证码已刷新');
    } else {
      ElMessage.error(res.msg || '获取验证码失败');
    }
  } catch (error) {
    console.error('获取验证码出错:', error);
    ElMessage.error('获取验证码失败，请稍后再试');
  } finally {
    isLoading.value = false;
  }
};

const verifyCaptcha = () => {
  formRef.value.validate((valid) => {
    if (valid) {
      request.post('/verifyCaptcha', data.form).then(res => {
        if (res.code === '200') {
          ElMessage.success("验证成功");
          router.push({ name: 'ResetPassword', query: {username: data.form.username}});
        } else {
          ElMessage.error(res.msg);
        }
      });
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

/* 新增验证码卡片样式 */
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
</style>
