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

export function uploadShopAvatarRequest(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/fd/shop/avatar/upload',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

export function getShopStoreDetailRequest() {
  return request({
    url: '/fd/shop/store/detail',
    method: 'get'
  })
}

export function createShopStoreRequest(data) {
  return request({
    url: '/fd/shop/store/create',
    method: 'post',
    data
  })
}

export function updateShopStoreRequest(data) {
  return request({
    url: '/fd/shop/store/update',
    method: 'post',
    data
  })
}

export function deleteShopStoreRequest() {
  return request({
    url: '/fd/shop/store/delete',
    method: 'post'
  })
}

export function uploadShopStoreImagesRequest(files = []) {
  const formData = new FormData()
  files.forEach(file => formData.append('files', file))
  return request({
    url: '/fd/shop/store/images/upload',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}
