package com.example.bakkal;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.SubMenu;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainMenu extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public static User user;

    public static Context context;

    public static NavigationView navigationView;

    public static ArrayList<CommonObjects.Category> categories;
    public static View.OnClickListener listenerOrderItem;

    private int countProductPage = 9, currentProductPage = 0, currentProductCategory = 0;

    private static LinearLayout llProduct;
    private ScrollView svProduct;
    private GridLayout glProduct;
    private String currentProductSearch = "";
    private boolean onSearch = false;

    private static Button btnCartConfirm;

    public static LinearLayout llProductInfo;
    private ScrollView svShoppingCart;
    public static LinearLayout llShoppingCart;
    public static LinearLayout llShoppingCartList;
    public static LinearLayout llOrder;

    public static ScrollView svOrder;
    public static LinearLayout llOrderList;


    public static LinearLayout llUserInfo;

    private EditText etSearch;

    public static ArrayList<CommonObjects.OrderItem> shoppingCart = new ArrayList<>();

    public static void loadUser() {
        user = new User();
        user.load();

        if (user.isLogged()) {
            navigationView.getMenu().setGroupVisible(R.id.nav_menu_visitor, false);
            navigationView.getMenu().setGroupVisible(R.id.nav_menu_user, true);
            navigationView.getMenu().findItem(R.id.nav_admin).setVisible(user.isAdmin());
            ((TextView) navigationView.getHeaderView(0).findViewById(R.id.nav_username)).setText(user.getName() + " " + user.getSurname());
            ((TextView) navigationView.getHeaderView(0).findViewById(R.id.nav_email)).setText(user.getEmail());
        } else {
            navigationView.getMenu().setGroupVisible(R.id.nav_menu_visitor, true);
            navigationView.getMenu().setGroupVisible(R.id.nav_menu_user, false);
            ((TextView) navigationView.getHeaderView(0).findViewById(R.id.nav_username)).setText(R.string.visitor);
            ((TextView) navigationView.getHeaderView(0).findViewById(R.id.nav_email)).setText("");
        }
    }

    public void viewProduct(CommonObjects.Product obj) {
        try {
            Functions.loadImage((ImageView) findViewById(R.id.ivProductInfoImage), Functions.BASE_URL + obj.getProductImage());

            ((EditText) findViewById(R.id.etProductInfoBrand)).setText(obj.getProductBrand());
            ((EditText) findViewById(R.id.etProductInfoDesc)).setText(obj.getProductDescription());
            ((EditText) findViewById(R.id.etProductInfoStock)).setText(String.valueOf(obj.getStock()));
            ((EditText) findViewById(R.id.etProductInfoName)).setText(obj.getProductName());
            ((EditText) findViewById(R.id.etProductInfoWeight)).setText(obj.getPacketWeight());
            ((EditText) findViewById(R.id.etProductInfoPrice)).setText(String.valueOf(obj.getProductPrice()));
            ((EditText) findViewById(R.id.etProductInfoCategory)).setText(findCategory(obj.getProductCategory()));
            ((Button) findViewById(R.id.btnProductInfoConfirm)).setTag(obj);
            ((EditText) findViewById(R.id.etProductInfoId)).setText(String.valueOf(obj.getId()));
            ((Button) findViewById(R.id.btnProductInfoConfirm)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        EditText etStock = findViewById(R.id.pre_product_info_stock);

                        if (etStock.getText() == null) {
                            Functions.message(MainMenu.context, "", getString(R.string.allow_stock), true);

                            return;
                        }

                        float orderCount = Float.parseFloat(etStock.getText().toString());

                        if (orderCount <= 0) {
                            Functions.message(MainMenu.context, "", getString(R.string.allow_stock), true);
                            return;
                        }

                        CommonObjects.Product cobj = (CommonObjects.Product) v.getTag();

                        if (cobj != null && orderCount > cobj.getStock()) {
                            Functions.message(MainMenu.this, "", Functions.CONTEXT.getString(R.string.more_than_stock), true);
                            return;
                        }

                        int index = -1;

                        for (int i = 0; i < shoppingCart.size(); i++) {
                            if (shoppingCart.get(i).getProduct().getId() == cobj.getId()) {
                                index = i;
                            }
                        }

                        if (index != -1) {
                            shoppingCart.get(index).setItemCount(shoppingCart.get(index).getItemCount() + orderCount);
                            shoppingCart.get(index).setItemTotal(shoppingCart.get(index).getItemCount() * shoppingCart.get(index).getProduct().getProductPrice());
                        } else {
                            if (cobj == null) {
                            }

                            CommonObjects.OrderItem x = new CommonObjects.OrderItem(cobj.getId(), 0, orderCount, orderCount * cobj.getProductPrice(), cobj);
                            shoppingCart.add(x);
                        }
                        updateCartInfo();
                    } catch (Exception e) {
                        Functions.Track.error("AC-ER", e);
                    }
                }
            });

            llUserInfo.setVisibility(View.INVISIBLE);
            llOrder.setVisibility(View.INVISIBLE);
            llShoppingCart.setVisibility(View.INVISIBLE);
            llProductInfo.setVisibility(View.VISIBLE);
            llProduct.setVisibility(View.INVISIBLE);
        } catch (Exception e) {
            Functions.Track.error("VP-MP", e);
        }
    }

    private String findCategory(int productCategory) {
        for (int i = 0; i < categories.size(); i++) {
            if (categories.get(i).getCategoryId() == productCategory) {
                return categories.get(i).getCategoryName();
            }
        }

        return "";
    }

    public static void updateCartInfo() {
        //todo

        if (shoppingCart.size() == 0) {
            llShoppingCartList.removeAllViews();
        }

        if (shoppingCart.size() > 0) {
            navigationView.getMenu().findItem(R.id.nav_cart).setTitle(Functions.CONTEXT.getString(R.string.full_cart, shoppingCart.size()));
            btnCartConfirm.setEnabled(true);

            float cartTotal = 0;

            for (int i = 0; i < shoppingCart.size(); i++) {
                cartTotal += shoppingCart.get(i).getItemCount() * shoppingCart.get(i).getProduct().getProductPrice();
            }

            btnCartConfirm.setText(Functions.CONTEXT.getString(R.string.cart_confirm_button, Functions.two(cartTotal)));

            if (llShoppingCartList.getChildCount() != shoppingCart.size() && llShoppingCart.getVisibility() == View.VISIBLE) {
                loadCart();
            }
        } else {
            btnCartConfirm.setText(MainMenu.context.getString(R.string.confirm_disable));
            navigationView.getMenu().findItem(R.id.nav_cart).setTitle(R.string.null_cart);
            btnCartConfirm.setEnabled(false);
        }
    }

    public static void loadCart() {
        try {
            llShoppingCartList.removeAllViews();

            for (int i = 0; i < shoppingCart.size(); i++) {
                llShoppingCartList.addView(shoppingCart.get(i).getView());
                shoppingCart.get(i).setIndex(i);
            }

            llUserInfo.setVisibility(View.INVISIBLE);
            llOrder.setVisibility(View.INVISIBLE);
            llProduct.setVisibility(View.INVISIBLE);
            llProductInfo.setVisibility(View.INVISIBLE);
            llShoppingCart.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            Functions.Track.error("LC", e);
        }
    }

    public void loadCategorises() {
        if (API.Category.load()) {
            categories = API.Category.gets();

            NavigationView navigationView = findViewById(R.id.nav_view);
            SubMenu subMenu = navigationView.getMenu().addSubMenu(R.string.categorises);

            CommonObjects.Category aof = new CommonObjects.Category(0, 0, getString(R.string.category_all_of_them), "", "");
            categories.add(aof);

            subMenu.add(aof.getCategoryName());

            for (int i = 1; i <= categories.size() - 1; i++) {
                subMenu.add(categories.get(i - 1).getCategoryName());
                Functions.loadImage(subMenu.getItem(i), categories.get(i - 1).getCategoryImage());
            }

            loadProducts();
        }
    }

    public void loadCategorises(String name) {
        for (int i = 0; i < categories.size(); i++) {
            if (categories.get(i).getCategoryName().equals(name)) {
                loadProducts(categories.get(i).getCategoryId());
            }
        }
    }

    public void loadProducts() {
        currentProductCategory = 0;

        loadProducts(0, false, false);
    }

    public void loadProducts(int category) {
        loadProducts(category, false, false);
    }

    public void loadProducts(int category, boolean isPageChange, boolean isSearch) {
        onSearch = isSearch;

        llUserInfo.setVisibility(View.INVISIBLE);
        llOrder.setVisibility(View.INVISIBLE);
        llProduct.setVisibility(View.VISIBLE);
        llShoppingCart.setVisibility(View.INVISIBLE);
        llProductInfo.setVisibility(View.INVISIBLE);

        if (isPageChange) {
        } else {
            glProduct.removeAllViews();
        }

        CommonObjects.Product[] products;

        if (isSearch) {
            products = API.Product.get(currentProductSearch, 0, currentProductPage, countProductPage);

        } else {
            products = API.Product.get(category, currentProductPage, countProductPage);
        }

        if (products.length == 0) {
            if (isPageChange && currentProductPage > 0) {
                currentProductPage--;
            }
        }

        for (int i = 0; i < products.length; i++) {
            View view = products[i].getView(this);
            glProduct.addView(view);
            currentProductCategory = isSearch ? 0 : category;
        }
    }

    public void loadMoreProduct(View v) {
        currentProductPage++;
        loadProducts(currentProductCategory, true, onSearch);
    }

    public void searchProduct(View v) {
        EditText etSearch = findViewById(R.id.etProductSearch);
        if (etSearch.getText() != null) {
            if (etSearch.getText().toString().equals("")) {
                currentProductPage = 0;
                currentProductSearch = "";
            } else {
                currentProductPage = 0;
                currentProductSearch = etSearch.getText().toString();
            }

            loadProducts(0, false, true);
        }
    }

    public void loadOrder() {
        ArrayList<CommonObjects.Order> objs = API.Order.select(-1, 0, 0);

        if (objs == null) {
            return;
        }

        llOrderList.removeAllViews();

        for (int i = 0; i < objs.size(); i++) {
            llOrderList.addView(objs.get(i).getView());
        }

        llUserInfo.setVisibility(View.INVISIBLE);
        llOrder.setVisibility(View.VISIBLE);
        llProductInfo.setVisibility(View.INVISIBLE);
        llShoppingCart.setVisibility(View.INVISIBLE);
        llProduct.setVisibility(View.INVISIBLE);
    }

    /*
        user_id,
        itemCount,
        totalAmount,
        OrderItems = { itemno, itemcount, itemprice ( serer ) }
     */
    public void completeOrder(View v) {
        if (shoppingCart.size() == 0) {
            Functions.message(this, "", getString(R.string.cart_null), true);
            return;
        }

        int itemCount = 0;
        float totalAmount = 0;

        JSONArray arrItems = new JSONArray();

        for (int i = 0; i < shoppingCart.size(); i++) {
            try {
                JSONObject obj = new JSONObject();
                obj.put("item_id", shoppingCart.get(i).getProduct().getId());
                obj.put("item_count", shoppingCart.get(i).getItemCount());

                itemCount++;
                totalAmount += shoppingCart.get(i).getItemCount() * shoppingCart.get(i).getProduct().getProductPrice();


                arrItems.put(obj);
            } catch (Exception e) {
                Functions.Track.error("CO-FI", e);
            }
        }

        Functions.WebResult result = API.Order.post(itemCount, totalAmount, arrItems.toString());

        if (result.isConnected() && result.isSuccess()) {
            Functions.message(MainMenu.context, "", result.getData(), false, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    shoppingCart.clear();
                    updateCartInfo();
                    loadProducts();
                }
            });
        } else {
            Functions.message(MainMenu.context, "", result.getData(), true);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        context = this;

        if (Functions.isOnline() == false) {
            Functions.message(this, "", getString(R.string.online), true, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    System.exit(0);
                }
            });

            return;
        }

        glProduct = findViewById(R.id.glProduct);
        svProduct = findViewById(R.id.svProduct);
        llProduct = findViewById(R.id.llProduct);

        llProductInfo = findViewById(R.id.llProductInfo);

        svShoppingCart = findViewById(R.id.svShoppingCart);
        llShoppingCart = findViewById(R.id.llShoppingCart);

        llShoppingCartList = findViewById(R.id.llShoppingCartList);

        llOrder = findViewById(R.id.llOrder);
        llOrderList = findViewById(R.id.llOrderList);

        etSearch = findViewById(R.id.etProductSearch);

        svOrder = findViewById(R.id.svOrder);

        llUserInfo = findViewById(R.id.llUserPanel);

        btnCartConfirm = findViewById(R.id.btnCartConfirm);
        btnCartConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setClickable(false);
                completeOrder(v);
                v.setClickable(true);
            }
        });

        listenerOrderItem = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonObjects.Product obj = (CommonObjects.Product) v.getTag();

                viewProduct(obj);
            }
        };

        Functions.CONTEXT = getApplicationContext();

        // ImagePicker.setMinQuality(128, 128);
        loadUser();


        //   ((Button) findViewById(R.id.test)).setText(user.getName());

        loadCategorises();


        // loadProducts();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (llShoppingCart.getVisibility() == View.VISIBLE) {
                llOrder.setVisibility(View.INVISIBLE);
                llShoppingCart.setVisibility(View.INVISIBLE);
                llProductInfo.setVisibility(View.INVISIBLE);
                llUserInfo.setVisibility(View.INVISIBLE);
                llProduct.setVisibility(View.VISIBLE);
            } else if (llProductInfo.getVisibility() == View.VISIBLE) {
                llOrder.setVisibility(View.INVISIBLE);
                llUserInfo.setVisibility(View.INVISIBLE);
                llShoppingCart.setVisibility(View.INVISIBLE);
                llProductInfo.setVisibility(View.INVISIBLE);
                llProduct.setVisibility(View.VISIBLE);
            } else if (llOrder.getVisibility() == View.VISIBLE) {
                llOrder.setVisibility(View.INVISIBLE);
                llUserInfo.setVisibility(View.INVISIBLE);
                llShoppingCart.setVisibility(View.INVISIBLE);
                llProductInfo.setVisibility(View.INVISIBLE);
                llProduct.setVisibility(View.VISIBLE);
            } else if (llUserInfo.getVisibility() == View.VISIBLE) {
                llOrder.setVisibility(View.INVISIBLE);
                llProduct.setVisibility(View.VISIBLE);
                llShoppingCart.setVisibility(View.INVISIBLE);
                llProductInfo.setVisibility(View.INVISIBLE);
                llUserInfo.setVisibility(View.INVISIBLE);
            } else if (etSearch.getText() != null && etSearch.getText().toString().equals("") == false) {
                etSearch.setText("");
                searchProduct(null);
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_exit:
                Functions.setConfig("is_logged", false);
                loadUser();
                Functions.message(this, "", getString(R.string.exit_ok), false);
                loadCategorises();
                break;
            case R.id.nav_login:
                startActivity(new Intent(MainMenu.this, LoginOrRegister.class).putExtra("request", "login"));
                break;
            case R.id.nav_register:
                startActivity(new Intent(MainMenu.this, LoginOrRegister.class).putExtra("request", "register"));
                break;
            case R.id.nav_forgot_password:
                startActivity(new Intent(MainMenu.this, LoginOrRegister.class).putExtra("request", "forgot_password"));
                break;
            case R.id.nav_cart:
                showCart();
                break;
            case R.id.nav_orders:
                loadOrder();
                break;
            case R.id.nav_admin:
                startActivity(new Intent(MainMenu.this, ControlPanel.class));
                break;
            case R.id.nav_account:
                showAccount();
                break;
            default:
                loadCategorises(item.getTitle().toString());
                break;
        }
