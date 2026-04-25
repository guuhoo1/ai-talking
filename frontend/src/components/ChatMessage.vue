<template>
  <div class="message-container" :class="{ 'user-message': message.role === 'user' }">
    <div class="message-avatar">
      {{ message.role === 'user' ? '👤' : '🤖' }}
    </div>
    <div class="message-content">
      <div class="message-text" v-html="renderedContent"></div>
      <div class="message-time">{{ formatTime(message.createTime) }}</div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { marked } from 'marked'
import type { ChatMessage } from '../types'

const props = defineProps<{
  message: ChatMessage
}>()

const renderedContent = computed(() => {
  return marked(props.message.content)
})

const formatTime = (time: string) => {
  const date = new Date(time)
  return date.toLocaleTimeString()
}
</script>

<style scoped>
.message-container {
  display: flex;
  margin-bottom: 16px;
  align-items: flex-start;
}

.user-message {
  flex-direction: row-reverse;
}

.message-avatar {
  font-size: 24px;
  margin: 0 12px;
  flex-shrink: 0;
}

.message-content {
  max-width: 70%;
}

.user-message .message-content {
  text-align: right;
}

.message-text {
  padding: 12px 16px;
  border-radius: 16px;
  background: #f0f0f0;
  line-height: 1.5;
}

.user-message .message-text {
  background: #007bff;
  color: #fff;
}

.message-time {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}

/* Markdown 样式 */
:deep(p) {
  margin: 8px 0;
}

:deep(code) {
  background: #f5f5f5;
  padding: 2px 4px;
  border-radius: 4px;
  font-family: 'Courier New', Courier, monospace;
}

:deep(pre) {
  background: #f5f5f5;
  padding: 12px;
  border-radius: 8px;
  overflow-x: auto;
  margin: 8px 0;
}

:deep(pre code) {
  background: none;
  padding: 0;
}

:deep(blockquote) {
  border-left: 4px solid #007bff;
  padding-left: 12px;
  margin: 8px 0;
  color: #666;
}
</style>