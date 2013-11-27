package se.kth.oberg.matn.merrills;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

public class Board {
    private Piece[] piece = new Piece[24];
    private GameRules rules;
    private Drawable draw;
    private int width;
    private int height;
    private int size;

    public Board(Drawable draw, int width, int height, int size) {
        this.draw = draw;
        this.width = width;
        this.height = height;
        this.size = size;
    }

    public void draw(Canvas canvas) {
        //TODO get these out of heaaar
        int offsetWidth = (int) (canvas.getWidth() / 20.0);
        int offsetHeight = (int) (canvas.getHeight() / 10.0);
        draw.setBounds(
                offsetWidth,
                offsetHeight,
                canvas.getWidth() - offsetWidth,
                canvas.getHeight() - offsetHeight);
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

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
