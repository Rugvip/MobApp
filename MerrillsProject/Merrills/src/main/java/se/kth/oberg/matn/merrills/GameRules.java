package se.kth.oberg.matn.merrills;


public class GameRules {
    private static final String template = "" +
            "C------F------I\n" +
            "|      |      |\n" +
            "| B----E----H |\n" +
            "| |    |    | |\n" +
            "| | A--D--G | |\n" +
            "| | |     | | |\n" +
            "X-W-V     J-K-L\n" +
            "| | |     | | |\n" +
            "| | S--P--M | |\n" +
            "| |    |    | |\n" +
            "| T----Q----N |\n" +
            "|      |      |\n" +
            "U------R------O";

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

    public static boolean isMillMaker(int index, int positions) {
        int mask = (1 << index);
        if ((index % 6) < 3) {
            if ((~positions & ((mask << 3) | (mask >> 21) | (mask << 6) | (mask >> 18))) == 0) {
                return true;
            }
            if ((~positions & ((mask >> 3) | (mask << 21) | (mask >> 6) | (mask << 18))) == 0) {
                return true;
            }
        } else {
            if ((~positions & ((mask << 3) | (mask >> 21) | (mask >> 3) | (mask << 21))) == 0) {
                return true;
            }
            int row = (index / 3) * 3;
            if ((((1 << row) | (1 << (row + 1)) | (1 << (row + 2))) & ~(positions | mask)) == 0) {
                return true;
            }
        }
        return false;
    }

    public boolean isMillMaker(boolean player, int index) {
        return isMillMaker(index, player ? truePlayer : falsePlayer) && (getAvailableMoves(player) & (1 << index)) != 0;
    }

    private int getAvailableMoves(int positions) {
        int ret = getAvailableForMask(positions);
        return ret & ~(truePlayer | falsePlayer);
    }

    public int getAvailableMoves(boolean player) {
        return getAvailableMoves(player ? truePlayer : falsePlayer);
    }

    public boolean isFreeSpot(int index) {
        return ((1 << index) & (~(truePlayer | falsePlayer))) != 0;
    }

    public boolean remove(boolean player, int index) {
        boolean ret = false;
        if (player) {
            ret = (truePlayer & (1 << index)) != 0;
            truePlayer &= ~(1 << index);
        } else {
            ret = (falsePlayer & (1 << index)) != 0;
            falsePlayer &= ~(1 << index);
        }
        return ret;
    }

    public boolean hasLost(boolean player) {
        int positions = player ? truePlayer : falsePlayer;
        positions = positions & (positions - 1);
        return (positions & (positions - 1)) == 0;
    }

    public static String drawPositions(int positions) {
        int pos;
        StringBuilder sb = new StringBuilder(template);
        for (char c = 'A'; c < 'Y'; c++) {
            for (pos = 0; sb.charAt(pos) != c; pos++) ;
            sb.setCharAt(pos, (positions & (1 << (c - 'A'))) == 0 ? ' ' : '@');
        }
        return sb.toString();
    }

    public static String drawPositions(int pos1, int pos2) {
        int pos;
        StringBuilder sb = new StringBuilder(template);
        for (char c = 'A'; c < 'Y'; c++) {
            for (pos = 0; sb.charAt(pos) != c; pos++) ;
            boolean b1 = (pos1 & (1 << (c - 'A'))) != 0;
            boolean b2 = (pos2 & (1 << (c - 'A'))) != 0;
            sb.setCharAt(pos, b1 && b2 ? '&' : (b1 ? '1' : b2 ? '2' : ' '));
        }
        return sb.toString();
    }

    public char getChar(int pos) {
        int mask = 1 << pos;
        if ((truePlayer & falsePlayer & mask) != 0) {
            return '&';
        }
        if ((truePlayer & mask) != 0) {
            return 't';
        }
        if ((falsePlayer & mask) != 0) {
            return 'f';
        }
        return ' ';
    }

    public String toString() {
        int pos;
        StringBuilder sb = new StringBuilder(template);
        for (char c = 'A'; c < 'Y'; c++) {
            for (pos = 0; sb.charAt(pos) != c; pos++) ;
            sb.setCharAt(pos, getChar(c - 'A'));
        }
        return sb.toString();
    }

    public void add(boolean player, int index) {
        if(player){
            truePlayer |= (1 << index);
        }else{
            falsePlayer |= (1<<index);
        }

    }
}
