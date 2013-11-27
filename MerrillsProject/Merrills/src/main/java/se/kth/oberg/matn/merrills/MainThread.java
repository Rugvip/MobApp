package se.kth.oberg.matn.merrills;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import android.util.Property;
import android.view.SurfaceHolder;
import android.view.animation.AnticipateInterpolator;

public class MainThread extends Thread {
    private SurfaceHolder holder;
    private Piece piece;

    public MainThread(SurfaceHolder holder, Piece piece) {
        this.holder = holder;
        this.piece = piece;
    }

    @Override
    public void run() {
        while (true) {
            Canvas canvas = holder.lockCanvas();
            {
                //Paint p = new Paint();
                //p.setColor(0xFF00face);
                //canvas.drawRect(w / 4.0f, h / 4.0f, w / 2.0f, h / 2.0f, p);
                canvas.drawColor(0xFF_FFFFFF);
                piece.draw(canvas);
            }
            holder.unlockCanvasAndPost(canvas);

            try {
                Thread.sleep(33);
            } catch (Exception ignored) {
            }
        }
    }
}
