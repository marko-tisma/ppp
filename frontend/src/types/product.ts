
export interface Category {
  id: number,
  name: string
}

export interface Product {
  id: number,
  category: Category,
  name: string,
  images: Image[],
  currentPrice: number,
  description: string,
  specifications: Specification[]
}

export interface Specification {
  name: string,
  value: string
}

export interface PriceHistory {
  amount: number,
  createdAt: Date
}

export interface Image {
  id: number
}