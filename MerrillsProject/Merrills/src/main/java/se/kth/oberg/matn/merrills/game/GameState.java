package se.kth.oberg.matn.merrills.game;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class GameState {
    private int selectedIndex = -1;
    private long state;

    public GameState() {
        state = Board.createBoard(false, true);
        Log.i("new board", Long.toBinaryString(state | (1L << 63)));
        Log.i("new board", Long.toBinaryString(0b10000011111000000000000000000000000111111111111111111111111L | (1L << 63)));
    }

    public void notifyDiff(long before, long after) {
        if (Board.getCurrentAction(before) == Board.getCurrentAction(after)
                && Board.getActivePlayer(before) == Board.getActivePlayer(after)) {
            return;
        }
        long diff = before ^ after;
        boolean active = Board.getActivePlayer(before);
        switch (Board.getCurrentAction(before)) {
            case Board.ACTION_PLACE:
                notifyAdded(Board.getActivePlayer(before), Board.getBitIndex(Board.getPlayerMask(diff, active)));
                break;
            case Board.ACTION_MOVE: {
                int from = Board.getPlayerMask(diff, active) & Board.getPlayerMask(before, active);
                int to = Board.getPlayerMask(diff, active) & Board.getPlayerMask(after, active);
                notifyMoved(Board.getBitIndex(from), Board.getBitIndex(to));
                break;
            }
            case Board.ACTION_REMOVE:
                notifyRemoved(Board.getBitIndex(Board.getPlayerMask(diff, !active)));
                break;
            case Board.ACTION_WON:
                break;
            default:
                throw new IllegalStateException("unknown action");
        }
        switch (Board.getCurrentAction(after)) {
            case Board.ACTION_PLACE:
                notifyTurnType(Board.getActivePlayer(after), TurnType.PLACE);
                break;
            case Board.ACTION_MOVE:
                notifyTurnType(Board.getActivePlayer(after), TurnType.CHOOSE_FROM);
                break;
            case Board.ACTION_REMOVE:
                notifyTurnType(Board.getActivePlayer(after), TurnType.REMOVE);
                break;
            case Board.ACTION_WON:
                notifyTurnType(Board.getActivePlayer(after), TurnType.WIN);
                break;
            default:
                throw new IllegalStateException("unknown action");
        }
    }

    public void doPosition(int index) {
        Log.i("doPosition", Long.toBinaryString(state | (1L << 63)));
        Log.i("doPosition", Long.toBinaryString(0b10000011111000000000000000000000000111111111111111111111111L | (1L << 63)));
        long oldState = state;
        switch (Board.getCurrentAction(state)) {
            case Board.ACTION_PLACE:
                doPlace(index);
                break;
            case Board.ACTION_MOVE:
                doMove(index);
                break;
            case Board.ACTION_REMOVE:
                doRemove(index);
                break;
            case Board.ACTION_WON:
                break;
            default:
                throw new IllegalStateException("unknown action");
        }
        notifyDiff(oldState, state);
        while (Board.getActivePlayer(state) == false && Board.getCurrentAction(state) != Board.ACTION_WON) {
            oldState = state;
            state = Ai.makeMove(state, false);
            Log.i("Ai", "Made move");
            Log.i("Ai", Long.toBinaryString(oldState | (1L << 63)));
            Log.i("Ai", Long.toBinaryString(state | (1L << 63)));
            Log.i("Ai", Long.toBinaryString(0b10000011111000000000000000000000000111111111111111111111111L | (1L << 63)));
            notifyDiff(oldState, state);
        }
    }

    private void doPlace(int index) {
        Log.i("doPlace", "index: " + index);
        if (Board.isAvailablePlacement(state, index)) {
//            notifyAdded(Board.getActivePlayer(state), index);
            state = Board.placeWithMask(state, 1 << index, true);

            switch (Board.getCurrentAction(state)) {
                case Board.ACTION_REMOVE:
//                    notifyTurnType(Board.getActivePlayer(state), TurnType.REMOVE);
                    break;
                case Board.ACTION_MOVE:
//                    notifyTurnType(Board.getActivePlayer(state), TurnType.CHOOSE_FROM);
                    break;
                case Board.ACTION_PLACE:
//                    notifyTurnType(Board.getActivePlayer(state), TurnType.PLACE);
                    break;
                case Board.ACTION_WON:
//                    notifyTurnType(Board.getActivePlayer(state), TurnType.WIN);
                    break;
                default:
                    throw new IllegalStateException("unknown action");
            }
        }
    }

    private void doMove(int index) {
        Log.i("doMove", "index: " + index);
        if (Board.isPlayer(state, index, true)) {
            if (selectedIndex >= 0) {
                notifySelected(selectedIndex, false);
                if (selectedIndex == index) {
                    selectedIndex = -1;
                    return;
                }
            }
            selectedIndex = index;
            notifySelected(selectedIndex, true);
        } else if (!Board.isPlayer(state, index, false)) {
            if (selectedIndex >= 0) {
                if (Board.isValidMove(state, selectedIndex, index, true)) {
                    state = Board.moveWithMasks(state, 1 << selectedIndex, 1 << index, true);
                    notifySelected(selectedIndex, false);
//                    notifyMoved(selectedIndex, index);
                    selectedIndex = -1;

                    switch (Board.getCurrentAction(state)) {
                        case Board.ACTION_REMOVE:
//                            notifyTurnType(Board.getActivePlayer(state), TurnType.REMOVE);
                            break;
                        case Board.ACTION_MOVE:
//                            notifyTurnType(Board.getActivePlayer(state), TurnType.CHOOSE_FROM);
                            break;
                        case Board.ACTION_WON:
//                            notifyTurnType(Board.getActivePlayer(state), TurnType.WIN);
                            break;
                        default:
                            throw new IllegalStateException("unknown action");
                    }
                }
            }
        }
    }

    private void doRemove(int index) {
        Log.i("doRemove", "index: " + index);
        if (Board.isAvailableRemove(state, index, true)) {
//            state = Board.remove(state, index);
            state = Board.removeWithMask(state, 1 << index, true);
//            notifyRemoved(index);

            switch (Board.getCurrentAction(state)) {
                case Board.ACTION_MOVE:
//                    notifyTurnType(Board.getActivePlayer(state), TurnType.CHOOSE_FROM);
                    break;
                case Board.ACTION_PLACE:
//                    notifyTurnType(Board.getActivePlayer(state), TurnType.PLACE);
                    break;
                case Board.ACTION_WON:
//                    notifyTurnType(Board.getActivePlayer(state), TurnType.WIN);
                    break;
                default:
                    throw new IllegalStateException("unknown action");
            }
        }
    }

    public int getSelectionMoveMask() {
        if (selectedIndex < 0) {
            return 0;
        }
        return Board.getAvailableMovesFrom(state, selectedIndex, true);
    }

    public int getMoveMask() {
        if (selectedIndex >= 0 || Board.getCurrentAction(state) != Board.ACTION_MOVE) {
            return 0;
        }
        return Board.getAvailableMoves(state, true);
    }

    public int getDeleteMask() {
        if (Board.getCurrentAction(state) != Board.ACTION_REMOVE) {
            return 0;
        }
        return Board.getAvailableRemoves(state, true);
    }

    public void load(long savedGameState) {
        state = savedGameState;
        selectedIndex = -1;
    }

    public long getState() {
        return state;
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
            listener.onPieceSelected(index, selected);
        }
    }

    private void notifyTurnType(boolean activePlayer, TurnType turnType) {
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
