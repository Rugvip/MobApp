package se.kth.oberg.matn.merrills;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by Rugvip on 2013-11-27.
 */
public class MainSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    private Thread mainThread;
    private Drawable darw;

    public MainSurfaceView(Context context) {
        super(context);

        getHolder().addCallback(this);
        mainThread = new MainThread(getHolder());
        // darw = context.getResources().getDrawable(android.R.drawable.ic_menu_share);
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
