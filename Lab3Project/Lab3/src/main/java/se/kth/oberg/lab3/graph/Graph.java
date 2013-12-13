package se.kth.oberg.lab3.graph;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;
import se.kth.oberg.lab3.R;

import java.text.DecimalFormat;

public class Graph extends SurfaceView implements SensorReader.SensorReaderListener {
    private static final int MAX_COUNT = 100;
    private static final int BUFF_SIZE = MAX_COUNT + 1;
    private static final int STEP_COUNT = 3;

    private static Paint PAINT_GRAPH[] = new Paint[] {new Paint(), new Paint(), new Paint()};
    static {
        PAINT_GRAPH[0].setColor(0xFF_FF0000);
        PAINT_GRAPH[1].setColor(0xFF_00FF00);
        PAINT_GRAPH[2].setColor(0xFF_0000FF);
        for (Paint paint : PAINT_GRAPH) {
            paint.setStrokeWidth(2.0f);
        }
    }

    private static Paint PAINT_GRID = new Paint();
    private static Paint PAINT_TEXT = new Paint();
    static {
        PAINT_GRID.setColor(0x7F_FFFFFF);
        PAINT_GRID.setStrokeWidth(1.0f);
        PAINT_TEXT.setColor(0xFF_FFFFFF);
        PAINT_TEXT.setTextSize(32.0f);
        PAINT_TEXT.setTextAlign(Paint.Align.CENTER);
    }

    private SurfaceHolder holder = getHolder();
    private float[][] graphData = new float[3][BUFF_SIZE];
    private int count = 0;
    private int offset = 0;
    private float step = 1;
    private DecimalFormat format = null;

    private TextView descriptionText;
    private TextView xText;
    private TextView yText;
    private TextView zText;

    public Graph(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void findViews(Activity activity) {
        descriptionText = (TextView) activity.findViewById(R.id.description);
        xText = (TextView) activity.findViewById(R.id.x);
        yText = (TextView) activity.findViewById(R.id.y);
        zText = (TextView) activity.findViewById(R.id.z);
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawColor(0xFF_000000);
        float width = canvas.getWidth() * 9 / 10.0f;
        float widthPerBuffs = width / MAX_COUNT;

        float graphScale = ((canvas.getHeight() / (STEP_COUNT * 2 + 1) / step));

        canvas.translate(canvas.getWidth(), canvas.getHeight() / 2);
        canvas.save();

        for (int i = -STEP_COUNT - 1; i < STEP_COUNT + 1; i++) {
            canvas.drawText("" + i, -width - 20.0f, i * step * graphScale + 12.0f, PAINT_TEXT);
            canvas.drawLine(-width, i * step * graphScale, 0, i * step * graphScale, PAINT_GRID);
        }

        for (int j = 0; j < PAINT_GRAPH.length; ++j) {
            float data[] = graphData[j];
            for (int i = 0; i < count - 1; ++i) {
                canvas.drawLine(
                        -widthPerBuffs * i, data[wrap(offset - i)] * graphScale,
                        -widthPerBuffs * (i + 1), data[wrap(offset - i - 1)] * graphScale,
                        PAINT_GRAPH[j]);
            }
        }
        canvas.restore();
    }

    public void restart() {
        count = 0;
        offset = 0;
    }

    public int wrap(int i) {
        i %= BUFF_SIZE;
        if (i < 0) {
            i += BUFF_SIZE;
        }
        return i;
    }

    @Override
    public void onSensorSwitched(String description, float step, DecimalFormat format) {
        this.step = step;
        this.format = format;
        descriptionText.setText(description);
        restart();
    }

    @Override
    public void onSensorValues(float[] values) {
        ++offset;
        if (count < MAX_COUNT) {
            ++count;
        }

        xText.setText("" + format.format(values[0]));
        yText.setText("" + format.format(values[1]));
        zText.setText("" + format.format(values[2]));

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
