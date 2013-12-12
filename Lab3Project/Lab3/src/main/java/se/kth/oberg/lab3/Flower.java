package se.kth.oberg.lab3;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class Flower extends SurfaceView {
    public Flower(Context context, AttributeSet attrs) {
        super(context, attrs);

        SurfaceHolder holder = getHolder();
        assert holder != null;

        holder.addCallback(new FlowerPot());
    }

    public void exterminate() {
        Log.i("Flower", "exterminate");
    }

    public void flex(double angle) {
        Log.i("Flower", "flex: " + angle);
    }

    private class FlowerPot implements SurfaceHolder.Callback {
        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            Log.i("FlowerPot", "surfaceCreated");
        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
            Log.i("FlowerPot", "surfaceChanged, size: " + width + "x" + height  + " format: " + format);
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            Log.i("FlowerPot", "surfaceDestroyed");
        }
    }
}
