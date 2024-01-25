import http from "../utils/http/http"
import { RsocketRequester } from "@/utils/rsocket/rsocket-requester"
import { WellKnownMimeType } from "rsocket-composite-metadata"
import { RSocket } from "rsocket-core"

export interface CustomerId {
  customerId?: string
  clientId: string
  attributes?: any
}

export interface CustomerFeatureConfig {
  id?: string
  featureKey?: string
  key?: string
  data?: any
  saData?: any
  expiredAt?: string
  priority?: number
  updatedAt?: string
}

export class Customers {
  private requester: RsocketRequester

  constructor(private readonly responder: Partial<RSocket> = null, private readonly baseUrl: string = "/api") {
    this.requester = new RsocketRequester(this.responder)
  }

  private getUrl(path: string) {
    return this.baseUrl + path
  }

  getCustomerFeatureConfigs(customerId: CustomerId, featureKey: string): Promise<CustomerFeatureConfig[]> {
    return http
      .post(this.getUrl(`/customers/featureConfigs/${featureKey}`), customerId)
      .then((res: any) => res?.data as CustomerFeatureConfig[])
  }

  getCustomerFeatureConfigsWithRSocket(customerId: CustomerId, featureKey: string): Promise<CustomerFeatureConfig[]> {
    return new Promise((resolve, reject) => {
      const result = [] as CustomerFeatureConfig[]
      this.requester
        .route(`customers.featureConfigs.${featureKey}`)
        .data("")
        .metadata(WellKnownMimeType.APPLICATION_JSON, Buffer.from(JSON.stringify(customerId)))
        .retrieveFlowable()
        .map((data) => {
          console.log("customers.featureConfigs receive: " + data)
          result.push(JSON.parse(data) as CustomerFeatureConfig)
        })
        .subscribe({
          onComplete: () => {
            console.log("done")
            resolve(result)
          },
          onError: (error) => {
            console.log(error)
            reject(error)
          },
          onNext: (value) => {
            console.log(value)
          },
          // Nothing happens until `request(n)` is called
          onSubscribe: (sub) => sub.request(100)
        })
    })
  }
}
