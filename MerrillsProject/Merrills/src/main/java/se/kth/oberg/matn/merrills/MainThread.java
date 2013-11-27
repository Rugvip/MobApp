package se.kth.oberg.matn.merrills;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import android.view.SurfaceHolder;

public class MainThread extends Thread {
    private SurfaceHolder holder;

    public MainThread(SurfaceHolder holder) {
        this.holder = holder;
    }

    @Override
    public void run() {
        while (true) {
            Log.e("yes", "yes");
            Canvas canvas = holder.lockCanvas();
            {
                Paint p = new Paint();
                p.setColor(0x00face);
                int w = canvas.getWidth();
                int h = canvas.getHeight();
                canvas.drawRect(w / 4.0f, h / 4.0f, w / 2.0f, h / 2.0f, p);
            }
            holder.unlockCanvasAndPost(canvas);

            try {
                Thread.sleep(1000);
            } catch (Exception ignored) {
            }
        }
    }
}
