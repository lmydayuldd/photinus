package edu.uci.ics.sdcl.firefly.report.predictive;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import edu.uci.ics.sdcl.firefly.Microtask;

/**
 * 
 * Generate two types of filters
 * 
 * Type-1 
 * See methods generate
 * Generate filters that select answers by workers' profession and
 * questions difficulty. Workers' score consist in the grade 
 * they got in the qualification test. The difficulty of question is
 * the level chosen by each worker. 
 * 
 * Type-2
 * UNION of one or more subcrowds (see method composeSubcrowds)
 * 
 *  
 * @author adrianoc
 *
 */
public class FilterCombination_Score_DifficultyMatrix {

	public static String WORKER_SCORE_100_DIFFICULTY_5 =  "WORKER_SCORE_100_DIFFICULTY_5";
	public static String WORKER_SCORE_100_DIFFICULTY_4 =  "WORKER_SCORE_100_DIFFICULTY_4";
	public static String WORKER_SCORE_100_DIFFICULTY_3 =  "WORKER_SCORE_100_DIFFICULTY_3";
	public static String WORKER_SCORE_100_DIFFICULTY_2 =  "WORKER_SCORE_100_DIFFICULTY_2";
	public static String WORKER_SCORE_100_DIFFICULTY_1 =  "WORKER_SCORE_100_DIFFICULTY_1";

	public static String WORKER_SCORE_80_DIFFICULTY_5 =  "WORKER_SCORE_100_DIFFICULTY_5";
	public static String WORKER_SCORE_80_DIFFICULTY_4 =  "WORKER_SCORE_100_DIFFICULTY_4";
	public static String WORKER_SCORE_80_DIFFICULTY_3 =  "WORKER_SCORE_100_DIFFICULTY_3";
	public static String WORKER_SCORE_80_DIFFICULTY_2 =  "WORKER_SCORE_100_DIFFICULTY_2";
	public static String WORKER_SCORE_80_DIFFICULTY_1 =  "WORKER_SCORE_100_DIFFICULTY_1";

	public static String WORKER_SCORE_60_DIFFICULTY_5 =  "WORKER_SCORE_60_DIFFICULTY_5";
	public static String WORKER_SCORE_60_DIFFICULTY_4 =  "WORKER_SCORE_60_DIFFICULTY_4";
	public static String WORKER_SCORE_60_DIFFICULTY_3 =  "WORKER_SCORE_60_DIFFICULTY_3";
	public static String WORKER_SCORE_60_DIFFICULTY_2 =  "WORKER_SCORE_60_DIFFICULTY_2";
	public static String WORKER_SCORE_60_DIFFICULTY_1 =  "WORKER_SCORE_60_DIFFICULTY_1";

	/** Produces a map with all pairs that must be excluded. The only level that
	 * should remain is the one provided as parameter
	 * 
	 * @param difficultyObj only difficulty level that should remain
	 * @return
	 */
	public static HashMap<String, Tuple> getExclusionTupleMap(Integer difficultyObj){

		int[] difficultyList = {1,2,3,4,5};
		int[] exclusionList = remove(difficultyList,difficultyObj.intValue());

		HashMap<String, Tuple>  map = new HashMap<String, Tuple>();
		for(int i=0;i<exclusionList.length;i++){
			int difficulty = exclusionList[i];
			map.put(new Tuple(0,difficulty).toString(), new Tuple(0,difficulty));
			map.put(new Tuple(1,difficulty).toString(), new Tuple(1,difficulty));
			map.put(new Tuple(2,difficulty).toString(), new Tuple(2,difficulty));
			map.put(new Tuple(3,difficulty).toString(), new Tuple(3,difficulty));
			map.put(new Tuple(4,difficulty).toString(), new Tuple(4,difficulty));
			map.put(new Tuple(5,difficulty).toString(), new Tuple(5,difficulty));
		}
		return map;
	}

	public static CombinedFilterRange getRangeScore(Integer score){

		int[] scoreList = {3,4,5};
		int[] exclusionList = remove(scoreList,score.intValue());	
		CombinedFilterRange range = new CombinedFilterRange();

		range.setMaxWorkerScore(score.intValue());
		range.setMinWorkerScore(score.intValue());
		range.setWorkerScoreExclusionList(exclusionList);
		range.setWorkerScoreList(new int[]{score.intValue()});
		range.setUndefinedWithDefault();

		return range;
	}


