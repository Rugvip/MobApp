package se.kth.oberg.matn.merrills;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

/**
 * Created by Rugvip on 2013-11-27.
 */
public class MainSurfaceView extends SurfaceView implements SurfaceHolder.Callback, View.OnTouchListener {
    private Thread mainThread;
    Piece piece;
    Board board;

    public MainSurfaceView(Context context) {
        super(context);

        getHolder().addCallback(this);
        Drawable d = context.getResources().getDrawable(android.R.drawable.ic_menu_share);
        Drawable b = context.getResources().getDrawable(R.drawable.board);
        piece = new Piece(d, 100, 100, 0xFFFF0000);
        board = new Board(b);
        mainThread = new MainThread(getHolder(), board);
        //mainThread = new MainThread(getHolder(), piece);

        setOnTouchListener(new PiecePokeListener(720) {
            @Override
            public void onPiecePoke(int id, float pieceX, float pieceY) {
                Log.e("poke", "piece: " + id + " x: " + pieceX + " y: " + pieceY);
            }
        });
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        Log.e("Holder.Callback", "surfaceCreated");
        mainThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
        Log.e("Holder.Callback", "surfaceChanged");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        Log.e("Holder.Callback", "surfaceDestroyed");
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getActionMasked() != MotionEvent.ACTION_DOWN) {
            return false;
        }
        ObjectAnimator animMiddleX = ObjectAnimator.ofInt(piece, "x", 360);
        ObjectAnimator animMiddleY = ObjectAnimator.ofInt(piece, "y", 640);
        ObjectAnimator animTouchX = ObjectAnimator.ofInt(piece, "x", (int) motionEvent.getX());
        ObjectAnimator animTouchY = ObjectAnimator.ofInt(piece, "y", (int) motionEvent.getY());

        AnimatorSet set = new AnimatorSet();
        set.play(animMiddleX).with(animMiddleY);
        set.play(animMiddleY).before(animTouchX);
        set.play(animTouchX).with(animTouchY);
        set.setDuration(1000);
        set.start();

        return false;
    }
}
