package com.developer.shams.quizzscanner.UI;

import android.appwidget.AppWidgetManager;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.developer.shams.quizzscanner.QuizAppWidget;
import com.developer.shams.quizzscanner.R;
import com.developer.shams.quizzscanner.Utils.Constants;

import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ResultActivity extends AppCompatActivity {


    @BindView(R.id.finalResult)
    TextView finalResult;
    @BindView(R.id.score)
    TextView score;

    private int result;
    private SharedPreferences mPref;
    private String totalResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        ButterKnife.bind(this);
        mPref = getSharedPreferences(Constants.PREF_CONSTANT, MODE_PRIVATE);
        setupFonts();
        if (getIntent() != null) {
            if (getIntent().hasExtra(Constants.RESULT)) {
                result = getIntent().getIntExtra(Constants.RESULT, 0);
                populateScore();
                setupSharedPref();
            }
        }

    }

    private void setupSharedPref() {
        SharedPreferences.Editor prefsEditor = mPref.edit();
        prefsEditor.putString(Constants.RESULT, totalResult);
        prefsEditor.apply();
        setupAppWidget();

    }
    private void setupAppWidget() {
        //handling update
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(ResultActivity.this);
        Bundle bundle = new Bundle();
        int appWidgetId = bundle.getInt(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
        QuizAppWidget.updateAppWidget(ResultActivity.this, appWidgetManager, appWidgetId, totalResult);
    }

    private void setupFonts() {
        Typeface font = Typeface.createFromAsset(
                this.getAssets(),
                "fonts/BreeSerif_Regular.ttf");
        score.setTypeface(font);
        finalResult.setTypeface(font);
    }

    private void populateScore() {
        float scoreInpercent = (((float) result / 5) * 100);
        DecimalFormat df = new DecimalFormat("#.##");
        totalResult = result + getString(R.string.total_questions);
        score.setText(totalResult);
        finalResult.setVisibility(View.VISIBLE);
        if (scoreInpercent >= 50.00) {
            finalResult.setText(getString(R.string.passed_result) + df.format(scoreInpercent)
                    + getString(R.string.percentage));
        } else {
            finalResult.setText(getString(R.string.failed_result) + df.format(scoreInpercent) +
                    getString(R.string.percentage));
        }

    }

}
