import request from '../utils/request'
import type { LoginRequest, RegisterRequest, User, ChatSession, ChatMessage, ChatRequest } from '../types'

export const authApi = {
  login: (data: LoginRequest) => request.post('/auth/login', data),
  register: (data: RegisterRequest) => request.post('/auth/register', data),
  getUserInfo: () => request.get<User>('/user/info'),
  logout: () => request.post('/auth/logout')
}

export const chatApi = {
  getSessionList: () => request.get<ChatSession[]>('/chat/session/list'),
  createSession: (title: string) => request.post<ChatSession>('/chat/session/create', { title }),
  deleteSession: (id: number) => request.delete(`/chat/session/${id}`),
  getMessages: (sessionId: number) => request.get<ChatMessage[]>(`/chat/message/${sessionId}`),
  sendMessage: (data: ChatRequest) => {
        // 从localStorage获取token
        const token = localStorage.getItem('token') || ''
        // 创建一个包含token的EventSource，使用token作为参数名
        return new EventSource(`http://localhost:8080/api/chat/send?sessionId=${data.sessionId}&content=${encodeURIComponent(data.content)}&model=${data.model}&token=${encodeURIComponent(token)}`)
    }
}

export const knowledgeApi = {
  extractKnowledge: (messageId: number) => request.post('/knowledge/extract', { messageId }),
  getKnowledgeById: (id: number) => request.get(`/knowledge/${id}`),
  getAllKnowledge: () => request.get('/knowledge/all'),
  getKnowledgeByTechTag: (tag: string) => request.get(`/knowledge/by-tag/${tag}`),
  deleteKnowledge: (id: number) => request.delete(`/knowledge/${id}`)
}