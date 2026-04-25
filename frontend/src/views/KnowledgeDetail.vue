<template>
  <div class="knowledge-detail-container">
    <div class="header">
      <button class="back-btn" @click="goBack">
        ← 返回知识库
      </button>
      <h1>{{ knowledge.title }}</h1>
    </div>
    <div class="content">
      <div class="tech-tags">
        <span v-for="tag in JSON.parse(knowledge.techTags)" :key="tag" class="tech-tag">
          {{ tag }}
        </span>
      </div>
      <div class="summary">
        <h2>概述</h2>
        <p>{{ knowledge.summary }}</p>
      </div>
      <div class="core-code">
        <h2>核心代码</h2>
        <div class="code-block">
          <pre><code>{{ knowledge.coreCode }}</code></pre>
          <button class="copy-btn" @click="copyCode(knowledge.coreCode)">
            复制代码
          </button>
        </div>
      </div>
      <div class="scene">
        <h2>使用场景</h2>
        <p>{{ knowledge.scene }}</p>
      </div>
      <div class="principle">
        <h2>原理说明</h2>
        <p>{{ knowledge.principle }}</p>
      </div>
      <div class="notes">
        <h2>注意事项</h2>
        <p>{{ knowledge.notes }}</p>
      </div>
      <div class="metadata">
        <p><strong>可复用性:</strong> {{ knowledge.reusable ? '是' : '否' }}</p>
        <p><strong>创建时间:</strong> {{ formatTime(knowledge.createTime) }}</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { knowledgeApi } from '../api'
import type { Knowledge } from '../types'

const router = useRouter()
const route = useRoute()
const knowledge = ref<Knowledge>({
  id: 0,
  title: '',
  summary: '',
  coreCode: '',
  techTags: '',
  scene: '',
  principle: '',
  notes: '',
  reusable: false,
  sourceMessageId: 0,
  createTime: ''
})

onMounted(async () => {
  const id = Number(route.params.id)
  await loadKnowledge(id)
})

const loadKnowledge = async (id: number) => {
  try {
    const response = await knowledgeApi.getKnowledgeById(id)
    knowledge.value = response.data
  } catch (error) {
    console.error('加载知识详情失败:', error)
  }
}

const goBack = () => {
  router.push('/knowledge')
}

const copyCode = (code: string) => {
  navigator.clipboard.writeText(code).then(() => {
    alert('代码已复制到剪贴板')
  }).catch(err => {
    console.error('复制失败:', err)
  })
}

const formatTime = (time: string) => {
  const date = new Date(time)
  return date.toLocaleString()
}
</script>

<style scoped>
.knowledge-detail-container {
  min-height: 100vh;
  background: var(--bg-color);
  padding: 32px;
}

.header {
  margin-bottom: 32px;
}

.back-btn {
  padding: 8px 16px;
  background: var(--bg-color);
  border: 1px solid var(--border-color);
  border-radius: var(--border-radius-md);
  color: var(--text-primary);
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s ease;
  margin-bottom: 16px;
}

.back-btn:hover {
  border-color: var(--primary-color);
  color: var(--primary-color);
}

.header h1 {
  margin: 0;
  font-size: 32px;
  font-weight: 700;
  color: var(--text-primary);
  line-height: 1.3;
}

.content {
  max-width: 800px;
  background: var(--card-color);
  border: 1px solid var(--border-color);
  border-radius: var(--border-radius-lg);
  padding: 32px;
  box-shadow: var(--shadow-sm);
}

.tech-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 24px;
}

.tech-tag {
  padding: 6px 16px;
  background: rgba(67, 97, 238, 0.1);
  border: 1px solid rgba(67, 97, 238, 0.2);
  border-radius: var(--border-radius-full);
  font-size: 14px;
  color: var(--primary-color);
}

.summary,
.core-code,
.scene {
  margin-bottom: 32px;
}

.summary h2,
.core-code h2,
.scene h2 {
  margin: 0 0 16px 0;
  font-size: 20px;
  font-weight: 600;
  color: var(--text-primary);
}

.summary p,
.scene p {
  margin: 0;
  font-size: 16px;
  line-height: 1.6;
  color: var(--text-secondary);
}

.code-block {
  position: relative;
  background: #f6f8fa;
  border: 1px solid #e1e4e8;
  border-radius: var(--border-radius-md);
  overflow: hidden;
}

.code-block pre {
  margin: 0;
  padding: 20px;
  overflow-x: auto;
  font-family: 'SFMono-Regular', Consolas, 'Liberation Mono', Menlo, monospace;
  font-size: 14px;
  line-height: 1.5;
}

.code-block code {
  background: none;
  padding: 0;
  color: var(--text-primary);
}

.copy-btn {
  position: absolute;
  top: 12px;
  right: 12px;
  padding: 6px 12px;
  background: rgba(255, 255, 255, 0.8);
  border: 1px solid #e1e4e8;
  border-radius: var(--border-radius-sm);
  font-size: 12px;
  color: #57606a;
  cursor: pointer;
  transition: all 0.3s ease;
}

.copy-btn:hover {
  background: white;
  border-color: var(--primary-color);
  color: var(--primary-color);
}

.metadata {
  border-top: 1px solid var(--border-color);
  padding-top: 24px;
  display: flex;
  gap: 32px;
  font-size: 14px;
  color: var(--text-muted);
}

@media (max-width: 768px) {
  .knowledge-detail-container {
    padding: 16px;
  }
  
  .header h1 {
    font-size: 24px;
  }
  
  .content {
    padding: 24px;
  }
  
  .metadata {
    flex-direction: column;
    gap: 12px;
  }
}
</style>