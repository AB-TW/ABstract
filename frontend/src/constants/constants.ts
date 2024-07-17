import { Status } from "@/model/Status"

export const EXPERIMENT_STATUS_TEXT_MAPPING = {
  [Status.DRAFT]: "待发布",
  [Status.NOT_BEGIN]: "待开始",
  [Status.COLLECTING]: "数据收集中",
  [Status.FINISH_COLLECT]: "数据收集结束",
  [Status.PAUSED]: "已暂停",
  [Status.STOPPED]: "已停止"
}

export const EXPERIMENT_STATUS_TAG_STATUS_MAPPING = {
  [Status.DRAFT]: "info",
  [Status.PAUSED]: "warning",
  [Status.STOPPED]: "warning",
  [Status.COLLECTING]: "success",
  [Status.FINISH_COLLECT]: "danger",
  [Status.NOT_BEGIN]: "info"
}
