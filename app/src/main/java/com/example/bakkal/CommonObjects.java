package com.example.bakkal;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;

public class CommonObjects {
    public static class Product {
        private String productImage, productName, productDescription, packetWeight, productBrand;
        private int productCategory = 0, id = 0;
        private float productPrice = 0, stock = 0;
        public static View.OnClickListener listenerOrderItem;

        public Product(String productName, String productDescription, String productCategory, String productImage, String packetWeight, float productPrice, String productBrand) {
            this.productName = productName;
            this.productDescription = productDescription;
            this.productImage = productImage;
            this.productCategory = Integer.parseInt(productCategory);
            setProductPrice(productPrice);
            setProductBrand(productBrand);
            setPacketWeight(packetWeight);
        }

        public Product(String productName, String productDescription, String productCategory, String productImage, float stock, String packetWeight, float productPrice, String productBrand) {
            this.productName = productName;
            this.productDescription = productDescription;
            this.productImage = productImage;
            this.productCategory = Integer.parseInt(productCategory);
            setStock(stock);
            setPacketWeight(packetWeight);
            setProductPrice(productPrice);
            setProductBrand(productBrand);
        }

        public Product(String id, String productName, String productDescription, String productCategory, String productImage, float stock, String packetWeight, float productPrice, String productBrand) {
            this.id = Integer.parseInt(id);
            this.productName = productName;
            this.productDescription = productDescription;
            this.productImage = productImage;
            this.productCategory = Integer.parseInt(productCategory);
            setStock(stock);
            setProductPrice(productPrice);
            setPacketWeight(packetWeight);
            setProductBrand(productBrand);
        }

        public void setProductBrand(String productBrand) {
            this.productBrand = productBrand;
        }

        public String getProductBrand() {
            return productBrand;
        }

        public void setId(int id) {
            this.id = id;
        }

        public void setPacketWeight(String packetWeight) {
            this.packetWeight = packetWeight;
        }

        public void setProductPrice(float productPrice) {
            this.productPrice = productPrice;
        }

        public int getId() {
            return id;
        }

        public float getProductPrice() {
            return productPrice;
        }

        public String getPacketWeight() {
            return packetWeight;
        }


        public float getStock() {
            try {

            } catch (Exception e) {

            }

            return stock;
        }

        public void setStock(float stock) {
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

        public View getView(Context context) {
            try {
                View category = View.inflate(context, R.layout.product, null);
                ((TextView) category.findViewById(R.id.tvProductName)).setText(getProductBrand() + " - " + getProductName());
                ((TextView) category.findViewById(R.id.tvProductPrice)).setText(String.valueOf(getProductPrice()) + " ₺");
                ((TextView) category.findViewById(R.id.tvProductStockOut)).setVisibility(getStock() == 0 ? View.VISIBLE : View.INVISIBLE);
                ((Button) category.findViewById(R.id.btnProductView)).setTag(this);
                ((Button) category.findViewById(R.id.btnProductView)).setOnClickListener(MainMenu.listenerOrderItem);

                Functions.loadImage((ImageView) category.findViewById(R.id.ivProductImage), Functions.BASE_URL + getProductImage());

                return category;
            } catch (Exception e) {
                Functions.Track.error("CO-GV", e);
                return null;
            }
        }
    }

    public static class Category {
        private String categoryName, categoryImage, categoryColor;
        private int categoryId, categoryFather;

        public Category() {

        }

        public Category(int categoryId, int categoryFather, String categoryName, String categoryImage, String categoryColor) {
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
            return Functions.BASE_URL + categoryImage;
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

    public static class Order {
        // ORDER > CONFIRM > SHIP > COMP ?? CANCEL
        public static final int ORDERED = 0, CONFIRMED = 1, SHIPPED = 2, COMPLETED = 3, CANCELED = 4;

        private int orderNo, orderStatus;
        private ArrayList<CommonObjects.OrderItem> orderItems;
        private float orderTotal = 0;
        private String orderDate;
        private View view;
        private boolean isItemListOpen = false;
        private int userNo;
        LinearLayout.LayoutParams paramHide, paramShow;

        public Order(int orderNo, int orderStatus, float orderTotal, String orderDate, ArrayList<CommonObjects.OrderItem> orderItems, int userNo) {
            setOrderNo(orderNo);
            setOrderStatus(orderStatus);
            setOrderDate(orderDate);
            setOrderTotal(orderTotal);
            setOrderItems(orderItems);


            paramHide = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
            paramShow = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) MainMenu.context.getResources().getDimension(R.dimen.show_menu));

            setUserNo(userNo);
        }

