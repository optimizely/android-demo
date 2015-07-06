package com.example.sshah.gilt_android;

import android.content.Intent;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.optimizely.Optimizely;


public class TutorialFlowActivity extends ActionBarActivity {

    private ViewPager mViewPager;
    private TutorialScreenPageAdapter pageAdapter;


    public static final String TUTORIAL_SCREEN_PARAMETER = "tutorial_screen_number";

    private ImageView indicator0, indicator1, indicator2, indicator3;

    private Button signUpButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial_flow);
        getSupportActionBar().hide();

        pageAdapter = new TutorialScreenPageAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager)findViewById(R.id.pager);
        mViewPager.setAdapter(pageAdapter);

        indicator0 = (ImageView)findViewById(R.id.pageIndicator0);
        indicator1 = (ImageView)findViewById(R.id.pageIndicator1);
        indicator2 = (ImageView)findViewById(R.id.pageIndicator2);
        indicator3 = (ImageView)findViewById(R.id.pageIndicator3);

        signUpButton = (Button)findViewById(R.id.signInButton);

        signUpButton.setOnClickListener(signInButtonPressed);

        updateIndicators(0);

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                updateIndicators(position);
            }

            @Override
            public void onPageSelected(int position) {
                updateIndicators(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });




    }

    private View.OnClickListener signInButtonPressed = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent signUpIntent = new Intent(TutorialFlowActivity.this, SignInActivity.class);
            TutorialFlowActivity.this.startActivity(signUpIntent);
            //TutorialFlowActivity.this.finish();
        }
    };

    private void updateIndicators(int selectedPage) {

        indicator0.setImageResource(R.drawable.page_indicator);
        indicator1.setImageResource(R.drawable.page_indicator);
        indicator2.setImageResource(R.drawable.page_indicator);
        indicator3.setImageResource(R.drawable.page_indicator);

        switch(selectedPage) {
            default:
                break;
            case 0:
                indicator0.setImageResource(R.drawable.page_indicator_selected);
                break;
            case 1:
                indicator1.setImageResource(R.drawable.page_indicator_selected);
                break;
            case 2:
                indicator2.setImageResource(R.drawable.page_indicator_selected);
                break;
            case 3:
                indicator3.setImageResource(R.drawable.page_indicator_selected);
                break;
        }
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
        private TextView detailsTextView;

        public TutorialScreenFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_tutorial_screen, container, false);

            Bundle args = getArguments();
            tutorialScreenNumber = args.getInt(TUTORIAL_SCREEN_PARAMETER);

            imageView = (ImageView)rootView.findViewById(R.id.imageView);
            detailsTextView = (TextView)rootView.findViewById(R.id.detailsTextView);

            setupTutorialScreen();
            Optimizely.setOptimizelyId("tutorialScreen" + tutorialScreenNumber, rootView);
            GiltLog.d("Set optimizely ID: " + "tutorialScreen" + tutorialScreenNumber);
            return rootView;
        }

        private void setupTutorialScreen()
        {
            switch (tutorialScreenNumber) {
                case 0:
                    imageView.setImageResource(R.drawable.tutorial_new_1);
                    detailsTextView.setText("Get access to the best deals on all the latest fashions");
                    break;
                case 1:
                    imageView.setImageResource(R.drawable.tutorial1);
                    detailsTextView.setText("Find the hottest accessories to complement any look");
                    break;
                case 2:
                    imageView.setImageResource(R.drawable.tutorial_new_3);
                    detailsTextView.setText("Browse deals for women, men, home, and kids");
                    break;
                default:
                    imageView.setImageResource(R.drawable.tutorial_new_4);
                    detailsTextView.setText("Get up to 70% off retail prices!");
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
