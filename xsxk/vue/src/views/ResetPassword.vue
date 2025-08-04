<template>
  <div class="reset-password-container">
    <div class="reset-password-box">
      <div class="reset-password-title">
        <span class="system-name">重置密码</span>
      </div>
      <el-form :model="data.form" ref="formRef" :rules="data.rules" class="reset-password-form">
        <el-form-item prop="newPassword">
          <el-input
              :prefix-icon="Lock"
              size="large"
              v-model="data.form.newPassword"
              placeholder="请输入新密码"
              show-password
              class="custom-input"
          />
        </el-form-item>
        <el-form-item prop="confirmPassword">
          <el-input
              :prefix-icon="Lock"
              size="large"
              v-model="data.form.confirmPassword"
              placeholder="请确认新密码"
              show-password
              class="custom-input"
          />
        </el-form-item>
        <el-form-item>
          <el-button
              size="large"
              type="primary"
              class="reset-password-button"
              @click="resetPassword"
          >重置密码</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from "vue";
import { Lock } from "@element-plus/icons-vue";
import request from "@/utils/request";
import { ElMessage } from "element-plus";
import router from "@/router";
import { useRoute } from "vue-router";

const route = useRoute();
const data = reactive({
  form: {
    username: route.query.username,
    newPassword: '',
    confirmPassword: ''
  },
  rules: {
    newPassword: [
      { required: true, message: '请输入新密码', trigger: 'blur' },
    ],
    confirmPassword: [
      { required: true, message: '请确认新密码', trigger: 'blur' },
      { validator: (rule, value, callback) => {
          if (value !== data.form.newPassword) {
            callback(new Error('两次输入的密码不一致'));
          } else {
            callback();
          }
        }, trigger: 'blur' }
    ]
  }
});

const formRef = ref();

const resetPassword = () => {
  formRef.value.validate((valid) => {
    if (valid) {
      // 确保 data.form 中包含 role 字段
      data.form.role = 'STUDENT'; // 根据实际情况修改角色
      request.post('/captcha/resetPassword', data.form).then(res => {
        if (res.code === '200') {
          ElMessage.success("密码重置成功，请重新登录");
          router.push('/login');
        } else {
          ElMessage.error(res.msg);
        }
      });
    }
  });
};
</script>

<style scoped>
.reset-password-container {
  height: 100vh;
  overflow: hidden;
  display: flex;
  justify-content: center;
  align-items: center;
  background-image: url("@/assets/imgs/2.png");
  background-size: cover;
  position: relative;
}

.reset-password-box {
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

.reset-password-box:hover {
  transform: translateY(-5px);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.2);
}

.reset-password-title {
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

.reset-password-form {
  margin-bottom: 20px;
}

.custom-input :deep(.el-input__wrapper) {
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  padding: 2px 12px;
  transition: all 0.3s;
}
</style>
