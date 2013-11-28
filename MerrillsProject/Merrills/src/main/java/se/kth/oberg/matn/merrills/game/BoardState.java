package se.kth.oberg.matn.merrills.game;

public class BoardState {
    public static final int TRUE_PLAYER = 1;
    public static final int FALSE_PLAYER = 0;
    public static final int NO_PLAYER = -1;

    public int truePlayer;
    public int falsePlayer;

    private static int[] availableLookup = new int[]{
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

    public static int getAvailableForPos(int index) {
        return availableLookup[index];
    }

    public static int getAvailableForMask(int positions) {
        int ret = 0;
        ret |= (positions << 3) | (positions >> 21);
        ret |= (positions >> 3) | (positions << 21);
        ret |= (positions & 0b011000011000011000011000) << 1;
        ret |= (positions & 0b110000110000110000110000) >> 1;
        return ret & 0xFFFFFF;
    }

    private static boolean isMillMaker(int index, int positions) {
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

    public int getAvailableMoves(boolean player) {
        return getAvailableForMask(player ? truePlayer : falsePlayer) & ~(truePlayer | falsePlayer);
    }

    public int getAvailableMoves(int index) {
        return getAvailableForPos(index) & ~(truePlayer | falsePlayer);
    }

    public boolean isValidMove(int fromIndex, int toIndex) {
        return (getAvailableMoves(fromIndex) & (1 << toIndex)) != 0;
    }

    public boolean isFreeSpot(int index) {
        return ((1 << index) & (~(truePlayer | falsePlayer))) != 0;
    }

    public boolean hasLost(boolean player) {
        if (getAvailableMoves(player) == 0) {
            return true;
        }
    int positions = player ? truePlayer : falsePlayer;
        positions = positions & (positions - 1);
        return (positions & (positions - 1)) == 0;
    }

    public int getPlayer(int index) {
        check();
        if (((1 << index) & truePlayer) != 0) {
            return TRUE_PLAYER;
        } else if (((1 << index) & falsePlayer) != 0) {
            return FALSE_PLAYER;
        } else {
            return NO_PLAYER;
        }
    }

    public boolean isPlayer(int index, boolean player) {
        return getPlayer(index) == (player ? BoardState.TRUE_PLAYER : BoardState.FALSE_PLAYER);
    }

    public void remove(int index) {
        truePlayer &= ~(1 << index);
        falsePlayer &= ~(1 << index);
        check();
    }

    public boolean add(int index, boolean player) {
        boolean ret = isMillMaker(index, player ? truePlayer : falsePlayer);
        if (player) {
            truePlayer |= (1 << index);
        } else {
            falsePlayer |= (1 << index);
        }
        check();
        return ret;
    }

    private void check() {
        if ((truePlayer & falsePlayer) != 0) {
            throw new IllegalStateException("Overlapping pieces");
        }
    }

    public boolean move(int fromIndex, int toIndex) {
        int playerType = getPlayer(fromIndex);
        if (playerType == NO_PLAYER) {
            throw new IllegalArgumentException("fromIndex is empty");
        }
        if ((getAvailableMoves(fromIndex) & (1 << toIndex)) == 0) {
            throw new IllegalArgumentException("illegal move");
        }
        remove(fromIndex);
        return add(toIndex, playerType == TRUE_PLAYER);
    }
}
