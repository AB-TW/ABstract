import { PageRequest } from "@/model/request/PageRequest"
import { Status } from "@/model/Status"

export interface ExperimentQueryRequest extends PageRequest {
  key?: string
  featureKey?: string
  status?: Status[]
}
