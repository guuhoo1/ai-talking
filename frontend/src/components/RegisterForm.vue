<template>
  <div class="login-form">
    <h2 class="form-title">注册</h2>
    <p class="form-subtitle">创建您的新账号</p>
    <a-form :model="formState" @finish="handleSubmit" :label-col="{ span: 24 }" :wrapper-col="{ span: 24 }">
      <a-form-item label="用户名" name="username" :rules="[{ required: true, message: '请输入用户名' }]">
        <a-input v-model:value="formState.username" placeholder="请输入用户名" class="form-input" />
      </a-form-item>

      <a-form-item label="密码" name="password" :rules="[{ required: true, message: '请输入密码' }]">
        <a-input-password v-model:value="formState.password" placeholder="请输入密码" class="form-input" />
      </a-form-item>

      <a-form-item>
        <a-button type="primary" html-type="submit" class="login-button">
          注册
        </a-button>
      </a-form-item>

      <div class="form-footer">
        <a @click="$router.push('/login')" class="register-link">
          已有账号？立即登录
        </a>
      </div>
    </a-form>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { authApi } from '../api'
import type { RegisterRequest } from '../types'

const router = useRouter()
const formState = ref<RegisterRequest>({ username: '', password: '' })

const handleSubmit = async () => {
  try {
    await authApi.register(formState.value)
    router.push('/login')
  } catch (error) {
    console.error('注册失败:', error)
  }
}
</script>

<style scoped>
.login-form {
  padding: 40px;
  background: var(--bg-color);
  border-radius: var(--border-radius-lg);
  box-shadow: none;
  border: none;
}

.form-title {
  font-size: 24px;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 8px;
  text-align: center;
}

.form-subtitle {
  font-size: 14px;
  color: var(--text-secondary);
  margin-bottom: 32px;
  text-align: center;
}

.form-input {
  width: 100%;
  height: 48px;
  font-size: 16px;
  border-radius: var(--border-radius-md);
  border: 1px solid var(--border-color);
  transition: all 0.3s ease;
}

.form-input:focus {
  border-color: var(--primary-color);
  box-shadow: 0 0 0 3px rgba(67, 97, 238, 0.1);
}

.login-button {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  border-radius: var(--border-radius-md);
  background-color: var(--primary-color);
  border-color: var(--primary-color);
  transition: all 0.3s ease;
  margin-bottom: 24px;
}

.login-button:hover {
  background-color: var(--secondary-color);
  border-color: var(--secondary-color);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(67, 97, 238, 0.3);
}

.login-button:active {
  transform: translateY(0);
}

.form-footer {
  text-align: center;
}

.register-link {
  color: var(--primary-color);
  font-size: 14px;
  text-decoration: none;
  transition: color 0.3s ease;
}

.register-link:hover {
  color: var(--secondary-color);
  text-decoration: underline;
}

:deep(.ant-form-item) {
  margin-bottom: 24px;
}

:deep(.ant-form-item-label > label) {
  font-weight: 600;
  color: var(--text-primary);
  font-size: 14px;
  margin-bottom: 8px;
  display: block;
  text-align: left;
}

:deep(.ant-input) {
  border-color: var(--border-color);
  border-radius: var(--border-radius-md);
  height: 48px;
  font-size: 16px;
}

:deep(.ant-input:focus) {
  border-color: var(--primary-color);
  box-shadow: 0 0 0 3px rgba(67, 97, 238, 0.1);
}

:deep(.ant-btn-primary) {
  background-color: var(--primary-color);
  border-color: var(--primary-color);
  border-radius: var(--border-radius-md);
  height: 48px;
  font-size: 16px;
  font-weight: 600;
}

:deep(.ant-btn-primary:hover) {
  background-color: var(--secondary-color);
  border-color: var(--secondary-color);
}

:deep(.ant-btn) {
  border-radius: var(--border-radius-md);
}

:deep(.ant-input-password) {
  width: 100%;
  height: 48px;
  border-radius: var(--border-radius-md);
  border: 1px solid var(--border-color);
  transition: all 0.3s ease;
}

:deep(.ant-input-password .ant-input) {
  width: 100%;
}

:deep(.ant-input-password input) {
  height: 48px;
  font-size: 16px;
}

:deep(.ant-input-password:focus-within) {
  border-color: var(--primary-color);
  box-shadow: 0 0 0 3px rgba(67, 97, 238, 0.1);
}
</style>