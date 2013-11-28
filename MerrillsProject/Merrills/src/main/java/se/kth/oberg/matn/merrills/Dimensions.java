package se.kth.oberg.matn.merrills;

import android.graphics.PointF;

public class Dimensions {
    private Dimensions() {
    }

    private int size;
    private int offsetX;
    private int offsetY;

    public int getSize() {
        return size;
    }
    public int getOffsetX() {
        return offsetX;
    }
    public int getOffsetY() {
        return offsetY;
    }

    public static Dimensions calculate(int width, int height, Dimensions dimensions) {
        if (dimensions == null) {
            dimensions = new Dimensions();
        }
        float size = width < height ? width * 9.0f / 10.0f : height * 9.0f / 10.0f;
        dimensions.size = (int) size;

        float offsetX = (width - size) / 2.0f;
        dimensions.offsetX = (int) offsetX;

        if (width >= height) {
            float offsetY = (height - size) / 2.0f;
            dimensions.offsetX = (int) offsetY;
        } else {
            dimensions.offsetY = (int) offsetX;
        }

        return dimensions;
    }

    public static PointF getPoint(int index) {
        return positions[index];
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
