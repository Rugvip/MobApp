package se.kth.oberg.matn.merrills.game;

import android.util.Log;

public class Ai {
    private static final int DEPTH_DEEP = 1;
    private static final int DEPTH_SHALLOW = 1;

    private static final int INITIAL_MULT = 16;

    private static final int SCORE_PIECE = 1;
    private static final int SCORE_FREEDOM = 2;
    private static final int SCORE_MILL_MAKER = 20;
    private static final int SCORE_MILL = 10;
    private static final int SCORE_WIN = 1000;

    private static int scoreState(long state, boolean active) {
        int activePieces = Board.getCount(state, active) + Board.countBits(Board.getPlayerMask(state, active));
        int inactivePieces = Board.getCount(state, !active) + Board.countBits(Board.getPlayerMask(state, !active));

        int pieceScore = 0;
        pieceScore += activePieces;
        pieceScore -= inactivePieces;
        pieceScore *= SCORE_PIECE;
        Log.e("Piece", "score: " + pieceScore + " active: " + (Board.getCount(state, active) + Board.countBits(Board.getPlayerMask(state, active)))
                + " inactive: " +  (Board.getCount(state, !active) + Board.countBits(Board.getPlayerMask(state, !active))));

        int moveScore = 0;
        if (activePieces > 3) {
            moveScore += Board.getAvailableMoveCount(state, active);
        }
        if (inactivePieces > 3) {
            moveScore -= Board.getAvailableMoveCount(state, !active);
        }
        moveScore *= SCORE_FREEDOM;
        Log.e("Moves", "score: " + moveScore + " active: " + Board.getAvailableMoveCount(state, active) + " inactive: " +  Board.getAvailableMoveCount(state, !active));

        int millScore = 0;
        millScore += Board.getMillCount(state, active);
        millScore -= Board.getMillCount(state, !active);
        millScore *= SCORE_MILL;
        Log.e("Mills", "score: " + millScore + " active: " + Board.getMillCount(state, active) + " inactive: " +  Board.getMillCount(state, !active));

//        int millMakerScore = 0;
//        millMakerScore += countMillMakers(state, active);
//        millMakerScore -= countMillMakers(state, !active);
//        millMakerScore *= SCORE_MILL_MAKER;
//        Log.e("MillM", "score: " + millMakerScore + " active: " + countMillMakers(state, active) + " inactive: " +  countMillMakers(state, !active));
//
//        int winScore = 0;
//        if (Board.isWinner(state, active)) {
//            winScore += SCORE_WIN;
//        } else if (Board.isWinner(state, !active)) {
//            winScore -= SCORE_WIN;
//        }

//        return pieceScore + moveScore + millScore + millMakerScore + winScore;
//        return pieceScore + moveScore + millScore + millMakerScore;
        Log.e("TOTAL SCORE", "" + (pieceScore + moveScore + millScore));
        return pieceScore + moveScore + millScore;
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

    private static int negamax(long state, int depth, int color) {
        if (depth <= 0) {
            return color * scoreState(state, true);
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
                                int score = -negamax(remove, depth - 1, -color) + SCORE_MILL_MAKER;
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
                            int score = -negamax(remove, depth - 1, -color) + SCORE_MILL_MAKER;
                            topScore = score > topScore ? score : topScore;
                        }
                    } else {
                        int score = -negamax(move, depth - 1, -color);
                        Log.i("SCORE", "SCORE: " + score);
                        topScore = score > topScore ? score : topScore;
                    }
                }
                break;
            }
            case Board.ACTION_WON:
                return color * SCORE_WIN;
            default:
                throw new IllegalStateException("illegal action");
        }

        return topScore;
    }

    public static int evaluateMove(long state, int deep, int shallow) {
//        if (shallow <= 0) {
//            return scoreState(state);
//        }
//
//        int firstScore = Integer.MIN_VALUE;
//        long firstMove = 0;
//        int secondScore = Integer.MIN_VALUE;
//        long secondMove = 0;
//        int thirdScore = Integer.MIN_VALUE;
//        long thirdMove = 0;
//
//        switch (Board.getCurrentAction(state)) {
//            case Board.ACTION_MOVE: {
//                if (shallow > 0 || deep > 0) {
//                    int tos = Board.getAvailableMoves(state, true);
//                    while (tos != 0) {
//                        int to = tos & -tos;
//                        tos &= tos - 1;
//
//                        int froms = Board.getAvailableMovesToMask(state, to, true);
//                        while (froms != 0) {
//                            int from = froms & -froms;
//                            froms &= froms - 1;
//                            long move = Board.moveWithMasks(state, from, to, true);
//                            int score = evaluateMove(move, 0, shallow - 1);
//                            if (score > firstScore) {
//                                thirdMove = secondMove;
//                                thirdScore = secondScore;
//                                secondMove = firstMove;
//                                secondScore = firstScore;
//                                firstMove = move;
//                                firstScore = score;
//                            } else if (score > secondScore) {
//                                thirdMove = secondMove;
//                                thirdScore = secondScore;
//                                secondMove = move;
//                                secondScore = score;
//                            } else if (score > thirdScore) {
//                                thirdMove = move;
//                                thirdScore = score;
//                            }
//                        }
//                    }
//                }
//                break;
//            }
//            case Board.ACTION_PLACE: {
//                if (shallow > 0 || deep > 0) {
//                    int masks = Board.getAvailablePlacements(state);
//                    while (masks != 0) {
//                        int mask = masks & -masks;
//                        masks &= masks - 1;
//
//                        long move = Board.placeWithMask(state, mask, true);
//                        int score = evaluateMove(move, 0, shallow - 1);
//                        if (score > firstScore) {
//                            thirdMove = secondMove;
//                            thirdScore = secondScore;
//                            secondMove = firstMove;
//                            secondScore = firstScore;
//                            firstMove = move;
//                            firstScore = score;
//                        } else if (score > secondScore) {
//                            thirdMove = secondMove;
//                            thirdScore = secondScore;
//                            secondMove = move;
//                            secondScore = score;
//                        } else if (score > thirdScore) {
//                            thirdMove = move;
//                            thirdScore = score;
//                        }
//                    }
//                }
//                break;
//            }
//            case Board.ACTION_REMOVE: {
//                int masks = Board.getAvailableRemoves(state, true);
//                while (masks != 0) {
//                    int mask = masks & -masks;
//                    masks &= masks - 1;
//
//                    long move = Board.removeWithMask(state, mask, true);
//                    int score = evaluateMove(move, 0, shallow);
//                    if (score > firstScore) {
//                        thirdMove = secondMove;
//                        thirdScore = secondScore;
//                        secondMove = firstMove;
//                        secondScore = firstScore;
//                        firstMove = move;
//                        firstScore = score;
//                    } else if (score > secondScore) {
//                        thirdMove = secondMove;
//                        thirdScore = secondScore;
//                        secondMove = move;
//                        secondScore = score;
//                    } else if (score > thirdScore) {
//                        thirdMove = move;
//                        thirdScore = score;
//                    }
//                }
//                break;
//            }
//            case Board.ACTION_WON:
//                return SCORE_WIN;
//            default:
//                throw new IllegalStateException("illegal action");
//        }
//
//        if (firstMove != 0) {
//            firstScore = scoreIt(firstMove, firstScore, deep);
//        }
//        if (secondMove != 0) {
//            secondScore = scoreIt(secondMove, secondScore, deep);
//        }
//        if (thirdMove != 0) {
//            thirdScore = scoreIt(thirdMove, thirdScore, deep);
//        }
//
//        int score = firstScore;
//        score = secondScore > score ? secondScore : score;
//        score = thirdScore > score ? thirdScore : score;
//
//        return score;
        return 0;
    }

    public static long makeMove(long state, boolean aiPlayer) {
        int topScore = Integer.MIN_VALUE;
        long topMove = 0;

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

                        if (Board.getCurrentAction(move) == Board.ACTION_REMOVE) {
                            int masks = Board.getAvailableRemoves(move, true);
                            while (masks != 0) {
                                int mask = masks & -masks;
                                masks &= masks - 1;

                                long remove = Board.removeWithMask(move, mask, true);
                                int score = negamax(remove, DEPTH_DEEP, 1);
                                if (score > topScore) {
                                    topScore = score;
                                    topMove = remove;
                                }
                                Log.w("Ai", "made remove with score: " + score);
                                Log.w("Ai", Long.toBinaryString(remove | (1L << 63)));
                                Log.w("Ai", Long.toBinaryString(0b10000011111000000000000000000000000111111111111111111111111L | (1L << 63)));
                            }
                        } else {
                            int score = negamax(move, DEPTH_DEEP, 1);
                            if (score > topScore) {
                                topScore = score;
                                topMove = move;
                            }
                            Log.w("Ai", "made move with score: " + score);
                            Log.w("Ai", Long.toBinaryString(move | (1L << 63)));
                            Log.w("Ai", Long.toBinaryString(0b10000011111000000000000000000000000111111111111111111111111L | (1L << 63)));
                        }
                    }
                }
                break;
            }
            case Board.ACTION_PLACE: {
                Log.w("Ai", "action = place");
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
                            int score = -negamax(remove, DEPTH_DEEP, -1);
                            if (score > topScore) {
                                topScore = score;
                                topMove = remove;
                            }
                            Log.w("Ai", "made remove with score: " + score);
                            Log.w("Ai", Long.toBinaryString(remove | (1L << 63)));
                            Log.w("Ai", Long.toBinaryString(0b10000011111000000000000000000000000111111111111111111111111L | (1L << 63)));
                        }
                    } else {
                        int score = -negamax(move, DEPTH_DEEP, -1);
                        if (score > topScore) {
                            topScore = score;
                            topMove = move;
                        }
                        Log.w("Ai", "made move with score: " + score);
                        Log.w("Ai", Long.toBinaryString(move | (1L << 63)));
                        Log.w("Ai", Long.toBinaryString(0b10000011111000000000000000000000000111111111111111111111111L | (1L << 63)));
                    }
                }
                break;
            }
