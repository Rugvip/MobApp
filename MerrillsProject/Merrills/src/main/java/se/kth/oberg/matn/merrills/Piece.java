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

    public Piece(Drawable drawable, int color) {
        this.drawable = drawable;
        this.color = color;
        //TODO this
        x = 0;
        y = 0;
    }

    public void draw(Canvas canvas, float seven) {
        drawable.setBounds(
                x - (int) (seven / 3.0f),
                y - (int) (seven / 3.0f),
                x + (int) (seven / 3.0f),
                y + (int) (seven / 3.0f));
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
