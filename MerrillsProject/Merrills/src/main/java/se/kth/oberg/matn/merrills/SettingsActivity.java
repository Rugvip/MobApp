package se.kth.oberg.matn.merrills;

import android.preference.PreferenceActivity;

import java.util.List;


public class SettingsActivity extends PreferenceActivity {

    @Override
    public void onBuildHeaders(List<Header> target) {
        super.onBuildHeaders(target);
        loadHeadersFromResource(R.xml.preference_headers, target);
    }

    @Override
    protected boolean isValidFragment(String fragmentName) {
        return true;
    }
}
