package se.kth.oberg.lab3.flower;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;

public class FlowerShakeSensor implements SensorEventListener {
    public static final int SENSOR_TYPE = Sensor.TYPE_LINEAR_ACCELERATION;

    private static final int THRESHOLD_MAGNITUDE = 8;
    private static final long THRESHOLD_TIME = 1000;
    private static final float ALPHA = 0.9f;

    private float[] samples = new float[]{0.0f, 0.0f, 0.0f};
    private long startTime;

    private FlowerShakeListener flowerShakeListener;

    public void setShakeListener(FlowerShakeListener flowerShakeListener) {
        this.flowerShakeListener = flowerShakeListener;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        samples[0] = ALPHA * samples[0] + (1 - ALPHA) * Math.abs(sensorEvent.values[0]);
        samples[1] = ALPHA * samples[1] + (1 - ALPHA) * Math.abs(sensorEvent.values[1]);
        samples[2] = ALPHA * samples[2] + (1 - ALPHA) * Math.abs(sensorEvent.values[2]);

        float max = Math.max(Math.max(samples[0], samples[1]), samples[2]);

//        Log.i("Sake", "max: " + max);
        if (max > THRESHOLD_MAGNITUDE) {
            if (System.currentTimeMillis() - startTime > THRESHOLD_TIME) {
                startTime = System.currentTimeMillis();
                flowerShakeListener.onShake();
                Log.d("shakeCheck", "shaken");
            }
        } else {
            startTime = System.currentTimeMillis();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.i("FlowerShakeSensor", "onAccuracyChanged: " + accuracy);
    }
}
