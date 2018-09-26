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
import android.widget.TextView;

import com.hit.project.pipedream.data.ScoreRecord;
import com.hit.project.pipedream.data.ScoresTable;

import java.util.List;
import java.util.Random;

public class ActivityScores extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scores);
        Typeface tf = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/" + "makhina.ttf");
        TextView title = new TextView(ActivityScores.this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        title.setLayoutParams(params);
        title.setText("High Scores:");
        title.setTypeface(tf);
        title.setTextSize(40);
        LinearLayout mainScoresLinearLayout = findViewById(R.id.high_scores_linear_layout);
        mainScoresLinearLayout.addView(title);

        List<ScoreRecord> records = ScoresTable.getAllScores();

        for (ScoreRecord record: records) {
            TextView score = new TextView(ActivityScores.this);

            score.setLayoutParams(params);
            score.setText(records.indexOf(record) + ". " + record.getNickname() + " " + record.getScore() + " " + record.getDate());
            score.setTypeface(tf);
            score.setTextSize(40);

            mainScoresLinearLayout.addView(score);
        }








        Button backButton = new Button(ActivityScores.this);
        backButton.setLayoutParams(params);
        backButton.setText("Back");
        backButton.setPadding(40,40,40,40);
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


        mainScoresLinearLayout.addView(backButton);






    }
}
