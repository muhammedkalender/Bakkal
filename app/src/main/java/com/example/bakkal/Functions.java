package com.example.bakkal;

import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

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
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class Functions {
    public static final String BASE_URL = "http://192.168.64.2";

    public static String clearAndEncodeData(String productName) {
        //todo
        return "";
    }

    public static String encodeData(String productDescription) {
        //todo
        return "";
    }

    static class WebResult {
        private boolean connected;
        private boolean success;
        private String data;
        private JSONObject object;

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
            } catch (Exception e) {
                //Format yanlış
                setSuccess(false);
                this.data = "";
                this.object = null;
            }
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

            params.put("token_key", MainActivity.user.getTokenKey());
            params.put("token_lock", MainActivity.user.getTokenLock());


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
            //todo
            Log.e("mesaj", e.getMessage() + "");
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

    public static String getConfig(String name){
        //todo
        return getConfig(name, "");
    }

    public static int getConfig(String name, int value){
        //todo
        return value;
    }

    public static String getConfig(String name, String value){
        //todo
        return value;

    }

    public static boolean getConfig(String name, boolean value){
        //todo
        return value;

    }

    public static void setConfig(String name, boolean value){
        //todo
    }

    public static void setConfig(String name, String value){
        //todo
    }

    public static void setConfig(String name, int value){
        //todo
    }

    public static boolean isOnline(){
        //todo
        return false;
    }
}
