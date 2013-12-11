package se.kth.oberg.lab3;

import android.hardware.SensorEvent;
import android.util.Log;

public class Acceleration {
    private static final int SHAKETRESH_N = 5;
    private static final int SHAKETRESH_V = 10;
    private static final long SHAKETRESH_T = 1000;
    private static final int ANGLETRESH = 5;
    private static final float ALPHA = 0.8f;

    private Callbacks callbacks;
    private float[] samples = new float[]{0.0f, 0.0f, 0.0f};
    private float[] oldSamples = new float[]{0.0f, 0.0f, 0.0f};

    private float[] linearAcceleration = new float[]{0.0f, 0.0f, 0.0f};

    private double angle;
    private double oldAngle;
    private long lastUpdate;
    private int shakeCount = 0;

    public Acceleration(Callbacks callbacks) {
        this.callbacks = callbacks;
    }


    public void accelerator(SensorEvent sensorEvent) {
        samples[0] = ALPHA * oldSamples[0] + (1 - ALPHA) * sensorEvent.values[0];
        samples[1] = ALPHA * oldSamples[1] + (1 - ALPHA) * sensorEvent.values[1];
        samples[2] = ALPHA * oldSamples[2] + (1 - ALPHA) * sensorEvent.values[2];

        linearAcceleration[0] = sensorEvent.values[0] - samples[0];
        linearAcceleration[1] = sensorEvent.values[1] - samples[1];
        linearAcceleration[2] = sensorEvent.values[2] - samples[2];

        float maxAcceleration = Math.max(Math.max(linearAcceleration[0], linearAcceleration[1]), linearAcceleration[2]);
        if (shakeCheck(maxAcceleration)) {
            Log.e("Acceleration", "Shaked");
            shakeCount = 0;
            callbacks.callbackShaked();
        }

        angle = Math.atan2(samples[0], samples[1]) / (Math.PI / 180);
        if ( Math.abs(angle - oldAngle) >= ANGLETRESH  ){
            oldAngle = angle;
            callbacks.callbackAngle(angle);
        }
        oldSamples = samples;
    }

    private boolean shakeCheck(float maxAcceleration) {
        long timeNow = System.currentTimeMillis();
        if (maxAcceleration > SHAKETRESH_V) {
            if ((timeNow - lastUpdate < SHAKETRESH_T)) {
                shakeCount++;
            } else {
                shakeCount = 1;
            }
            lastUpdate = timeNow;
            Log.e("shakeCheck", "shakeCount: " + shakeCount);
        }
        return shakeCount >= SHAKETRESH_N;
    }
}
