package com.josephbloxsom.check;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.auth.FirebaseAuth;

public class Settings_Loader {
    SharedPreferences settingsPref;
        public Settings_Loader(Context context){
            settingsPref = context.getSharedPreferences("settings", Context.MODE_PRIVATE); }

        public void setNightModeState(Boolean nightModeState){
            SharedPreferences.Editor editor = settingsPref.edit();
            editor.putBoolean(FirebaseAuth.getInstance().getUid(), nightModeState);
            editor.apply();
        }

        public Boolean loadNightModeState(){
            Boolean state = settingsPref.getBoolean(FirebaseAuth.getInstance().getUid(), false);
            return state;
        }
}
