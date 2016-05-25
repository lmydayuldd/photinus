package edu.uci.ics.sdcl.firefly.report.predictive;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import edu.uci.ics.sdcl.firefly.Answer;
import edu.uci.ics.sdcl.firefly.report.predictive.inspectlines.QuestionLinesMap;

/**
 * Each question has a vote count which is basically Number of YES's minus the Number of NO's.
 * 
 * @author adrianoc
 *
 */
public class WithinQuestionConsensus extends Consensus{

	public String name = "Within-question";

	public static String Absolute_YES_Consensus="absolute YES consensus";

	public static String Balance_YES_NO_Consensus="balance YES NO consensus";

	private String consensusType = Balance_YES_NO_Consensus; //default

	private HashMap<String, Integer> voteMap;

	private HashMap<String, Integer> questionYESCountMap;

	private AnswerData data;
	
	private boolean includeIDK = true;
	
	private Integer minimumAnswersPerQuestion = 20;

	/** Difference between number of YES's and NO's. Default is 1.*/
	private int calibration=0;

	/** Minimum number of YES's to consider a question as locating a fault */
	private Integer minimumYesCount;


	public WithinQuestionConsensus(){
		super();
	}

	/**
	 * 
	 * @param type one of the two consensus types available in the class (see static attributes)
	 * @param minimumYesCount null if type if Balance_YES_NO_Consensus, otherwise provide a non-negative integer.
	 */
	public WithinQuestionConsensus(String type, Integer minimumYesCount, Integer calibration){
		this.calibration = calibration;
		this.minimumYesCount = minimumYesCount;
		this.consensusType = type;

		String suffix="0";
		if(minimumYesCount!=null)
			suffix=minimumYesCount.toString();

		this.name = this.name + " " + type + "_" + suffix + "_" + this.calibration;;


	}
	
	/**
	 * 
	 * @param type one of the two consensus types available in the class (see static attributes)
	 * @param minimumYesCount null if type if Balance_YES_NO_Consensus, otherwise provide a non-negative integer.
	 */
	public WithinQuestionConsensus(String type, Integer minimumYesCount, Integer calibration, Integer minimumAnswersPerQuestion, boolean includeIDK){
		this.calibration = calibration;
		this.minimumYesCount = minimumYesCount;
		this.consensusType = type;
		
		this.minimumAnswersPerQuestion = minimumAnswersPerQuestion;
		this.includeIDK = includeIDK;

		String suffix="0";
		if(minimumYesCount!=null)
			suffix=minimumYesCount.toString();

		this.name = this.name + " " + type + "_" + suffix + "_" + this.calibration;;


	}

	@Override
	public void setCalibration(int calibration){
		this.calibration = calibration;
	}

	@Override
	public int getCalibration(){
		return this.calibration;
	}

	public void setMinimumYESToConsiderFault(int minimumYesCount){
		this.minimumYesCount=minimumYesCount;
	}

	@Override
	public void setData(AnswerData data){
		this.data = data;
	}

	/** The number of bug covering questions that were actually found */
	@Override
	public Integer computeThreshold(AnswerData data){
		this.data = data;
		this.questionYESCountMap = this.computeNumberOfYES(data.getAnswerMap());
		HashMap<String, Integer> questionNoCountMap = this.computeNumberOfNO(data.getAnswerMap());
		if(this.consensusType.matches(this.Balance_YES_NO_Consensus)){
			this.voteMap = this.computeQuestionVoteMap(questionYESCountMap,questionNoCountMap); 
		}
		else 
			this.voteMap = this.computeQuestionVoteMap(questionYESCountMap);

		return this.computeTruePositives();
			
	}

	@Override
	public Integer computeSignalStrength(AnswerData data){
		if(voteMap==null)
			this.computeThreshold(data);

		if(getTruePositives()==0)
			return -1;

		Integer extraVotes=0;

		for(String questionID: data.bugCoveringMap.keySet()){

			Integer vote = voteMap.get(questionID);
			if(vote!=null && vote>this.calibration){
				extraVotes = extraVotes + vote-this.calibration;
			}
		}

		return extraVotes;
	}


	@Override
	public Integer computeNumberOfWorkers(AnswerData data) {
		int maxAnswers=0; 
		for(ArrayList<String> answerList :  data.answerMap.values()){
			if(answerList.size()>maxAnswers)
				maxAnswers = answerList.size();
		}
		return maxAnswers;
	}

