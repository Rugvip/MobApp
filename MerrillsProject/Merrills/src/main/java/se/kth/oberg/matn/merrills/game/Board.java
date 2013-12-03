package se.kth.oberg.matn.merrills.game;

import android.util.Log;

public class Board {
    private static final long MASK_MASK = 0xFFFFFFL;
    private static final int INT_MASK_MASK = 0xFFFFFF;
    private static final long COUNT_MASK = 0x1FL;

    private static final int OFFSET_TRUE = 0;
    private static final int OFFSET_FALSE = 24;
    private static final int OFFSET_TRUE_COUNT = 48;
    private static final int OFFSET_FALSE_COUNT = 53;
    private static final int OFFSET_ACTIVE_PLAYER = 58;
    private static final int OFFSET_IS_REMOVE_TURN = 59;
    private static final int OFFSET_ALLOW_REMOVE_MILL = 60;
    private static final int OFFSET_ALLOW_FLIGHT = 61;
    private static final int OFFSET_UNUSED_2 = 62;
    private static final int OFFSET_UNUSED_3 = 63;

    public static final int ACTION_WON = 1 << 0;
    public static final int ACTION_PLACE = 1 << 1;
    public static final int ACTION_MOVE = 1 << 2;
    public static final int ACTION_REMOVE = 1 << 3;

    public static long createBoard(boolean allowRemoveMill, boolean allowFlight) {
        long state = 0;
        state |= (9L << OFFSET_TRUE_COUNT);
        state |= (9L << OFFSET_FALSE_COUNT);
        state = putBit(state, OFFSET_ALLOW_REMOVE_MILL, allowRemoveMill);
        state = putBit(state, OFFSET_ALLOW_FLIGHT, allowFlight);
        state = setBit(state, OFFSET_ACTIVE_PLAYER);
        return state;
    }

    private static long putBit(long state, int offset, boolean bit) {
        if (bit) {
            return setBit(state, offset);
        } else {
            return clearBit(state, offset);
        }
    }

    private static long setBit(long state, int offset) {
        return state | (1L << offset);
    }

    private static long clearBit(long state, int offset) {
        return state & ~(1L << offset);
    }

    private static long toggleBit(long state, int offset) {
        return state ^ (1L << offset);
    }

    private static boolean getBit(long state, int offset) {
        return ((state >>> offset) & 1) != 0;
    }

    static int getBitIndex(int mask) {
        int index = 0;
        index += ((mask & 0xFFFF0000) != 0) ? 16 : 0;
        index += ((mask & 0xFF00FF00) != 0) ? 8 : 0;
        index += ((mask & 0xF0F0F0F0) != 0) ? 4 : 0;
        index += ((mask & 0xCCCCCCCC) != 0) ? 2 : 0;
        index += ((mask & 0xAAAAAAAA) != 0) ? 1 : 0;
        return index;
    }

    static int countBits(int mask) {
        int count = 0;
        for (;mask != 0; mask &= mask - 1) {
            count++;
        }
        return count;
    }

    static int getMaskOffset(long state, boolean active) {
        return ((((state >>> OFFSET_ACTIVE_PLAYER) & 1) != 0) == active) ? OFFSET_TRUE : OFFSET_FALSE;
    }

    public static int getPlayerMask(long state, boolean player) {
        return (int) ((state >>> (player ? OFFSET_TRUE : OFFSET_FALSE)) & MASK_MASK);
    }

    public static int getPlayerCount(long state, boolean player) {
        return (int) ((state >>> (player ? OFFSET_TRUE_COUNT : OFFSET_FALSE_COUNT)) & COUNT_MASK);
    }

    static int getCount(long state, boolean active) {
        return getPlayerCount(state, active == getActivePlayer(state));
//        return (int) ((state >>> (((((state >>> OFFSET_ACTIVE_PLAYER) & 1) != 0) == active) ? OFFSET_TRUE_COUNT : OFFSET_FALSE_COUNT)) & COUNT_MASK);
    }

    public static boolean getActivePlayer(long state) {
        return ((state >>> OFFSET_ACTIVE_PLAYER) & 1) != 0;
    }

    static boolean isPlayer(long state, int index, boolean active) {
        return getBit(state, (getActivePlayer(state) == active ? OFFSET_TRUE : OFFSET_FALSE) + index);
    }

    static int getMask(long state, boolean active) {
        return (int) ((state >>> (((((state >>> OFFSET_ACTIVE_PLAYER) & 1) != 0) == active) ? OFFSET_TRUE : OFFSET_FALSE)) & MASK_MASK);
    }

    private static int getBothMask(long state) {
        return (int) (((state >>> OFFSET_TRUE) | (state >>> OFFSET_FALSE)) & MASK_MASK);
    }

    static int getAvailableMovesFrom(long state, int index, boolean active) {
        if (isFlying(state, active)) {
            return ~getBothMask(state);
        }
        return availableTable[index] & ~getBothMask(state);
    }

    static int getAvailableMovesTo(long state, int index, boolean active) {
        if (isFlying(state, active)) {
            return getMask(state, active);
        }
        return availableTable[index] & getMask(state, active);
    }

