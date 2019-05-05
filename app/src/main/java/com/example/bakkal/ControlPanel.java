package com.example.bakkal;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control_panel);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Log.e("asda", "xx1");

        svMenu = findViewById(R.id.svMenu);
        Log.e("asda", "xx2");

        llCategory = findViewById(R.id.llAPCategory);
        etCategory = findViewById(R.id.etCategoryName);

        spinnerCategory = findViewById(R.id.spinCategory);
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
        // Bitmap bitmap = ImagePicker.getImageFromResult(this, requestCode, resultCode, data);
        String URL = ImagePicker.getImagePathFromResult(this, requestCode, resultCode, data);

        if (URL != null && URL.equals("") == false) {
        }
        // TODO do something with the bitmap
    }


    public void loadCategory(View view) {
        try {
            categories = API.Category.gets(true);

            if (categories.length == 0) {
                //todo
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
                        Toast.makeText(ControlPanel.this, position + "", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        Toast.makeText(ControlPanel.this, "boşş", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            spinner.clear();

            for (int i = 0; i < categories.length; i++) {
                spinner.add(categories[i].getCategoryId() + " - " + categories[i].getCategoryName());
            }

            svMenu.setVisibility(View.INVISIBLE);
            Log.e("asda", "xx7771");

            llCategory.setVisibility(View.VISIBLE);
            Log.e("asda", "999xx1");

        } catch (Exception e) {
            Functions.Track.error("AP-CAT", e);
        }
    }

    public void addCategory(View view) {
        if (etCategory.getText() != null && etCategory.getText().toString().equals("") == false) {
            Functions.WebResult result = API.Category.insert(0, etCategory.getText().toString(), "", "");
            Log.e("asdds", etCategory.getText().toString() + "aa");
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
}
