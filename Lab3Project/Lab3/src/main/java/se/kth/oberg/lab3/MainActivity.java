package se.kth.oberg.lab3;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import se.kth.oberg.lab3.flower.FlowerActivity;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

    public void glActivity(View view) {
        startActivity(new Intent(this, GraphSurface.class));
    }

    public void flowerActivity(View view) {
        startActivity(new Intent(this, FlowerActivity.class));
    }

    public void graphActivity(View view) {
        startActivity(new Intent(this, GraphActivity.class));
    }
}
