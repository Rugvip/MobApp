package se.kth.oberg.matn.merrills;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

public class Piece {
    private Drawable drawable;
    private int x;
    private int y;
    private int width;
    private int height;

    public Piece(Drawable drawable, int width, int height) {
        this.drawable = drawable;
        this.width = width;
        this.height = height;
    }

    public void draw(Canvas canvas) {
        drawable.setBounds(x - width / 2, y - height / 2, x + width / 2, y + height / 2);
        drawable.draw(canvas);
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
