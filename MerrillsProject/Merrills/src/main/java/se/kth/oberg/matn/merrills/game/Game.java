package se.kth.oberg.matn.merrills.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class Game extends Observable {
    private enum State {
        PLACE,
        CHOOSE_FROM,
        CHOOSE_TO,
        WIN
    }

    private boolean activePlayer = true;
    private TurnType turnType = TurnType.ADD;
    private State state = State.PLACE;
    private GameRules board = new GameRules();
    private int fromIndex;

    public void doPosition(int index) {
        switch (state) {
            case PLACE:
                doPlace(index);
                break;
            case CHOOSE_FROM:
                doFrom(index);
                break;
            case CHOOSE_TO:
                doTo(index);
                break;
            case WIN:
                break;
        }
    }

    private void doPlace(int index) {
        if (board.isFreeSpot(index)) {
            board.add(activePlayer, index);
            notifyAdded(activePlayer, index);
            activePlayer = !activePlayer;
        } else {
            state = State.CHOOSE_TO;
            fromIndex = index;
        }
    }

    private void doFrom(int index) {

    }

    private void doTo(int index) {
        if (board.isFreeSpot(index)) {
            board.remove(activePlayer, fromIndex);
            board.add(activePlayer, index);
            notifyMoved(fromIndex, index);
            activePlayer = !activePlayer;
            state = State.PLACE;
        }
    }

    private void notifyMoved(int from, int to) {
        for (PieceMoveListener listener : moveListeners) {
            listener.onPieceMoved(from, to);
        }
    }

    private void notifyRemoved(int index) {
        for (PieceRemoveListener listener : removeListeners) {
            listener.onPieceRemoved(index);
        }
    }

    private void notifyAdded(boolean player, int index) {
        for (PieceAddListener listener : addListeners) {
            listener.onPieceAdded(player, index);
        }
    }

    private List<PieceMoveListener> moveListeners = new ArrayList<>();
    public void addPieceMoveListener(PieceMoveListener listener) {
        moveListeners.add(listener);
    }
    public void removePieceMoveListener(PieceMoveListener listener) {
        moveListeners.remove(listener);
    }

    private List<PieceRemoveListener> removeListeners = new ArrayList<>();
    public void addPieceRemoveListener(PieceRemoveListener listener) {
        removeListeners.add(listener);
    }
    public void removePieceRemoveListener(PieceRemoveListener listener) {
        removeListeners.remove(listener);
    }

    private List<PieceAddListener> addListeners = new ArrayList<>();
    public void addPieceAddListener(PieceAddListener listener) {
        addListeners.add(listener);
    }
    public void removePieceAddListener(PieceAddListener listener) {
        addListeners.remove(listener);
    }
}
