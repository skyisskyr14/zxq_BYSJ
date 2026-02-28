import Vue from 'vue'
import Vuex from 'vuex'
import auth from './auth'
import app from './app'
import dict from './dict'

Vue.use(Vuex)

export default new Vuex.Store({
  modules: {
    auth,
    app,
    dict
  }
})

