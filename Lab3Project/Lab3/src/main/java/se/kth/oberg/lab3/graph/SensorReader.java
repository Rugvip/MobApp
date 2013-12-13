package se.kth.oberg.lab3.graph;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.preference.PreferenceManager;
import android.util.Log;

import java.text.DecimalFormat;
import java.util.Arrays;

public class SensorReader implements SensorEventListener, SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String SENSOR_SETTING_KEY = "sensor";
    private static final int SENSOR_DELAY = SensorManager.SENSOR_DELAY_UI;
    private SensorReaderListener listener;
    private Sensor sensor;
    private SensorManager sensorManager;
    private SharedPreferences sharedPreferences;
    private int sensorType;
    private boolean running = false;

    public SensorReader(Context context) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sensorType = stringToSensorType(sharedPreferences.getString(SENSOR_SETTING_KEY, "acc"));
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    private int stringToSensorType(String value) {
        switch (value) {
            case "acc":
                return Sensor.TYPE_LINEAR_ACCELERATION;
            case "gyr":
                return Sensor.TYPE_GYROSCOPE;
            case "mag":
                return Sensor.TYPE_MAGNETIC_FIELD;
        }
        return -1;
    }

    public String getDescription() {
        switch (sensorType) {
            case Sensor.TYPE_GYROSCOPE:
                return "Rotation rate in 2π radians/second (2π rad s⁻¹)";
            case Sensor.TYPE_LINEAR_ACCELERATION:
                return "Gravitational force in G (9.81 m s⁻²)";
            case Sensor.TYPE_MAGNETIC_FIELD:
                return "Magnetic field in 100 micro-Tesla (100 uT)";
        }
        return "null";
    }

    public float getStep() {
        switch (sensorType) {
            case Sensor.TYPE_GYROSCOPE:
                return (float) (2 * Math.PI);
            case Sensor.TYPE_LINEAR_ACCELERATION:
                return 9.81f;
            case Sensor.TYPE_MAGNETIC_FIELD:
                return 50;
        }
        return 1;
    }

    public DecimalFormat getFormat() {
        switch (sensorType) {
            case Sensor.TYPE_GYROSCOPE:
                return new DecimalFormat("#.##");
            case Sensor.TYPE_LINEAR_ACCELERATION:
                return new DecimalFormat("#.##");
            case Sensor.TYPE_MAGNETIC_FIELD:
                return new DecimalFormat("###");
        }
        return null;
    }

    public void setListener(SensorReaderListener listener) {
        this.listener = listener;
        listener.onSensorSwitched(getDescription(), getStep(), getFormat());
    }

    public void start() {
        running = true;
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(sensorType), SENSOR_DELAY);
    }

    public void stop() {
        running = false;
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (listener == null) {
            return;
        }

        switch (sensorType) {
            case Sensor.TYPE_GYROSCOPE:
            case Sensor.TYPE_LINEAR_ACCELERATION:
            case Sensor.TYPE_MAGNETIC_FIELD:
                listener.onSensorValues(sensorEvent.values);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.i("SensorReader", "onAccuracyChanged: " + accuracy);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (SENSOR_SETTING_KEY.equals(key)) {
            int oldType = sensorType;
            sensorType = stringToSensorType(sharedPreferences.getString(key, "acc"));
            if (oldType != sensorType) {
                if (running) {
                    sensorManager.unregisterListener(this);
                    sensorManager.registerListener(this, sensorManager.getDefaultSensor(sensorType), SENSOR_DELAY);
                }
                if (listener != null) {
                    listener.onSensorSwitched(getDescription(), getStep(), getFormat());
                }
            }
        }
    }

    public interface SensorReaderListener {
        public void onSensorSwitched(String description, float step, DecimalFormat format);
        public void onSensorValues(float values[]);
    }
}


