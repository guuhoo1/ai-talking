<template>
  <div class="message-container" :class="{ 'user-message': message.role === 'user' }">
    <div class="message-avatar">
      {{ message.role === 'user' ? '👤' : '💬' }}
    </div>
    <div class="message-content">
      <div :class="{
        'user-message-bubble': message.role === 'user',
        'ai-message-bubble': message.role === 'assistant'
      }">
        <div class="message-text-content">
          <MarkdownRender :content="renderedContent" />
        </div>
        <div class="message-time">{{ formatTime(message.createTime) }}</div>
        <div v-if="message.role === 'assistant'" class="message-actions">
          <button class="extract-knowledge-btn" @click="handleExtractKnowledge" :disabled="isLoading">
            <span v-if="isLoading">提炼中...</span>
            <span v-else>提炼知识</span>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import MarkdownRender from 'markstream-vue'
import { knowledgeApi } from '../api'
import type { ChatMessage } from '../types'

// 为Window对象添加copyCode属性的类型定义
declare global {
  interface Window {
    copyCode: (button: HTMLElement) => void
  }
}

const props = defineProps<{
  message: ChatMessage
}>()

const isLoading = ref(false)

const renderedContent = computed(() => {
  return props.message.content
})

const handleExtractKnowledge = async () => {
  try {
    isLoading.value = true
    await knowledgeApi.extractKnowledge(props.message.id)
    alert('知识提炼成功！')
  } catch (error) {
    console.error('提炼知识失败:', error)
    alert('提炼知识失败，请稍后重试')
  } finally {
    isLoading.value = false
  }
}

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
  animation: fadeIn var(--transition-normal);
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
  margin: 0 var(--spacing-s);
  flex-shrink: 0;
  width: 36px;
  height: 36px;
  border-radius: var(--border-radius-full);
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--card-color);
  border: 1px solid var(--border-color);
  color: var(--text-primary);
  transition: all var(--transition-fast);
}

.message-content {
  display: flex;
  flex-direction: column;
  max-width: 70%;
}

.user-message .message-content {
  text-align: right;
  align-items: flex-end;
}

.user-message-bubble {
  background: var(--user-message-bg);
  color: var(--user-message-text);
  border: 1px solid var(--border-color);
  border-radius: var(--border-radius-pill);
  padding: 10px 16px;
  line-height: var(--line-height-relaxed);
  min-width: auto;
  width: fit-content;
  position: relative;
  align-self: flex-end;
  margin-left: auto;
  font-weight: var(--font-weight-light-medium);
  transition: all var(--transition-fast);
}

.ai-message-bubble {
  background: var(--ai-message-bg);
  color: var(--ai-message-text);
  border: 1px solid var(--border-color);
  border-radius: var(--border-radius-pill);
  padding: 10px 16px;
  line-height: var(--line-height-relaxed);
  min-width: auto;
  width: fit-content;
  position: relative;
  align-self: flex-start;
  margin-right: auto;
  font-weight: var(--font-weight-light-medium);
  transition: all var(--transition-fast);
}

.user-message-bubble:hover {
  background: var(--primary-hover);
  box-shadow: var(--shadow-sm);
}

.ai-message-bubble:hover {
  background: var(--card-hover);
  box-shadow: var(--shadow-sm);
}

.message-text-content {
  overflow-wrap: anywhere;
  white-space: pre-wrap;
}

.message-time {
  font-size: var(--font-size-xs);
  color: var(--text-muted);
  margin-top: var(--spacing-s);
  line-height: 1;
  display: inline-block;
  font-weight: var(--font-weight-medium);
  letter-spacing: 0.6px;
  text-transform: uppercase;
}

.user-message .message-time {
  color: var(--text-muted);
  margin-left: auto;
}

.message-actions {
  margin-top: var(--spacing-s);
  display: flex;
  justify-content: flex-end;
  gap: var(--spacing-s);
}

.extract-knowledge-btn {
  padding: 4px 10px;
  background: var(--card-color);
  color: var(--text-primary);
  border: 1px solid var(--border-color);
  border-radius: var(--border-radius-pill);
  font-size: var(--font-size-xs);
  cursor: pointer;
  transition: all var(--transition-fast);
  font-weight: var(--font-weight-medium);
  letter-spacing: 0.6px;
  text-transform: uppercase;
}

.extract-knowledge-btn:hover {
  background: var(--sidebar-hover-bg);
  border-color: var(--primary-color);
  color: var(--primary-color);
  transform: translateY(-1px);
  box-shadow: var(--shadow-sm);
}

.extract-knowledge-btn:active {
  transform: translateY(0);
  box-shadow: none;
}

.extract-knowledge-btn:disabled {
  background: var(--text-muted);
  cursor: not-allowed;
  transform: none;
  opacity: 0.7;
  box-shadow: none;
}

.extract-knowledge-btn:focus {
  outline: var(--focus-outline);
  outline-offset: var(--focus-offset);
}

/* Markdown 样式 */
:deep(p) {
  margin: var(--spacing-xs) 0;
  color: inherit;
  line-height: var(--line-height-relaxed);
}

:deep(blockquote) {
  border-left: 4px solid var(--primary-color);
  padding-left: var(--spacing-m);
  margin: var(--spacing-s) 0;
  color: inherit;
  opacity: 0.8;
  font-style: italic;
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
  letter-spacing: 0.6px;
  text-transform: uppercase;
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
  background: var(--card-color);
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
</style>