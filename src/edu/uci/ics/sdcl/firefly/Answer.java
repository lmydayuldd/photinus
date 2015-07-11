package edu.uci.ics.sdcl.firefly;

import java.io.Serializable;

public class Answer implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String YES = "YES, THERE IS AN ISSUE";
	public static final String I_DONT_KNOW = "I DON'T KNOW";
	public static final String NO = "NO, THERE IS NOT AN ISSUE";

	private String option;
	private int confidenceOption;
	private String explanation;
	private String workerId;
	private String elapsedTime; // duration of the answer
	private String timeStamp;
	private int difficulty;
	
	
	public Answer(String option, int confidenceOption, String explanation, String workerId, 
			String elapsedTime, String timeStamp, int difficulty){
		this.option = option;
		this.confidenceOption = confidenceOption;
		this.explanation = explanation;
		this.workerId = workerId;
		this.elapsedTime = elapsedTime;
		this.timeStamp = timeStamp;
		this.difficulty = difficulty;
	}
	
	
	/**
	 * Support for the old version
	 * 
	 * @param option
	 * @param explanation
	 * @param workerId
	 * @param elapsedTime
	 * @param timeStamp
	 */
	public Answer(String option, String explanation, String workerId,
			String elapsedTime, String timeStamp) {
		super();
		this.option = option;
		this.explanation = explanation;
		this.workerId = workerId;
		this.elapsedTime = elapsedTime;
		this.timeStamp = timeStamp;
	}



	public String getOption() {
		return option;
	}
	
	public int getConfidenceOption(){
		return confidenceOption;
	}
	
	public String getExplanation() {
		return explanation;
	}
	
	public String getWorkerId() {
		return workerId;
	}
	
	public String getElapsedTime() {
		return elapsedTime;
	}

	public String getTimeStamp() {
		return timeStamp;
	}
	
	public int getDifficulty(){
		return difficulty;
	}

	public static String mapOptionToString(int number){
		switch(number){
		case 1: return Answer.YES; 
		case 2: return Answer.I_DONT_KNOW; 
		case 3: return Answer.NO; 
		default: return null;
		}
	}

	public static int mapOptionNumber(String option){
		switch(option){
		case  Answer.YES: return 1; 
		case Answer.I_DONT_KNOW: return 2; 
		case Answer.NO: return 3; 		
		default: return 0;
		}
	}	
	
}
