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
    private static final int BUFF_SIZE = 100;
    private float[] graphData = new float[BUFF_SIZE];
    private int dataStart = 0;
    private int dataEnd = 0;

    private Paint X_GRAPH = new Paint();
    private static final float RECT_SIZE = 50f;

    private float accelerationX, accelerationY, acecelerationZ;

    public Graph(Context context, AttributeSet attrs) {
        super(context, attrs);
        X_GRAPH.setColor(0xFF0000FF);
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawColor(0xFFFFFFFF);
        float width = canvas.getWidth();
        float widthPerBuffs = width / BUFF_SIZE;

        canvas.translate(width, canvas.getHeight() / 2);

        canvas.save();
        Log.e("ased", "end: " + dataEnd + "start: " + dataStart);
        int offsetX = 0;
        for (int i = dataEnd; i != dataStart; i--) {
            if (i < 1) {
                canvas.drawLine(-widthPerBuffs * offsetX, graphData[i], -widthPerBuffs * (offsetX + 1), graphData[BUFF_SIZE - 1], X_GRAPH);
                i = BUFF_SIZE - 1;
            } else {
                canvas.drawLine(-widthPerBuffs * offsetX, graphData[i], -widthPerBuffs * (offsetX + 1), graphData[i - 1], X_GRAPH);
            }
            offsetX++;
        }
        canvas.restore();
    }

    @Override
    public void accelerationHappend(float x, float y, float z) {
        graphData[dataEnd] = x;
        dataEnd = (dataEnd + 1) % graphData.length;
        if (dataEnd == dataStart) {
            dataStart = (dataStart + 1) % graphData.length;
        }

        this.accelerationX = x;
        Canvas canvas = holder.lockCanvas();
        if (canvas != null) {
            draw(canvas);
            holder.unlockCanvasAndPost(canvas);
        }
    }
}
