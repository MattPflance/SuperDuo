package it.jaschke.alexandria;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

import it.jaschke.alexandria.CameraPreview.CameraPreview;
import it.jaschke.alexandria.CameraPreview.GraphicOverlay;

/**
 * Activity for the multi-tracker app.  This app detects barcodes and displays the value with the
 * rear facing camera. During detection overlay graphics are drawn to indicate the position,
 * size, and ID of each barcode.
 */
public final class BarcodeReaderActivity extends AppCompatActivity {
    private static final String TAG = "Barcode-reader";

    // intent request code to handle updating play services if needed.
    private static final int RC_HANDLE_GMS = 9001;
    public static final String BarcodeObject = "Barcode";

    private CameraSource mCameraSource;
    private GraphicOverlay<BarcodeGraphic> mGraphicOverlay;
    private CameraPreview mPreview;

    private GestureDetector gestureDetector;
    
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.barcode_capture);

        mPreview = (CameraPreview) findViewById(R.id.camera_preview);
        mGraphicOverlay = (GraphicOverlay<BarcodeGraphic>) findViewById(R.id.graphic_overlay);

        gestureDetector = new GestureDetector(this, new CaptureGestureListener());

        createCameraSource();

    }

    /**
     * Override touch event to activate our gesture listener
     */
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        boolean gesture = gestureDetector.onTouchEvent(e);
        return gesture || super.onTouchEvent(e);
    }


    private void createCameraSource() {
        Context context = getApplicationContext();

        // Set up multiprocessor to track multiple barcodes
        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(context).build();
        BarcodeTrackerFactory barcodeFactory = new BarcodeTrackerFactory(mGraphicOverlay);
        barcodeDetector.setProcessor(new MultiProcessor.Builder<>(barcodeFactory).build());

        if (!barcodeDetector.isOperational()) {
            Toast.makeText(this, R.string.barcode_down, Toast.LENGTH_LONG).show();
        }

        // build camera
        mCameraSource = new CameraSource.Builder(getApplicationContext(), barcodeDetector)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setAutoFocusEnabled(true)
                .setRequestedPreviewSize(1600, 1024)
                .setRequestedFps(15.0f)
                .build();
    }

    /**
     * Restarts the camera.
     */
    @Override
    protected void onResume() {
        super.onResume();
        startCameraSource();
    }

    /**
     * Stops the camera.
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (mPreview != null) {
            mPreview.stop();
        }
    }

    /**
     * Releases the resources associated with the camera source, the associated detectors, and the
     * rest of the processing pipeline.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPreview != null) {
            mPreview.release();
        }
    }

    /**
     * Starts or restarts the camera source, if it exists.  If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     */
    private void startCameraSource() throws SecurityException {
        // check that the device has play services available.
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS).show();
        }

        if (mCameraSource != null) {
            try {
                mPreview.start(mCameraSource);
            } catch (IOException e) {
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }

    /**
     * onTap is called to capture the oldest barcode currently detected and
     * return it to the caller.
     *
     * @param rawX - the raw position of the tap
     * @param rawY - the raw position of the tap.
     * @return true if the activity is ending.
     */
    private boolean onTap(float rawX, float rawY) {

        BarcodeGraphic graphic = mGraphicOverlay.getFirstGraphic();
        Barcode barcode = null;
        if (graphic != null) {
            barcode = graphic.getBarcode();
            if (barcode != null) {
                Intent data = new Intent();
                data.putExtra(BarcodeObject, barcode);
                setResult(CommonStatusCodes.SUCCESS, data);
                finish();
            }
        }
        return barcode != null;
    }


    /*
     * Copyright (C) The Android Open Source Project
     *
     * Licensed under the Apache License, Version 2.0 (the "License");
     * you may not use this file except in compliance with the License.
     * You may obtain a copy of the License at
     *
     *      http://www.apache.org/licenses/LICENSE-2.0
     *
     * Unless required by applicable law or agreed to in writing, software
     * distributed under the License is distributed on an "AS IS" BASIS,
     * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
     * See the License for the specific language governing permissions and
     * limitations under the License.
     */

    /**
     * Factory for creating a tracker and associated graphic to be associated with a new barcode.  The
     * multi-processor uses this factory to create barcode trackers as needed -- one for each barcode.
     */
    class BarcodeTrackerFactory implements MultiProcessor.Factory<Barcode> {
        private GraphicOverlay<BarcodeGraphic> mGraphicOverlay;

        BarcodeTrackerFactory(GraphicOverlay<BarcodeGraphic> barcodeGraphicOverlay) {
            mGraphicOverlay = barcodeGraphicOverlay;
        }

        @Override
        public Tracker<Barcode> create(Barcode barcode) {
            // Found a barcode to use. Make graphic.
            BarcodeGraphic graphic = new BarcodeGraphic(mGraphicOverlay);
            return new BarcodeGraphicTracker(mGraphicOverlay, graphic);
        }

    }

    /**
     * Graphic instance for rendering barcode position, size, and ID within an associated graphic
     * overlay view.
     */
    public class BarcodeGraphic extends GraphicOverlay.Graphic {

        private int mId;

        private final int COLOR_CHOICES[] = {
                Color.BLUE,
                Color.CYAN,
                Color.GREEN
        };

        private int mCurrentColorIndex = 0;

        private Paint mRectPaint;
        private Paint mTextPaint;
        private volatile Barcode mBarcode;

        BarcodeGraphic(GraphicOverlay overlay) {
            super(overlay);

            mCurrentColorIndex = (mCurrentColorIndex + 1) % COLOR_CHOICES.length;
            final int selectedColor = COLOR_CHOICES[mCurrentColorIndex];

            mRectPaint = new Paint();
            mRectPaint.setColor(selectedColor);
            mRectPaint.setStyle(Paint.Style.STROKE);
            mRectPaint.setStrokeWidth(4.0f);

            mTextPaint = new Paint();
            mTextPaint.setColor(selectedColor);
            mTextPaint.setTextSize(36.0f);
        }

        public int getId() {
            return mId;
        }

        public void setId(int id) {
            this.mId = id;
        }

        public Barcode getBarcode() {
            return mBarcode;
        }

        /**
         * Updates the barcode instance from the detection of the most recent frame.  Invalidates the
         * relevant portions of the overlay to trigger a redraw.
         */
        void updateItem(Barcode barcode) {
            mBarcode = barcode;
            postInvalidate();
        }

        /**
         * Draws the barcode annotations for position, size, and raw value on the supplied canvas.
         */
        @Override
        public void draw(Canvas canvas) {
            Barcode barcode = mBarcode;
            if (barcode == null) {
                return;
            }

            // Draws the bounding box around the barcode.
            RectF rect = new RectF(barcode.getBoundingBox());
            rect.left = translateX(rect.left);
            rect.top = translateY(rect.top);
            rect.right = translateX(rect.right);
            rect.bottom = translateY(rect.bottom);
            canvas.drawRect(rect, mRectPaint);

            // Draws a label at the bottom of the barcode indicate the barcode value that was detected.
            canvas.drawText(barcode.rawValue, rect.left, rect.bottom, mTextPaint);
        }
    }

    /**
     * Generic tracker which is used for tracking or reading a barcode (and can really be used for
     * any type of item).  This is used to receive newly detected items, add a graphical representation
     * to an overlay, update the graphics as the item changes, and remove the graphics when the item
     * goes away.
     */
    class BarcodeGraphicTracker extends Tracker<Barcode> {
        private GraphicOverlay<BarcodeGraphic> mOverlay;
        private BarcodeGraphic mGraphic;

        BarcodeGraphicTracker(GraphicOverlay<BarcodeGraphic> overlay, BarcodeGraphic graphic) {
            mOverlay = overlay;
            mGraphic = graphic;
        }

        /**
         * Start tracking the detected item instance within the item overlay.
         */
        @Override
        public void onNewItem(int id, Barcode item) {
            mGraphic.setId(id);
        }

        /**
         * Update the position/characteristics of the item within the overlay.
         */
        @Override
        public void onUpdate(Detector.Detections<Barcode> detectionResults, Barcode item) {
            mOverlay.add(mGraphic);
            mGraphic.updateItem(item);
        }

        /**
         * Hide the graphic when the corresponding object was not detected.  This can happen for
         * intermediate frames temporarily, for example if the object was momentarily blocked from
         * view.
         */
        @Override
        public void onMissing(Detector.Detections<Barcode> detectionResults) {
            mOverlay.remove(mGraphic);
        }

        /**
         * Called when the item is assumed to be gone for good. Remove the graphic annotation from
         * the overlay.
         */
        @Override
        public void onDone() {
            mOverlay.remove(mGraphic);
        }
    }

    private class CaptureGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            return onTap(e.getRawX(), e.getRawY()) || super.onSingleTapConfirmed(e);
        }
    }
}