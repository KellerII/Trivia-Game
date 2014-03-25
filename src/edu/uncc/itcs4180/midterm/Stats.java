/*
 * James Keller
 * ITCS 4180
 * Midterm Project
 * 11/18/14
 */

package edu.uncc.itcs4180.midterm;

import java.util.ArrayList;
import java.util.Collections;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class Stats extends Activity {
	ArrayList<Question> receivedQuestions;
	int rawScore;
	double convScore;
	int convIntScore;
	ProgressBar scoreBar;
	TextView summaryText;
	TextView scoreText;
	Button quitButton;
	Button againButton;
	final static String BACK_TRIVIA_LIST_KEY = "serializedList";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stats);
		rawScore = 0;
		convScore = 0;
		convIntScore = 0;
		
		//Receives the score and an arraylist of questions from the trivia activity
		if(getIntent().getExtras() != null) {
			rawScore = getIntent().getExtras().getInt(Trivia.NUMBER_CORRECT);
			receivedQuestions = (ArrayList<Question>) getIntent().getExtras().getSerializable(Trivia.SERIALIZED_LIST_KEY);
		}
		
		//Calculates the score and displays it as text and a progress bar
		convScore = rawScore * 6.25;
		int convIntScore = (int) convScore;
		Log.d("demo", convScore + "");
		scoreBar = (ProgressBar) findViewById(R.id.progressBar1);
		scoreText = (TextView) findViewById(R.id.textView4);
		quitButton = (Button) findViewById(R.id.button1);
		againButton =(Button) findViewById(R.id.button2);
		scoreText.setText(convScore + "%");
		
		scoreBar.setMax(100);
		scoreBar.setProgress(convIntScore);
		summaryText = (TextView) findViewById(R.id.textView3);
		
		//Creates colored text based on the percentage correct
		if (convIntScore > 80){
			scoreText.setTextColor(Color.GREEN);
		} else {
			if (convIntScore > 70) {
				scoreText.setTextColor(Color.YELLOW);
			} else {
				if (convIntScore > 60) {
					scoreText.setTextColor(Color.YELLOW);
				} else {
					scoreText.setTextColor(Color.RED);
				}
			}
		}
		
		//More text based on the user's score
		if(convScore == 100) {
			summaryText.setText("Perfect Score! Congratulations.");
		} else {
			summaryText.setText("Try again and see if you can get all the correct answers!");
		}
		
		againButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent tryAgainIntent = new Intent(Stats.this, Trivia.class);
				tryAgainIntent.putExtra(BACK_TRIVIA_LIST_KEY, receivedQuestions);
				startActivity(tryAgainIntent);
				finish();
			}
		});
		
		quitButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.stats, menu);
		return true;
	}

}
