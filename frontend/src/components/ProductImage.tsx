import Image from "next/image";
import { Product } from "../types/product";
import { apiUrl } from "./service/ProductService";

import noImage from "../../public/no_image.png";

const ProductImage: any = ({product, width, height}: {product: Product, width: number, height: number}) => {
  return product && product.images && product.images[0]
    ? <Image src={`${apiUrl}/images/download/${product.images[0].id}`} width={width} height={height}></Image>
    : <Image src={noImage} width={width} height={height}></Image>
}

export default ProductImage;