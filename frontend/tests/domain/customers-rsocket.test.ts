import { describe, expect, it, vi } from "vitest"
import { Customers } from "@/domain/Customers"

vi.mock("@/utils/rsocket/rsocket-requester", () => ({
  RsocketRequester: vi.fn().mockImplementation(() => ({
    route: () => ({
      data: () => ({
        metadata: () => ({
          retrieveFlowable: () => ({
            map: (mapper: (data: unknown) => void) => ({
              subscribe: (observer: any) => {
                observer.onSubscribe({ request: vi.fn() })
                try {
                  mapper({ key: "config-a", data: { enabled: true } })
                  observer.onComplete()
                } catch (error) {
                  observer.onError(error)
                }
              }
            })
          })
        })
      })
    })
  }))
}))

describe("Customers RSocket domain service", () => {
  it("accepts parsed feature config objects from the requester", async () => {
    const customers = new Customers()

    await expect(
      customers.getCustomerFeatureConfigsWithRSocket(
        { customerId: "customer-1", clientId: "client-1", attributes: {} },
        "feature-a"
      )
    ).resolves.toEqual([{ key: "config-a", data: { enabled: true } }])
  })
})
