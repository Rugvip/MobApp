package se.kth.oberg.matn.merrills.test;

public class Main {

    public static void main(String[] args) {
        MerrillsModel mm = new MerrillsModel();
        mm.black = 0b01100101_00101101_01001010;
        mm.white = 0b00011001_01011000_00110010;
        System.out.println("model:\n" + mm);
        System.out.println("wat: " + (10/3));
    }
}
