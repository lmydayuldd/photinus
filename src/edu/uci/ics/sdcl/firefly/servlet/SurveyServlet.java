package edu.uci.ics.sdcl.firefly.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.uci.ics.sdcl.firefly.Worker;
import edu.uci.ics.sdcl.firefly.storage.WorkerStorage;

/**
 * Servlet implementation class SurveyServlet
 */
public class SurveyServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    public static String question[] = {"Gender", "Age", "Years progr.", "Difficulty", "Feedback"};   

    /**
     * @see HttpServlet#HttpServlet()
     */
    public SurveyServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		WorkerStorage subjectStore = new WorkerStorage();	// to retrieve date from database
//		Worker subject = subjectStore.readSingleWorker(request.getParameter("userId"));
		Worker subject = subjectStore.readSingleWorker(request.getParameter("userId"));
		if (null != subject){
			subject.addAnswer(question[0], request.getParameter("gender"));
			subject.addAnswer(question[1], request.getParameter("age"));
			subject.addAnswer(question[2], request.getParameter("experience"));
			subject.addAnswer(question[3], request.getParameter("difficulty"));
			subject.addAnswer(question[4], request.getParameter("feedback"));
			System.out.println("Survey: " + subject.getSurveyAnswers());
			subjectStore.insert(request.getParameter("userId"), subject);
			System.out.println("UserId: " + subject.getUserId());
			//Displays the Thanks message
			request.getRequestDispatcher("/Thanks.html").forward(request, response);
		} else{
			request.setAttribute("userId", request.getParameter("userId"));
			request.setAttribute("hitId", request.getParameter("hitId"));
			request.setAttribute("error", "@SurveyServlet - object 'subject' is null");
			//Displays the error page
			request.getRequestDispatcher("/ErrorPage.jsp").forward(request, response);
		}
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}