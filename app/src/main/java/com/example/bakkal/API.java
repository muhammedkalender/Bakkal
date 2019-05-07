package com.example.bakkal;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.ArrayMap;
import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class API {
    public static Functions.WebResult Image(String path) {
        try {
            Bitmap bm = BitmapFactory.decodeFile(path);
            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 90, bao);
            byte[] ba = bao.toByteArray();
            String base64 = Base64.encodeToString(ba, 0);

            HashMap<String, String> params = new HashMap<>();
            params.put("req", "upload_image");
            params.put("cat", "all");

            params.put("image", base64);

            return Functions.getData(params);
        } catch (Exception e) {
            Functions.Track.error("UP-IM", e);
            return new Functions.WebResult(false, false, "Sistemler Hata Oluştu");
        }
    }

    static class Product {
        public static Functions.WebResult insert(String productBrand, String productName, String productDescription, int productCategory, String productImage, String productWeight, float productPrice) {

            //todo image for upload olacka string değil

            productName = Functions.clearAndEncodeData(productName);
            productDescription = Functions.encodeData(productDescription);

            HashMap<String, String> params = new HashMap<>();
            params.put("req", "insert");
            params.put("cat", "product");

            params.put("product_brand", productBrand);
            params.put("product_name", productName);
            params.put("product_desc", productDescription);
            params.put("product_category", String.valueOf(productCategory));
            params.put("product_image", productImage);
            params.put("product_weight", productWeight);
            params.put("product_price", String.valueOf(productPrice));

            return Functions.getData(params);
        }

        public static CommonObjects.Product[] get(int category, int page, int count) {
            return get("", category, page, count);
        }

        //kategoriledekiler ve babsı varsa ondakiler ? daha doğrusu tersi //todo
        public static CommonObjects.Product[] get(String search, int category, int page, int count) {
            //todo result dizimi teklimi ? bak bi ona göre
            Log.e("a1sdas", "aaaa1");

            HashMap<String, String> params = new HashMap<>();
            params.put("req", "select");
            params.put("cat", "product");
            params.put("page", String.valueOf(page));
            params.put("count", String.valueOf(count));
            params.put("category", search.equals("") ? String.valueOf(category) : "0");
            params.put("search", search);
            params.put("order", "");

            Functions.WebResult result = Functions.getData(params);

            if (result.isConnected() && result.isSuccess()) {
                try {
                    JSONArray arr = result.getRaw().getJSONArray(1);

                    CommonObjects.Product[] objs = new CommonObjects.Product[arr.length()];

                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject obj = arr.getJSONObject(i);

                        objs[i] = new CommonObjects.Product(
                                obj.getString("product_id"),
                                obj.getString("product_name"),
                                obj.getString("product_desc"),
                                obj.getString("product_category"),
                                obj.getString("product_image"),
                                (float) obj.getDouble("product_stock"),
                                obj.getString("product_weight"),
                                (float) obj.getDouble("product_price"),
                                obj.getString("product_brand")
                        );
                    }

                    return objs;
                } catch (Exception e) {
                    Functions.Track.error("API-GP-FOR", e);
                    return null;
                }
            } else {
                return new CommonObjects.Product[0];
            }
        }

        public static CommonObjects.Product get(int productId) throws JSONException {
            try {
                HashMap<String, String> params = new HashMap<>();
                params.put("req", "get");
                params.put("cat", "product");
                params.put("page", "0");
                params.put("count", "0");

                params.put("product_id", String.valueOf(productId));

                Functions.WebResult result = Functions.getData(params);

                if (result.isConnected() && result.isSuccess()) {
                    JSONArray a = result.getRaw().getJSONArray(2);
                    JSONObject x = a.getJSONObject(0);

                    return new CommonObjects.Product(
                            x.getString("product_id"),
                            x.getString("product_name"),
                            x.getString("product_desc"),
                            x.getString("product_category"),
                            x.getString("product_image"),
                            (float) x.getDouble("product_stock"),
                            x.getString("product_weight"),
                            (float) x.getDouble("product_price"),
                            x.getString("product_brand")
                    );
                } else {
                    return null;
                }
            } catch (Exception e) {
                Functions.Track.error("G", e);
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

        public static Functions.WebResult delete(int productId) {
            HashMap<String, String> params = new HashMap<>();
            params.put("req", "delete");
            params.put("cat", "product");

            params.put("product_id", String.valueOf(productId));

            return Functions.getData(params);
        }

        public static Functions.WebResult uploadImage(int productId, String path) {
            Bitmap bm = BitmapFactory.decodeFile(path);
            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 90, bao);
            byte[] ba = bao.toByteArray();
            String base64 = Base64.encodeToString(ba, 0);

            HashMap<String, String> params = new HashMap<>();
            params.put("req", "upload_image");
            params.put("cat", "product");

            params.put("product_id", String.valueOf(productId));
            params.put("product_image", base64);

            return Functions.getData(params);
        }

        public static Functions.WebResult delStock(int id, float two) {
            HashMap<String, String> params = new HashMap<>();
            params.put("req", "del_stock");
            params.put("cat", "product");

            params.put("product_id", String.valueOf(id));
            params.put("product_stock", String.valueOf(two));

            return Functions.getData(params);
        }

        public static Functions.WebResult addStock(int id, float two) {
            HashMap<String, String> params = new HashMap<>();
            params.put("req", "add_stock");
            params.put("cat", "product");

            params.put("product_id", String.valueOf(id));
            params.put("product_stock", String.valueOf(two));

            return Functions.getData(params);
        }

        public static Functions.WebResult update(int productId, String productBrand, String productName, String productDescription, int productCategory, String productImage, String productWeight, float productPrice) {
            HashMap<String, String> params = new HashMap<>();
            params.put("req", "update");
            params.put("cat", "product");

            params.put("product_id", String.valueOf(productId));
            params.put("product_brand", productBrand);
            params.put("product_name", productName);
            params.put("product_desc", productDescription);
            params.put("product_category", String.valueOf(productCategory));
            params.put("product_image", productImage);
            params.put("product_weight", productWeight);
            params.put("product_price", String.valueOf(productPrice));

            return Functions.getData(params);
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
            params.put("req", "select");
            params.put("cat", "category");
            params.put("page", "0");
            params.put("count", "0");
            params.put("order", "category_name asc");

            Functions.WebResult result = Functions.getData(params);

            if (result.isConnected() && result.isSuccess()) {
                try {
                    JSONArray arr = result.getRaw().getJSONArray(2);

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
                    Functions.Track.error("API-CAT-FOR", e);
                    return false;
                }
            } else {
                return false;
            }
        }


        public static CommonObjects.Category[] gets(boolean isReset) {
            //TODO LOAD Again pu, felan varsa ?
            //todo api call
            HashMap<String, String> params = new HashMap<>();
            params.put("req", "select");
            params.put("cat", "category");
            params.put("page", "0");
            params.put("count", "0");
            params.put("order", "category_name asc");

            Functions.WebResult result = Functions.getData(params);

            if (result.isConnected() && result.isSuccess()) {
                try {
                    JSONArray arr = result.getRaw().getJSONArray(2);

                    CommonObjects.Category[] arrCategorises = new CommonObjects.Category[arr.length()];

                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject obj = arr.getJSONObject(i);

                        arrCategorises[i] = new CommonObjects.Category(
                                obj.getInt("category_id"),
                                obj.getInt("category_father"),
                                obj.getString("category_name"),
                                obj.getString("category_image"),
                                obj.getString("category_color")
                        );
                    }

                    return arrCategorises;
                } catch (Exception e) {
                    Functions.Track.error("API-CAT-FOR", e);
                    return new CommonObjects.Category[0];
                }
            } else {
                return new CommonObjects.Category[0];
            }
        }

        public static ArrayList<CommonObjects.Category> gets() {
            return gets(0);
        }

        public static ArrayList<CommonObjects.Category> gets(int fatherId) {
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

            return cats;
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

        public static Functions.WebResult insert(int categoryFather, String categoryName, String categoryImage, String categoryColor) {

            //todo image for upload olacka string değil
//todo değişen varmı bak bi yoksa devam et


            categoryName = Functions.clearAndEncodeData(categoryName);
            categoryColor = Functions.clearAndEncodeData(categoryColor);

            String imageURL = API.uploadImage(categoryImage);

            HashMap<String, String> params = new HashMap<>();
            params.put("req", "insert");
            params.put("cat", "category");

            Log.e("catt", categoryName + "1");

            params.put("category_name", categoryName);
            params.put("category_father", String.valueOf(categoryFather));
            params.put("category_color", categoryColor);
            params.put("category_image", imageURL);

            return Functions.getData(params);
        }


        public static Functions.WebResult delete(int categoryId) {
            HashMap<String, String> params = new HashMap<>();
            params.put("req", "delete");
            params.put("cat", "category");

            params.put("category_id", String.valueOf(categoryId));

            return Functions.getData(params);
        }

        public static Functions.WebResult update(int categoryId, String name) {
            name = Functions.clearAndEncodeData(name);

            HashMap<String, String> params = new HashMap<>();
            params.put("req", "update");
            params.put("cat", "category");

            params.put("category_name", name);
            params.put("category_id", String.valueOf(categoryId));

            return Functions.getData(params);
        }
    }

    static class Order {

        public static Functions.WebResult post(int itemCount, float totalAmount, String JSON) {
            HashMap<String, String> params = new HashMap<>();
            params.put("req", "insert");
            params.put("cat", "order");

            params.put("item_count", String.valueOf(itemCount));
            params.put("total_amount", String.valueOf(totalAmount));
            params.put("item_json", JSON);

            return Functions.getData(params);
        }

        public static Functions.WebResult post(String orderDate, CommonObjects.OrderItem[] orderItems) {
            JSONArray items = new JSONArray();

            for (CommonObjects.OrderItem item : orderItems) {
                try {
                    JSONObject obj = new JSONObject();

                    obj.put("order_item", item.getItemNo());
                    obj.put("order_count", item.getItemCount());
                    obj.put("order_price", item.getItemTotal());

                    items.put(obj);
                } catch (Exception e) {
//todo
                }
            }

            HashMap<String, String> params = new HashMap<>();
            params.put("method", "post");
            params.put("cat", "order");

            params.put("order_date", orderDate);
            params.put("order_item", items.toString());

            return Functions.getData(params);
        }


        public static final int ORDERED = 0; //SIPARIS VERILDI
        public static final int CONFIRM = 0; //ONAYLANDI
        public static final int READY = 0; //HAZIRLANDI
        public static final int DELIVERY = 0; //YOLA ÇIKTI
        public static final int SUCCESS = 0; //TAMAMLANDI
        public static final int CANCELLED = 0; //IPTAL EDILDI


        public static Functions.WebResult updateStatus(int orderId, int newStatus) {
            HashMap<String, String> params = new HashMap<>();
            params.put("method", "put");
            params.put("cat", "order_status");

            params.put("order_id", String.valueOf(orderId));
            params.put("order_status", String.valueOf(newStatus));

            return Functions.getData(params);
        }

        public static Functions.WebResult deleteOrder(int orderId) {
            HashMap<String, String> params = new HashMap<>();
            params.put("method", "delete");
            params.put("cat", "order");

            params.put("order_id", String.valueOf(orderId));

            return Functions.getData(params);
        }

        public static CommonObjects.Order get(int orderId) {
            try {
                HashMap<String, String> params = new HashMap<>();
                params.put("method", "get");
                params.put("cat", "order");

                params.put("order_id", String.valueOf(orderId));

                Functions.WebResult result = Functions.getData(params);

                if (result.isConnected() && result.isSuccess()) {
                    ArrayList<CommonObjects.OrderItem> orderItems = new ArrayList<>();

                    JSONArray array = new JSONArray(result.getData());
                    array = array.getJSONArray(5); //todo

                    for (int i = 0; i < array.length(); i++) {
                        JSONObject obj = array.getJSONObject(i);

                        CommonObjects.OrderItem item = new CommonObjects.OrderItem(obj.getInt("item_id"), obj.getInt("order_id"), (float) obj.getDouble("item_count"), (float) obj.getDouble("item_price"), API.Product.get(obj.getInt("item_id")));


                        orderItems.add(item);
                    }

                    return new CommonObjects.Order(
                            Integer.parseInt(result.getJSONData("order_id")),
                            Integer.parseInt(result.getJSONData("order_status")),
                            Float.parseFloat(result.getJSONData("order_total")),
                            result.getJSONData("order_date"), orderItems);
                } else {
                    return null;
                }
            } catch (Exception e) {
                return null;
                //todo
            }
        }

        public static ArrayList<CommonObjects.Order> select(int status, int page, int count) {
            try {
                HashMap<String, String> params = new HashMap<>();
                params.put("req", "select");
                params.put("cat", "order");

                params.put("count", String.valueOf(count));
                params.put("page", String.valueOf(page));
                params.put("status", String.valueOf(status));

                Functions.WebResult result = Functions.getData(params);

                ArrayList<CommonObjects.Order> items = new ArrayList<>();

                if (result.isConnected() && result.isSuccess()) {
                    JSONArray res = result.getRaw().getJSONArray(2);

                    int lastOrder = 0;

                    for (int i = 0; i < res.length(); i++) {
                        JSONObject obj = res.getJSONObject(i);

                        if (lastOrder != obj.getInt("order_id")) {
                            items.add(new CommonObjects.Order(
                                    obj.getInt("order_id"),
                                    obj.getInt("order_status"),
                                    (float) obj.getDouble("order_total"),
                                    obj.getString("order_date"),
                                    new ArrayList<CommonObjects.OrderItem>()
                            ));

                            lastOrder = obj.getInt("order_id");
                        }

                        if (!obj.isNull("item_id")) {
                            ArrayList<CommonObjects.OrderItem> arr = items.get(items.size() - 1).getOrderItems();

                            arr.add(
                                    new CommonObjects.OrderItem(
                                            obj.getInt("item"),
                                            obj.getInt("order_id"),
                                            (float) obj.getDouble("item_count"),
                                            (float) obj.getDouble("order_total"),
                                            API.Product.get(obj.getInt("item")),
                                            (float) obj.getDouble("item_price"))
                            );

                            items.get(items.size() - 1).setOrderItems(
                                    arr
                            );
                        }
                    }

                    return items;
                } else {
                    //todo
                    Log.e("asdasd", result.getRaw().toString());
                    return null;
                }
            } catch (Exception e) {
                Functions.Track.error("SO", e);
                return null;
            }
        }
    }

    private static String uploadImage(String productImage) {
        return "";
        //TODO
    }
}
