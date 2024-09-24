import { CustomAttributeCondition } from "@/domain/FilterConditions"

export interface FilterConditionAddRequest {
  attributeConditions: CustomAttributeCondition[]
  templateName: string
}
