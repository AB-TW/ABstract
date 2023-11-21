import { Status } from "@/model/Status"

export const EXPERIMENT_STATUS_MAPPING = {
  [Status.DRAFT]: "待发布",
  [Status.PUBLISHED]: "未开始",
  [Status.PAUSED]: "已暂停",
  [Status.RUNNING]: "运行中",
  [Status.STOPPED]: "已停用",
  [Status.COLLECTING]: "数据收集中",
  [Status.FINISH_COLLECT]: "停止收集数据"
}
