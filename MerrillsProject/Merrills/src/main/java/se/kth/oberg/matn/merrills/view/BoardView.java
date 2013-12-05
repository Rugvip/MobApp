package se.kth.oberg.matn.merrills.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import se.kth.oberg.matn.merrills.Dimensions;
import se.kth.oberg.matn.merrills.game.Board;
import se.kth.oberg.matn.merrills.game.GameState;
import se.kth.oberg.matn.merrills.game.PieceAddListener;
import se.kth.oberg.matn.merrills.game.PieceMoveListener;
import se.kth.oberg.matn.merrills.game.PieceRemoveListener;
import se.kth.oberg.matn.merrills.game.PieceSelectListener;
import se.kth.oberg.matn.merrills.game.TurnListener;
import se.kth.oberg.matn.merrills.game.TurnType;

public class BoardView extends SurfaceView implements SurfaceHolder.Callback{
    private final PieceView[] pieces = new PieceView[24];
    private final List<PieceView> removedPieces = new ArrayList<>();
    private final LinkedList<PieceView> trueQueue = new LinkedList<>();
    private final LinkedList<PieceView> falseQueue = new LinkedList<>();
    private Thread mainThread;
    private GameState gameState;
    private Drawable backgroundDrawable;
    private Drawable trueDrawable;
    private Drawable falseDrawable;
    private Dimensions dimensions;

    private static Paint boardPaint = new Paint();
    static {
        boardPaint.setStyle(Paint.Style.STROKE);
        boardPaint.setColor(0xFF_000000);
    }

    public void load(long savedGameState) {
        boolean activePlayer = Board.getActivePlayer(savedGameState);
        int trueCount = Board.getPlayerCount(savedGameState, true);
        int falseCount = Board.getPlayerCount(savedGameState, false);
        int trueMask = Board.getPlayerMask(savedGameState, true);
        int falseMask = Board.getPlayerMask(savedGameState, false);
        Log.i("BoardView", "Loaded: " + activePlayer + " " + trueCount + " " + falseCount + " " + Integer.toBinaryString(trueMask) + " " + Integer.toBinaryString(falseMask));

        for (int i = 0; i < trueCount; i++) {
            PieceView truePiece = new PieceView(trueDrawable);
            trueQueue.add(truePiece);
        }
        for (int i = 0; i < falseCount; i++) {
            PieceView falsePiece = new PieceView(falseDrawable);
            falseQueue.add(falsePiece);
        }
        for (int i = 0; i < 24; i++) {
            PieceView piece = null;
            if (((1 << i) & trueMask) != 0) {
                piece = new PieceView(trueDrawable);
            }
            if (((1 << i) & falseMask) != 0) {
                piece = new PieceView(falseDrawable);
            }
            if (piece != null) {
                PointF point = Dimensions.getPoint(i);
                piece.setX(point.x);
                piece.setY(point.y);
                pieces[i] = piece;
            }
        }

        switch (Board.getCurrentAction(savedGameState)) {
            case Board.ACTION_REMOVE:
                turnString = TurnType.REMOVE.toString();
                break;
            case Board.ACTION_MOVE:
                turnString = TurnType.CHOOSE_FROM.toString();
                break;
            case Board.ACTION_PLACE:
                turnString = TurnType.PLACE.toString();
                break;
            case Board.ACTION_WON:
                turnString = TurnType.WIN.toString();
                break;
            case Board.ACTION_LOST:
            default:
                throw new IllegalStateException("illegal action");
        }
        turnString = (activePlayer ? "Player 1: " : "Player 2: ") + turnString;
        turnPaint = activePlayer ? trueTurnPaint : falseTurnPaint;
    }

    public boolean positionedPieces = false;

    public void positionPieceQueues(boolean vertical) {
        int i = 0;
        for (PieceView piece : trueQueue) {
            i++;
            if (vertical) {
                piece.setX(1 + i * 0.2f);
                piece.setY(7.5f);
            } else {
                piece.setX(-0.5f);
                piece.setY(2.5f + i * 0.2f);
            }
        }
        i = 0;
        for (PieceView piece : falseQueue) {
            i++;
            if (vertical) {
                piece.setX(6 - i * 0.2f);
                piece.setY(7.5f);
            } else {
                piece.setX(7.5f);
                piece.setY(2.5f + i * 0.2f);
            }
        }
    }

    public void reset() {
        for (int i = 0; i < 9; i++) {
            PieceView truePiece = new PieceView(trueDrawable);
            PieceView falsePiece = new PieceView(falseDrawable);
            trueQueue.add(truePiece);
            falseQueue.add(falsePiece);
        }
    }

    public BoardView(Context context, GameState gameState) {
        super(context);

        this.gameState = gameState;
        getHolder().addCallback(this);

//        R.drawable.red_piece
        trueDrawable = context.getResources().getDrawable(
                context.getResources()
                        .getIdentifier(PreferenceManager.getDefaultSharedPreferences(context)
                                .getString("player1_color","red_piece"),"drawable", context.getPackageName()));
        falseDrawable = context.getResources().getDrawable(
                context.getResources()
                        .getIdentifier(PreferenceManager.getDefaultSharedPreferences(context)
                                .getString("player2_color","blue_piece"),"drawable", context.getPackageName()));
        backgroundDrawable = context.getResources().getDrawable(
                context.getResources()
                        .getIdentifier(PreferenceManager.getDefaultSharedPreferences(context)
                                .getString("board_color","board"),"drawable", context.getPackageName()));

        gameState.addPieceAddListener(pieceAddListener);
        gameState.addPieceRemoveListener(pieceRemoveListener);
        gameState.addPieceMoveListener(pieceMoveListener);
        gameState.addPieceSelectListener(pieceSelectListener);
        gameState.addTurnListener(turnListener);

    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        Log.e("Holder.Callback", "surfaceCreated");
        mainThread = new RenderThread(surfaceHolder);
        mainThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
        Log.e("Holder.Callback", "surfaceChanged");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        Log.e("Holder.Callback", "surfaceDestroyed");
        mainThread.interrupt();
    }

