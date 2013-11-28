package se.kth.oberg.matn.merrills;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class WelcomeActivity extends Activity {

    private Button newGameButton;
    private Button loadGameButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_activity);
    }

    public void newGameListener(View view) {
        Toast.makeText(this, "You get a toast!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("loadFile", false);
        startActivity(intent);
    }

    public void loadGameListener(View view) {
//        Toast.makeText(this, "You get another toast!", Toast.LENGTH_SHORT).show();
//        Intent intent = new Intent(this, MainActivity.class);
//        intent.putExtra("loadFile", true);
//        startActivity(intent);
        DatabaseConnection db = new DatabaseConnection(this);
        db.open();
        db.saveGame("asdasd", 00011110, 11100001, 4, 4, 1);
        db.saveGame("dasdasda", 00011110, 11100001, 4, 4, 1);
        SavedGameState save = db.loadGame(1);
        db.close();
        Log.d("lol", "" + save.getName());
        Toast.makeText(this, "LOOL" + save.getName(), Toast.LENGTH_LONG).show();
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
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
