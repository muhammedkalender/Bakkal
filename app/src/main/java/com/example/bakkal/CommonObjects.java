package com.example.bakkal;

public class CommonObjects {
    static class Product {
        private String productImage, productName, productDescription;
        private int productCategory;

        public Product(String productName, String productDescription, String productCategory, String productImage) {
            this.productName = productName;
            this.productDescription = productDescription;
            this.productImage = productImage;
            this.productCategory = Integer.parseInt(productCategory);
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
