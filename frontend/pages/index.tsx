import { MouseEvent, useCallback, useRef, useState } from 'react';
import ProductTable from '../components/ProductTable';
import { Category, getCategories, getProducts, Product, refreshProducts } from '../services/ProductService';
import styles from '../styles/Home.module.css';
import dynamic from 'next/dynamic';

const ProductDetails = dynamic(() => import('../components/ProductDetails'));

export async function getStaticProps(context: any) {
  let categories = await getCategories();
  if (categories.length === 0) {
    const refreshed = await refreshProducts();
    if (refreshed) {
      categories = await getCategories();
    }
  }

  const categoryProducts = [];
  for (const category of categories) {
    const products = await getProducts(category, 0, 20);
    categoryProducts.push([category, products]);
  }

  return {
    props: {
      categories: categoryProducts
    }
  }
}

const Home = ({ categories }: { categories: Map<Category, Product[]> }) => {

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
        {Array.from(categories).map(([c, p]) =>
          <div key={c.id}>
            <button onClick={(e) => onCategorySelect(e, c)} className={styles.btn}>
              {c.name}
              {selectedCategory == c ?
                <span>🔼</span> : <span>🔽</span>
              }
            </button>
            {selectedCategory == c &&
              <ProductTable category={c} selectProduct={selectProduct} initialProducts={p} />
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
