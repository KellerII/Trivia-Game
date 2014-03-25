/*
 * James Keller
 * ITCS 4180
 * Midterm Project
 * 11/18/14
 */


package edu.uncc.itcs4180.midterm;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import org.xmlpull.v1.XmlPullParserException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class Main extends Activity {
	static ProgressBar progressBar;
	Button triviaButton;
	Button exitButton;
	ArrayList<Question> serializedQuestions = new ArrayList<Question>();
	final static String QUESTIONS_SERIALIZABLE_KEY = "questionsSerializable";
	TextView loadingText;
	TextView loadedText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//Linking XML elements programmatically
		exitButton = (Button) findViewById(R.id.button2);
		triviaButton = (Button) findViewById(R.id.button1);
		triviaButton.setEnabled(false);
		loadedText = (TextView) findViewById(R.id.textView3);
		loadedText.setVisibility(View.INVISIBLE);
		
		//Creating async task to fetch data from the XML
		new AsyncGetQuestions().execute(getString(R.string.xml_parsing_url));
		
		triviaButton.setOnClickListener(new View.OnClickListener() {
			//Sends the list to the trivia activity
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getBaseContext(), Trivia.class);
				intent.putExtra(QUESTIONS_SERIALIZABLE_KEY, serializedQuestions);
				startActivity(intent);
			}
		});
		
		exitButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	//Async activity to handle the connection request and receives data from the parser
	//it also reveals and hides the progress bar, text, and buttons based on the progress
	//of the XML fetch
	public class AsyncGetQuestions extends AsyncTask<String, Void, ArrayList<Question>> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressBar = (ProgressBar) findViewById(R.id.progressBar1);
			loadingText = (TextView) findViewById(R.id.textView2);
			loadingText.setTextColor(Color.LTGRAY);
		}

		@Override
		protected ArrayList<Question> doInBackground(String... params) {
			try {
				String urlString = params[0];
				URL url = new URL(urlString);
				HttpURLConnection con = (HttpURLConnection) url.openConnection();
				con.setRequestMethod("GET");
				con.connect();
				int statusCode = con.getResponseCode();
				if (statusCode == HttpURLConnection.HTTP_OK) {
					InputStream in = con.getInputStream();
					ArrayList<Question> questions = QuestionUtil.QuestionsXMLPullParser.parseQuestions(in);
					return questions;
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (ProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (XmlPullParserException e) {
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(ArrayList<Question> result) {
			super.onPostExecute(result);
			serializedQuestions = result;
			loadingText.setVisibility(View.INVISIBLE);
			progressBar.setVisibility(View.INVISIBLE);
			triviaButton.setEnabled(true);
			loadedText.setVisibility(View.VISIBLE);
		}
		
	}
	
}
