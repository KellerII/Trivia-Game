/*
 * James Keller
 * ITCS 4180
 * Midterm Project
 * 11/18/14
 */

package edu.uncc.itcs4180.midterm;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class QuestionUtil {
	//Pull parser class used to parse the XML based on its tags and attributes. Uses a counter to 
	//understand which choice is correct and stores that choice as an index for the corresponding 
	//arraylist used to hold the choices for each question
	static public class QuestionsXMLPullParser {
		static ArrayList<Question> parseQuestions(InputStream xmlIn) throws XmlPullParserException, IOException {
			XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
			parser.setInput(xmlIn, "UTF-8");
			Question question = null;
			int indexCounter = 0;
			ArrayList<Question> questions = null;
			int event = parser.getEventType();
			while(event != XmlPullParser.END_DOCUMENT) {
				switch (event) {
				case XmlPullParser.START_DOCUMENT:
					questions = new ArrayList<Question>();
					break;
				case XmlPullParser.START_TAG:
					if(parser.getName().equals("question")) {
						question = new Question();
						question.setId(String.valueOf(parser.getAttributeValue(null, "id").trim()));
					} else if(parser.getName().equals("text")) {
						question.setText(parser.nextText().trim());
					} else if(parser.getName().equals("image")) {
						question.setImageURL(parser.nextText().trim());
					} else if (parser.getName().equals("choice")) {
						if(parser.getAttributeValue(null, "answer") != null) {
							question.setCorrectChoiceIndex(indexCounter);
							question.addChoice(parser.nextText());
						} else {
							question.addChoice(parser.nextText());
						}
						indexCounter++;
					}
					break;
				case XmlPullParser.END_TAG:
					if(parser.getName().equals("question")) {
						questions.add(question);
						indexCounter = 0;
					}
				default:
					break;
				}
				event = parser.next();
			}
			return questions;
		}
	}
}
