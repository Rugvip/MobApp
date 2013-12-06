package se.kth.oberg.matn.mtb;

import android.os.AsyncTask;
import android.util.Log;

import java.text.DecimalFormat;
import java.util.concurrent.CyclicBarrier;

public class Calculator extends AsyncTask<Integer, Void, Long> {
    private static int DEPTH = 16;

    public static void setDepth(int depth) {
        DEPTH = depth;
    }

    private static double doWork(double in, int depth) {
        if (depth <= 0) {
            return in;
        }
        return doWork(Math.sin(Math.cos(Math.sqrt(Math.log(Math.log10(Math.toRadians(Math.toDegrees(in + 0.1))))))), depth - 1)
                + doWork(Math.sin(Math.cos(Math.sqrt(Math.log(Math.log10(Math.toRadians(Math.toDegrees(in - 0.1))))))), depth - 1);
    }

    @Override
    protected Long doInBackground(Integer... counts) {
        if (counts.length != 2) {
            throw new IllegalArgumentException("Failed length");
        }

        final int threadCount = counts[0];
        final int workCount = counts[1];
        final Thread threads[] = new Thread[threadCount];
        final double doubles[][] = new double[threadCount][workCount];

        for (int i = 0; i < threadCount; i++) {
            for (int j = 0; j < workCount; j++) {
                doubles[i][j] = j * threadCount + i + workCount * 10 + Math.random();
            }
        }

        final CyclicBarrier startBarrier = new CyclicBarrier(threadCount + 1);
        final CyclicBarrier endBarrier = new CyclicBarrier(threadCount + 1);

        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            threads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Log.i("Threading", "thread " + index + " start awaiting barrier");
                        startBarrier.await();
                        Log.i("Threading", "thread " + index + " start passed barrier");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    synchronized (Calculator.this) {}
                    for (int i = 0; i < workCount; i++) {
                        doubles[index][i] = doWork(doubles[index][i], DEPTH);
                    }
                    try {
                        Log.i("Threading", "2 thread " + index + " end awaiting barrier");
                        endBarrier.await();
                        Log.i("Threading", "2 thread " + index + " end passed barrier");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Log.i("Calculator", "thread " + index + " completed at " + format.format(System.nanoTime() / 1000000000.0));
                }
            });
        }

        for (int i = 0; i < threadCount; i++) {
            threads[i].start();
            Log.i("Calculator", "started thread " + i + " at " + format.format(System.nanoTime() / 1000000000.0));
        }

        synchronized (this) {
            try {
                Log.i("Threading", "main awaiting start barrier");
                startBarrier.await();
                Log.i("Threading", "main passed start barrier");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        final long start = System.nanoTime();
        Log.i("Threading", "notified threads");

        try {
            Log.i("Threading", "main awaiting end barrier");
            endBarrier.await();
            Log.i("Threading", "main passed end barrier");
        } catch (Exception e) {
            e.printStackTrace();
        }

        final long end = System.nanoTime();

        for (int i = 0; i < threadCount; i++) {
            try {
                threads[i].join();
                Log.i("Threading", "joined thread " + i);
            } catch (InterruptedException e) {
                Log.e("Calculator", "failed to join thread " + i);
            }
        }

        Log.i("Calculator", "start: " + format.format(start / 1000000000.0) +
                " end: " + format.format(end / 1000000000.0) +
                " diff: " + format.format((end - start) / 1000000000.0));
        return end - start;
    }

    private final DecimalFormat format = new DecimalFormat("#.###");
}
