package com.yisheng.ysim.main.ulive.preference;


import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.yisheng.ysim.R;


public class SettingsFragment extends PreferenceFragmentCompat {
    public static final String TAG = "SettingsFragment";

    public static SettingsFragment newInstance() {
        SettingsFragment f = new SettingsFragment();
        return f;
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.live_settings);
    }
}
