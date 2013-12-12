package se.kth.oberg.lab3;

import android.content.Context;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class Flower extends SurfaceView implements FlowerLeanListener {
    private final Drawable stem;
    private final Drawable disc;

    public Flower(Context context, AttributeSet attrs) {
        super(context, attrs);

        SurfaceHolder holder = getHolder();
        assert holder != null;

        stem = context.getResources().getDrawable(R.drawable.stem);
        disc = context.getResources().getDrawable(R.drawable.disc);

        holder.addCallback(new FlowerPot());
    }

    public void exterminate() {
        Log.i("Flower", "exterminate");
    }

    private float angleX;
    private float angleZ;

    @Override
    public void onFlex(float angleX, float angleZ) {
        this.angleX = Math.abs(angleX / 12) ;
        this.angleZ = angleZ / 3;
    }

    private void drawAPrettyFlower(Canvas canvas) {
        int width = canvas.getWidth();
        int height = canvas.getHeight();

        canvas.drawColor(0xFFFFFFFF);

        canvas.translate(width / 2, height);

        {
            Camera camera = new Camera();
            camera.translate(0, height / 4, height / 4);
            camera.rotateX(angleX);
            camera.rotateZ(angleZ);
            camera.applyToCanvas(canvas);
        }

        stem.setBounds(-width / 40, -3 * height / 4, width / 40, 0);
        stem.draw(canvas);

        canvas.translate(0, -3 * height / 4);

        disc.setBounds(-width / 10, - width / 10, width / 10, width / 10);
        disc.draw(canvas);

        for (int i = 0; i < NUM_PETALS; i++) {
            canvas.drawOval(new RectF(-width/40, -width/3, width/40, -width/20), PETAL_PAINT);
            canvas.rotate(360/NUM_PETALS);
        }

        canvas.restore();
    }

    private static final int NUM_PETALS = 15;

    private static final Paint PETAL_PAINT = new Paint(), STEM_PAINT = new Paint(), LEAF_PAINT = new Paint(), DISC_PAINT = new Paint();

    static {
        PETAL_PAINT.setColor(0xFFFF4422);
        STEM_PAINT.setColor(0xFF22AA33);
        LEAF_PAINT.setColor(0xFF44BB55);
        DISC_PAINT.setColor(0xFFCCCC22);
    }

    private class FlowerPot implements SurfaceHolder.Callback {
        private FlowerFiber fiber;

        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            Log.i("FlowerPot", "surfaceCreated");
            fiber = new FlowerFiber(surfaceHolder);
            fiber.start();
        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
            Log.i("FlowerPot", "surfaceChanged, size: " + width + "x" + height + " format: " + format);
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            Log.i("FlowerPot", "surfaceDestroyed");
            fiber.interrupt();
        }
    }

    private class FlowerFiber extends Thread {
        private SurfaceHolder holder;

        private FlowerFiber(SurfaceHolder holder) {
            this.holder = holder;
        }

        @Override
        public void run() {
            while (true) {
                Canvas canvas = holder.lockCanvas();
                if (canvas != null) {
                    drawAPrettyFlower(canvas);
                    holder.unlockCanvasAndPost(canvas);
                }

                try {
                    Thread.sleep(33);
                } catch (InterruptedException e) {
                }
            }
        }
    }
}
