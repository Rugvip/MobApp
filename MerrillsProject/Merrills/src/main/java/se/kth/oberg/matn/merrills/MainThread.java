package se.kth.oberg.matn.merrills;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class MainThread extends Thread {
    private SurfaceHolder holder;
    private Board board;

    public MainThread(SurfaceHolder holder, Board board) {
        this.holder = holder;
        this.board = board;
    }

    @Overridek
    public void run() {
        while (true) {
            Canvas canvas = holder.lockCanvas();
            {
                //Paint p = new Paint();
                //p.setColor(0xFF00face);
                //canvas.drawRect(w / 4.0f, h / 4.0f, w / 2.0f, h / 2.0f, p);
                canvas.drawColor(0xFF_FFFFFF);
                board.draw(canvas);
            }
            holder.unlockCanvasAndPost(canvas);

            try {
                Thread.sleep(33);
            } catch (Exception ignored) {
            }
        }
    }
}