	private static int[] remove(int[] list, int score){

		int length = list.length-1;
		int[] resultList = new int[length];

		int j=0;
		for(int i=0; i<list.length; i++){
			if(list[i]!=score){
				resultList[j] = list[i];
				j++;
			}
		}
		return resultList;
	}


	private static String getName(int score, int difficulty){

		String name = "WORKER_SCORE-";


		Double percentScore = new Double(score/5.0) * 100;
		String percentScoreStr = new DecimalFormat("#").format(percentScore);

		name = name+percentScoreStr+"-DIFFICULTY-"+difficulty;

		return name;
	}
	

	public static HashMap<String,CombinedFilterRange> generateFilter(){

		HashMap<String,CombinedFilterRange> rangeMap = new 	HashMap<String,CombinedFilterRange>();

		for(int score =3;score<=5;score++){
			for(int difficulty=1;difficulty<=5;difficulty++){
				CombinedFilterRange range = getRangeScore(score);
				range.setRangeName(getName(score,difficulty));
				range.setConfidenceDifficultyPairMap(getExclusionTupleMap(difficulty));
				rangeMap.put(range.getRangeName(),range);
			}
		}
		return rangeMap;
	}	


	public static String tupleMapToString(HashMap<String, Tuple> tupleMap){

		String result="";

		Iterator<String> iter = tupleMap.keySet().iterator();
		while(iter.hasNext()){
			result = result + ";" + iter.next();
		}
		return result;
	}

	

	private static FilterCombination generateCombination (CombinedFilterRange range){
		FilterCombination combination = new FilterCombination();
		combination.addFilterParam(FilterCombination.FIRST_ANSWER_DURATION, range.getMaxFirstAnswerDuration(), range.getMinFirstAnswerDuration());
		combination.addFilterParam(FilterCombination.SECOND_THIRD_ANSWER_DURATION, range.getMaxSecondThirdAnswerDuration(), range.getMinSecondThirdAnswerDuration());
		combination.addFilterParam(FilterCombination.CONFIDENCE_DIFFICULTY_PAIRS,range.getConfidenceDifficultyPairList());
		combination.addFilterParam(FilterCombination.CONFIDENCE_LEVEL, range.getMaxConfidence(), range.getMinConfidence());
		combination.addFilterParam(FilterCombination.DIFFICULTY_LEVEL,range.getMaxDifficulty(),range.getMinDifficulty());
		combination.addFilterParam(FilterCombination.EXPLANATION_SIZE, range.getMaxExplanationSize(), range.getMinExplanationSize());
		combination.addFilterParam(FilterCombination.WORKER_SCORE_EXCLUSION, range.getWorkerScoreExclusionList());
		combination.addFilterParam(FilterCombination.WORKER_SCORE, range.getMaxWorkerScore(), range.getMinWorkerScore());
		combination.addFilterParam(FilterCombination.WORKER_IDK, range.getMaxWorkerIDKPercentage(),range.getMinWorkerIDKPercentage());
		combination.addFilterParam(FilterCombination.WORKER_PROFESSION, range.getProfessionExclusionList());
		combination.addFilterParam(FilterCombination.WORKER_YEARS_OF_EXEPERIENCE, range.getMaxYearsOfExperience(), range.getMinWorkerYearsOfExperience());
		combination.addFilterParam(FilterCombination.EXCLUDED_QUESTIONS, range.getQuestionsToExcludeMap());
		combination.addFilterParam(FilterCombination.FIRST_HOURS, range.getMaxDate(),range.getMinDate());
		combination.addFilterParam(FilterCombination.MAX_ANSWERS, 20, 0);
		return combination;
	}

