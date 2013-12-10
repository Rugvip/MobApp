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
import android.widget.TextView;

public class MainActivity extends Activity implements SensorEventListener {

    private TextView dataOutput;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private float[] samples = new float[]{0.0f,0.0f,0.0f};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dataOutput = (TextView) findViewById(R.id.textview_output);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_UI);
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
        final float alpha = 0.8f;
        samples[0] = alpha * samples[0] + (1-alpha) * sensorEvent.values[0];
        samples[1] = alpha * samples[1] + (1-alpha) * sensorEvent.values[1];
        samples[2] = alpha * samples[2] + (1-alpha) * sensorEvent.values[2];

        Log.e("onSensorChanged", "Values: "
                + samples[0]
                + " 1: " + samples[1]
                + " 2: " + samples[2]
        );
    }
}
