package com.example.sshah.gilt_android;

import com.example.sshah.gilt_android.util.SystemUiHider;
import com.optimizely.CodeBlocks.CodeBlock;
import com.optimizely.CodeBlocks.DefaultCodeBlock;
import com.optimizely.CodeBlocks.OptimizelyCodeBlock;
import com.optimizely.Optimizely;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class SplashActivity extends Activity {

    private static final int SPLASH_SCREEN_LENGTH = 2000;

    private static OptimizelyCodeBlock signUpFlow = Optimizely.codeBlockWithBranchNames("showTutorial");

    // Hack to work around the fact that onCreate() gets called twice
    private boolean showSignUpFlowOnResume = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (!isTaskRoot()) {
            final Intent intent = getIntent();
            final String intentAction = intent.getAction();
            if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && intentAction != null && intentAction.equals(Intent.ACTION_MAIN)) {
                GiltLog.d("Main Activity is not the root.  Finishing Main Activity instead of launching.");
                finish();
                return;
            }
        }


        GiltLog.d("splash onCreate");
        Optimizely.setEditGestureEnabled(true);
        Optimizely.setVerboseLogging(true);
        Optimizely.startOptimizely(getOptimizelyToken(), getApplication());

        showSignUpFlow();
        showSignUpFlowOnResume = false;


        setContentView(R.layout.activity_splash);

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
    }

    private String getOptimizelyToken()
    {
        Intent launchIntent = getIntent();
        String appetizeToken = null;

        if(launchIntent.getExtras() != null) {
            appetizeToken = launchIntent.getExtras().getString("project");
        }

        String projectToken;

        if(appetizeToken != null) {
            projectToken = appetizeToken;
            GiltLog.d("Using appetize project token");
            Optimizely.enableEditor();
        } else {
            projectToken = "AAM7hIkA_MgWBe0vo3LNNmAmyrDdeQc4~2615150125";
            GiltLog.d("Using hardcoded token: " + projectToken);
        }

        return projectToken;
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        GiltLog.d("Config changed: " + newConfig);
    }


    private void showSignUpFlow()
    {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                GiltLog.d("Starting sign up flow");

                signUpFlow.execute(new DefaultCodeBlock() {
                    @Override
                    public void execute() {
                        Intent signUpIntent = new Intent(SplashActivity.this, SignInActivity.class);
                        SplashActivity.this.startActivity(signUpIntent);
                    }
                }, new CodeBlock("showTutorial") {
                    @Override
                    public void execute() {
                        Intent tutorialIntent = new Intent(SplashActivity.this, TutorialFlowActivity.class);
                        SplashActivity.this.startActivity(tutorialIntent);
                    }
                });
            }
        }, SPLASH_SCREEN_LENGTH);
    }

    @Override
    protected void onResume() {
        super.onResume();
        GiltLog.d("onResume being called");
        if(showSignUpFlowOnResume) {
            showSignUpFlow();
        } else {
            GiltLog.d("Not showing sign up flow, two activities being created");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        showSignUpFlowOnResume = true;
    }



}
