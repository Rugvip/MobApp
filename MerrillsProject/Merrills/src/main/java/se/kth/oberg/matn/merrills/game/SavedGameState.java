package se.kth.oberg.matn.merrills.game;

import android.graphics.Point;
import android.os.Bundle;
import se.kth.oberg.matn.merrills.Dimensions;

public class SavedGameState {
    public static final String BUNDLE_NAME = "savedGameState";

    private static final int TRUE_OFFSET = 0;
    private static final int FALSE_OFFSET = 24;
    private static final int TRUE_COUNT_OFFSET = 48;
    private static final int FALSE_COUNT_OFFSET = 53;
    private static final int ACTIVE_PLAYER_OFFSET = 58;

    private SavedGameState() {
    }

    public static long mask(boolean activePlayer, int trueMask, int falseMask, int trueCount, int falseCount) {
        long mask = 0;
        mask |= ((long) trueMask) << TRUE_OFFSET;
        mask |= ((long) falseMask) << FALSE_OFFSET;
        mask |= ((long) trueCount) << TRUE_COUNT_OFFSET;
        mask |= ((long) falseCount) << FALSE_COUNT_OFFSET;
        mask |= (activePlayer ? 1L : 0L ) << FALSE_COUNT_OFFSET;
        return mask;
    }

    public static int getTrueMask(long mask) {
        return (int) ((mask >> TRUE_OFFSET) & 0xFFFFFFL);
    }

    public static int getFalseMask(long mask) {
        return (int) ((mask >> FALSE_OFFSET) & 0xFFFFFFL);
    }

    public static int getTrueCount(long mask) {
        return (int) ((mask >> TRUE_COUNT_OFFSET) & 0x1F);
    }

    public static int getFalseCount(long mask) {
        return (int) ((mask >> FALSE_COUNT_OFFSET) & 0x1F);
    }

    public static boolean getActivePlayer(long mask) {
        return ((mask >> ACTIVE_PLAYER_OFFSET) & 1) != 0;
    }
}