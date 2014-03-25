/*
 * James Keller
 * ITCS 4180
 * Midterm Project
 * 11/18/14
 */

package edu.uncc.itcs4180.midterm;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class Trivia extends Activity {
	ArrayList<Question> serializedQuestions;
	int listIndex = 0;
	int questionNumber = 1;
	int numberCorrect = 0;
	static ProgressBar progressBar;
	TextView loadingText;
	ImageView displayedImage;
	LinearLayout linear;
	RadioGroup radioGroup;
	Button nextButton;
	Button quitButton;
	TextView numberText;
	TextView questionText;
	final static String SERIALIZED_LIST_KEY = "serializedList";
	final static String NUMBER_CORRECT = "numberCorrect";
	GetImageAsyncTask myTask;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_trivia);

		Bundle extras = getIntent().getExtras();
		
		//Determines which activity sent the intent and shuffles accordingly
		if(getIntent().getExtras() != null) {
			if(extras.containsKey(Main.QUESTIONS_SERIALIZABLE_KEY)){
				serializedQuestions = (ArrayList<Question>) extras.getSerializable(Main.QUESTIONS_SERIALIZABLE_KEY);
				Collections.shuffle(serializedQuestions);
			} else{
				serializedQuestions = (ArrayList<Question>) extras.getSerializable(Stats.BACK_TRIVIA_LIST_KEY);
				Collections.shuffle(serializedQuestions);
				Log.d("demo", serializedQuestions.toString());
				Log.d("demo",listIndex + " " + questionNumber + " " + numberCorrect);
			}
		}
		
		//Linking the XML elements programmatically
		linear = (LinearLayout) findViewById(R.id.linearLayout1);
		radioGroup = (RadioGroup) findViewById(R.id.radioGroup1);
		displayedImage = (ImageView) findViewById(R.id.imageView1);
		numberText = (TextView) findViewById(R.id.textView1);
		questionText = (TextView) findViewById(R.id.textView2);
		final TextView timerText = (TextView) findViewById(R.id.textView4);
		Button nextButton = (Button) findViewById(R.id.button1);
		loadingText = (TextView) findViewById(R.id.textView3);
		progressBar = (ProgressBar) findViewById(R.id.progressBar1);
		loadingText.setTextColor(Color.LTGRAY);
		timerText.setTextColor(Color.RED);
		quitButton = (Button) findViewById(R.id.button2);
		
		//Sets the question number and the question text
		numberText.setText("Q" + String.valueOf(questionNumber));
		questionText.setText(serializedQuestions.get(listIndex).getText());
		
		//Creates the count down timer that goes to the Stats activity if time
		//is reached. Prints the time by setting a textview
		final CountDownTimer timer = new CountDownTimer(241000, 1000) {

			public void onTick(long millisUntilFinished) {
				timerText.setText("Time Left: "
						+ String.valueOf(millisUntilFinished / 1000)
						+ " seconds");
			}

			public void onFinish() {
				timerText.setText("Time Has Expired!");
				Intent triviaIntent = new Intent(getBaseContext(), Stats.class);
				triviaIntent.putExtra(SERIALIZED_LIST_KEY, serializedQuestions);
				triviaIntent.putExtra(NUMBER_CORRECT, numberCorrect);
				startActivity(triviaIntent);
				finish();
			}
		};
		timer.start();
		
		//Detect whether or not a question has a pic and uses an async task to fetch it.
		//Controls the progress bar and imageview visibility accordingly
		if(serializedQuestions.get(listIndex).hasImage()) {
			myTask = (GetImageAsyncTask) new GetImageAsyncTask().execute(serializedQuestions.get(listIndex).getImageURL());
		}else {
			progressBar.setVisibility(View.INVISIBLE);
			loadingText.setVisibility(View.INVISIBLE);
		}
		
		//Loops through the choice arraylist to dynamically generate the radiobuttons
		for(int i = 0; i < serializedQuestions.get(listIndex).getChoices().size(); i++) {
			RadioButton radioButton = new RadioButton(this);
			radioButton.setText(serializedQuestions.get(listIndex).getChoices().get(i));
			radioButton.setId(i);
			radioGroup.addView(radioButton);
		}
		
		nextButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				//Tracks the score of the user based on whether the correct answer is 
				//chosen based on the index of the arraylist for the choices
				if((listIndex + 1) < serializedQuestions.size()) {
					if(radioGroup.getCheckedRadioButtonId() == serializedQuestions.get(listIndex).getCorrectChoiceIndex()) {
						numberCorrect++;
					}
					
					//Cancels unfinished tasks if the user clicks too fast through the game. Ensures
					//that the appropriate picture is shown for the question
					if (!(serializedQuestions.get(0).hasImage())) {
						//do nothing
					} else {
						if (myTask.getStatus() != AsyncTask.Status.FINISHED) {
							myTask.cancel(true);
						}
					}
					
					//Increments the counters
					displayedImage.setVisibility(View.INVISIBLE);
					questionNumber++;
					listIndex++;
					
					numberText.setText("Q" + String.valueOf(questionNumber));
					questionText.setText(serializedQuestions.get(listIndex).getText());
					
					if(serializedQuestions.get(listIndex).hasImage()) {
						myTask = (GetImageAsyncTask) new GetImageAsyncTask().execute(serializedQuestions.get(listIndex).getImageURL());
					} else {
						progressBar.setVisibility(View.INVISIBLE);
						loadingText.setVisibility(View.INVISIBLE);
					}
					
					//Resets the radio buttons for new choices
					radioGroup.removeAllViews();
					radioGroup.clearCheck();
					
					for(int i = 0; i < serializedQuestions.get(listIndex).getChoices().size(); i++) {
						RadioButton radioButton = new RadioButton(Trivia.this);
						radioButton.setText(serializedQuestions.get(listIndex).getChoices().get(i));
						radioButton.setId(i);
						radioGroup.addView(radioButton);
					}
				} else {
					if(radioGroup.getCheckedRadioButtonId() == serializedQuestions.get(listIndex).getCorrectChoiceIndex()) {
						numberCorrect++;
					} else {
						progressBar.setVisibility(View.INVISIBLE);
						loadingText.setVisibility(View.INVISIBLE);
					}
					
					//Ends the game and sends the user to Stats if all of the questions have been answered
					//or skipped. Cancels the timer and finishes the current activity
					Intent triviaIntent = new Intent(getBaseContext(), Stats.class);
					triviaIntent.putExtra(SERIALIZED_LIST_KEY, serializedQuestions);
					triviaIntent.putExtra(NUMBER_CORRECT, numberCorrect);
					startActivity(triviaIntent);
					timer.cancel();
					finish();
				}
			}			

		});
		
		quitButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				timer.cancel();
				finish();
			}
		});
	}
			
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.trivia, menu);
		return true;
	}
	
	//Async task that fetches pictures for questions if they have one. Controls
	//the visibility of progress bar and text accordingly
	public class GetImageAsyncTask extends AsyncTask<String, Void, Bitmap> {
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressBar.setVisibility(View.VISIBLE);
			loadingText.setVisibility(View.VISIBLE);
		}

		@Override
		protected Bitmap doInBackground(String... params) {
			String imgURL = params[0];
			Bitmap image = null;
			try {
				URL url = new URL(imgURL);
				image = BitmapFactory.decodeStream(url.openStream());
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return image;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			super.onPostExecute(result);
			if (result != null) {
				progressBar.setVisibility(View.INVISIBLE);
				loadingText.setVisibility(View.INVISIBLE);
				displayedImage.setImageBitmap(result);
				displayedImage.setVisibility(View.VISIBLE);
			}	
		}	
	}
}
