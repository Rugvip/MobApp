package se.kth.oberg.matn.mtb;

import android.os.AsyncTask;

public abstract class Benchmarker {
    final private int works;
    final private int threads;
    final private int count;

    public Benchmarker(int works, int threads, int count) {
        this.works = works;
        this.threads = threads;
        this.count = count;
    }

    public void run() {
        if (count <= 0) {
            return;
        }

        CalcTask next = new CalcTask(threads, works) {
            @Override
            public void onDone(long totalTime) {
                Benchmarker.this.onDone(totalTime / count);
            }
        };

        for (int i = 0; i < count - 1; i++) {
            CalcTask prev = new CalcTask(threads, works);
            prev.setNext(next);
            next = prev;
        }

        next.run(0L);
    }


    public abstract void onDone(long averageTime);
}
