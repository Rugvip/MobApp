package se.kth.oberg.matn.merrills.game;

import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;

public class Ai extends AsyncTask<Long, Void, Long> {
    private static final int DEPTH_DEEP = 5;

    private static final int SCORE_PIECE = 1;
    private static final int SCORE_MOVEMENT = 2;
    private static final int SCORE_MILL = 10;
    private static final int SCORE_WIN = 1000;

    private static int scoreState(long state, boolean active) {
        int activePieces = Board.getCount(state, active) + Board.countBits(Board.getPlayerMask(state, active));
        int inactivePieces = Board.getCount(state, !active) + Board.countBits(Board.getPlayerMask(state, !active));

        int pieceScore = 0;
        pieceScore += activePieces;
        pieceScore -= inactivePieces;
        pieceScore *= SCORE_PIECE;

        int moveScore = 0;
        if (activePieces > 3) {
            moveScore += Board.getAvailableMoveCount(state, active);
        }
        if (inactivePieces > 3) {
            moveScore -= Board.getAvailableMoveCount(state, !active);
        }
        moveScore *= SCORE_MOVEMENT;

        return pieceScore + moveScore;
    }

    private static int negamax(long state, int depth, int color) {
        if (depth <= 0) {
            return color * scoreState(state, false);
        }

        int topScore = Integer.MIN_VALUE;

        switch (Board.getCurrentAction(state)) {
            case Board.ACTION_MOVE: {
                int tos = Board.getAvailableMoves(state, true);
                while (tos != 0) {
                    int to = tos & -tos;
                    tos &= tos - 1;

                    int froms = Board.getAvailableMovesToMask(state, to, true);
                    while (froms != 0) {
                        int from = froms & -froms;
                        froms &= froms - 1;

                        long move = Board.moveWithMasks(state, from, to, true);

                        if (Board.getCurrentAction(move) == Board.ACTION_REMOVE) {
                            int masks = Board.getAvailableRemoves(move, true);
                            while (masks != 0) {
                                int mask = masks & -masks;
                                masks &= masks - 1;

                                long remove = Board.removeWithMask(move, mask, true);
                                int score = -negamax(remove, depth - 1, -color) + SCORE_MILL;
                                topScore = score > topScore ? score : topScore;
                            }
                        } else {
                            int score = -negamax(move, depth - 1, -color);
                            topScore = score > topScore ? score : topScore;
                        }
                    }
                }
                break;
            }
            case Board.ACTION_PLACE: {
                int places = Board.getAvailablePlacements(state);
                while (places != 0) {
                    int place = places & -places;
                    places &= places - 1;

                    long move = Board.placeWithMask(state, place, true);

                    if (Board.getCurrentAction(move) == Board.ACTION_REMOVE) {
                        int masks = Board.getAvailableRemoves(move, true);
                        while (masks != 0) {
                            int mask = masks & -masks;
                            masks &= masks - 1;

                            long remove = Board.removeWithMask(move, mask, true);
                            int score = -negamax(remove, depth - 3, -color) + SCORE_MILL;
                            topScore = score > topScore ? score : topScore;
                        }
                    } else {
                        int score = -negamax(move, depth - 3, -color);
                        topScore = score > topScore ? score : topScore;
                    }
                }
                break;
            }
            case Board.ACTION_WON: {
                topScore = color * SCORE_WIN * (depth + 1) * (Board.getActivePlayer(state) ? -1 : 1);
                topScore += -negamax(state, depth - 1, -color);
                break;
            }
            case Board.ACTION_LOST:
                topScore = color * SCORE_WIN * (depth + 1) * (Board.getActivePlayer(state) ? 1 : -1);
                topScore += -negamax(state, depth - 1, -color);
                break;
            default:
                throw new IllegalStateException("illegal action");
        }

        return topScore;
    }

    @Override
    protected Long doInBackground(Long... states) {
        if (states.length != 1) {
            throw new IllegalArgumentException("can only compute moves for one state at a time");
        }

        final long moves[] = getMoveList(states[0]);
        final int scores[] = new int[moves.length];
        Thread threads[] = new Thread[moves.length];

        for (int i = 0; i < moves.length; i++) {
            final int index = i;
            threads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    scores[index] = -negamax(moves[index], DEPTH_DEEP, -1);
                    if (Board.isFlagSet(moves[index])) {
                        scores[index] += SCORE_MILL;
                        moves[index] = Board.clearFlag(moves[index]);
                    }
                }
            });
            threads[i].start();
        }

        try {
            for (Thread thread : threads) {
                Log.i("Ai", "thread joined");
                thread.join(10000);
            }
            Log.i("Ai", "all threads complete");

            int topScore = Integer.MIN_VALUE;
            int topIndex = -1;

            for (int i = 0; i < moves.length; i++) {
                if (scores[i] > topScore) {
                    topScore = scores[i];
                    topIndex = i;
                }
            }

            return moves[topIndex];
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            for (Thread thread : threads) {
                thread.interrupt();
            }
        }

        return null;
    }

    public static long[] getMoveList(long state) {
        ArrayList<Long> moveList = new ArrayList<>();

        switch (Board.getCurrentAction(state)) {
            case Board.ACTION_MOVE: {
                int tos = Board.getAvailableMoves(state, true);
                while (tos != 0) {
                    int to = tos & -tos;
                    tos &= tos - 1;

                    int froms = Board.getAvailableMovesToMask(state, to, true);
                    while (froms != 0) {
                        int from = froms & -froms;
                        froms &= froms - 1;
                        long move = Board.moveWithMasks(state, from, to, true);

                        if (Board.getCurrentAction(move) == Board.ACTION_REMOVE) {
                            int masks = Board.getAvailableRemoves(move, true);
                            while (masks != 0) {
                                int mask = masks & -masks;
                                masks &= masks - 1;

                                long remove = Board.setFlag(Board.removeWithMask(move, mask, true));
                                moveList.add(remove);
                            }
                        } else {
                            moveList.add(move);
                        }
                    }
                }
                break;
            }
            case Board.ACTION_PLACE: {
                int places = Board.getAvailablePlacements(state);
                while (places != 0) {
                    int place = places & -places;
                    places &= places - 1;

                    long move = Board.placeWithMask(state, place, true);

                    if (Board.getCurrentAction(move) == Board.ACTION_REMOVE) {
                        int masks = Board.getAvailableRemoves(move, true);
                        while (masks != 0) {
                            int mask = masks & -masks;
                            masks &= masks - 1;

                            long remove = Board.setFlag(Board.removeWithMask(move, mask, true));
                            moveList.add(remove);
                        }
                    } else {
                        moveList.add(move);
                    }
                }
                break;
            }
            case Board.ACTION_WON:
                break;
            default:
                throw new IllegalStateException("illegal action");
        }

        long moveArray[] = new long[moveList.size()];

        for (int i = 0; i < moveList.size(); i++) {
            moveArray[i] = moveList.get(i);
        }

        return moveArray;
    }
}
