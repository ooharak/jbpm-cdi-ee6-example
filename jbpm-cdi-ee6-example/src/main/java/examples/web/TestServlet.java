package examples.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.kie.api.task.model.TaskSummary;

import examples.ejb.TestBean;

/**
 * An entry point for the example bean.
 * 
 * JAX-RS impl would be way simpler, but here I choose Servlet
 * because every Java web developer knows .
 * 
 * @author ooharak
 *
 */
@WebServlet(urlPatterns={"/test"})
public class TestServlet extends HttpServlet { 
	@EJB
	TestBean testBean;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.setCharacterEncoding("utf-8");
		resp.setContentType("text/plain");
		PrintWriter out = resp.getWriter();
		
		
		String command = req.getParameter("command");
		String actorId = req.getParameter("actorId");
		String taskId = req.getParameter("taskId");
		String result = req.getParameter("result");
		String instanceId = req.getParameter("instanceId");
		
		
		if ("start".equals(command)) {
			out.println("started a process: instanceID:" + testBean.startProcess());
			
		} else if ("tasks".equals(command)) {
			List<TaskSummary> tasks = testBean.getTasks(Long.parseLong(instanceId), actorId);
			for (TaskSummary taskSummary : tasks) {
				out.println(taskSummary.getName() + ":id=" + taskSummary.getId());
			}
			
		} else if ("doTask".equals(command)) {
			testBean.startAndCompleteTask(Long.parseLong(instanceId), actorId, Long.parseLong(taskId), result);
		}
		out.println("end of reponse");
	}
	
}