    static int getAvailableMovesToMask(long state, int mask, boolean active) {
        if (isFlying(state, active)) {
            return getMask(state, active);
        }
        return INT_MASK_MASK & (getMoveMask(mask) & getMask(state, active));
    }

    public static int getCurrentAction(long state) {
        if (isWinner(state, true)) {
            return ACTION_WON;
        } else if (getBit(state, OFFSET_IS_REMOVE_TURN)) {
            return ACTION_REMOVE;
        } else if (((state >>> OFFSET_ACTIVE_PLAYER) & 1) != 0 ? ((state >>> OFFSET_TRUE_COUNT) & COUNT_MASK) > 0 :  ((state >>> OFFSET_FALSE_COUNT) & COUNT_MASK) > 0) {
            return ACTION_PLACE;
        } else {
            return ACTION_MOVE;
        }
    }

    static int getAvailablePlacements(long state) {
        return INT_MASK_MASK & ~getBothMask(state);
    }

    static boolean isAvailablePlacement(long state, int index) {
        return getBit(getAvailablePlacements(state), index);
    }

    private static boolean isFlying(long state, boolean active) {
        if (!getBit(state, OFFSET_ALLOW_FLIGHT)) {
            return false;
        }
        if (getCount(state, active) > 0) {
            return false;
        }
        int positions = getMask(state, active);
        positions &= (positions - 1);
        positions &= (positions - 1);
        return ((positions & (positions - 1)) == 0);
    }

    private static int getMoveMask(int positions) {
        int ret = 0;
        ret |= (positions << 3) | (positions >>> 21);
        ret |= (positions >>> 3) | (positions << 21);
        ret |= (positions & 0b011000011000011000011000) << 1;
        ret |= (positions & 0b110000110000110000110000) >>> 1;
        return ret & INT_MASK_MASK;
    }

    static int getAvailableMoves(long state, boolean active) {
        if (isFlying(state, active)) {
            return INT_MASK_MASK & ~getBothMask(state);
        }
        return INT_MASK_MASK & (getMoveMask(getMask(state, active)) & ~getBothMask(state));
    }

    static boolean isValidMove(long state, int fromIndex, int toIndex, boolean active) {
        return (getAvailableMovesTo(state, toIndex, active) & (1 << fromIndex)) != 0;
    }

    static int getAvailableRemoves(long state, boolean active) {
        int mask = getMask(state, !active);
        int ret = mask;

        if (!getBit(state, OFFSET_ALLOW_REMOVE_MILL)) {
            for (int i = 0; i < millTable.length; i++) {
                int mill = millTable[i];
                if (((mask & mill) ^ mill) == 0) {
                    ret &= ~mill;
                }
            }
        }

        return ret != 0 ? ret : mask;
    }

    static boolean isAvailableRemove(long state, int index, boolean active) {
        return getBit(getAvailableRemoves(state, active), index);
    }

    private static long switchActivePlayer(long state) {
        if (getCurrentAction(state) == ACTION_WON) {
            return state;
        } else {
            return toggleBit(state, OFFSET_ACTIVE_PLAYER);
        }
    }

//    public static long move(long state, int fromIndex, int toIndex) {
//        if (getCurrentAction(state) != ACTION_MOVE) {
//            throw new IllegalStateException("not a move turn");
//        }
//
//        state = clearBit(state, getMaskOffset(state, true) + fromIndex);
//        state = setBit(state, getMaskOffset(state, true) + toIndex);
//
//        if (isMillMaker(state, 1 << toIndex)) {
//            return setBit(state, OFFSET_IS_REMOVE_TURN);
//        } else {
//            return switchActivePlayer(state);
//        }
//    }

    static long moveWithMasks(long state, int fromMask, int toMask, boolean active) {
        state &= ~(((long) fromMask) << getMaskOffset(state, active));
        state |= (((long) toMask) << getMaskOffset(state, active));

        if (isMillMaker(state, toMask, active)) {
            return setBit(state, OFFSET_IS_REMOVE_TURN);
        } else {
            return switchActivePlayer(state);
        }
    }

//    public static long place(long state, int index) {
//        if (getCurrentAction(state) != ACTION_PLACE) {
//            throw new IllegalStateException("not a place turn");
//        }
//
//        state = setBit(state, getMaskOffset(state, true) + index);
//        state -= (1L << ((((state >>> OFFSET_ACTIVE_PLAYER) & 1) != 0) ? OFFSET_TRUE_COUNT : OFFSET_FALSE_COUNT));
//
//        if (isMillMaker(state, 1 << index)) {
//            return setBit(state, OFFSET_IS_REMOVE_TURN);
//        } else {
//            return switchActivePlayer(state);
//        }
//    }

