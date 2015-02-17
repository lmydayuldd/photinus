package edu.uci.ics.sdcl.firefly.report.analysis;

import java.util.ArrayList;
import java.util.HashMap;

import edu.uci.ics.sdcl.firefly.report.Result;

public class FilterContent {

	/** in milliseconds */
	Double minimalDuration;
	/** 1,2,3,4 */
	Integer workerScore;
	/** 0 to 10 */
	Integer maxICantTell;
	
	/** Number of answers the filter produced (i.e., left). */
	Integer validAnswers; 
	
	/** Number of workers who contributed the validAnswers */
	Integer activeWorkers;
	
	String fileName;
	
	/** Result from majority voting */
	Result majorityResult;
	
	/** Result from strength signal */
	Result strengthSignalResult;
	
	/** The actual answers indexed by microtask ID*/
	HashMap<String,ArrayList<String>> answerMap;
	
	public FilterContent(Double minimalDuration, Integer workerScore, Integer maxICantTell) {
		this.minimalDuration = minimalDuration;
		this.maxICantTell = maxICantTell;
		this.workerScore = workerScore;
	}
	
	public String getFilterName(){
		String minimalDurationStr = minimalDuration.toString();
		minimalDurationStr = minimalDurationStr.substring(0, minimalDurationStr.length()-2);
		String name = "Duration-"+minimalDuration+"_Score-"+workerScore+"_ICT-"+maxICantTell; 
		return name;
	}
	
	public String convertWorkerScore(){
		if(workerScore==2)
			return "50%";
		else
			if(workerScore==3)
				return "75%";
			else
				if(workerScore==4)
					return "100%";
				else 
					return "";
	}
	
	
	
}
