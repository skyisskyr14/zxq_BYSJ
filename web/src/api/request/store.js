import request from '@/utils/request'

export function getStoreListRequest() {
  return request({
    url: '/fd/shop/store/public/list',
    method: 'get'
  })
}

export function getStoreDetailRequest(id) {
  return request({
    url: `/fd/shop/store/public/detail/${id}`,
    method: 'get'
  })
}