    static long placeWithMask(long state, int mask, boolean active) {
        state |= (((long) mask) << getMaskOffset(state, active));
        state -= (1L << (((((state >>> OFFSET_ACTIVE_PLAYER) & 1) != 0) == active) ? OFFSET_TRUE_COUNT : OFFSET_FALSE_COUNT));

        if (isMillMaker(state, mask, active)) {
            return setBit(state, OFFSET_IS_REMOVE_TURN);
        } else {
            return switchActivePlayer(state);
        }
    }

//    public static long remove(long state, int index) {
//        if (getCurrentAction(state) != ACTION_REMOVE) {
//            throw new IllegalStateException("not a remove turn");
//        }
//
//        state = clearBit(state, getMaskOffset(state, false) + index);
//        state = clearBit(state, OFFSET_IS_REMOVE_TURN);
//
//        return switchActivePlayer(state);
//    }

    static long removeWithMask(long state, int mask, boolean active) {
        state &= ~(((long) mask) << getMaskOffset(state, !active));
        state = clearBit(state, OFFSET_IS_REMOVE_TURN);

        return switchActivePlayer(state);
    }

    static boolean isWinner(long state, boolean active) {
        if (getCount(state, !active) > 0) {
            return false;
        }
        if (getAvailableMoves(state, !active) == 0) {
            return true;
        }
        int positions = getMask(state, !active);
        positions &= (positions - 1);
        return (positions & (positions - 1)) == 0;
    }

/*
    private static boolean isMillMaker(long state, int index) {
        int positions = getMask(state, true);
        int mask = (1 << index);

        if ((index % 6) < 3) {
            if (((0xFFFFFF ^ positions) & ((mask << 3) | (mask >> 21) | (mask << 6) | (mask >> 18))) == 0) {
                return true;
            }
            if (((0xFFFFFF ^ positions) & ((mask >> 3) | (mask << 21) | (mask >> 6) | (mask << 18))) == 0) {
                return true;
            }
        } else {
            if (((0xFFFFFF ^ positions) & ((mask << 3) | (mask >> 21) | (mask >> 3) | (mask << 21))) == 0) {
                return true;
            }
            int row = (index / 3) * 3;
            if ((((1 << row) | (1 << (row + 1)) | (1 << (row + 2))) & (0xFFFFFF ^ (positions | mask))) == 0) {
                return true;
            }
        }
        return false;
    }
*/

    static int getAvailableMoveCount(long state, boolean active) {
        int count = 0;
        int moves = Board.getAvailableMoves(state, active);
        while (moves != 0) {
            int move = moves & -moves;
            moves &= moves - 1;
            count += Board.countBits(Board.getAvailableMovesToMask(state, move, active));
        }
        return count;
    }

    static int getMillCount(long state, boolean active) {
        int count = 0;
        int mask = getPlayerMask(state, active);
        for (int i = 0; i < millTable.length; i++) {
            int mill = millTable[i];
            if (((mask & mill) ^ mill) == 0) {
                count++;
            }
        }
        return count;
    }

    static boolean isMillMaker(long state, int mask, boolean active) {
        int positions = getMask(state, active);

        if ((mask & 0b000111000111000111000111) != 0) {
            if (((0xFFFFFF ^ positions) & ((mask << 3) | (mask >> 21) | (mask << 6) | (mask >> 18))) == 0) {
                return true;
            }
            if (((0xFFFFFF ^ positions) & ((mask >> 3) | (mask << 21) | (mask >> 6) | (mask << 18))) == 0) {
                return true;
            }
        } else {
            if (((0xFFFFFF ^ positions) & ((mask << 3) | (mask >> 21) | (mask >> 3) | (mask << 21))) == 0) {
                return true;
            }
            if (((0xFFFFFF ^ positions) & (((mask << 1) | (mask << 2) | (mask >>> 1) | (mask >>> 2)) & 0b111000111000111000111000)) == 0) {
                return true;
            }
        }
        return false;
    }

    private static int[] availableTable = new int[]{
            0b001000000000000000001000,
            0b010000000000000000010000,
            0b100000000000000000100000,
            0b000000000000000001010001,
            0b000000000000000010101010,
            0b000000000000000100010100,
            0b000000000000001000001000,
            0b000000000000010000010000,
            0b000000000000100000100000,
            0b000000000001010001000000,
            0b000000000010101010000000,
            0b000000000100010100000000,
            0b000000001000001000000000,
            0b000000010000010000000000,
            0b000000100000100000000000,
            0b000001010001000000000000,
            0b000010101010000000000000,
            0b000100010100000000000000,
            0b001000001000000000000000,
            0b010000010000000000000000,
            0b100000100000000000000000,
            0b010001000000000000000001,
            0b101010000000000000000010,
            0b010100000000000000000100
            //011000011000011000011000 << 1
            //110000110000110000110000 >> 1
    };

    private final static int millTable[] = new int[] {
            0b000000000000000001001001,
            0b000000000000000010010010,
            0b000000000000000100100100,
            0b000000000001001001000000,
            0b000000000010010010000000,
            0b000000000100100100000000,
            0b000001001001000000000000,
            0b000010010010000000000000,
            0b000100100100000000000000,
            0b001001000000000000000001,
            0b010010000000000000000010,
            0b100100000000000000000100,
            0b000000000000000000111000,
            0b000000000000111000000000,
            0b000000111000000000000000,
            0b111000000000000000000000
    };
}
