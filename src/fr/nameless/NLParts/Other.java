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

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.preference.CheckBoxPreference;
import android.provider.Settings;
import fr.nameless.NLParts.ShellInterface;
import android.app.ProgressDialog;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.util.Log;

import android.widget.Toast;


public class Other extends PreferenceActivity
implements Preference.OnPreferenceChangeListener {
    private static final String TAG = "NLParts";
    private static final String ADB_NOTIFY = "adb_notify";
    private CheckBoxPreference mAdbNotify;
    private static final String ADB_WIFI_PREF = "adb_wifi";
    private CheckBoxPreference mAdbWifiPref;
    private static final String ADB_PORT = "5555";
    private static final String ELECTRON_BEAM_ANIMATION_ON = "electron_beam_animation_on";
    private CheckBoxPreference mElectronBeamAnimationOn;
    private static final String ELECTRON_BEAM_ANIMATION_OFF = "electron_beam_animation_off";
    private CheckBoxPreference mElectronBeamAnimationOff;
    private static final String KILL_APP_LONGPRESS_BACK = "kill_app_longpress_back";
    private CheckBoxPreference mKillAppLongpressBack;

    

    public ProgressDialog patience = null;
    final Handler mHandler = new Handler();
    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.other);
        PreferenceScreen prefSet = getPreferenceScreen();

        /*WIFI ADB*/
        mAdbWifiPref = (CheckBoxPreference) findPreference(ADB_WIFI_PREF);
        mAdbWifiPref.setOnPreferenceChangeListener(this);
        
        /*ADB Notify*/
        mAdbNotify = (CheckBoxPreference) findPreference(ADB_NOTIFY);
        
        /* Electron Beam control */
        boolean animateScreenLights = getResources().getBoolean(
                com.android.internal.R.bool.config_animateScreenLights);
        mElectronBeamAnimationOn = (CheckBoxPreference)prefSet.findPreference(ELECTRON_BEAM_ANIMATION_ON);
        mElectronBeamAnimationOn.setChecked(Settings.System.getInt(getContentResolver(),
                Settings.System.ELECTRON_BEAM_ANIMATION_ON, 0) == 1);
        mElectronBeamAnimationOff = (CheckBoxPreference)prefSet.findPreference(ELECTRON_BEAM_ANIMATION_OFF);
        mElectronBeamAnimationOff.setChecked(Settings.System.getInt(getContentResolver(),
                Settings.System.ELECTRON_BEAM_ANIMATION_OFF, 1) == 1);

        /*Press Back long Kill*/
        mKillAppLongpressBack = (CheckBoxPreference) findPreference(KILL_APP_LONGPRESS_BACK);
        
        /* Hide Electron Beam controls if electron beam is disabled */
        if (animateScreenLights) {
            prefSet.removePreference(mElectronBeamAnimationOn);
            prefSet.removePreference(mElectronBeamAnimationOff);
        }
    }
    
    protected void onResume() {
        super.onResume();
        mAdbNotify.setChecked(Settings.Secure.getInt(getContentResolver(),
         Settings.Secure.ADB_NOTIFY, 1) != 0);
        
        mKillAppLongpressBack.setChecked(Settings.Secure.getInt(getContentResolver(),
                Settings.Secure.KILL_APP_LONGPRESS_BACK, 0) != 0);
    }
    

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        boolean value;
     if (preference == mAdbNotify) {
     Settings.Secure.putInt(getContentResolver(), Settings.Secure.ADB_NOTIFY,
             mAdbNotify.isChecked() ? 1 : 0);
        } else if (preference == mAdbNotify) {
         Settings.Secure.putInt(getContentResolver(), Settings.Secure.ADB_NOTIFY,
         mAdbNotify.isChecked() ? 1 : 0);
            return true;
        } else if (preference == mElectronBeamAnimationOn) {
            value = mElectronBeamAnimationOn.isChecked();
            Settings.System.putInt(getContentResolver(),
                    Settings.System.ELECTRON_BEAM_ANIMATION_ON, value ? 1 : 0);
            return true;
        } else if (preference == mElectronBeamAnimationOff) {
            value = mElectronBeamAnimationOff.isChecked();
            Settings.System.putInt(getContentResolver(),
                    Settings.System.ELECTRON_BEAM_ANIMATION_OFF, value ? 1 : 0);
            return true;
        } else if (preference == mKillAppLongpressBack) {
            Settings.Secure.putInt(getContentResolver(), Settings.Secure.KILL_APP_LONGPRESS_BACK,
                    mKillAppLongpressBack.isChecked() ? 1 : 0);
        }
        return false;
    }
    
     public boolean onPreferenceChange(Preference preference, Object objValue) {
         final String key = preference.getKey();
         if (preference == mAdbWifiPref) {
      boolean have = mAdbWifiPref.isChecked();
      if (!have) {
      WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
      WifiInfo wifiInfo = wifiManager.getConnectionInfo();
      String ipAddress = null;
      if (wifiInfo != null) {
      long addr = wifiInfo.getIpAddress();
      if (addr != 0) {
      // handle negative values whe first octet > 127
      if (addr < 0) addr += 0x100000000L;
      ipAddress = String.format("%d.%d.%d.%d", addr & 0xFF, (addr >> 8) & 0xFF, (addr >> 16) & 0xFF, (addr >> 24) & 0xFF);
      }
      }
      String[] commands = {
      "setprop service.adb.tcp.port " + ADB_PORT,
      "stop adbd",
      "start adbd"
      };
      sendshell(commands, false,
      getResources().getString(R.string.adb_instructions_on)
      .replaceFirst("%ip%", ipAddress)
      .replaceFirst("%P%", ADB_PORT));
      } else {
      String[] commands = {
      "setprop service.adb.tcp.port -1",
      "stop adbd",
      "start adbd"
      };
      sendshell(commands, false, getResources().getString(R.string.adb_instructions_off));
      }
         }
            return true;
    }



           /**
* Methods for popups
*/

         public void toast(final CharSequence message) {
         Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
         toast.show();
         }

         public void toastLong(final CharSequence message) {
         Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
         toast.show();
         }

         public void popup(final String title, final String message) {
         Log.i(TAG, "popup");
         AlertDialog.Builder builder = new AlertDialog.Builder(this);
         builder.setTitle(title)
         .setMessage(message)
         .setCancelable(false)
         .setPositiveButton("OK", new DialogInterface.OnClickListener() {
         public void onClick(DialogInterface dialog, int id) {
         }
         });
         AlertDialog alert = builder.create();
         alert.show();
         }

         final Runnable mNeedReboot = new Runnable() {
         public void run() { needreboot(); }
         };

         public void needreboot() {
         Log.i(TAG, "needreboot");
         AlertDialog.Builder builder = new AlertDialog.Builder(this);
         builder.setMessage("Reboot is requiered to apply. Would you like to reboot now?")
         .setCancelable(false)
         .setPositiveButton(getResources().getString(R.string.yes),
         new DialogInterface.OnClickListener() {
         public void onClick(DialogInterface dialog, int id) {
         String[] commands = { "reboot" };
         sendshell(commands, false, getResources().getString(R.string.rebooting));
         }
         })
         .setNegativeButton(getResources().getString(R.string.no),
         new DialogInterface.OnClickListener() {
         public void onClick(DialogInterface dialog, int id) {
         }
         });
         AlertDialog alert = builder.create();
         alert.show();
         }

         final Runnable mCommandFinished = new Runnable() {
         public void run() { patience.cancel(); }
         };

         public boolean sendshell(final String[] commands, final boolean reboot, final String message) {
         if (message != null)
         patience = ProgressDialog.show(this, "", message, true);
         Thread t = new Thread() {
         public void run() {
         ShellInterface shell = new ShellInterface(commands);
         shell.start();
         while (shell.isAlive())
         {
         if (message != null)
         patience.setProgress(shell.getStatus());
         try {
         Thread.sleep(500);
         }
         catch (InterruptedException e) {
         e.printStackTrace();
         }
         }
         if (message != null)
         mHandler.post(mCommandFinished);
         if (shell.interrupted())
         popup(getResources().getString(R.string.error), getResources().getString(R.string.download_install_error));
         if (reboot == true)
         mHandler.post(mNeedReboot);
         }
         };
         t.start();
         return true;
         }
        

}