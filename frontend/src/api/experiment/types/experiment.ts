export interface Experiment {
  id: string
  name: string
  description: string
  featureKey: string
  samplePercentage: number
  status: ExperimentStatus
  buckets: ExperimentBucket[]
  createdUsername: string
  createdAt: string
  updatedAt?: string
}

export interface ExperimentBucket {
  key: string
  name: string
  description: string
  config: any
  percentage: number
}

export enum ExperimentStatus {
  DRAFT = "DRAFT",
  PUBLISHED = "PUBLISHED",
  RUNNING = "RUNNING",
  PAUSED = "PAUSED",
  STOPPED = "STOPPED"
}
