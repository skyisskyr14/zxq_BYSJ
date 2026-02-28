const state = {
  sidebarCollapsed: false
}

const getters = {
  sidebarCollapsed: state => state.sidebarCollapsed
}

const mutations = {
  TOGGLE_SIDEBAR(state) {
    state.sidebarCollapsed = !state.sidebarCollapsed
  },
  SET_SIDEBAR(state, val) {
    state.sidebarCollapsed = val
  }
}

export default {
  namespaced: true,
  state,
  getters,
  mutations
}

