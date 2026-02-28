import request from '@/utils/request'

export function getCaptcha(type = 'image') {
  return request({
    url: '/auth/captcha',
    method: 'get',
    params: { type }
  })
}

export function loginRequest(data) {
  return request({
    url: '/auth/user/login',
    method: 'post',
    data
  })
}

export function adminLoginRequest(data) {
  return request({
    url: '/auth/admin/login',
    method: 'post',
    data
  })
}

export function registerUserRequest(data) {
  return request({
    url: '/fd/user/register',
    method: 'post',
    data
  })
}

export function registerMerchantRequest(data) {
  return request({
    url: '/fd/shop/register',
    method: 'post',
    data
  })
}
