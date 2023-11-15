import { Status } from "@/model/Status"

export interface ExperimentStatus {
  status: Status
  staticStatus: Status
  startTime: string
  endCollectTime: string
  endTime: string
}
