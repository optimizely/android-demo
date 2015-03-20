package com.example.sshah.gilt_android;

import android.util.Log;

import com.optimizely.Optimizely;
import com.optimizely.integration.OptimizelyExperimentData;

import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

/**
 * Created by sshah on 3/3/15.
 */
public class GiltLog {

    static final boolean VERBOSE = true;

    public static void d(String format, Object... args) {
        Log.d("GiltApp", String.format(Locale.getDefault(), format, args));
    }

    public static void prettyPrintExperiment(OptimizelyExperimentData experiment)
    {
        d("Experiment: " + experiment.experimentName + " variation: " + experiment.variationName + "\n" +
                        "visitedThisSession: " + experiment.visitedThisSession + " visitedEver: " + experiment.visitedEver + "\n" +
                        "visitedCount: " + experiment.visitedCount + " state: " + experiment.state
        );
    }

    public static void prettyPrintExperimentMap(Map<String,OptimizelyExperimentData> map)
    {
        Iterator<OptimizelyExperimentData> iterator = map.values().iterator();

        while(iterator.hasNext()) {
            GiltLog.prettyPrintExperiment(iterator.next());
        }
    }

    public static void printAllExperiments()
    {
        GiltLog.d("All Experiments: " + Optimizely.getAllExperiments().size());
        GiltLog.prettyPrintExperimentMap(Optimizely.getAllExperiments());

        GiltLog.d("Visited Experiments: " + Optimizely.getVisitedExperiments().size());
        GiltLog.prettyPrintExperimentMap(Optimizely.getVisitedExperiments());
    }



}
