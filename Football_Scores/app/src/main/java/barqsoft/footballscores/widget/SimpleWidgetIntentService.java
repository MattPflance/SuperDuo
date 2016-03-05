package barqsoft.footballscores.widget;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.widget.RemoteViews;

import java.text.SimpleDateFormat;
import java.util.Date;

import barqsoft.footballscores.DatabaseContract;
import barqsoft.footballscores.R;
import barqsoft.footballscores.Utilies;
import barqsoft.footballscores.MainActivity;

/**
 * IntentService which handles updating all SimpleWidgets with the latest data
 */
public class SimpleWidgetIntentService extends IntentService {

    private static final String[] FOOTBALL_COLUMNS = {
            DatabaseContract.scores_table.HOME_COL,
            DatabaseContract.scores_table.AWAY_COL,
            DatabaseContract.scores_table.HOME_GOALS_COL,
            DatabaseContract.scores_table.AWAY_GOALS_COL,
            DatabaseContract.scores_table.TIME_COL,
            DatabaseContract.scores_table.LEAGUE_COL,
            DatabaseContract.scores_table.MATCH_DAY
    };

    // these indices must match the projection
    private static final int INDEX_HOME_COL = 0;
    private static final int INDEX_AWAY_COL = 1;
    private static final int INDEX_HOME_GOALS_COL = 2;
    private static final int INDEX_AWAY_GOALS_COL = 3;
    private static final int INDEX_TIME_COL = 4;
    private static final int INDEX_LEAGUE_COL = 5;
    private static final int INDEX_MATCH_DAY = 6;

    public SimpleWidgetIntentService() {
        super("SimpleWidgetIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Retrieve all of the Today widget ids: these are the widgets we need to update
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this,
                SimpleWidgetProvider.class));

        // Get today's data from the ContentProvider
        Uri scoresUri = DatabaseContract.scores_table.buildScoreWithDate();
        Cursor data = getContentResolver().query(
                scoresUri,
                FOOTBALL_COLUMNS,
                null,
                new String[] { new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis())) },
                null);

        if (data == null) {
            return;
        }
        if (!data.moveToFirst()) {
            data.close();
            return;
        }

        /** Extract football data from cursor **/
        String homeName = data.getString(INDEX_HOME_COL);
        int homeGoals = data.getInt(INDEX_HOME_GOALS_COL);
        int homeResId = Utilies.getTeamCrestByTeamName(homeName);
        String awayName = data.getString(INDEX_AWAY_COL);
        int awayGoals = data.getInt(INDEX_AWAY_GOALS_COL);
        int awayResId = Utilies.getTeamCrestByTeamName(awayName);
        String time = data.getString(INDEX_TIME_COL);
        int leagueNum = data.getInt(INDEX_LEAGUE_COL);
        int match_day = data.getInt(INDEX_MATCH_DAY);

        // Perform this loop procedure for each Today widget
        for (int appWidgetId : appWidgetIds) {
            // Find the correct layout based on the widget's width
            int widgetWidth = getWidgetWidth(appWidgetManager, appWidgetId);
            int defaultWidth = getResources().getDimensionPixelSize(R.dimen.widget_simple_default_width);
            int largeWidth = getResources().getDimensionPixelSize(R.dimen.widget_simple_large_width);
            int layoutId = 0;
            if (widgetWidth >= largeWidth) {
                layoutId = R.layout.widget_simple_large;
            } else if (widgetWidth >= defaultWidth) {
                layoutId = R.layout.widget_simple_medium;
            } else {
                layoutId = R.layout.widget_simple_small;
            }
            RemoteViews views = new RemoteViews(getPackageName(), layoutId);

            // Add the data to the RemoteViews
            views.setImageViewResource(R.id.widget_home_crest, homeResId);
            views.setImageViewResource(R.id.widget_away_crest, awayResId);
            views.setTextViewText(R.id.widget_score_textview, Utilies.getScores(homeGoals, awayGoals));
            views.setTextViewText(R.id.widget_time_textview, time);
            views.setTextViewText(R.id.widget_home_name, homeName);
            views.setTextViewText(R.id.widget_away_name, awayName);
            views.setTextViewText(R.id.widget_league, Utilies.getLeague(leagueNum));
            views.setTextViewText(R.id.widget_match_day, "Matchday: " + match_day);

            // Create an Intent to launch MainActivity
            Intent launchIntent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, launchIntent, 0);
            views.setOnClickPendingIntent(R.id.widget, pendingIntent);

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    private int getWidgetWidth(AppWidgetManager appWidgetManager, int appWidgetId) {
        // Prior to Jelly Bean, widgets were always their default size
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            return getResources().getDimensionPixelSize(R.dimen.widget_simple_default_width);
        }
        // For Jelly Bean and higher devices, widgets can be resized - the current size can be
        // retrieved from the newly added App Widget Options
        return getWidgetWidthFromOptions(appWidgetManager, appWidgetId);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private int getWidgetWidthFromOptions(AppWidgetManager appWidgetManager, int appWidgetId) {
        Bundle options = appWidgetManager.getAppWidgetOptions(appWidgetId);
        if (options.containsKey(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH)) {
            int minWidthDp = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
            // The width returned is in dp, but we'll convert it to pixels to match the other widths
            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, minWidthDp,
                    displayMetrics);
        }
        return  getResources().getDimensionPixelSize(R.dimen.widget_simple_default_width);
    }
}

