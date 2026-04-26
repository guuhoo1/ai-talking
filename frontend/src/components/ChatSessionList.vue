<template>
  <div class="session-list">
    <div class="session-header">
      <h2>会话</h2>
      <button class="new-chat-btn" @click="handleCreateSession">
        <span>+</span> 新建会话
      </button>
    </div>
    <div class="session-items">
      <div
        v-for="session in sessions"
        :key="session.id"
        class="session-item"
        :class="{ active: session.id === currentSessionId }"
        @click="handleSelectSession(session.id)"
      >
        <div class="session-title">{{ session.title }}</div>
        <div class="session-time">{{ formatTime(session.createTime) }}</div>
      </div>
    </div>
    <div class="user-info">
      <div class="user-details">
        <span>{{ username }}</span>
      </div>
      <button class="logout-btn" @click="handleLogout">
        退出登录
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { chatApi, authApi } from '../api'
import { useUserStore, useChatStore } from '../store'

const router = useRouter()
const userStore = useUserStore()
const chatStore = useChatStore()

const sessions = computed(() => chatStore.sessions)
const currentSessionId = computed(() => chatStore.currentSessionId)
const username = computed(() => userStore.username || '')

const handleCreateSession = async () => {
  try {
    console.log('开始创建会话')
    const response = await chatApi.createSession('新会话')
    console.log('创建会话响应:', response)
    console.log('创建会话成功，会话数据:', response.data)
    // 检查会话数据是否包含id字段
    if (!response.data.id) {
      console.log('会话数据缺少id字段，生成临时id')
      response.data.id = Date.now()
    }
    chatStore.addSession(response.data)
    console.log('添加会话后，currentSessionId:', chatStore.currentSessionId)
  } catch (error) {
    console.error('创建会话失败:', error)
  }
}

const handleSelectSession = (sessionId: number) => {
  chatStore.setCurrentSessionId(sessionId)
  loadMessages(sessionId)
}

const handleLogout = async () => {
  console.log('退出登录按钮被点击')
  try {
    console.log('调用authApi.logout()')
    await authApi.logout()
    console.log('调用userStore.logout()')
    userStore.logout()
    console.log('调用router.push(\'/login\')')
    router.push('/login')
  } catch (error) {
    console.error('退出登录失败:', error)
  }
}

const loadMessages = async (sessionId: number) => {
  try {
    const response = await chatApi.getMessages(sessionId)
    chatStore.setMessages(sessionId, response.data)
  } catch (error) {
    console.error('加载消息失败:', error)
  }
}

const formatTime = (time: string) => {
  const date = new Date(time)
  return date.toLocaleDateString()
}

onMounted(async () => {
  try {
    const response = await chatApi.getSessionList()
    chatStore.setSessions(response.data)
    if (response.data.length > 0) {
      chatStore.setCurrentSessionId(response.data[0].id)
      loadMessages(response.data[0].id)
    }
  } catch (error) {
    console.error('加载会话列表失败:', error)
  }
})
</script>

<style scoped>
.session-list {
  width: 280px;
  height: 100vh;
  background: var(--sidebar-bg);
  border-right: 1px solid var(--border-color);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.session-header {
  padding: var(--spacing-m) var(--spacing-m);
  border-bottom: 1px solid var(--border-color);
  display: flex;
  flex-direction: column;
  gap: var(--spacing-s);
  background: var(--sidebar-bg);
  position: sticky;
  top: 0;
  z-index: 10;
}

.session-header h2 {
  margin: 0;
  font-size: var(--font-size-xl);
  font-weight: var(--font-weight-bold);
  color: var(--text-primary);
  letter-spacing: -0.14px;
}

.session-header .new-chat-btn {
  width: 100%;
  padding: 8px 18px 10px;
  background: var(--card-color);
  border: 1px solid var(--border-color);
  border-radius: var(--border-radius-pill);
  color: var(--text-primary);
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-semibold);
  cursor: pointer;
  transition: all var(--transition-fast);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--spacing-s);
}

.session-header .new-chat-btn:hover {
  background: var(--sidebar-hover-bg);
  border-color: var(--primary-color);
  color: var(--primary-color);
  transform: translateY(-1px);
  box-shadow: var(--shadow-sm);
}

.session-header .new-chat-btn:active {
  transform: translateY(0);
  box-shadow: none;
}

