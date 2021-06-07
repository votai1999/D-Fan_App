package com.example.hologram;

import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;

import static android.app.Activity.RESULT_OK;


public class HomeFragment extends Fragment {
    HomeFragment context = HomeFragment.this;
    String gifUrl = "https://i.kinja-img.com/gawker-media/image/upload/s--B7tUiM5l--/gf2r69yorbdesguga10i.gif";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.home_fragment, container, false);
    }

    ImageButton buttonChoose;
    TextView textViewNameFile;
    ImageView imageViewGif;
    Intent intent;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        textViewNameFile = (TextView) getActivity().findViewById(R.id.text_file);
        buttonChoose = (ImageButton) getActivity().findViewById(R.id.button_Choose_File);
        imageViewGif = (ImageView) getActivity().findViewById(R.id.ImageGif);
        buttonChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("*/*");
                intent.putExtra(Intent.EXTRA_MIME_TYPES,
                        new String[]{"image/jpeg", "image/png", "image/gif", "video/mp4", "video/quicktime"});
//                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, 1);
                getActivity().setResult(Activity.RESULT_OK);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            String pad = data.getData().getPath();
            String buf_path = pad.substring(pad.indexOf(":") + 1, pad.length());
            File dir = Environment.getExternalStorageDirectory();
            String path = dir.getAbsolutePath() + "/" + buf_path;
            Glide
                    .with(context)
                    .load(Uri.fromFile(new File(path)))
                    .apply(RequestOptions.circleCropTransform())
                    .into(imageViewGif);
            File file = new File(pad);
            String name = file.getName();
            textViewNameFile.setText(name);
        }
    }
}