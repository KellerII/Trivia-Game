/*
 * James Keller
 * ITCS 4180
 * Midterm Project
 * 11/18/14
 */

package edu.uncc.itcs4180.midterm;

import java.io.Serializable;
import java.util.ArrayList;

//Question class implementing serializable. Uses an arraylist
//to the hold the choice options for each question and implements methods
//for detecting images, setter, getters, toString, and the arraylist index of the correct choice
//for each question
public class Question implements Serializable {
	
	String id;
	String text;
	String imageURL;
	ArrayList<String> choices = new ArrayList<String>();
	int correctChoiceIndex;
	
	public Question() {
		
	}
	
	public Question(String id, String text, String imageURL, int correctChoiceIndex) {
		super();
		this.id = id;
		this.text = text;
		this.imageURL = imageURL;
		this.correctChoiceIndex = correctChoiceIndex;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getImageURL() {
		return imageURL;
	}

	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}

	public int getCorrectChoiceIndex() {
		return correctChoiceIndex;
	}

	public void setCorrectChoiceIndex(int correctChoiceIndex) {
		this.correctChoiceIndex = correctChoiceIndex;
	}
	public void addChoice(String s) {
		choices.add(s);
	}
	
	public ArrayList<String> getChoices() {
		return choices;
	}

	public void setChoices(ArrayList<String> choices) {
		this.choices = choices;
	}

	public int getNumberOfChoices() {
		return choices.size();
	}
	
	public boolean hasImage() {
		if(imageURL != null) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		return "Question [id=" + id + ", text=" + text + ", imageURL="
				+ imageURL + ", choices=" + choices + ", correctChoiceIndex="
				+ correctChoiceIndex + "]";
	}
	
	
}
