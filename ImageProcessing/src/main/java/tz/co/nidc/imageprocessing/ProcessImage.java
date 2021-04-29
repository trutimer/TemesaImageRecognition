package tz.co.nidc.imageprocessing;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.SystemClock;
import android.view.Surface;
import android.widget.Toast;

import org.tensorflow.lite.examples.classification.tflite.Classifier;

import java.io.IOException;
import java.util.List;

public class ProcessImage {

    private Classifier classifier;
    private final Handler handler;
    Integer sensorOrientation;
    private final Activity activity;

    /** Input image size of the model along x axis. */
    private int imageSizeX;
    /** Input image size of the model along y axis. */
    private int imageSizeY;

    public ProcessImage(Activity activity){
        this.activity = activity;

        recreateClassifier(model, device, numThreads);

        HandlerThread handlerThread = new HandlerThread("inference");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
    }

    private ImageResultListener imageResultListener;

    public void getImageResults(ImageResultListener imageResultListener){
        this.imageResultListener = imageResultListener;
    }
    public interface ImageResultListener{
        void getImageResults(List<Classifier.Recognition> results);
    }

    public void processImage(Bitmap bitmap){
        sensorOrientation = 90 - getScreenOrientation();
        Toast.makeText(activity, "Processing...", Toast.LENGTH_SHORT).show();
        runInBackground(
                () -> {
                    final long startTime = SystemClock.uptimeMillis();
                    final List<Classifier.Recognition> results =
                            classifier.recognizeImage(bitmap, sensorOrientation);
                    final long lastProcessingTimeMs = SystemClock.uptimeMillis() - startTime;
                    activity.runOnUiThread(() -> {
                        if (imageResultListener != null){
                            imageResultListener.getImageResults(results);
                        }
                    });
                });
    }

    protected int getScreenOrientation() {
        switch (activity.getWindowManager().getDefaultDisplay().getRotation()) {
            case Surface.ROTATION_270:
                return 270;
            case Surface.ROTATION_180:
                return 180;
            case Surface.ROTATION_90:
                return 90;
            default:
                return 0;
        }
    }

    protected synchronized void runInBackground(final Runnable r) {
        if (handler != null) {
            handler.post(r);
        }
    }

    private void recreateClassifier(Classifier.Model model, Classifier.Device device, int numThreads) {
        if (classifier != null) {
            classifier.close();
            classifier = null;
        }
        if (device == Classifier.Device.GPU
                && (model == Classifier.Model.QUANTIZED_MOBILENET || model == Classifier.Model.QUANTIZED_EFFICIENTNET)) {
            return;
        }
        try {
            classifier = Classifier.create(activity, model, device, numThreads);
        } catch (IOException | IllegalArgumentException e) {
            return;
        }

        // Updates the input image size.
        imageSizeX = classifier.getImageSizeX();
        imageSizeY = classifier.getImageSizeY();
    }

    private final Classifier.Model model = Classifier.Model.FLOAT_EFFICIENTNET;
    private final Classifier.Device device = Classifier.Device.CPU;
    private final int numThreads = 1;
}
