package com.example.hologram;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.app.AppCompatDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.lifecycle.ViewModelProvider;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Switch;
import android.widget.TextView;

public class AboutFragment extends Fragment {
    Switch aSwitch;
    LinearLayout linearLayoutTheme;
    PopupMenu popupMenuTheme;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    TextView textViewTheme;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.about_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // TODO: Use the ViewModel
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        editor = sharedPreferences.edit();
        textViewTheme = (TextView) getActivity().findViewById(R.id.textTheme);
        linearLayoutTheme = (LinearLayout) getActivity().findViewById(R.id.linearlayoutTheme);
        textViewTheme.setText(sharedPreferences.getString("TitleTheme", ""));
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
        linearLayoutTheme.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupMenuTheme = new PopupMenu(getContext(), v);
                popupMenuTheme.inflate(R.menu.menu_theme);
                if (IdItem == 0) {
                    popupMenuTheme.getMenu().findItem(R.id.themeDefault).setChecked(true);
                    textViewTheme.setText("Default");
                } else
                    popupMenuTheme.getMenu().findItem((IdItem)).setChecked(CheckItem);
                popupMenuTheme.show();
                popupMenuTheme.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        editor.putInt("IdItem", item.getItemId());
                        editor.putBoolean("CheckItem", item.isCheckable());
                        editor.putString("TitleTheme", (String) item.getTitle());
                        editor.commit();
                        switch (item.getItemId()) {
                            case R.id.themeLight:
                                item.setChecked(!item.isChecked());
                                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                                break;
                            case R.id.themeDark:
                                item.setChecked(!item.isChecked());
                                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                                break;
                            case R.id.themeDefault:
                                item.setChecked(!item.isChecked());
                                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                                break;
                        }
                        return false;
                    }
                });
                return false;
            }
        });
    }
}