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
          <a-select v-model:value="selectedModel" class="model-select">
            <a-option value="qwen2.5:3b">Qwen2.5:3b</a-option>
            <a-option value="llama3">Llama3</a-option>
            <a-option value="mistral">Mistral</a-option>
          </a-select>
        </div>
      </div>
      <div class="chat-messages" ref="messagesContainer">
        <div class="messages-wrapper">
          <ChatMessage v-for="message in currentMessages" :key="message.id" :message="message" />
          <div v-if="isStreaming" class="message-container">
            <div class="message-avatar">
              <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M21 15C21 15.5523 20.5523 16 20 16H19V19C19 20.1046 18.1046 21 17 21H7C5.89543 21 5 20.1046 5 19V16H4C3.44772 16 3 15.5523 3 15V9C3 8.44772 3.44772 8 4 8H5V5C5 3.89543 5.89543 3 7 3H17C18.1046 3 19 3.89543 19 5V8H20C20.5523 8 21 8.44772 21 9V15Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <path d="M8 11H16" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <path d="M8 14H16" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
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
              <path d="M22 2L11 13" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M22 2L15 22L11 13L2 9L22 2Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
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
const selectedModel = ref('qwen2.5:3b')
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
  console.log('调用API，模型:', selectedModel.value)
  try {
    eventSource = chatApi.sendMessage({
      sessionId: currentSessionId.value,
      content: message,
      model: selectedModel.value
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
  background: var(--card-color);
  box-shadow: var(--shadow-md);
  border-left: 1px solid var(--border-color);
}

.chat-header {
  padding: 16px 24px;
  border-bottom: 1px solid var(--border-color);
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: var(--card-color);
  position: sticky;
  top: 0;
  z-index: 10;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.header-left h2 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: var(--text-primary);
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.knowledge-link {
  padding: 8px 16px;
  background: var(--bg-color);
  border: 1px solid var(--border-color);
  border-radius: var(--border-radius-md);
  color: var(--text-primary);
  font-size: 14px;
  text-decoration: none;
  transition: all 0.3s ease;
}

.knowledge-link:hover {
  border-color: var(--primary-color);
  color: var(--primary-color);
  background: rgba(67, 97, 238, 0.05);
}

.model-select {
  width: 140px;
  border-radius: var(--border-radius-md);
  border: 1px solid var(--border-color);
  font-size: 14px;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 24px;
  background: linear-gradient(180deg, #f8f9fa 0%, #e9ecef 100%);
}

.messages-wrapper {
  max-width: 1000px;
  margin: 0 auto;
}

.chat-messages::-webkit-scrollbar {
  width: 8px;
}

.chat-messages::-webkit-scrollbar-track {
  background: transparent;
}

.chat-messages::-webkit-scrollbar-thumb {
  background: var(--border-color);
  border-radius: 4px;
  transition: background 0.3s ease;
}

.chat-messages::-webkit-scrollbar-thumb:hover {
  background: var(--text-muted);
}

.chat-input {
  padding: 20px 24px;
  border-top: 1px solid var(--border-color);
  background: var(--card-color);
  position: sticky;
  bottom: 0;
  z-index: 10;
  box-shadow: 0 -4px 12px rgba(0, 0, 0, 0.05);
}

.input-wrapper {
  max-width: 1000px;
  margin: 0 auto;
  display: flex;
  gap: 12px;
  align-items: flex-end;
}

.message-input {
  flex: 1;
  resize: none;
  min-height: 80px;
  max-height: 200px;
  border: 1px solid var(--border-color);
  border-radius: var(--border-radius-md);
  padding: 16px;
  font-size: 14px;
  line-height: 1.5;
  transition: all 0.3s ease;
  background: var(--card-color);
  color: var(--text-primary);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.message-input::placeholder {
  color: var(--text-muted);
}

.message-input:focus {
  outline: none;
  border-color: var(--primary-color);
  box-shadow: 0 0 0 3px rgba(67, 97, 238, 0.1);
}

.send-button {
  padding: 12px 24px;
  background: var(--primary-color);
  color: white;
  border: none;
  border-radius: var(--border-radius-md);
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 8px;
  min-height: 48px;
  white-space: nowrap;
}

.send-button:hover {
  background: var(--secondary-color);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(67, 97, 238, 0.3);
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

:deep(.ant-select-selector) {
  border-radius: var(--border-radius-md) !important;
  border: 1px solid var(--border-color) !important;
}

:deep(.ant-select-selector:hover) {
  border-color: var(--primary-color) !important;
}

:deep(.ant-select-open .ant-select-selector) {
  border-color: var(--primary-color) !important;
  box-shadow: 0 0 0 2px rgba(67, 97, 238, 0.1) !important;
}

/* 代码块样式 */
:deep(.code-block) {
  margin: 16px 0;
  border-radius: var(--border-radius-md);
  overflow: hidden;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  background-color: #f6f8fa;
  border: 1px solid #e1e4e8;
}

:deep(.code-header) {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 16px;
  background-color: #f1f3f4;
  border-bottom: 1px solid #e1e4e8;
  font-size: 12px;
}

:deep(.code-language) {
  color: #57606a;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
}

:deep(.copy-button) {
  background: none;
  border: none;
  color: #57606a;
  font-size: 12px;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: var(--border-radius-sm);
  transition: all 0.3s ease;
}

:deep(.copy-button:hover) {
  background-color: rgba(175, 184, 193, 0.2);
  color: #0969da;
}

:deep(.copy-button.copied) {
  color: #1f883d;
}

:deep(.code-block pre) {
  margin: 0;
  padding: 16px;
  overflow-x: auto;
  font-family: 'SFMono-Regular', Consolas, 'Liberation Mono', Menlo, monospace;
  font-size: 14px;
  line-height: 1.5;
}

:deep(.code-block code) {
  background: none;
  padding: 0;
  font-family: 'SFMono-Regular', Consolas, 'Liberation Mono', Menlo, monospace;
  font-size: 14px;
}

/* 语法高亮样式 */
:deep(.keyword) {
  color: #d73a49;
  font-weight: 600;
}

:deep(.string) {
  color: #032f62;
}

:deep(.comment) {
  color: #6a737d;
  font-style: italic;
}

:deep(.number) {
  color: #005cc5;
}

/* 单行代码样式 */
:deep(pre code) {
  display: block;
}

:deep(code) {
  background-color: #f1f3f4;
  padding: 2px 4px;
  border-radius: 3px;
  font-family: 'SFMono-Regular', Consolas, 'Liberation Mono', Menlo, monospace;
  font-size: 0.9em;
  color: #005cc5;
}
</style>