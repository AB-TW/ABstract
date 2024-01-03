import http from "../utils/http/http"

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
  constructor(private readonly baseUrl: string = "/api") {}

  private getUrl(path: string) {
    return this.baseUrl + path
  }

  getCustomerFeatureConfigs(customerId: CustomerId, featureKey: string): Promise<CustomerFeatureConfig[]> {
    return http
      .post(this.getUrl(`/customers/featureConfigs/${featureKey}`), customerId)
      .then((res: any) => res?.data as CustomerFeatureConfig[])
  }
}
