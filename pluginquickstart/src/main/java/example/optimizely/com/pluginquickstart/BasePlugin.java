package example.optimizely.com.pluginquickstart;

import com.optimizely.CodeBlocks.OptimizelyCodeBlock;
import com.optimizely.JSON.OptimizelyView;
import com.optimizely.Optimizely;
import com.optimizely.Variable.LiveVariable;
import com.optimizely.integration.DefaultOptimizelyEventListener;
import com.optimizely.integration.OptimizelyEventListener;
import com.optimizely.integration.OptimizelyExperimentData;
import com.optimizely.integration.OptimizelyPlugin;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

/**
 * How to build a plugin
 *
 * In the plugin code:
 *  - Implement the OptimizelyPlugin interface
 *
 * Then in the app code:
 *  - Whitelist the plugin: `Optimizely.whitelistPlugin(pluginInstance, jsonConfig);`
 *  - Register the plugin: `Optimizely.registerPlugin(pluginInstance);
 *
 * Plugins can:
 *  - Register and modify Live Variables and Code Blocks
 *  - Contribute Visual Changes
 *  - Evaluate Audience Conditions
 *  - Receive notifications about App/Optimizely lifecycle
 */
public class BasePlugin implements OptimizelyPlugin {

    @NonNull
    @Override
    public String getPluginId() {
        return "base_plugin";
    }

    /**
     * The configuration object passed to Optimizely.whitelistPlugin(plugin, configObject)
     * will be given to the plugin on start. This is a good way to get API keys, or data from
     * a canvas app. If the plugin is configured from the Optimizely dashboard, all data
     * from the dashboard sidebar will be contained in the config object.
     */
    @Override
    public boolean start(Optimizely optimizely, JSONObject config) {
        return true;
    }

    @Override
    public void stop() {

    }

    @Nullable
    @Override
    public List<String> getRequiredPermissions(Context context) {
        return null;
    }

    @Nullable
    @Override
    public List<String> getDependencies() {
        return null;
    }

    @Nullable
    @Override
    public View.OnTouchListener getOnTouchListener() {
        return null;
    }

    @Nullable
    @Override
    public Application.ActivityLifecycleCallbacks getActivityLifecycleCallbacks() {
        return null;
    }

    ////////////////////////////////////////////////////////////////////////////////
    //////////////////// ANALYTICS INTEGRATIONS ////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////

    OptimizelyEventListener mOptimizelyListener = new DefaultOptimizelyEventListener() {
        @Override
        public void onOptimizelyStarted() {

        }

        /**
         * You can use this listener API to export Optimizely conversion events to a third-party
         * analytics SDK like Amplitude
         */
        @Override
        public void onGoalTriggered(String description, List<OptimizelyExperimentData> affectedExperiments) {
            String eventName = "Optimizely: " + description;

            for (OptimizelyExperimentData experimentData : affectedExperiments) {
                String propertyName = "Optimizely: " + experimentData.experimentName;
                String propertyValue = experimentData.variationName;
                // mySDK.setEventProperty(propertyName, propertyValue);
            }
            // mySDK.trackEvent(eventName);
        }

        /**
         * You can use this listener API to tag analytics events in a third-party analytics SDK, such
         * as Amplitude with properties showing which experiments are active
         */
        @Override
        public void onOptimizelyExperimentVisited(OptimizelyExperimentData experimentState) {
            String propertyName = "Optimizely: " + experimentState.experimentName;
            String propertyValue = experimentState.variationName;
            // mySDK.setGlobalProperty(propertyName, propertyValue);
        }
    };

    @Nullable
    @Override
    public OptimizelyEventListener getOptimizelyEventsListener() {
        // If your plugin does not need to listen to Optimizely events, you may return null here.
        return mOptimizelyListener;
    }

    ////////////////////////////////////////////////////////////////////////////////
    //////////////////// CONTROLLING LIVE VARIABLES ////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////

    // Plugins can register live variables, just like any othe piece of code.
    // It's a good idea to prefix your keys with your plugin name to avoid collision.
    public static LiveVariable<String> STRING_VAR =
            Optimizely.stringForKey("base_plugin.from_plugin", "");
    public static LiveVariable<Integer> INT_VAR =
            Optimizely.integerForKey("base_plugin.other_live_var", -1);

