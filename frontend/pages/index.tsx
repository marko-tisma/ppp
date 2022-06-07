import Head from 'next/head';
import Image from 'next/image';
import { useEffect, useRef, useState } from 'react';
import { Line, LineChart, ResponsiveContainer, XAxis, YAxis } from 'recharts';
import noImage from "../public/no_image.png";
import styles from '../styles/Home.module.css';

const apiUrl = "http://localhost:8080/api/v1";

interface Category {
  id: number,
  name: string
}

interface Product {
  id: number,
  category: Category,
  name: string,
  images: Image[],
  currentPrice: number,
  description: string,
  specifications: Specification[]
}

interface Specification {
  name: string,
  value: string
}

interface PriceHistory {
  amount: number,
  createdAt: Date
}

interface Image {
  id: number
}

async function getCategories(): Promise<Category[]> {
  const res = await fetch(apiUrl + '/categories');
  return await res.json(); 
}

async function refreshProducts(): Promise<boolean> {
  const res = await fetch(apiUrl + '/products/refresh');
  return res.ok;
}

export async function getStaticProps(context: any) {
  let categories = await getCategories();
  if (categories.length === 0) {
    const refreshed = await refreshProducts();
    if (refreshed) {
      categories = await getCategories();
    }
  }
  return {
    props: {
      categories
    }
  }
}

const debounce = (f: Function, timeout = 300) => {
  let timer: NodeJS.Timeout;
  return (...args: any[]) => {
    clearTimeout(timer);
    timer = setTimeout(() => { f.apply(this, args); }, timeout);
  }
}

const ProductImage: any = ({product, width, height}: {product: Product, width: number, height: number}) => {
  return product && product.images && product.images[0]
    ? <Image src={`${apiUrl}/images/download/${product.images[0].id}`} width={width} height={height}></Image>
    : <Image src={noImage} width={width} height={height}></Image>
}

const Products: any = ({category, setActiveProduct} : {category: Category, setActiveProduct: Function}) => {

  const [products, setProducts] = useState<Product[]>([]);
  const [page, setPage] = useState<number>(0);
  const [sortBy, setSortBy] = useState<string | null>(null);
  const [searchString, setSearchString] = useState<string | null>(null);
  const [loading, setLoading] = useState<boolean>(false);
  const tableRef = useRef<HTMLTableElement>(null);

  let pageSize = 20;

  async function fetchProducts(page: number): Promise<Product[]> {
    const urlParams = new URLSearchParams();
    urlParams.set("page", page.toString());
    urlParams.set("size", pageSize.toString());
    if (sortBy) urlParams.set("sortBy", sortBy);
    if (searchString) urlParams.set("nameQuery", searchString);
    const url = `${apiUrl}/categories/${category.id}?${urlParams}`;
    console.log(url)
    const res = await fetch(url);
    return await res.json();
  }

  useEffect(() => {
    async function repopulateProducts() {
      setPage(0);
      const products = await fetchProducts(0);
      setProducts(products);
      const table: HTMLTableElement | null = tableRef.current;
      if (table) table.scroll(0, 0);
    }
    repopulateProducts();
  }, [sortBy, searchString])

  const onSort = debounce((property: string) => {
    if (sortBy === property || sortBy?.substring(1) === property) {
      setSortBy(sortBy.at(0) == '-' ? `+${property}` : `-${property}`);
    } else {
      setSortBy(`+${property}`);
    }
  });

  const onSearch = debounce(setSearchString);

  const onScroll = debounce(async () => {
    const table: HTMLTableElement | null = tableRef.current;
    if (table && table.scrollHeight - table.scrollTop === table.clientHeight) {
      setLoading(true);
      setPage(page + 1);
      let newPage = await fetchProducts(page + 1);
      newPage = newPage.filter(p => !products.find((value) => value.id == p.id));
      setProducts([...products, ...newPage]);
      setLoading(false);
    }
  });

  const popup = useRef<HTMLDivElement>(null);

  return (
    <div className={styles.productsContainer}>
      <input type="text" onChange={(e) => onSearch(e.target.value)} className={styles.searchInput}></input>
      {products.length > 0 ? (
        <div ref={tableRef} className={styles.productTableContainer} onScroll={onScroll}>
          <table className={styles.productTable}>
            <thead>
              <tr>
                <th className={styles.imageTh}></th>
                <th onClick={() => onSort('name')} className={styles.nameTh}>
                  <h1>Name{sortBy == '+name' ? <span>⬆️</span> : sortBy == '-name' ? <span>⬇️</span>: ""} </h1>
                </th>
                <th onClick={() => onSort('currentPrice')} className={styles.priceTh}>
                  <h2>Price{sortBy == '+currentPrice' ? <span>⬆️</span> : sortBy == '-currentPrice' ? <span>⬇️</span>: ""}</h2>
                </th>
              </tr>
            </thead>
            <tbody>
              {products && products.map(p =>
                  <tr key={p.id} onClick={() => setActiveProduct(p)}>
                  <td>
                    <ProductImage product={p} width={64} height={64}></ProductImage>
                  </td>
                  <td className={styles.nameTd}>{p.name}</td>
                  <td className={styles.priceTd}>{Number(p.currentPrice).toLocaleString()} RSD</td>
                </tr>
              )}
            </tbody>
          </table>
        </div> 
      ) : <h2>No results</h2>}
    </div>
  );
}

