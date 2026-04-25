<template>
  <div class="register-container">
    <h1 class="title">AI 聊天机器人</h1>
    <RegisterForm />
  </div>
</template>

<script setup lang="ts">
import RegisterForm from '../components/RegisterForm.vue'
</script>

<style scoped>
.register-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.title {
  color: #fff;
  margin-bottom: 32px;
  font-size: 28px;
  font-weight: 600;
}
</style>