	@Override
	/**
	 * 
	 * @return number of YES of the bug covering question that has the smallest positive vote. If the fault was not found returns -1.
	 */
	public Integer getMinimumNumberYESAnswersThatLocatedFault(){

		if (voteMap==null){
			if(this.computeThreshold(data)==0)
				return -1;
		}

		int smallestVote = this.computeNumberOfWorkers(data); //starts with the maximum possible.
		String questionIDSmallestVote=null;

		//find the number of YES of the bug covering question that has the smallest positive vote
		for(String questionID: this.voteMap.keySet()){
			if(data.bugCoveringMap.containsKey(questionID)){
				Integer vote = this.voteMap.get(questionID);
				if(vote!=null && vote>this.calibration && vote<smallestVote){
					smallestVote = vote;
					questionIDSmallestVote = new String(questionID);
				}
			}
		}

		if(questionIDSmallestVote!=null)
			return this.questionYESCountMap.get(questionIDSmallestVote);
		else 
			return -1;
	}

	/** Same result as function compute */
	@Override
	public Integer getTruePositives(){
		if(this.voteMap!=null)
			return this.computeTruePositives();
		else
			return null;
	}

	@Override
	public Integer getFalsePositives(){
		if(this.voteMap!=null)
			return computeFalsePositives();
		else
			return null;
	}

	@Override
	public Integer getFalseNegatives(){
		if(this.voteMap!=null)
			return computeFalseNegatives();
		else
			return null;
	}

	@Override
	public Integer getTrueNegatives(){
		if(this.voteMap!=null)
			return computeTrueNegatives();
		else
			return null;
	}

	/** Relies on matching the bugCovering list with the list of questions
	 * sent, which should be only the ones pertaining one HIT (e.g., HIT01_8).
	 * @return
	 */
	private Integer getNumberBugCoveringQuestions(){
		if(this.voteMap!=null)
			return countBugCovering();
		else
			return null;
	}

	@Override
	public String getName(){
		return this.name;
	}



	//----------------------------------------------------------------------------------------------------------

	/**
	 * @param questionOptionsMap questionID and list of answer options (YES, NO, IDK)
	 * @return a map <questionID, number of YES's>
	 */
	private HashMap<String, Integer> computeNumberOfYES(HashMap<String, ArrayList<String>> questionOptionsMap){

		HashMap<String, Integer> questionYESCountMap= new HashMap<String, Integer>(); 

		for(String questionID: questionOptionsMap.keySet()){
			ArrayList<String> optionList = questionOptionsMap.get(questionID);
			int counter = 0;
			for(String option : optionList){
				//System.out.println(option);
				if(option.compareTo(Answer.YES)==0)
					counter++;
			}
			//System.out.println("questionID: "+ questionID+"counter:"+counter);
			questionYESCountMap.put(questionID, new Integer(counter));
		}
		return questionYESCountMap;
	}


	/**
	 * @param questionOptionsMap questionID and list of answer options (YES, NO, IDK)
	 * @return a map <questionID, number of NO's>
	 */
	private HashMap<String, Integer> computeNumberOfNO(HashMap<String, ArrayList<String>> questionOptionsMap){

		HashMap<String, Integer> questionNOCountMap= new HashMap<String, Integer>(); 

		for(String questionID: questionOptionsMap.keySet()){
			ArrayList<String> optionList = questionOptionsMap.get(questionID);
			int counter = 0;
			for(String option : optionList){
				//System.out.println(option);
				if(option.compareTo(Answer.NO)==0)
					counter++;
			}
			//System.out.println("questionID: "+ questionID+"counter:"+counter);
			questionNOCountMap.put(questionID, new Integer(counter));
		}
		return questionNOCountMap;
	}

	/**
	 * Each question has a vote count which is basically Number of YES's minus the Number of NO's.
	 * 
	 * 
	 * @param questionYESCountMap
	 * @param questionNOCountMap
	 * @return map of questions and respective votes
	 */
	private HashMap<String,Integer> computeQuestionVoteMap(HashMap<String, Integer> questionYESCountMap,
			HashMap<String, Integer> questionNOCountMap) {

		HashMap<String,Integer> voteMap =  new HashMap<String,Integer>();
		for(String questionID : questionYESCountMap.keySet()){
			Integer yesCount = questionYESCountMap.get(questionID);
			Integer noCount = questionNOCountMap.get(questionID);
			Integer vote = yesCount - noCount;

			voteMap.put(questionID, vote);
		}
		return voteMap;
	}

