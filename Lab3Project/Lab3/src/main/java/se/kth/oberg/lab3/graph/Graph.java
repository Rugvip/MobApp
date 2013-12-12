package se.kth.oberg.lab3.graph;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;

public class Graph extends SurfaceView implements SensorReader.SensorReaderListener {
    private SurfaceHolder holder = getHolder();
    private static final int MAX_COUNT = 100;
    private static final int BUFF_SIZE = MAX_COUNT + 1;
    private static final float MAX_SENSOR_VALUE = 50;
    private float[][] graphData = new float[3][BUFF_SIZE];
    private int count = 0;
    private int offset = 0;

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
        for (Paint paint : paints) {
            paint.setStrokeWidth(2.0f);
        }
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawColor(0xFF000000);
        float width = canvas.getWidth();
        float widthPerBuffs = width / MAX_COUNT;
        float graphScale = (canvas.getHeight() / 2.0f) / MAX_SENSOR_VALUE;

        canvas.translate(width, canvas.getHeight() / 2);

        canvas.save();
        for (int j = 0; j < paints.size(); ++j) {
            float data[] = graphData[j];
            for (int i = 0; i < count - 1; ++i) {
                canvas.drawLine(
                        -widthPerBuffs * i, data[wrap(offset - i)] * graphScale,
                        -widthPerBuffs * (i + 1), data[wrap(offset - i - 1)] * graphScale,
                        paints.get(j));
            }
        }
        canvas.restore();
    }

    public void restart() {
        count = 0;
        offset = 0;
    }

    @Override
    public void onSensorSwitched() {
        restart();
    }

    public int wrap(int i) {
        i %= BUFF_SIZE;
        if (i < 0) {
            i += BUFF_SIZE;
        }
        return i;
    }

    @Override
    public void onSensorValues(float[] values) {
        ++offset;
        if (count < MAX_COUNT) {
            ++count;
        }

        int i = wrap(offset);
        graphData[0][i] = values[0];
        graphData[1][i] = values[1];
        graphData[2][i] = values[2];

        Canvas canvas = holder.lockCanvas();
        if (canvas != null) {
            draw(canvas);
            holder.unlockCanvasAndPost(canvas);
        }
    }
}
