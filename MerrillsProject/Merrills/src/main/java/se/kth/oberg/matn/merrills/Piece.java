package se.kth.oberg.matn.merrills;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;

public class Piece {
    private Drawable drawable;
    private int x;
    private int y;
    private int width;
    private int height;
    private int color;

    public Piece(Drawable drawable, int width, int height, int color) {
        this.drawable = drawable;
        this.width = width;
        this.height = height;
        this.color = color;
    }

    public void draw(Canvas canvas) {
        drawable.setBounds(x - width / 2, y - height / 2, x + width / 2, y + height / 2);
        drawable.draw(canvas);
    }

    public void animateMove(Point newPosition) {
        ObjectAnimator animMiddleX = ObjectAnimator.ofInt(this, "x", 360);
        ObjectAnimator animMiddleY = ObjectAnimator.ofInt(this, "y", 640);
        ObjectAnimator animTouchX = ObjectAnimator.ofInt(this, "x", (int) newPosition.x);
        ObjectAnimator animTouchY = ObjectAnimator.ofInt(this, "y", (int) newPosition.y);

        AnimatorSet set = new AnimatorSet();
        set.play(animMiddleX).with(animMiddleY);
        set.play(animMiddleY).before(animTouchX);
        set.play(animTouchX).with(animTouchY);
        set.setDuration(1000);
        set.start();
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

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
