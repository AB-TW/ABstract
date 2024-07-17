<template>
  <el-timeline style="max-width: 600px" v-loading="loading">
    <el-timeline-item
      v-for="(activity, index) in activities"
      :key="index"
      :icon="activity.icon"
      :type="activity.type"
      :color="activity.color"
      :size="activity.size"
      :hollow="activity.hollow"
      :timestamp="activity.timestamp"
    >
      {{ activity.content }}
    </el-timeline-item>
  </el-timeline>
</template>

<script lang="ts" setup>
import { ArrowDownBold, ArrowUpBold, StarFilled } from "@element-plus/icons-vue"
import { Features, FeatureTimeLine, FeatureTimeType } from "@/domain/Features"
import { ref } from "vue"

const features = new Features()

const loading = ref(true)

const props = defineProps({
  featureKey: {
    type: String,
    required: true
  }
})
const activities = ref([])
console.log(props.featureKey)

function getPrefixOfTimeLine(item: FeatureTimeLine) {
  return [FeatureTimeType.CONFIG_BEGIN, FeatureTimeType.CONFIG_END].includes(item.type)
    ? "[配置] "
    : FeatureTimeType.CURRENT === item.type
    ? ""
    : "[实验] "
}

function getIconOfTimeLint(item: FeatureTimeLine) {
  return [FeatureTimeType.CONFIG_BEGIN, FeatureTimeType.EXPERIMENT_BEGIN].includes(item.type)
    ? ArrowDownBold
    : FeatureTimeType.CURRENT === item.type
    ? StarFilled
    : ArrowUpBold
}

features
  .timeLine(props.featureKey)
  .then((result) => {
    activities.value = result.map((item) => {
      return {
        icon: getIconOfTimeLint(item),
        content: getPrefixOfTimeLine(item) + item.title,
        timestamp: item.timestamp,
        color: item.type === FeatureTimeType.CURRENT ? "#0bbd87" : "#f56c6c"
      }
    })
  })
  .finally(() => (loading.value = false))
</script>
