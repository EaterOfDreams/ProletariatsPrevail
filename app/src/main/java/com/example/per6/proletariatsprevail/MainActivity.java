package com.example.per6.proletariatsprevail;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceView;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

public class MainActivity extends AppCompatActivity  implements CameraBridgeViewBase.CvCameraViewListener2{

    private static final String TAG = "123" ;

    static {System.loadLibrary("opencv_java3");}

    CascadeClassifier face_cascade;

    JavaCameraView cameraView;
    CameraBridgeViewBase imga;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imga = (CameraBridgeViewBase) findViewById(R.id.HelloOpenCvView);
        imga.setVisibility(SurfaceView.VISIBLE);
        imga.setCvCameraViewListener(this);
        imga.enableView();


        face_cascade = new CascadeClassifier();
        if( !face_cascade.load( "haarscascade_frontalface_alt.xml" ) ){
            Log.d(TAG, "onCreate: Load Success");};

    }

    @Override
    public void onCameraViewStarted(int width, int height) {

    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        Mat frame_gray;
        frame_gray = inputFrame.gray();
        Imgproc.equalizeHist(frame_gray, frame_gray);
        MatOfRect rect = new MatOfRect();
        face_cascade.detectMultiScale(frame_gray, rect);

        Mat color = inputFrame.rgba();
        Rect[] facesArray = rect.toArray();
        for (int i = 0; i < facesArray.length; i++)
            Imgproc.rectangle(color, facesArray[i].tl(), facesArray[i].br(), new Scalar(255, 0, 0, 255), 3);


        return color;

    }
}
