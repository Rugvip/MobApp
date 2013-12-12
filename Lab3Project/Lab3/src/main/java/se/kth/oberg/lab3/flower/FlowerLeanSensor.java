package se.kth.oberg.lab3.flower;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;

public class FlowerLeanSensor implements SensorEventListener {
    public static final int SENSOR_TYPE = Sensor.TYPE_ACCELEROMETER;

    private static final float ALPHA = 0.9f;

    private float[] samples = new float[]{0.0f, 0.0f, 0.0f};

    private FlowerLeanListener flowerLeanListener;

    public void setLeanListener(FlowerLeanListener flowerLeanListener) {
        this.flowerLeanListener = flowerLeanListener;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        samples[0] = ALPHA * samples[0] + (1 - ALPHA) * sensorEvent.values[0];
        samples[1] = ALPHA * samples[1] + (1 - ALPHA) * sensorEvent.values[1];
        samples[2] = ALPHA * samples[2] + (1 - ALPHA) * sensorEvent.values[2];
//        Log.e("samples", "1: " + sensorEvent.values[0] + " 2: " + sensorEvent.values[1] + " 3: " + sensorEvent.values[2]);

        float angle = (float) (Math.atan2(samples[0], samples[1]) * (180.0 / Math.PI));
        flowerLeanListener.onFlex(angle);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.i("FlowerLeanSensor", "onAccuracyChanged: " + accuracy);
    }
}
