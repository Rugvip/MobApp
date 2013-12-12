package se.kth.oberg.lab3;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

/**
 * Created by Brickhead on 2013-12-12.
 */
public class Asdasd implements SensorEventListener {
    public static final int SENSOR_TYPE = Sensor.TYPE_LINEAR_ACCELERATION;
    private float x = 0.0f;
    private float y = 0.0f;

    public float getX() {
        return x;
    }

    public void addX(float x) {
        this.x += x;
    }

    public float getY() {
        return y;
    }

    public void addY(float y) {
        this.y += y;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
