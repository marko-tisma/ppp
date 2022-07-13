export const apiUrl = "http://localhost:8080/api/v1";

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

export async function getProducts(
  category: Category,
  page: number,
  size: number,
  sortBy: string | null,
  query: string | null
): Promise<Product[]> {
  const urlParams = new URLSearchParams();
  urlParams.set("page", page.toString());
  urlParams.set("size", size.toString());
  if (sortBy) urlParams.set("sortBy", sortBy);
  if (query) urlParams.set("nameQuery", query);

  const url = `${apiUrl}/categories/${category.id}?${urlParams}`;
  console.log(url)
  const res = await fetch(url);
  return await res.json();
}

export async function getProductHistory(product: Product, from: Date, to: Date): Promise<PriceHistory[]> {
  let toISO = to.toISOString();
  toISO = toISO.substring(0, toISO.indexOf("T"));
  let fromISO = from.toISOString();
  fromISO = fromISO.substring(0, fromISO.indexOf("T"));

  const res = await fetch(`${apiUrl}/products/${product.id}/history?from=${fromISO}&to=${toISO}`);
  return await res.json();
}

export async function getProductSpecs(product: Product): Promise<Specification[]> {
  const res = await fetch(`${apiUrl}/products/${product.id}/specifications`);
  return await res.json();
}

export async function getCategories(): Promise<Category[]> {
  const res = await fetch(apiUrl + '/categories');
  return await res.json();
}

export async function refreshProducts(): Promise<boolean> {
  const res = await fetch(apiUrl + '/products/refresh');
  return res.ok;
}
