import request from '@/utils/request'

export function getUserBaseInfoRequest() {
  return request({
    url: '/fd/user/baseInfo',
    method: 'get'
  })
}

export function updateUserBaseInfoRequest(data) {
  return request({
    url: '/fd/user/update',
    method: 'post',
    params: data
  })
}

export function uploadUserAvatarRequest(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/fd/user/avatar/upload',
    method: 'post',
    data: formData
  })
}