    private PieceAddListener pieceAddListener = new PieceAddListener() {
        @Override
        public void onPieceAdded(boolean player, int index) {
            if (player) {
                synchronized (trueQueue) {
                    pieces[index] = trueQueue.removeLast();
                }
            } else {
                synchronized (falseQueue) {
                    pieces[index] = falseQueue.removeLast();
                }
            }
            pieces[index].animateMove(Dimensions.getPoint(index));
        }
    };

    private PieceRemoveListener pieceRemoveListener = new PieceRemoveListener() {
        @Override
        public void onPieceRemoved(int index) {
            synchronized (removedPieces) {
                removedPieces.add(pieces[index]);
            }
            pieces[index].animateRemove();
            pieces[index] = null;
        }
    };

    private PieceMoveListener pieceMoveListener = new PieceMoveListener() {
        @Override
        public void onPieceMoved(int fromIndex, int toIndex) {
            pieces[toIndex] = pieces[fromIndex];
            pieces[toIndex].animateMove(Dimensions.getPoint(toIndex));
        }
    };

    private PieceSelectListener pieceSelectListener = new PieceSelectListener() {
        @Override
        public void onPieceSelected(int index, boolean selected) {
            pieces[index].setSelected(selected);
        }
    };

    private String turnString;
    private static final Paint trueTurnPaint = new Paint();
    private static final Paint falseTurnPaint = new Paint();
    static {
        falseTurnPaint.setColor(0xFF_0000FF);
        trueTurnPaint.setColor(0xFF_FF0000);
    }
    private Paint turnPaint;

    private TurnListener turnListener = new TurnListener() {
        @Override
        public void onTurn(boolean player, TurnType type) {
            turnPaint = player ? trueTurnPaint : falseTurnPaint;
            turnString = (player ? "Player 1: " : "Player 2: ") + type.toString();
        }
    };

    public class RenderThread extends Thread {
        private SurfaceHolder holder;

        public RenderThread(SurfaceHolder holder) {
            this.holder = holder;
        }

        @Override
        public void run() {
            while (true) {
                Canvas canvas = holder.lockCanvas();
                if (canvas != null) {
                    canvas.drawColor(0xFF_FFFFFF);

                    dimensions = Dimensions.calculate(canvas.getWidth(), canvas.getHeight(), dimensions);

                    if (!positionedPieces) {
                        positionPieceQueues(dimensions.isVertical());
                        positionedPieces = true;
                    }

                    canvas.translate(dimensions.getOffsetX(), dimensions.getOffsetY());
                    backgroundDrawable.setBounds(0, 0, dimensions.getSize(), dimensions.getSize());
                    backgroundDrawable.draw(canvas);

                    float size = dimensions.getSize();
                    float seven = size / 7.0f;
                    float seven2 = size / 14.0f;

                    boardPaint.setStrokeWidth(size / 100.0f);

                    canvas.drawRect(seven2 * 1.0f, seven2 * 1.0f, seven2 * 13.0f, seven2 * 13.0f, boardPaint);
                    canvas.drawRect(seven2 * 3.0f, seven2 * 3.0f, seven2 * 11.0f, seven2 * 11.0f, boardPaint);
                    canvas.drawRect(seven2 * 5.0f, seven2 * 5.0f, seven2 * 09.0f, seven2 * 09.0f, boardPaint);
                    canvas.drawLine(seven2 * 7.0f, seven2 * 1.0f, seven2 * 07.0f, seven2 * 05.0f, boardPaint);
                    canvas.drawLine(seven2 * 9.0f, seven2 * 7.0f, seven2 * 13.0f, seven2 * 07.0f, boardPaint);
                    canvas.drawLine(seven2 * 7.0f, seven2 * 9.0f, seven2 * 07.0f, seven2 * 13.0f, boardPaint);
                    canvas.drawLine(seven2 * 1.0f, seven2 * 7.0f, seven2 * 05.0f, seven2 * 07.0f, boardPaint);

                    Markers.BLACK.draw(canvas, ~0, seven);

                    synchronized (trueQueue) {
                        for (PieceView piece : trueQueue) {
                            piece.draw(canvas, seven);
                        }
                    }

                    synchronized (falseQueue) {
                        for (PieceView piece : falseQueue) {
                            piece.draw(canvas, seven);
                        }
                    }

                    for (int index = 0; index < 24; index++) {
                        if (null != pieces[index]) {
                            pieces[index].draw(canvas, seven);
                        }
                    }

                    synchronized (removedPieces) {
                        for (PieceView piece : removedPieces) {
                            piece.draw(canvas, seven);
                        }
                    }

                    Markers.GREEN.draw(canvas, gameState.getSelectionMoveMask(), seven);
                    Markers.GREEN.draw(canvas, gameState.getMoveMask(), seven);
                    Markers.CROSS.draw(canvas, gameState.getDeleteMask(), seven);

                    if (turnString != null) {
                        turnPaint.setStrokeWidth(0.2f * seven);
                        turnPaint.setTextSize(seven * 0.4f);
                        turnPaint.setTextAlign(Paint.Align.CENTER);
                        canvas.drawText(turnString, 3.5f * seven, 8.5f * seven, turnPaint);
                    }
                } else {
                    break;
                }
                holder.unlockCanvasAndPost(canvas);

                try {
                    Thread.sleep(33);
                } catch (Exception ignored) {
                }
            }
        }
    }
}
