import http from "@/utils/http/http"

export class Ai {
  constructor(private readonly baseUrl: string = "/api") {}

  private getUrl(path: string) {
    return this.baseUrl + path
  }

  async ai(message: string): Promise<any> {
    return await http.streamGet(this.getUrl("/ai?message=" + message))
  }
}
