package se.kth.oberg.matn.mtb;

public class CalcTask {
    final private Calculator task;
    final private int threads;
    final private int works;
    private long totalTime = 0;

    private CalcTask next = null;

    public CalcTask(int threads, int works) {
        this.threads = threads;
        this.works = works;
        this.task = new Calculator() {
            @Override
            protected void onPostExecute(Long time) {
                totalTime += time;
                if (next != null) {
                    next.run(totalTime);
                } else {
                    onDone(totalTime);
                }
            }
        };
    }

    public void onDone(long totalTime) {
        throw new IllegalStateException("this should not be called");
    }

    public void setNext(CalcTask task) {
        this.next = task;
    }

    public void run(long totalTime) {
        this.totalTime = totalTime;
        task.execute(threads, works);
    }
}
