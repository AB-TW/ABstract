import { PageResponse } from "./Features"
import http from "../utils/http/http"
import { FilterCondition } from "./FilterConditions"

export class FeatureConfigs {
  constructor(private readonly baseUrl: string = "/features") {}

  private getUrl(path: string) {
    return this.baseUrl + path
  }

  fetchAll(
    title: string,
    page: number,
    pageSize: number,
    featureKey: string,
    status?: string[]
  ): Promise<PageResponse<FeatureConfig>> {
    return http
      .get(this.getUrl(`/${featureKey}/configs`), {
        params: {
          title,
          page,
          pageSize,
          featureKey,
          status: status
        }
      })
      .then((res: any) => res?.data as PageResponse<FeatureConfig>)
  }

  fetchById(featureKey: string, id: string): Promise<FeatureConfig> {
    return http.get(this.getUrl(`/${featureKey}/configs/${id}`), {}).then((res: any) => res?.data as FeatureConfig)
  }

  create(featureKey: string, config: FeatureConfig): Promise<FeatureConfig> {
    return http.post(this.getUrl(`/${featureKey}/configs`), config).then((res: any) => res?.data as FeatureConfig)
  }

  update(featureKey: string, id: string, config: FeatureConfig): Promise<FeatureConfig> {
    return http.put(this.getUrl(`/${featureKey}/configs/${id}`), config).then()
  }

  delete(featureKey: string, id: string): Promise<void> {
    return http.del(this.getUrl(`/${featureKey}/configs/${id}`), {}).then()
  }

  publish(featureKey: string, id: string): Promise<void> {
    return http.put(this.getUrl(`/${featureKey}/configs/${id}/publish`), {}).then()
  }

  disable(featureKey: string, id: string): Promise<void> {
    return http.put(this.getUrl(`/${featureKey}/configs/${id}/disable`), {}).then()
  }

  top(featureKey: any, id: string) {
    return http.put(this.getUrl(`/${featureKey}/configs/${id}/top`), {}).then()
  }
}

export interface FeatureConfig {
  id?: string
  type: string
  title?: string
  description?: string
  key?: string
  featureKey?: string
  data: any
  saData?: any
  staticStatus?: StaticStatus
  status?: string
  startTime?: string
  endTime?: string
  priority?: number
  templateData?: any
  filterCondition?: FilterCondition
}

enum StaticStatus {
  PUBLISHED = "PUBLISHED",
  DRAFT = "DRAFT",
  DISABLED = "DISABLED",
  DELETED = "DELETED"
}
