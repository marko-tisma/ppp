import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import { Category, getProducts, Product } from "../services/ProductService";
import styles from "../styles/ProductTable.module.css";
import ProductImage from "./ProductImage";

const debounce = (f: Function, timeout = 300) => {
  let timer: NodeJS.Timeout;
  return (...args: any[]) => {
    clearTimeout(timer);
    timer = setTimeout(() => { f.apply(this, args); }, timeout);
  }
}

let pageSize = 20;

const ProductTable = ({ category, selectProduct, initialProducts = [] }:
  { category: Category, selectProduct: Function, initialProducts: Product[] }) => {

  const [products, setProducts] = useState<Product[]>([]);
  const [page, setPage] = useState<number>(0);
  const [sortBy, setSortBy] = useState<string | undefined>(undefined);
  const [nameQuery, setNameQuery] = useState<string | undefined>(undefined);

  const tableRef = useRef<HTMLTableElement>(null);
  const containerRef = useRef<HTMLDivElement>(null);

  const firstUpdate = useRef(true);

  useEffect(() => {
    containerRef.current?.parentElement?.scrollIntoView({ behavior: 'smooth' });
  });

  useEffect(() => {
    async function reloadProducts() {
      setPage(0);
      const products = await getProducts(category, 0, pageSize, sortBy, nameQuery);
      setProducts(products);
    }
    if (firstUpdate.current && initialProducts.length > 0) {
      setProducts(initialProducts);
    } else {
      reloadProducts();
      tableRef.current?.scroll(0, 0);
    }
    firstUpdate.current = false;
  }, [category, initialProducts, sortBy, nameQuery])

  const onSort = useMemo(() => debounce((property: string) => {
    if (sortBy === property || sortBy?.substring(1) === property) {
      setSortBy(sortBy.at(0) == '-' ? `+${property}` : `-${property}`);
    } else {
      setSortBy(`+${property}`);
    }
  }), [sortBy]);

  const onSearch = useMemo(() => debounce(setNameQuery), []);

  const onScroll = useMemo(() => debounce(async () => {
    const table = tableRef.current;
    if (table && table.scrollHeight - table.scrollTop === table.clientHeight) {
      setPage(page + 1);
      let newPage = await getProducts(category, page + 1, pageSize, sortBy, nameQuery);
      newPage = newPage.filter(p => !products.find((value) => value.id === p.id));
      setProducts([...products, ...newPage]);
    }
  }), [products, page, sortBy, nameQuery]);

  return (
    <div className={styles.container} ref={containerRef}>
      <input type="text" onChange={(e) => onSearch(e.target.value)} className={styles.searchInput} placeholder="Search..."></input>
      {products.length > 0 ? (
        <div ref={tableRef} className={styles.productTableContainer} onScroll={onScroll}>
          <table className={styles.productTable}>
            <thead>
              <tr>
                <th className={styles.imageTh}></th>
                <th onClick={() => onSort('name')} className={styles.nameTh}>
                  Name{sortBy == '+name' ? <span>⬆️</span> : sortBy == '-name' ? <span>⬇️</span> : ""}
                </th>
                <th onClick={() => onSort('currentPrice')} className={styles.priceTh}>
                  Price{sortBy == '+currentPrice' ? <span>⬆️</span> : sortBy == '-currentPrice' ? <span>⬇️</span> : ""}
                </th>
              </tr>
            </thead>
            <tbody>
              {products && products.map(p =>
                <tr key={p.id} onClick={() => selectProduct(p)}>
                  <td>
                    <ProductImage product={p} width={64} height={64} />
                  </td>
                  <td className={styles.nameTd}>
                    {p.name}
                  </td>
                  <td className={styles.priceTd}>
                    {Number(p.currentPrice).toLocaleString()} RSD
                  </td>
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