package se.kth.oberg.lab3;

import android.app.Activity;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class FlowerActivity extends Activity {
    private FlowerLeanSensor ffs = new FlowerLeanSensor();
    private FlowerShakeSensor fss = new FlowerShakeSensor();
    private SensorManager mSensorManager;
    private Flower flower;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flower);
        flower = (Flower) findViewById(R.id.flower);
        ffs.setLeanListener(flower);
        fss.setShakeListener(flower);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(ffs, mSensorManager.getDefaultSensor(FlowerLeanSensor.SENSOR_TYPE), SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(fss, mSensorManager.getDefaultSensor(FlowerShakeSensor.SENSOR_TYPE), SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(ffs);
        mSensorManager.unregisterListener(fss);
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
}
