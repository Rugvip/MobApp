package se.kth.oberg.matn.merrills;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;

import se.kth.oberg.matn.merrills.game.GameState;
import se.kth.oberg.matn.merrills.game.PieceAddListener;
import se.kth.oberg.matn.merrills.game.PieceMoveListener;
import se.kth.oberg.matn.merrills.game.PieceRemoveListener;
import se.kth.oberg.matn.merrills.game.PieceSelectListener;

public class BoardView extends SurfaceView implements SurfaceHolder.Callback{
    private Piece[] pieces = new Piece[24];
    private List<Piece> removedPieces = new ArrayList<>();
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

    public BoardView(Context context, GameState gameState) {
        super(context);

        this.gameState = gameState;
        getHolder().addCallback(this);

        falseDrawable = context.getResources().getDrawable(R.drawable.false_piece);
        trueDrawable = context.getResources().getDrawable(R.drawable.true_piece);
        backgroundDrawable = context.getResources().getDrawable(R.drawable.board);

        gameState.addPieceAddListener(pieceAddListener);
        gameState.addPieceRemoveListener(pieceRemoveListener);
        gameState.addPieceMoveListener(pieceMoveListener);
        gameState.addPieceSelectListener(pieceSelectListener);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        Log.e("Holder.Callback", "surfaceCreated");
        mainThread = new RenderThread(getHolder());
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
            pieces[index] = new Piece(player ? trueDrawable : falseDrawable);
            pieces[index].animateSpawn(Dimensions.getPoint(index));
        }
    };

    private PieceRemoveListener pieceRemoveListener = new PieceRemoveListener() {
        @Override
        public void onPieceRemoved(int index) {
            removedPieces.add(pieces[index]);
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
        public void onPieceSelect(int index, boolean selected) {
            pieces[index].setSelected(selected);
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
                    for (int index = 0; index < 24; index++) {
                        if (null != pieces[index]) {
                            pieces[index].draw(canvas, seven);
                        }
                    }

                    for (Piece piece : removedPieces) {
                        piece.draw(canvas, seven);
                    }

                    Markers.GREEN.draw(canvas, gameState.getSelectionMoves(), seven);
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
