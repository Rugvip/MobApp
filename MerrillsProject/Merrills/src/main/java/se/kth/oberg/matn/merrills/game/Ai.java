package se.kth.oberg.matn.merrills.game;

import android.util.Log;

public class Ai {
    private static final int DEPTH_DEEP = 2;
    private static final int DEPTH_SHALLOW = 1;

    private static final int INITIAL_MULT = 16;

    private static final int SCORE_PIECE = 1;
    private static final int SCORE_FREEDOM = 2;
    private static final int SCORE_MILL_MAKER = 5;
    private static final int SCORE_MILL = 10;
    private static final int SCORE_WIN = 1000;

    private static int scoreState(long state) {
        int pieceScore = 0;
        pieceScore += Board.getCount(state, true) + Board.countBits(Board.getPlayerMask(state, true));
        pieceScore -= Board.getCount(state, false) + Board.countBits(Board.getPlayerMask(state, false));
        pieceScore *= SCORE_PIECE;

        int moveScore = 0;
        moveScore += Board.getAvailableMoveCount(state, true);
        moveScore -= Board.getAvailableMoveCount(state, true);
        moveScore *= SCORE_FREEDOM;

        int millMakerScore = 0;
        millMakerScore += countMillMakers(state, true);
        millMakerScore -= countMillMakers(state, false);
        millMakerScore *= SCORE_MILL_MAKER;


        int millScore = 0;
        millScore += Board.getMillCount(state, true);
        millScore -= Board.getMillCount(state, false);
        millScore *= SCORE_MILL;

        int winScore = 0;
        if (Board.isWinner(state, true)) {
            winScore += SCORE_WIN;
        } else if (Board.isWinner(state, false)) {
            winScore -= SCORE_WIN;
        }

        return pieceScore + moveScore + millMakerScore + millScore + winScore;
    }

    private static int countMillMakers(long state, boolean active) {
        int count = 0;

        if (Board.getCount(state, active) > 0) {
            int masks = Board.getAvailablePlacements(state);
            while (masks != 0) {
                int mask = masks & -masks;
                masks &= masks - 1;

                if (Board.getCurrentAction(Board.placeWithMask(state, mask, active)) == Board.ACTION_REMOVE) {
                    count++;
                }
            }
        }

        int tos = Board.getAvailableMoves(state, active);
        while (tos != 0) {
            int to = tos & -tos;
            tos &= tos - 1;

            int froms = Board.getAvailableMovesToMask(state, to, active);
            while (froms != 0) {
                int from = froms & -froms;
                froms &= froms - 1;

                if (Board.getCurrentAction(Board.moveWithMasks(state, from, to, active)) == Board.ACTION_REMOVE) {
                    count++;
                }
            }
        }

        return count;
    }

    public static int evaluateMove(long state, int deep, int shallow) {
        if (shallow <= 0) {
            return scoreState(state);
        }

        int firstScore = Integer.MIN_VALUE;
        long firstMove = 0;
        int secondScore = Integer.MIN_VALUE;
        long secondMove = 0;
        int thirdScore = Integer.MIN_VALUE;
        long thirdMove = 0;

        switch (Board.getCurrentAction(state)) {
            case Board.ACTION_MOVE: {
                if (shallow > 0 || deep > 0) {
                    int tos = Board.getAvailableMoves(state, true);
                    while (tos != 0) {
                        int to = tos & -tos;
                        tos &= tos - 1;

                        int froms = Board.getAvailableMovesToMask(state, to, true);
                        while (froms != 0) {
                            int from = froms & -froms;
                            froms &= froms - 1;
                            long move = Board.moveWithMasks(state, from, to, true);
                            int score = evaluateMove(move, 0, shallow - 1);
                            if (score > firstScore) {
                                thirdMove = secondMove;
                                thirdScore = secondScore;
                                secondMove = firstMove;
                                secondScore = firstScore;
                                firstMove = move;
                                firstScore = score;
                            } else if (score > secondScore) {
                                thirdMove = secondMove;
                                thirdScore = secondScore;
                                secondMove = move;
                                secondScore = score;
                            } else if (score > thirdScore) {
                                thirdMove = move;
                                thirdScore = score;
                            }
                        }
                    }
                }
                break;
            }
            case Board.ACTION_PLACE: {
                if (shallow > 0 || deep > 0) {
                    int masks = Board.getAvailablePlacements(state);
                    while (masks != 0) {
                        int mask = masks & -masks;
                        masks &= masks - 1;

                        long move = Board.placeWithMask(state, mask, true);
                        int score = evaluateMove(move, 0, shallow - 1);
                        if (score > firstScore) {
                            thirdMove = secondMove;
                            thirdScore = secondScore;
                            secondMove = firstMove;
                            secondScore = firstScore;
                            firstMove = move;
                            firstScore = score;
                        } else if (score > secondScore) {
                            thirdMove = secondMove;
                            thirdScore = secondScore;
                            secondMove = move;
                            secondScore = score;
                        } else if (score > thirdScore) {
                            thirdMove = move;
                            thirdScore = score;
                        }
                    }
                }
                break;
            }
            case Board.ACTION_REMOVE: {
                int masks = Board.getAvailableRemoves(state, true);
                while (masks != 0) {
                    int mask = masks & -masks;
                    masks &= masks - 1;

                    long move = Board.removeWithMask(state, mask, true);
                    int score = evaluateMove(move, 0, shallow);
                    if (score > firstScore) {
                        thirdMove = secondMove;
                        thirdScore = secondScore;
                        secondMove = firstMove;
                        secondScore = firstScore;
                        firstMove = move;
                        firstScore = score;
                    } else if (score > secondScore) {
                        thirdMove = secondMove;
                        thirdScore = secondScore;
                        secondMove = move;
                        secondScore = score;
                    } else if (score > thirdScore) {
                        thirdMove = move;
                        thirdScore = score;
                    }
                }
                break;
            }
            case Board.ACTION_WON:
                return SCORE_WIN;
            default:
                throw new IllegalStateException("illegal action");
        }

        if (firstMove != 0) {
            firstScore = scoreIt(firstMove, firstScore, deep);
        }
        if (secondMove != 0) {
            secondScore = scoreIt(secondMove, secondScore, deep);
        }
        if (thirdMove != 0) {
            thirdScore = scoreIt(thirdMove, thirdScore, deep);
        }

        int score = firstScore;
        score = secondScore > score ? secondScore : score;
        score = thirdScore > score ? thirdScore : score;

        return score;
    }

