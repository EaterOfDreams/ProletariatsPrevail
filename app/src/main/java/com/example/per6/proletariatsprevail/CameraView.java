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
        mainCamera.setDisplayOrientation(90);
        //get the holder and set this class as the callback, so we can get camera data here
        mainHolder = getHolder();
        mainHolder.addCallback(this);
        mainHolder.setType(SurfaceHolder.SURFACE_TYPE_NORMAL);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        try{
            //when the surface is created, we can set the camera to draw images in this surfaceholder
            mainCamera.setPreviewDisplay(surfaceHolder);
            mainCamera.startPreview();
        } catch (IOException e) {
            Log.d("ERROR", "Camera error on surfaceCreated " + e.getMessage());
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
        //before changing the application orientation, you need to stop the preview, rotate and then start it again
        if(mainHolder.getSurface() == null)//check if the surface is ready to receive camera data
            return;

        try{
            mainCamera.stopPreview();
        } catch (Exception e){
            //this will happen when you are trying the camera if it's not running
        }

        //now, recreate the camera preview
        try{
            mainCamera.setPreviewDisplay(mainHolder);
            mainCamera.startPreview();
        } catch (IOException e) {
            Log.d("ERROR", "Camera error on surfaceChanged " + e.getMessage());
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        //our app has only one screen, so we'll destroy the camera in the surface
        //if you are unsing with more screens, please move this code your activity
        mainCamera.stopPreview();
        mainCamera.release();
    }
}