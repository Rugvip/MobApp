package se.kth.oberg.matn.mtb;

import android.os.AsyncTask;

public abstract class ChainedAsyncTask<Input, Progress, Output> extends AsyncTask<Input, Progress, Output> {
    private ChainedAsyncTask next = null;

    public void setNext(ChainedAsyncTask next) {
        this.next = next;
    }

    @Override
    protected void onPostExecute(Output output) {
        super.onPostExecute(output);

        if (next != null) {
            next.execute(transformOutput(output));
        } else {
            onCompleted();
        }
    }

    public Input[] transformOutput(Output output) {
        return null;
    }

    public abstract void onCompleted();
}
