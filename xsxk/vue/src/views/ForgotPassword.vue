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
              :prefix-icon="VerificationCode"
              size="large"
              v-model="data.form.captcha"
              placeholder="请输入验证码"
              class="custom-input"
          />
          <img :src="captchaUrl" @click="refreshCaptcha" style="margin-left: 10px; cursor: pointer;" />
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
import { reactive, ref } from "vue";
import { User, VerificationCode } from "@element-plus/icons-vue";
import request from "@/utils/request";
import { ElMessage } from "element-plus";
import router from "@/router";

// 组件名称定义 (替代原来的 export default { name: "ForgotPassword" })
defineOptions({
  name: "ForgotPassword"
})

const data = reactive({
  form: {},
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
const captchaUrl = ref(import.meta.env.VITE_BASE_URL + '/captcha');

const refreshCaptcha = () => {
  captchaUrl.value = import.meta.env.VITE_BASE_URL + '/captcha?' + new Date().getTime();
};

const verifyCaptcha = () => {
  formRef.value.validate((valid) => {
    if (valid) {
      request.post('/verifyCaptcha', data.form).then(res => {
        if (res.code === '200') {
          ElMessage.success("验证成功");
          router.push({ name: 'ResetPassword', query: { username: data.form.username } });
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
</style>