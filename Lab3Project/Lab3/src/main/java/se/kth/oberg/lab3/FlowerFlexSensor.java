package se.kth.oberg.lab3;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;

public class FlowerFlexSensor implements SensorEventListener {
    public static final int SENSOR_TYPE = Sensor.TYPE_ACCELEROMETER;

    private static final float ALPHA = 0.8f;

    private float[] samples = new float[]{0.0f, 0.0f, 0.0f};
    private double angle;

    private FlowerFlexListener flowerFlexListener;

    public FlowerFlexSensor(FlowerFlexListener flowerFlexListener) {
        this.flowerFlexListener = flowerFlexListener;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        samples[0] = ALPHA * samples[0] + (1 - ALPHA) * sensorEvent.values[0];
        samples[1] = ALPHA * samples[1] + (1 - ALPHA) * sensorEvent.values[1];
        samples[2] = ALPHA * samples[2] + (1 - ALPHA) * sensorEvent.values[2];
//        Log.e("samples", "1: " + sensorEvent.values[0] + " 2: " + sensorEvent.values[1] + " 3: " + sensorEvent.values[2]);

        angle = Math.atan2(samples[0], samples[1]) * (180.0 / Math.PI);
        flowerFlexListener.onFlex(angle);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.i("FlowerFlexSensor", "onAccuracyChanged: " + accuracy);
    }
}
