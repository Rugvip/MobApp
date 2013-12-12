package se.kth.oberg.lab3;

import android.app.Activity;
import android.hardware.SensorManager;
import android.os.Bundle;

public class GraphActivity extends Activity {
    private Graph graph;
    private GraphAccelerationSensor graphAccelerationSensor = new GraphAccelerationSensor();
    SensorManager mSensorManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        graph = (Graph) findViewById(R.id.graph);
        graphAccelerationSensor.setGraphAccelerationListener(graph);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(graphAccelerationSensor,mSensorManager.getDefaultSensor(GraphAccelerationSensor.SENSOR_TYPE),SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(graphAccelerationSensor);
    }
}
