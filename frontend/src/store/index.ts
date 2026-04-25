import { defineStore } from 'pinia'

interface UserState {
  id: number | null
  username: string | null
  token: string | null
}

interface ChatState {
  sessions: Array<{
    id: number
    title: string
    createTime: string
  }>
  currentSessionId: number | null
  messages: Record<number, Array<{
    id: number
    role: 'user' | 'assistant'
    content: string
    createTime: string
  }>>
  isStreaming: boolean
  currentStreamMessage: string
}

export const useUserStore = defineStore('user', {
  state: (): UserState => ({
    id: null,
    username: null,
    token: localStorage.getItem('token') || null
  }),
  actions: {
    setUser(user: { id: number; username: string; token: string }) {
      this.id = user.id
      this.username = user.username
      this.token = user.token
      localStorage.setItem('token', user.token)
    },
    logout() {
      this.id = null
      this.username = null
      this.token = null
      localStorage.removeItem('token')
    }
  }
})

export const useChatStore = defineStore('chat', {
  state: (): ChatState => ({
    sessions: [],
    currentSessionId: null,
    messages: {},
    isStreaming: false,
    currentStreamMessage: ''
  }),
  actions: {
    setSessions(sessions: Array<{
      id: number
      title: string
      createTime: string
    }>) {
      this.sessions = sessions
    },
    setCurrentSessionId(sessionId: number) {
      this.currentSessionId = sessionId
    },
    addSession(session: {
      id: number
      title: string
      createTime: string
    }) {
      this.sessions.unshift(session)
      this.currentSessionId = session.id
      // 为新会话初始化messages数组
      this.messages[session.id] = []
    },
    deleteSession(sessionId: number) {
      this.sessions = this.sessions.filter(s => s.id !== sessionId)
      if (this.currentSessionId === sessionId) {
        this.currentSessionId = this.sessions.length > 0 ? this.sessions[0].id : null
      }
      delete this.messages[sessionId]
    },
    setMessages(sessionId: number, messages: Array<{
      id: number
      role: 'user' | 'assistant'
      content: string
      createTime: string
    }>) {
      this.messages[sessionId] = messages
    },
    addMessage(sessionId: number, message: {
      id: number
      role: 'user' | 'assistant'
      content: string
      createTime: string
    }) {
      if (!this.messages[sessionId]) {
        this.messages[sessionId] = []
      }
      this.messages[sessionId].push(message)
    },
    setIsStreaming(isStreaming: boolean) {
      this.isStreaming = isStreaming
    },
    setCurrentStreamMessage(message: string) {
      this.currentStreamMessage = message
    }
  }
})