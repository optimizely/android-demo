package com.example.sshah.gilt_android;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.optimizely.Optimizely;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new SignInFragment())
                    .commit();
        }

        String appetizeToken = getIntent().getExtras().getString("project");

        String projectToken;

        if(appetizeToken != null) {
            projectToken = appetizeToken;
        } else {
            projectToken = "AAM7hIkA_MgWBe0vo3LNNmAmyrDdeQc4~2615150125";
        }

        Optimizely.setEditGestureEnabled(true);
        Optimizely.setVerboseLogging(true);
        Optimizely.startOptimizely(projectToken, getApplication());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sign_in, menu);
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
    public static class SignInFragment extends Fragment {

        private Button signInButton, fbSignInButton;

        public SignInFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_sign_in, container, false);
            signInButton = (Button)rootView.findViewById(R.id.signInButton);
            signInButton.setOnClickListener(signInButtonClicked);
            fbSignInButton = (Button)rootView.findViewById(R.id.fbSignInButton);

            GiltSale.getSales(new GiltSale.GetSalesResponseHandler() {
                @Override
                public void onCompletion(ArrayList<GiltSale> sales) {
                    // do nothing, start the download of sales when the app starts for caching
                }
            });
            return rootView;
        }

        private View.OnClickListener signInButtonClicked = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Sign in button pressed
                Intent showSales = new Intent(SignInFragment.this.getActivity(), GiltSalesListActivity.class);
                SignInFragment.this.getActivity().startActivity(showSales);
            }
        };


    }
}
