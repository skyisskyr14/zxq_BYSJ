import request from '@/utils/request'

export function listCageRequest() {
  return request({
    url: '/fd/shop/cage/list',
    method: 'get'
  })
}

export function createCageRequest(data) {
  return request({
    url: '/fd/shop/cage/create',
    method: 'post',
    data
  })
}

export function updateCageRequest(data) {
  return request({
    url: '/fd/shop/cage/update',
    method: 'post',
    data
  })
}

export function deleteCageRequest(id) {
  return request({
    url: '/fd/shop/cage/delete',
    method: 'post',
    params: { id }
  })
}
