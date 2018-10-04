package com.hit.project.pipedream;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hit.project.pipedream.data.ScoreRecord;
import com.hit.project.pipedream.data.ScoresTable;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Random;

public class ActivityScores extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scores);

        RelativeLayout relativeLayout = new RelativeLayout(ActivityScores.this);
        RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT);

        RelativeLayout.LayoutParams relativeItemParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.WRAP_CONTENT);


        relativeLayout.setLayoutParams(relativeParams);

        Typeface tf = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/" + "makhina.ttf");
        TextView title = new TextView(ActivityScores.this);
                LinearLayout mainScoresLinearLayout = findViewById(R.id.high_scores_linear_layout);
                mainScoresLinearLayout.addView(relativeLayout);
        title.setId(10);

        title.setLayoutParams(relativeItemParams);
        title.setText(R.string.str_scores_activity_title);
        title.setGravity(Gravity.CENTER);
        title.setTypeface(tf);
        title.setTextSize(40);
        title.setPadding(0,0,0,10);

        relativeLayout.addView(title);
        RelativeLayout.LayoutParams buttonParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        buttonParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

        Button backButton = new Button(ActivityScores.this);
        backButton.setLayoutParams(buttonParams);
        backButton.setText(R.string.str_scores_activity_back_button);
        backButton.setPadding(80,40,80,40);
        backButton.setTypeface(tf);
        backButton.setTextSize(20);
        backButton.setGravity(Gravity.CENTER);
        backButton.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        backButton.setBackgroundResource(R.drawable.menu_button_border);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        relativeLayout.addView(backButton);

        RelativeLayout.LayoutParams scoreParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        scoreParams.addRule(RelativeLayout.BELOW, title.getId());

        int numOfScores;
        List<ScoreRecord> records = ScoresTable.getAllScores();
        if (records.size() < 5) {
            numOfScores = records.size();
        }
        else {
            numOfScores = 5;
        }
        TextView lastScore = null;
        for (int i = 0; i < numOfScores; i++) {
            TextView score = new TextView(ActivityScores.this);
            ScoreRecord record = records.get(i);




            if (record != null) {
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/YYYY");

                String date = formatter.format(record.getDate());

                score.setText(records.indexOf(record) + 1 + ". " + record.getNickname() + " " + record.getScore() + " " + date);
                score.setTypeface(tf);
                score.setTextSize(30);
                score.setGravity(Gravity.LEFT);
                score.setPadding(0, 0, 0, 30);
            }
            scoreParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
            if (lastScore == null) {
                scoreParams.addRule(RelativeLayout.BELOW, title.getId());

            }
            else {
                scoreParams.addRule(RelativeLayout.BELOW, lastScore.getId());
            }
            score.setId(i);
            score.setLayoutParams(scoreParams);
            relativeLayout.addView(score);

            lastScore = score;
        }




//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        params.weight = 2;
//
//
//        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//buttonParams.weight = 1;
//buttonParams.gravity = Gravity.END;
//
//        title.setLayoutParams(params);
//        title.setText(R.string.str_scores_activity_title);
//        title.setGravity(Gravity.CENTER);
//        title.setTypeface(tf);
//        title.setTextSize(40);
//        title.setPadding(0,0,0,10);
//
//        LinearLayout mainScoresLinearLayout = findViewById(R.id.high_scores_linear_layout);
//        mainScoresLinearLayout.addView(title);
//
//        int numOfScores;
//        List<ScoreRecord> records = ScoresTable.getAllScores();
//        if (records.size() < 5) {
//            numOfScores = records.size();
//        }
//        else {
//            numOfScores = 5;
//        }
//        for (int i = 0; i < numOfScores; i++) {
//            TextView score = new TextView(ActivityScores.this);
//            ScoreRecord record = records.get(i);
//
//            if (record != null) {
//                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/YYYY");
//
//                String date = formatter.format(record.getDate());
//
//                score.setLayoutParams(params);
//                score.setText(records.indexOf(record) + 1 + ". " + record.getNickname() + " " + record.getScore() + " " + date);
//                score.setTypeface(tf);
//                score.setTextSize(30);
//                score.setGravity(Gravity.LEFT);
//                score.setPadding(0, 0, 0, 10);
//
//                mainScoresLinearLayout.addView(score);
//            }
//        }
//
////        params.gravity = Gravity.END;
//
//        Button backButton = new Button(ActivityScores.this);
//        backButton.setLayoutParams(buttonParams);
//        backButton.setText(R.string.str_scores_activity_back_button);
//        backButton.setPadding(40,40,40,40);
//        backButton.setTypeface(tf);
//        backButton.setTextSize(20);
//        backButton.setGravity(Gravity.CENTER);
//        backButton.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
//        backButton.setBackgroundResource(R.drawable.menu_button_border);
//        backButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                finish();
//            }
//        });
//
//
//        mainScoresLinearLayout.addView(backButton);
//
//




    }
}
