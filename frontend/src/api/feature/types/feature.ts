export enum DataType {
  BOOLEAN,
  STRING,
  NUMBER,
  OBJECT,
  ARRAY
}

export interface Validator {
  required: boolean
  maxLength: number
  max: number
  min: number
  pattern: string
}

export interface FeatureTemplate {
  dataTye: DataType
  key: string
  name: string
  description: string
  defaultValue: string
  validator: Validator
  items: Array<FeatureTemplate>
}

export interface Description {
  name: string
  description: string
  enable: boolean
  template: FeatureTemplate
  createdAt?: String
  updateAt?: String
}

export interface Feature {
  id: string
  description: Description
}
