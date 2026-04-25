import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import Chat from '../views/Chat.vue'
import KnowledgeBase from '../views/KnowledgeBase.vue'
import KnowledgeDetail from '../views/KnowledgeDetail.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      redirect: '/chat'
    },
    {
      path: '/login',
      component: Login
    },
    {
      path: '/register',
      component: Register
    },
    {
      path: '/chat',
      component: Chat,
      meta: {
        requiresAuth: true
      }
    },
    {
      path: '/knowledge',
      component: KnowledgeBase,
      meta: {
        requiresAuth: true
      }
    },
    {
      path: '/knowledge/:id',
      component: KnowledgeDetail,
      meta: {
        requiresAuth: true
      }
    }
  ]
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.meta.requiresAuth && !token) {
    next('/login')
  } else {
    next()
  }
})

export default router