import request from '@/utils/request'

export function getShopBaseInfoRequest() {
  return request({
    url: '/fd/shop/baseInfo',
    method: 'get'
  })
}

export function createShopBaseInfoRequest(data) {
  return request({
    url: '/fd/shop/create',
    method: 'post',
    data
  })
}

export function updateShopBaseInfoRequest(data) {
  return request({
    url: '/fd/shop/update',
    method: 'post',
    data
  })
}

export function deleteShopBaseInfoRequest() {
  return request({
    url: '/fd/shop/delete',
    method: 'post'
  })
}

export function listShopBaseInfoRequest() {
  return request({
    url: '/fd/shop/list',
    method: 'get'
  })
}
