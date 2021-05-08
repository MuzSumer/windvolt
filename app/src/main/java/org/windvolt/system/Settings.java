/*
    This file is part of windvolt.

    Copyright (c) 2020 Max Sumer

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package org.windvolt.system;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import org.windvolt.R;

public class Settings extends AppCompatActivity {


    public Settings() {}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.system_settings);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            //actionBar.setDisplayHomeAsUpEnabled(true);

            actionBar.setTitle(getString(R.string.title_settings));
        }

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container_settings, new PreferenceFragment())
                .commit();
    }

    /* --------------------------------windvolt-------------------------------- */

    // preference fragment
    public static class PreferenceFragment extends PreferenceFragmentCompat {

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.preferences, rootKey);

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

            /* hidden since 1.11

            Preference p_location = findPreference("location_input");
            String location = sharedPreferences.getString("location_input", "");
            p_location.setTitle(location);

            Preference p_longitude = findPreference("location_longitude");
            String longitude = sharedPreferences.getString("location_longitude", "");
            p_longitude.setTitle(longitude);

            Preference p_latitude = findPreference("location_latitude");
            String latitude = sharedPreferences.getString("location_latitude", "");
            p_latitude.setTitle(latitude);
             */


        }


    }//PreferenceFragment
}