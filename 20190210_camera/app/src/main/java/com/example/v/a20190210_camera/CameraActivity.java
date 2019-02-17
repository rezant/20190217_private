package com.example.v.a20190210_camera;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

public class CameraActivity extends AppCompatActivity {

    public static final String CAMERA_ID = "CameraActivity_CAMERA_ID";

    private MyCameraPreview mMyCameraPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        int cameraId = getIntent().getIntExtra(CAMERA_ID, 0);
        mMyCameraPreview = new MyCameraPreview(this , cameraId);
        ((FrameLayout)findViewById(R.id.container_camera)).addView(mMyCameraPreview);

        findViewById(R.id.button_take_picture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMyCameraPreview.takePicture();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMyCameraPreview.onPause();
    }
}
