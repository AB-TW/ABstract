import http from "@/utils/http/http"
import { PageResponse } from "@/domain/Features"
import { FilterConditionQueryRequest } from "@/model/request/FilterConditionQueryRequest"
import { FilterConditionAddRequest } from "@/model/request/FilterConditionAddRequest"
import { FilterConditionUpdateRequest } from "@/model/request/FilterConditionUpdateRequest"

export interface FilterCondition {
  grayReleaseCondition?: GrayReleaseCondition
  attributeConditions?: CustomAttributeCondition[]
}

export interface GrayReleaseCondition {
  releasePercentage: number
}

export interface CustomAttributeCondition {
  name: string
  key: string
  value: string
  items: string[]
  operator: CompareOperator
}

export enum CompareOperator {
  EQUALS = "EQUALS",
  NOT_EQUALS = "NOT_EQUALS",
  GREATER_THAN = "GREATER_THAN",
  GREATER_THAN_OR_EQUALS = "GREATER_THAN_OR_EQUALS",
  LESS_THAN = "LESS_THAN",
  LESS_THAN_OR_EQUALS = "LESS_THAN_OR_EQUALS",
  IN = "IN",
  NOT_IN = "NOT_IN",
  VERSION_GREATER_THAN = "VERSION_GREATER_THAN",
  VERSION_GREATER_THAN_EQUALS = "VERSION_GREATER_THAN_EQUALS",
  VERSION_LESS_THAN = "VERSION_LESS_THAN",
  VERSION_LESS_THAN_OR_EQUALS = "VERSION_LESS_THAN_OR_EQUALS"
}

export const isListCompareOperator = (operator: CompareOperator): boolean =>
  [CompareOperator.IN, CompareOperator.NOT_IN].includes(operator)

export const isValidCustomAttributeCondition = (condition: CustomAttributeCondition): boolean => {
  if (!condition?.key) {
    return false
  }

  if (isListCompareOperator(condition.operator)) {
    return condition.items?.length > 0
  }

  return !!condition.value
}

export interface FilterConditionTemplate {
  templateId: string
  templateName: string
  attributeConditions: CustomAttributeCondition[]
  createdAt: string
  updatedAt: string
}

export const defaultFilterCondition = (): FilterCondition =>
  ({
    grayReleaseCondition: {
      releasePercentage: 100
    },
    attributeConditions: []
  } as FilterCondition)

export class FilterConditions {
  constructor(private readonly baseUrl: string = "") {}

  private getUrl(path: string) {
    return this.baseUrl + path
  }

  async getByPage(request: FilterConditionQueryRequest): Promise<PageResponse<FilterConditionTemplate>> {
    return await http.get(this.getUrl("/filterCondition"), { params: request }).then((res: any) => res?.data)
  }

  async getById(templateId: string): Promise<PageResponse<FilterConditionTemplate>> {
    return await http.get(this.getUrl(`/filterCondition/${templateId}`), {}).then((res: any) => res?.data)
  }

  async add(request: FilterConditionAddRequest): Promise<FilterConditionTemplate> {
    return await http.post(this.getUrl("/filterCondition"), request).then((res: any) => res?.data)
  }

  async update(request: FilterConditionUpdateRequest): Promise<void> {
    return await http.put(this.getUrl(`/filterCondition/${request.templateId}`), request).then((res: any) => res?.data)
  }

  async deleteById(templateId: string): Promise<void> {
    return await http.del(this.getUrl(`/filterCondition/${templateId}`), {}).then((res: any) => res?.data)
  }
}
