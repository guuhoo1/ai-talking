<template>
  <div class="knowledge-base-container">
    <div class="main-content">
      <h1>AI知识库</h1>
      <div class="knowledge-cards">
        <div v-for="knowledge in knowledgeList" :key="knowledge.id" class="knowledge-card">
          <h3>{{ knowledge.title }}</h3>
          <div class="tech-tags">
            <span v-for="tag in JSON.parse(knowledge.techTags)" :key="tag" class="tech-tag">
              {{ tag }}
            </span>
          </div>
          <p class="summary">{{ knowledge.summary }}</p>
          <div class="card-actions">
            <button class="detail-btn" @click="viewDetail(knowledge)">
              查看详情
            </button>
            <button class="copy-btn" @click="copyCode(knowledge.coreCode)">
              复制代码
            </button>
            <button class="delete-btn" @click="deleteKnowledge(knowledge.id)">
              删除
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
  
  <!-- 知识详情弹窗 -->
  <a-modal
    v-model:visible="isModalVisible"
    :title="selectedKnowledge?.title || '知识详情'"
    @cancel="handleCancel"
    width="800px"
    destroyOnClose
  >
    <div v-if="selectedKnowledge" class="knowledge-detail-content">
      <div class="tech-tags">
        <span v-for="tag in JSON.parse(selectedKnowledge.techTags)" :key="tag" class="tech-tag">
          {{ tag }}
        </span>
      </div>
      <div class="summary">
        <h3>摘要</h3>
        <p>{{ selectedKnowledge.summary }}</p>
      </div>
      <div v-if="selectedKnowledge.coreCode" class="core-code">
        <h3>核心代码</h3>
        <pre>{{ selectedKnowledge.coreCode }}</pre>
        <button class="copy-btn" @click="copyCode(selectedKnowledge.coreCode)">
          复制代码
        </button>
      </div>
      <div class="scene">
        <h3>使用场景</h3>
        <p>{{ selectedKnowledge.scene }}</p>
      </div>
      <div class="principle">
        <h3>原理说明</h3>
        <p>{{ selectedKnowledge.principle }}</p>
      </div>
      <div class="notes">
        <h3>注意事项</h3>
        <p>{{ selectedKnowledge.notes }}</p>
      </div>
      <div class="metadata">
        <p><strong>可复用性:</strong> {{ selectedKnowledge.reusable ? '是' : '否' }}</p>
        <p><strong>创建时间:</strong> {{ formatTime(selectedKnowledge.createTime) }}</p>
      </div>
    </div>
  </a-modal>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { knowledgeApi } from '../api'
import type { Knowledge } from '../types'

const knowledgeList = ref<Knowledge[]>([])
const isModalVisible = ref(false)
const selectedKnowledge = ref<Knowledge | null>(null)

onMounted(async () => {
  await loadKnowledge()
})

const loadKnowledge = async () => {
  try {
    const response = await knowledgeApi.getAllKnowledge() as any
    knowledgeList.value = response.data
  } catch (error) {
    console.error('加载知识库失败:', error)
  }
}

const viewDetail = (knowledge: Knowledge) => {
  selectedKnowledge.value = knowledge
  isModalVisible.value = true
}

const handleCancel = () => {
  isModalVisible.value = false
  selectedKnowledge.value = null
}

const copyCode = (code: string) => {
  navigator.clipboard.writeText(code).then(() => {
    alert('代码已复制到剪贴板')
  }).catch(err => {
    console.error('复制失败:', err)
  })
}

const deleteKnowledge = async (id: number) => {
  if (confirm('确定要删除这条知识吗？')) {
    try {
      await knowledgeApi.deleteKnowledge(id)
      await loadKnowledge()
      alert('删除成功')
    } catch (error) {
      console.error('删除失败:', error)
      alert('删除失败，请重试')
    }
  }
}

const formatTime = (time: string) => {
  const date = new Date(time)
  return date.toLocaleString()
}
</script>

<style scoped>
.knowledge-base-container {
  min-height: 100vh;
  background: var(--bg-color);
  overflow: hidden;
}

.main-content {
  padding: var(--spacing-xl);
  overflow-y: auto;
}

.main-content h1 {
  margin: 0 0 var(--spacing-xl) 0;
  font-size: var(--font-size-3xl);
  font-weight: var(--font-weight-bold);
  color: var(--text-primary);
  letter-spacing: -0.26px;
  line-height: var(--line-height-relaxed);
}

.knowledge-cards {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(400px, 1fr));
  gap: var(--spacing-l-plus);
}

.knowledge-card {
  background: var(--card-color);
  border: 1px solid var(--border-color);
  border-radius: var(--border-radius-lg);
  padding: var(--spacing-l-plus);
  box-shadow: var(--shadow-sm);
  transition: all var(--transition-normal);
}

