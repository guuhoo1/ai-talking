<template>
  <div class="chat-container">
    <ChatSessionList />
    <div class="chat-main">
      <div class="chat-header">
        <div class="header-left">
          <h2>{{ currentSessionTitle }}</h2>
        </div>
        <div class="header-right">
          <router-link to="/knowledge" class="knowledge-link">
            知识库
          </router-link>
        </div>
      </div>
      <div class="chat-messages" ref="messagesContainer">
        <div class="messages-wrapper">
          <ChatMessage v-for="message in currentMessages" :key="message.id" :message="message" />
          <div v-if="isStreaming" class="message-container">
            <div class="message-avatar">
              <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path
                  d="M21 15C21 15.5523 20.5523 16 20 16H19V19C19 20.1046 18.1046 21 17 21H7C5.89543 21 5 20.1046 5 19V16H4C3.44772 16 3 15.5523 3 15V9C3 8.44772 3.44772 8 4 8H5V5C5 3.89543 5.89543 3 7 3H17C18.1046 3 19 3.89543 19 5V8H20C20.5523 8 21 8.44772 21 9V15Z"
                  stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" />
                <path d="M8 11H16" stroke="currentColor" stroke-width="2" stroke-linecap="round"
                  stroke-linejoin="round" />
                <path d="M8 14H16" stroke="currentColor" stroke-width="2" stroke-linecap="round"
                  stroke-linejoin="round" />
              </svg>
            </div>
            <div class="message-content">
              <div class="message-text">
                <MarkdownRender :content="renderedStreamContent" />
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="chat-input">
        <div class="input-wrapper">
          <a-textarea v-model:value="inputMessage" placeholder="输入消息..." :disabled="isStreaming"
            @keyup.enter.exact="handleSendMessage" class="message-input" />
          <a-button type="primary" @click="handleSendMessage"
            :disabled="!inputMessage || !currentSessionId || isStreaming" class="send-button">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M22 2L11 13" stroke="currentColor" stroke-width="2" stroke-linecap="round"
                stroke-linejoin="round" />
              <path d="M22 2L15 22L11 13L2 9L22 2Z" stroke="currentColor" stroke-width="2" stroke-linecap="round"
                stroke-linejoin="round" />
            </svg>
            发送
          </a-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, nextTick, onMounted } from 'vue'
import { chatApi } from '../api'
import { useChatStore } from '../store'
import ChatSessionList from '../components/ChatSessionList.vue'
import ChatMessage from '../components/ChatMessage.vue'
import MarkdownRender from 'markstream-vue'
import 'markstream-vue/index.css'

// 复制代码到剪贴板
window.copyCode = (button: HTMLElement) => {
  const codeBlock = button.closest('.code-block') as HTMLElement
  const codeElement = codeBlock.querySelector('code') as HTMLElement
  const code = codeElement.textContent || ''

  navigator.clipboard.writeText(code).then(() => {
    const originalText = button.textContent
    button.textContent = '已复制!'
    button.classList.add('copied')

    setTimeout(() => {
      button.textContent = originalText
      button.classList.remove('copied')
    }, 2000)
  }).catch(err => {
    console.error('复制失败:', err)
  })
}

const chatStore = useChatStore()
const messagesContainer = ref<HTMLElement>()
const inputMessage = ref('')
let eventSource: EventSource | null = null

const currentSessionId = computed(() => chatStore.currentSessionId)
const currentMessages = computed(() => {
  if (!currentSessionId.value) return []
  console.log('currentSessionId.value:', currentSessionId.value)
  console.log('chatStore.messages:', chatStore.messages)
  console.log('chatStore.messages[currentSessionId.value]:', chatStore.messages[currentSessionId.value])
  return chatStore.messages[currentSessionId.value] || []
})
const isStreaming = computed(() => chatStore.isStreaming)
const currentStreamMessage = computed(() => chatStore.currentStreamMessage)

const currentSessionTitle = computed(() => {
  if (!currentSessionId.value) return '新会话'
  const session = chatStore.sessions.find(s => s.id === currentSessionId.value)
  return session ? session.title : '新会话'
})

const renderedStreamContent = computed(() => {
  return currentStreamMessage.value
})

