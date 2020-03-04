package com.developer.shams.quizzscanner.UI;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import com.developer.shams.quizzscanner.R;
import com.developer.shams.quizzscanner.Utils.Constants;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;


import butterknife.BindView;
import butterknife.ButterKnife;

public class ScanActivity extends AppCompatActivity {

    @BindView(R.id.camera)
    SurfaceView camera;

    private CameraSource cameraSource;

    private BarcodeDetector barcodeDetector;
    private final int requestpermissionID = 9001;

    private boolean isScanned;


    //ask permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case requestpermissionID:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    try {
                        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        cameraSource.start(camera.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        ButterKnife.bind(this);
        isScanned = true;


        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.QR_CODE).build();

        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setAutoFocusEnabled(true)
                .setRequestedPreviewSize(640, 480).build();


        cameraInitialization();
        barcodeInitialization();

    }






    //initialize barcode Reader
    private void barcodeInitialization() {
        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> qrCodes = detections.getDetectedItems();
                if (qrCodes.size() != 0 && isScanned) {
                    isScanned = false;
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            setVibration();
                            Intent quizIntenet = new Intent(ScanActivity.this, QuizActivity.class);
                            quizIntenet.putExtra(Constants.QUIZ_COUNTER, qrCodes.valueAt(0).displayValue);
                            startActivity(quizIntenet);
                            finish();
                            cameraSource.stop();
                            barcodeDetector.release();
                        }
                    });
                }
            }
        });
    }

    private void setVibration() {
        Vibrator vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(500);
    }


    //initialize camera
    private void cameraInitialization() {
        camera.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ScanActivity.this,
                            new String[]{Manifest.permission.CAMERA}, requestpermissionID
                    );
                    return;
                }
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
                barcodeDetector.release();
            }
        });
    }

    @Override
    protected void onPause() {
        cameraSource.stop();
        barcodeDetector.release();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        cameraSource.stop();
        barcodeDetector.release();
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        cameraSource.stop();
        barcodeDetector.release();
        super.onStop();
    }



}
