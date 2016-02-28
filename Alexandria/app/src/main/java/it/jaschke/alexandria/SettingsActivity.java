package it.jaschke.alexandria;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;

/**
 * Created by saj on 27/01/15.
 */
public class SettingsActivity extends AppCompatPreferenceActivity implements Preference.OnPreferenceChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_start_key)));
    }


    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {
        // Set the summary to the label of the value
        preference.setSummary(getLabelFromPreference(preference, value.toString()));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                // Just pop this activity off the stack
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Attaches a listener so the summary is always updated with the preference value.
     * Also fires the listener once, to initialize the summary (so it shows up before the value
     * is changed.)
     */
    private void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(this);

        // Grab the value from the preference
        String value = PreferenceManager
                .getDefaultSharedPreferences(preference.getContext())
                .getString(preference.getKey(), "");

        // Set the summary to the label of the value
        preference.setSummary(getLabelFromPreference(preference, value));
    }

    /**
     * Gets the label of the preference given that preference's key
     */
    private String getLabelFromPreference(Preference preference, String value) {
        // Get list preference label instead of value
        ListPreference listPreference = (ListPreference) preference;
        int prefIndex = listPreference.findIndexOfValue(value);
        // Check if the entry exists
        if (prefIndex >= 0) {
            return listPreference.getEntries()[prefIndex].toString();
        }
        return null;
    }
}
