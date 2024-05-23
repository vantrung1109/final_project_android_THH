package com.example.projectfinaltth.data.ShareRefences;

import android.content.Context;

public class DataLocalManager {
//    private static final String PREF_LIST_HISTORY = "PREF_LIST_HISTORY";
    private static final String PREF_TOKEN = "PREF_TOKEN";
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

//    public static void setListHistories(List<String> listHistory) {
//        Gson gson = new Gson();
//        JsonArray jsonArray = gson.toJsonTree(listHistory).getAsJsonArray();
//        String strJsonArray = jsonArray.toString();
//        DataLocalManager.getInstance().mySharedPreferences.putStringValue(PREF_LIST_HISTORY, strJsonArray);
//    }
//    public static List<String> getListHistories() {
//        String strJsonArray = DataLocalManager.getInstance().mySharedPreferences.getStringValue(PREF_LIST_HISTORY);
//        List<String> listHistories = new ArrayList<>();
//        try {
//            if (strJsonArray.isEmpty()) {
//                return listHistories;
//            }
//            JSONArray jsonArray = new JSONArray(strJsonArray);
//            String string;
//            for (int i = 0; i < jsonArray.length(); i++) {
//                string = jsonArray.getString(i);
//                listHistories.add(string);
//            }
//        } catch (JSONException e) {
//            throw new RuntimeException(e);
//        }
//        return listHistories;
//    }
}
