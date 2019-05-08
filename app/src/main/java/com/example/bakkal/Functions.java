package com.example.bakkal;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import static android.content.Context.MODE_PRIVATE;

public class Functions {
    public static final String BASE_URL = "http://192.168.64.2/";
    public static Context CONTEXT = null;
    public static DecimalFormat decimalFormat = new DecimalFormat("#.##");

    public static float two(float data) {
        return Float.valueOf(decimalFormat.format(data));
    }

    public static String clearAndEncodeData(String productName) {
        //Gerek Kalmadı
        return productName;
    }

    public static String encodeData(String productDescription) {
        //Gerek Kalmadı
        return productDescription;
    }

    public static void loadImage(final MenuItem menuItem, String categoryImage) {
        try {
            Glide.with(CONTEXT).load(categoryImage).addListener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    return false;
                    //todo
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    menuItem.setIcon(resource);
                    return false;
                }
            });
        } catch (Exception e) {
            Track.error("GLD-MI", e);
        }
    }

    public static void loadImage(final ImageView object, String categoryImage) {
        try {
            Glide.with(CONTEXT).load(categoryImage).into(object);

           /* Glide.with(CONTEXT).load(categoryImage).addListener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    Log.e("faaS","failll");
                    return false;
                    //todo
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    Log.e("faaS","trwasd");

                    object.setImageDrawable(resource);
                    return false;
                }
            });*/
        } catch (Exception e) {
            Track.error("GLD-MI", e);
        }
    }

    static class WebResult {
        private boolean connected;
        private boolean success;
        private String data;
        private JSONObject object;
        private JSONArray raw;

        public WebResult(boolean connected, boolean success, String data) {
            setConnected(connected);
            setSuccess(success);

            setData(data);
        }

        public boolean isConnected() {
            return connected;
        }

        public void setConnected(boolean connected) {
            this.connected = connected;
        }

        public void setData(String data) {
            try {
                JSONArray result = new JSONArray(data);
                setSuccess((boolean) result.get(0));
                this.data = result.get(1).toString();
                this.raw = result;
            } catch (Exception e) {
                //Format yanlış
                Track.error("GDSD", e);
                Log.e("DATA", data + "22");
                setSuccess(false);
                this.data = "";
                this.object = null;
                this.raw = null;
            }
        }

        public JSONArray getRaw() {
            return raw;
        }

        public String getData() {
            return data;
        }

        public String getJSONData(String name) {
            try {
                if (object == null) {
                    this.object = new JSONObject(getData());
                    return "";
                }

                return object.getString(name);
            } catch (Exception e) {
                return "";
            }
        }

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }
    }

    public static WebResult getData(HashMap<String, String> params) {
        try {
            StrictMode.ThreadPolicy policy = new
                    StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            if (MainMenu.user == null) {
                Log.e("asdasd", "null");
            }
            params.put("token_key", MainMenu.user.getTokenKey());
            params.put("token_lock", MainMenu.user.getTokenLock());


            String response = "";
            URL url = new URL(BASE_URL + "/api.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPostDataString(params));

            writer.flush();
            writer.close();
            os.close();
            int responseCode = conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = br.readLine()) != null) {
                    response += line;
                }
                return new WebResult(true, true, response);
            } else {

                return new WebResult(false, false, "");
            }
        } catch (Exception e) {
            Track.error("GET", e);
            return new WebResult(false, false, "");
        }
    }

    //https://stackoverflow.com/a/31357311
    private static String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }

    public static String getConfig(String name) {
        return getConfig(name, "");
    }

    public static int getConfig(String name, int value) {
        try {
            SharedPreferences settings = CONTEXT.getSharedPreferences("bakkal", MODE_PRIVATE);
            return settings.getInt(name, value);
        } catch (Exception e) {
            Track.error("GCI", e);
            return value;
        }
    }

    public static String getConfig(String name, String value) {
        try {
            SharedPreferences settings = CONTEXT.getSharedPreferences("bakkal", MODE_PRIVATE);
            return settings.getString(name, value);
        } catch (Exception e) {
            Track.error("GCS", e);
            return value;
        }

    }

    public static boolean getConfig(String name, boolean value) {
        try {
            SharedPreferences settings = CONTEXT.getSharedPreferences("bakkal", MODE_PRIVATE);
            return settings.getBoolean(name, value);
        } catch (Exception e) {
            Track.error("GCB", e);
            return value;
        }
    }

    public static void setConfig(String name, boolean value) {
        try {
            SharedPreferences preferences = CONTEXT.getSharedPreferences("bakkal", MODE_PRIVATE);
            SharedPreferences.Editor edit = preferences.edit();

            edit.putBoolean(name, value);
            edit.apply();
        } catch (Exception e) {
            Track.error("SCB", e);
        }
    }

    public static void setConfig(String name, String value) {
        try {
            SharedPreferences preferences = CONTEXT.getSharedPreferences("bakkal", MODE_PRIVATE);
            SharedPreferences.Editor edit = preferences.edit();

            edit.putString(name, value);
            edit.apply();
        } catch (Exception e) {
            Track.error("SCS", e);
        }
    }

    public static void setConfig(String name, int value) {
        try {
            SharedPreferences preferences = CONTEXT.getSharedPreferences("bakkal", MODE_PRIVATE);
            SharedPreferences.Editor edit = preferences.edit();

            edit.putInt(name, value);
            edit.apply();
        } catch (Exception e) {
            Track.error("SCI", e);
        }
    }

    public static boolean isOnline() {
        try {
            ConnectivityManager connectivityManager
                    = (ConnectivityManager) MainMenu.context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        } catch (Exception e) {
            Track.error("IO", e);
            return false;
        }
    }


    public static void message(String title, String message, boolean isError) {
        try {
            if (title == null || title.equals("")) {
                title = isError ? "Hata Mesajı" : "Durum Bilgisi";
            }
            new AlertDialog.Builder(CONTEXT)
                    .setTitle(title)
                    .setMessage(message)
                    .setIcon(isError ? android.R.drawable.ic_dialog_alert : android.R.drawable.ic_dialog_info).setNeutralButton("Tamam", null).show();

        } catch (Exception e) {
            Track.error("SHW_MSG", e);
        }
    }

    public static void message(Context context, String title, String message, boolean isError) {
        try {
            if (title == null || title.equals("")) {
                title = isError ? "Hata Mesajı" : "Durum Bilgisi";
            }
            new AlertDialog.Builder(context)
                    .setTitle(title)
                    .setMessage(message)
                    .setIcon(isError ? android.R.drawable.ic_dialog_alert : android.R.drawable.ic_dialog_info).setNeutralButton("Tamam", null).show();

        } catch (Exception e) {
            Track.error("SHW_MSG", e);
        }
    }

    public static void message(Context context, String title, String message, boolean isError, DialogInterface.OnClickListener listener) {
        try {
            if (title == null || title.equals("")) {
                title = isError ? "Hata Mesajı" : "Durum Bilgisi";
            }
            new AlertDialog.Builder(context)
                    .setTitle(title)
                    .setMessage(message)
                    .setIcon(android.R.drawable.ic_dialog_alert).setNeutralButton("Tamam", listener).show();

        } catch (Exception e) {
            Track.error("SHW_MSG", e);
        }
    }

    public static class Track {
        public static void error(String name, Exception e) {
            Log.e(name, e.getMessage() + "");
        }
    }
}
