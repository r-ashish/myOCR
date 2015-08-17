/*
 * Copyright 2015 Ashish Ranjan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.ashish.release.myOCR;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.android.ashish.release.myOCR.language.LanguageCodeHelper;


public class settings extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Spinner drop_d = (Spinner)findViewById(R.id.drop_down);
        String[] avail_langs = getApplicationContext().getResources().getStringArray(R.array.iso6393);
        for (int i = 0; i < avail_langs.length; i++) {
            if (avail_langs[i].equals(PreferenceManager
                    .getDefaultSharedPreferences(this)
                    .getString(PreferencesActivity.KEY_SOURCE_LANGUAGE_PREFERENCE,
                            CaptureActivity.DEFAULT_SOURCE_LANGUAGE_CODE))) {
                drop_d.setSelection(i);
                break;
            }
        }
        drop_d.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int pos = ((Spinner) findViewById(R.id.drop_down)).getSelectedItemPosition();
                SharedPreferences pm = PreferenceManager.getDefaultSharedPreferences(parent.getContext());
                String[] lang_codes = getApplicationContext().getResources().getStringArray(R.array.iso6393);
                pm.edit().putString(PreferencesActivity.KEY_SOURCE_LANGUAGE_PREFERENCE, lang_codes[pos]).commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        Switch modeSwitch = ((Switch)findViewById(R.id.mode_switch));
        modeSwitch.setChecked(PreferenceManager
                .getDefaultSharedPreferences(this)
                .getBoolean(PreferencesActivity.KEY_CONTINUOUS_PREVIEW, CaptureActivity.DEFAULT_TOGGLE_CONTINUOUS));
        modeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PreferenceManager.getDefaultSharedPreferences(getParent())
                        .edit().putBoolean(PreferencesActivity.KEY_CONTINUOUS_PREVIEW, isChecked).commit();
            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }
}
