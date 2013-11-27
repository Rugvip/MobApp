package se.kth.oberg.matn.merrills;

public class Board {
    private Piece[] piece = new Piece[24];
    private GameRules rules;

    public Board() {
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
