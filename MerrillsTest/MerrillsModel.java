
public class MerrillsModel {
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
    public int black;
    public int white;

    private static int[] availableLookup = new int[] {
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

    private static int[] rowLookup = new int[] {
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


    public boolean isWhiteMillMaker(int index) {
        return isMillMaker(index, white);
    }

    private int getAvailableMoves(int positions) {
        int ret = getAvailableForMask(positions);
        return ret & ~(white | black);
    }

    public int getWhiteAvailableMoves() {
        return getAvailableMoves(white);
    }

    public int getBlackAvailableMoves() {
        return getAvailableMoves(black);
    }

    private static boolean hasLost(int positions) {
        positions = positions & (positions - 1);
        return (positions & (positions - 1)) == 0;
    }

    public static String drawPositions(int positions) {
        int pos;
        StringBuilder sb = new StringBuilder(template);
        for (char c = 'A'; c < 'Y'; c++) {
            for (pos = 0; sb.charAt(pos) != c; pos++);
            sb.setCharAt(pos, (positions & (1 << (c - 'A'))) == 0 ? ' ' : '@');
        }
        return sb.toString();
    }
    public static String drawPositions(int pos1, int pos2) {
        int pos;
        StringBuilder sb = new StringBuilder(template);
        for (char c = 'A'; c < 'Y'; c++) {
            for (pos = 0; sb.charAt(pos) != c; pos++);
            boolean b1 = (pos1 & (1 << (c - 'A'))) != 0;
            boolean b2 = (pos2 & (1 << (c - 'A'))) != 0;
            sb.setCharAt(pos, b1 && b2 ? '&' : (b1 ? '1' : b2 ? '2' : ' '));
        }
        return sb.toString();
    }

    public char getChar(int pos) {
        int mask = 1 << pos;
        if ((black & white & mask) != 0) {
            return '&';
        }
        if ((black & mask) != 0) {
            return 'b';
        }
        if ((white & mask) != 0) {
            return 'w';
        }
        return ' ';
    }

    public String toString() {
        int pos;
        StringBuilder sb = new StringBuilder(template);
        for (char c = 'A'; c < 'Y'; c++) {
            for (pos = 0; sb.charAt(pos) != c; pos++);
            sb.setCharAt(pos, getChar(c - 'A'));
        }
        return sb.toString();
    }
/*
0------3------6
|\     |     /|
| 1----4----7 |
| |\   |   /| |
| | 2--5--8 | |
| | |     | | |
O O O     b a 9
| | |     | | |
| | O-- --O | |
| |/   |   \| |
| O----O----d |
|/     |     \|
O------O------c
*/
}
