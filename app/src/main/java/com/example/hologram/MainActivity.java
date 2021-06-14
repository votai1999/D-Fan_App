package com.example.hologram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import java.util.ArrayList;
import java.util.List;

import static com.example.hologram.R.id.connect;

public class MainActivity extends AppCompatActivity {
    final HomeFragment fragmentHome = new HomeFragment();
    final AboutFragment fragmentAbout = new AboutFragment();
    final ConnectFragment fragmentConnect = new ConnectFragment();
    final FragmentManager fragmentManager = getSupportFragmentManager();
    Fragment active = fragmentHome;
    //    BottomNavigationView navigationView;
    ChipNavigationBar navigationView;
    //
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        editor = sharedPreferences.edit();
        int IdItem = (sharedPreferences.getInt("IdItem", 0) == 0) ? R.id.themeLight : sharedPreferences.getInt("IdItem", 0);
        boolean CheckItem = sharedPreferences.getBoolean("CheckItem", true);
        switch (IdItem) {
            case R.id.themeLight:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case R.id.themeDark:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case R.id.themeDefault:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
        }
        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        switch (currentNightMode) {
            case Configuration.UI_MODE_NIGHT_NO:
                // Night mode is not active, we're using the light theme
                getWindow()
                        .getDecorView()
                        .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                break;
            case Configuration.UI_MODE_NIGHT_YES:
                // Night mode is active, we're using dark theme
                getWindow()
                        .getDecorView()
                        .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                break;
        }
        this.getWindow().getAttributes().flags &= ~WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        setContentView(R.layout.activity_main);
        navigationView = (ChipNavigationBar) findViewById(R.id.bottomNavigati);
        navigationView.setItemSelected(R.id.home);
        navigationView.setOnItemSelectedListener(navListener);
        fragmentManager.beginTransaction().replace(R.id.fragmentContainer, fragmentHome).commit();
        fragmentManager.beginTransaction().add(R.id.fragmentContainer, fragmentConnect, "2")
                .hide(fragmentConnect)
                .commit();
        fragmentManager.beginTransaction().add(R.id.fragmentContainer, fragmentAbout, "3")
                .hide(fragmentAbout)
                .commit();
        Log.d(String.valueOf(checkAndRequestPermissions()), "onCreate: ");
    }

    private boolean checkAndRequestPermissions() {
        int permissionSendMessage = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int locationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (locationPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (permissionSendMessage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 1);
            return false;
        }
        return true;
    }

    private ChipNavigationBar.OnItemSelectedListener navListener = new ChipNavigationBar.OnItemSelectedListener() {
        @Override
        public void onItemSelected(int i) {
            Fragment selectedFragment = null;
            switch (i) {
                case R.id.home:
                    if (active != fragmentHome) {
                        if (active == fragmentAbout) {
                            fragmentManager.beginTransaction()
                                    .setCustomAnimations(R.anim.in_left, R.anim.out_right)
                                    .hide(active)
                                    .show(fragmentHome).commit();
                        } else {
                            fragmentManager.beginTransaction()
                                    .setCustomAnimations(R.anim.in_right, R.anim.out_left)
                                    .hide(active)
                                    .show(fragmentHome).commit();
                        }
                    }
                    active = fragmentHome;
                    break;
                case R.id.info:
                    if (active != fragmentAbout) {
                        fragmentManager.beginTransaction()
                                .setCustomAnimations(R.anim.in_right, R.anim.out_left)
                                .hide(active)
                                .show(fragmentAbout).commit();
                    }
                    active = fragmentAbout;
                    break;
                case connect:
                    if (active != fragmentConnect) {
                        fragmentManager.beginTransaction()
                                .setCustomAnimations(R.anim.in_left, R.anim.out_right)
                                .hide(active)
                                .show(fragmentConnect).commit();
                    }
                    active = fragmentConnect;
                    break;
            }
        }
    };


}