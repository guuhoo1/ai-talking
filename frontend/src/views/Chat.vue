<template>
  <div class="chat-container">
    <ChatSessionList />
    <div class="chat-main">
      <div class="chat-header">
        <h2>{{ currentSessionTitle }}</h2>
        <a-select v-model:value="selectedModel" style="width: 120px">
          <a-option value="qwen2.5:3b">Qwen2.5:3b</a-option>
          <a-option value="llama3">Llama3</a-option>
          <a-option value="mistral">Mistral</a-option>
        </a-select>
      </div>
      <div class="chat-messages" ref="messagesContainer">
        <ChatMessage v-for="message in currentMessages" :key="message.id" :message="message" />
        <div v-if="isStreaming" class="message-container">
          <div class="message-avatar">🤖</div>
          <div class="message-content">
            <div class="message-text" v-html="renderedStreamContent"></div>
          </div>
        </div>
      </div>
      <div class="chat-input">
        <a-textarea v-model:value="inputMessage" placeholder="输入消息..." :disabled="isStreaming"
          @keyup.enter.exact="handleSendMessage" />
        <a-button type="primary" @click="handleSendMessage"
          :disabled="!inputMessage || !currentSessionId || isStreaming">
          发送
        </a-button>
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
import { marked } from 'marked'

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
  return marked(currentStreamMessage.value)
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
}

.chat-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: #fff;
}

.chat-header {
  padding: 16px 24px;
  border-bottom: 1px solid #e8e8e8;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chat-header h2 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 24px;
  background: #fafafa;
}

.chat-input {
  padding: 16px 24px;
  border-top: 1px solid #e8e8e8;
  display: flex;
  gap: 12px;
  background: #fff;
}

.chat-input textarea {
  flex: 1;
  resize: none;
  min-height: 80px;
}

.chat-input button {
  align-self: flex-end;
}
</style>