package se.kth.oberg.matn.merrills;

import android.util.Log;

import se.kth.oberg.matn.merrills.game.Game;
import se.kth.oberg.matn.merrills.game.PieceAddListener;
import se.kth.oberg.matn.merrills.game.PieceMoveListener;
import se.kth.oberg.matn.merrills.game.PieceRemoveListener;
import se.kth.oberg.matn.merrills.game.PieceSelectListener;

public class GameLogger {
    private Game game;

    public GameLogger(Game game) {
        this.game = game;

        game.addPieceAddListener(pieceAddListener);
        game.addPieceRemoveListener(pieceRemoveListener);
        game.addPieceMoveListener(pieceMoveListener);
        game.addPieceSelectListener(pieceSelectListener);
    }

    private PieceAddListener pieceAddListener = new PieceAddListener() {
        @Override
        public void onPieceAdded(boolean player, int index) {
            Log.d("Game", player + " added piece at " + index);
        }
    };

    private PieceRemoveListener pieceRemoveListener = new PieceRemoveListener() {
        @Override
        public void onPieceRemoved(int index) {
            Log.d("Game", "piece removed from " + index);
        }
    };

    private PieceMoveListener pieceMoveListener = new PieceMoveListener() {
        @Override
        public void onPieceMoved(int fromIndex, int toIndex) {
            Log.d("Game", "piece moved from " + fromIndex + " to " + toIndex);
        }
    };

    private PieceSelectListener pieceSelectListener = new PieceSelectListener() {
        @Override
        public void onPieceSelect(int index, boolean selected) {
            Log.d("Game", "piece " + (selected ? "selected" : "deselected") + " at " + index);
        }
    };
}
