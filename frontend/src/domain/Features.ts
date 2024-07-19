import http from "../utils/http/http"
import { FeatureConfigs } from "@/domain/FeatureConfigs"
import { Experiments } from "@/domain/Experiments"
import { Status } from "@/model/Status"
import moment from "moment"

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

export interface FeatureTimeLine {
  type: FeatureTimeType
  title: string
  id: string
  description: string
  timestamp: string
  order: string
}

export enum FeatureTimeType {
  CONFIG_BEGIN = "CONFIG_BEGIN",
  CONFIG_END = "CONFIG_END",
  EXPERIMENT_BEGIN = "EXPERIMENT_BEGIN",
  EXPERIMENT_END = "EXPERIMENT_END",
  CURRENT = "CURRENT"
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
  private featureConfigs: FeatureConfigs = new FeatureConfigs()
  private experiments: Experiments = new Experiments()

  constructor(private readonly baseUrl: string = "") {}

  private getUrl(path: string) {
    return this.baseUrl + path
  }

  fetchAll(page: number, pageSize: number, featureKey?: string, enabled?: boolean): Promise<PageResponse<Feature>> {
    return http
      .get(this.getUrl(`/features`), { params: { page, pageSize, key: featureKey, enabled } })
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

  disable(featureKey: string): Promise<void> {
    return http.put(this.getUrl(`/features/${featureKey}/disable`), null).then()
  }

  enable(featureKey: string): Promise<void> {
    return http.put(this.getUrl(`/features/${featureKey}/enable`), null).then()
  }

  timeLine(featureKey: string): Promise<FeatureTimeLine[]> {
    return Promise.all([
      this.featureConfigs.fetchAll("", 0, 100, featureKey, ["RUNNING", "NOT_BEGIN"]),
      this.experiments.page({
        featureKey: featureKey,
        status: [Status.NOT_BEGIN, Status.COLLECTING, Status.PAUSED, Status.FINISH_COLLECT],
        page: 0,
        pageSize: 100
      })
    ]).then(([configs, experiments]) => {
      const result = [] as FeatureTimeLine[]
      const LONG_TIME = "长期有效"
      configs?.content?.forEach((config) => {
        result.push({
          type: FeatureTimeType.CONFIG_BEGIN,
          title: config.title,
          id: config.id,
          description: config.description,
          timestamp: config.startTime?.replace("T", " ") || LONG_TIME,
          order: config.startTime || moment().subtract(100, "year").format("YYYY-MM-DDTHH:mm:ss")
        })
        result.push({
          type: FeatureTimeType.CONFIG_END,
          title: config.title,
          id: config.id,
          description: config.description,
          timestamp: config.endTime?.replace("T", " ") || LONG_TIME,
          order: config.endTime || moment().add(100, "year").format("YYYY-MM-DDTHH:mm:ss")
        })
      })

      experiments?.content?.forEach((experiment) => {
        result.push({
          type: FeatureTimeType.EXPERIMENT_BEGIN,
          title: experiment.name,
          id: experiment.id,
          description: experiment.description,
          timestamp: experiment.status?.startTime?.replace("T", " ") || LONG_TIME,
          order: experiment.status?.startTime || moment().subtract(100, "year").format("YYYY-MM-DDTHH:mm:ss")
        })
        result.push({
          type: FeatureTimeType.EXPERIMENT_END,
          title: experiment.name,
          id: experiment.id,
          description: experiment.description,
          timestamp: experiment.status?.endTime?.replace("T", " ") || LONG_TIME,
          order: experiment.status?.endTime || moment().add(100, "year").format("YYYY-MM-DDTHH:mm:ss")
        })
      })

      result.push({
        type: FeatureTimeType.CURRENT,
        title: "",
        id: "",
        description: "当前时间",
        timestamp: "",
        order: moment().format("YYYY-MM-DDTHH:mm:ss")
      })
      return result.sort((a, b) => b.order.localeCompare(a.order))
    })
  }
}
