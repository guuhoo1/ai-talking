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
import { useRouter } from 'vue-router'
import { Modal } from 'ant-design-vue'
import { knowledgeApi } from '../api'
import type { Knowledge } from '../types'

const router = useRouter()
const knowledgeList = ref<Knowledge[]>([])
const isModalVisible = ref(false)
const selectedKnowledge = ref<Knowledge | null>(null)

onMounted(async () => {
  await loadKnowledge()
})

const loadKnowledge = async () => {
  try {
    const response = await knowledgeApi.getAllKnowledge()
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
  padding: 32px;
  overflow-y: auto;
}

.main-content h1 {
  margin: 0 0 32px 0;
  font-size: 28px;
  font-weight: 700;
  color: var(--text-primary);
}

.knowledge-cards {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(400px, 1fr));
  gap: 24px;
}

.knowledge-card {
  background: var(--card-color);
  border: 1px solid var(--border-color);
  border-radius: var(--border-radius-lg);
  padding: 24px;
  box-shadow: var(--shadow-sm);
  transition: all 0.3s ease;
}

.knowledge-card:hover {
  box-shadow: var(--shadow-md);
  transform: translateY(-4px);
}

.knowledge-card h3 {
  margin: 0 0 16px 0;
  font-size: 18px;
  font-weight: 600;
  color: var(--text-primary);
  line-height: 1.4;
}

.tech-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 16px;
}

.tech-tag {
  padding: 4px 12px;
  background: rgba(67, 97, 238, 0.1);
  border: 1px solid rgba(67, 97, 238, 0.2);
  border-radius: var(--border-radius-full);
  font-size: 12px;
  color: var(--primary-color);
}

.summary {
  margin: 0 0 20px 0;
  font-size: 14px;
  line-height: 1.5;
  color: var(--text-secondary);
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-actions {
  display: flex;
  gap: 12px;
}

.detail-btn, .copy-btn {
  padding: 8px 16px;
  border: 1px solid var(--border-color);
  border-radius: var(--border-radius-md);
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.detail-btn {
  background: var(--primary-color);
  color: white;
  border-color: var(--primary-color);
}

.detail-btn:hover {
  background: var(--secondary-color);
  border-color: var(--secondary-color);
  transform: translateY(-1px);
}

.copy-btn {
  background: var(--bg-color);
  color: var(--text-primary);
}

.copy-btn:hover {
  border-color: var(--primary-color);
  color: var(--primary-color);
  transform: translateY(-1px);
}

.delete-btn {
  background: #ff4d4f;
  color: white;
  border: 1px solid #ff4d4f;
  padding: 8px 16px;
  border-radius: var(--border-radius-md);
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.delete-btn:hover {
  background: #ff7875;
  border-color: #ff7875;
  transform: translateY(-1px);
}

/* 弹窗内容样式 */
.knowledge-detail-content {
  padding: 20px 0;
}

.knowledge-detail-content .tech-tags {
  margin-bottom: 20px;
}

.knowledge-detail-content .summary,
.knowledge-detail-content .core-code,
.knowledge-detail-content .scene,
.knowledge-detail-content .principle,
.knowledge-detail-content .notes,
.knowledge-detail-content .metadata {
  margin-bottom: 24px;
}

.knowledge-detail-content h3 {
  margin: 0 0 12px 0;
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
}

.knowledge-detail-content p {
  margin: 0;
  font-size: 14px;
  line-height: 1.5;
  color: var(--text-secondary);
}

.knowledge-detail-content pre {
  background: var(--bg-color);
  border: 1px solid var(--border-color);
  border-radius: var(--border-radius-md);
  padding: 16px;
  margin: 0 0 12px 0;
  font-size: 14px;
  line-height: 1.5;
  overflow-x: auto;
  max-height: 300px;
  overflow-y: auto;
}

.knowledge-detail-content .copy-btn {
  margin-top: 8px;
}

.knowledge-detail-content .metadata {
  display: flex;
  flex-wrap: wrap;
  gap: 24px;
  font-size: 14px;
  color: var(--text-secondary);
}

@media (max-width: 768px) {
  .knowledge-base-container {
    flex-direction: column;
  }
  
  .sidebar {
    width: 100%;
    border-right: none;
    border-bottom: 1px solid var(--border-color);
    padding: 16px;
  }
  
  .tag-filter {
    flex-direction: row;
    flex-wrap: wrap;
  }
  
  .main-content {
    padding: 16px;
  }
  
  .knowledge-cards {
    grid-template-columns: 1fr;
  }
  
  /* 响应式弹窗宽度 */
  :deep(.ant-modal) {
    width: 90% !important;
    max-width: 90% !important;
  }
}
</style>