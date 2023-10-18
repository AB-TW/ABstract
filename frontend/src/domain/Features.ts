import http from "../utils/http/http"
import { FeatureConfig } from "@/domain/FeatureConfigs"

export interface Feature {
  id?: string
  featureKey: string
  name: string
  description?: string
  enabled: boolean
  dataSource: Array<FeatureTemplate>
  template: FeatureTemplate
}

export interface PageResponse<T> {
  total: number
  page: number
  pageSize: number
  content: T[]
}

export interface FeatureTemplate {
  key?: string
  name?: string
  description?: string
  dataType: TemplateDataType
  defaultValue?: unknown | string | object
  validator?: any
  items?: FeatureTemplate[]
}

export enum TemplateDataType {
  BOOLEAN = "BOOLEAN",
  STRING = "STRING",
  NUMBER = "NUMBER",
  OBJECT = "OBJECT",
  ARRAY = "ARRAY"
}

export const getDefaultValue = (feature: Feature): any => {
  return getTemplateDefaultValue(feature.template)
}

export const getTemplateDefaultValue = (template: FeatureTemplate | undefined): any | any[] => {
  if (!template) {
    return null
  }

  if (template.dataType === TemplateDataType.OBJECT) {
    const result = {} as any
    ;(template.items || []).forEach((item: FeatureTemplate) => {
      result[item.key as string] = getTemplateDefaultValue(item)
    })
    return result
  } else if (template.dataType === TemplateDataType.ARRAY) {
    const result = [] as any[]
    if (template.items && template.items.length > 0) {
      result.push(getTemplateDefaultValue(template.items[0]))
    }
    return result
  } else if (template.dataType === TemplateDataType.BOOLEAN) {
    return template.defaultValue == "true" || template.defaultValue == true
  } else if (template.dataType === TemplateDataType.STRING) {
    return template.defaultValue || ""
  } else if (template.dataType === TemplateDataType.NUMBER) {
    return Number(template.defaultValue) || 0
  }

  return template.defaultValue || ""
}

export class Features {
  constructor(private readonly baseUrl: string = "/api") {}

  private getUrl(path: string) {
    return this.baseUrl + path
  }

  fetchAll(page: number, pageSize: number, featureKey?: string, enabled?: boolean): Promise<PageResponse<Feature>> {
    return http
      .get(this.getUrl(`/features`), { page, pageSize, key: featureKey, enabled })
      .then((res: any) => res?.data as PageResponse<Feature>)
  }

  fetch(featureKey: string): Promise<Feature> {
    return http.get(this.getUrl(`/features/${featureKey}`), {}).then((res: any) => res?.data as Feature)
  }

  create(feature: Feature): Promise<Feature> {
    feature.enabled = true
    return http.post(this.getUrl(`/features`), feature).then((res: any) => res?.data as Feature)
  }

  update(feature: Feature): Promise<void> {
    return http.put(this.getUrl(`/features/${feature.featureKey}`), feature).then()
  }

  publish(featureKey: string): Promise<void> {
    return http.put(this.getUrl(`/features/${featureKey}/publish`), null).then()
  }

  disable(featureKey: string): Promise<void> {
    return http.put(this.getUrl(`/features/${featureKey}/disable`), null).then()
  }

  enable(featureKey: string): Promise<void> {
    return http.put(this.getUrl(`/features/${featureKey}/enable`), null).then()
  }

  updateConfig(featureKey: string, configId: string, featureConfig: FeatureConfig): Promise<void> {
    return http.put(this.getUrl(`/features/${featureKey}/configs/${configId}`), featureConfig).then()
  }
}