const handleSendMessage = () => {
  console.log('发送按钮被点击')
  console.log('输入消息:', inputMessage.value)
  console.log('当前会话ID:', currentSessionId.value)
  console.log('是否正在流式响应:', isStreaming.value)

  if (!inputMessage.value || !currentSessionId.value) {
    console.log('输入为空或没有会话ID')
    return
  }

  const message = inputMessage.value.trim()
  inputMessage.value = ''

  // 添加用户消息
  const userMessage = {
    id: Date.now(),
    sessionId: currentSessionId.value,
    role: 'user' as const,
    content: message,
    createTime: new Date().toISOString()
  }
  chatStore.addMessage(currentSessionId.value, userMessage)
  console.log('添加用户消息成功')

  // 开始流式响应
  chatStore.setIsStreaming(true)
  chatStore.setCurrentStreamMessage('')
  console.log('开始流式响应')

  // 调用API
  console.log('调用API，模型: qwen2.5:3b')
  try {
    eventSource = chatApi.sendMessage({
      sessionId: currentSessionId.value,
      content: message,
      model: 'qwen2.5:3b'
    })
    console.log('EventSource创建成功')

    eventSource.onmessage = (event) => {
      console.log('接收到消息:', event.data)
      if (event.data === '[DONE]') {
        // 流式响应结束
        console.log('收到[DONE]消息，currentStreamMessage:', chatStore.currentStreamMessage)
        console.log('currentSessionId:', currentSessionId.value)
        if (chatStore.currentStreamMessage && currentSessionId.value) {
          // 将AI的响应添加到消息列表中
          const aiMessage = {
            id: Date.now(),
            sessionId: currentSessionId.value,
            role: 'assistant' as const,
            content: chatStore.currentStreamMessage,
            createTime: new Date().toISOString()
          }
          console.log('创建AI消息:', aiMessage)
          chatStore.addMessage(currentSessionId.value, aiMessage)
          console.log('添加AI消息后，messages:', chatStore.messages[currentSessionId.value])
        }
        chatStore.setIsStreaming(false)
        chatStore.setCurrentStreamMessage('')
        eventSource?.close()
        eventSource = null
        console.log('流式响应结束')
      } else {
        // 接收流式数据
        chatStore.setCurrentStreamMessage(chatStore.currentStreamMessage + event.data)
        console.log('接收流式数据后，currentStreamMessage:', chatStore.currentStreamMessage)
      }
    }

    eventSource.onerror = (error) => {
      console.error('EventSource错误:', error)
      chatStore.setIsStreaming(false)
      chatStore.setCurrentStreamMessage('')
      eventSource?.close()
      eventSource = null
    }
  } catch (error) {
    console.error('API调用错误:', error)
    chatStore.setIsStreaming(false)
  }
}

const scrollToBottom = async () => {
  await nextTick()
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
  }
}

watch(currentMessages, scrollToBottom, { deep: true })
watch(currentStreamMessage, scrollToBottom)

onMounted(() => {
  scrollToBottom()
})
</script>

<style scoped>
.chat-container {
  display: flex;
  height: 100vh;
  background: var(--bg-color);
  overflow: hidden;
}

.chat-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: var(--bg-color);
  border-left: 1px solid var(--border-color);
}

.chat-header {
  padding: var(--spacing-m) var(--spacing-l);
  border-bottom: 1px solid var(--border-color);
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: var(--card-color);
  position: sticky;
  top: 0;
  z-index: 10;
  box-shadow: var(--shadow-sm);
}

.header-left h2 {
  margin: 0;
  font-size: var(--font-size-xl);
  font-weight: var(--font-weight-bold);
  color: var(--text-primary);
  letter-spacing: -0.14px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: var(--spacing-m);
}

.knowledge-link {
  padding: 8px 18px 10px;
  background: var(--card-color);
  border: 1px solid var(--border-color);
  border-radius: var(--border-radius-pill);
  color: var(--text-primary);
  font-size: var(--font-size-base);
  text-decoration: none;
  transition: all var(--transition-fast);
  font-weight: var(--font-weight-medium);
}

.knowledge-link:hover {
  border-color: var(--primary-color);
  color: var(--primary-color);
  background: rgba(0, 0, 0, 0.05);
}

.knowledge-link:focus {
  outline: var(--focus-outline);
  outline-offset: var(--focus-offset);
}

.model-select {
  width: 140px;
  border-radius: var(--border-radius-pill);
  font-size: var(--font-size-base);
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: var(--spacing-l);
  background: var(--bg-color);
}

.messages-wrapper {
  max-width: 800px;
  margin: 0 auto;
}

.chat-messages::-webkit-scrollbar {
  width: 8px;
}

.chat-messages::-webkit-scrollbar-track {
  background: var(--bg-color);
}

.chat-messages::-webkit-scrollbar-thumb {
  background: var(--border-color);
  border-radius: var(--border-radius-full);
  transition: background var(--transition-fast);
}

.chat-messages::-webkit-scrollbar-thumb:hover {
  background: var(--text-muted);
}

.chat-input {
  padding: var(--spacing-m) var(--spacing-l);
  border-top: 1px solid var(--border-color);
  background: var(--bg-color);
  position: sticky;
  bottom: 0;
  z-index: 10;
}

.input-wrapper {
  max-width: 800px;
  margin: 0 auto;
  display: flex;
  gap: var(--spacing-s);
  align-items: flex-end;
}

.message-input {
  flex: 1;
  resize: none;
  min-height: 44px;
  max-height: 200px;
  border: 1px solid var(--input-border);
  border-radius: var(--border-radius-pill);
  padding: 10px 16px;
  font-size: var(--font-size-base);
  line-height: var(--line-height-relaxed);
  transition: all var(--transition-fast);
  background: var(--card-color);
  color: var(--text-primary);
  font-weight: var(--font-weight-light-medium);
}

