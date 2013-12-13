package se.kth.anderslm.noninsensortest;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class Graph extends SurfaceView {
    private static final int MAX_COUNT = 100;
    private static final int BUFF_SIZE = MAX_COUNT + 1;
    private static final int STEP_COUNT = 5;

    private static Paint PAINT_GRAPH = new Paint();
    static {
        PAINT_GRAPH.setColor(0xFFFF0000);
    }

    private static Paint PAINT_GRID = new Paint();
    private static Paint PAINT_TEXT = new Paint();
    static {
        PAINT_GRID.setColor(0x7FFFFFFF);
        PAINT_GRID.setStrokeWidth(1.0f);
        PAINT_TEXT.setColor(0xFFFFFFFF);
        PAINT_TEXT.setTextSize(32.0f);
        PAINT_TEXT.setTextAlign(Paint.Align.CENTER);
    }

    private SurfaceHolder holder = getHolder();
    private float graphData[] = new float[BUFF_SIZE];
    private int count = 0;
    private int offset = 0;
    private float step = 40;

    public Graph(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawColor(0xFF000000);
        float width = canvas.getWidth() * 9 / 10.0f;
        float widthPerBuffs = width / MAX_COUNT;

        float graphScale = ((canvas.getHeight() / (STEP_COUNT + 1) / step));

        canvas.translate(canvas.getWidth(), canvas.getHeight() - step / 2.0f);
        canvas.save();

        for (int i = 0; i < STEP_COUNT + 1; i++) {
            canvas.drawText("" + (i*step), -width - 20.0f, -i * step * graphScale + 12.0f, PAINT_TEXT);
            canvas.drawLine(-width, -i * step * graphScale, 0, -i * step * graphScale, PAINT_GRID);
        }

        for (int i = 0; i < count - 1; ++i) {
            canvas.drawLine(
                    -widthPerBuffs * i, -graphData[wrap(offset - i)] * graphScale,
                    -widthPerBuffs * (i + 1), -graphData[wrap(offset - i - 1)] * graphScale,
                    PAINT_GRAPH);
        }
        canvas.restore();
    }

    public int wrap(int i) {
        i %= BUFF_SIZE;
        if (i < 0) {
            i += BUFF_SIZE;
        }
        return i;
    }

    public void addValue(float value) {
        ++offset;
        if (count < MAX_COUNT) {
            ++count;
        }

        graphData[wrap(offset)] = value;

        Canvas canvas = holder.lockCanvas();
        if (canvas != null) {
            draw(canvas);
            holder.unlockCanvasAndPost(canvas);
        }
    }
}
