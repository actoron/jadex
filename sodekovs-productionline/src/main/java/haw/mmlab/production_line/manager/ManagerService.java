package haw.mmlab.production_line.manager;

import haw.mmlab.production_line.common.ConsoleMessage;
import haw.mmlab.production_line.service.IManagerService;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;

/**
 * Service methods provided by the manager agent.
 * 
 * @author thomas
 */
@Service
public class ManagerService implements IManagerService {

	/** Reference to the manager agent */
	@ServiceComponent
	private ManagerAgent agent = null;

	public void informWPProduced(String taskId) {
		if (agent.getProducedWPs().containsKey(taskId)) {
			// increment by 1
			agent.getProducedWPs().put(taskId, agent.getProducedWPs().get(taskId) + 1);
		} else {
			agent.getProducedWPs().put(taskId, 1);

			if (!agent.isFirstWPConsumed()) {
				agent.setFirstWPConsumed(true);
				agent.startDropout();
			}
		}
	}

	public void informWPConsumed(String taskId) {
		if (agent.getConsumedWPs().containsKey(taskId)) {
			// increment by 1
			agent.getConsumedWPs().put(taskId, agent.getConsumedWPs().get(taskId) + 1);
		} else {
			agent.getConsumedWPs().put(taskId, 1);
		}
	}

	public void informFinished(String taskId) {
		agent.getFinishedTasks().put(taskId, Boolean.TRUE);
		agent.checkFinish();
	}

	public void handleConsoleMsg(ConsoleMessage message) {
		agent.printConsoleMsg(message);
	}
}