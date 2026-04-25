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
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { chatApi, authApi } from '../api'
import { useUserStore, useChatStore } from '../store'
import type { ChatSession } from '../types'

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
    if (response.code === 200) {
      console.log('创建会话成功，会话数据:', response.data)
      // 检查会话数据是否包含id字段
      if (!response.data.id) {
        console.log('会话数据缺少id字段，生成临时id')
        response.data.id = Date.now()
      }
      chatStore.addSession(response.data)
      console.log('添加会话后，currentSessionId:', chatStore.currentSessionId)
    }
  } catch (error) {
    console.error('创建会话失败:', error)
  }
}

const handleSelectSession = (sessionId: number) => {
  chatStore.setCurrentSessionId(sessionId)
  loadMessages(sessionId)
}

const handleDeleteSession = async (sessionId: number) => {
  try {
    const response = await chatApi.deleteSession(sessionId)
    if (response.code === 200) {
      chatStore.deleteSession(sessionId)
    }
  } catch (error) {
    console.error('删除会话失败:', error)
  }
}

const handleLogout = async () => {
  console.log('退出登录按钮被点击')
  try {
    console.log('调用authApi.logout()')
    const response = await authApi.logout()
    console.log('logout响应:', response)
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
    if (response.code === 200) {
      chatStore.setMessages(sessionId, response.data)
    }
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
    if (response.code === 200) {
      chatStore.setSessions(response.data)
      if (response.data.length > 0) {
        chatStore.setCurrentSessionId(response.data[0].id)
        loadMessages(response.data[0].id)
      }
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
  background: var(--card-color);
  border-right: 1px solid var(--border-color);
  display: flex;
  flex-direction: column;
  box-shadow: var(--shadow-md);
  overflow: hidden;
}

.session-header {
  padding: 20px 24px;
  border-bottom: 1px solid var(--border-color);
  display: flex;
  flex-direction: column;
  gap: 16px;
  background: var(--card-color);
  position: sticky;
  top: 0;
  z-index: 10;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.session-header h2 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: var(--text-primary);
}

.session-header .new-chat-btn {
  width: 100%;
  padding: 12px 16px;
  background: var(--primary-color);
  border: 1px solid var(--primary-color);
  border-radius: var(--border-radius-md);
  color: white;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  box-shadow: 0 2px 4px rgba(67, 97, 238, 0.2);
}

.session-header .new-chat-btn:hover {
  background: var(--secondary-color);
  border-color: var(--secondary-color);
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(67, 97, 238, 0.3);
}

.session-header .new-chat-btn:active {
  transform: translateY(0);
}

.session-items {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
}

.session-items::-webkit-scrollbar {
  width: 6px;
}

.session-items::-webkit-scrollbar-track {
  background: transparent;
}

.session-items::-webkit-scrollbar-thumb {
  background: var(--border-color);
  border-radius: 3px;
  transition: background 0.3s ease;
}

.session-items::-webkit-scrollbar-thumb:hover {
  background: var(--text-muted);
}

.session-item {
  padding: 12px 16px;
  margin-bottom: 8px;
  background: var(--card-color);
  border-radius: var(--border-radius-md);
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
  border: 1px solid var(--border-color);
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.session-item:hover {
  background: rgba(67, 97, 238, 0.05);
  border-color: var(--primary-color);
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
  transform: translateX(4px);
}

.session-item.active {
  background: var(--primary-color);
  color: white;
  border-color: var(--primary-color);
  box-shadow: 0 2px 6px rgba(67, 97, 238, 0.3);
  transform: translateX(4px);
}

.session-title {
  font-size: 14px;
  font-weight: 500;
  color: var(--text-primary);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  line-height: 1.4;
}

.session-item.active .session-title {
  color: white;
  font-weight: 600;
}

.session-time {
  font-size: 11px;
  color: var(--text-muted);
  font-weight: 400;
}

.session-item.active .session-time {
  color: rgba(255, 255, 255, 0.8);
}

.user-info {
  padding: 20px 24px;
  border-top: 1px solid var(--border-color);
  display: flex;
  flex-direction: column;
  gap: 12px;
  background: var(--card-color);
  position: sticky;
  bottom: 0;
  z-index: 10;
  box-shadow: 0 -2px 4px rgba(0, 0, 0, 0.05);
}

.user-info .user-details {
  width: 100%;
  padding: 12px 16px;
  background: var(--bg-color);
  border-radius: var(--border-radius-md);
  display: flex;
  justify-content: space-between;
  align-items: center;
  border: 1px solid var(--border-color);
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.user-info .user-details span {
  color: var(--text-primary);
  font-size: 14px;
  font-weight: 500;
}

.user-info .logout-btn {
  width: 100%;
  padding: 12px 16px;
  background: transparent;
  border: 1px solid var(--border-color);
  border-radius: var(--border-radius-md);
  color: var(--text-primary);
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  text-align: left;
  display: flex;
  align-items: center;
  gap: 8px;
}

.user-info .logout-btn:hover {
  background: var(--bg-color);
  border-color: var(--primary-color);
  color: var(--primary-color);
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
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

.session-header .new-chat-btn::before {
  content: "";
  display: inline-block;
  width: 16px;
  height: 16px;
  background-image: url("data:image/svg+xml,%3Csvg width='16' height='16' viewBox='0 0 24 24' fill='none' xmlns='http://www.w3.org/2000/svg'%3E%3Cpath d='M12 5V19' stroke='currentColor' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'/%3E%3Cpath d='M5 12H19' stroke='currentColor' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'/%3E%3C/svg%3E");
  background-repeat: no-repeat;
  background-position: center;
}
</style>