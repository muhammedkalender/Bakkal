package com.example.bakkal;

import org.json.JSONObject;

import java.util.HashMap;

public class User {
    private static String tokenLock, tokenKey;

    private static boolean admin = false;
    private static boolean logged = false;

    private static int id;
    private static String name, surname, adress;


    public User() {
        //todo is logged ? check firebase ? yada mysql
    }


    public static boolean login(String username, String password) {
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

                setLogged(true);
            }catch (Exception e){
                return false;
            }

            return true;
        } else {
            return false;
        }
    }

    public static boolean logout() {
        
    }

    public static boolean isLogged() {
        return logged;
    }

    public static void setLogged(boolean logged) {
        User.logged = logged;
    }

    public static boolean isAdmin() {
        return admin;
    }

    public static void setAdmin(boolean admin) {
        User.admin = admin;
    }

    public static String getAdress() {
        return adress;
    }

    public static void setAdress(String adress) {
        User.adress = adress;
    }

    public static int getId() {
        return id;
    }

    public static void setId(int id) {
        User.id = id;
    }


    public static void setName(String name) {
        User.name = name;
    }

    public static String getName() {
        return name;
    }

    public static void setSurname(String surname) {
        User.surname = surname;
    }

    public static String getSurname() {
        return surname;
    }

    public static String getTokenKey() {
        return tokenKey;
    }

    public static void setTokenKey(String tokenKey) {
        User.tokenKey = tokenKey;
    }

    public static String getTokenLock() {
        return tokenLock;
    }

    public static void setTokenLock(String tokenLock) {
        User.tokenLock = tokenLock;
    }
}
