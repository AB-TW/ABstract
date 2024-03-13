import { RSocket } from "rsocket-core"
import { RSocketConnector } from "rsocket-core"
import { WebsocketClientTransport } from "rsocket-websocket-client"
import { WellKnownMimeType } from "rsocket-composite-metadata"
import { RsocketRequesterSpec } from "./rsocket-requester.spec"

const MESSAGE_RSOCKET_COMPOSITE_METADATA = WellKnownMimeType.MESSAGE_RSOCKET_COMPOSITE_METADATA

export class RsocketRequester {
  private _rSocket: any
  private isRsocketClosed = true
  constructor(private readonly responder: Partial<RSocket> = null) {}

  private makeConnector(): RSocketConnector {
    return new RSocketConnector({
      setup: {
        keepAlive: 5000,
        lifetime: 30000,
        dataMimeType: "application/json",
        metadataMimeType: MESSAGE_RSOCKET_COMPOSITE_METADATA.string
      },
      transport: new WebsocketClientTransport({
        url: import.meta.env.VITE_WEB_SOCKET_URL || "ws://localhost:7002",
        wsCreator: (url) => new WebSocket(url) as any,
        debug: true
      }),
      responder: this.responder
    })
  }

  async getRSocket(): Promise<RSocket> {
    if (this._rSocket && !this.isRsocketClosed) {
      return this._rSocket
    }
    this._rSocket = await this.makeConnector().connect()
    this.isRsocketClosed = false
    this._rSocket.onClose(() => (this.isRsocketClosed = true))
    return this._rSocket
  }

  public route(route: string): RsocketRequesterSpec {
    return new RsocketRequesterSpec(this, route)
  }
}
