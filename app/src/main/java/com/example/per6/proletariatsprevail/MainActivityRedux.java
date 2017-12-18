package com.example.per6.proletariatsprevail;

import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivityRedux extends AppCompatActivity implements SurfaceHolder.Callback {

    private static final String TAG = "123" ;

    static {System.loadLibrary("opencv_java3");}

    CascadeClassifier face_cascade, eye_cascade;

    CameraBridgeViewBase imga;
    Mat image, gray, resizedCommie;

    Button back, save;
    ImageView imageView;
    TextView testView;

    String filepath="";

    Camera camera;
    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;

    PictureCallback rawCallback;
    ShutterCallback shutterCallback;
    PictureCallback jpegCallback;
    double curTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_redux);

        imageView = (ImageView)findViewById(R.id.image_view);

        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        surfaceHolder = surfaceView.getHolder();

        surfaceHolder.addCallback(this);

        jpegCallback = new PictureCallback() {
            public void onPictureTaken(byte[] data, Camera camera) {
                FileOutputStream outStream = null;
                try {
                    curTime = System.currentTimeMillis();
                    outStream = new FileOutputStream(String.format(getFilesDir().getPath(),
                            curTime));
                    outStream.write(data);
                    outStream.close();
                    Log.d("Log", "onPictureTaken - wrote bytes: " + data.length);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                }
                Toast.makeText(MainActivityRedux.this, "Picture saved", Toast.LENGTH_SHORT).show();
                refreshCamera();
                drawImage();
            }
        };

    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        filepath = image.getAbsolutePath();
        return image;
    }

    private void drawImage() {

        Mat imageRaw = Imgcodecs.imread(String.format(getFilesDir().getPath(), curTime));
        Log.d(TAG, "drawImage: "+imageRaw.toString()+" "+);
        Mat frame_gray = imageRaw.clone();
        Imgproc.cvtColor(imageRaw,frame_gray,Imgproc.COLOR_RGB2GRAY);
        //Mat frame_gray; // Creates matrix frame_gray // Sets frame_gray to grayscale
        Imgproc.equalizeHist(frame_gray, frame_gray); // equalizes histogram of frame_gray
        MatOfRect rect = new MatOfRect(); // creates an new matrix of rectangles rect
        face_cascade.detectMultiScale(frame_gray, rect); // detects faces from the matrix frame_gray
//        Mat marx = Imgcodecs.imread(classifierPath("marx.png",R.raw.marx), Imgcodecs.IMREAD_UNCHANGED); // sets the new matrix marx to the image codec of marx.png
        Mat lord = Imgcodecs.imread(classifierPath("lord.bmp", Imgcodecs.IMREAD_UNCHANGED));
        Mat color = imageRaw; // get color image from camera feed
        Rect[] facesArray = rect.toArray();
        for (int i = 0; i < facesArray.length; i++) {
            Imgproc.rectangle(color, facesArray[i].tl(), facesArray[i].br(), new Scalar(255, 0, 0, 255), 3);
            //marx.copyTo(color);
            Mat resizedCommie = new Mat();
            Imgproc.resize(lord ,resizedCommie,facesArray[i].size());
            Log.d(TAG, "drawImage: "+ resizedCommie.channels() + " " + color.submat(facesArray[i]).channels() + " " + image.channels() + " " + frame_gray.channels());
            Core.addWeighted(resizedCommie,0.5, color.submat(facesArray[i]), 0.5, 0, color.submat(facesArray[i]));

        }
        //.rowRange((int)facesArray[i].tl().y, (int)facesArray[i].br().y).colRange((int)facesArray[i].tl().x, (int)facesArray[i].br().x)
        Bitmap bm = Bitmap.createBitmap(resizedCommie.cols(), resizedCommie.rows(),Bitmap.Config.ALPHA_8);
        Utils.matToBitmap(resizedCommie, bm);

        // find the imageview and draw it!
        imageView.setImageBitmap(bm);
    }

    public String classifierPath(String name, int id){
        InputStream is = getResources().openRawResource(id);
        File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
        File cascadeFile = new File(cascadeDir,name);
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(cascadeFile);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            is.close();
            os.close();
            return cascadeFile.getAbsolutePath();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public void captureImage(View v) throws IOException {
        //take the picture
        camera.takePicture(null, null, jpegCallback);
    }

    public void refreshCamera() {
        if (surfaceHolder.getSurface() == null) {
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try {
            camera.stopPreview();
        } catch (Exception e) {

        }

        // set preview size and make any resize, rotate or
        // reformatting changes here
        // start preview with new settings
        try {
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        } catch (Exception e) {

        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // Now that the size is known, set up the camera parameters and begin
        // the preview.
        refreshCamera();
    }

    public void surfaceCreated(SurfaceHolder holder) {
        try {
            // open the camera
            camera = Camera.open();
        } catch (RuntimeException e) {
            // check for exceptions
            System.err.println(e);
            return;
        }
        Camera.Parameters param;
        param = camera.getParameters();

        // modify parameter
        param.setPreviewSize(352, 288);
        camera.setParameters(param);
        try {
            // The Surface has been created, now tell the camera where to draw
            // the preview.
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        } catch (Exception e) {
            // check for exceptions
            System.err.println(e);
            return;
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // stop preview and release camera
        camera.stopPreview();
        camera.release();
        camera = null;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}