package com.example.projectfinaltth.data.ShareRefences;

import android.content.Context;

public class DataLocalManager {
    private static final String PREF_TOKEN = "PREF_TOKEN";
    private static final String PREF_CART_ID = "PREF_CART_ID";
    private static DataLocalManager instance;
    private PreferencesService mySharedPreferences;

    public static void init(Context context) {
        instance = new DataLocalManager();
        instance.mySharedPreferences = new PreferencesService(context);
    }

    public static DataLocalManager getInstance() {
        if (instance == null) {
            instance = new DataLocalManager();
        }
        return instance;
    }

    public static void setToken(String token) {
        DataLocalManager.getInstance().mySharedPreferences.putStringValue(PREF_TOKEN, token);
    }

    public static String getToken() {
        return DataLocalManager.getInstance().mySharedPreferences.getStringValue(PREF_TOKEN);
    }

    public static void removeToken() {
        DataLocalManager.getInstance().mySharedPreferences.putStringValue(PREF_TOKEN, null);
    }

    public static void setCartId(String cartId) {
        DataLocalManager.getInstance().mySharedPreferences.putStringValue(PREF_CART_ID, cartId);
    }

    public static String getCartId() {
        return DataLocalManager.getInstance().mySharedPreferences.getStringValue(PREF_CART_ID);
    }

    public static void removeCartId() {
        DataLocalManager.getInstance().mySharedPreferences.putStringValue(PREF_CART_ID, null);
    }
}
