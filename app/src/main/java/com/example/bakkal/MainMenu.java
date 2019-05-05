package com.example.bakkal;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.design.internal.NavigationMenu;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.Layout;
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
import android.widget.Toast;

import com.bumptech.glide.Glide;

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

    private int countProductPage = 20, currentProductPage = 0, currentProductCategory = 0;

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

    public static ArrayList<CommonObjects.OrderItem> shoppingCart = new ArrayList<>();

    public static void loadUser() {
        user = new User();
        user.load();

        //todo kategory seçilsede, menü seçili kalıyor

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
            Functions.loadImage((ImageView) findViewById(R.id.ivProductInfoImage), obj.getProductImage());

            ((EditText) findViewById(R.id.etProductInfoBrand)).setText(obj.getProductBrand());
            ((EditText) findViewById(R.id.etProductInfoDesc)).setText(obj.getProductDescription());
            Log.e("asdas", "xx");
            ((EditText) findViewById(R.id.etProductInfoStock)).setText(String.valueOf(obj.getStock()));
            Log.e("asdas", "xx222");

            ((EditText) findViewById(R.id.etProductInfoName)).setText(obj.getProductName());
            ((EditText) findViewById(R.id.etProductInfoWeight)).setText(obj.getPacketWeight());
            ((EditText) findViewById(R.id.etProductInfoPrice)).setText(String.valueOf(obj.getProductPrice()));

            ((Button) findViewById(R.id.btnProductInfoConfirm)).setTag(obj);
            ((Button) findViewById(R.id.btnProductInfoConfirm)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//todo
                    try {
                        EditText etStock = findViewById(R.id.pre_product_info_stock);

                        if (etStock.getText() == null) {
                            Log.e("asdas", "xx2");

                            //todo
                            return;
                        }

                        float orderCount = Float.parseFloat(etStock.getText().toString());

                        if (orderCount <= 0) {
                            Log.e("asdas", "xx1");

                            //todo
                            return;
                        }

                        CommonObjects.Product cobj = (CommonObjects.Product) v.getTag();
                        Log.e("asdas", "xx233");

                        if (cobj != null && orderCount > cobj.getStock()) {
                            Functions.message(MainMenu.this, "", Functions.CONTEXT.getString(R.string.more_than_stock), true);
                            return;
                        }
                        Log.e("asdas", "xx244");


                        int index = -1;
                        Log.e("asdas", "xx255");

                        for (int i = 0; i < shoppingCart.size(); i++) {
                            if (shoppingCart.get(i).getProduct().getId() == cobj.getId()) {
                                index = i;
                            }
                        }
                        Log.e("asdas", "xx667772");

                        if (index != -1) {
                            shoppingCart.get(index).setItemCount(shoppingCart.get(index).getItemCount() + orderCount);
                            shoppingCart.get(index).setItemTotal(shoppingCart.get(index).getItemCount() * shoppingCart.get(index).getProduct().getProductPrice());
                            Log.e("asdas", "xx29999999");
                            //todo carttıda update etmeye gerek varmı ?
                        } else {
                            if (cobj == null) {
                                Log.e("asdasd", "nykkke");
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

            llShoppingCart.setVisibility(View.INVISIBLE);
            llProductInfo.setVisibility(View.VISIBLE);
            llProduct.setVisibility(View.INVISIBLE);
        } catch (Exception e) {
            Functions.Track.error("VP-MP", e);
        }
    }

    public static void updateCartInfo() {
        //todo

        if (shoppingCart.size() == 0) {
            llShoppingCartList.removeAllViews();
        }

        if (shoppingCart.size() > 0) {
            Log.e("a1", "asdasdas");
            navigationView.getMenu().findItem(R.id.nav_cart).setTitle(Functions.CONTEXT.getString(R.string.full_cart, shoppingCart.size()));
            btnCartConfirm.setEnabled(true);

            float cartTotal = 0;

            for (int i = 0; i < shoppingCart.size(); i++) {
                cartTotal = shoppingCart.get(i).getItemCount() * shoppingCart.get(i).getProduct().getProductPrice();
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
            //todo subMenu.getItem(0).setIcon()

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


        //todo
    }

    public void loadProducts(int category) {
        loadProducts(category, false, false);
    }

    public void loadProducts(int category, boolean isPageChange, boolean isSearch) {
        //todo

        onSearch = isSearch;

        llProduct.setVisibility(View.VISIBLE);
        llShoppingCart.setVisibility(View.INVISIBLE);
        llProductInfo.setVisibility(View.INVISIBLE);

        if (isPageChange) {
            //todo
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
            //todo
        }

        for (int i = 0; i < products.length; i++) {
            View view = products[i].getView(this);
            glProduct.addView(view);
            Log.e("sadas", products[i].getProductName() + "");
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

        if (objs == null){
            return;
        }

        llOrderList.removeAllViews();

        for (int i = 0; i < objs.size(); i++) {
            llOrderList.addView(objs.get(i).getView());
        }

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
    public void complateOrder(View v) {
        if (shoppingCart.size() == 0) {
            //todo
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
                //todo
                Functions.Track.error("CO-FI", e);
            }
        }
        Log.e("sdad", arrItems.toString());
        Functions.WebResult result = API.Order.post(itemCount, totalAmount, arrItems.toString());

        if (result.isConnected() && result.isSuccess()) {
            //todo
            Functions.message(MainMenu.context, "", result.getData(), false, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    shoppingCart.clear();
                    updateCartInfo();
                    loadProducts();
                }
            });
        } else {
            //todo
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

        glProduct = findViewById(R.id.glProduct);
        svProduct = findViewById(R.id.svProduct);
        llProduct = findViewById(R.id.llProduct);

        llProductInfo = findViewById(R.id.llProductInfo);

        svShoppingCart = findViewById(R.id.svShoppingCart);
        llShoppingCart = findViewById(R.id.llShoppingCart);

        llShoppingCartList = findViewById(R.id.llShoppingCartList);

        llOrder = findViewById(R.id.llOrder);
        llOrderList = findViewById(R.id.llOrderList);

        svOrder = findViewById(R.id.svOrder);

        btnCartConfirm = findViewById(R.id.btnCartConfirm);
        btnCartConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo
                complateOrder(v);
            }
        });

        listenerOrderItem = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonObjects.Product obj = (CommonObjects.Product) v.getTag();

                viewProduct(obj);
                Log.e("asdasd", obj.getProductName());
            }
        };

        Functions.CONTEXT = getApplicationContext();

        // ImagePicker.setMinQuality(128, 128);
        loadUser();

        Toast.makeText(this, user.getId() + "" + user.getTokenKey(), Toast.LENGTH_SHORT).show();

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
                llProduct.setVisibility(View.VISIBLE);
            } else if (llProductInfo.getVisibility() == View.VISIBLE) {
                llOrder.setVisibility(View.INVISIBLE);
                llShoppingCart.setVisibility(View.INVISIBLE);
                llProductInfo.setVisibility(View.INVISIBLE);
                llProduct.setVisibility(View.VISIBLE);
            } else if (llOrder.getVisibility() == View.VISIBLE) {
                llOrder.setVisibility(View.INVISIBLE);
                llShoppingCart.setVisibility(View.INVISIBLE);
                llProductInfo.setVisibility(View.INVISIBLE);
                llProduct.setVisibility(View.VISIBLE);
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

    private void showCart() {

        loadCart();
        updateCartInfo();
    }
}
