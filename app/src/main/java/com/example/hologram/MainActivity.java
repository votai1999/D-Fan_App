package com.example.hologram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import static com.example.hologram.R.id.connect;

public class MainActivity extends AppCompatActivity {
    final HomeFragment fragmentHome = new HomeFragment();
    final SettingFragment fragmentSetting = new SettingFragment();
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow()
                .getDecorView()
                .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        this.getWindow().getAttributes().flags &= ~WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        setContentView(R.layout.activity_main);
        verifyStoragePermissions(this);
        navigationView = (ChipNavigationBar) findViewById(R.id.bottomNavigati);
        navigationView.setItemSelected(R.id.home);

//        navigationView.setOnNavigationItemSelectedListener(navListener);
        navigationView.setOnItemSelectedListener(navListener);
        fragmentManager.beginTransaction().replace(R.id.fragmentContainer, fragmentHome).commit();
        fragmentManager.beginTransaction().add(R.id.fragmentContainer, fragmentConnect, "2")
                .hide(fragmentConnect)
                .commit();
        fragmentManager.beginTransaction().add(R.id.fragmentContainer, fragmentSetting, "3")
                .hide(fragmentSetting)
                .commit();
    }

    /**
     * Checks if the app has permission to write to device storage
     * <p>
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    private ChipNavigationBar.OnItemSelectedListener navListener = new ChipNavigationBar.OnItemSelectedListener() {
        @Override
        public void onItemSelected(int i) {
            Fragment selectedFragment = null;
            switch (i) {
                case R.id.home:
                    if (active != fragmentHome) {
                        if (active == fragmentSetting) {
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
                case R.id.setting:
                    if (active != fragmentSetting) {
                        fragmentManager.beginTransaction()
                                .setCustomAnimations(R.anim.in_right, R.anim.out_left)
                                .hide(active)
                                .show(fragmentSetting).commit();
                    }
                    active = fragmentSetting;
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