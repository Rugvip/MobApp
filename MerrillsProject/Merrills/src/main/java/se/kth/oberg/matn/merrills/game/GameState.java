package se.kth.oberg.matn.merrills.game;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class GameState {
    private boolean activePlayer = true;
    private TurnType turnType = TurnType.PLACE;

    private BoardState board = new BoardState();
    private int selectedIndex = -1;
    private int trueCount = 9;
    private int falseCount = 9;

    public void doPosition(int index) {
        switch (turnType) {
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
            turnType = TurnType.PLACE;
        } else {
            if (board.hasLost(activePlayer)) {
                turnType = TurnType.WIN;
                activePlayer = !activePlayer;
            } else {
                turnType = TurnType.CHOOSE_FROM;
            }
        }
        notifyTurnType();
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
                turnType = TurnType.REMOVE;
                notifyTurnType();
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
            turnType = TurnType.CHOOSE_TO;
            notifyTurnType();
        }
    }

    private void doTo(int index) {
        if (index == selectedIndex) {
            notifySelected(selectedIndex, false);
            selectedIndex = -1;
            turnType = TurnType.CHOOSE_FROM;
            notifyTurnType();
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
                turnType = TurnType.REMOVE;
                notifyTurnType();
            } else {
                next();
            }
        }
    }

    public int getSelectionMoveMask() {
        if (selectedIndex < 0) {
            return 0;
        }
        return board.getAvailableMoves(selectedIndex);
    }

    public int getMoveMask() {
        if (turnType != TurnType.CHOOSE_FROM) {
            return 0;
        }
        return board.getAvailableMoves(activePlayer);
    }

    public int getDeleteMask() {
        if (turnType != TurnType.REMOVE) {
            return 0;
        }
        return board.getRemoveable(!activePlayer);
    }

    public void load(long savedGameState) {
        activePlayer = SavedGameState.getActivePlayer(savedGameState);
        trueCount = SavedGameState.getTrueCount(savedGameState);
        falseCount = SavedGameState.getFalseCount(savedGameState);
        board.setPlayerMask(true, SavedGameState.getTrueMask(savedGameState));
        board.setPlayerMask(false, SavedGameState.getFalseMask(savedGameState));

        turnType = (activePlayer ? trueCount > 0 : falseCount > 0) ? TurnType.PLACE : TurnType.CHOOSE_FROM;
    }

    public long save() {
        return SavedGameState.mask(activePlayer, board.getPlayerMask(true), board.getPlayerMask(false), trueCount, falseCount);
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

    private void notifyTurnType() {
        for (TurnListener listener : turnListeners) {
            listener.onTurn(activePlayer, turnType);
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

    private List<TurnListener> turnListeners = new ArrayList<>();

    public void addTurnListener(TurnListener listener) {
        turnListeners.add(listener);
    }
    public void removeTurnListener(TurnListener listener) {
        turnListeners.remove(listener);
    }
}