    /**
     * This method is queried on startup by the Optimizely core SDK, so make sure you
     * report all the variables that you care about.
     */
    @Nullable
    @Override
    public List<String> getLiveVariables() {
        // Static list
        return Arrays.asList(
                "base_plugin.from_plugin",
                "base_plugin.other_live_var");
    }

    /**
     * This method will only be called when a live variable with one of the keys that
     * you returned from @link{#getLiveVariables()} is evaluated. If you do not care
     * about setting the value for the variable, just return the defaultValue.
     * If an experiment makes use of the given variable, the experiment will take precedence
     * over all plugins.
     */
    @Nullable
    @Override
    public <T> Object evaluateLiveVariable(Class<T> type, String key,
                                           @Nullable T defaultValue) {
        switch (key) {
            case "base_plugin.from_plugin":
                return "Hello from my plugin!";
            case "base_plugin.other_live_var":
                return 42;
            default:
                return defaultValue;
        }
    }


    ////////////////////////////////////////////////////////////////////////////////
    //////////////////// CONTROLLING CODE BLOCKS ///////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////

    // Plugins can register code blocks, just like any othe piece of code.
    // It's a good idea to prefix your keys with your plugin name to avoid collision.
    OptimizelyCodeBlock mBlock = Optimizely.codeBlock("base_plugin.block1")
            .withBranchNames("a", "b", "c");

    /**
     * This method is queried on startup by the Optimizely core SDK, so make sure you
     * report all the code blocks that you care about.
     */
    @Nullable
    @Override
    public List<String> getCodeBlocks() {
        return Arrays.asList("base_plugin.block1");
    }

    /**
     * This method will only be called when a code block with one of the keys that
     * you returned from @link{#getCodeBlocks()} is evaluated. If you do not care
     * about setting the branch, just return null.
     * If an experiment makes use of the given code block, the experiment will take precedence
     * over all plugins.
     */
    @Nullable
    @Override
    public String evaluateCodeBlock(String blockKey) {
        switch (blockKey) {
            case "base_plugin.block1":
                return "b";
            default:
                return null;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////
    //////////////////// CONTROLLING VISUAL CHANGES ////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * This method will be called during the onResume callback for each activity.
     * You can retrieve an OptimizelyId for a given view by calling
     * @link{optimizelyInstance.getIdManager().getOptimizelyId(View)}.
     *
     * If you have no changes for a given activity, return an empty list or null.
     */
    @Nullable
    @Override
    public List<OptimizelyView> getVisualChangesForActivity(@NonNull Activity activity,
                                                            @NonNull String name) {
        ArrayList<OptimizelyView> changes = new ArrayList<>(1);

        if ("SignInActivity".equals(name)) {
            OptimizelyView view = new OptimizelyView();
            view.setKey("text");
            view.setValue("Hello!");
            view.setType("string");
            view.setOptimizelyId("SignInActivity@content>FrameLayout>LinearLayout>AppCompatTextView:nth-of-type(0)");

            changes.add(view);
        }

        return changes;
    }

    ////////////////////////////////////////////////////////////////////////////////
    //////////////////// EVALUATING AUDIENCES //////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * This method is queried on startup by the Optimizely core SDK, so make sure you
     * report all the dimensions that you know how to evaluate. The core SDK will automatically
     * handle prefixing the dimension names by your plugin name.
     */
    @Nullable
    @Override
    public List<String> getDimensionNames() {
        // Tell the core SDK that we know how to evaluate an audience condition that looks like:
        // {
        //    "name": "base_plugin.matches_sql_query",
        //    "value": "count distinct from mytable where name == 'foobar'",
        //    "match_type": "sql_query",
        //    "type": "third_party_dimension"
        // }
        return Arrays.asList("matches_sql_query");
    }

    @Override
    public boolean evaluateDimensions(@NonNull String dimensionName,
                                      @NonNull JSONObject condition) {
        switch (dimensionName) {
            case "matches_sql_query":
                try {
                    String sqlQuery = condition.getString("value");
                    return evaluateSqlQuery(sqlQuery);
                } catch (JSONException e) {
                    return false;
                }
            default:
                return false;
        }
    }

    // Example evaluation
    private boolean evaluateSqlQuery(String query) {
        // Here you would look at the database, or do whatever crunching you need to do
        return true;
    }
}
