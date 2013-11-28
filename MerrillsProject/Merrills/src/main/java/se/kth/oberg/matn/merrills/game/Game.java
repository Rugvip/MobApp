package se.kth.oberg.matn.merrills.game;

import java.util.ArrayList;
import java.util.List;

public class Game {
    private enum State {
        PLACE,
        REMOVE,
        CHOOSE_FROM,
        CHOOSE_TO,
        WIN
    }

    private boolean activePlayer = true;
    private TurnType turnType = TurnType.ADD;
    private State state = State.PLACE;

    private GameRules board = new GameRules();
    private int selectedIndex;
    private int trueCount = 9;
    private int falseCount = 9;

    public void doPosition(int index) {
        switch (state) {
            case PLACE:
                doPlace(index);
                break;
            case REMOVE:
                doRemove(index);
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

    private void next() {
        activePlayer = !activePlayer;
        int count = activePlayer ? trueCount : falseCount;
        if (count > 0) {
            state = State.PLACE;
        } else {
            state = State.CHOOSE_FROM;
        }
    }

    private void doPlace(int index) {
        if (board.isFreeSpot(index)) {
            boolean remove = board.add(index, activePlayer);
            notifyAdded(activePlayer, index);

            if (activePlayer) {
                --trueCount;
            } else {
                --falseCount;
            }

            if (remove) {
                state = State.REMOVE;
            } else {
                next();
            }
        }
    }

    private void doRemove(int index) {
        if (board.isPlayer(index, !activePlayer)) {
            board.remove(index);
            notifyRemoved(index);

            next();
        }
    }

    private void doFrom(int index) {
        if (board.isPlayer(index, activePlayer)) {
            if (selectedIndex >= 0) {
                notifySelected(selectedIndex, false);
            }
            selectedIndex = index;
            notifySelected(index, true);
            state = State.CHOOSE_TO;
        }
    }

    private void doTo(int index) {
        if (index == selectedIndex) {
            notifySelected(selectedIndex, false);
            selectedIndex = -1;
            state = State.CHOOSE_FROM;
        } else if (board.isPlayer(index, activePlayer)) {
            if (selectedIndex >= 0) {
                notifySelected(selectedIndex, false);
            }
            selectedIndex = index;
            notifySelected(selectedIndex, true);
        } else if (board.isValidMove(selectedIndex, index)) {
            notifySelected(selectedIndex, false);
            boolean remove = board.move(selectedIndex, index);
            notifyMoved(selectedIndex, index);
            selectedIndex = -1;

            if (remove) {
                state = State.REMOVE;
            } else {
                next();
            }
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

    private void notifySelected(int index, boolean selected) {
        for (PieceSelectListener listener : selectListeners) {
            listener.onPieceSelect(index, selected);
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

    private List<PieceSelectListener> selectListeners = new ArrayList<>();

    public void addPieceSelectListener(PieceSelectListener listener) {
        selectListeners.add(listener);
    }
    public void removePieceSelectListener(PieceSelectListener listener) {
        selectListeners.remove(listener);
    }
}
