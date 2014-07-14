package edu.uci.ics.sdcl.firefly.servlet;

import java.io.IOException; 
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.uci.ics.sdcl.firefly.FileDebugSession;
import edu.uci.ics.sdcl.firefly.Microtask;
import edu.uci.ics.sdcl.firefly.export.file.ExcelFileDealer;
import edu.uci.ics.sdcl.firefly.memento.MicrotaskMemento;

/**
 * Servlet implementation class MicrotaskController
 */
public class ResultsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ResultsServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		//Build the structure to report
		HashMap<String,HashMap<String,MethodData>> resultMap = buildResults();
		
		// Extracting the xlsx file
		ExcelFileDealer.writeToXlsx(resultMap);

		String results = this.toString(resultMap);
		
		System.out.println("Results: "+ results);
		
		request.setAttribute("results",results);
		
		request.getRequestDispatcher("/Results.jsp").forward(request, response);
	}


	private HashMap<String,HashMap<String,MethodData>> buildResults(){
		//indexed by fileName and method name
		HashMap<String,HashMap<String,MethodData>> resultMap = new HashMap<String,HashMap<String,MethodData>> ();

		MicrotaskMemento memento = new MicrotaskMemento();
		Set<String> sessionSet= memento.retrieveDebuggingSessionNames();
		if((sessionSet==null) || (!sessionSet.iterator().hasNext())){
			//EMPTY!!!
			System.out.println("DebugSession empty!!");
		}
		else{
			Iterator<String> sessionIterator = sessionSet.iterator();
			while(sessionIterator.hasNext()){
				String fileName = (String) sessionIterator.next();
				FileDebugSession map = memento.read(fileName);

				if(resultMap.get(fileName)==null)
					resultMap.put(fileName, new HashMap<String,MethodData>());

				//Iterate over microtasks
				HashMap<Integer, Microtask> microtaskMap = map.getMicrotaskMap();
				Iterator<Integer> microtaskIter = microtaskMap.keySet().iterator();
				while(microtaskIter.hasNext()){
					Microtask mtask = microtaskMap.get((Integer) microtaskIter.next());
					String methodName = mtask.getMethod().getMethodSignature().getName();
									
					HashMap<String,MethodData> methodMap = resultMap.get(fileName);
					MethodData methodData = methodMap.get(methodName);
					if(methodData==null){
						methodData = new MethodData(mtask.getQuestion());
					}
					methodData.addAnswerList(mtask.getQuestion(),mtask.getAnswerList());
					methodMap.put(methodName, methodData);
					resultMap.put(fileName, methodMap);
				}				
			}

		}
		return resultMap;
	}

	private String toString(HashMap<String,HashMap<String,MethodData>> resultMap){
		String result = new String();

		Iterator<String> fileNamesIter = resultMap.keySet().iterator();
		while(fileNamesIter.hasNext()){
			String fileName = fileNamesIter.next();
			HashMap<String,MethodData> methodMap = resultMap.get(fileName);
			Iterator<String> methodIter = methodMap.keySet().iterator();
			while(methodIter.hasNext()){
				String methodName = methodIter.next();
				MethodData methodData = methodMap.get(methodName);
				HashMap<String, String> answerPerQuestionMap = methodData.getQuestionAnswerMap();
				Iterator<String> questionIter = answerPerQuestionMap.keySet().iterator();
				while(questionIter.hasNext()){
					String questionName = questionIter.next();
					String answers = answerPerQuestionMap.get(questionName);
					result = result + formatString(fileName,methodName,questionName,answers) + "<br><br>";
				}
			}
		}
		return result;
	}

	private String formatString(String fileName, String methodName,
			String questionName, String answers) {

		return "<b>File: </b>"+ fileName+ "<b> Method: </b>"+methodName+"<b> Question: </b>"+ 
				questionName+ "<b> Answers: </b>"+answers;

	}

}
