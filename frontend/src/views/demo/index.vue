<template>
  <div>
    <div>
      <el-card>
        HTTP Client Demo
        <div class="header">
          <el-input v-model="clientId" placeholder="请输入ClientId" class="search-input">
            <template #append>
              <el-button icon="Search" @click="fetchFeatureConfig" />
            </template>
          </el-input>
        </div>

        <el-card>
          <el-form>
            <el-form-item label="Response">
              <el-input type="textarea" placeholder="" v-model="response" autosize />
              <el-button :type="buttonType">Feature demo按钮</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-card>
    </div>
    <div>
      <el-card>
        RSocket Client Demo
        <div class="header">
          <el-input v-model="clientId" placeholder="请输入ClientId" class="search-input">
            <template #append>
              <el-button icon="Search" @click="fetchFeatureConfigWithRsocket" />
            </template>
          </el-input>
        </div>

        <el-card>
          <el-form>
            <el-form-item label="Response">
              <el-input type="textarea" placeholder="" v-model="rSocketResponse" autosize />
              <el-button :type="rSocketButtonType">Feature demo按钮</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue"
import { Customers } from "@/domain/Customers"
import FingerprintJS from "@fingerprintjs/fingerprintjs"
import { RSocket } from "rsocket-core"
import { Cancellable, OnTerminalSubscriber, Payload } from "rsocket-core/dist/RSocket"
import { decodeCompositeMetadata, decodeRoutes, Entry, WellKnownMimeType } from "rsocket-composite-metadata"
import MESSAGE_RSOCKET_ROUTING = WellKnownMimeType.MESSAGE_RSOCKET_ROUTING

const clientId = ref<string>("123456")
const buttonType = ref<string>("primary")
const response = ref<string>("")
buttonType.value = "success"
const rSocketButtonType = ref<string>("primary")
const rSocketResponse = ref<string>("")
rSocketButtonType.value = "success"

const invalidateFeature = (data) => {
  console.log(data)
  if (data == "demoFeature") {
    fetchFeatureConfigWithRsocket()
  }
}

const rSocket: Partial<RSocket> = {
  fireAndForget(payload: Payload, responderStream: OnTerminalSubscriber): Cancellable {
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

getDeviceFingerprint()
const fetchFeatureConfig = () => {
  customers
    .getCustomerFeatureConfigs(
      {
        customerId: "test",
        clientId: clientId.value
      },
      "demoFeature"
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
        customerId: "test",
        clientId: clientId.value
      },
      "demoFeature"
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
</style>