        public Order(int orderNo, int orderStatus, float orderTotal, String orderDate, ArrayList<CommonObjects.OrderItem> orderItems) {
            setOrderNo(orderNo);
            setOrderStatus(orderStatus);
            setOrderDate(orderDate);
            setOrderTotal(orderTotal);
            setOrderItems(orderItems);


            paramHide = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
            paramShow = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) MainMenu.context.getResources().getDimension(R.dimen.show_menu));
        }

        public void showUserButton() {
            view.findViewById(R.id.btnShowOrderUserInfo).setVisibility(View.VISIBLE);
            view.findViewById(R.id.btnShowOrderUserInfo).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showUserInfo();
                }
            });
        }

        public void showUpdateButton() {
            view.findViewById(R.id.btnSetStatue).setVisibility(View.VISIBLE);
            view.findViewById(R.id.btnSetStatue).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setOrderStatus();
                }
            });
        }

        private void setOrderStatus() {
            Functions.WebResult result = API.Order.updateStatus(getOrderNo(), ControlPanel.spinnerSetStatue.getSelectedItemPosition());

            if (result.isConnected() && result.isSuccess()) {
                Functions.message(ControlPanel.context, "", result.getData(), false, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ControlPanel.ordersFromStatue(null);
                    }
                });

            } else {
                Functions.message(ControlPanel.context, "", result.getData(), true);
            }
        }

        private void showUserInfo() {
            try {

                Functions.WebResult result = API.User.get(getUserNo());

                if (result.isConnected() && result.isSuccess()) {
                    if (result.getRaw().getJSONArray(2).length() == 0) {
                        Functions.message(MainMenu.context, "", ControlPanel.context.getString(R.string.found_user), true);
                        return;
                    }

                    JSONObject usr = result.getRaw().getJSONArray(2).getJSONObject(0);

                    String message = ControlPanel.context.getString(R.string.user_info, usr.getString("user_name"), usr.getString("user_surname"), usr.getString("user_phone"), usr.getString("user_address"), usr.getString("user_email"));

                    Functions.message(ControlPanel.context, ControlPanel.context.getString(R.string.user_info_title), message, false);
                } else {
                    Functions.message(MainMenu.context, "", result.getData(), true);
                }
            } catch (Exception e) {
                Functions.Track.error("SUI", e);
            }
        }

        public int getUserNo() {
            return userNo;
        }

        public void setUserNo(int userNo) {
            this.userNo = userNo;
        }

        public void setOrderDate(String orderDate) {
            this.orderDate = orderDate;
        }

        public float getOrderTotal() {
            return orderTotal;
        }

        public void setOrderItems(ArrayList<CommonObjects.OrderItem> orderItems) {
            this.orderItems = orderItems;
        }

        public ArrayList<CommonObjects.OrderItem> getOrderItems() {
            return orderItems;
        }

        public void setOrderNo(int orderNo) {
            this.orderNo = orderNo;
        }

        public int getOrderNo() {
            return orderNo;
        }

        public void setOrderStatus(int orderStatus) {
            this.orderStatus = orderStatus;
        }

        public String getOrderStatus(int orderStatus) {
            int id = 0;

            switch (orderStatus) {
                case ORDERED:
                    id = R.string.status_ordered;
                    break;
                case CANCELED:
                    id = R.string.status_canceled;
                    break;
                case CONFIRMED:
                    id = R.string.status_confirmed;
                    break;
                case COMPLETED:
                    id = R.string.status_completed;
                    break;
                case SHIPPED:
                    id = R.string.status_shipped;
                    break;
            }

            if (id == 0) {
                return "";
            }

            return MainMenu.context.getString(id);
        }

        public int getOrderStatus() {
            return orderStatus;
        }

        public void setOrderTotal(float orderTotal) {
            this.orderTotal = orderTotal;
        }

        public String getOrderDate() {
            return orderDate;
        }

        public void hideList() {
            if (view != null) {
                ((LinearLayout) view.findViewById(R.id.llOrderItemList)).removeAllViews();
                view.findViewById(R.id.svOrderItem).setLayoutParams(paramHide);
                isItemListOpen = false;
            }
        }

        public boolean buildList(ArrayList<OrderItem> orderItems) {
            try {
                if (orderItems.size() == 0) {
                    hideList();
                    return true;
                }

                LinearLayout list = view.findViewById(R.id.llOrderItemList);
                list.removeAllViews();


                ((ScrollView) view.findViewById(R.id.svOrderItem)).setScrollX(0);

                for (int i = 0; i < orderItems.size(); i++) {
                    if (orderItems.get(i) == null || orderItems.get(i).getProduct() == null) {
                        continue;
                    }

                    View x = View.inflate(MainMenu.context, R.layout.order_item, null);

                    ((TextView) x.findViewById(R.id.orderItemBrandNName)).setText(orderItems.get(i).getProduct().getProductBrand() + " - " + orderItems.get(i).getProduct().getProductName());
                    ((TextView) x.findViewById(R.id.orderItemCount)).setText(orderItems.get(i).getItemCount() + "");
                    ((TextView) x.findViewById(R.id.orderItemPrice)).setText(orderItems.get(i).getItemPrice() + "");
                    ((TextView) x.findViewById(R.id.orderItemTotal)).setText(Functions.two(orderItems.get(i).getItemPrice() * orderItems.get(i).getItemCount()) + "");

                    list.addView(x);
                }
                MainMenu.llShoppingCartList.setVisibility(View.INVISIBLE);
                MainMenu.llShoppingCart.setVisibility(View.INVISIBLE);
                MainMenu.llProductInfo.setVisibility(View.INVISIBLE);

                view.findViewById(R.id.svOrderItem).setLayoutParams(paramShow);
                isItemListOpen = true;
                MainMenu.llOrder.setVisibility(View.VISIBLE);

                return true;
            } catch (Exception e) {
                Functions.Track.error("GO-BL", e);
                return false;
            }
        }

        public View getView() {
            try {
                if (view != null) {
                    return this.view;
                }

                view = View.inflate(MainMenu.context, R.layout.order, null);

                ((TextView) view.findViewById(R.id.orderOrderNumber)).setText("#" + getOrderNo());
                ((TextView) view.findViewById(R.id.orderOrderStatus)).setText(getOrderStatus(getOrderStatus()));
                ((TextView) view.findViewById(R.id.orderOrderAmound)).setText(getOrderTotal() + " ₺");

                ImageView btn = ((ImageView) view.findViewById(R.id.btnShowOrderItem));

                btn.setTag(this);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            if (isItemListOpen) {
                                hideList();
                                ((ImageView) view.findViewById(R.id.btnShowOrderItem)).setImageDrawable(MainMenu.context.getDrawable(android.R.drawable.arrow_down_float));
                            } else {
                                buildList(((Order) v.getTag()).getOrderItems());
                                ((ImageView) view.findViewById(R.id.btnShowOrderItem)).setImageDrawable(MainMenu.context.getDrawable(android.R.drawable.arrow_up_float));
                            }
                        } catch (Exception e) {
                            Functions.Track.error("GO-GV-CL", e);
                        }
                    }
                });


                return view;
            } catch (Exception e) {
                Functions.Track.error("GO-GV", e);
                return null;
            }
        }
    }

    public static class OrderItem {
        private int itemNo, orderNo, index = 0;
        private float itemCount = 0;
        private float itemTotal = 0;
        private CommonObjects.Product product;
        private View view;
        private float itemPrice = 0;

        /*
        public OrderItem(int itemNo, int orderNo, float itemCount, float itemTotal) {
            setItemNo(itemNo);
            setOrderNo(orderNo);
            setItemCount(itemCount);
            setItemTotal(itemTotal);
        }*/


        public OrderItem(int itemNo, int orderNo, float itemCount, float itemTotal, CommonObjects.Product product) {
            setItemNo(itemNo);
            setOrderNo(orderNo);
            setItemCount(itemCount);
            setItemTotal(itemTotal);
            setProduct(product);
        }


        public OrderItem(int itemNo, int orderNo, float itemCount, float itemTotal, CommonObjects.Product product, float price) {
            setItemNo(itemNo);
            setOrderNo(orderNo);
            setItemCount(itemCount);
            setItemTotal(itemTotal);
            setProduct(product);
            setItemPrice(price);
        }

        public void setItemPrice(float itemPrice) {
            this.itemPrice = itemPrice;
        }

        public float getItemPrice() {
            return itemPrice;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public void setProduct(Product product) {
            this.product = product;
        }

        public Product getProduct() {
            return product;
        }

        public int getOrderNo() {
            return orderNo;
        }

        public void setOrderNo(int orderNo) {
            this.orderNo = orderNo;
        }

        public float getItemCount() {
            return itemCount;
        }

        public void setItemCount(float itemCount) {
            if (getProduct() != null && itemCount > getProduct().getStock()) {
                Log.e("asdas", itemCount + "---" + getProduct().getStock() + "---" + getItemCount());
                Functions.message(MainMenu.context, "", MainMenu.context.getString(R.string.more_than_stock), true);
                return;
            }

            this.itemCount = Functions.two(itemCount);

            if (view != null) {
                ((EditText) view.findViewById(R.id.etCartItemCount)).setText(String.valueOf((int) this.itemCount));
                ((TextView) view.findViewById(R.id.tvCartPriceNTotal)).setText(getItemTotal() + " ₺\n(" + getProduct().getProductPrice() + " ₺)");
            }
        }

        public void setItemNo(int itemNo) {
            this.itemNo = itemNo;
        }

        public int getItemNo() {
            return itemNo;
        }

        public void setItemTotal(float itemTotal) {
            this.itemTotal = Functions.two(itemTotal);
        }

        public float getItemTotal() {
            return itemTotal;
        }

        public View getView() {
            try {
                view = View.inflate(Functions.CONTEXT, R.layout.shopping_cart_item, null);

                Product x = getProduct();

                Functions.loadImage((ImageView) view.findViewById(R.id.ivCartItemImage), Functions.BASE_URL + x.getProductImage());

                ((TextView) view.findViewById(R.id.tvCartItemBrandNName)).setText(x.getProductBrand() + " - " + x.getProductName() + " (" + x.getPacketWeight() + ")");

                ((TextView) view.findViewById(R.id.tvCartPriceNTotal)).setText(getItemTotal() + " ₺\n(" + getProduct().getProductPrice() + " ₺)");

                ((EditText) view.findViewById(R.id.etCartItemCount)).setText(String.valueOf((int) getItemCount()));

                ((Button) view.findViewById(R.id.btnCartItemUpdate)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            EditText et = view.findViewById(R.id.etCartItemCount);

                            if (et.getText() == null) {
                                Functions.message(MainMenu.context, "", MainMenu.context.getString(R.string.allow_stock), true);
                                return;
                            } else {
                                float count = Float.parseFloat(et.getText().toString());

                                if (count <= 0) {
                                    Functions.message(MainMenu.context, "", MainMenu.context.getString(R.string.allow_stock), true);
                                    return;
                                }

                                if (count > getProduct().getStock()) {
                                    Functions.message(MainMenu.context, "", Functions.CONTEXT.getString(R.string.more_than_stock), true);
                                    return;
                                }

                                setItemCount(count);
                                setItemTotal(count * getProduct().getProductPrice());

                                ((TextView) view.findViewById(R.id.tvCartPriceNTotal)).setText(getItemTotal() + " ₺\n(" + getProduct().getProductPrice() + " ₺)");

                                MainMenu.updateCartInfo();
                            }
                        } catch (Exception e) {
                            Functions.Track.error("CI-GV-UC", e);
                        }
                    }
                });

                ((Button) view.findViewById(R.id.btnCartItemAction)).setText(R.string.delete);
                ((Button) view.findViewById(R.id.btnCartItemAction)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            for (int i = 0; i < MainMenu.shoppingCart.size(); i++) {
                                if (MainMenu.shoppingCart.get(i).getItemNo() == getItemNo()) {
                                    MainMenu.shoppingCart.remove(i);
                                    break;
                                }
                            }

                            MainMenu.updateCartInfo();
                        } catch (Exception e) {
                            Functions.Track.error("CI-GV-AB", e);
                        }
                    }
                });


                return view;
            } catch (Exception e) {
                Functions.Track.error("SCI-GV", e);
                return null;
            }
        }
    }
}
