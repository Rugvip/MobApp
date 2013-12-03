package se.kth.oberg.matn.merrills;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by Axel on 2013-11-29.
 */
public class SettingsFragmentTwo extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences_two);
    }
}