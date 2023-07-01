import { request } from "@/utils/service"
import { Feature } from "@/api/feature/types/feature"

/** 增 */
export function createFeatureApi(data: Feature) {
  return request({
    url: "table",
    method: "post",
    data
  })
}

/** 删 */
export function deleteFeatureApi(id: string) {
  return request({
    url: `table/${id}`,
    method: "delete"
  })
}

/** 改 */
export function updateFeatureApi(data: Feature) {
  return request({
    url: "table",
    method: "put",
    data
  })
}

/** 查 */
export function getFeatureDataApi(params: Feature) {
  return Promise.resolve([])
}