//            case Board.ACTION_REMOVE: {
//                Log.w("Ai", "action = remove");
//                int masks = Board.getAvailableRemoves(state, true);
//                while (masks != 0) {
//                    int mask = masks & -masks;
//                    masks &= masks - 1;
//
//                    long move = Board.removeWithMask(state, mask, true);
//                    int score = negamax(move, DEPTH_DEEP, Integer.MIN_VALUE, Integer.MAX_VALUE, -1);
//                    Log.w("Ai", "made move with score: " + score);
//                    Log.w("Ai", Long.toBinaryString(move | (1L << 63)));
//                    Log.w("Ai", Long.toBinaryString(0b10000011111000000000000000000000000111111111111111111111111L | (1L << 63)));
//                    if (score > topScore) {
//                        topScore = score;
//                        topMove = move;
//                    }
//                }
//                break;
//            }
            case Board.ACTION_WON:
                Log.w("Ai", "action = won");
                return state;
            default:
                throw new IllegalStateException("illegal action");
        }

        Log.e("Ai", "best move was: " + topScore);
        Log.e("Ai", Long.toBinaryString(topMove | (1L << 63)));
        Log.e("Ai", Long.toBinaryString(0b10000011111000000000000000000000000111111111111111111111111L | (1L << 63)));

        return topMove;
    }
}
