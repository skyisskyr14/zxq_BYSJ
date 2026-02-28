import request from '@/utils/request'

export function listServiceRequest() {
  return request({
    url: '/fd/shop/service/list',
    method: 'get'
  })
}

export function createServiceRequest(data) {
  return request({
    url: '/fd/shop/service/create',
    method: 'post',
    data
  })
}

export function updateServiceRequest(data) {
  return request({
    url: '/fd/shop/service/update',
    method: 'post',
    data
  })
}

export function deleteServiceRequest(id) {
  return request({
    url: '/fd/shop/service/delete',
    method: 'post',
    params: { id }
  })
}
