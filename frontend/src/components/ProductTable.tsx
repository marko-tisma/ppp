import { useEffect, useRef, useState } from "react";
import { Category, Product } from "../types/product";
import { getProducts } from "./service/ProductService";
import ProductImage from "./ProductImage";

import styles from "../../styles/ProductTable.module.css";

const debounce = (f: Function, timeout = 300) => {
  let timer: NodeJS.Timeout;
  return (...args: any[]) => {
    clearTimeout(timer);
    timer = setTimeout(() => { f.apply(this, args); }, timeout);
  }
}

const ProductTable: any = ({category, setActiveProduct} : {category: Category, setActiveProduct: Function}) => {

  const [products, setProducts] = useState<Product[]>([]);
  const [page, setPage] = useState<number>(0);
  const [sortBy, setSortBy] = useState<string | null>(null);
  const [nameQuery, setNameQuery] = useState<string | null>(null);

  const tableRef = useRef<HTMLTableElement>(null);

  let pageSize = 20;

  useEffect(() => {
    async function reloadProducts() {
      setPage(0);
      const products = await getProducts(category, 0, pageSize, sortBy, nameQuery);
      setProducts(products);
    }
    reloadProducts();
    const table: HTMLTableElement | null = tableRef.current;
    if (table) table.scroll(0, 0);
  }, [sortBy, nameQuery])

  const onSort = debounce((property: string) => {
    if (sortBy === property || sortBy?.substring(1) === property) {
      setSortBy(sortBy.at(0) == '-' ? `+${property}` : `-${property}`);
    } else {
      setSortBy(`+${property}`);
    }
  });

  const onSearch = debounce(setNameQuery);

  const onScroll = debounce(async () => {
    const table: HTMLTableElement | null = tableRef.current;
    if (table && table.scrollHeight - table.scrollTop === table.clientHeight) {
      setPage(page + 1);
      let newPage = await getProducts(category, page + 1, pageSize, sortBy, nameQuery);
      newPage = newPage.filter(p => !products.find((value) => value.id == p.id));
      setProducts([...products, ...newPage]);
    }
  });

  return (
    <div className={styles.productsContainer}>
      <input type="text" onChange={(e) => onSearch(e.target.value)} className={styles.searchInput} placeholder="Search..."></input>
      {products.length > 0 ? (
        <div ref={tableRef} className={styles.productTableContainer} onScroll={onScroll}>
          <table className={styles.productTable}>
            <thead>
              <tr>
                <th className={styles.imageTh}></th>
                <th onClick={() => onSort('name')} className={styles.nameTh}>
                  Name{sortBy == '+name' ? <span>⬆️</span> : sortBy == '-name' ? <span>⬇️</span>: ""}
                </th>
                <th onClick={() => onSort('currentPrice')} className={styles.priceTh}>
                  Price{sortBy == '+currentPrice' ? <span>⬆️</span> : sortBy == '-currentPrice' ? <span>⬇️</span>: ""}
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

export default ProductTable;