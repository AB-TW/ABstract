import http from "@/utils/http/http"

export class Ai {
  constructor(private readonly baseUrl: string = "/api") {}

  private getUrl(path: string) {
    return this.baseUrl + path
  }

  async ai(message: string, clientId: string): Promise<any> {
    return await http.streamGet(this.getUrl("/ai?message=" + message), { headers: { "x-client-id": clientId } })
  }

  history(clientId: string): Promise<any> {
    return http.get(this.getUrl("/ai/history"), { headers: { "x-client-id": clientId } })
  }

  clearHistory(clientId: string): Promise<any> {
    return http.post(this.getUrl("/ai/history/clear"), undefined, { headers: { "x-client-id": clientId } })
  }
}
