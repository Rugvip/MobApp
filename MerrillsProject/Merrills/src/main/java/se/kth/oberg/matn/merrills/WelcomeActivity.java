package se.kth.oberg.matn.merrills;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

public class WelcomeActivity extends Activity {
    private long continueState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_activity);
    }

    @Override
    protected void onStart() {
        super.onStart();
        refreshGameList();
    }

    private void refreshGameList() {
        ListView spinner = (ListView) findViewById(R.id.load_spinner);
        final ArrayAdapter<DatabaseConnection.SavedGame> arrayAdapter = new ArrayAdapter<>(this,
                R.layout.list_item, DatabaseConnection.getLoadList(this));
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                DatabaseConnection.SavedGame savedGame = arrayAdapter.getItem(pos);
                Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                intent.putExtra("state", savedGame.getState());
                Log.e("Load", "intent with extra: " + Long.toBinaryString(savedGame.getState()));
                startActivity(intent);
            }
        });
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

    public void resetDatabase(View view) {
        DatabaseConnection.resetDatabase(this);
        refreshGameList();
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
            case R.id.action_new_game:
                Intent intent = new Intent(this,SettingsActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
