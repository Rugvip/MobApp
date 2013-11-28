

public class Main {

    public static void main(String[] args) {
        MerrillsModel mm = new MerrillsModel();
        mm.falsePlayer = 0b00000000_00000000_00000000;
        mm.truePlayer = 0b11100011_01101011_01011011;
        System.out.println("model:\n" + mm);
        // System.out.println("white:\n" + MerrillsModel.drawPositions(mm.getWhiteAvailableMoves()));
        // System.out.println("black:\n" + MerrillsModel.drawPositions(mm.getBlackAvailableMoves()));
        int res = 0;
        for (int i = 0; i < 24; i++) {
            res |= mm.isMillMaker(true, i) ? 1 << i : 0;
            // System.out.println(Integer.toBinaryString(MerrillsModel.avail(i)) + ",");
            // System.out.println("avail " + i + " " + (1 << i) + " " + MerrillsModel.avail(i) + "\n" + MerrillsModel.drawPositions(1 << i, MerrillsModel.availbl(1 << i)));
        }
        System.out.println(MerrillsModel.drawPositions(res));
    }
}
