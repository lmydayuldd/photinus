package edu.uci.ics.sdcl.firefly.report.predictive;

import java.util.HashMap;

import edu.uci.ics.sdcl.firefly.report.descriptive.Filter;

/** 
 * Class responsible to keep the multiple filter combinations and the respective outcome of
 * computing predictors with the data produced by a concrete filter combination.
 * 
 * @author adrianoc
 *
 */
public class FilterCombination {

	public static final String ANSWER_DURATION = "ANSWER DURATION RANGE";
	public static final String SESSION_DURATION = "SESSION DURATION RANGE";
	public static final String CONFIDENCE_LEVEL = "CONFIDENCE LEVEL RANGE";
	public static final String DIFFICULTY_LEVEL = "DIFFICULTY LEVEL RANGE";
	public static final String WORKER_SCORE = "WORKER SCORE RANGE";
	public static final String WORKER_SCORE_EXCLUSION = "WORKER SCORES EXCLUDED"; //List of professions to be considered
	public static final String EXPLANATION_SIZE = "EXPLANATION SIZE RANGE";
	public static final String WORKER_IDK = "WORKER %IDK RANGE";
	public static final String WORKER_PROFESSION = "WORKER PROFESSION"; //List of professions to be considered.
	public static final String WORKER_YEARS_OF_EXEPERIENCE = "WORKER YEARS OF EXPERIENCE"; //List of professions to be considered.

	public HashMap<String,Range> combinationMap;


	//-----------------------------------------------------------------------------------------------------
	//FOR REPORT GENERATION
	
	public static String[] headerList = { 
		
		ANSWER_DURATION,
		CONFIDENCE_LEVEL,
		DIFFICULTY_LEVEL,
		EXPLANATION_SIZE,
		WORKER_SCORE_EXCLUSION,
		WORKER_SCORE,
		WORKER_IDK,
		WORKER_PROFESSION,
		WORKER_YEARS_OF_EXEPERIENCE
	};

	public static String getFilterHeaders(){
		String result="";
		for(String name:headerList){
			if(result.length()==0) 
				result = name+":";
			else
				result = result + name+":";
		}
		return result;
	}




	public String toString(String[] headerList){
		String result="";
		for(String name : headerList){
			Range range = combinationMap.get(name);
			//System.out.println(name);
			if(result.length()==0)				
				result = range.toString();
			else
				result = result+":"+range.toString();
		}
		return result;
	}


	public void addFilterParam(String filterName, int max, int min){
		if(combinationMap==null)
			combinationMap = new HashMap<String, Range>();
		combinationMap.put(filterName, new Range(min,max));
	}

	public Range getFilterParam(String filterName){
		return this.combinationMap.get(filterName);
	}


	public Filter getFilter(){

		Filter filter = new Filter();

		for(String filterName: this.combinationMap.keySet()){

			int min=-1;
			int max=-1;

			if(this.combinationMap.get(filterName).min!=null){
				min = this.combinationMap.get(filterName).min.intValue();
				max = this.combinationMap.get(filterName).max.intValue();
			}

			if(filterName.compareTo(FilterCombination.ANSWER_DURATION)==0){
				filter.setAnswerDurationCriteria(new Double(min), new Double(max));
			}
			else
				if(filterName.compareTo(FilterCombination.SESSION_DURATION)==0){
					filter.setSessionDurationCriteria(new Double(min), new Double(max));
				}
				else
					if(filterName.compareTo(FilterCombination.CONFIDENCE_LEVEL)==0){
						filter.setConfidenceCriteria(min, max);
					}
					else
						if(filterName.compareTo(FilterCombination.DIFFICULTY_LEVEL)==0){
							filter.setDifficultyCriteria(min, max);
						}
						else 
							if(filterName.compareTo(FilterCombination.EXPLANATION_SIZE)==0){
								filter.setExplanationSizeCriteria(min, max);
							}
							else 
								if(filterName.compareTo(FilterCombination.WORKER_SCORE)==0){
									filter.setWorkerScoreCriteria(min, max);
								}
								else
									if(filterName.compareTo(FilterCombination.WORKER_SCORE_EXCLUSION)==0){
										filter.setWorkerScoreExclusionList(this.combinationMap.get(filterName).list);
									}
									else
										if(filterName.compareTo(FilterCombination.WORKER_IDK)==0){
											filter.setIDKPercentageCriteria(new Double(min), new Double(max));
										}
										else
											if(filterName.compareTo(FilterCombination.WORKER_PROFESSION)==0){
												filter.setWorkerProfessionMap(this.combinationMap.get(filterName).professionExclusionList);
											}
											else
												if(filterName.compareTo(FilterCombination.WORKER_YEARS_OF_EXEPERIENCE)==0){
													Range range = this.combinationMap.get(filterName);
													filter.setYearsOfExperience(range.minD, range.maxD);
												}
		}
		return filter;	
	}

	public void addFilterParam(String workerScoreExclusion,
			int[] workerExclusionList) {
		if(combinationMap==null)
			combinationMap = new HashMap<String, Range>();
		combinationMap.put(workerScoreExclusion, new Range(workerExclusionList));
	}

	public void addFilterParam(String workerProfession, String[] workerProfessionList) {
		if(combinationMap==null)
			combinationMap = new HashMap<String, Range>();
		combinationMap.put(workerProfession, new Range(workerProfessionList));
	}




	public void addFilterParam(String workerYearsOfExeperienceProfession,
			double maxYearsOfExperience, double minYearsOfExperience) {
		if(combinationMap==null)
			combinationMap = new HashMap<String, Range>();
		combinationMap.put(workerYearsOfExeperienceProfession, new Range(minYearsOfExperience,maxYearsOfExperience));		
	}


}
