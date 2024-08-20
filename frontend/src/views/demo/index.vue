<template>
  <div>
    <el-card>
      <el-card-header>
        <el-form label-width="140px">
          <el-form-item label="Feature Key">
            <el-input v-model="featureKey" placeholder="请输入Feature Key" />
          </el-form-item>
          <el-form-item label="Client ID">
            <el-input v-model="clientId" placeholder="请输入ClientId" />
          </el-form-item>
          <el-form-item label="Customer ID">
            <el-input v-model="customerId" placeholder="请输入CustomerId" />
          </el-form-item>
          <el-form-item label="自定义属性" prop="trackingData">
            <div class="track-cls">
              <tracking-data-item
                v-for="(item, index) in attributes"
                :key="item.index"
                :item="item"
                parentProp="attributes"
                :enable-validation="false"
                :index="index"
                @deleteItem="deleteAttribute(index)"
              />
              <div>
                <el-button type="primary" @click="addAttribute">
                  <el-icon>
                    <Plus />
                  </el-icon>
                </el-button>
              </div>
            </div>
          </el-form-item>
        </el-form>
      </el-card-header>
      <el-divider />
      <div class="example-container">
        <el-card class="demo-card">
          <el-card-header> HTTP Client Demo</el-card-header>
          <el-divider />
          <el-form label-width="140">
            <el-form-item label="Effect Result">
              <el-button :type="buttonType" :tip="tip">Effect Result</el-button>
              <el-tooltip effect="dark" :content="tip" placement="top">
                <el-icon style="margin-left: 4px" :size="12">
                  <Warning />
                </el-icon>
              </el-tooltip>
            </el-form-item>
            <el-form-item label="Curl Command">
              <el-input type="textarea" placeholder="" v-model="curl" autosize />
            </el-form-item>
            <el-form-item>
              <el-button icon="Search" @click="fetchFeatureConfig" type="primary"> 查询</el-button>
            </el-form-item>
            <el-divider />
            <el-form-item label="Response">
              <el-input type="textarea" placeholder="" v-model="response" autosize />
            </el-form-item>
          </el-form>
        </el-card>
        <el-card class="demo-card">
          <el-card-header> RSocket Client Demo</el-card-header>
          <el-divider />
          <el-form label-width="140">
            <el-form-item label="Effect Result">
              <el-button :type="rSocketButtonType" :tip="tip">Effect Result</el-button>
              <el-tooltip effect="dark" :content="tip" placement="top">
                <el-icon style="margin-left: 4px" :size="12">
                  <Warning />
                </el-icon>
              </el-tooltip>
            </el-form-item>
            <el-form-item label="RSC Command">
              <el-input type="textarea" placeholder="" v-model="rsc" autosize />
            </el-form-item>
            <el-form-item>
              <el-button icon="Search" @click="fetchFeatureConfigWithRsocket" type="primary"> 查询</el-button>
            </el-form-item>
            <el-divider />
            <el-form-item label="Response">
              <el-input type="textarea" placeholder="" v-model="rSocketResponse" autosize />
            </el-form-item>
          </el-form>
        </el-card>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from "vue"
import { Customers } from "@/domain/Customers"
import FingerprintJS from "@fingerprintjs/fingerprintjs"
import { RSocket } from "rsocket-core"
import { Cancellable, Payload } from "rsocket-core/dist/RSocket"
import { decodeCompositeMetadata, decodeRoutes, Entry, WellKnownMimeType } from "rsocket-composite-metadata"
import MESSAGE_RSOCKET_ROUTING = WellKnownMimeType.MESSAGE_RSOCKET_ROUTING
import { ElButton, ElFormItem } from "element-plus"
import TrackingDataItem from "@/components/TrackingDataItem.vue"