	public static ArrayList<SubCrowd> composeSubCrowds(){

		ArrayList<SubCrowd>  subCrowdList = new ArrayList<SubCrowd>();
		HashMap<String, CombinedFilterRange> map = AttributeRangeGenerator.getMostDifficultySkill();
		CombinedFilterRange range;


		//------------------------------------------------
		//All 100 score, ignore 4,5 difficulty from 80 score and 60 score
		SubCrowd crowd = new SubCrowd();
		crowd.name = "score100_diffAny U score80_diff123 U score60_diff123";

		range = map.get(AttributeRangeGenerator.WORKER_SCORE_100_DIFFICULTY_ALL);			
		FilterCombination combination = generateCombination(range);
		crowd.addOR_Filter(combination);

		range = map.get(AttributeRangeGenerator.WORKER_SCORE_80_DIFFICULTY_1_2_3);	
		combination = generateCombination(range);
		crowd.addOR_Filter(combination);

		range = map.get(AttributeRangeGenerator.WORKER_SCORE_60_DIFFICULTY_1_2_3);	
		combination = generateCombination(range);
		crowd.addOR_Filter(combination);
		subCrowdList.add(crowd);

		//------------------------------------------------
		//All 100 score, ignore 4,5 difficulty from 80 score and 5 difficulty 60 score
		crowd = new SubCrowd();
		crowd.name = "score100_diffAny U score80_diff123 U score60_diff1234";

		range = map.get(AttributeRangeGenerator.WORKER_SCORE_100_DIFFICULTY_ALL);			
		combination = generateCombination(range);
		crowd.addOR_Filter(combination);

		range = map.get(AttributeRangeGenerator.WORKER_SCORE_80_DIFFICULTY_1_2_3);	
		combination = generateCombination(range);
		crowd.addOR_Filter(combination);

		range = map.get(AttributeRangeGenerator.WORKER_SCORE_60_DIFFICULTY_1_2_3_4);	
		combination = generateCombination(range);
		crowd.addOR_Filter(combination);
		subCrowdList.add(crowd);


		//------------------------------------------------
		//All 100 score, ignore top 4 difficulty from 80 and top 4,5  of 60 score
		crowd = new SubCrowd();
		crowd.name = "score100_diffAny U score80_diff1234 U score60_diff123";

		range = map.get(AttributeRangeGenerator.WORKER_SCORE_100_DIFFICULTY_ALL);			
		combination = generateCombination(range);
		crowd.addOR_Filter(combination);

		range = map.get(AttributeRangeGenerator.WORKER_SCORE_80_DIFFICULTY_1_2_3_4);	
		combination = generateCombination(range);
		crowd.addOR_Filter(combination);

		range = map.get(AttributeRangeGenerator.WORKER_SCORE_60_DIFFICULTY_1_2_3);	
		combination = generateCombination(range);
		crowd.addOR_Filter(combination);
		subCrowdList.add(crowd);

		//------------------------------------------------
		//All 100 score, ignore 4,5 difficulty from 80 score and 4,5 difficulty 60 score
		crowd = new SubCrowd();
		crowd.name = "score100_diffAny U score80_diff1234 U score60_diff1234";

		range = map.get(AttributeRangeGenerator.WORKER_SCORE_100_DIFFICULTY_ALL);			
		combination = generateCombination(range);
		crowd.addOR_Filter(combination);

		range = map.get(AttributeRangeGenerator.WORKER_SCORE_80_DIFFICULTY_1_2_3_4);	
		combination = generateCombination(range);
		crowd.addOR_Filter(combination);

		range = map.get(AttributeRangeGenerator.WORKER_SCORE_60_DIFFICULTY_1_2_3_4);	
		combination = generateCombination(range);
		crowd.addOR_Filter(combination);
		subCrowdList.add(crowd);

		//------------------------------------------------
		//All 100 score, all 80 and top 4,5  of 60 score
		crowd = new SubCrowd();
		crowd.name = "score100_80diffAny U score60_diff123";

		range = map.get(AttributeRangeGenerator.WORKER_SCORE_100_80_DIFFICULTY_ALL);			
		combination = generateCombination(range);
		crowd.addOR_Filter(combination);

		range = map.get(AttributeRangeGenerator.WORKER_SCORE_60_DIFFICULTY_1_2_3);	
		combination = generateCombination(range);
		crowd.addOR_Filter(combination);
		subCrowdList.add(crowd);

		//------------------------------------------------
		//Difficult 3,4,5 from 100 or 80 scores + difficulty 123 from 60 score
		crowd = new SubCrowd();
		crowd.name = "score100_diff543 U score80_diff543 U score60_diff123";

		range = map.get(AttributeRangeGenerator.WORKER_SCORE_100_80_DIFFICULTY_5_4_3);			
		combination = generateCombination(range);
		crowd.addOR_Filter(combination);

		range = map.get(AttributeRangeGenerator.WORKER_SCORE_60_DIFFICULTY_1_2_3);	
		combination = generateCombination(range);
		crowd.addOR_Filter(combination);
		subCrowdList.add(crowd);
		
		//------------------------------------------------
		//ignore diff 5 from 100 score, ignore 4,5 difficulty from 80 score and 4,5 difficulty 60 score
		crowd = new SubCrowd();
		crowd.name = "score100_diff1234 U score80_diff123 U score60_diff123";

		range = map.get(AttributeRangeGenerator.WORKER_SCORE_100_DIFFICULTY_1_2_3_4);			
		combination = generateCombination(range);
		crowd.addOR_Filter(combination);

		range = map.get(AttributeRangeGenerator.WORKER_SCORE_80_DIFFICULTY_1_2_3);	
		combination = generateCombination(range);
		crowd.addOR_Filter(combination);

		range = map.get(AttributeRangeGenerator.WORKER_SCORE_60_DIFFICULTY_1_2_3);	
		combination = generateCombination(range);
		crowd.addOR_Filter(combination);
		subCrowdList.add(crowd);
		
		//------------------------------------------------
				//ignore diff 5 from 100 score, ignore 5 difficulty from 80 score and5 difficulty 60 score
				crowd = new SubCrowd();
				crowd.name = "score100_diff1234 U score80_diff1234 U score60_diff1234";

				range = map.get(AttributeRangeGenerator.WORKER_SCORE_100_DIFFICULTY_1_2_3_4);			
				combination = generateCombination(range);
				crowd.addOR_Filter(combination);

				range = map.get(AttributeRangeGenerator.WORKER_SCORE_80_DIFFICULTY_1_2_3_4);	
				combination = generateCombination(range);
				crowd.addOR_Filter(combination);

				range = map.get(AttributeRangeGenerator.WORKER_SCORE_60_DIFFICULTY_1_2_3_4);	
				combination = generateCombination(range);
				crowd.addOR_Filter(combination);
				subCrowdList.add(crowd);

				
				//------------------------------------------------
				//ignore diff 5 from 100 score, ignore 5 difficulty from 80 score and5 difficulty 60 score
				crowd = new SubCrowd();
				crowd.name = "score100_diff123 U score80_diff123 U score60_diff123";

				range = map.get(AttributeRangeGenerator.WORKER_SCORE_100_DIFFICULTY_1_2_3);			
				combination = generateCombination(range);
				crowd.addOR_Filter(combination);

				range = map.get(AttributeRangeGenerator.WORKER_SCORE_80_DIFFICULTY_1_2_3);	
				combination = generateCombination(range);
				crowd.addOR_Filter(combination);

				range = map.get(AttributeRangeGenerator.WORKER_SCORE_60_DIFFICULTY_1_2_3);	
				combination = generateCombination(range);
				crowd.addOR_Filter(combination);
				subCrowdList.add(crowd);
				
		return subCrowdList;

	}

	/** Used for testing */
	public static void main(String args[]){

		HashMap<String,CombinedFilterRange> map = FilterCombination_Score_DifficultyMatrix.generateFilter();
		Iterator<String> iter = map.keySet().iterator();
		while(iter.hasNext()){
			String key = iter.next();
			CombinedFilterRange range = map.get(key);
			String name = range.getRangeName();
			int[] scoreList = range.getWorkerScoreList();
			int[] excludedList = range.getWorkerScoreExclusionList();
			HashMap<String, Tuple> tupleMap = range.getConfidenceDifficultyPairList();

			System.out.println("key:"+name+" score:"+scoreList[0] + " excluded: "+excludedList[0]+","+excludedList[1] + ", confidence:difficulty =" + tupleMapToString(tupleMap));
		}
	}


}
