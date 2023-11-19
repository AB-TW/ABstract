import { ExperimentBucket } from "@/model/ExperimentBucket"
import { FilterCondition } from "@/domain/FilterConditions"
import { ExperimentStatus } from "@/model/ExperimentStatus"
import http from "@/utils/http/http"
import { ExperimentQueryRequest } from "@/model/request/ExperimentQueryRequest"
import { PageResponse } from "@/domain/Features"

export interface Experiment {
  id: string
  name: string
  description: string
  featureKey: string
  status: ExperimentStatus
  buckets: ExperimentBucket[]
  createdAt: string
  updatedAt: string
  filterCondition: FilterCondition
}

export class Experiments {
  constructor(private readonly baseUrl: string = "/api") {}

  private getUrl(path: string) {
    return this.baseUrl + path
  }

  async create(experiment: Experiment): Promise<Experiment> {
    return await http.post(this.getUrl("/experiments"), experiment).then((res: any) => res?.data as Experiment)
  }

  async page(request: ExperimentQueryRequest): Promise<PageResponse<Experiment>> {
    return await http.get(this.getUrl("/experiments"), request).then((res: any) => res?.data)
  }
}