	/**
	 * Each question has a vote count which is basically Number of YES's minus the Number of NO's.
	 * 
	 * 
	 * @param questionYESCountMap
	 * @param questionNOCountMap
	 * @return map of questions and respective votes
	 */
	private HashMap<String,Integer> computeQuestionVoteMap(HashMap<String, Integer> questionYESCountMap) {

		HashMap<String,Integer> voteMap =  new HashMap<String,Integer>();
		for(String questionID : questionYESCountMap.keySet()){
			Integer yesCount = questionYESCountMap.get(questionID);

			Integer vote = yesCount - this.minimumYesCount-1;

			voteMap.put(questionID, vote);
		}
		return voteMap;
	}
	
	/** Check if question has at list minimum answers to evaluate the rule
	 * The minimum can come from the calibration level, e.g., Y - N >4 , then need at least 4 answers to compute. 
	 * @param minimum number of answers
	 * @param includeIDK if true, means that we should count the IDK towards the minimum number of answers, false otherwise 
	 * @return
	 */
	private Boolean checkIfQuestionReceivedMinimumNumberOfAnswers(String questionID){
		
		if(this.minimumAnswersPerQuestion==0){
			return true;
		}
		else{
			ArrayList<String> answerList = data.answerMap.get(questionID);
			if(answerList.size()>=this.minimumAnswersPerQuestion){
				if(!this.includeIDK){
					int IDKCount = data.countOption(answerList, Answer.I_DONT_KNOW);
					
					if(answerList.size()-IDKCount<this.minimumAnswersPerQuestion)
						System.out.print("["+answerList.size()+"],"+IDKCount+" / ");
					return ((answerList.size()-IDKCount)>=this.minimumAnswersPerQuestion);
				}
				else{ 
					return ((answerList.size())>=this.minimumAnswersPerQuestion);
				}
			}
			else
				return false;
		}
	}

	private Integer computeTruePositives() {

		Integer quantityTruePositives=0;

		for(String questionID: voteMap.keySet()){
			if(data.bugCoveringMap.containsKey(questionID)){
				Integer vote = voteMap.get(questionID);
				if(vote!=null && vote>this.calibration && this.checkIfQuestionReceivedMinimumNumberOfAnswers(questionID))
					quantityTruePositives = quantityTruePositives +1;
			}
		}
		return quantityTruePositives;
	}


	private Integer computeFalsePositives() {

		Integer quantityFalsePositives=0;

		for(String questionID: voteMap.keySet()){
			if(!data.bugCoveringMap.containsKey(questionID)){
				Integer vote = voteMap.get(questionID);
				if(vote!=null && vote>this.calibration && this.checkIfQuestionReceivedMinimumNumberOfAnswers(questionID))
					quantityFalsePositives = quantityFalsePositives +1;
			}
		}
		return quantityFalsePositives;
	}

	private Integer computeFalseNegatives() {

		Integer quantityFalseNegatives=0;

		for(String questionID: voteMap.keySet()){
			if(data.bugCoveringMap.containsKey(questionID)){
				Integer vote = voteMap.get(questionID);
				if(vote!=null && vote<=this.calibration && this.checkIfQuestionReceivedMinimumNumberOfAnswers(questionID))
					quantityFalseNegatives = quantityFalseNegatives +1;
			}
		}
		return quantityFalseNegatives;
	}

	private Integer computeTrueNegatives() {

		Integer quantityTrueNegatives=0;

		for(String questionID: voteMap.keySet()){
			if(!data.bugCoveringMap.containsKey(questionID)){
				Integer vote = voteMap.get(questionID);
				if(vote!=null && vote<=this.calibration && this.checkIfQuestionReceivedMinimumNumberOfAnswers(questionID))
					quantityTrueNegatives = quantityTrueNegatives +1;
			}
		}
		return quantityTrueNegatives;
	}

	private Integer countBugCovering(){

		Integer count=0;

		for(String questionID: voteMap.keySet()){
			if(data.bugCoveringMap.containsKey(questionID))			
				count ++;
		}
		return count;
	}