.knowledge-card:hover {
  box-shadow: var(--shadow-md);
  transform: translateY(-4px);
  border-color: var(--primary-color);
}

.knowledge-card h3 {
  margin: 0 0 var(--spacing-m-plus) 0;
  font-size: var(--font-size-xl);
  font-weight: var(--font-weight-bold);
  color: var(--text-primary);
  line-height: var(--line-height-relaxed);
  letter-spacing: -0.14px;
}

.tech-tags {
  display: flex;
  flex-wrap: wrap;
  gap: var(--spacing-s);
  margin-bottom: var(--spacing-m-plus);
}

.tech-tag {
  padding: 4px 12px;
  background: var(--glass-dark);
  border: 1px solid var(--border-color);
  border-radius: var(--border-radius-pill);
  font-size: var(--font-size-sm);
  color: var(--text-primary);
  transition: all var(--transition-fast);
  font-weight: var(--font-weight-normal);
  letter-spacing: 0.6px;
  text-transform: uppercase;
  font-family: var(--font-mono);
}

.tech-tag:hover {
  background: var(--sidebar-hover-bg);
  border-color: var(--primary-color);
  color: var(--primary-color);
}

.summary {
  margin: 0 0 var(--spacing-l-plus) 0;
  font-size: var(--font-size-base);
  line-height: var(--line-height-relaxed);
  color: var(--text-secondary);
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
  font-weight: var(--font-weight-light-medium);
  letter-spacing: -0.14px;
}

.card-actions {
  display: flex;
  gap: var(--spacing-s);
}

.detail-btn, .copy-btn, .delete-btn {
  padding: 8px 18px 10px;
  border: 1px solid var(--border-color);
  border-radius: var(--border-radius-pill);
  font-size: var(--font-size-base);
  cursor: pointer;
  transition: all var(--transition-fast);
  font-weight: var(--font-weight-semibold);
  letter-spacing: -0.14px;
  font-family: var(--font-family);
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

.detail-btn {
  background: var(--primary-color);
  color: var(--text-light);
  border-color: var(--primary-color);
}

.detail-btn:hover {
  background: var(--primary-hover);
  border-color: var(--primary-hover);
  transform: translateY(-1px);
  box-shadow: var(--shadow-sm);
}

.detail-btn:focus {
  outline: var(--focus-outline);
  outline-offset: var(--focus-offset);
  box-shadow: none;
}

.copy-btn {
  background: var(--bg-color);
  color: var(--text-primary);
}

.copy-btn:hover {
  border-color: var(--primary-color);
  color: var(--primary-color);
  transform: translateY(-1px);
  box-shadow: var(--shadow-sm);
}

.copy-btn:focus {
  outline: var(--focus-outline);
  outline-offset: var(--focus-offset);
  box-shadow: none;
}

.delete-btn {
  background: var(--error-color);
  color: var(--text-light);
  border: 1px solid var(--error-color);
}

.delete-btn:hover {
  background: #ff7875;
  border-color: #ff7875;
  transform: translateY(-1px);
  box-shadow: var(--shadow-sm);
}

.delete-btn:focus {
  outline: var(--focus-outline);
  outline-offset: var(--focus-offset);
  box-shadow: none;
}

/* 弹窗内容样式 */
.knowledge-detail-content {
  padding: var(--spacing-m-plus) 0;
}

.knowledge-detail-content .tech-tags {
  margin-bottom: var(--spacing-l-plus);
}

.knowledge-detail-content .summary,
.knowledge-detail-content .core-code,
.knowledge-detail-content .scene,
.knowledge-detail-content .principle,
.knowledge-detail-content .notes,
.knowledge-detail-content .metadata {
  margin-bottom: var(--spacing-l-plus);
}

.knowledge-detail-content h3 {
  margin: 0 0 var(--spacing-s) 0;
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-bold);
  color: var(--text-primary);
  letter-spacing: -0.14px;
  line-height: var(--line-height-relaxed);
}

.knowledge-detail-content p {
  margin: 0;
  font-size: var(--font-size-base);
  line-height: var(--line-height-relaxed);
  color: var(--text-secondary);
  font-weight: var(--font-weight-light-medium);
  letter-spacing: -0.14px;
}

.knowledge-detail-content pre {
  background: var(--card-color);
  border: 1px solid var(--border-color);
  border-radius: var(--border-radius-lg);
  padding: var(--spacing-m-plus);
  margin: 0 0 var(--spacing-s) 0;
  font-size: var(--font-size-base);
  line-height: var(--line-height-normal);
  overflow-x: auto;
  max-height: 300px;
  overflow-y: auto;
  font-family: var(--font-mono);
  letter-spacing: normal;
}

