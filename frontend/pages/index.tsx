import Head from 'next/head';
import { useState } from 'react';
import ProductDetails from '../src/components/ProductDetails';
import ProductTable from '../src/components/ProductTable';
import styles from '../styles/Home.module.css';
import { Category, Product } from '../src/types/product';
import { getCategories, refreshProducts } from '../src/components/service/ProductService';


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
      <main className={styles.main}>
          {categories.map(c => 
            <div key={c.id}>
              <button onClick={() => selectCategory(c)} className={styles.btn}>
                {c.name}
              </button>
              {selectedCategory == c && <ProductTable category={c} setActiveProduct={selectProduct}></ProductTable>}
            </div>
          )}
      </main>
      {selectedProduct && <aside>
          <ProductDetails product={selectedProduct} setActiveProduct={selectProduct}></ProductDetails>
        </aside>
      }
    </div>
  )
}

export default Home;
