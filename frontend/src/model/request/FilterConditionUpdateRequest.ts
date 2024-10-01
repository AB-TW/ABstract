import { CustomAttributeCondition } from "@/domain/FilterConditions"

export interface FilterConditionUpdateRequest {
  templateId: string
  attributeCondition: CustomAttributeCondition
  templateName: string
}
