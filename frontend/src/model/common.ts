interface PageResponse<T> {
  content: T[]
  total: number
  page: number
  pageSize: number
}

export type { PageResponse }
