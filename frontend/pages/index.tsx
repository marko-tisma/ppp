import dynamic from 'next/dynamic';
import { useRouter } from 'next/router';
import { Fragment, MouseEvent, useCallback, useEffect, useState } from 'react';
import ProductTable from '../components/ProductTable';
import { getUser, logout, User } from '../services/UserService';
import { Category, getCategories, getProducts, Product, refreshProducts } from '../services/ProductService';
import styles from '../styles/Home.module.css';

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

  const router = useRouter();

  const [selectedCategory, setSelectedCategory] = useState<Category | null>(null);
  const [selectedProduct, setSelectedProduct] = useState<Product | null>(null);
  const [user, setUser] = useState<User | null>(null);

  useEffect(() => {
    setUser(getUser());
  }, [])

  const onCategorySelect = useCallback((event: MouseEvent, category: Category) => {
    setSelectedCategory(selectedCategory == category ? null : category);
  }, [selectedCategory]);

  const selectProduct = useCallback((product: Product) => {
    setSelectedProduct(selectedProduct == product ? null : product);
  }, [selectedProduct]);

  const onLogout = useCallback(() => {
    logout();
    setUser(null);
  }, []);

  return (
    <div className={styles.container}>
      <aside className={styles.sidebar}>
        {!user &&
          <button className={styles.sidebarBtn} onClick={() => router.push('/login')}>
            <span>👤</span>Login
          </button>
        }
        {user &&
          <Fragment>
            <div className={styles.username}>Username: {user.username}</div>
            <button className={styles.sidebarBtn} onClick={onLogout}>
              <span>🛑</span>Logout
            </button>
          </Fragment>
        }
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