	private Double computeTruePositiveRate(){
		Double numberOfBugCovering = this.getNumberBugCoveringQuestions().doubleValue();
		Double numberOfTruePositives = this.getTruePositives().doubleValue();
		return numberOfTruePositives / numberOfBugCovering;
	}

	//-----------------------------------------------------------------------------------

	/** Used to test the Majority voting functions */
	public static void main(String[] args){

		HashMap<String,String> bugCoveringMap = new HashMap<String,String>();

		bugCoveringMap.put("1","1");// received one yes
		bugCoveringMap.put("3","3");// received four yes's

		HashMap<String, ArrayList<String>> answerMap = new HashMap<String, ArrayList<String>>();

		ArrayList<String> answerList = new ArrayList<String>();//2 yes's TRUE NEGATIVE
		answerList.add(Answer.NO);
		answerList.add(Answer.NO);
		answerList.add("IDK");
		answerList.add(Answer.YES);
		answerMap.put("0",answerList);

		answerList = new ArrayList<String>();//1 yes BUG COVERING FALSE NEGATIVE
		answerList.add(Answer.YES);
		answerList.add(Answer.NO);
		answerList.add(Answer.NO);
		answerList.add(Answer.NO);
		answerMap.put("1",answerList);

		answerList = new ArrayList<String>();//2 yes's NON-BUG COVERING FALSE POSITIVE
		answerList.add(Answer.YES);
		answerList.add(Answer.YES);
		answerList.add(Answer.NO);
		answerList.add(Answer.NO);
		answerMap.put("2",answerList);

		answerList = new ArrayList<String>();//4 yes's BUG COVERING TRUE POSITIVE
		answerList.add(Answer.YES);
		answerList.add(Answer.YES);
		answerList.add(Answer.NO);
		answerList.add(Answer.NO);
		answerMap.put("3",answerList);

		String hitFileName = "HIT00_0";

		AnswerData data = new AnswerData(hitFileName,answerMap,bugCoveringMap,4,4);

		WithinQuestionConsensus predictor = new WithinQuestionConsensus();
		predictor.setCalibration(-1);
		predictor.computeThreshold(data);
		Double bugCoveringQuestionsLocated =  predictor.getTruePositives().doubleValue();
		Double totalBugCovering = 2.0;

		Double percentageFaults = new Double( bugCoveringQuestionsLocated/totalBugCovering) * 100;

		System.out.println("expected: 50% bug covering question located, actual: "+ percentageFaults.toString());


		Integer falsePositives = predictor.getFalsePositives();
		System.out.println("expected: 1, actual: "+ falsePositives.toString());

		Integer falseNegatives = predictor.getFalseNegatives();
		System.out.println("expected: 1, actual: "+ falseNegatives.toString());

		Integer trueNegatives = predictor.getTrueNegatives();
		System.out.println("expected: 1, actual: "+ trueNegatives.toString());

		Integer truePositives = predictor.getTruePositives();
		System.out.println("expected: 1, actual: "+ truePositives.toString());
	}

	@Override
	public HashMap<String, Integer> getTruePositiveLines(
			HashMap<String, QuestionLinesMap> lineMapping) {

		HashMap<String, Integer> map=  new HashMap<String,Integer>();

		for(String questionID: voteMap.keySet()){
			if(data.bugCoveringMap.containsKey(questionID)){
				Integer vote = voteMap.get(questionID);
				if(vote!=null && vote>this.calibration && this.checkIfQuestionReceivedMinimumNumberOfAnswers(questionID)){
					QuestionLinesMap questionLinesMap =lineMapping.get(questionID);
					if(questionLinesMap==null || questionLinesMap.faultyLines==null) System.err.println("No mapping for questionID: "+questionID);
					map =  this.loadLines(map, questionLinesMap.faultyLines);
				}
			}
		}
		return map;
	}


	@Override
	public HashMap<String, Integer> getNearPositiveLines(
			HashMap<String, QuestionLinesMap> lineMapping) {

		HashMap<String, Integer> map =  new HashMap<String,Integer>();

		for(String questionID: this.questionYESCountMap.keySet()){
			if(data.bugCoveringMap.containsKey(questionID)){
				Integer vote = voteMap.get(questionID);
				if(vote!=null && vote>this.calibration && this.checkIfQuestionReceivedMinimumNumberOfAnswers(questionID)){
					QuestionLinesMap questionLinesMap =lineMapping.get(questionID);
					map = loadLines(map,questionLinesMap.nearFaultyLines);
				}
			}
		}
		return map;
	}



