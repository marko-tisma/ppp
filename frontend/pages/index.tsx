import Head from 'next/head';
import Image from 'next/image';
import { useEffect, useRef, useState } from 'react';
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
  specs: Spec[]
}

interface Spec {
  name: string,
  value: string
}

interface Image {
  id: number
}

async function getCategories(): Promise<Category[]> {
  const res = await fetch(apiUrl + '/categories');
  return await res.json(); 
}

export async function getStaticProps(context: any) {
  const categories = await getCategories();
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

const Products: any = ({category} : {category: Category}) => {

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

  return (
    <div className={styles.productsContainer}>
      <input type="text" onChange={(e) => onSearch(e.target.value)} className={styles.searchInput}></input>
      {products.length > 0 ? (
        <div ref={tableRef} className={styles.productsTable} onScroll={onScroll} style={{backgroundColor: 'white'}}>
          <table>
            <thead>
              <tr>
                <th className={styles.imageTh}></th>
                <th onClick={() => onSort('name')}><h2>Name</h2></th>
                <th onClick={() => onSort('currentPrice')}><h2>Price</h2></th>
              </tr>
            </thead>
            <tbody>
              {products && products.map(p =>
                <tr key={p.id}>
                  <th className={styles.imageTh}>
                    {p.images && p.images[0] ? <Image src={`${apiUrl}/images/download/${p.images[0].id}`} width="64" height="64"></Image>
                    : <Image src={noImage} width="64" height="64"></Image>}
                  </th>
                  <th>{p.name}</th>
                  <th>{p.currentPrice}</th>
                </tr>
              )}
            </tbody>
          </table>
        </div> 
      ) : <h2>No results</h2>}
    </div>
  );
}


const Home: any = ({categories}: {categories: Category[]}) => {

  const [activeCategory, setActiveCategory] = useState<Category | null>(null);

  const selectCategory = (event: React.MouseEvent<HTMLButtonElement>, category: Category) => {
    setActiveCategory(activeCategory == category ? null : category);
    if (event.target instanceof Element) {
      const t: Element = event.target;
      setTimeout(() => t.scrollIntoView(true), 50);
    }
  }

  return (
    <div className={styles.container}>
      <Head>
        <title>PPP</title>
      </Head>
      <main className={styles.main}>
        <div>
          {categories.map(c => 
            <div key={c.id} className={styles.categoryContainer}>
              <button onClick={(e) => selectCategory(e, c)} className={styles.btn}>
                {c.name}
              </button>
              {activeCategory == c && <Products category={c}></Products>}
            </div>
          )}
        </div>
      </main>
    </div>
  )
}

export default Home;
