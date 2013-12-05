package se.kth.oberg.matn.merrills.game;

public enum TurnType {
    PLACE("place a piece"),
    REMOVE("remove a piece"),
    CHOOSE_FROM("choose piece to move"),
    CHOOSE_TO("choose piece destination"),
    WIN(" won!");

    private String message;
    private TurnType(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return message;
    }
}
