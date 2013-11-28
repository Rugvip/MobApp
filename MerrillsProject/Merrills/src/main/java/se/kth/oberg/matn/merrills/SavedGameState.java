package se.kth.oberg.matn.merrills;

public class SavedGameState {
    private int trueMask;
    private int falseMask;
    private int trueCount;
    private int falseCount;
    private String name;
    private int id;
    private int turn;

    public SavedGameState(int id, String name, int turn, int trueMask, int falseMask, int trueCount, int falseCount) {
        this.turn = turn;
        this.trueMask = trueMask;
        this.falseMask = falseMask;
        this.trueCount = trueCount;
        this.falseCount = falseCount;
        this.id = id;
        this.name = name;
    }

    public int getTrueMask() {
        return trueMask;
    }

    public int getFalseMask() {
        return falseMask;
    }

    public int getTrueCount() {
        return trueCount;
    }

    public int getFalseCount() {
        return falseCount;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public int getTurn() {
        return turn;
    }
}