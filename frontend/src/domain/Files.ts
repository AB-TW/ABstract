import http from "../utils/http/http"

export class Files {
  private fileServiceUrl = import.meta.env.VITE_FILE_SERVICE_URL || "http://localhost:8081"

  async uploadFile(file: File): Promise<{ url: string }> {
    const formData = new FormData()
    formData.append("file", file)
    return http
      .post(`${this.fileServiceUrl}/files`, formData, {
        headers: {
          "Content-Type": "multipart/form-data"
        }
      })
      .then((res: any) => {
        return res.data as { url: string }
      }) as Promise<{ url: string }>
  }
}
