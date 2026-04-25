<template>
  <div class="message-container" :class="{ 'user-message': message.role === 'user' }">
    <div class="message-avatar">
      {{ message.role === 'user' ? '👤' : '💬' }}
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
  margin-bottom: var(--spacing-m);
  align-items: flex-start;
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.user-message {
  flex-direction: row-reverse;
}

.message-avatar {
  font-size: 20px;
  margin: 0 var(--spacing-m);
  flex-shrink: 0;
  width: 36px;
  height: 36px;
  border-radius: var(--border-radius-full);
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--bg-color);
  border: 1px solid var(--border-color);
  color: var(--text-primary);
}

.message-content {
  max-width: 70%;
}

.user-message .message-content {
  text-align: right;
}

.message-text {
  padding: var(--spacing-m);
  border-radius: var(--border-radius-lg);
  background: var(--bg-color);
  line-height: 1.5;
  box-shadow: var(--shadow-sm);
  border: 1px solid var(--border-color);
  color: var(--text-primary);
}

.user-message .message-text {
  background: var(--primary-color);
  color: white;
  border-color: var(--primary-color);
}

.message-time {
  font-size: 12px;
  color: var(--text-muted);
  margin-top: var(--spacing-xs);
}

/* Markdown 样式 */
:deep(p) {
  margin: var(--spacing-s) 0;
  color: var(--text-primary);
}

:deep(code) {
  background: rgba(0, 0, 0, 0.05);
  padding: 2px 4px;
  border-radius: var(--border-radius-sm);
  font-family: 'Courier New', Courier, monospace;
  font-size: 12px;
  color: var(--text-primary);
}

:deep(pre) {
  background: rgba(0, 0, 0, 0.05);
  padding: var(--spacing-m);
  border-radius: var(--border-radius-md);
  overflow-x: auto;
  margin: var(--spacing-s) 0;
  border: 1px solid var(--border-color);
}

:deep(pre code) {
  background: none;
  padding: 0;
  color: var(--text-primary);
}

:deep(blockquote) {
  border-left: 4px solid var(--primary-color);
  padding-left: var(--spacing-m);
  margin: var(--spacing-s) 0;
  color: var(--text-secondary);
}
</style>