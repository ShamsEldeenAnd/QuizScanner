package com.developer.shams.quizzscanner;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.RemoteViews;

import com.developer.shams.quizzscanner.Utils.Constants;

import static android.content.Context.MODE_PRIVATE;

/**
 * Implementation of App Widget functionality.
 */


public class QuizAppWidget extends AppWidgetProvider {


    private SharedPreferences mPref;

    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                       int appWidgetId, String result) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.quiz_app_widget);
        views.setTextViewText(R.id.result, result);

        appWidgetManager.updateAppWidget(new ComponentName(context, QuizAppWidget.class), views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        mPref = context.getSharedPreferences(Constants.PREF_CONSTANT, MODE_PRIVATE);
        String result = mPref.getString(Constants.RESULT, "");

        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, result);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

