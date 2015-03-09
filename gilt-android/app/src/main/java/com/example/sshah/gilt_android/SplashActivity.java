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

    private static OptimizelyCodeBlock signUpFlow = Optimizely.codeBlockWithBranchNames("showTutorial","noTutorial");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.getActionBar().hide();

        setContentView(R.layout.activity_splash);

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
                        Intent signUpIntent = new Intent(SplashActivity.this, TutorialFlowActivity.class);
                        SplashActivity.this.startActivity(signUpIntent);
                    }
                });
            }
        }, SPLASH_SCREEN_LENGTH);



        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
    }


}
