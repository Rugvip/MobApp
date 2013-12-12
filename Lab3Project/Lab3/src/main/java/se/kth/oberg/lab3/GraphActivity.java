package se.kth.oberg.lab3;

import android.app.Activity;
import android.content.Intent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class GraphActivity extends Activity {
    private Graph graph;
    private SensorReader sensorReader;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        graph = (Graph) findViewById(R.id.graph);
        sensorReader = new SensorReader(this);
        sensorReader.setListener(graph);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorReader.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorReader.stop();
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
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
