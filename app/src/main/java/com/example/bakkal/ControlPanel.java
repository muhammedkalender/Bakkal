package com.example.bakkal;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import com.mvc.imagepicker.ImagePicker;

import java.net.URI;
import java.util.ArrayList;

public class ControlPanel extends AppCompatActivity {

    ScrollView svMenu;
    Spinner spinnerCategory;
    LinearLayout llCategory;
    EditText etCategory;
    ImageView ivCategory;
    CommonObjects.Category[] categories;
    int selectedCategoryPosition = 0;
    ArrayAdapter<String> spinner;

    boolean isProductImage = false;

    LinearLayout llPageProduct, llProductEdit;
    ScrollView svProduct;

    EditText etProduct;
    ImageView ivProduct;
    String productImageUrl;
    CommonObjects.Product currentProduct;
    Spinner spinnerProductCategory;
    int selectedProductCategoryPosition = 0;
    LinearLayout llProductStock;

    LinearLayout llOrder;
    LinearLayout llOrderContent;
    static LinearLayout llOrderlist;
    ScrollView svOrder;
    static Spinner spinnerStatue;
    public static Spinner spinnerSetStatue;
    int orderPage = 0;

    public static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control_panel);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        svMenu = findViewById(R.id.svMenu);

        llCategory = findViewById(R.id.llAPCategory);
        etCategory = findViewById(R.id.etCategoryName);

        spinnerCategory = findViewById(R.id.spinCategory);

        llPageProduct = findViewById(R.id.llAPProduct);
        llProductEdit = findViewById(R.id.llAPProductGetter);
        svProduct = findViewById(R.id.svAPProduct);

        etProduct = findViewById(R.id.etProductID);

        ivProduct = findViewById(R.id.ivProductInfoImage);

        spinnerProductCategory = findViewById(R.id.spinnerProductCateogry);

        llProductStock = findViewById(R.id.llProductStock);

        llOrder = findViewById(R.id.llAPOrder);
        llOrderContent = findViewById(R.id.llAPContent);
        svOrder = findViewById(R.id.svAPOrder);
        llOrderlist = findViewById(R.id.llAPList);
        spinnerStatue = findViewById(R.id.spinOrderStatus);

        spinnerSetStatue = findViewById(R.id.spinOrderSetStatus);
        context = this;
        // ivCategory = findViewById(R.id.ivCategoryImage);

       /* ivCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker .pickImage(ControlPanel.this, getString(R.string.select_image));
            }
        });*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            String URL = ImagePicker.getImagePathFromResult(this, requestCode, resultCode, data);

            if (URL != null && URL.equals("") == false) {
                Functions.WebResult result = API.Image(URL);

                if (result.isConnected() && result.isSuccess()) {
                    if (isProductImage) {

                        Functions.loadImage(ivProduct, Functions.BASE_URL + result.getRaw().getString(2));
                        productImageUrl = result.getRaw().getString(2);
                    }
                } else {
                    Functions.message(this, "", result.getData(), true);
                }
            }
        } catch (Exception e) {
            Functions.Track.error("GI", e);
        }
    }


    public void loadCategory(View view) {
        try {
            categories = API.Category.gets(true);

            if (categories.length == 0) {
                Functions.message(ControlPanel.context, "", getString(R.string.null_category), true);
                return;
            }

            if (spinner == null) {
                spinner = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
                spinnerCategory.setAdapter(spinner);
                spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        etCategory.setText(categories[position].getCategoryName());
                        //Functions.loadImage(ivCategory, categories.get(position).getCategoryImage());
                        selectedCategoryPosition = position;
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
            }

            spinner.clear();

            for (int i = 0; i < categories.length; i++) {
                spinner.add(categories[i].getCategoryId() + " - " + categories[i].getCategoryName());
            }

            svMenu.setVisibility(View.INVISIBLE);

            llCategory.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            Functions.Track.error("AP-CAT", e);
        }
    }

    public void addCategory(View view) {
        if (etCategory.getText() != null && etCategory.getText().toString().equals("") == false) {
            Functions.WebResult result = API.Category.insert(0, etCategory.getText().toString(), "", "");

            if (result.isConnected() && result.isSuccess()) {
                loadCategory(null);

                Functions.message(this, "", result.getData(), false, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        loadCategory(null);
                    }
                });
            } else {
                Functions.message(this, "", result.getData(), true);
            }
        }
    }

    public void deleteCategory(View view) {
        int pos = spinnerCategory.getSelectedItemPosition();

        if (etCategory.getText() != null && etCategory.getText().toString().equals("") == false) {
            Functions.WebResult result = API.Category.delete(categories[pos].getCategoryId());

            if (result.isConnected() && result.isSuccess()) {
                Functions.message(this, "", result.getData(), false, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        loadCategory(null);
                    }
                });
            } else {
                Functions.message(this, "", result.getData(), true);
            }
        }
    }

    public void saveCategory(View view) {
        int pos = spinnerCategory.getSelectedItemPosition();


        if (etCategory.getText() != null && etCategory.getText().toString().equals("") == false) {
            Functions.WebResult result = API.Category.update(categories[pos].getCategoryId(), etCategory.getText().toString());

            if (result.isConnected() && result.isSuccess()) {
                Functions.message(this, "", result.getData(), false, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        loadCategory(null);
                    }
                });
            } else {
                Functions.message(this, "", result.getData(), true);
            }
        }
    }

    public void getProductFromId(View view) {
        try {
            if (etProduct.getText() == null || etProduct.getText().toString().equals("")) {
                Functions.message(this, "", getString(R.string.choose_product), true);
                return;
            }

            CommonObjects.Product x = API.Product.get(Integer.parseInt(etProduct.getText().toString()));

            if (x == null) {
                Functions.message(this, "", getString(R.string.product_null), true);
                return;
            } else {
                ((EditText) findViewById(R.id.etProductInfoPrice)).setText(String.valueOf(Functions.two(x.getProductPrice())));
                ((EditText) findViewById(R.id.etProductInfoStock)).setText(String.valueOf(Functions.two(x.getStock())));
                ((EditText) findViewById(R.id.etProductInfoWeight)).setText(x.getPacketWeight());
                ((EditText) findViewById(R.id.etProductInfoName)).setText(x.getProductName());
                ((EditText) findViewById(R.id.etProductInfoBrand)).setText(x.getProductBrand());
                ((EditText) findViewById(R.id.etProductInfoDesc)).setText(x.getProductDescription());
                spinnerProductCategory.setSelection(findPosition(x.getProductCategory()));

                ((Button) findViewById(R.id.btnDeleteProduct)).setTag(x.getId());

                productImageUrl = x.getProductImage();
                currentProduct = x;
                Functions.loadImage(ivProduct, Functions.BASE_URL + x.getProductImage());
            }
        } catch (Exception e) {
            Functions.message(this, "", getString(R.string.error_product), true);
            Functions.Track.error("GP-ERR", e);
        }
    }

    private int findPosition(int productCategory) {
        for (int i = 0; i < categories.length; i++) {
            if (categories[i].getCategoryId() == productCategory) {
                return i;
            }
        }

        return 0;
    }

    public void productImage(View view) {
        ImagePicker.pickImage(ControlPanel.this, getString(R.string.pick_image_intent_text));
    }


    public void addProduct(View view) {
        currentProduct = null;

        ivProduct.setImageDrawable(null);

        productImageUrl = "";

        isProductImage = true;

        svMenu.setVisibility(View.INVISIBLE);
        llProductEdit.setVisibility(View.INVISIBLE);
        llPageProduct.setVisibility(View.VISIBLE);
        llProductStock.setVisibility(View.INVISIBLE);
        ((Button) findViewById(R.id.btnDeleteProduct)).setVisibility(View.INVISIBLE);
        updateCategorises();

        spinnerProductCategory.setSelection(0);
        resetEdit(R.id.etProductInfoPrice);
        resetEdit(R.id.etProductInfoWeight);
        resetEdit(R.id.etProductInfoDesc);
        resetEdit(R.id.etProductInfoName);
        resetEdit(R.id.etProductInfoBrand);
        resetEdit(R.id.etProductEditStock);
        resetEdit(R.id.etProductInfoStock);
    }

    public void editProduct(View view) {
        currentProduct = null;
        ivProduct.setImageDrawable(null);

        productImageUrl = "";

        isProductImage = true;
        llProductStock.setVisibility(View.VISIBLE);

        svMenu.setVisibility(View.INVISIBLE);
        llProductEdit.setVisibility(View.VISIBLE);
        llPageProduct.setVisibility(View.VISIBLE);
        ((Button) findViewById(R.id.btnDeleteProduct)).setVisibility(View.VISIBLE);
        updateCategorises();

        spinnerProductCategory.setSelection(0);
        resetEdit(R.id.etProductInfoPrice);
        resetEdit(R.id.etProductInfoWeight);
        resetEdit(R.id.etProductInfoDesc);
        resetEdit(R.id.etProductInfoName);
        resetEdit(R.id.etProductInfoBrand);
        resetEdit(R.id.etProductInfoStock);
        resetEdit(R.id.etProductEditStock);
    }

    public void deleteProduct(View view) {
        try {
            if (etProduct.getText() == null || etProduct.getText().toString().equals("")) {
                Functions.message(this, "", getString(R.string.null_product), true);
                return;
            }

            Functions.message(ControlPanel.this, "", getString(R.string.confirm_delete), false, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Functions.WebResult result = API.Product.delete(Integer.parseInt(etProduct.getText().toString()));

                    if (result.isConnected() && result.isSuccess()) {
                        Functions.message(ControlPanel.this, "", result.getData(), false);
                    } else {
                        Functions.message(ControlPanel.this, "", result.getData(), true);
                    }
                }
            });
        } catch (Exception e) {
            Functions.Track.error("DP", e);
        }
    }

    public void confirmProduct(View view) {
        if (llProductEdit.getVisibility() == View.INVISIBLE) {
            addProduct();
        } else {
            editProduct();
        }
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

    private void updateCategorises() {
        categories = API.Category.gets(true);

        if (categories.length == 0) {
            Functions.message(context, "", getString(R.string.null_category), true);

        }

        if (spinner == null) {
            spinner = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
            spinnerCategory.setAdapter(spinner);
            spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    etCategory.setText(categories[position].getCategoryName());
                    //Functions.loadImage(ivCategory, categories.get(position).getCategoryImage());
                    selectedCategoryPosition = position;
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        }

        spinnerProductCategory.setAdapter(spinner);
        spinnerProductCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                //Functions.loadImage(ivCategory, categories.get(position).getCategoryImage());
                selectedProductCategoryPosition = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        spinner.clear();

        for (int i = 0; i < categories.length; i++) {
            spinner.add(categories[i].getCategoryId() + " - " + categories[i].getCategoryName());
        }
    }

    private void addProduct() {
        try {
            Functions.WebResult result = API.Product.insert(
                    editValue(R.id.etProductInfoBrand),
                    editValue(R.id.etProductInfoName),
                    editValue(R.id.etProductInfoDesc),
                    categories[selectedProductCategoryPosition].getCategoryId(),
                    productImageUrl,
                    editValue(R.id.etProductInfoWeight),
                    Functions.two(Float.parseFloat(editValue(R.id.etProductInfoPrice))));

            if (result.isConnected() && result.isSuccess()) {
                Functions.message(this, "", result.getData(), true);

                spinnerProductCategory.setSelection(0);
                resetEdit(R.id.etProductInfoPrice);
                resetEdit(R.id.etProductInfoWeight);
                resetEdit(R.id.etProductInfoDesc);
                resetEdit(R.id.etProductInfoName);
                resetEdit(R.id.etProductInfoBrand);
                resetEdit(R.id.etProductEditStock);
                resetEdit(R.id.etProductInfoStock);

                ivProduct.setImageDrawable(null);
            } else {
                Functions.message(this, "", getString(R.string.error_add_product), true);
            }
        } catch (Exception e) {
            Functions.Track.error("APP", e);
            Functions.message(this, "", getString(R.string.error_product_add), true);
        }
    }


    private void editProduct() {
        try {
            Functions.WebResult result = API.Product.update(
                    currentProduct.getId(),
                    editValue(R.id.etProductInfoBrand),
                    editValue(R.id.etProductInfoName),
                    editValue(R.id.etProductInfoDesc),
                    categories[selectedProductCategoryPosition].getCategoryId(),
                    productImageUrl,
                    editValue(R.id.etProductInfoWeight),
                    Functions.two(Float.parseFloat(editValue(R.id.etProductInfoPrice))));

            if (result.isConnected() && result.isSuccess()) {
                Functions.message(this, "", result.getData(), false);
            } else {
                Functions.message(this, "", result.getData(), true);
            }
        } catch (Exception e) {
            Functions.Track.error("APP", e);
            Functions.message(this, "", getString(R.string.error_edit_add), true);
        }
    }

    public void addStock(View view) {
        try {

            if (currentProduct == null) {
                Functions.message(this, "", getString(R.string.must_choose_product), true);
                return;
            }

            if (editValue(R.id.etProductEditStock).equals("")) {
                Functions.message(this, "", getString(R.string.must_insert), true);
                return;
            }

            Functions.WebResult result = API.Product.addStock(currentProduct.getId(), Functions.two(Float.parseFloat(editValue(R.id.etProductEditStock))));

            if (result.isConnected() && result.isSuccess()) {
                Functions.message(this, "", result.getData(), false);
                etProduct.setText(String.valueOf(currentProduct.getId()));
                getProductFromId(null);
            } else {
                Functions.message(this, "", result.getData(), true);
            }
        } catch (Exception e) {
            Functions.Track.error("ADDS", e);
            Functions.message(this, "", getString(R.string.error_stock), true);
        }
    }

    public void delStock(View view) {
        try {

            if (currentProduct == null) {
                Functions.message(this, "", getString(R.string.must_choose_product), true);
                return;
            }

            if (editValue(R.id.etProductEditStock).equals("")) {
                Functions.message(this, "", getString(R.string.must_insert), true);
                return;
            }

            Functions.WebResult result = API.Product.delStock(currentProduct.getId(), Functions.two(Float.parseFloat(editValue(R.id.etProductEditStock))));

            if (result.isConnected() && result.isSuccess()) {
                Functions.message(this, "", result.getData(), false);
                etProduct.setText(String.valueOf(currentProduct.getId()));
                getProductFromId(null);
            } else {
                Functions.message(this, "", result.getData(), true);
            }
        } catch (Exception e) {
            Functions.Track.error("DELLS", e);
            Functions.message(this, "", getString(R.string.error_stock), true);
        }
    }

    @Override
    public void onBackPressed() {
        if (llCategory.getVisibility() == View.VISIBLE) {
            svMenu.setVisibility(View.VISIBLE);
            llCategory.setVisibility(View.INVISIBLE);
        } else if (llPageProduct.getVisibility() == View.VISIBLE) {
            svMenu.setVisibility(View.VISIBLE);
            llPageProduct.setVisibility(View.INVISIBLE);
        } else {
            super.onBackPressed();
        }
    }

    public void editOrder(View view) {
        svMenu.setVisibility(View.INVISIBLE);
        llOrder.setVisibility(View.VISIBLE);
    }

    public static void ordersFromStatue(View view) {
        ArrayList<CommonObjects.Order> objs = API.Order.select(spinnerStatue.getSelectedItemPosition(), 0, 0,true);

        llOrderlist.removeAllViews();

        if (objs == null) {
            Functions.message(context, "", ControlPanel.context.getString(R.string.null_order_statue), true);
            return;
        }

        for (int i = 0; i < objs.size(); i++) {
            View x = objs.get(i).getView();

            objs.get(i).showUserButton();
            objs.get(i).showUpdateButton();


            if (x != null) {
                llOrderlist.addView(x);
            }
        }
    }

    public void orderFromId(View view) {
        if (editValue(R.id.etOrder).equals("")) {
            Functions.message(context, "", getString(R.string.null_order_id), true);
            return;
        }

        CommonObjects.Order obj = API.Order.get(Integer.parseInt(editValue(R.id.etOrder)), MainMenu.user.isAdmin());

        llOrderlist.removeAllViews();

        if (obj == null) {
            Functions.message(context, "", getString(R.string.null_order), true);
            return;
        }

        View x = obj.getView();
        obj.showUserButton();
        obj.showUpdateButton();

        if (x == null) {
            Functions.message(context, "", getString(R.string.null_order), true);
            return;
        }

        llOrderlist.addView(x);
    }
}
