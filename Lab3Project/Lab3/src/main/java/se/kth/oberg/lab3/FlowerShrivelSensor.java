package se.kth.oberg.lab3;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;

public class FlowerShrivelSensor implements SensorEventListener {
    public static final int SENSOR_TYPE = Sensor.TYPE_LINEAR_ACCELERATION;

    private static final int THRESHOLD_MAGNITUDE = 10;
    private static final long THRESHOLD_TIME = 1000;
    private static final float ALPHA = 0.9f;

    private float[] samples = new float[]{0.0f, 0.0f, 0.0f};
    private long startTime;

    private FlowerShrivelListener flowerShrivelListener;

    public FlowerShrivelSensor(FlowerShrivelListener flowerShrivelListener) {
        this.flowerShrivelListener = flowerShrivelListener;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        samples[0] = ALPHA * samples[0] + (1 - ALPHA) * Math.abs(sensorEvent.values[0]);
        samples[1] = ALPHA * samples[1] + (1 - ALPHA) * Math.abs(sensorEvent.values[1]);
        samples[2] = ALPHA * samples[2] + (1 - ALPHA) * Math.abs(sensorEvent.values[2]);

        float max = Math.max(Math.max(samples[0], samples[1]), samples[2]);

        if (max > THRESHOLD_MAGNITUDE) {
            if (System.currentTimeMillis() - startTime > THRESHOLD_TIME) {
                startTime = System.currentTimeMillis();
                flowerShrivelListener.onShrivel();
                Log.e("shakeCheck", "shaken");
            }
        } else {
            startTime = System.currentTimeMillis();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.i("FlowerShrivelSensor", "onAccuracyChanged: " + accuracy);
    }
}
