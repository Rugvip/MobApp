package se.kth.oberg.lab3;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Random;

public class Flower extends SurfaceView implements FlowerLeanListener, FlowerShakeListener {
    private static final int STEM_SIZE = 20;
    private static final float STEM_SEGMENT_HEIGHT = 2;
    private static final float STEM_SEGMENT_WIDTH = 0.5f;
    private static final float STEM_SEGMENT_FLEXIBILITY = 1 / 40.0f;
    private static final float PETAL_OFFSET = 6;
    private static final float PETAL_SCALE_X = 0.5f;
    private static final float PETAL_SCALE_Y = 4;
    private static final float DISC_RADIUS = 3;
    private static final int PETAL_COUNT = 27;

    private static Random rand = new Random();

    private final Paint PETAL_PAINT = new Paint();
    private final Paint STEM_PAINT = new Paint();
    private final Paint DISC_PAINT = new Paint();

    private float unit = 0;
    private float angle;
    private Petal[] petals = new Petal[PETAL_COUNT];
    private boolean borked = false;

    public Flower(Context context, AttributeSet attrs) {
        super(context, attrs);

        SurfaceHolder holder = getHolder();
        assert holder != null;

        holder.addCallback(new FlowerPot());
    }

    @Override
    public void onShake() {
        Log.i("Flower", "exterminate");
        if (borked) {
            return;
        }

        float startX = 0;
        float startY = 0;
        float startA = 0;

        for (int i = 0; i < STEM_SIZE; i++) {
            startA -= angle * STEM_SEGMENT_FLEXIBILITY;
            startX += Math.sin(Math.toRadians(startA)) *  STEM_SEGMENT_HEIGHT;
            startY -= Math.cos(Math.toRadians(startA)) *  STEM_SEGMENT_HEIGHT;
        }

        for (int i = 0; i < PETAL_COUNT; i++) {
            petals[i] = new Petal(startX + (float) Math.cos(Math.toRadians(startA)) * PETAL_OFFSET,
                    startY + (float) Math.sin(Math.toRadians(startA)) * PETAL_OFFSET,
                    startA + 90);
            startA += 360.0f / PETAL_COUNT;
        }
        borked = true;
    }


    @Override
    public void onFlex(float angle) {
        this.angle = angle;
    }

    private void drawAPrettyFlower(Canvas canvas) {
        canvas.drawColor(0xFFFFFFFF);

        canvas.translate(canvas.getWidth() / 2, canvas.getHeight());
        canvas.scale(unit, unit);

        canvas.save();

        for (int i = 0; i < STEM_SIZE; i++) {
            canvas.rotate(-angle * STEM_SEGMENT_FLEXIBILITY);
            canvas.drawRect(-STEM_SEGMENT_WIDTH, -STEM_SEGMENT_HEIGHT, STEM_SEGMENT_WIDTH, 0.1f, STEM_PAINT);
            canvas.translate(0, -STEM_SEGMENT_HEIGHT);
        }

        canvas.drawCircle(0, 0, DISC_RADIUS, DISC_PAINT);

        if (borked) {
            canvas.restore();

            for (Petal petal : petals) {
                petal.draw(canvas);
                petal.update();
            }
        } else {
            for (int i = 0; i < PETAL_COUNT; i++) {
                canvas.save();
                {
                    canvas.translate(0, -PETAL_OFFSET);
                    canvas.scale(PETAL_SCALE_X, PETAL_SCALE_Y);
                    canvas.drawRect(-1, -1, 1, 1, PETAL_PAINT);
                }
                canvas.restore();
                canvas.rotate(360.0f / PETAL_COUNT);
            }

            canvas.restore();
        }
    }

    private class Petal {
        private static final float GRAVITY = 0.6f;
        private static final float STRAY_SPIN = 5f;
        private static final float STRAY_X = 0.5f;
        private static final float STRAY_Y = 0.5f;

        private float x;
        private float y;
        private float a;
        private float velA;
        private float velX;
        private float velY;

        private Petal(float x, float y, float a) {
            this.x = x;
            this.y = y;
            this.a = a;
            velA = STRAY_SPIN * (rand.nextFloat() - 0.5f);
            velX = STRAY_X * (rand.nextFloat() - 0.5f);
            velY = STRAY_Y * (rand.nextFloat() - 0.5f);
        }
        private void draw(Canvas canvas) {
            canvas.save();
            {
                canvas.translate(x, y);
                canvas.rotate(a);
                canvas.scale(PETAL_SCALE_X, PETAL_SCALE_Y);
                canvas.drawRect(-1, -1, 1, 1, PETAL_PAINT);
            }
            canvas.restore();
        }
        private void update() {
            if (y < 0) {
                y += GRAVITY * Math.cos(Math.toRadians(angle));
                x += -GRAVITY * Math.sin(Math.toRadians(angle));
                a += velA;
                x += velX;
                y += velY;
                velX /= 1.01f;
                velY /= 1.01f;
                velA /= 1.001f;
            }
        }

        @Override
        public String toString() {
            return "(" + x + ", " + y + ")@" + angle;
        }
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
            unit = height / 60;
            STEM_PAINT.setShader(new LinearGradient(-STEM_SEGMENT_WIDTH, 0, STEM_SEGMENT_WIDTH, 0, new int[]{0xFF229933, 0xFF44CC55, 0xFF229933}, null, Shader.TileMode.CLAMP));
            DISC_PAINT.setShader(new RadialGradient(0, 0, DISC_RADIUS, 0xFFFFCC22, 0xFFFFEE44, Shader.TileMode.CLAMP));
            PETAL_PAINT.setShader(new RadialGradient(0, 0, 1, new int[]{0xFFFF0000, 0}, new float[]{0.99f, 1}, Shader.TileMode.CLAMP));
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
