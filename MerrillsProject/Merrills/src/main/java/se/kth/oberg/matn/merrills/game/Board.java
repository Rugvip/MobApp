package se.kth.oberg.matn.merrills.game;

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

    public static int getMaskOffset(long state, boolean active) {
        return ((((state >>> OFFSET_ACTIVE_PLAYER) & 1) != 0) == active) ? OFFSET_TRUE : OFFSET_FALSE;
    }

    public static int getTrueMask(long state) {
        return (int) ((state >>> OFFSET_TRUE) & MASK_MASK);
    }

    public static int getFalseMask(long state) {
        return (int) ((state >>> OFFSET_FALSE) & MASK_MASK);
    }

    public static int getTrueCount(long state) {
        return (int) ((state >>> OFFSET_TRUE_COUNT) & COUNT_MASK);
    }

    public static int getFalseCount(long state) {
        return (int) ((state >>> OFFSET_FALSE_COUNT) & COUNT_MASK);
    }

    public static int getCount(long state, boolean active) {
        return (int) ((state >>> ((((state >>> OFFSET_ACTIVE_PLAYER) & 1) != 0) == active ? OFFSET_TRUE_COUNT : OFFSET_FALSE_COUNT)) & COUNT_MASK);
    }

    public static boolean getActivePlayer(long state) {
        return ((state >>> OFFSET_ACTIVE_PLAYER) & 1) != 0;
    }

    public static boolean isPlayer(long state, int index, boolean active) {
        return getBit(state, (getActivePlayer(state) == active ? OFFSET_TRUE : OFFSET_FALSE) + index);
    }

    public static int getMask(long state, boolean active) {
        return (int) ((state >>> (((((state >>> OFFSET_ACTIVE_PLAYER) & 1) != 0) == active) ? OFFSET_TRUE : OFFSET_FALSE)) & MASK_MASK);
    }

    private static int getBothMask(long state) {
        return (int) (((state >>> OFFSET_TRUE) | (state >>> OFFSET_FALSE)) & MASK_MASK);
    }

    public static int getAvailableForPos(long state, int index) {
        if (isFlying(state)) {
            return ~getBothMask(state);
        }
        return availableTable[index] & ~getBothMask(state);
    }

    public static int getMovableTo(long state, int index) {
        if (isFlying(state)) {
            return getMask(state, true);
        }
        return availableTable[index] & getMask(state, true);
    }

    public static int getCurrentAction(long state) {
        if (isWinner(state)) {
            return ACTION_WON;
        } else if (getBit(state, OFFSET_IS_REMOVE_TURN)) {
            return ACTION_REMOVE;
        } else if (((state >>> OFFSET_ACTIVE_PLAYER) & 1) != 0 ? ((state >>> OFFSET_TRUE_COUNT) & COUNT_MASK) > 0 :  ((state >>> OFFSET_FALSE_COUNT) & COUNT_MASK) > 0) {
            return ACTION_PLACE;
        } else {
            return ACTION_MOVE;
        }
    }

    public static int getAvailablePlacements(long state) {
        return ~getBothMask(state);
    }

    public static boolean isAvailablePlacement(long state, int index) {
        return getBit(getAvailablePlacements(state), index);
    }

    private static boolean isFlying(long state) {
        if (!getBit(state, OFFSET_ALLOW_FLIGHT)) {
            return false;
        }
        int positions = getMask(state, true);
        positions &= (positions - 1);
        positions &= (positions - 1);
        return ((positions & (positions - 1)) == 0);
    }

    public static int getAvailableMoves(long state, boolean active) {
        if (isFlying(state)) {
            return ~getBothMask(state);
        }
        int positions = getMask(state, active);
        int ret = 0;
        ret |= (positions << 3) | (positions >> 21);
        ret |= (positions >> 3) | (positions << 21);
        ret |= (positions & 0b011000011000011000011000) << 1;
        ret |= (positions & 0b110000110000110000110000) >> 1;
        return INT_MASK_MASK & (ret & ~getBothMask(state));
    }

    public static boolean isValidMove(long state, int fromIndex, int toIndex) {
        return (getMovableTo(state, toIndex) & (1 << fromIndex)) != 0;
    }

    public static int getAvailableRemoves(long state) {
        int mask = getMask(state, false);
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

    public static boolean isAvailableRemove(long state, int index) {
        return getBit(getAvailableRemoves(state), index);
    }

    private static long switchActivePlayer(long state) {
        if (getCurrentAction(state) == ACTION_WON) {
            return state;
        } else {
            return toggleBit(state, OFFSET_ACTIVE_PLAYER);
        }
    }

    public static long move(long state, int fromIndex, int toIndex) {
        if (getCurrentAction(state) != ACTION_MOVE) {
            throw new IllegalStateException("not a move turn");
        }

        state = clearBit(state, getMaskOffset(state, true) + fromIndex);
        state = setBit(state, getMaskOffset(state, true) + toIndex);

        if (isMillMaker(state, toIndex)) {
            return setBit(state, OFFSET_IS_REMOVE_TURN);
        } else {
            return switchActivePlayer(state);
        }
    }

    public static long place(long state, int index) {
        if (getCurrentAction(state) != ACTION_PLACE) {
            throw new IllegalStateException("not a place turn");
        }

        state = setBit(state, getMaskOffset(state, true) + index);
        state -= (1L << ((((state >>> OFFSET_ACTIVE_PLAYER) & 1) != 0) ? OFFSET_TRUE_COUNT : OFFSET_FALSE_COUNT));

        if (isMillMaker(state, index)) {
            return setBit(state, OFFSET_IS_REMOVE_TURN);
        } else {
            return switchActivePlayer(state);
        }
    }

    public static long remove(long state, int index) {
        if (getCurrentAction(state) != ACTION_REMOVE) {
            throw new IllegalStateException("not a remove turn");
        }

        state = clearBit(state, getMaskOffset(state, false) + index);
        state = clearBit(state, OFFSET_IS_REMOVE_TURN);

        return switchActivePlayer(state);
    }

    public static boolean isWinner(long state) {
        if (getCount(state, false) > 0) {
            return false;
        }
        if (getAvailableMoves(state, false) == 0) {
            return true;
        }
        int positions = getMask(state, false);
        positions &= (positions - 1);
        return (positions & (positions - 1)) == 0;
    }

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
