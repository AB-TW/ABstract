export interface FilterCondition {
  grayReleasePercentage: number
  customAttributeConditions: CustomAttributeCondition[]
}

export interface CustomAttributeCondition {
  key: string
  operator: CompareOperator
  value: string | string[]
}

export enum CompareOperator {
  EQUALS = "EQUALS",
  NOT_EQUALS = "NOT_EQUALS",
  NOT_CONTAINS = "NOT_CONTAINS",
  GREATER_THAN = "GREATER_THAN",
  GREATER_THAN_OR_EQUALS = "GREATER_THAN_OR_EQUALS",
  LESS_THAN = "LESS_THAN",
  LESS_THAN_OR_EQUALS = "LESS_THAN_OR_EQUALS",
  IN = "IN",
  NOT_IN = "NOT_IN"
}
