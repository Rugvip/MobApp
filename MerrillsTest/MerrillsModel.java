package se.kth.oberg.matn.merrills.test;

public class MerrillsModel {
    private static final String template = "" +
        "A------D------G\n" +
        "|\\     |     /|\n" +
        "| B----E----H |\n" +
        "| |\\   |   /| |\n" +
        "| | C--F--I | |\n" +
        "| | |     | | |\n" +
        "V W X     L K J\n" +
        "| | |     | | |\n" +
        "| | U--R--O | |\n" +
        "| |/   |   \\| |\n" +
        "| T----Q----N |\n" +
        "|/     |     \\|\n" +
        "S------P------M";
    public int black;
    public int white;

    public char getChar(int pos) {
        int mask = 1 << pos;
        if ((black & white & mask) != 0) {
            return '@';
        }
        if ((black & mask) != 0) {
            return '•';
        }
        if ((white & mask) != 0) {
            return '○';
        }
        return ' ';
    }

    private int getAvailableMoves(int positions) {
        int ret = 0;
        int free = ~(white | black);
        for (int pos = 0; pos < 24; pos++) {
            if (((1 << pos) & free) != 0) {

            }
        }
        return ret;
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
