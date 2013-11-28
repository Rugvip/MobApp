package se.kth.oberg.matn.merrills;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;

public class BoardView {
    private Piece[] piece = new Piece[24];
    private GameRules rules = new GameRules();
    private Drawable backgroundDrawable;
    private Drawable trueDrawable;
    private Drawable falseDrawable;
    private Dimensions dimensions;
    private int size;
    private static Paint boardPaint = new Paint();

    static {
        boardPaint.setColor(0xFF000000);
        boardPaint.setStyle(Paint.Style.STROKE);
    }

    public BoardView(Drawable backgroundDrawable, Drawable falseDrawable, Drawable trueDrawable) {
        this.backgroundDrawable = backgroundDrawable;
        this.falseDrawable = falseDrawable;
        this.trueDrawable = trueDrawable;
    }

    public void draw(Canvas canvas) {
        dimensions = Dimensions.calculate(canvas.getWidth(), canvas.getHeight(), dimensions);
        canvas.translate(dimensions.getOffsetX(), dimensions.getOffsetY());
        backgroundDrawable.setBounds(0, 0, dimensions.getSize(), dimensions.getSize());
        backgroundDrawable.draw(canvas);

        float size = dimensions.getSize();
        float seven = size / 7.0f;
        float seven2 = size / 14.0f;

        boardPaint.setStrokeWidth(size / 100.0f);

        canvas.drawRect(seven2 * 1.0f, seven2 * 1.0f, seven2 * 13.0f, seven2 * 13.0f, boardPaint);
        canvas.drawRect(seven2 * 3.0f, seven2 * 3.0f, seven2 * 11.0f, seven2 * 11.0f, boardPaint);
        canvas.drawRect(seven2 * 5.0f, seven2 * 5.0f, seven2 * 09.0f, seven2 * 09.0f, boardPaint);
        canvas.drawLine(seven2 * 7.0f, seven2 * 1.0f, seven2 * 07.0f, seven2 * 05.0f, boardPaint);
        canvas.drawLine(seven2 * 9.0f, seven2 * 7.0f, seven2 * 13.0f, seven2 * 07.0f, boardPaint);
        canvas.drawLine(seven2 * 7.0f, seven2 * 9.0f, seven2 * 07.0f, seven2 * 13.0f, boardPaint);
        canvas.drawLine(seven2 * 1.0f, seven2 * 7.0f, seven2 * 05.0f, seven2 * 07.0f, boardPaint);

        Markers.BLACK.draw(canvas, ~0, seven);
        for (int index = 0; index < 24; index++) {
            if (null != piece[index]) {
                piece[index].draw(canvas, seven);
            }
        }
    }

    @Deprecated
    public void tryPlace(int to, boolean player) throws IllegalMoveException {
        if (!rules.isFreeSpot(to)) {
            throw new IllegalMoveException("illegal placement!");
        } else {
            if (player) {
                piece[to] = new Piece(trueDrawable);
            } else {
                piece[to] = new Piece(falseDrawable);
            }
            piece[to].animateMove(Dimensions.getPoint(to));
            rules.add(true, to);
        }
    }

    public void placePiece(int to, boolean player) {
        if (player) {
            piece[to] = new Piece(trueDrawable);
        } else {
            piece[to] = new Piece(falseDrawable);
        }
        piece[to].animateMove(Dimensions.getPoint(to));
        rules.add(player, to);
    }

    private void movePiece(int to, int from) {
        piece[to] = piece[from];
        piece[from] = null;
        piece[to].animateMove(Dimensions.getPoint(to));
    }

    private void removePiece(int from){
        piece[from].animateRemove();
    }
}
