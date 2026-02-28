import { login, getUserByRole } from '@/mock'

const STORAGE_KEY = 'pet-boarding-auth'

const getStored = () => {
  try {
    return JSON.parse(localStorage.getItem(STORAGE_KEY) || '{}')
  } catch (e) {
    return {}
  }
}

const setStored = data => {
  localStorage.setItem(STORAGE_KEY, JSON.stringify(data))
}

const state = {
  token: getStored().token || '',
  role: getStored().role || '',
  userInfo: getStored().userInfo || null,
  permissions: getStored().permissions || [],
  superAdmin: !!getStored().superAdmin,
  loginMessage: getStored().loginMessage || ''
}

const getters = {
  token: state => state.token,
  role: state => state.role,
  userInfo: state => state.userInfo,
  permissions: state => state.permissions,
  superAdmin: state => state.superAdmin,
  loginMessage: state => state.loginMessage
}

const mutations = {
  SET_AUTH(state, payload) {
    state.token = payload.token || ''
    state.role = payload.role || ''
    state.userInfo = payload.userInfo || null
    state.permissions = payload.permissions || []
    state.superAdmin = !!payload.superAdmin
    state.loginMessage = payload.loginMessage || ''
    setStored({
      token: state.token,
      role: state.role,
      userInfo: state.userInfo,
      permissions: state.permissions,
      superAdmin: state.superAdmin,
      loginMessage: state.loginMessage
    })
  },
  CLEAR_AUTH(state) {
    state.token = ''
    state.role = ''
    state.userInfo = null
    state.permissions = []
    state.superAdmin = false
    state.loginMessage = ''
    setStored({})
  }
}

const actions = {
  async login({ commit }, payload) {
    const user = login(payload)
    commit('SET_AUTH', { role: user.role, userInfo: user, permissions: [], superAdmin: false, loginMessage: '' })
    return user
  },
  async demoLogin({ commit }, role) {
    const user = getUserByRole(role)
    if (!user) throw new Error('角色不可用')
    commit('SET_AUTH', { role: user.role, userInfo: user, permissions: [], superAdmin: false, loginMessage: '' })
    return user
  },
  logout({ commit }) {
    commit('CLEAR_AUTH')
  },
  switchRole({ commit, state }, role) {
    const user = getUserByRole(role)
    if (!user) throw new Error('角色不可用')
    commit('SET_AUTH', {
      token: state.token,
      role: user.role,
      userInfo: user,
      permissions: state.permissions || [],
      superAdmin: state.superAdmin,
      loginMessage: state.loginMessage
    })
    return user
  }
}

export default {
  namespaced: true,
  state,
  getters,
  mutations,
  actions
}

