package com.example.bakkal;

import android.os.Debug;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpResponse;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        HashMap<String, String> params = new HashMap<>();
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

        }
    }
}
