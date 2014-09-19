package edu.uci.ics.sdcl.firefly;

import java.io.Serializable;
import java.util.ArrayList;

public class Microtask implements Serializable
{	
	private static final long serialVersionUID = 1L;
	private String question;
	private String failureDescription;
	private CodeSnippet method;
	private CodeElement codeElement;
	private String codeElementType;
	


	private ArrayList<Answer> answerList;

	private Integer startingLine;
	private Integer startingColumn;
	private Integer endingLine;
	private Integer endingColumn;
	private Integer ID;
	private ArrayList<String> elapsedTimeList;
	private ArrayList<String> timeStampList;


	/**
	 * 
	 * Represents a question about one element (see CodeElement class) in a code snippet. The element is 
	 * localized by the line and column parameters provided.
	 * 
	 * @param codeElementTypeArg
	 * @param methodArg
	 * @param codeElement TODO
	 * @param questionArg
	 * @param startingLineNumber 
	 * @param startingColumnNumber
	 * @param endingLineNumber
	 * @param endingColumnNumber
	 * @param ID
	 */
	public Microtask(String codeElementTypeArg, CodeSnippet methodArg, CodeElement codeElement, String questionArg, Integer startingLineNumber, 
			Integer startingColumnNumber, Integer endingLineNumber, Integer endingColumnNumber, Integer ID, String failureDescription)
	{
		this.setCodeSnippet(methodArg);
		this.setQuestion(questionArg);
		this.setCodeElementType(codeElementTypeArg);
		this.codeElement = codeElement;
		this.startingLine = startingLineNumber;
		this.startingColumn = startingColumnNumber;
		this.endingLine = endingLineNumber;
		this.endingColumn = endingColumnNumber;
		this.answerList = new ArrayList<Answer>();
		this.elapsedTimeList = new ArrayList<String>();
		this.timeStampList = new ArrayList<String>();
		this.ID = ID;
		this.failureDescription = failureDescription;
	}


	public Integer getID(){
		return this.ID;
	}
	
	public String getQuestion()
	{
		return question;
	}

	public void setQuestion(String question)
	{
		this.question = question;
	}
	
	public String getCodeElementType()
	{
		return codeElementType;
	}

	public void setCodeElementType(String type)
	{
		this.codeElementType = type;
	}

	public CodeSnippet getCodeSnippet()
	{
		return method;
	}

	public CodeElement getCodeElement() {
		return codeElement;
	}
	
	public void setCodeSnippet(CodeSnippet method)
	{
		this.method = method;
	}

	public ArrayList<Answer> getAnswerList()
	{
		return answerList;
	}

	public void setAnswer(ArrayList<Answer> answerList)
	{
		this.answerList = answerList;
	}
	
	public void addAnswer(Answer answer){
		this.answerList.add(answer);
	}
	
	/* getters for the position */
	public Integer getStartingLine() {
		return startingLine;
	}

	public Integer getStartingColumn() {
		return startingColumn;
	}

	public Integer getEndingLine() {
		return endingLine;
	}

	public Integer getEndingColumn() {
		return endingColumn;
	}

	public int getNumberOfAnswers() {
		return this.answerList.size();
	}

	public String getFailureDescription() {
		return failureDescription;
	}

	public void addTimeStamp(String timeStamp) {
		this.timeStampList.add(timeStamp);
		
	}

	public void addElapsedTime(String elapsedTime) {
		this.elapsedTimeList.add(elapsedTime);
		
	}

	public ArrayList<String> getElapsedTimeList() { 
		return elapsedTimeList;
	}

	
	public ArrayList<String> getTimeStampList() {
		return timeStampList;
	}

	
}