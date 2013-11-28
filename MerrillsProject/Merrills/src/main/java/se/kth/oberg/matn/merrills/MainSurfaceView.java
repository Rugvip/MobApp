package se.kth.oberg.matn.merrills;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by Rugvip on 2013-11-27.
 */
public class MainSurfaceView extends SurfaceView implements SurfaceHolder.Callback{
    private Thread mainThread;
    Piece piece;
    BoardView board;
    boolean player = true;

    public MainSurfaceView(Context context) {
        super(context);

        getHolder().addCallback(this);
        Drawable tP = context.getResources().getDrawable(R.drawable.falsepiece);
        Drawable fP = context.getResources().getDrawable(R.drawable.truepiece);
        Drawable b = context.getResources().getDrawable(R.drawable.board);
        board = new BoardView(b,fP,tP);
        mainThread = new MainThread(getHolder(), board);

        setOnTouchListener(new PiecePokeListener() {
            @Override
            public void onPiecePoke(int id, float pieceX, float pieceY) {
                Log.e("poke", "falsepiece: " + id + " x: " + pieceX + " y: " + pieceY);
                try {
                    board.tryPlace(id,player);
                    player = !player;
                } catch (IllegalMoveException e) {
                    e.printStackTrace();
                    Log.e("tryPlace","" + e.toString());
                }
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

}
