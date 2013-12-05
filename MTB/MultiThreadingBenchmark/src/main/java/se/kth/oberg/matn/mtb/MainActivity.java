package se.kth.oberg.matn.mtb;

import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity {

    SectionsPagerAdapter mSectionsPagerAdapter;

    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return new FirstTestFragment();
//            return FirstTestFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class FirstTestFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
//        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
//        public static FirstTestFragment newInstance(int sectionNumber) {
//            FirstTestFragment fragment = new FirstTestFragment();
//            Bundle args = new Bundle();
//            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
//            fragment.setArguments(args);
//            return fragment;
//        }

        public FirstTestFragment() {
        }

        private TextView textView;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.fragment_first_test   , container, false);
            assert rootView != null;
            final Button runButton = (Button) rootView.findViewById(R.id.buttonRun);
            textView = (TextView) rootView.findViewById(R.id.textView);

            runButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    int count = Integer.parseInt(editText.getText().toString());
                    new CalcTask("1x120: ", 1, 120,
                        new CalcTask(" 2x60: ", 2, 60,
                            new CalcTask(" 3x40: ", 3, 40,
                                new CalcTask(" 4x30: ", 4, 30,
                                    new CalcTask(" 6x20: ", 6, 20,
                                        new CalcTask(" 8x15: ", 8, 15,
                                            new CalcTask("12x10: ", 12, 10,
                                                new CalcTask(" 24x5: ", 24, 5,
                                                    new CalcTask(" 60x2: ", 60, 2,
                                                        new CalcTask("120x1: ", 120, 1, null)))))))))).run();
                }
            });

            return rootView;
        }


        private class CalcTask extends Calculator {
            private String tag;
            private int threads;
            private int count;
            private CalcTask next;

            private List<CalcTask> tasks;
            public CalcTask(String tag, int threads, int count, CalcTask next) {
                this.tag = tag;
                this.threads = threads;
                this.count = count;
                this.next = next;
            }
            public void run() {
                execute(threads, count);
            }
            @Override
            protected void onPostExecute(Long format) {
                textView.append(tag + this.format.format(format / 1000000000.0) + "\n");
                if (next != null) {
                    next.run();
                }
            }
            private final DecimalFormat format = new DecimalFormat("#.###");
        }
    }

}