.message-input::placeholder {
  color: var(--text-muted);
}

.message-input:focus {
  outline: var(--focus-outline);
  outline-offset: var(--focus-offset);
  border-color: var(--primary-color);
  box-shadow: none;
}

.send-button {
  padding: 10px 20px;
  background: var(--primary-color);
  color: var(--text-light);
  border: none;
  border-radius: var(--border-radius-pill);
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-semibold);
  cursor: pointer;
  transition: all var(--transition-fast);
  display: flex;
  align-items: center;
  gap: var(--spacing-s);
  min-height: 44px;
  white-space: nowrap;
  padding: 8px 18px 10px;
}

.send-button:hover {
  background: var(--primary-hover);
  transform: translateY(-1px);
  box-shadow: var(--shadow-md);
}

.send-button:active {
  transform: translateY(0);
}

.send-button:disabled {
  background: var(--text-muted);
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.send-button:focus {
  outline: var(--focus-outline);
  outline-offset: var(--focus-offset);
}

:deep(.ant-select-selector) {
  border-radius: var(--border-radius-pill) !important;
  border: 1px solid var(--border-color) !important;
  background: var(--input-bg) !important;
}

:deep(.ant-select-selection-item) {
  color: var(--text-primary) !important;
  font-weight: var(--font-weight-light-medium);
}

:deep(.ant-select-selector:hover) {
  border-color: var(--primary-color) !important;
}

:deep(.ant-select-open .ant-select-selector) {
  border-color: var(--primary-color) !important;
  outline: var(--focus-outline) !important;
  outline-offset: var(--focus-offset) !important;
  box-shadow: none !important;
}

:deep(.ant-select-dropdown) {
  background: var(--card-color) !important;
  border: 1px solid var(--border-color) !important;
  border-radius: var(--border-radius-lg) !important;
  box-shadow: var(--shadow-md) !important;
}

:deep(.ant-select-item) {
  color: var(--text-primary) !important;
  transition: background var(--transition-fast) !important;
  font-weight: var(--font-weight-light-medium);
}

:deep(.ant-select-item:hover) {
  background: var(--sidebar-hover-bg) !important;
}

:deep(.ant-select-item-selected) {
  background: var(--primary-color) !important;
  color: var(--text-light) !important;
}

/* 代码块样式 */
:deep(.code-block) {
  margin: var(--spacing-m) 0;
  border-radius: var(--border-radius-lg);
  overflow: hidden;
  box-shadow: var(--shadow-sm);
  background-color: var(--card-color);
  border: 1px solid var(--border-color);
}

:deep(.code-header) {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--spacing-s) var(--spacing-m);
  background-color: var(--bg-light);
  border-bottom: 1px solid var(--border-color);
  font-size: var(--font-size-sm);
}

:deep(.code-language) {
  color: var(--text-secondary);
  font-family: var(--font-mono);
  font-weight: var(--font-weight-medium);
  letter-spacing: 0.6px;
  text-transform: uppercase;
}

:deep(.copy-button) {
  background: none;
  border: none;
  color: var(--text-secondary);
  font-size: var(--font-size-sm);
  cursor: pointer;
  padding: 4px 8px;
  border-radius: var(--border-radius-sm);
  transition: all var(--transition-fast);
  font-weight: var(--font-weight-medium);
}

:deep(.copy-button:hover) {
  background-color: var(--sidebar-hover-bg);
  color: var(--primary-color);
}

:deep(.copy-button:focus) {
  outline: var(--focus-outline);
  outline-offset: var(--focus-offset);
}

:deep(.copy-button.copied) {
  color: var(--success-color);
}

:deep(.code-block pre) {
  margin: 0;
  padding: var(--spacing-m);
  overflow-x: auto;
  font-family: var(--font-mono);
  font-size: var(--font-size-base);
  line-height: var(--line-height-normal);
  background-color: var(--card-color);
  border: none;
}

:deep(.code-block code) {
  background: none;
  padding: 0;
  font-family: var(--font-mono);
  font-size: var(--font-size-base);
  color: var(--text-primary);
}

/* 语法高亮样式 */
:deep(.keyword) {
  color: #f97316;
  font-weight: var(--font-weight-semibold);
}

:deep(.string) {
  color: #10b981;
}

:deep(.comment) {
  color: var(--text-muted);
  font-style: italic;
}

:deep(.number) {
  color: var(--primary-color);
}

/* 单行代码样式 */
:deep(pre code) {
  display: block;
}

:deep(code) {
  background-color: var(--card-color);
  padding: 2px 4px;
  border-radius: var(--border-radius-sm);
  font-family: var(--font-mono);
  font-size: 0.9em;
  color: var(--primary-color);
  border: 1px solid var(--border-color);
}

/* 消息动画 */
.fade-in {
  animation: fadeIn var(--transition-normal);
}
</style>