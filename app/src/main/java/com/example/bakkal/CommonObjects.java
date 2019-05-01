package com.example.bakkal;

public class CommonObjects {
    public static class Product {
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

    public static class Category {
        private String categoryName, categoryImage, categoryColor;
        private int categoryId, categoryFather;

        public Category(){

        }

        public Category(int categoryId, int categoryFather, String categoryName, String categoryImage, String categoryColor){
            setCategoryId(categoryId);
            setCategoryFather(categoryFather);
            setCategoryName(categoryName);
            setCategoryImage(categoryImage);
            setCategoryColor(categoryColor);
        }

        public int getCategoryFather() {
            return categoryFather;
        }

        public int getCategoryId() {
            return categoryId;
        }

        public String getCategoryColor() {
            return categoryColor;
        }

        public String getCategoryImage() {
            return categoryImage;
        }

        public String getCategoryName() {
            return categoryName;
        }

        public void setCategoryColor(String categoryColor) {
            this.categoryColor = categoryColor;
        }

        public void setCategoryFather(int categoryFather) {
            this.categoryFather = categoryFather;
        }

        public void setCategoryId(int categoryId) {
            this.categoryId = categoryId;
        }

        public void setCategoryImage(String categoryImage) {
            this.categoryImage = categoryImage;
        }

        public void setCategoryName(String categoryName) {
            this.categoryName = categoryName;
        }
    }
}
