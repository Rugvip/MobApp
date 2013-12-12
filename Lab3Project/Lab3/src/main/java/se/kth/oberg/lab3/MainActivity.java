package se.kth.oberg.lab3;

import android.app.Activity;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements FlowerFlexListener, FlowerShakeListener {

    private FlowerFlexSensor ffs = new FlowerFlexSensor(this);
    private FlowerShakeSensor fss = new FlowerShakeSensor(this);
    private SensorManager mSensorManager;
    private Flower flower;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        flower = (Flower) findViewById(R.id.flower);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(ffs, mSensorManager.getDefaultSensor(FlowerFlexSensor.SENSOR_TYPE), SensorManager.SENSOR_DELAY_GAME);
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

    @Override
    public void onFlex(double angle) {
        flower.flex(angle);
        progressBar.setProgress((int) angle + 90);
    }

    @Override
    public void onShake() {
        flower.exterminate();
        Toast.makeText(this,"Shaked!",Toast.LENGTH_SHORT).show();
    }
}
