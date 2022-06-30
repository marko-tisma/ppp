import { useEffect, useRef, useState } from "react";
import { Line, LineChart, ResponsiveContainer, Tooltip, XAxis, YAxis } from "recharts";
import { PriceHistory, Product, Specification } from "../types/product";
import { getProductSpecs, getProductHistory } from "./service/ProductService";
import ProductImage from "./ProductImage";

import styles from "../../styles/ProductDetails.module.css";

const ProductDetails: any = ({product}: {product: Product}) => {

  const [specs, setSpecs] = useState<Specification[]>([]);
  const [history, setHistory] = useState<PriceHistory[]>([]);

  const containerRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    async function reloadDetails() {
      if (product) {
        setSpecs(await getProductSpecs(product));
        let to = new Date();
        let from = new Date();
        from.setMonth(from.getMonth() - 1);
        setHistory(await getProductHistory(product, from, to));
      }
      else {
        setSpecs([]);
        setHistory([]);
      }
    }
    reloadDetails();
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
        <PriceHistoryChart history={history}></PriceHistoryChart>
      </div>
    </div>
  )
}

const PriceHistoryChart: any = ({history}: {history: PriceHistory[]}) => {
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
        <Tooltip formatter={(value:any, name:any, props:any) => [Number(value).toLocaleString() + " RSD", "price"]}/>
        <Line type="monotone" dataKey="amount" activeDot={{ r: 8 }}></Line>
      </LineChart>
    </ResponsiveContainer>
    )
}

export default ProductDetails;