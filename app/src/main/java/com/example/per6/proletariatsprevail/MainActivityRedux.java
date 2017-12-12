package com.example.per6.proletariatsprevail;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;

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


public class MainActivityRedux extends AppCompatActivity {

    private static final String TAG = "123" ;

    static {System.loadLibrary("opencv_java3");}

    CascadeClassifier face_cascade, eye_cascade;

    CameraBridgeViewBase imga;
    Mat image, gray, resizedCommie;

    Button back, save;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_redux);
        imageView = (ImageView)findViewById(R.id.img);


    }

    private void drawImage() {
        Mat frame_gray; // Creates matrix frame_gray
        frame_gray = gray; // Sets frame_gray to grayscale
        Imgproc.equalizeHist(frame_gray, frame_gray); // equalizes histogram of frame_gray
        MatOfRect rect = new MatOfRect(); // creates an new matrix of rectangles rect
        face_cascade.detectMultiScale(frame_gray, rect); // detects faces from the matrix frame_gray
        Mat marx = Imgcodecs.imread(classifierPath("marx.png",R.raw.marx), Imgcodecs.IMREAD_UNCHANGED); // sets the new matrix marx to the image codec of marx.png
        Mat lord = Imgcodecs.imread(classifierPath("lord.bmp", Imgcodecs.IMREAD_UNCHANGED));
        Mat color = image; // get color image from camera feed
        Rect[] facesArray = rect.toArray();
        for (int i = 0; i < facesArray.length; i++) {
            Imgproc.rectangle(color, facesArray[i].tl(), facesArray[i].br(), new Scalar(255, 0, 0, 255), 3);
            //marx.copyTo(color);
            Mat resizedCommie = new Mat();
            Imgproc.resize(marx,resizedCommie,facesArray[i].size());
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

}