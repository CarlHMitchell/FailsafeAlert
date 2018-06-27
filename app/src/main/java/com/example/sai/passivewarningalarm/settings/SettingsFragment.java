package com.example.sai.passivewarningalarm.settings;


import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v4.app.Fragment;

import com.example.sai.passivewarningalarm.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends PreferenceFragment {


    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the settings from the XML resource
        addPreferencesFromResource(R.xml.settings);
    }


    /*
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }
    */

}