const clientId = ref<string>("123456")
const buttonType = ref<string>("primary")
const response = ref<string>("")
buttonType.value = "success"
const rSocketButtonType = ref<string>("primary")
const rSocketResponse = ref<string>("")
rSocketButtonType.value = "success"
const featureKey = ref("demoFeature")
const attributes = ref([] as any[])
let attributesIndex = 0
const customerId = ref("111111")

const curl = computed(() => {
  return `curl -X POST http(s)://{host}/customers/featureConfigs/${featureKey.value} \\
-H 'Content-type: application/json' \\
-d '{"customerId": "${customerId.value}", "clientId": "${clientId.value}","attributes": ${JSON.stringify(
    getAttributesObject()
  )}}'
  `
})

const rsc = computed(() => {
  return `rsc --route=customers.featureConfigs.${featureKey.value} --stream -m '{"customerId": "${
    customerId.value
  }","clientId":"${clientId.value}","attributes":${JSON.stringify(
    getAttributesObject()
  )}}' --mmt application/json   tcp://{host}:7001`
})

const tip = "【response.data==true？buttonType=Success : buttonType=Danger】"

const deleteAttribute = (index) => {
  attributes.value.splice(index, 1)
}

const addAttribute = () => {
  attributes.value.push({
    attributesIndex: attributesIndex++,
    key: "",
    value: ""
  })
}

const invalidateFeature = (data) => {
  console.log(data)
  if (data == featureKey.value) {
    fetchFeatureConfigWithRsocket()
  }
}

const rSocket: Partial<RSocket> = {
  fireAndForget(payload: Payload): Cancellable {
    const map: Generator<Entry, void, any> = decodeCompositeMetadata(payload?.metadata)
    const entry = map.next(MESSAGE_RSOCKET_ROUTING.string)?.value
    const route = entry ? decodeRoutes(entry.content).next().value || "" : ""
    const data = payload?.data?.toString("utf-8") || ""
    console.log(route + ":" + data)
    if (route == "feature.changed") {
      invalidateFeature(data)
    }
    return {
      cancel: () => {}
    }
  }
}

const customers = new Customers(rSocket)

const getDeviceFingerprint = async () => {
  FingerprintJS.load().then((fp) => {
    fp.get().then((result) => {
      const fingerprintId = result.visitorId
      localStorage.setItem("browserId", fingerprintId)
      clientId.value = fingerprintId
    })
  })
}

const getAttributesObject = (): any => {
  return attributes.value.reduce((acc, cur) => {
    if (cur.key) {
      acc[cur.key] = cur.value
    }
    return acc
  }, {} as any)
}

getDeviceFingerprint()
const fetchFeatureConfig = () => {
  customers
    .getCustomerFeatureConfigs(
      {
        customerId: customerId.value,
        clientId: clientId.value,
        attributes: getAttributesObject() || {}
      },
      featureKey.value
    )
    .then((res) => {
      if (res.length > 0 && res[0]?.data === true) {
        buttonType.value = "success"
      } else {
        buttonType.value = "danger"
      }
      response.value = JSON.stringify(res, null, 2)
      console.log(res)
    })
    .catch((err) => {
      console.log(err)
    })
}

const fetchFeatureConfigWithRsocket = () => {
  customers
    .getCustomerFeatureConfigsWithRSocket(
      {
        customerId: customerId.value,
        clientId: clientId.value,
        attributes: getAttributesObject() || {}
      },
      featureKey.value
    )
    .then((res) => {
      if (res.length > 0 && res[0]?.data === true) {
        rSocketButtonType.value = "success"
      } else {
        rSocketButtonType.value = "danger"
      }
      rSocketResponse.value = JSON.stringify(res, null, 2)
      console.log(res)
    })
    .catch((err) => {
      console.log(err)
    })
}
</script>

<style scoped lang="scss">
.header {
  display: flex;
  align-items: center;
  gap: 20px;

  .search-input {
    width: 400px;
  }
}

.example-container {
  display: flex;
  justify-content: space-between;
}

.demo-card {
  width: 49%;
}
</style>