.knowledge-detail-content .copy-btn {
  margin-top: var(--spacing-s);
}

.knowledge-detail-content .metadata {
  display: flex;
  flex-wrap: wrap;
  gap: var(--spacing-l-plus);
  font-size: var(--font-size-base);
  color: var(--text-secondary);
  font-weight: var(--font-weight-light-medium);
  letter-spacing: -0.14px;
}

/* 弹窗样式 */
:deep(.ant-modal) {
  background: var(--bg-color) !important;
  border-radius: var(--border-radius-lg) !important;
  box-shadow: var(--shadow-lg) !important;
}

:deep(.ant-modal-content) {
  background: var(--card-color) !important;
  border: 1px solid var(--border-color) !important;
  border-radius: var(--border-radius-lg) !important;
}

:deep(.ant-modal-header) {
  background: var(--card-color) !important;
  border-bottom: 1px solid var(--border-color) !important;
}

:deep(.ant-modal-title) {
  color: var(--text-primary) !important;
  font-weight: var(--font-weight-bold) !important;
  font-size: var(--font-size-xl) !important;
  letter-spacing: -0.14px !important;
  line-height: var(--line-height-relaxed) !important;
  font-family: var(--font-family) !important;
}

:deep(.ant-modal-body) {
  background: var(--card-color) !important;
  color: var(--text-primary) !important;
}

:deep(.ant-modal-footer) {
  background: var(--card-color) !important;
  border-top: 1px solid var(--border-color) !important;
  display: flex !important;
  justify-content: flex-end !important;
  gap: var(--spacing-s) !important;
}

:deep(.ant-btn) {
  border-radius: var(--border-radius-pill) !important;
  transition: all var(--transition-fast) !important;
  font-weight: var(--font-weight-semibold) !important;
  padding: 8px 18px 10px !important;
  letter-spacing: -0.14px !important;
  font-family: var(--font-family) !important;
  min-width: 80px !important;
  text-align: center !important;
}

:deep(.ant-btn:focus) {
  outline: var(--focus-outline) !important;
  outline-offset: var(--focus-offset) !important;
  box-shadow: none !important;
}

:deep(.ant-btn-primary) {
  background: var(--primary-color) !important;
  border-color: var(--primary-color) !important;
  color: var(--text-light) !important;
}

:deep(.ant-btn-primary:hover) {
  background: var(--primary-hover) !important;
  border-color: var(--primary-hover) !important;
  box-shadow: var(--shadow-sm) !important;
}

:deep(.ant-btn-default) {
  background: var(--bg-color) !important;
  border-color: var(--border-color) !important;
  color: var(--text-primary) !important;
}

:deep(.ant-btn-default:hover) {
  border-color: var(--primary-color) !important;
  color: var(--primary-color) !important;
  box-shadow: var(--shadow-sm) !important;
}

/* 全局弹窗样式 */
:global(.ant-modal-footer) {
  background: var(--card-color) !important;
  border-top: 1px solid var(--border-color) !important;
  display: flex !important;
  justify-content: flex-end !important;
  gap: var(--spacing-s) !important;
}

:global(.ant-btn) {
  border-radius: var(--border-radius-pill) !important;
  transition: all var(--transition-fast) !important;
  font-weight: var(--font-weight-semibold) !important;
  padding: 8px 18px 10px !important;
  letter-spacing: -0.14px !important;
  font-family: var(--font-family) !important;
  min-width: 80px !important;
  text-align: center !important;
}

:global(.ant-btn:focus) {
  outline: var(--focus-outline) !important;
  outline-offset: var(--focus-offset) !important;
  box-shadow: none !important;
}

:global(.ant-btn-primary) {
  background: var(--primary-color) !important;
  border-color: var(--primary-color) !important;
  color: var(--text-light) !important;
}

:global(.ant-btn-primary:hover) {
  background: var(--primary-hover) !important;
  border-color: var(--primary-hover) !important;
  box-shadow: var(--shadow-sm) !important;
}

:global(.ant-btn-default) {
  background: var(--bg-color) !important;
  border-color: var(--border-color) !important;
  color: var(--text-primary) !important;
}

:global(.ant-btn-default:hover) {
  border-color: var(--primary-color) !important;
  color: var(--primary-color) !important;
  box-shadow: var(--shadow-sm) !important;
}

@media (max-width: 768px) {
  .main-content {
    padding: var(--spacing-m-plus);
  }
  
  .knowledge-cards {
    grid-template-columns: 1fr;
  }
  
  /* 响应式弹窗宽度 */
  :deep(.ant-modal) {
    width: 90% !important;
    max-width: 90% !important;
  }
  
  .card-actions {
    flex-direction: column;
  }
  
  .detail-btn, .copy-btn, .delete-btn {
    width: 100%;
    justify-content: center;
  }
}
</style>