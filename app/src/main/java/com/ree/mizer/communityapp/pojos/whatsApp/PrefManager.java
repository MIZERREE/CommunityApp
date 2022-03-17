package com.ree.mizer.communityapp.pojos.whatsApp;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefManager {
    private static final String ISON = "ISON";
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    public PrefManager(Context context){
        pref = context.getSharedPreferences("MySP",Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void setIsOn(boolean isOn){
        editor.putBoolean(ISON,isOn);
        editor.commit();
    }

    public boolean getIsOn(){
        return pref.getBoolean(ISON, false);
    }
}
