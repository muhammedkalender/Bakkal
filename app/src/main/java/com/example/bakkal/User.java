package com.example.bakkal;

import android.util.Log;

import org.json.JSONObject;

import java.util.HashMap;

public class User {
    private String tokenLock = "", tokenKey = "";

    private boolean admin = false;
    private boolean logged = false;

    private int id;
    private String name, surname, address, phone, email;

    public User() {
        //todo
    }

    public void load() {
        boolean isLogged = Functions.getConfig("is_logged", false);
        Log.e("asdas", isLogged ? "tr" : "fl");
        if (isLogged) {
            String token = Functions.getConfig("token_key");
            String token_lock = Functions.getConfig("token_lock");

            if (token != null && token_lock != null) {
                if (Functions.isOnline()) {
                    HashMap<String, String> params = new HashMap<>();
                    params.put("cat", "user");
                    params.put("req", "login_check");


                    setTokenKey(Functions.getConfig("token_key"));
                    setTokenLock(Functions.getConfig("token_lock"));
                    // params.put("token_key", Functions.getConfig("token_key"));
                    //params.put("token_lock", Functions.getConfig("token_lock"));
                    Log.e("sadasd", Functions.getConfig("token_key") + "----" + Functions.getConfig("token_lock"));
                    Functions.WebResult result = Functions.getData(params);

                    if (result.isConnected() && result.isSuccess()) {
                        setId(Functions.getConfig("user_id", 0));
                        setAdmin(Functions.getConfig("user_admin", false));
                        setName(Functions.getConfig("user_name"));
                        setSurname(Functions.getConfig("user_surname"));
                        setAddress(Functions.getConfig("user_address"));
                        setTokenKey(Functions.getConfig("token_key"));
                        setTokenLock(Functions.getConfig("token_lock"));
                        setPhone("user_phone");
                        setEmail(Functions.getConfig("user_email"));

                        setLogged(true);
                    } else {
                        Log.e("asdas", "aa1");
                        Log.e("asdasdsa", result.getData());
                        setLogged(false);
                    }
                } else {
                    Log.e("asdas", "aa2");

                    //todo network error ?
                    setLogged(false);
                }
            } else {
                Log.e("asdas", "aa3");

                setLogged(false);
            }
        }
        //todo is logged ? check firebase ? yada mysql
    }

    public User(int userId) {
        //todo
    }

    public Functions.WebResult register(String name, String surname, String email, String password, String passwordRepeat, String address, String phone) {
        HashMap<String, String> params = new HashMap<>();
        params.put("req", "register");
        params.put("cat", "user");


        params.put("user_name", name);
        params.put("user_surname", surname);
        params.put("user_address", address);
        params.put("user_phone", phone);
        params.put("user_email", email);
        params.put("user_password", password);
        params.put("user_password_repeat", passwordRepeat);

        return Functions.getData(params);
    }

    public Functions.WebResult login(String username, String password) {
        HashMap<String, String> params = new HashMap<>();
        params.put("req", "login");
        params.put("cat", "user");

        params.put("user_email", username);
        params.put("user_password", password);

        Functions.WebResult result = Functions.getData(params);

        if (result.isConnected() && result.isSuccess()) {
            try {
                JSONObject user = result.getRaw().getJSONObject(2);
                setId(user.getInt("user_id"));
                setAdmin(user.getInt("user_admin") == 1);
                setName(user.getString("user_name"));
                setSurname(user.getString("user_surname"));
                setAddress(user.getString("user_address"));
                setTokenKey(result.getRaw().getString(4));
                setTokenLock(result.getRaw().getString(3));
                setPhone(user.getString("user_phone"));
                setEmail(user.getString("user_email"));

                setLogged(true);

                MainMenu.loadUser();
            } catch (Exception e) {
                Functions.Track.error("LGN_JSON", e);
                return result;
            }

            return result;
        } else {
            return result;
        }
    }

    public boolean logout() {
        //todo
        return false;
    }

    public boolean isLogged() {
        return this.logged;
    }

    public void setLogged(boolean logged) {
        this.logged = logged;
        Log.e("asda", "değişiö" + (logged ? "tr" : "fl"));
        Functions.setConfig("is_logged", this.logged);
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
        Functions.setConfig("user_admin", admin);
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
        Functions.setConfig("user_address", address);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
        Functions.setConfig("user_id", id);
    }


    public void setName(String name) {
        this.name = name;
        Functions.setConfig("user_name", name);
    }

    public String getName() {
        return name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
        Functions.setConfig("user_surname", surname);
    }

    public String getSurname() {
        return surname;
    }

    public String getTokenKey() {
        return tokenKey == null ? "" : tokenKey;
    }

    public void setTokenKey(String tokenKey) {
        this.tokenKey = tokenKey;
        Functions.setConfig("token_key", tokenKey);
        Log.e("TOK KEY", tokenKey);
    }

    public String getTokenLock() {
        return tokenLock == null ? "" : tokenLock;
    }

    public void setTokenLock(String tokenLock) {
        this.tokenLock = tokenLock;
        Functions.setConfig("token_lock", tokenLock);
        Log.e("TOK LOCK", tokenLock);

    }

    public void setPhone(String phone) {
        this.phone = phone;
        Functions.setConfig("user_phone", phone);

    }

    public void setEmail(String email) {
        this.email = email;
        Functions.setConfig("user_email", email);

    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }
}