.session-header .new-chat-btn:focus {
  outline: var(--focus-outline);
  outline-offset: var(--focus-offset);
}

.session-items {
  flex: 1;
  overflow-y: auto;
  padding: var(--spacing-m);
}

.session-items::-webkit-scrollbar {
  width: 6px;
}

.session-items::-webkit-scrollbar-track {
  background: var(--sidebar-bg);
}

.session-items::-webkit-scrollbar-thumb {
  background: var(--border-color);
  border-radius: var(--border-radius-full);
  transition: background var(--transition-fast);
}

.session-items::-webkit-scrollbar-thumb:hover {
  background: var(--text-muted);
}

.session-item {
  padding: var(--spacing-s) var(--spacing-m);
  margin-bottom: var(--spacing-xs);
  background: var(--sidebar-bg);
  border-radius: var(--border-radius-pill);
  cursor: pointer;
  transition: all var(--transition-fast);
  position: relative;
  border: 1px solid transparent;
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xs);
}

.session-item:hover {
  background: var(--sidebar-hover-bg);
  border-color: var(--primary-color);
  transform: translateX(4px);
  box-shadow: var(--shadow-sm);
}

.session-item.active {
  background: var(--primary-color);
  color: var(--text-light);
  border-color: var(--primary-color);
  transform: translateX(4px);
  box-shadow: var(--shadow-sm);
}

.session-title {
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-medium);
  color: var(--text-primary);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  line-height: var(--line-height-relaxed);
}

.session-item.active .session-title {
  color: var(--text-light);
  font-weight: var(--font-weight-semibold);
}

.session-time {
  font-size: var(--font-size-xs);
  color: var(--text-muted);
  font-weight: var(--font-weight-medium);
  letter-spacing: 0.6px;
  text-transform: uppercase;
}

.session-item.active .session-time {
  color: rgba(255, 255, 255, 0.7);
}

.user-info {
  padding: var(--spacing-m) var(--spacing-m);
  border-top: 1px solid var(--border-color);
  display: flex;
  flex-direction: column;
  gap: var(--spacing-s);
  background: var(--sidebar-bg);
  position: sticky;
  bottom: 0;
  z-index: 10;
}

.user-info .user-details {
  width: 100%;
  padding: var(--spacing-s) var(--spacing-m);
  background: var(--card-color);
  border-radius: var(--border-radius-pill);
  display: flex;
  justify-content: space-between;
  align-items: center;
  border: 1px solid var(--border-color);
  transition: all var(--transition-fast);
}

.user-info .user-details:hover {
  border-color: var(--primary-color);
  box-shadow: var(--shadow-sm);
}

.user-info .user-details span {
  color: var(--text-primary);
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-medium);
}

.user-info .logout-btn {
  width: 100%;
  padding: 8px 18px 10px;
  background: var(--card-color);
  border: 1px solid var(--border-color);
  border-radius: var(--border-radius-pill);
  color: var(--text-primary);
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-semibold);
  cursor: pointer;
  transition: all var(--transition-fast);
  text-align: left;
  display: flex;
  align-items: center;
  gap: var(--spacing-s);
}

.user-info .logout-btn:hover {
  background: var(--sidebar-hover-bg);
  border-color: var(--primary-color);
  color: var(--primary-color);
  box-shadow: var(--shadow-sm);
}

.user-info .logout-btn:active {
  box-shadow: none;
}

.user-info .logout-btn:focus {
  outline: var(--focus-outline);
  outline-offset: var(--focus-offset);
}

.user-info .logout-btn::before {
  content: "";
  display: inline-block;
  width: 16px;
  height: 16px;
  background-image: url("data:image/svg+xml,%3Csvg width='16' height='16' viewBox='0 0 24 24' fill='none' xmlns='http://www.w3.org/2000/svg'%3E%3Cpath d='M9 21H5C4.46957 21 3.96086 20.7893 3.58579 20.4142C3.21071 20.0391 3 19.5304 3 19V5C3 4.46957 3.21071 3.96086 3.58579 3.58579C3.96086 3.21071 4.46957 3 5 3H9' stroke='currentColor' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'/%3E%3Cpath d='M16 17L21 12L16 7' stroke='currentColor' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'/%3E%3Cpath d='M21 12H9' stroke='currentColor' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'/%3E%3C/svg%3E");
  background-repeat: no-repeat;
  background-position: center;
}

/* 移除背景图片加号，只保留模板中的文本加号 */
</style>