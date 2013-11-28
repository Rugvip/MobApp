package se.kth.oberg.matn.merrills;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;

public class Piece {
    private Drawable drawable;
    private float x;
    private float y;
    private float scale = 1.0f;
    private int color;

    public Piece(Drawable drawable) {
        this.drawable = drawable;
        x = 0;
        y = 0;
    }

    public void draw(Canvas canvas, float seven) {
        float one3 = 1.0f / 3.0f;
        drawable.setBounds(
                (int) (scale*(x - one3) * seven),
                (int) (scale*(y - one3) * seven),
                (int) (scale*(x + one3) * seven),
                (int) (scale*(y + one3) * seven));
        drawable.draw(canvas);
    }

    public void animateMove(PointF newPosition) {
        ObjectAnimator animMiddleX = ObjectAnimator.ofFloat(this, "x", 3.5f);
        ObjectAnimator animMiddleY = ObjectAnimator.ofFloat(this, "y", 3.5f);
        ObjectAnimator animTouchX = ObjectAnimator.ofFloat(this, "x", newPosition.x);
        ObjectAnimator animTouchY = ObjectAnimator.ofFloat(this, "y", newPosition.y);

        AnimatorSet set = new AnimatorSet();
        set.play(animMiddleX).with(animMiddleY);
        set.play(animMiddleY).before(animTouchX);
        set.play(animTouchX).with(animTouchY);
        set.setDuration(1000);
        set.start();
    }

    public void animateRemove(){
        ObjectAnimator scaleEnd = ObjectAnimator.ofFloat(this, "scale", 0);
        AnimatorSet set = new AnimatorSet();
        set.play(scaleEnd);
        set.setDuration(1000);
        set.start();
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
