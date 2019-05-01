package com.example.bakkal;

import org.json.JSONObject;

import java.util.HashMap;

public class User {
    private String tokenLock, tokenKey;

    private boolean admin = false;
    private boolean logged = false;

    private int id;
    private String name, surname, adress, phone;


    public User() {
        boolean isLogged = Functions.getConfig("is_logged", false);

        if (isLogged) {
            String token = Functions.getConfig("token_key");
            String token_lock = Functions.getConfig("token_lock");

            if (token != null && token_lock != null) {
                if (Functions.isOnline()) {
                    HashMap<String, String> params = new HashMap<>();
                    params.put("method", "post");
                    params.put("req", "login_check");

                    params.put("token_key", Functions.getConfig("token_key"));
                    params.put("token_lock", Functions.getConfig("token_lock"));

                    Functions.WebResult result = Functions.getData(params);

                    if (result.isConnected() && result.isSuccess()) {
                        setId(Functions.getConfig("user_id", 0));
                        setAdmin(Functions.getConfig("user_admin", false));
                        setName(Functions.getConfig("user_name"));
                        setSurname(Functions.getConfig("user_surname"));
                        setAdress(Functions.getConfig("user_address"));
                        setTokenKey(Functions.getConfig("token_key"));
                        setTokenLock(Functions.getConfig("token_lock"));
                        setPhone("user_phone");

                        setLogged(true);
                    } else {
                        setLogged(false);
                    }
                } else {
                    //todo network error ?
                    setLogged(false);
                }
            } else {
                setLogged(false);
            }
        }
        //todo is logged ? check firebase ? yada mysql
    }

    public User(int userId){
        //todo
    }


    public boolean login(String username, String password) {
        HashMap<String, String> params = new HashMap<>();
        params.put("method", "get");
        params.put("cat", "user");

        params.put("user_login", username);
        params.put("user_password", password);

        Functions.WebResult result = Functions.getData(params);

        if (result.isConnected() && result.isSuccess()) {
            try {
                JSONObject user = new JSONObject(result.getData());

                setId(user.getInt("user_id"));
                setAdmin(user.getBoolean("user_admin"));
                setName(user.getString("user_name"));
                setSurname(user.getString("user_surname"));
                setAdress(user.getString("user_address"));
                setTokenKey(user.getString("token_key"));
                setTokenLock(user.getString("token_lock"));
                setPhone("user_phone");

                setLogged(true);
            } catch (Exception e) {
                return false;
            }

            return true;
        } else {
            return false;
        }
    }

    public  boolean logout() {
        //todo
        return false;
    }

    public  boolean isLogged() {
        return this.logged;
    }

    public  void setLogged(boolean logged) {
        this.logged = logged;
        Functions.setConfig("is_logged", this.logged);
    }

    public  boolean isAdmin() {
        return admin;
    }

    public  void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public  String getAdress() {
        return adress;
    }

    public  void setAdress(String adress) {
        this.adress = adress;
    }

    public  int getId() {
        return id;
    }

    public  void setId(int id) {
        this.id = id;
    }


    public  void setName(String name) {
        this.name = name;
    }

    public  String getName() {
        return name;
    }

    public  void setSurname(String surname) {
        this.surname = surname;
    }

    public  String getSurname() {
        return surname;
    }

    public  String getTokenKey() {
        return tokenKey;
    }

    public void setTokenKey(String tokenKey) {
        this.tokenKey = tokenKey;
    }

    public  String getTokenLock() {
        return tokenLock;
    }

    public  void setTokenLock(String tokenLock) {
        this.tokenLock = tokenLock;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }
}
