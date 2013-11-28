package se.kth.oberg.matn.merrills;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

public enum Markers {
    GREEN, BLACK;

    private final Paint paint = new Paint();
    static {
        GREEN.paint.setColor(0xFF00BB00);
        BLACK.paint.setColor(0xFF000000);
    }

    public void draw(Canvas canvas, int mask, float seven) {
        for (int i = 0; i < 24; i++) {
            PointF pos = positions[i];
            if (((1 << i) & mask) != 0) {
                canvas.drawCircle(pos.x * seven, pos.y * seven, seven / 10.0f, paint);
            }
        }
        PointF p = new PointF(0, 0);
    }

    private static final PointF[] positions = new PointF[] {
        new PointF(2.5f, 2.5f), new PointF(1.5f, 1.5f), new PointF(0.5f, 0.5f),
        new PointF(3.5f, 2.5f), new PointF(3.5f, 1.5f), new PointF(3.5f, 0.5f),
        new PointF(4.5f, 2.5f), new PointF(5.5f, 1.5f), new PointF(6.5f, 0.5f),
        new PointF(4.5f, 3.5f), new PointF(5.5f, 3.5f), new PointF(6.5f, 3.5f),
        new PointF(4.5f, 4.5f), new PointF(5.5f, 5.5f), new PointF(6.5f, 6.5f),
        new PointF(3.5f, 4.5f), new PointF(3.5f, 5.5f), new PointF(3.5f, 6.5f),
        new PointF(2.5f, 4.5f), new PointF(1.5f, 5.5f), new PointF(0.5f, 6.5f),
        new PointF(2.5f, 3.5f), new PointF(1.5f, 3.5f), new PointF(0.5f, 3.5f)
    };
}
