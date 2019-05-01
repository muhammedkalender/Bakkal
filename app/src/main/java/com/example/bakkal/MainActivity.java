package com.example.bakkal;

import android.os.Debug;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    public static User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        user = new User();

        for (int i = 0;i < 20; i++){

            View x = getLayoutInflater().inflate(R.layout.category, null);

            ((GridLayout)findViewById(R.id.glCategory)).addView(x);
        }

       /* HashMap<String, String> params = new HashMap<>();
        params.put("method", "get");
        params.put("cat", "user");

        params.put("user_login", "test");
        params.put("user_password", "123");

        Functions.WebResult result = Functions.getData(params);

        Toast.makeText(this, result.getData() + "", Toast.LENGTH_SHORT).show();
        Log.e("aS", result.getData() + "");
        if (result.isConnected() && result.isSuccess()) {
            ((TextView) findViewById(R.id.text)).setText(result.getData() + "");

        }else{
            ((TextView) findViewById(R.id.text)).setText(result.getData() + "ddd");

        }*/
    }
}
