package com.softwaremakeinindia.iotcexample1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

public class Scanqr extends AppCompatActivity {


    SurfaceView surfaceView;
    CameraSource cameraSource;
    BarcodeDetector barcodeDetector;
    boolean sent = false;

    SharedPreferences sharedPreferences;
    public static final String MyPREFERENCES = "KeyStore" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanqr);

        sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        surfaceView = findViewById(R.id.camerapreview);

        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.QR_CODE).build();

        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(640, 480).build();

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                try {
                    cameraSource.start(surfaceHolder);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                cameraSource.stop();
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                SparseArray<Barcode> qrCodes = detections.getDetectedItems();
                Vibrator vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                if (qrCodes.size() != 0 && !sent){
                    sent = true;
                    String key = qrCodes.valueAt(0).displayValue;
                    Intent i = new Intent(getApplicationContext(), DeviceControl.class);
                    i.putExtra("key", key);
                    startActivity(i);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("key", key);
                    editor.commit();
                    finish();
                    vibrator.vibrate(100);
                }
            }
        });

    }
}
