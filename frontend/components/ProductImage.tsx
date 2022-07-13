import Image from "next/image";
import noImage from "../public/no_image.png";
import { apiUrl, Product } from "../services/ProductService";

const ProductImage: any = ({ product, width, height }: { product: Product, width: number, height: number }) => {
  return product && product.images && product.images[0]
    ? <Image src={`${apiUrl}/images/download/${product.images[0].id}`} width={width} height={height}></Image>
    : <Image src={noImage} width={width} height={height}></Image>
}

export default ProductImage;