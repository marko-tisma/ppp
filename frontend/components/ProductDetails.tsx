import { useEffect, useState } from "react";
import { Line, LineChart, ResponsiveContainer, Tooltip, XAxis, YAxis } from "recharts";
import { getProductHistory, getProductSpecs, PriceHistory, Product, Specification } from "../services/ProductService";
import styles from "../styles/ProductDetails.module.css";
import ProductImage from "./ProductImage";

const ProductDetails = ({ product, selectProduct }: { product: Product, selectProduct: Function }) => {

  const [specs, setSpecs] = useState<Specification[]>([]);
  const [history, setHistory] = useState<PriceHistory[]>([]);

  useEffect(() => {
    async function reloadDetails() {
      setSpecs(await getProductSpecs(product));
      let to = new Date();
      let from = new Date();
      from.setFullYear(from.getFullYear() - 1);
      let history = await getProductHistory(product, from, to)
      history = history.filter((h, index) => history.findIndex(x => x.createdAt === h.createdAt) == index);
      setHistory(history);
    }
    reloadDetails();
  }, [product])

  return (
    <div className={styles.container}>
      <div className={styles.close} onClick={() => selectProduct(null)}>
        <span>❌</span>
      </div>
      <ProductImage product={product} width={160} height={160} />
      <div className={styles.productName}>{product.name}</div>
      <table className={styles.detailsTable}>
        <tbody>
          {specs.map(s =>
            <tr key={s.name}>
              <td>{s.name}</td>
              <td>{s.value}</td>
            </tr>
          )}
        </tbody>
      </table>
      <div className={styles.historyContainer}>
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
            <XAxis dataKey="createdAt" />
            <YAxis dataKey="amount" />
            <Tooltip formatter={
              (value: number, name: any, props: any) =>
                [Number(value).toLocaleString() + " RSD", "price"]
            } />
            <Line type="monotone" dataKey="amount" activeDot={{ r: 8 }} />
          </LineChart>
        </ResponsiveContainer>
      </div>
    </div>
  )
}

export default ProductDetails;