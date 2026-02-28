import request from '@/utils/request'

export function listPetRequest() {
  return request({
    url: '/fd/pet/list',
    method: 'get'
  })
}

export function detailPetRequest(id) {
  return request({
    url: '/fd/pet/detail',
    method: 'get',
    params: { id }
  })
}

export function createPetRequest(data) {
  return request({
    url: '/fd/pet/create',
    method: 'post',
    data
  })
}

export function updatePetRequest(data) {
  return request({
    url: '/fd/pet/update',
    method: 'post',
    data
  })
}

export function deletePetRequest(id) {
  return request({
    url: '/fd/pet/delete',
    method: 'post',
    params: { id }
  })
}