const PriceHistory: any = ({history}: {history: PriceHistory[]}) => {
  return history && history.length > 0 && (
    <ResponsiveContainer width="100%" height="100%">
      <LineChart 
        data={history}
        margin={{
          top: 30,
          right: 10,
          left: 0,
          bottom: 30
        }}
      >
        <XAxis dataKey="createdAt"/>
        <YAxis dataKey="amount"/>
        <Line type="monotone" dataKey="amount"></Line>
      </LineChart>
    </ResponsiveContainer>
    )
}

const Details: any = ({product}: {product: Product}) => {

  const [specs, setSpecs] = useState<Specification[]>([]);
  const [history, setHistory] = useState<PriceHistory[]>([]);

  const containerRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    async function getProductSpecs() {
      const res = await fetch(`${apiUrl}/products/${product.id}/specifications`);
      setSpecs(await res.json());
    }

    async function getProductHistory() {
      let now = new Date();
      let to = now.toISOString();
      to = to.substring(0, to.indexOf("T"));

      now.setMonth(now.getMonth() - 1);
      let from = now.toISOString();
      from = from.substring(0, from.indexOf("T"));

      const res = await fetch(`${apiUrl}/products/${product.id}/history?from=${from}&to=${to}`);
      setHistory(await res.json());
    }

    if (product) {
      getProductSpecs();
      getProductHistory();
    }
    else {
      setSpecs([]);
      setHistory([]);
    }
  }, [product])

  return product && (
    <div className={styles.detailsTableContainer} ref={containerRef}>
      <ProductImage product={product} width={160} height={160}></ProductImage>
      <h3>{product.name}</h3>
      <table className={styles.detailsTable}>
        <tbody>
          {specs.map(s => 
          <tr key={s.name} className={styles.detailsTableTr}>
            <td className={styles.detailsTableTd}>{s.name}</td>
            <td className={styles.detailsTableTd}>{s.value}</td>
          </tr>
        )}
        </tbody>
      </table>
      <div className={styles.historyContainer}>
        <PriceHistory history={history}></PriceHistory>
      </div>
    </div>
  )
}

const Home: any = ({categories}: {categories: Category[]}) => {

  const [selectedCategory, setSelectedCategory] = useState<Category | null>(null);
  const [selectedProduct, setSelectedProduct] = useState<Product | null>(null);

  const selectCategory = (category: Category) => {
    setSelectedCategory(selectedCategory == category ? null : category);
  }

  const selectProduct = (product: Product) => {
    setSelectedProduct(selectedProduct == product ? null : product);
  }

  return (
    <div className={styles.container}>
      <Head>
        <title>PPP</title>
      </Head>
      <aside className={styles.leftAside}></aside>
      <main className={styles.main}>
          {categories.map(c => 
            <div key={c.id}>
              <button onClick={() => selectCategory(c)} className={styles.btn}>
                {c.name}
              </button>
              {selectedCategory == c && <Products category={c} setActiveProduct={selectProduct}></Products>}
            </div>
          )}
      </main>
      <aside className={styles.rightAside}>
        <Details product={selectedProduct}></Details>
      </aside>
    </div>
  )
}

export default Home;