    private static int scoreIt(long move, int score, int deep) {
        switch (Board.getCurrentAction(move)) {
            case Board.ACTION_MOVE:
                if (deep > 0) {
                    return -evaluateMove(move, deep - 1, DEPTH_SHALLOW);
                } else {
                    return -score;
                }
            case Board.ACTION_PLACE:
                if (deep > 0) {
                    return -evaluateMove(move, deep - 1, DEPTH_SHALLOW);
                } else {
                    return -score;
                }
            case Board.ACTION_REMOVE:
                if (deep > 0) {
                    return evaluateMove(move, deep, DEPTH_SHALLOW);
                } else {
                    return score;
                }
            case Board.ACTION_WON:
                return -SCORE_WIN;
            default:
                throw new IllegalStateException("illegal action");
        }
    }

    public static long makeMove(long state, boolean aiPlayer) {
        int maxScore = Integer.MIN_VALUE;
        long maxMove = 0;

        switch (Board.getCurrentAction(state)) {
            case Board.ACTION_MOVE: {
                Log.w("Ai", "action = move");
                int tos = Board.getAvailableMoves(state, true);
                while (tos != 0) {
                    int to = tos & -tos;
                    tos &= tos - 1;


                    int froms = Board.getAvailableMovesToMask(state, to, true);
                    while (froms != 0) {
                        int from = froms & -froms;
                        froms &= froms - 1;
                        long move = Board.moveWithMasks(state, from, to, true);
                        int score;
                        if (Board.getCurrentAction(move) == Board.ACTION_REMOVE) {
                            score = evaluateMove(move, DEPTH_DEEP, DEPTH_SHALLOW);
                        } else {
                            score = -evaluateMove(move, DEPTH_DEEP, DEPTH_SHALLOW);
                        }
                        Log.w("Ai", "made move with score: " + score);
                        Log.w("Ai", Long.toBinaryString(move | (1L << 63)));
                        Log.w("Ai", Long.toBinaryString(0b10000011111000000000000000000000000111111111111111111111111L | (1L << 63)));
                        if (score > maxScore) {
                            maxScore = score;
                            maxMove = move;
                        }
                    }
                }
                break;
            }
            case Board.ACTION_PLACE: {
                Log.w("Ai", "action = place");
                int masks = Board.getAvailablePlacements(state);
                Log.w("Ai", Long.toBinaryString(masks | (1L << 63)));
                Log.w("Ai", Long.toBinaryString(0b10000011111000000000000000000000000111111111111111111111111L | (1L << 63)));
                while (masks != 0) {
                    int mask = masks & -masks;
                    masks &= masks - 1;

                    long move = Board.placeWithMask(state, mask, true);
                    int score;
                    if (Board.getCurrentAction(move) == Board.ACTION_REMOVE) {
                        score = evaluateMove(move, DEPTH_DEEP, DEPTH_SHALLOW);
                    } else {
                        score = -evaluateMove(move, DEPTH_DEEP, DEPTH_SHALLOW);
                    }
                    Log.w("Ai", "made move with score: " + score);
                    Log.w("Ai", Long.toBinaryString(move | (1L << 63)));
                    Log.w("Ai", Long.toBinaryString(0b10000011111000000000000000000000000111111111111111111111111L | (1L << 63)));
                    if (score > maxScore) {
                        maxScore = score;
                        maxMove = move;
                    }
                }
                break;
            }
            case Board.ACTION_REMOVE: {
                Log.w("Ai", "action = remove");
                int masks = Board.getAvailableRemoves(state, true);
                while (masks != 0) {
                    int mask = masks & -masks;
                    masks &= masks - 1;

                    long move = Board.removeWithMask(state, mask, true);
                    int score = -evaluateMove(move, DEPTH_DEEP, DEPTH_SHALLOW);
                    Log.w("Ai", "made move with score: " + score);
                    Log.w("Ai", Long.toBinaryString(move | (1L << 63)));
                    Log.w("Ai", Long.toBinaryString(0b10000011111000000000000000000000000111111111111111111111111L | (1L << 63)));
                    if (score > maxScore) {
                        maxScore = score;
                        maxMove = move;
                    }
                }
                break;
            }
            case Board.ACTION_WON:
                Log.w("Ai", "action = won");
                return state;
            default:
                throw new IllegalStateException("illegal action");
        }

        Log.e("Ai", "best move was: " + maxScore);
        Log.e("Ai", Long.toBinaryString(maxMove | (1L << 63)));
        Log.e("Ai", Long.toBinaryString(0b10000011111000000000000000000000000111111111111111111111111L | (1L << 63)));

        return maxMove;
    }
}
