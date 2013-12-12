package se.kth.oberg.lab3;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.hardware.SensorManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class Graph extends SurfaceView implements GraphAccelerationListener {
    private SensorManager mSensorManager;
    private SurfaceHolder holder = getHolder();

    private Paint ACCELERATION_BOX_X = new Paint();
    private static final float RECT_SIZE = 15f;

    private float accelerationX, accelerationY, acecelerationZ;

    public Graph(Context context, AttributeSet attrs) {
        super(context, attrs);
        ACCELERATION_BOX_X.setColor(0xFFCCFFCF);
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawColor(0xFFFFFFFF);
        canvas.translate(canvas.getWidth() / 2, canvas.getHeight() / 2);
        canvas.save();
        Log.e("ased", "X: " + accelerationX + "Y: " + accelerationY + "Z: " + acecelerationZ);
        canvas.drawRect((RECT_SIZE / 2) - accelerationX, (RECT_SIZE / 2) - accelerationY, (RECT_SIZE / 2) + accelerationX, (RECT_SIZE / 2) + accelerationY, ACCELERATION_BOX_X);
        canvas.restore();
    }

    @Override
    public void accelerationHappend(float x, float y, float z) {
        this.accelerationX = x;
        this.accelerationY = y;
        this.acecelerationZ = z;
        Canvas canvas = holder.lockCanvas();
        if (canvas != null) {
            draw(canvas);
            holder.unlockCanvasAndPost(canvas);
        }
    }
}
