<template>
  <div class="login-container">
    <div class="login-left">
      <div class="left-content">
        <h1 class="left-title">企业级沟通解决方案</h1>
        <p class="left-description">
          高效、安全、智能的企业级聊天应用，助力团队协作与沟通
        </p>
        <div class="features">
          <div class="feature-item">
            <div class="feature-icon">
              <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <path d="M22 4 12 14.01l-3-3" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </div>
            <span>实时消息</span>
          </div>
          <div class="feature-item">
            <div class="feature-icon">
              <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </div>
            <span>安全加密</span>
          </div>
          <div class="feature-item">
            <div class="feature-icon">
              <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <circle cx="9" cy="7" r="4" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <path d="M23 21v-2a4 4 0 0 0-3-3.87" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <path d="M16 3.13a4 4 0 0 1 0 7.75" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </div>
            <span>多设备同步</span>
          </div>
        </div>
      </div>
    </div>
    <div class="login-right">
      <div class="login-wrapper">
        <LoginForm />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import LoginForm from '../components/LoginForm.vue'
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  overflow: hidden;
  background: var(--bg-color);
}

.login-left {
  flex: 1;
  background: linear-gradient(135deg, #000000 0%, #333333 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
  color: white;
}

.left-content {
  max-width: 400px;
}

.left-title {
  font-size: var(--font-size-5xl);
  font-weight: var(--font-weight-bold);
  margin-bottom: var(--spacing-m);
  line-height: var(--line-height-tight);
  letter-spacing: -0.96px;
}

.left-description {
  font-size: var(--font-size-lg);
  margin-bottom: var(--spacing-xl);
  line-height: var(--line-height-relaxed);
  font-weight: var(--font-weight-light-medium);
  opacity: 0.9;
}

.features {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-l);
}

.feature-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-m);
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-medium);
}

.feature-icon {
  width: 40px;
  height: 40px;
  background: rgba(255, 255, 255, 0.16);
  border-radius: var(--border-radius-full);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  transition: all var(--transition-fast);
}

.feature-item:hover .feature-icon {
  transform: scale(1.1);
  background: rgba(255, 255, 255, 0.2);
}

.login-right {
  width: 480px;
  background: var(--bg-color);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
  border-left: 1px solid var(--border-color);
}

.login-wrapper {
  width: 100%;
  background: var(--card-color);
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-lg);
  overflow: hidden;
  border: 1px solid var(--border-color);
  animation: fadeIn var(--transition-normal);
}

.login-header {
  background: var(--card-color);
  padding: 40px;
  text-align: center;
  color: var(--text-primary);
  border-bottom: 1px solid var(--border-color);
}

.logo {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--spacing-m);
}

.logo-icon {
  width: 80px;
  height: 80px;
  background: var(--primary-color);
  border-radius: var(--border-radius-full);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  margin-bottom: var(--spacing-m);
  box-shadow: var(--shadow-md);
  transition: all var(--transition-normal);
}

.logo-icon:hover {
  transform: scale(1.05);
  box-shadow: var(--shadow-lg);
}

.title {
  color: var(--text-primary);
  font-size: var(--font-size-3xl);
  font-weight: var(--font-weight-bold);
  margin: 0;
}

.subtitle {
  color: var(--text-secondary);
  font-size: var(--font-size-base);
  margin: 0;
  font-weight: var(--font-weight-light-medium);
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

@media (max-width: 768px) {
  .login-container {
    flex-direction: column;
  }
  
  .login-left {
    width: 100%;
    padding: 30px;
    border-bottom: 1px solid var(--border-color);
  }
  
  .login-right {
    width: 100%;
    padding: 30px;
    border-left: none;
  }
  
  .left-title {
    font-size: var(--font-size-4xl);
  }
  
  .left-description {
    font-size: var(--font-size-base);
  }
  
  .login-wrapper {
    box-shadow: var(--shadow-md);
  }
}
</style>