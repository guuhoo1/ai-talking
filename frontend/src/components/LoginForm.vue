<template>
  <a-form :model="formState" @finish="handleSubmit" class="login-form">
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
      <a-button @click="$router.push('/register')" style="margin-left: 8px">
        注册
      </a-button>
    </a-form-item>
  </a-form>
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
    const response = await authApi.login(formState.value)
    if (response.code === 200) {
      userStore.setUser({
        id: response.data.id,
        username: response.data.username,
        token: response.token
      })
      router.push('/chat')
    }
  } catch (error) {
    console.error('登录失败:', error)
  }
}
</script>

<style scoped>
.login-form {
  max-width: 400px;
  margin: 0 auto;
  padding: 24px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.login-button {
  width: 100%;
}
</style>