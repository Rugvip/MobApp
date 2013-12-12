package se.kth.oberg.lab3;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;

public class Graph extends SurfaceView implements SensorReader.SensorReaderListener {
    private SurfaceHolder holder = getHolder();
    private static final int BUFF_SIZE = 500;
    private float[][] graphData = new float[3][BUFF_SIZE];
    private int dataStart = 0;
    private int dataEnd = 0;

    private Paint X_GRAPH = new Paint();
    private Paint Y_GRAPH = new Paint();
    private Paint Z_GRAPH = new Paint();

    List<Paint> paints = new ArrayList<Paint>();

    public Graph(Context context, AttributeSet attrs) {
        super(context, attrs);
        X_GRAPH.setColor(0xFF0000FF);
        Y_GRAPH.setColor(0xFFFF0000);
        Z_GRAPH.setColor(0xFF00FF00);
        paints.add(X_GRAPH);
        paints.add(Y_GRAPH);
        paints.add(Z_GRAPH);
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawColor(0xFFFFFFFF);
        float width = canvas.getWidth();
        float widthPerBuffs = width / BUFF_SIZE;

        canvas.translate(width, canvas.getHeight() / 2);

        canvas.save();
        Log.e("ased", "end: " + dataEnd + "start: " + dataStart);
        for (int j = 0; j< paints.size();j++) {
            int offsetX = 0;
            for (int i = dataEnd; i != dataStart; i--) {
                if (i < 1) {
                    canvas.drawLine(-widthPerBuffs * offsetX, graphData[j][i], -widthPerBuffs * (offsetX + 1), graphData[j][BUFF_SIZE - 1], paints.get(j));
                    i = BUFF_SIZE;
                } else {
                    canvas.drawLine(-widthPerBuffs * offsetX, graphData[j][i], -widthPerBuffs * (offsetX + 1), graphData[j][i - 1], paints.get(j));
                }
                offsetX++;
            }
        }
        canvas.restore();
    }

    @Override
    public void onSensorSwitched() {
        dataStart = 0;
        dataEnd = 0;
    }

    @Override
    public void onSensorValues(float[] values) {
        graphData[0][dataEnd] = values[0];
        graphData[1][dataEnd] = values[1];
        graphData[2][dataEnd] = values[2];
        dataEnd = (dataEnd + 1) % graphData[0].length;
        if (dataEnd == dataStart) {
            dataStart = (dataStart + 1) % graphData[0].length;
        }

        Canvas canvas = holder.lockCanvas();
        if (canvas != null) {
            draw(canvas);
            holder.unlockCanvasAndPost(canvas);
        }
    }
}
