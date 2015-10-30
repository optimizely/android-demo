package com.example.sshah.gilt_android;

import com.example.sshah.gilt_android.util.SystemUiHider;
import com.facebook.FacebookSdk;
import com.localytics.android.AnalyticsListener;
import com.localytics.android.Localytics;
import com.optimizely.CodeBlocks.CodeBranch;
import com.optimizely.CodeBlocks.DefaultCodeBranch;
import com.optimizely.CodeBlocks.OptimizelyCodeBlock;
import com.optimizely.Optimizely;
import com.optimizely.integration.DefaultOptimizelyEventListener;
import com.optimizely.integration.OptimizelyEventListener;
import com.optimizely.integration.OptimizelyExperimentData;
import com.optimizely.integrations.localytics.OptimizelyLocalyticsIntegration;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import java.util.List;
import java.util.Map;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class SplashActivity extends Activity {

    private static final int SPLASH_SCREEN_LENGTH = 2000;


    // Hack to work around the fact that onCreate() gets called twice
    private boolean showSignUpFlowOnResume = false;


    /*  This method is used to pull in the correct Optimizely token and enables the mobile playground
    as well as the personalConstants file
     */
    private String getOptimizelyToken() {
        String projectToken = "fake_token";
        Intent launchIntent = getIntent();
        String appetizeToken = null;

        final Bundle extras = launchIntent.getExtras();
        if (extras != null) {
            appetizeToken = extras.getString("project");
        }

        // Check to see if a personal constants file/string is defined in the project
        int personalConstantsID = getResources().getIdentifier("personal_project_token", "string", getPackageName());

        if (appetizeToken != null) {
            projectToken = appetizeToken;
            GiltLog.d("Using appetize project token");
            Optimizely.enableEditor();
            String socketHostname = extras.getString("socketServerHostname");
            if (socketHostname != null) {
                Optimizely.setSocketHost(socketHostname);
            }
        } else if (personalConstantsID != 0) {
            projectToken = getResources().getString(personalConstantsID);
            GiltLog.d("Using personal constants token");
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setMessage("You haven't set an Optimizely project token. Please set one in the personal_constants.xml file in the res/values directory");
            builder.create().show();
            GiltLog.d("No project token found");
        }

        return projectToken;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());


        // The below is a hack to workaround the fact that when starting EditMode, the SplashActivity gets created twice-- and we only want to
        // show and create the SignUpActivity once. We also need to show the signupactivity on resume.
        if (!isTaskRoot()) {
            final Intent intent = getIntent();
            final String intentAction = intent.getAction();
            if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && intentAction != null && intentAction.equals(Intent.ACTION_MAIN)) {
                GiltLog.d("Main Activity is not the root.  Finishing Main Activity instead of launching.");
                finish();
                return;
            }
        }

        Optimizely.enableEditor();
        Optimizely.setVerboseLogging(true);
        Optimizely.setDumpNetworkCalls(true);
        Optimizely.addOptimizelyEventListener(optimizelyListener);
        Optimizely.startOptimizelyWithAPIToken(getOptimizelyToken(), getApplication());
        Optimizely.registerPlugin(new OptimizelyLocalyticsIntegration());

        // The api_key string resource should be set in a file called personal_constants.xml because
        // that file is git ignored and everyone has different project keys.
        // DO NOT SET THIS IS A STRINGS FILE THAT IS SOURCE CONTROLLED OR YOU WILL BREAK THE BUILD
        Optimizely.startOptimizelyWithAPIToken(getString(R.string.personal_project_token), getApplication());

        showSignUpFlow();

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                Localytics.addAnalyticsListener(new AnalyticsListener() {
                    @Override
                    public void localyticsSessionWillOpen(boolean b, boolean b1, boolean b2) {
                        Log.d("GILT", "localyticsSessionWillOpen");
                    }

                    @Override
                    public void localyticsSessionDidOpen(boolean b, boolean b1, boolean b2) {
                        Log.d("GILT", "localyticsSessionDidOpen");
                    }

                    @Override
                    public void localyticsSessionWillClose() {
                        Log.d("GILT", "localyticsSessionWillClose");
                    }

                    @Override
                    public void localyticsDidTagEvent(String s, Map<String, String> map, long l) {
                        Log.d("GILT", "localyticsDidTagEvent");
                    }
                });
            }
        });

        showSignUpFlowOnResume = false;

        setContentView(R.layout.activity_splash);
    }

    private static OptimizelyEventListener optimizelyListener = new DefaultOptimizelyEventListener() {
        @Override
        public void onOptimizelyStarted() {
            GiltLog.d("OptimizelyStarted");
            GiltLog.printAllExperiments();
        }

        @Override
        public void onOptimizelyFailedToStart(String s) {
            GiltLog.d("Optimizely Failed to start");
        }

        @Override
        public void onOptimizelyExperimentViewed(OptimizelyExperimentData optimizelyExperimentData) {
            GiltLog.d("Experiment viewed");
            GiltLog.prettyPrintExperiment(optimizelyExperimentData);
            Optimizely.sendEvents();
        }

        @Override
        public void onOptimizelyEditorEnabled() {
            GiltLog.d("Optimizely editor enabled");
        }

        @Override
        public void onOptimizelyDataFileLoaded() {
            GiltLog.d("Optimizely datafile loaded");
        }

        @Override
        public void onGoalTriggered(String s, List<OptimizelyExperimentData> list) {
            GiltLog.d("Goal triggered: " + s);
            GiltLog.d("Triggered for experiments: ");

            for (int x = 0; x < list.size(); x++) {
                GiltLog.prettyPrintExperiment(list.get(x));
            }

            Optimizely.sendEvents();
        }

        @Override
        public void onMessage(String s, String s2, Bundle bundle) {
        }
    };



    private static OptimizelyCodeBlock signUpFlow = Optimizely.codeBlock("onboardingFlow").withBranchNames("showTutorial");

    private void showSignUpFlow() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                GiltLog.d("Starting sign up flow");


                signUpFlow.execute(new DefaultCodeBranch() {
                    @Override
                    public void execute() {
                        Intent signUpIntent = new Intent(SplashActivity.this, SignInActivity.class);
                        SplashActivity.this.startActivity(signUpIntent);
                    }
                }, new CodeBranch() {
                    @Override
                    public void execute() {
                        Intent tutorialIntent = new Intent(SplashActivity.this, TutorialFlowActivity.class);
                        SplashActivity.this.startActivity(tutorialIntent);
                    }
                });

            }
        }, SPLASH_SCREEN_LENGTH);

    }

    private void showProductListings() {
        Intent salesIntent = new Intent(SplashActivity.this, GiltSalesListActivity.class);
        SplashActivity.this.startActivity(salesIntent);
    }

    private static final String SIGNED_IN_KEY = "hasSignedIn";

    @Override
    protected void onResume() {
        super.onResume();
        if (showSignUpFlowOnResume) {
            showSignUpFlow();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        showSignUpFlowOnResume = true;
    }
}
