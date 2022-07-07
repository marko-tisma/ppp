import Image from "next/image";
import { FC, useEffect, useState } from "react";
import { Line, LineChart, ResponsiveContainer, Tooltip, XAxis, YAxis } from "recharts";
import xImage from "../../public/x_button.png";
import { PriceHistory, Product, Specification } from "../types/product";
import ProductImage from "./ProductImage";
import { getProductHistory, getProductSpecs } from "./service/ProductService";

import styles from "../../styles/ProductDetails.module.css";

const ProductDetails = ({product, setActiveProduct}: {product: Product, setActiveProduct: Function}) => {

  const [specs, setSpecs] = useState<Specification[]>([]);
  const [history, setHistory] = useState<PriceHistory[]>([]);

  useEffect(() => {
    async function reloadDetails() {
      if (product) {
        setSpecs(await getProductSpecs(product));
        let to = new Date();
        let from = new Date();
        from.setMonth(from.getMonth() - 1);
        let history = await getProductHistory(product, from, to)
        history = history.filter((h, index) => history.findIndex(x => x.createdAt === h.createdAt) == index);
        setHistory(history);
      }
      else {
        setSpecs([]);
        setHistory([]);
      }
    }
    reloadDetails();
  }, [product])

  return product && (
    <div className={styles.detailsContainer}>
        <div className={styles.xImage} onClick={() => setActiveProduct(null)}>
          <Image src={xImage} width={32} height={32}></Image>
        </div>
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
      {history && history.length > 0 && <div className={styles.historyContainer}>
        <PriceHistoryChart history={history}></PriceHistoryChart>
      </div>}
    </div>
  )
}

const PriceHistoryChart = ({history}: {history: PriceHistory[]}) => (
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

export default ProductDetails;