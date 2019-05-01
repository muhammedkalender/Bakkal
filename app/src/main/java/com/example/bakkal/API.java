package com.example.bakkal;

import android.util.ArrayMap;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class API {
    static class Product {
        public static boolean post(String productName, String productDescription, int productCategory, String productImage) {

            //todo image for upload olacka string değil

            productName = Functions.clearAndEncodeData(productName);
            productDescription = Functions.encodeData(productDescription);

            String imageURL = API.uploadImage(productImage);

            HashMap<String, String> params = new HashMap<>();
            params.put("method", "post");
            params.put("cat", "product");

            params.put("product_name", productName);
            params.put("product_description", productDescription);
            params.put("product_category", String.valueOf(productCategory));
            params.put("product_image", imageURL);

            Functions.WebResult result = Functions.getData(params);

            if (result.isConnected() && result.isSuccess()) {
                return true;
            } else {
                return false;
            }
        }

        //kategoriledekiler ve babsı varsa ondakiler ? daha doğrusu tersi //todo
        public static CommonObjects.Product[] get(int category, int page, int count) {
            //todo result dizimi teklimi ? bak bi ona göre

            HashMap<String, String> params = new HashMap<>();
            params.put("method", "get");
            params.put("cat", "product");
            params.put("page", String.valueOf(page));
            params.put("count", String.valueOf(count));

            params.put("product_category", String.valueOf(category));

            Functions.WebResult result = Functions.getData(params);

            if (result.isConnected() && result.isSuccess()) {
                try {
                    JSONArray arr = new JSONArray(result.getData());

                    CommonObjects.Product[] objs = new CommonObjects.Product[arr.length()];

                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject obj = arr.getJSONObject(i);

                        objs[i] = new CommonObjects.Product(
                                result.getJSONData("product_id"),
                                obj.getString("product_name"),
                                result.getJSONData("product_description"),
                                result.getJSONData("product_category"),
                                result.getJSONData("product_image"),
                                Integer.parseInt(result.getJSONData("product_stock"))

                        );
                    }

                    return objs;
                } catch (Exception e) {
                    return null;
                }
            } else {
                return null;
            }
        }

        public static CommonObjects.Product get(int productId) {
            HashMap<String, String> params = new HashMap<>();
            params.put("method", "get");
            params.put("cat", "product");
            params.put("page", "0");
            params.put("count", "0");

            params.put("product_id", String.valueOf(productId));

            Functions.WebResult result = Functions.getData(params);

            if (result.isConnected() && result.isSuccess()) {
                return new CommonObjects.Product(
                        result.getJSONData("product_id"),
                        result.getJSONData("product_name"),
                        result.getJSONData("product_description"),
                        result.getJSONData("product_category"),
                        result.getJSONData("product_image"),
                        Integer.parseInt(result.getJSONData("product_stock"))
                );
            } else {
                return null;
            }
        }

        public static boolean put(int productId, String productName, String productDescription, int productCategory, String productImage) {

            //todo image for upload olacka string değil
//todo değişen varmı bak bi yoksa devam et


            productName = Functions.clearAndEncodeData(productName);
            productDescription = Functions.encodeData(productDescription);

            String imageURL = API.uploadImage(productImage);

            HashMap<String, String> params = new HashMap<>();
            params.put("method", "put");
            params.put("cat", "product");

            params.put("product_id", String.valueOf(productId));
            params.put("product_name", productName);
            params.put("product_description", productDescription);
            params.put("product_category", String.valueOf(productCategory));
            params.put("product_image", imageURL);

            Functions.WebResult result = Functions.getData(params);

            if (result.isConnected() && result.isSuccess()) {
                return true;
            } else {
                return false;
            }
        }

        public static boolean delete(int productId) {
            HashMap<String, String> params = new HashMap<>();
            params.put("method", "delete");
            params.put("cat", "product");

            params.put("product_id", String.valueOf(productId));

            Functions.WebResult result = Functions.getData(params);

            if (result.isConnected() && result.isSuccess()) {
                return true;
            } else {
                return true;
            }
        }
    }

    static class Category {
        private static CommonObjects.Category[] categorises;

        //Father Id 0 ise, Ana Kategori, değil ise alt kategori
        //Açılışta belleğe alınacak, sonradan kullanılacak
        public static boolean load() {
            //TODO LOAD Again pu, felan varsa ?
            //todo api call
            HashMap<String, String> params = new HashMap<>();
            params.put("method", "get");
            params.put("cat", "category");

            Functions.WebResult result = Functions.getData(params);

            if (result.isConnected() && result.isSuccess()) {
                try {
                    JSONArray arr = new JSONArray(result.getData());

                    categorises = new CommonObjects.Category[arr.length()];

                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject obj = arr.getJSONObject(i);

                        categorises[i] = new CommonObjects.Category(
                                obj.getInt("category_id"),
                                obj.getInt("category_father"),
                                obj.getString("category_name"),
                                obj.getString("category_image"),
                                obj.getString("category_color")
                        );
                    }

                    return true;
                } catch (Exception e) {
                    return false;
                }
            } else {
                return false;
            }
        }

        public static CommonObjects.Category[] gets() {
            return gets(0);
        }

        public static CommonObjects.Category[] gets(int fatherId) {
            if (categorises == null && load() == false) {
                //todo
                return null;
            }

            ArrayList<CommonObjects.Category> cats = new ArrayList<>();

            for (CommonObjects.Category cat : categorises) {
                if (cat.getCategoryFather() == fatherId) {
                    cats.add(cat);
                }
            }

            return (CommonObjects.Category[]) cats.toArray();
        }

        public static CommonObjects.Category get(int categoryId) {
            for (int i = 0; i < categorises.length; i++) {
                if (categorises[i].getCategoryId() == categoryId) {
                    return categorises[i];
                }
            }

            return null;
        }

        public static boolean put(int categoryId, int categoryFather, String categoryName, String categoryImage, String categoryColor) {

            //todo image for upload olacka string değil
//todo değişen varmı bak bi yoksa devam et


            categoryName = Functions.clearAndEncodeData(categoryName);
            categoryColor = Functions.clearAndEncodeData(categoryColor);

            String imageURL = API.uploadImage(categoryImage);

            HashMap<String, String> params = new HashMap<>();
            params.put("method", "put");
            params.put("cat", "category");

            params.put("category_id", String.valueOf(categoryId));
            params.put("category_name", categoryName);
            params.put("category_father", String.valueOf(categoryFather));
            params.put("category_color", categoryColor);
            params.put("category_image", imageURL);

            Functions.WebResult result = Functions.getData(params);

            if (result.isConnected() && result.isSuccess()) {
                API.Category.load();
                return true;
            } else {
                return false;
            }
        }

        public static boolean put(int categoryFather, String categoryName, String categoryImage, String categoryColor) {

            //todo image for upload olacka string değil
//todo değişen varmı bak bi yoksa devam et


            categoryName = Functions.clearAndEncodeData(categoryName);
            categoryColor = Functions.clearAndEncodeData(categoryColor);

            String imageURL = API.uploadImage(categoryImage);

            HashMap<String, String> params = new HashMap<>();
            params.put("method", "put");
            params.put("cat", "category");

            params.put("category_name", categoryName);
            params.put("category_father", String.valueOf(categoryFather));
            params.put("category_color", categoryColor);
            params.put("category_image", imageURL);

            Functions.WebResult result = Functions.getData(params);

            if (result.isConnected() && result.isSuccess()) {
                API.Category.load();
                return true;
            } else {
                return false;
            }
        }


        public static boolean delete(int categoryId) {
            HashMap<String, String> params = new HashMap<>();
            params.put("method", "delete");
            params.put("cat", "category");

            params.put("category_id", String.valueOf(categoryId));

            Functions.WebResult result = Functions.getData(params);

            if (result.isConnected() && result.isSuccess()) {
                API.Category.load();
                return true;
            } else {
                return true;
            }
        }
    }

    private static String uploadImage(String productImage) {
        return "";
        //TODO
    }
}
