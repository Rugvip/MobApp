package se.kth.oberg.matn.merrills;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;

public class Board {
    private Piece[] piece = new Piece[24];
    private GameRules rules;
    private Drawable draw;
    private Dimentionalizer.Dimentionalization diment = new Dimentionalizer.Dimentionalization();
    private int size;

    public Board(Drawable draw) {
        this.draw = draw;
    }

    public void draw(Canvas canvas) {
        Dimentionalizer.dimentionalize(canvas.getWidth(), canvas.getHeight(), diment);
        canvas.translate(diment.getOffsetX(), diment.getOffsetY());
        draw.setBounds(0, 0, diment.getSize(), diment.getSize());
        draw.draw(canvas);
        float size = diment.getSize();

        Paint p = new Paint();
        p.setColor(0xFF000000);
        p.setStrokeWidth(size / 100.0f);
        p.setStyle(Paint.Style.STROKE);
        canvas.drawRect(size/14.0f, size/14.0f, size*13.0f/14.0f, size*13.0f/14.0f, p);
    }

    public void tryMove(int to, int from) {
        //Check if legal, Move in arrayList, animation in piece.move w/e
        if (rules.legalMove(to, from, piece[to].getColor())) {
            movePiece(to, from);
        }
    }

    private void movePiece(int to, int from) {
        piece[to] = piece[from];
        piece[from] = null;
        //       piece[to].animateMove(CoordinateConverter(from));

        if (rules.remove(to)) {
            //Allow user to remove a marker
        }
    }
}
