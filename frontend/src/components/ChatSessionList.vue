<template>
  <div class="session-list">
    <div class="session-header">
      <h2>会话</h2>
      <a-button type="primary" size="small" @click="handleCreateSession">
        新建会话
      </a-button>
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
        <a-button
          type="text"
          size="small"
          @click.stop="handleDeleteSession(session.id)"
          danger
        >
          删除
        </a-button>
      </div>
    </div>
    <div class="user-info">
      <span>{{ username }}</span>
      <a-button type="text" size="small" @click="handleLogout">
        退出登录
      </a-button>
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
  width: 300px;
  height: 100vh;
  background: #f5f5f5;
  border-right: 1px solid #e8e8e8;
  display: flex;
  flex-direction: column;
}

.session-header {
  padding: 16px;
  border-bottom: 1px solid #e8e8e8;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.session-header h2 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
}

.session-items {
  flex: 1;
  overflow-y: auto;
  padding: 8px;
}

.session-item {
  padding: 12px;
  margin-bottom: 8px;
  background: #fff;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  position: relative;
}

.session-item:hover {
  background: #e6f7ff;
}

.session-item.active {
  background: #1890ff;
  color: #fff;
}

.session-title {
  font-size: 14px;
  font-weight: 500;
  margin-bottom: 4px;
}

.session-time {
  font-size: 12px;
  color: #999;
}

.session-item.active .session-time {
  color: rgba(255, 255, 255, 0.8);
}

.user-info {
  padding: 16px;
  border-top: 1px solid #e8e8e8;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>