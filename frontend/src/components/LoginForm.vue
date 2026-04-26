<template>
  <div class="login-form">
    <h2 class="form-title">登录</h2>
    <p class="form-subtitle">请输入您的账号信息</p>
    <a-form :model="formState" @finish="handleSubmit" :label-col="{ span: 24 }" :wrapper-col="{ span: 24 }">
      <a-form-item label="用户名" name="username" :rules="[{ required: true, message: '请输入用户名' }]">
        <a-input v-model:value="formState.username" placeholder="请输入用户名" />
      </a-form-item>

      <a-form-item label="密码" name="password" :rules="[{ required: true, message: '请输入密码' }]">
        <a-input-password v-model:value="formState.password" placeholder="请输入密码" />
      </a-form-item>

      <a-form-item>
        <a-button type="primary" html-type="submit" class="login-button">
          登录
        </a-button>
      </a-form-item>

      <div class="form-footer">
        <a @click="$router.push('/register')" class="register-link">
          没有账号？立即注册
        </a>
      </div>
    </a-form>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { authApi } from '../api'
import { useUserStore } from '../store'
import type { LoginRequest } from '../types'

const router = useRouter()
const userStore = useUserStore()
const formState = ref<LoginRequest>({ username: '', password: '' })

const handleSubmit = async () => {
  try {
    const response = await authApi.login(formState.value) as any
    userStore.setUser({
      id: response.id,
      username: response.username,
      token: response.token
    })
    router.push('/chat')
  } catch (error) {
    console.error('登录失败:', error)
  }
}
</script>

<style scoped>
.login-form {
  padding: 40px;
  background: var(--card-color);
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-md);
  border: 1px solid var(--border-color);
  animation: fadeIn var(--transition-normal);
}

.form-title {
  font-size: var(--font-size-3xl);
  font-weight: var(--font-weight-bold);
  color: var(--text-primary);
  margin-bottom: var(--spacing-s);
  text-align: center;
  letter-spacing: -0.26px;
}

.form-subtitle {
  font-size: var(--font-size-base);
  color: var(--text-secondary);
  margin-bottom: var(--spacing-xl);
  text-align: center;
  font-weight: var(--font-weight-light-medium);
}

.login-button {
  width: 100%;
  height: 48px;
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-semibold);
  border-radius: var(--border-radius-pill);
  background-color: var(--primary-color);
  border-color: var(--primary-color);
  color: var(--text-light);
  transition: all var(--transition-fast);
  margin-bottom: var(--spacing-l);
  padding: 8px 18px 10px;
}

.login-button:hover {
  background-color: var(--primary-hover);
  border-color: var(--primary-hover);
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
}

.login-button:active {
  transform: translateY(0);
}

.login-button:focus {
  outline: var(--focus-outline);
  outline-offset: var(--focus-offset);
}

.form-footer {
  text-align: center;
}

.register-link {
  color: var(--primary-color);
  font-size: var(--font-size-base);
  text-decoration: none;
  transition: color var(--transition-fast);
}

.register-link:hover {
  color: var(--primary-hover);
  text-decoration: underline;
}

.register-link:focus {
  outline: var(--focus-outline);
  outline-offset: var(--focus-offset);
}

:deep(.ant-form-item) {
  margin-bottom: var(--spacing-l);
}

:deep(.ant-form-item-label > label) {
  font-weight: var(--font-weight-medium);
  color: var(--text-primary);
  font-size: var(--font-size-base);
  margin-bottom: var(--spacing-s);
  display: block;
  text-align: left;
  letter-spacing: -0.14px;
}

:deep(.ant-input) {
  border-color: var(--border-color);
  border-radius: var(--border-radius-md);
  height: 48px;
  font-size: var(--font-size-base);
  background: var(--input-bg);
  color: var(--text-primary);
  transition: all var(--transition-fast);
}

:deep(.ant-input:focus) {
  border-color: var(--primary-color);
  outline: var(--focus-outline);
  outline-offset: var(--focus-offset);
  box-shadow: none;
}

:deep(.ant-input::placeholder) {
  color: var(--text-muted);
}

:deep(.ant-btn-primary) {
  background-color: var(--primary-color);
  border-color: var(--primary-color);
  border-radius: var(--border-radius-pill);
  height: 48px;
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-semibold);
  color: var(--text-light);
  transition: all var(--transition-fast);
  padding: 8px 18px 10px;
}

:deep(.ant-btn-primary:hover) {
  background-color: var(--primary-hover);
  border-color: var(--primary-hover);
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
}

:deep(.ant-btn) {
  border-radius: var(--border-radius-pill);
}

:deep(.ant-btn:focus) {
  outline: var(--focus-outline);
  outline-offset: var(--focus-offset);
  box-shadow: none;
}

:deep(.ant-input-password) {
  width: 100%;
  height: 48px;
  border-radius: var(--border-radius-md);
  border: 1px solid var(--border-color);
  transition: all var(--transition-fast);
  background: var(--input-bg);
  display: flex;
  align-items: center;
}

:deep(.ant-input-password .ant-input) {
  width: 100%;
  height: 100%;
  background: var(--input-bg);
  color: var(--text-primary);
  border: none;
  border-radius: var(--border-radius-md);
  padding: 0 16px;
}

:deep(.ant-input-password:focus-within) {
  border-color: var(--primary-color);
  outline: var(--focus-outline);
  outline-offset: var(--focus-offset);
  box-shadow: none;
}

:deep(.ant-input-password .ant-input-suffix) {
  margin-right: 12px;
  color: var(--text-secondary);
  display: flex;
  align-items: center;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>