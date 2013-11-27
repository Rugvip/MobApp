package se.kth.oberg.matn.merrills;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

public class Board {
    private Piece[] piece = new Piece[24];
    private GameRules rules;
    private Drawable draw;
    private int size;

    public Board(Drawable draw) {
        this.draw = draw;
    }

    public void draw(Canvas canvas) {
        //TODO get these out of heaaar
        int offsetWidth = (int) (canvas.getWidth() / 20.0);
        int offsetHeight = (int) (canvas.getHeight() / 10.0);
        size = Math.min(canvas.getHeight(), canvas.getWidth());
        if (canvas.getHeight() > canvas.getWidth()) {
            draw.setBounds(
                    offsetWidth,
                    offsetHeight,
                    size - offsetWidth,
                    size);
            draw.draw(canvas);
        } else {
            draw.setBounds(
                    offsetWidth,
                    offsetHeight,
                    size,
                    size - offsetHeight);
            draw.draw(canvas);

        }
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
