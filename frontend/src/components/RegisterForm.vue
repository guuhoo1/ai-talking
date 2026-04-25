<template>
  <a-form :model="formState" @finish="handleSubmit" class="register-form">
    <a-form-item label="用户名" name="username" :rules="[{ required: true, message: '请输入用户名' }]">
      <a-input v-model:value="formState.username" placeholder="请输入用户名" />
    </a-form-item>

    <a-form-item label="密码" name="password" :rules="[{ required: true, message: '请输入密码' }]">
      <a-input-password v-model:value="formState.password" placeholder="请输入密码" />
    </a-form-item>

    <a-form-item>
      <a-button type="primary" html-type="submit" class="register-button">
        注册
      </a-button>
      <a-button @click="$router.push('/login')" style="margin-left: 8px">
        登录
      </a-button>
    </a-form-item>
  </a-form>
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
    const response = await authApi.register(formState.value)
    if (response.code === 200) {
      router.push('/login')
    }
  } catch (error) {
    console.error('注册失败:', error)
  }
}
</script>

<style scoped>
.register-form {
  max-width: 400px;
  margin: 0 auto;
  padding: 24px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.register-button {
  width: 100%;
}
</style>