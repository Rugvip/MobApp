package se.kth.oberg.matn.merrills;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.EditText;

import android.widget.EditText;
import se.kth.oberg.matn.merrills.game.GameState;
import se.kth.oberg.matn.merrills.view.BoardView;

public class MainActivity extends Activity {
    private BoardView view;
    private GameState gameState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        gameState = new GameState(this);
        new GameLogger(gameState);

        view = new BoardView(this, gameState);
        setContentView(view);

        long state;

        if (savedInstanceState != null) {
            state = savedInstanceState.getLong("saved_state");
            Log.i("Restore", Long.toBinaryString(state));
        } else {
            state = getIntent().getLongExtra("state", 0L);
            Log.i("Continue", Long.toBinaryString(state));
        }

        if (state != 0) {
            gameState.load(state);
            view.load(state);
        } else {
            view.reset();
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
        Log.i("Saved pref", Long.toBinaryString(gameState.getState()));
        Log.e("Activity", "onStop");
    }

    @Override
    protected void onPause() {
        super.onPause();
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
            case R.id.action_new_game:
                finish();
                startActivity(new Intent(this, MainActivity.class));
                return true;
            case R.id.action_save_game:
                final EditText input = new EditText(this);
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Would you like to sef?").setView(input)
                        .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Log.e("action_save_game", "should savegame" + input.getText().toString());
                                DatabaseConnection.saveGame(MainActivity.this,input.getText().toString(),gameState.getState());
                            }
                        })
                        .setNegativeButton("Nah", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Log.e("action_save_game", "Do nothing: should NOT savegame");
                            }
                        });
                builder.create().show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
