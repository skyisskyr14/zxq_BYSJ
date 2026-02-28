import request from '@/utils/request'

export function getUserBaseInfoRequest() {
  return request({
    url: '/fd/user/baseInfo',
    method: 'get'
  })
}

