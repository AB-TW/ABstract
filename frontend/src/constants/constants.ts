import { ExperimentStatus } from "@/api/experiment/types/experiment"

export const EXPERIMENT_STATUS_MAPPING = {
  [ExperimentStatus.DRAFT]: "待发布",
  [ExperimentStatus.PUBLISHED]: "未开始",
  [ExperimentStatus.PAUSED]: "已暂停",
  [ExperimentStatus.RUNNING]: "运行中",
  [ExperimentStatus.STOPPED]: "已停用"
}
