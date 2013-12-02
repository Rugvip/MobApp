package se.kth.oberg.matn.merrills;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import se.kth.oberg.matn.merrills.game.GameState;
import se.kth.oberg.matn.merrills.view.BoardView;

public class MainActivity extends Activity {
    private BoardView view;
    private GameState gameState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        gameState = new GameState(this);
        new GameLogger(gameState);

        view = new BoardView(this, gameState);
        setContentView(view);

        long savedState = 0;

        if (savedInstanceState != null) {
            savedState = savedInstanceState.getLong("saved_state");
            Log.i("Restore", Long.toBinaryString(savedState));
        } else {
            savedState = PreferenceManager.getDefaultSharedPreferences(this).getLong("saved_state", 0L);
            Log.i("Continue", Long.toBinaryString(savedState));
            view.reset();
        }

        if (savedState != 0) {
            gameState.load(savedState);
            view.load(savedState);
        }

        view.setOnTouchListener(new PiecePokeListener() {
            @Override
            public void onPiecePoke(int id, float pieceX, float pieceY) {
                gameState.doPosition(id);
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i("Saved", Long.toBinaryString(gameState.getState()));
        outState.putLong("saved_state", gameState.getState());
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("Activity", "onResume");
    }

    @Override
    protected void onStop() {
        super.onStop();
        PreferenceManager.getDefaultSharedPreferences(this)
                .edit().putLong("saved_state", gameState.getState())
                .apply();
        Log.e("Activity", "onStop");
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveGame("Autosave");
        Log.e("Activity", "onPause");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e("Activity", "onStart");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e("Activity", "onRestart");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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

    private void saveGame(String name) {

    }
}
