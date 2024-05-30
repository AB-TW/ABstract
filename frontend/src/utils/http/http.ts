import instance from "./axios"

const post = (url: string, data: any, config?: any) => {
  return new Promise((resolve, reject) =>
    instance
      .post(url, data, { headers: config?.headers, timeout: 30000 })
      .then((res) => resolve(res))
      .catch((error) => reject(error))
  )
}

const get = (url: string, config: any) => {
  return new Promise((resolve, reject) =>
    instance
      .get(url, { params: config.params, headers: config.headers, timeout: 30000 })
      .then((res) => resolve(res))
      .catch((error) => reject(error))
  )
}

const streamGet = (url: string, config: any = {}) => {
  return fetch(url, { method: "GET", headers: { ...config.headers, Accept: "text/event-stream" }, ...config })
}

const put = (url: string, data: any) => {
  return new Promise((resolve, reject) =>
    instance
      .put(url, data)
      .then((res) => resolve(res))
      .catch((error) => reject(error))
  )
}

const del = (url: string, params?: any) => {
  return new Promise((resolve, reject) =>
    instance
      .delete(url, { params })
      .then((res) => resolve(res))
      .catch((error) => reject(error))
  )
}

export default { post, get, put, del, streamGet }
