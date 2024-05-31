package com.example.projectfinaltth.data.ShareRefences;

import android.content.Context;

public class DataLocalManager {
    private static final String PREF_TOKEN = "PREF_TOKEN";
    private static final String PREF_CART_ID = "PREF_CART_ID";
    private static final String PREF_EMAIL = "PREF_EMAIL";
    private static final String PREF_PASSWORD = "PREF_PASSWORD";
    private static final String PREF_PROFILE_PICTURE = "PREF_PROFILE_PICTURE";
    private static final String PREF_ROLE = "PREF_ROLE";
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

    public static void setEmail(String email) {
        DataLocalManager.getInstance().mySharedPreferences.putStringValue(PREF_EMAIL, email);
    }

    public static String getEmail() {
        return DataLocalManager.getInstance().mySharedPreferences.getStringValue(PREF_EMAIL);
    }

    public static void setPassword(String password) {
        DataLocalManager.getInstance().mySharedPreferences.putStringValue(PREF_PASSWORD, password);
    }

    public static String getPassword() {
        return DataLocalManager.getInstance().mySharedPreferences.getStringValue(PREF_PASSWORD);
    }

    public static void setProfilePicture(String profilePicture) {
        DataLocalManager.getInstance().mySharedPreferences.putStringValue(PREF_PROFILE_PICTURE, profilePicture);
    }

    public static String getProfilePicture() {
        return DataLocalManager.getInstance().mySharedPreferences.getStringValue(PREF_PROFILE_PICTURE);
    }

    public static void setRole(String role) {
        DataLocalManager.getInstance().mySharedPreferences.putStringValue(PREF_ROLE, role);
    }

    public static String getRole() {
        return DataLocalManager.getInstance().mySharedPreferences.getStringValue(PREF_ROLE);
    }
}
