package com.example.sshah.gilt_android;

import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


public class TutorialFlowActivity extends ActionBarActivity {

    private ViewPager mViewPager;
    private TutorialScreenPageAdapter pageAdapter;


    public static final String TUTORIAL_SCREEN_PARAMETER = "tutorial_screen_number";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial_flow);

        pageAdapter = new TutorialScreenPageAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager)findViewById(R.id.pager);
        mViewPager.setAdapter(pageAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tutorial_flow, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class TutorialScreenFragment extends Fragment {

        private int tutorialScreenNumber = 0;

        private ImageView imageView;
        private TextView titleTextView, detailsTextView;

        public TutorialScreenFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_tutorial_screen, container, false);

            Bundle args = getArguments();
            tutorialScreenNumber = args.getInt(TUTORIAL_SCREEN_PARAMETER);

            imageView = (ImageView)rootView.findViewById(R.id.imageView);
            titleTextView = (TextView)rootView.findViewById(R.id.titleTextView);
            detailsTextView = (TextView)rootView.findViewById(R.id.detailsTextView);

            setupTutorialScreen();

            return rootView;
        }

        private void setupTutorialScreen()
        {
            switch (tutorialScreenNumber) {
                case 0:
                    imageView.setImageResource(R.drawable.tutorial1);
                    titleTextView.setText("Title 1");
                    detailsTextView.setText("THE BEST DESCRIPTION EVER 1");
                    break;
                case 1:
                    imageView.setImageResource(R.drawable.tutorial1);
                    titleTextView.setText("Title 2");
                    detailsTextView.setText("THE BEST DESCRIPTION EVER 1");
                    break;
                case 2:
                    imageView.setImageResource(R.drawable.tutorial1);
                    titleTextView.setText("Title 3");
                    detailsTextView.setText("THE BEST DESCRIPTION EVER 1");
                    break;
                default:
                    imageView.setImageResource(R.drawable.tutorial1);
                    titleTextView.setText("Title 4");
                    detailsTextView.setText("THE BEST DESCRIPTION EVER 1");
                    break;
            }
        }
    }

    public class TutorialScreenPageAdapter extends FragmentPagerAdapter
    {
        public TutorialScreenPageAdapter(android.support.v4.app.FragmentManager fm)
        {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            Fragment fragment = new TutorialScreenFragment();
            Bundle args = new Bundle();
            args.putInt(TUTORIAL_SCREEN_PARAMETER, i);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "OBJECT " + (position + 1);
        }
    }
}
