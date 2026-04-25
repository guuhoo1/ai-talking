<template>
  <div class="message-container" :class="{ 'user-message': message.role === 'user' }">
    <div class="message-avatar">
      {{ message.role === 'user' ? '👤' : '💬' }}
    </div>
    <div class="message-content">
      <div class="message-text">
        <MarkdownRender :content="renderedContent" />
        <div v-if="message.role === 'assistant'" class="message-actions">
          <button class="extract-knowledge-btn" @click="handleExtractKnowledge" :disabled="isLoading">
            <span v-if="isLoading">提炼中...</span>
            <span v-else>提炼知识</span>
          </button>
        </div>
      </div>
      <div class="message-time">{{ formatTime(message.createTime) }}</div>
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
    const response = await knowledgeApi.extractKnowledge(props.message.id)
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

.message-actions {
  margin-top: 12px;
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}

.extract-knowledge-btn {
  padding: 6px 12px;
  background: var(--primary-color);
  color: white;
  border: none;
  border-radius: var(--border-radius-sm);
  font-size: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.extract-knowledge-btn:hover {
  background: var(--secondary-color);
  transform: translateY(-1px);
}

.extract-knowledge-btn:active {
  transform: translateY(0);
}

.extract-knowledge-btn:disabled {
  background: var(--text-muted);
  cursor: not-allowed;
  transform: none;
  opacity: 0.7;
}

/* Markdown 样式 */
:deep(p) {
  margin: var(--spacing-s) 0;
  color: var(--text-primary);
}

:deep(blockquote) {
  border-left: 4px solid var(--primary-color);
  padding-left: var(--spacing-m);
  margin: var(--spacing-s) 0;
  color: var(--text-secondary);
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
  background: none;
  border: none;
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