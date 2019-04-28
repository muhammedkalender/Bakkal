package com.example.bakkal;

public class CommonObjects {
    static class Product {
        private String productImage, productName, productDescription;
        private int productCategory, stock, id, count;

        public Product(String productName, String productDescription, String productCategory, String productImage) {
            this.productName = productName;
            this.productDescription = productDescription;
            this.productImage = productImage;
            this.productCategory = Integer.parseInt(productCategory);
        }

        public Product(String productName, String productDescription, String productCategory, String productImage, int stock) {
            this.productName = productName;
            this.productDescription = productDescription;
            this.productImage = productImage;
            this.productCategory = Integer.parseInt(productCategory);
            setStock(stock);
        }

        public Product(String id, String productName, String productDescription, String productCategory, String productImage, int stock) {
           this.id = Integer.parseInt(id);
            this.productName = productName;
            this.productDescription = productDescription;
            this.productImage = productImage;
            this.productCategory = Integer.parseInt(productCategory);
            setStock(stock);
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            //todo if(count > stock)
            this.count = count;
        }

        public int getStock() {
            return stock;
        }

        public void setStock(int stock) {
            this.stock = stock;
        }

        public String getProductImage() {
            return productImage;
        }

        public String getProductDescription() {
            return productDescription;
        }

        public String getProductName() {
            return productName;
        }

        public int getProductCategory() {
            return productCategory;
        }

        public void setProductCategory(int productCategory) {
            this.productCategory = productCategory;
        }

        public void setProductDescription(String productDescription) {
            this.productDescription = productDescription;
        }

        public void setProductImage(String productImage) {
            this.productImage = productImage;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }
    }
}
