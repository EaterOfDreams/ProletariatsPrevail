package com.example.per6.proletariatsprevail;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

/**
 * Created by Jayden Kemanian on 13/12/2017.
 */

public class CameraView extends SurfaceView implements SurfaceHolder.Callback{

    SurfaceHolder mainHolder;
    Camera mainCamera;

    public CameraView(Context context, Camera camera){
        super(context);
        mainCamera = camera;
        mainCamera.setDisplayOrientation(0);
        mainHolder = getHolder();
        mainHolder.addCallback(this);
//        mainHolder.setType(SurfaceHolder.SURFACE_TYPE_NORMAL);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        try{
            mainCamera.setPreviewDisplay(surfaceHolder);
            mainCamera.startPreview();
        } catch (IOException e) {
            Log.d("ERROR", "Camera error on surfaceCreated " + e.getMessage());
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
        if(mainHolder.getSurface() == null) //check if surface view is ready to receive camera feed
            return;

        try{
            mainCamera.stopPreview();
        } catch (Exception e){

        }

        try{
            mainCamera.setPreviewDisplay(mainHolder);
            mainCamera.startPreview();
        } catch (IOException e) {
            Log.d("ERROR", "Camera error on surfaceChanged " + e.getMessage());
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        mainCamera.stopPreview();
        mainCamera.release();
    }
}