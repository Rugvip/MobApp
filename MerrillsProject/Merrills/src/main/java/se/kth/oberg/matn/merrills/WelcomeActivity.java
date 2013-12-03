package se.kth.oberg.matn.merrills;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class WelcomeActivity extends Activity {
    DatabaseConnection db = new DatabaseConnection(this);
    private Button newGameButton;
    private Button loadGameButton;
    private long continueState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_activity);
    }

    @Override
    protected void onResume() {
        super.onResume();

        continueState = PreferenceManager.getDefaultSharedPreferences(this).getLong("saved_state", 0L);
        if (continueState == 0) {
            Button continueView = (Button) findViewById(R.id.continueGameButton);
            continueView.setEnabled(false);
        }
        Log.e("Wat", "state: " + Long.toBinaryString(continueState));
    }

    public void resetDatabase(View view) {
        db.open();
        db.resetDatabase();
        db.close();
        Toast.makeText(this, "Database reseted", Toast.LENGTH_LONG).show();
    }

    public void newGameListener(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void continueGameListener(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("state", continueState);
        Log.e("Continue", "intent with extra: " + Long.toBinaryString(continueState));
        startActivity(intent);
    }

    public void loadGameListener(View view) {
//        Toast.makeText(this, "You get another toast!", Toast.LENGTH_SHORT).show();
//        Intent intent = new Intent(this, MainActivity.class);
//        intent.putExtra("loadFile", true);
//        startActivity(intent);

        db.open();
        db.saveGame("asdasd", 346346345345L);
        db.saveGame("dasdasda", 246723572457L);
        long save = db.loadGame(1);
        db.close();
        Toast.makeText(this, "LOOL", Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.welcome, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(this,SettingsActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
