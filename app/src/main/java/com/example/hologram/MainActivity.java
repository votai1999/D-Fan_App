package com.example.hologram;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

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

    ChipNavigationBar navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.getWindow().getAttributes().flags &= ~WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        setContentView(R.layout.activity_main);
        navigationView = findViewById(R.id.bottomNavigati);
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
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[0]), 1);
            return false;
        }
        return true;
    }

    private final ChipNavigationBar.OnItemSelectedListener navListener = i -> {
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
    };


}