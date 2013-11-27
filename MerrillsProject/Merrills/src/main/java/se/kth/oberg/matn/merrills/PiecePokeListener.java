package se.kth.oberg.matn.merrills;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Rugvip on 2013-11-27.
 */
public abstract class PiecePokeListener implements View.OnTouchListener {
    private float boardSize;
    private float boardSize2;
    private float boardSize7;

    public PiecePokeListener(float boardSize) {
        this.boardSize = boardSize;
        boardSize2 = boardSize / 2.0f;
        boardSize7 = boardSize / 7.0f;
    }

    public void setBoardSize(float boardSize) {
        this.boardSize = boardSize;
        boardSize2 = boardSize / 2.0f;
        boardSize7 = boardSize / 7.0f;
    }


    public abstract void onPiecePoke(int id, float pieceX, float pieceY);

    private static final int[][] lookup = new int[][] {
        { 2,-1,-1, 5,-1,-1, 8},
        {-1, 1,-1, 4,-1, 7,-1},
        {-1,-1, 0, 3, 6,-1,-1},
        {23,22,21,-1, 9,10,11},
        {-1,-1,18,15,12,-1,-1},
        {-1,19,-1,16,-1,13,-1},
        {20,-1,-1,17,-1,-1,14},
    };

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getActionMasked() != MotionEvent.ACTION_DOWN) {
            return false;
        }

        /* convert to board coordinates [0,7] */
        float x = (motionEvent.getX() - (view.getWidth() / 2.0f - boardSize2)) / boardSize7;
        float y = (motionEvent.getY() - (view.getHeight() / 2.0f - boardSize2)) /  boardSize7;

        Log.d("Touch", "down at (" + x + ", " + y + ")");

        if (x < 0 || x > boardSize || y < 0 || y > boardSize) {
            return false; /* outside board */
        }

        int xi = (int) x;
        int yi = (int) y;

        int id = lookup[yi][xi];

        if (id < 0) {
            return false; /* invalid rect */
        }

        float xd = x - (xi + 0.5f);
        float yd = y - (yi + 0.5f);

        if (Math.sqrt(xd * xd + yd * yd) > 1.0f) {
            return false; /* not inside touch circle */
        }

        onPiecePoke(id, (xi + 0.5f) * boardSize7, (yi + 0.5f) * boardSize7);

        return false;
    }
}
