package se.kth.oberg.lab3;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;

public class GraphAccelerationSensor implements SensorEventListener {
    public static final int SENSOR_TYPE = Sensor.TYPE_LINEAR_ACCELERATION;
    public static final float ALPHA = 0.8f;

    GraphAccelerationListener graphAccelerationListener;
    public void setGraphAccelerationListener(GraphAccelerationListener gAL) {
        this.graphAccelerationListener = gAL;
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        graphAccelerationListener.accelerationHappend(x,y,z);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
