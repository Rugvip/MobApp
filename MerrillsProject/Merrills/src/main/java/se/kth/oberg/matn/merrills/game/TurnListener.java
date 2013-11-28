package se.kth.oberg.matn.merrills.game;

public interface TurnListener {
    public void onNextTurn(boolean player, TurnType type);
}
