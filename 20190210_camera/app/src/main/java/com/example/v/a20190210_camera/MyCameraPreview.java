package com.example.v.a20190210_camera;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MyCameraPreview extends SurfaceView implements SurfaceHolder.Callback {

    private final static String TAG = "MyCameraPreview";

    private int cameraId;

    private SurfaceHolder mHolder;

    private Camera mCamera;

    private String RAW_EXTENSION = "raw";
    private String JPG_EXTENSION = "jpg";

    public MyCameraPreview(Context context, int cameraId) {
        super(context);
        this.cameraId = cameraId;
        mHolder = getHolder();
        mHolder.addCallback(this);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        mHolder = holder;
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        if (mHolder.getSurface() == null){
            return;
        }
        stopPreview();
        startPreview();
    }

    private void startPreview() {
        Camera camera = Camera.open(this.cameraId);
        mCamera = camera;
        try {
            mCamera.setPreviewDisplay(mHolder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mCamera.setDisplayOrientation(getCameraDisplayOrientation());
        mCamera.startPreview();
    }

    private void stopPreview(){
        if (mCamera != null) {
            mCamera.stopPreview();
        }
    }

    public void onPause(){
        if(mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    public int getCameraDisplayOrientation() {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int rotation = ((Activity)getContext()).getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;
        } else {
            result = (info.orientation - degrees + 360) % 360;
        }
        return result;
    }

    private Camera.ShutterCallback mShutterCallback = new Camera.ShutterCallback() {
        @Override
        public void onShutter() {
            Log.d(TAG, "onShutter: ");
        }
    };
    private Camera.PictureCallback mRawPictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            Log.d(TAG, "Raw onPictureTaken: ");
            if (data != null) {
                saveTakenData(data , RAW_EXTENSION);
            }
        }
    };

    private Camera.PictureCallback mPostViewPictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            Log.d(TAG, "PostView onPictureTaken: ");
            if (data != null) {
                saveTakenData(data , JPG_EXTENSION);
            }
        }
    };

    private Camera.PictureCallback mJpegPictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            Log.d(TAG, "Jpeg onPictureTaken: ");
            if (data != null) {
                saveTakenData(data , JPG_EXTENSION);
            }
            startPreview();
        }
    };

    public void takePicture(){
        mCamera.takePicture(mShutterCallback, mRawPictureCallback , mPostViewPictureCallback , mJpegPictureCallback);
    }


    private void saveTakenData(byte[] data, String extension) {
        SimpleDateFormat date = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String fileName = date.format(Calendar.getInstance().getTime()) + "." + extension;
        FileOutputStream fos = null;
        try {
            fos = getContext().openFileOutput(fileName, Context.MODE_APPEND);
            fos.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            Log.d("Camera", "Done:" + fileName);
        }
    }
}
