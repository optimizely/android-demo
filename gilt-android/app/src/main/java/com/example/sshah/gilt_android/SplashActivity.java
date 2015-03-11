package com.example.sshah.gilt_android;

import com.example.sshah.gilt_android.util.SystemUiHider;
import com.optimizely.CodeBlocks.CodeBlock;
import com.optimizely.CodeBlocks.DefaultCodeBlock;
import com.optimizely.CodeBlocks.OptimizelyCodeBlock;
import com.optimizely.Optimizely;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class SplashActivity extends Activity {

    private static final int SPLASH_SCREEN_LENGTH = 1000;

    private static OptimizelyCodeBlock signUpFlow = Optimizely.codeBlockWithBranchNames("showTutorial");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String projectToken = "AAM7hIkA_MgWBe0vo3LNNmAmyrDdeQc4~2615150125";

        Optimizely.setEditGestureEnabled(true);
        Optimizely.setVerboseLogging(true);
        Optimizely.startOptimizely(projectToken, getApplication());

        this.getActionBar().hide();

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
            GiltLog.d("Using appetize token: " + appetizeToken);
            Optimizely.enableEditor();
        } else {
            projectToken = "AAM7hIkA_MgWBe0vo3LNNmAmyrDdeQc4~2615150125";
            GiltLog.d("Using hardcoded token: " + projectToken);
        }

        return projectToken;
    }


    @Override
    protected void onResume()
    {
        super.onResume();

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {

                signUpFlow.execute(new DefaultCodeBlock() {
                    @Override
                    public void execute() {
                        Intent signUpIntent = new Intent(SplashActivity.this, MainActivity.class);
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


}
