package com.example.per6.proletariatsprevail;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2{

    private static final String TAG = "123" ;

    static {System.loadLibrary("opencv_java3");}

    CascadeClassifier face_cascade;

    CameraBridgeViewBase imga;
    Mat image, gray;

    Button back, save;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imga = (CameraBridgeViewBase) findViewById(R.id.HelloOpenCvView);
        imga.setVisibility(SurfaceView.VISIBLE);
        imga.setCvCameraViewListener(this);
        imga.enableView();

        back = (Button)findViewById(R.id.back);
        save = (Button)findViewById(R.id.save);
        imageView = (ImageView)findViewById(R.id.image);


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imga.enableView();
                back.setVisibility(View.INVISIBLE);
                save.setVisibility(View.INVISIBLE);
                imageView.setVisibility(View.INVISIBLE);
            }
        });

        imga.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imga.disableView();
                back.setVisibility(View.VISIBLE);
                save.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.VISIBLE);
                drawImage();
            }
        });


        face_cascade = new CascadeClassifier();
        // load cascade file from application resources

        if( !face_cascade.load( classifierPath("haarcascade_frontalface_alt.xml", R.raw.haarcascade_frontalface_alt ))) {
            Log.wtf(TAG, "onCreate: LOAD HAARSCASCADE FAILED!");
        }


    }

    private void drawImage() {
        Mat frame_gray;
        frame_gray = gray;
        Imgproc.equalizeHist(frame_gray, frame_gray);
        MatOfRect rect = new MatOfRect();
        face_cascade.detectMultiScale(frame_gray, rect);

        Mat color = gray;
        Rect[] facesArray = rect.toArray();
        for (int i = 0; i < facesArray.length; i++)
            Imgproc.rectangle(color, facesArray[i].tl(), facesArray[i].br(), new Scalar(255, 0, 0, 255), 3);
        Bitmap bm = Bitmap.createBitmap(color.cols(), color.rows(),Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(color, bm);

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

    @Override
    public void onCameraViewStarted(int width, int height) {

    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
//        Mat frame_gray;
//        frame_gray = inputFrame.gray();
//        Imgproc.equalizeHist(frame_gray, frame_gray);
//        MatOfRect rect = new MatOfRect();
//        face_cascade.detectMultiScale(frame_gray, rect);
//
//        Mat color = inputFrame.rgba();
//        Rect[] facesArray = rect.toArray();
//        for (int i = 0; i < facesArray.length; i++)
//            Imgproc.rectangle(color, facesArray[i].tl(), facesArray[i].br(), new Scalar(255, 0, 0, 255), 3);
        gray = inputFrame.gray();
        image = inputFrame.rgba();
        return image;

    }
}
