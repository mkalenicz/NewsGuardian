package com.kalenicz.maciej.newsguardian;

import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    public static class NewsPreferenceFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {
        private String mKeyText;
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_main);

            mKeyText = getText(R.string.settings_select_sections_key).toString();

            final MultiSelectListPreference multiPref = (MultiSelectListPreference) findPreference(mKeyText);
            multiPref.setOnPreferenceChangeListener(this);

        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {

            final String key = preference.getKey();
            if (key.equals(mKeyText)) {

                Log.i ("MainPreferenceActivity", "onPreferenceChange" + mKeyText);
                return true;
            } else {
                return false;
            }
        }

    }

}
