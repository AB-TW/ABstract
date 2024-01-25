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

  async getById(id: string): Promise<Experiment> {
    return await http.get(this.getUrl(`/experiments/${id}`), {}).then((res: any) => res?.data)
  }

  async update(experiment: Experiment): Promise<void> {
    return await http.put(this.getUrl(`/experiments/${experiment.id}`), experiment).then((res: any) => res?.data)
  }

  async publish(id: string) {
    return await http.put(this.getUrl(`/experiments/${id}/publish`), undefined).then((res: any) => res?.data)
  }

  async paused(id: string) {
    return await http.put(this.getUrl(`/experiments/${id}/pause`), undefined).then((res: any) => res?.data)
  }

  async stop(id: string) {
    return await http.put(this.getUrl(`/experiments/${id}/stop`), undefined).then((res: any) => res?.data)
  }

  async deleted(id: string) {
    return await http.del(this.getUrl(`/experiments/${id}`), undefined).then((res: any) => res?.data)
  }
}
