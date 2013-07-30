package examples.ejb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.TaskSummary;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class TestBean {
	
	@Inject
	RuntimeEngine runtime;
	
	@Inject
	TaskService taskService;
	
	public long startProcess() {
		ProcessInstance pi = runtime.getKieSession().startProcess("Example1");
		return pi.getId();
	}
	
	public List<TaskSummary> getTasks(long instanceId, String actorId) {
		 List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner(actorId, "en-UK");
		 return filterByInstanceId(tasks, instanceId);
	}
	
	public void startAndCompleteTask(long instanceId, String actorId, long taskId, String result) {
		taskService.start(taskId, actorId);
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("result", result);
		taskService.complete(taskId, actorId, data);
	}

	private List<TaskSummary> filterByInstanceId(List<TaskSummary> tasks,
			long instanceId) {
		List<TaskSummary> filtered = new ArrayList<TaskSummary>();
		for (TaskSummary taskSummary : tasks) {
			if (taskSummary.getProcessInstanceId() == instanceId) {
				filtered.add(taskSummary);
			}
		}
		return filtered;
	}


}
