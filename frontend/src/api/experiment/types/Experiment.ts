import { ExperimentBucket } from "@/model/ExperimentBucket"
import { ExperimentStatus } from "@/model/ExperimentStatus"

export interface Experiment {
  id: string
  name: string
  description: string
  featureKey: string
  samplePercentage: number
  status: ExperimentStatus
  buckets: ExperimentBucket[]
  createdAt: string
  updatedAt?: string
}
