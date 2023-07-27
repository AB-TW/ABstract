import { PageResponse } from "./FeatureFlags"
import http from "../utils/http/http"

export class FeatureConfigs {
  constructor(private readonly baseUrl: string = "/features") {}

  private getUrl(path: string) {
    return this.baseUrl + path
  }

  fetchAll(title: string, page: number, pageSize: number, featureKey?: string): Promise<PageResponse<FeatureConfig>> {
    return http
      .get(this.getUrl(`/${featureKey}/configs`), {
        title,
        page,
        pageSize,
        featureKey
        // status: StaticStatus.DRAFT
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
  displayRule?: DisplayRule
  priority?: number
  templateData?: any
  customerCriteriaCondition?: CustomerCriteriaCondition
}

interface CustomerCriteriaCondition {
  whiteListCondition?: WhiteListCondition
  locationCondition?: LocationCondition
  abTestingCondition?: AbTestingCondition
}

interface WhiteListCondition {
  whiteList: string[]
}

interface LocationCondition {
  adCodes: string[]
}

interface AbTestingCondition {
  experimentId: string
  bucketKey: string
}

interface DisplayRule {
  type: DisplayRuleType
  times: number
  dailyConditions?: DailyCondition[]
}

enum DisplayRuleType {
  EVERYTIME = "EVERYTIME",
  FIX_TIMES = "FIX_TIMES",
  DAY_FIX_TIMES = "DAY_FIX_TIMES"
}

interface DailyCondition {
  dayOfWeek: number
  start: string
  end: string
}

interface TimeRange {
  startDate: string
  endDate: string
}

enum StaticStatus {
  PUBLISHED = "PUBLISHED",
  DRAFT = "DRAFT",
  DISABLED = "DISABLED",
  DELETED = "DELETED"
}
