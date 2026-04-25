export interface LoginRequest {
  username: string
  password: string
}

export interface RegisterRequest {
  username: string
  password: string
}

export interface User {
  id: number
  username: string
}

export interface ChatSession {
  id: number
  title: string
  createTime: string
}

export interface ChatMessage {
  id: number
  sessionId: number
  role: 'user' | 'assistant'
  content: string
  createTime: string
}

export interface ChatRequest {
  sessionId: number
  content: string
  model: string
}

export interface ChatResponse {
  content: string
  done: boolean
}