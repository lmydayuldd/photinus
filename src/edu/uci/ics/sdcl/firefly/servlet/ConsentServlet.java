package edu.uci.ics.sdcl.firefly.servlet;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.uci.ics.sdcl.firefly.controller.StorageManager;
import edu.uci.ics.sdcl.firefly.storage.SkillTestSource;

/**
 * Servlet implementation class ConsentServlet
 */
public class ConsentServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private String SkillTestPage = "/SkillTest.jsp";
	private String ErrorPage = "/ErrorPage.jsp";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ConsentServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String subAction = request.getParameter("subAction");
		
		if(subAction.compareTo("loadQuestions")==0){
				Date currentDate = new Date();	
				StorageManager manager = new StorageManager();
				String userId = manager.generateWorkerID(currentDate);
				String hitId = "hitID";
				// now passing parameters to the next page
				request.setAttribute("userId", userId);
				request.setAttribute("hitId", hitId);
				request.setAttribute("subAction", "gradeAnswers");
				request = this.loadQuestions(request, response);
				request.getRequestDispatcher(SkillTestPage).include(request, response);
			}
			else{
				request.setAttribute("error", "action not recognized: "+ subAction);
				request.getRequestDispatcher(ErrorPage).include(request, response);
			}	
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {}

	private HttpServletRequest loadQuestions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		SkillTestSource source = new SkillTestSource();
		request.setAttribute("editor1", source.getSourceOne());
		request.setAttribute("editor2", source.getSourceTwo());
		request.setAttribute("subAction", "gradeAnswers");
		return request;
	}
	
}
