package se.kth.oberg.matn.mtb;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FirstTestFragment extends Fragment {
    private Button sendButton;
    private TextView textView;

    public FirstTestFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_first_test   , container, false);
        assert rootView != null;
        final Button runButton = (Button) rootView.findViewById(R.id.buttonRun);
        sendButton = (Button) rootView.findViewById(R.id.sendResult);
        textView = (TextView) rootView.findViewById(R.id.textView);
        final EditText editTextDepth = (EditText) rootView.findViewById(R.id.editTextDepth);
        final EditText editTextRepeat = (EditText) rootView.findViewById(R.id.editTextRepeat);

        runButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().setProgressBarIndeterminate(true);
                getActivity().setProgressBarIndeterminateVisibility(true);

                int depth = 0;
                int repeat = 1;

                try {
                    depth = Integer.parseInt(editTextDepth.getText().toString());
                } catch (NumberFormatException e) {}
                try {
                    repeat = Integer.parseInt(editTextRepeat.getText().toString());
                } catch (NumberFormatException e) {}

                Calculator.setDepth(depth);
                textView.append("> Preparing new test\n");
                textView.append("Stack depth: " + depth + "\n");
                textView.append("Repeat count: " + repeat + "\n");

                final int finalRepeat = repeat;

                new Benchmarker(8, 15, 5) {
                    @Override
                    public void onDone(long averageTime) {
                        runBenchmarks(finalRepeat);
                    }
                }.run();
                textView.append("> Warming up...\n");
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent send = new Intent(Intent.ACTION_SENDTO);
                String uriText = "mailto:" + Uri.encode("poldsberg@gmail.com,axel.odelberg@gmail.com") +
                        "?subject=" + Uri.encode("Multithreading Benchmark Result - " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())) +
                        "&body=" + Uri.encode(textView.getText().toString());
                Uri uri = Uri.parse(uriText);

                send.setData(uri);
                startActivity(Intent.createChooser(send, "Send mail..."));
            }
        });

        textView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                ((TextView) view).setText("");
                return false;
            }
        });

        return rootView;
    }

    private static final BenchmarkInfo[] infos = new BenchmarkInfo[] {
            new BenchmarkInfo(1, 120),
            new BenchmarkInfo(2, 60),
            new BenchmarkInfo(3, 40),
            new BenchmarkInfo(4, 30),
            new BenchmarkInfo(5, 24),
            new BenchmarkInfo(6, 20),
            new BenchmarkInfo(8, 15),
            new BenchmarkInfo(10, 12),
            new BenchmarkInfo(12, 10),
            new BenchmarkInfo(15, 8),
            new BenchmarkInfo(20, 6),
            new BenchmarkInfo(24, 5),
            new BenchmarkInfo(30, 4),
            new BenchmarkInfo(40, 3),
            new BenchmarkInfo(60, 2),
            new BenchmarkInfo(120, 1)
    };

    private void runBenchmarks(int repeat) {
        textView.append("> Starting test...\n");
        Benchmarker next = null;
        for (int i = infos.length - 1; i >= 0; i--) {
            final Benchmarker reallyNext = next;
            final BenchmarkInfo info = infos[i];
            next = new Benchmarker(info.works, info.threads, repeat) {
                @Override
                public void onDone(long averageTime) {
                    textView.append(info.name + ": " + format.format(averageTime / 1000000000.0) + "\n");
                    if (reallyNext != null) {
                        reallyNext.run();
                    } else {
                        try {
                            textView.append("> Test complete!\n");
                            getActivity().setProgressBarIndeterminateVisibility(false);
                        } catch (NullPointerException e) {}
                    }
                }
            };
        }

        next.run();
    }

    private static final DecimalFormat format = new DecimalFormat("#.###");

    private static class BenchmarkInfo {
        public String name;
        public int works;
        public int threads;
        private BenchmarkInfo(int threads, int works) {
            name = works + " task" + (works > 1 ? "s" : "") + " in " + threads + " thread" + (threads > 1 ? "s" : "");
            this.works = works;
            this.threads = threads;
        }
    }
}
