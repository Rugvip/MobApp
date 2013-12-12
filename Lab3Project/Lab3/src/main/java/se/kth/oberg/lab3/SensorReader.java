package se.kth.oberg.lab3;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.preference.PreferenceManager;
import android.util.Log;

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

    public int stringToSensorType(String value) {
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

    public void setListener(SensorReaderListener listener) {
        this.listener = listener;
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
                Log.e("Data", "" + Arrays.toString(sensorEvent.values));
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
            if (running && oldType != sensorType) {
                sensorManager.unregisterListener(this);
                sensorManager.registerListener(this, sensorManager.getDefaultSensor(sensorType), SENSOR_DELAY);
                if (listener != null) {
                    listener.onSensorSwitched();
                }
            }
        }
    }

    public interface SensorReaderListener {
        public void onSensorSwitched();
        public void onSensorValues(float values[]);
    }
}