	@Override
	public HashMap<String, Integer> getFalsePositiveLines(
			HashMap<String, QuestionLinesMap> lineMapping) {

		HashMap<String, Integer> map =  new HashMap<String,Integer>();

		for(String questionID: this.questionYESCountMap.keySet()){
			if(!data.bugCoveringMap.containsKey(questionID)){
				Integer vote = voteMap.get(questionID);
				if(vote!=null && vote>this.calibration && this.checkIfQuestionReceivedMinimumNumberOfAnswers(questionID)){
					QuestionLinesMap questionLinesMap =lineMapping.get(questionID);
					if(questionLinesMap.nonFaultyLines==null) 
						System.err.println("QuestionID: "+questionID +" is not failure related, but has a bug at same line");
					else
						map = loadLines(map,questionLinesMap.nonFaultyLines);
				}
			}
		}
		return map;
	}

	@Override
	public HashMap<String, Integer> getTrueNegativeLines(
			HashMap<String, QuestionLinesMap> lineMapping) {

		HashMap<String, Integer> map =  new HashMap<String,Integer>();

		for(String questionID: this.questionYESCountMap.keySet()){
			if(!data.bugCoveringMap.containsKey(questionID)){
				Integer vote = voteMap.get(questionID);
				if(vote!=null && vote<=this.calibration && this.checkIfQuestionReceivedMinimumNumberOfAnswers(questionID)){
					QuestionLinesMap questionLinesMap =lineMapping.get(questionID);
					map = loadLines(map,questionLinesMap.nonFaultyLines);
				}
			}
		}
		return map;
	}

	@Override
	public HashMap<String, Integer> getFalseNegativeLines(
			HashMap<String, QuestionLinesMap> lineMapping) {

		HashMap<String, Integer> map =  new HashMap<String,Integer>();

		for(String questionID: this.questionYESCountMap.keySet()){
			if(data.bugCoveringMap.containsKey(questionID)){
				Integer vote = voteMap.get(questionID);
				if(vote!=null && vote<=this.calibration && this.checkIfQuestionReceivedMinimumNumberOfAnswers(questionID)){
					QuestionLinesMap questionLinesMap =lineMapping.get(questionID);
					map = loadLines(map,questionLinesMap.faultyLines);
				}
			}
		}
		return map;
	}





	private HashMap<String, Integer> loadLines(HashMap<String, Integer> map, HashMap<String, String> questionLineMapping) {

		HashMap<String, Integer> newMap = (HashMap<String, Integer>) map.clone();

		Iterator<String> iter = questionLineMapping.keySet().iterator();
		while(iter.hasNext()){
			String line = iter.next();
			if(map.containsKey(line)){
				Integer count = map.get(line);
				count++;
				newMap.put(line, count);
			}
			else
				newMap.put(line, 1);
		}

		return newMap;
	}

	@Override
	public HashMap<String, HashMap<String,Integer>> getNearPositiveLinesQuestions(HashMap<String, QuestionLinesMap> lineMapping){

		HashMap<String, HashMap<String,Integer>> questionMap = new HashMap<String, HashMap<String,Integer>>();
		for(String questionID: this.questionYESCountMap.keySet()){
			if(data.bugCoveringMap.containsKey(questionID)){
				Integer vote = voteMap.get(questionID);
				if(vote!=null && vote>this.calibration && this.checkIfQuestionReceivedMinimumNumberOfAnswers(questionID)){
					QuestionLinesMap questionLinesMap =lineMapping.get(questionID);
					HashMap<String, Integer> map = loadLines(new HashMap<String, Integer>(),questionLinesMap.nearFaultyLines);
					if(map.size()>0){
						questionMap.put(questionID, map);
					}
				}
			}
		}
		return questionMap;
	}
	

	public void setMinimumAnswersPerQuestion(int minimum){
		this.minimumAnswersPerQuestion = minimum;
	}
	
	public void setIncludeIDK(boolean includeIDK){
		this.includeIDK = includeIDK;
	}
	
	
}



//--------------------------------------------------------------------------------------------------------------




