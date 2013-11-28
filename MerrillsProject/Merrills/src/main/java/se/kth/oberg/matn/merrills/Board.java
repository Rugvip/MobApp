package se.kth.oberg.matn.merrills;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;

public class Board {
    private Piece[] piece = new Piece[24];
    private GameRules rules;
    private Drawable draw;
    private Dimensions dimensions;
    private int size;

    public Board(Drawable draw) {
        this.draw = draw;
    }

    public void draw(Canvas canvas) {
        dimensions = Dimensions.calculate(canvas.getWidth(), canvas.getHeight(), dimensions);
        canvas.translate(dimensions.getOffsetX(), dimensions.getOffsetY());
        draw.setBounds(0, 0, dimensions.getSize(), dimensions.getSize());
        draw.draw(canvas);

        float size = dimensions.getSize();
        float seven = size / 7.0f;
        float seven2 = size / 14.0f;

        Paint p = new Paint();
        p.setColor(0xFF000000);
        p.setStrokeWidth(size / 100.0f);
        p.setStyle(Paint.Style.STROKE);
        canvas.drawRect(size / 14.0f, size / 14.0f, size * 13.0f / 14.0f, size * 13.0f / 14.0f, p);
        canvas.drawRect(size / (14.0f / 3.0f), size / (14.0f / 3.0f), size / (14.0f / 11.0f), size / (14.0f / 11.0f), p);
        canvas.drawRect(size / (14.0f / 5.0f), size / (14.0f / 5.0f), size / (14.0f / 9.0f), size / (14.0f / 9.0f), p);
        canvas.drawLine(size / (14.0f / 7.0f), size / 14.0f, size / (14.0f / 7.0f), size / (14.0f / 5.0f), p);
        canvas.drawLine(size / (14.0f / 9.0f), size / (14.0f / 7.0f), size / (14.0f / 13.0f), size / (14.0f / 7.0f), p);
        canvas.drawLine(size / (14.0f / 7.0f), size / (14.0f / 9.0f), size / (14.0f / 7.0f), size / (14.0f / 13.0f), p);
        canvas.drawLine(size / 14.0f, size / (14.0f / 7.0f), size / (14.0f / 5.0f), size / (14.0f / 7.0f), p);

        Markers.BLACK.draw(canvas, ~0, seven);
        Markers.GREEN.draw(canvas, 0b01100010_11010101_01010110, seven);
    }

    public void tryMove(int to, int from) {
        //Check if legal, Move in arrayList, animation in piece.move w/e
//        if (rules.legalMove(to, from, piece[to].getColor())) {
//            movePiece(to, from);
//        }
    }

    private void movePiece(int to, int from) {
        piece[to] = piece[from];
        piece[from] = null;
        //       piece[to].animateMove(CoordinateConverter(from));

        if (rules.isMillMaker(true, to)) {
            //Allow user to remove a marker
        }
    }
}