/*
        if (id == R.id.nav_home) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_tools) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }
*/
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showAccount() {
        llOrder.setVisibility(View.INVISIBLE);
        llProduct.setVisibility(View.INVISIBLE);
        llShoppingCart.setVisibility(View.INVISIBLE);
        llProductInfo.setVisibility(View.INVISIBLE);
        llUserInfo.setVisibility(View.VISIBLE);

        ((EditText) findViewById(R.id.etRegisterPhone)).setText(user.getPhone());
        ((EditText) findViewById(R.id.etRegisterAddress)).setText(user.getAddress());
        ((EditText) findViewById(R.id.etRegisterEmail)).setText(user.getEmail());
        ((EditText) findViewById(R.id.etRegisterName)).setText(user.getName());
        ((EditText) findViewById(R.id.etRegisterSurName)).setText(user.getSurname());

    }

    private void resetEdit(int id) {
        try {
            ((EditText) findViewById(id)).setText("");
        } catch (Exception e) {
            Functions.Track.error("RE", e);
        }
    }

    private String editValue(int id) {
        try {
            return ((EditText) findViewById(id)).getText().toString();
        } catch (Exception e) {
            Functions.Track.error("EDV", e);
            return "";
        }
    }

    private void showCart() {

        loadCart();
        updateCartInfo();
    }

    public void updatePassword(View view) {
        if (!user.isLogged()) {
            return;
        }

        if (editValue(R.id.etRegisterPassword).equals("") || editValue(R.id.etRegisterPasswordRepeat).equals("")) {
            Functions.message(this, "", getString(R.string.null_password), true);

            return;
        } else if (!editValue(R.id.etRegisterPassword).equals(editValue(R.id.etRegisterPasswordRepeat))) {
            Functions.message(this, "", getString(R.string.match_password), true);
            return;
        }

        Functions.WebResult result = user.changePassword(editValue(R.id.etRegisterPassword));

        if (result.isConnected() && result.isSuccess()) {
            Functions.message(this, "", result.getData(), false, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Functions.setConfig("is_logged", false);
                    loadUser();
                    Functions.message(MainMenu.this, "", getString(R.string.exit_ok), false);
                    llUserInfo.setVisibility(View.INVISIBLE);
                    llProduct.setVisibility(View.VISIBLE);
                }
            });
        } else {
            Functions.message(this, "", result.getData(), true);
        }
    }

    public void updateUserInfo(View view) {
        Functions.WebResult result = user.update(editValue(R.id.etRegisterName), editValue(R.id.etRegisterSurName), editValue(R.id.etRegisterAddress), editValue(R.id.etRegisterPhone));

        if (result.isConnected() && result.isSuccess()) {
            Functions.message(this, "", result.getData(), false);
            MainMenu.loadUser();
        } else {
            Functions.message(this, "", result.getData(), true);
        }

    }
}
