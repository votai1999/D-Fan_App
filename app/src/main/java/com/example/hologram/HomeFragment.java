package com.example.hologram;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.theartofdev.edmodo.cropper.CropImage;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLConnection;


public class HomeFragment extends Fragment {
    Button imageButtonParameter;
    ImageButton buttonChoose;
    ImageView play;
    ImageView previous;
    ImageView next;
    TextView textViewNameFile;

    public static boolean isImageFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith("image");
    }

    public static boolean isVideoFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith("video");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment, container, false);

        textViewNameFile = view.findViewById(R.id.text_file);
        buttonChoose = view.findViewById(R.id.button_Choose_File);
        imageButtonParameter = view.findViewById(R.id.button_setting_parameter);
        play = view.findViewById(R.id.play);
        previous = view.findViewById(R.id.back);
        next = view.findViewById(R.id.next);

        buttonChoose.setOnClickListener(v -> {
            if (OpenCVLoader.initDebug()) {
                CropImage.activity()
                        .setActivityTitle("Crop")
                        .setAspectRatio(1,1)
                        .start(requireContext(), this);
            }else {
                Toast.makeText(requireActivity(), "Wait for open cv", Toast.LENGTH_LONG).show();
            }

        });
        imageButtonParameter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetParameter();
            }
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == Activity.RESULT_OK) {
                Uri resultUri = result.getUri();
                File file = new File(resultUri.getPath());
                textViewNameFile.setText(file.getName());
                Bitmap bmp = null;
                try {
                    bmp = MediaStore.Images.Media.getBitmap(
                            requireActivity().getContentResolver(),
                            resultUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                assert bmp != null;
                Mat image = new Mat(bmp.getWidth(), bmp.getHeight(), CvType.CV_8UC4);
                Utils.bitmapToMat(bmp, image);
                Mat resized = new Mat();
                Size size = new Size(200,200);
                Imgproc.resize(image,resized,size);

                Bitmap previewBitmap = Bitmap.createBitmap(resized.rows(), resized.cols(),
                        Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(resized,previewBitmap);
                Log.e("URI", String.valueOf(resized.cols()));
                Log.e("URI", String.valueOf(resized.rows()));
                resized.convertTo(resized,CvType.CV_64FC3);
                double [] array = new double[(int) resized.total()*resized.channels()];
                resized.get(0,0,array);
                File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator+ "aaa.bin");
                if (!f.exists()) {
                    File parentFile = f.getParentFile();
                    assert parentFile != null;
                    if (!parentFile.exists()) {
                        parentFile.mkdirs();
                    }
                    try {
                        if(f.createNewFile()){
                            Log.e("create",f.getPath());
                        };
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    FileOutputStream fos = new FileOutputStream(f);
                    for (double v : array) {
                        fos.write((int)v);
                    }
                    fos.flush();
                    fos.close();
                    Log.e("path",f.getPath());
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }



    public void BottomSheetParameter() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireActivity(), R.style.BottomSheetDialogTheme);
        View bottomSheetView = LayoutInflater.from(getActivity())
                .inflate(R.layout.bottom_parameter,
                         requireActivity().findViewById(R.id.bottom_sheet));
        bottomSheetView.findViewById(R.id.SendData).setOnClickListener(v -> bottomSheetDialog.dismiss());
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

}