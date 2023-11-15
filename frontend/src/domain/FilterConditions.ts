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
  value: string | string[]
  operator: CompareOperator
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

export const defaultFilterCondition = (): FilterCondition =>
  ({
    grayReleaseCondition: {
      releasePercentage: 100
    },
    attributeConditions: []
  } as FilterCondition)
