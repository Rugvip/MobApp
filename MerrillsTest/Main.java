

public class Main {

    public static void main(String[] args) {
        MerrillsModel mm = new MerrillsModel();
        // mm.black = 0b01000100_00100001_01000000;
        mm.white = 0b00000011_01001000_00000100;
        System.out.println("model:\n" + mm);
        // System.out.println("white:\n" + MerrillsModel.drawPositions(mm.getWhiteAvailableMoves()));
        // System.out.println("black:\n" + MerrillsModel.drawPositions(mm.getBlackAvailableMoves()));
        for (int i = 0; i < 24; i++) {
            System.out.println(i + ": " + mm.isWhiteMillMaker(i));
            // System.out.println(Integer.toBinaryString(MerrillsModel.avail(i)) + ",");
            // System.out.println("avail " + i + " " + (1 << i) + " " + MerrillsModel.avail(i) + "\n" + MerrillsModel.drawPositions(1 << i, MerrillsModel.availbl(1 << i)));
        }
    }
}
