package se.kth.oberg.matn.mtb;

import android.os.AsyncTask;
import android.util.Log;

import java.text.DecimalFormat;

public class Calculator extends AsyncTask<Integer, Void, Long> {
    private static final int DEPTH = 16;

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

        for (int i = 0; i < 10; i++) {
            doWork(Math.random(), DEPTH);
        }

        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            threads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < workCount; i++) {
                        doubles[index][i] = doWork(doubles[index][i], DEPTH);
                    }
                    Log.i("Calculator", "thread " + index + " completed at " + format.format(System.nanoTime() / 1000000000.0));
                }
            });
        }

        final long start = System.nanoTime();
        for (int i = 0; i < threads.length; i++) {
            threads[i].start();
            Log.i("Calculator", "started thread " + i + " at " + format.format(System.nanoTime() / 1000000000.0));
        }
        for (int i = 0; i < threads.length; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                Log.e("Calculator", "failed to join thread " + i);
            }
        }

        final long end = System.nanoTime();
        Log.i("Calculator", "start: " + format.format(start / 1000000000.0) +
                " end: " + format.format(end / 1000000000.0) +
                " diff: " + format.format((end - start) / 1000000000.0));
        return end - start;
    }

    private final DecimalFormat format = new DecimalFormat("#.###");
}
