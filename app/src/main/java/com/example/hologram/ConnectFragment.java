package com.example.hologram;

import androidx.annotation.UiThread;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.material.textfield.TextInputEditText;

import static android.app.Activity.RESULT_OK;

public class ConnectFragment extends Fragment {

    ImageButton imageButtonVisibility;
    ImageButton imageButtonScanWifi;
    EditText editTextPass;
    EditText editTextSSID;

    public static ConnectFragment newInstance() {
        return new ConnectFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.connect_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        imageButtonVisibility = (ImageButton) getActivity().findViewById(R.id.imageButtonVisibility);
        imageButtonScanWifi = (ImageButton) getActivity().findViewById(R.id.imageScanSSID);
        editTextPass = (EditText) getActivity().findViewById(R.id.editPass);
        editTextSSID = (EditText) getActivity().findViewById(R.id.editSsid);
        Log.d(String.valueOf(editTextPass.getInputType()), "onActivityCreated: ");
        imageButtonVisibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editTextPass.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                    imageButtonVisibility.setImageResource(R.drawable.ic_baseline_visibility_24);
                    editTextPass.setInputType(InputType.TYPE_CLASS_TEXT);
                } else {
                    editTextPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    imageButtonVisibility.setImageResource(R.drawable.ic_baseline_visibility_off_24);
                }
            }
        });
        PopUpScanWifi();
    }



    public void PopUpScanWifi() {
        Intent intent = new Intent(getActivity(), PopUpScanWifi.class);
        imageButtonScanWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                String SSID = data.getStringExtra("SSID");
                editTextSSID.setText(SSID);
            }
        }
    }
}