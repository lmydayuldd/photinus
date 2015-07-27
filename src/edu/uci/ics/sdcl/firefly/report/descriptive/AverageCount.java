package edu.uci.ics.sdcl.firefly.report.descriptive;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import edu.uci.ics.sdcl.firefly.Answer;
import edu.uci.ics.sdcl.firefly.Microtask;
import edu.uci.ics.sdcl.firefly.util.PropertyManager;

public class AverageCount extends CountReport {
	
	private final int NUMBER_OF_ANSWERS;
 
	
	public AverageCount() {
		PropertyManager properties = PropertyManager.initializeSingleton();
		this.NUMBER_OF_ANSWERS = properties.answersPerMicrotask;
	}
	
	@Override
	public Map<String, List<String>> generateReport(HeaderReport headerReport, AnswerReport answerReport) {
		answerReportAverage(headerReport,answerReport);
		answerOptionAverage(headerReport,answerReport);
		confidenceAverage(headerReport, answerReport);
		return this.countContent;
	}
	
	@Override
	public String getType() {
		return "Averages Answer Option / Confidence Level";
	}
	
	private void answerReportAverage(HeaderReport headerReport, AnswerReport answerReport)
	{
		Map<String, List<String>> content = answerReport.getContent();
		List<String> firstElement = (content.entrySet().iterator().next()).getValue();
		List<String> averageList = new ArrayList<String>();
		for (int i = 0; i < firstElement.size() ; i++) {
			int size = 0;
			Double average = 0.0;
			for (String key : content.keySet()) {
				String text = content.get(key).get(i);
				if(!text.equals(""))
				{
					average += Integer.valueOf(text);
					size++;
				}
			}
			average /= size;
			averageList.add(average.toString());
		}
		this.countContent.put("Average " + answerReport.getDataNature(), averageList);
	}
	
	private void confidenceAverage(HeaderReport headerReport, AnswerReport answerReport)
	{
		List<List<String>> confidenceList = new ArrayList<List<String>>();
		for (int i = 0; i < 6; i++) {
			confidenceList.add(new ArrayList<String>());
		}
		
		Double[] confidenceAverage = new Double[6];
		Integer[] confidenceNumber = new Integer[6];
		List<String> questionIDList = headerReport.getContent().get("Question ID");
		SessionDTO dto = new FileSessionDTO();
		Map<String, Microtask> microtaks = dto.getMicrotasks();
		Map<String, List<String>> content = answerReport.getContent();
		for (int i = 0; i < questionIDList.size(); i++) {
			String questionID = questionIDList.get(i);
			Arrays.fill(confidenceAverage, 0D);
			Arrays.fill(confidenceNumber, 0);
			Microtask question = microtaks.get(questionID);
			List<Answer> answerList = question.getAnswerList();
			Iterator<Entry<String, List<String>>> it = content.entrySet().iterator();
			for (int j =0; j < NUMBER_OF_ANSWERS; j++ ) {
				if((answerList.size()-1) >= j)
				{
					Answer answer = answerList.get(j);
					String text = (it.next().getValue().get(i));
					confidenceAverage[answer.getConfidenceOption()] += (text.equals("") ? 0 : Integer.valueOf(text));
					confidenceNumber[answer.getConfidenceOption()]++;
				}
			}
			for (int j = 0; j < confidenceNumber.length; j++) {
				if((confidenceAverage[j] != 0) && (confidenceNumber[j] != 0))
					confidenceAverage[j] /= confidenceNumber[j];
				confidenceList.get(j).add(confidenceAverage[j].toString());
			}
		}
		for (int i = 0; i < confidenceList.size(); i++) {
			this.countContent.put("Average " + answerReport.getDataNature() + "Confidence ( " + i + " )", confidenceList.get(i));
		}
	}
	
	private void answerOptionAverage(HeaderReport headerReport, AnswerReport answerReport)
	{	
		List<String> questionIDList = headerReport.getContent().get("Question ID");
		SessionDTO dto = new FileSessionDTO();
		Map<String, Microtask> microtaks = dto.getMicrotasks();
		Map<String, List<String>> content = answerReport.getContent();
		Map<String, Double> optionsAverage = null;
		Map<String, Integer> optionsSize = null;
		Map<String, List<String>> optionList = new LinkedHashMap<String, List<String>>();
		
		optionList.put(Answer.YES, new ArrayList<String>());
		optionList.put(Answer.NO, new ArrayList<String>());
		optionList.put(Answer.I_DONT_KNOW, new ArrayList<String>());
		
		for (int i = 0; i < questionIDList.size(); i++) {
			optionsAverage = new LinkedHashMap<String, Double>();
			optionsSize = new LinkedHashMap<String, Integer>();
			String questionID = questionIDList.get(i);
			Microtask question = microtaks.get(questionID);
			List<Answer> answerList = question.getAnswerList();
			Iterator<Entry<String, List<String>>> it = content.entrySet().iterator();
			for (int j =0; j < NUMBER_OF_ANSWERS; j++ ) {
				if((answerList.size()-1) >= j)
				{
					Answer answer = answerList.get(j);
					String text = (it.next().getValue().get(i));
					if(optionsAverage.containsKey(answer.getOption()))
					{
						optionsAverage.put(answer.getOption(), optionsAverage.get(answer.getOption()) + (text.equals("") ? 0 : Double.valueOf(text)));
						optionsSize.put(answer.getOption(), optionsSize.get(answer.getOption()) + 1);
					}
					else
					{
						optionsAverage.put(answer.getOption(), (text.equals("") ? 0 : Double.valueOf(text)));
						optionsSize.put(answer.getOption(), 1);
					}
				}
			}
			for (String key : optionList.keySet()) {
				if(optionsAverage.containsKey(key))
				{
					optionsAverage.put(key, optionsAverage.get(key) / optionsSize.get(key));
					optionList.get(key).add(optionsAverage.get(key).toString());
				}
				else
					optionList.get(key).add("0");
			}
		}
		for (String key : optionList.keySet()) {
			this.countContent.put("Average " + answerReport.getDataNature() + " of " + key, optionList.get(key));
		}
	}

}