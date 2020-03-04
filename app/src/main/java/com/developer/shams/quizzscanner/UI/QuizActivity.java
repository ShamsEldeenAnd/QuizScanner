package com.developer.shams.quizzscanner.UI;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import android.support.v7.app.AppCompatActivity;
import android.view.View;

import android.widget.TextView;


import com.developer.shams.quizzscanner.Model.Question;
import com.developer.shams.quizzscanner.R;
import com.developer.shams.quizzscanner.Utils.Constants;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class QuizActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.timer)
    TextView timer;
    @BindView(R.id.question)
    TextView questionheader;
    @BindView(R.id.selection_1)
    TextView selection1;
    @BindView(R.id.selection_2)
    TextView selection2;
    @BindView(R.id.selection_3)
    TextView selection3;
    @BindView(R.id.selection_4)
    TextView selection4;


    private String mquizNumber;

    private int questionCounter = 0;

    private List<Question> questions;
    private List<String> answers;

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private ValueEventListener eventListener;
    private ProgressDialog dialog;

    private CountDownTimer downTimer;
    private long milliseconeds = 10000;

    private boolean isEvaluated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        ButterKnife.bind(this);

        //initialize question and answers lists
        questions = new ArrayList<>();
        answers = new ArrayList<>();
        isEvaluated = true;


        //setup fonts
        setupFonts();

        initializeOnclickListner();

        if (savedInstanceState == null)
            if (getIntent() != null) {
                if (getIntent().hasExtra(Constants.QUIZ_COUNTER)) {
                    mquizNumber = getIntent().getStringExtra(Constants.QUIZ_COUNTER);
                    showProcessDialog();
                    firebaseinitialize();
                    attachFirebaseListner();
                }
            }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(Constants.QUESTION_COUNTER, questionCounter);
        outState.putLong(Constants.LEFTED_MILLI, milliseconeds);
        outState.putParcelableArrayList(Constants.QUESTION_LIST, (ArrayList<? extends Parcelable>) questions);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        questionCounter = savedInstanceState.getInt(Constants.QUESTION_COUNTER);
        milliseconeds = savedInstanceState.getLong(Constants.LEFTED_MILLI);
        questions = savedInstanceState.getParcelableArrayList(Constants.QUESTION_LIST);
        initializeTimer();
        populateUI();
    }

    private void initializeOnclickListner() {
        selection1.setOnClickListener(this);
        selection2.setOnClickListener(this);
        selection3.setOnClickListener(this);
        selection4.setOnClickListener(this);
    }

    //show progress dialog
    private void showProcessDialog() {
        dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.fetch_question));
        dialog.setCancelable(false);
        dialog.show();
    }

    //timer initialization wit 10 sec
    private void initializeTimer() {

        downTimer = new CountDownTimer(milliseconeds, 1000) {
            public void onTick(long millisUntilFinished) {
                milliseconeds = millisUntilFinished;
                updateTimerText();
            }

            public void onFinish() {
                if (questionCounter < 4) {
                    questionCounter++;
                    populateUI();
                    saveAnswer(getString(R.string.wrong_answer_placeholder));
                    milliseconeds = 10000;
                    //recursion to reinitialize timer after time finish in thinking :)
                    initializeTimer();
                } else if (isEvaluated) {
                    evaluateResults();
                    isEvaluated = false;
                }
            }
        }.start();
    }

    private void updateTimerText() {
        timer.setText("00: " + milliseconeds / 1000);
    }

    //initialize fire base
    private void firebaseinitialize() {
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference().child("Quizzes")
                .child(mquizNumber)
                .child("question")
        ;
    }


    //attach listener to databaseReference
    private void attachFirebaseListner() {
        eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //fill the array with questions
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    questions.add(snapshot.getValue(Question.class));
                }
                initialiazeQuestion();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        databaseReference.addValueEventListener(eventListener);
    }

    //initialize first question
    private void initialiazeQuestion() {
        dialog.dismiss();
        initializeTimer();
        populateUI();
    }

    //binding the ui with data
    private void populateUI() {
        if (questionCounter < 5) {
            questionheader.setText(questions.get(questionCounter).getHeader());
            selection1.setText(questions.get(questionCounter).getSelections().get(0));
            selection2.setText(questions.get(questionCounter).getSelections().get(1));
            selection3.setText(questions.get(questionCounter).getSelections().get(2));
            selection4.setText(questions.get(questionCounter).getSelections().get(3));
        } else if (isEvaluated) {
            evaluateResults();
            isEvaluated = false;
        }

    }

    //evaluate the score send it to Result Activity
    private void evaluateResults() {
        int result = 0;
        for (int i = 0; i < answers.size(); i++) {
            if (answers.get(i).equals(questions.get(i).getAnswer())) {
                result++;
            }
        }
        downTimer.cancel();
        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra(Constants.RESULT, result);
        startActivity(intent);
        finish();
    }

    //handle choose answer
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.selection_1:
                nextQuestion(selection1.getText().toString());
                break;
            case R.id.selection_2:
                nextQuestion(selection2.getText().toString());
                break;
            case R.id.selection_3:
                nextQuestion(selection3.getText().toString());
                break;
            case R.id.selection_4:
                nextQuestion(selection4.getText().toString());
                break;
        }
    }

    //step to next question
    private void nextQuestion(String answer) {
        downTimer.cancel();
        questionCounter++;
        saveAnswer(answer);
        populateUI();
        milliseconeds = 10000;
        initializeTimer();
    }

    //save the answer in array to evaluate
    private void saveAnswer(String answer) {
        answers.add(answer);
    }

    private void setupFonts() {
        Typeface font = Typeface.createFromAsset(
                this.getAssets(),
                "fonts/BreeSerif_Regular.ttf");
        questionheader.setTypeface(font);
        selection1.setTypeface(font);
        selection2.setTypeface(font);
        selection3.setTypeface(font);
        selection4.setTypeface(font);
        timer.setTypeface(font);
    }
}
