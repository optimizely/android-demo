package com.example.sshah.gilt_android;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import com.optimizely.Optimizely;
import com.optimizely.Variable.LiveVariable;

/**
 * Created by sshah on 3/7/15.
 */
public class AppRater {

    public static int numEventsTriggered = 0;

    private static LiveVariable<Integer> numberOfEventsToShowRatingsPrompt = Optimizely.integerVariable("Number of events before showing ratings", 3);

    private static final String defaultPrompt1Text = "Do you love Gilt?";
    private static final String defaultPrompt2Title = "Rate Gilt";
    private static final String defaultPrompt2Message = "If you enjoy using Gilt, would you mind taking a moment to rate it?";

    private static LiveVariable<String> ratingsPrompt1Text = Optimizely.stringVariable("RatingsPrompt1",defaultPrompt1Text);
    private static LiveVariable<String> ratingsPrompt2Text = Optimizely.stringVariable("RatingsPrompt2", defaultPrompt2Message);
    private static LiveVariable<String> ratingsPrompt2Title = Optimizely.stringVariable("RatingsPrompt2Title", defaultPrompt2Title);

    public static void showRatingsPrompt(final Context ctx)
    {
        AlertDialog firstPrompt = new AlertDialog.Builder(ctx)
                .setMessage(ratingsPrompt1Text.get())
                .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showSecondPrompt(ctx);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Do nothing
                        dialog.dismiss();
                    }
                })
                .create();

        firstPrompt.show();
    }

    public static void showSecondPrompt(final Context ctx)
    {
        AlertDialog secondPrompt = new AlertDialog.Builder(ctx)
                .setTitle(ratingsPrompt2Title.get())
                .setMessage(ratingsPrompt2Text.get())
                .setPositiveButton("Rate it now", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showPlayStoreRatings(ctx);
                    }
                })
                .setNegativeButton("No thanks", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create();
        secondPrompt.show();
    }

    public static void didSignificantEvent(Context ctx)
    {
        numEventsTriggered++;
        GiltLog.d("App Rater: did significant event");

        if(numEventsTriggered == numberOfEventsToShowRatingsPrompt.get()) {
            showRatingsPrompt(ctx);
            numEventsTriggered = 0;
            GiltLog.d("App Rater: showing prompt");
        }
    }

    static final String packageName = "com.gilt.android";

    public static void showPlayStoreRatings(Context ctx)
    {

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id=" + packageName));
        if (checkIfIntentWorks(ctx, intent) == false) {
            //Market (Google play) app seems not installed, let's try to open a webbrowser
            intent.setData(Uri.parse("https://play.google.com/store/apps/details?id="+packageName));
            if (checkIfIntentWorks(ctx, intent) == false) {
                //Well if this also fails, we have run out of options, inform the user.
                Toast.makeText(ctx, "Can't show ratings popup", Toast.LENGTH_LONG).show();
            }
        }
    }

    private static boolean checkIfIntentWorks(Context ctx, Intent aIntent) {
        try {
            ctx.startActivity(aIntent);
            return true;
        } catch (ActivityNotFoundException e) {
            return false;
        }
    }
}
