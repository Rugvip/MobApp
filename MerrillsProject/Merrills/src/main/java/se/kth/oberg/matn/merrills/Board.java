package se.kth.oberg.matn.merrills;

import android.graphics.Canvas;
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
        draw.setBounds(
                diment.getOffsetX(),
                diment.getOffsetY(),
                diment.getSize() + diment.getOffsetX(),
                diment.getSize() + diment.getOffsetY());
        draw.draw(canvas);

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
