import { request } from "@/utils/service"
import { PageSearchExperimentRequest } from "@/api/experiment/types/ExperimentRequest"
import { PageResponse } from "@/model/common"
import { Experiment } from "@/api/experiment/types/experiment"

export default {
  pageQueryExperiments(params: PageSearchExperimentRequest): Promise<PageResponse<Experiment>> {
    return request({
      url: "experiment/list",
      method: "get",
      params
    })
  }
}
