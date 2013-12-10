package se.kth.oberg.lab3;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements SensorEventListener {
    private static final int SHAKETRESH_N = 5;
    private static final int SHAKETRESH_V = 10;
    private static final long SHAKETRESH_T = 1000;

    private Acceleration acceleration = new Acceleration();

    private TextView dataOutput;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private float[] samples = new float[]{0.0f, 0.0f, 0.0f};
    private float[] oldSamples = new float[]{0.0f, 0.0f, 0.0f};
    private float[] linearAcceleration = new float[]{0.0f, 0.0f, 0.0f};
    private long lastUpdate;
    private double angle;
    private ProgressBar progressBar;
    private int shakeCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dataOutput = (TextView) findViewById(R.id.textview_output);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        switch(sensorEvent.sensor.getType()){
            case Sensor.TYPE_ACCELEROMETER:
            acceleration.
        }
        final float alpha = 0.8f;

        samples[0] = alpha * oldSamples[0] + (1 - alpha) * sensorEvent.values[0];
        samples[1] = alpha * oldSamples[1] + (1 - alpha) * sensorEvent.values[1];
        samples[2] = alpha * oldSamples[2] + (1 - alpha) * sensorEvent.values[2];

        linearAcceleration[0] = sensorEvent.values[0] - samples[0];
        linearAcceleration[1] = sensorEvent.values[1] - samples[1];
        linearAcceleration[2] = sensorEvent.values[2] - samples[2];

        float maxAcceleration = Math.max(Math.max(linearAcceleration[0], linearAcceleration[1]), linearAcceleration[2]);

        if (shakeCheck(maxAcceleration)) {
            Log.e("onSensorChanged", "Shaked");
            Toast.makeText(this, "Don't shake me brah!", Toast.LENGTH_SHORT).show();
            shakeCount = 0;
        }

        angle = Math.atan2(samples[0], samples[1]) / (Math.PI / 180);
//        Log.e("onSensorChanged", "Angle: " + angle);
        progressBar.setProgress((int) angle + 90);
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
