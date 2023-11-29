import { Status } from "@/model/Status"

export const EXPERIMENT_STATUS_MAPPING = {
  [Status.DRAFT]: "待发布",
  [Status.PAUSED]: "已暂停",
  [Status.STOPPED]: "已停止",
  [Status.COLLECTING]: "数据收集中",
  [Status.FINISH_COLLECT]: "数据收集结束",
  [Status.NOT_BEGIN]: "待开始"
}
