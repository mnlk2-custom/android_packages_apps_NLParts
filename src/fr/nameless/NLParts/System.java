/*
* Copyright (C) 2010 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package fr.nameless.NLParts;

import android.os.Bundle;
import android.os.SystemProperties;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.preference.CheckBoxPreference;
import android.provider.Settings; 
import android.app.ProgressDialog;
import android.os.Handler;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.IOException;


public class System extends PreferenceActivity 
implements Preference.OnPreferenceChangeListener {
    private static final String TAG = "NLParts";
    private static final String UI_EXP_WIDGET = "expanded_widget";
    private CheckBoxPreference mPowerWidget;
    private static final String UI_EXP_WIDGET_COLOR = "expanded_color_mask";
    private Preference mPowerWidgetColor;
    private static final String UI_EXP_WIDGET_PICKER = "widget_picker";
    private PreferenceScreen mPowerPicker;
    private static final String UI_EXP_WIDGET_ORDER = "widget_order";
    private PreferenceScreen mPowerOrder;

    

    public ProgressDialog patience = null;
    final Handler mHandler = new Handler();
    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.system);
        PreferenceScreen prefSet = getPreferenceScreen();

        /*Modversion*/
        setValueSummary("mod_version", "ro.modversion");
        
        /*System Info setting*/
        setStringSummary("device_cpu", getCPUInfo());
        setStringSummary("device_memory", getMemAvail().toString()+" MB / "+getMemTotal().toString()+" MB");        
              
        /* Expanded View Power Widget */
        mPowerWidget = (CheckBoxPreference) prefSet.findPreference(UI_EXP_WIDGET);
        mPowerWidgetColor = prefSet.findPreference(UI_EXP_WIDGET_COLOR);
        mPowerPicker = (PreferenceScreen)prefSet.findPreference(UI_EXP_WIDGET_PICKER);
        mPowerOrder = (PreferenceScreen) prefSet.findPreference(UI_EXP_WIDGET_ORDER);    

        } 

    private void setStringSummary(String preference, String value) {
        try {
            findPreference(preference).setSummary(value);
        } catch (RuntimeException e) {
            findPreference(preference).setSummary(
                getResources().getString(R.string.device_info_default));
        }
    }   
    
    private void setValueSummary(String preference, String property) {
        try {
            findPreference(preference).setSummary(
                    SystemProperties.get(property,
                            getResources().getString(R.string.device_info_default)));
        } catch (RuntimeException e) {

        }
    }    
    
    private Long getMemTotal() {
    	Long total = null;
      BufferedReader reader = null;

      try {
         // Grab a reader to /proc/meminfo
         reader = new BufferedReader(new InputStreamReader(new FileInputStream("/proc/meminfo")), 1000);

         // Grab the first line which contains mem total
         String line = reader.readLine();

         // Split line on the colon, we need info to the right of the colon
         String[] info = line.split(":");

         // We have to remove the kb on the end
         String[] memTotal = info[1].trim().split(" ");

    	// Convert kb into mb
         total = Long.parseLong(memTotal[0]);
         total = total / 1024;
      }
      catch(Exception e) {
         e.printStackTrace();
         // We don't want to return null so default to 0
         total = Long.parseLong("0");
      }
      finally {
         // Make sure the reader is closed no matter what
         try { reader.close(); }
         catch(Exception e) {}
         reader = null;
      }

      return total;
    }

    private Long getMemAvail() {
      Long avail = null;
      BufferedReader reader = null;

      try {
         // Grab a reader to /proc/meminfo
         reader = new BufferedReader(new InputStreamReader(new FileInputStream("/proc/meminfo")), 1000);

    	// This is memTotal which we don't need
         String line = reader.readLine();

         // This is memFree which we need
         line = reader.readLine();
         String[] free = line.split(":");
         // Have to remove the kb on the end
         String [] memFree = free[1].trim().split(" ");

         // This is Buffers which we don't need
         line = reader.readLine();

         // This is Cached which we need
         line = reader.readLine();
         String[] cached = line.split(":");
         // Have to remove the kb on the end
         String[] memCached = cached[1].trim().split(" ");

         avail = Long.parseLong(memFree[0]) + Long.parseLong(memCached[0]);
         avail = avail / 1024;
      }
      catch(Exception e) {
         e.printStackTrace();
         // We don't want to return null so default to 0
         avail = Long.parseLong("0");
      }
      finally {
         // Make sure the reader is closed no matter what
         try { reader.close(); }
         catch(Exception e) {}
         reader = null;
      }

      return avail;
    }

   private String getCPUInfo() {
      String[] info = null;
      BufferedReader reader = null;

      try {
         // Grab a reader to /proc/cpuinfo
        reader = new BufferedReader(new InputStreamReader(new FileInputStream("/proc/cpuinfo")), 1000);

         // Grab a single line from cpuinfo
         String line = reader.readLine();

         // Split on the colon, we need info to the right of colon
         info = line.split(":");
      }
      catch(IOException io) {
         io.printStackTrace();
         info = new String[1];
         info[1] = "error";
      }
      finally {
         // Make sure the reader is closed no matter what
         try { reader.close(); }
         catch(Exception e) {}
         reader = null;
      }

      return info[1];
    }    
    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        boolean value;        
        if (preference == mPowerPicker) {
            startActivity(mPowerPicker.getIntent());
        } else if(preference == mPowerWidget) {
           value = mPowerWidget.isChecked();
            Settings.System.putInt(getContentResolver(),
                    Settings.System.EXPANDED_VIEW_WIDGET, value ? 1 : 0);
        } else   if (preference == mPowerOrder) {
            startActivity(mPowerOrder.getIntent());
        }
        return false;
    }


	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		// TODO Auto-generated method stub
		return false;
	}    	

}