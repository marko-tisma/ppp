import { MouseEvent, useCallback, useRef, useState } from 'react';
import ProductDetails from '../components/ProductDetails';
import ProductTable from '../components/ProductTable';
import { Category, getCategories, Product, refreshProducts } from '../services/ProductService';
import styles from '../styles/Home.module.css';

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

const Home = ({ categories }: { categories: Category[] }) => {

  const [selectedCategory, setSelectedCategory] = useState<Category | null>(null);
  const [selectedProduct, setSelectedProduct] = useState<Product | null>(null);

  const selectedCategoryButton = useRef<Element>();

  const onCategorySelect = useCallback((event: MouseEvent, category: Category) => {
    if (event.target instanceof Element) {
      selectedCategoryButton.current = event.target;
    }
    setSelectedCategory(selectedCategory == category ? null : category);
  }, [selectedCategory]);

  const selectProduct = useCallback((product: Product) => {
    setSelectedProduct(selectedProduct == product ? null : product);
  }, [selectedProduct]);

  return (
    <div className={styles.container}>
      <aside className={styles.sidebar}>
        <button className={styles.sidebarBtn}>
          <span>👤</span>Login
        </button>
        <button className={styles.sidebarBtn}>
          <span>🛑</span>Logout
        </button>
      </aside>
      <main className={styles.main}>
        {categories.map(c =>
          <div key={c.id}>
            <button onClick={(e) => onCategorySelect(e, c)} className={styles.btn}>
              {c.name}
              {selectedCategory == c ?
                <span>🔼</span> : <span>🔽</span>
              }
            </button>
            {selectedCategory == c &&
              <ProductTable category={c} selectProduct={selectProduct} />
            }
          </div>
        )}
      </main>
      {selectedProduct &&
        <ProductDetails product={selectedProduct} selectProduct={selectProduct} />
      }
    </div>
  )
}

export default Home;
