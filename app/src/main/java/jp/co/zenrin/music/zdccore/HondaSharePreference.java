package jp.co.zenrin.music.zdccore;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import jp.co.zenrin.music.common.HondaConstants;

/**
 * @Author: Hoang Vu
 * @Date: 2017/02/23
 */

public class HondaSharePreference {

    private final String STORAGE = "jp.co.zenrin.music.zdccore.STORAGE";
    private SharedPreferences preferences;
    private Context context;

    /**
     *
     * @param context
     */
    public HondaSharePreference(Context context) {
        this.context = context;
    }

    /**
     * store track list to preference
     * @param arrayList
     */
    public void storeTrackList(ArrayList<Track> arrayList) {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(arrayList);
        editor.putString(HondaConstants.PREFERENCE_TRACK_LIST, json);
        editor.apply();
    }

    /**
     * Load track from preference
     * @return
     */
    public ArrayList<Track> loadTrackList() {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = preferences.getString(HondaConstants.PREFERENCE_TRACK_LIST, null);
        Type type = new TypeToken<ArrayList<Track>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    /**
     * Store index of track
     * @param index
     */
    public void storeTrackIndex(int index) {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(HondaConstants.PREFERENCE_TRACK_INDEX, index);
        editor.apply();
    }

    /**
     * Load index of track
     * @return
     */
    public int loadTrackIndex() {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        //return -1 if no data found
        return preferences.getInt(HondaConstants.PREFERENCE_TRACK_INDEX, -1);
    }

    /**
     * Clear preference
     */
    public void clearCachedTrackPlayList() {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }

    public void storeMPLServiceStatus(boolean serviceBound) {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(HondaConstants.PREFERENCE_MPL_SERVICE_STATUS, serviceBound);
        editor.apply();
    }
    public boolean loadMPLServiceStatus() {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        return preferences.getBoolean(HondaConstants.PREFERENCE_MPL_SERVICE_STATUS,false);
    }
}
