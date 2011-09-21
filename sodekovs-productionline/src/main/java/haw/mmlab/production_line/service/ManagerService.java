package haw.mmlab.production_line.service;

import haw.mmlab.production_line.common.ConsoleMessage;
import haw.mmlab.production_line.manager.ManagerAgent;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.commons.future.IFuture;

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

	public IFuture<Void> informWPProduced(String taskId) {
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

		return IFuture.DONE;
	}

	public IFuture<Void> informWPConsumed(String taskId) {
		if (agent.getConsumedWPs().containsKey(taskId)) {
			// increment by 1
			agent.getConsumedWPs().put(taskId, agent.getConsumedWPs().get(taskId) + 1);
		} else {
			agent.getConsumedWPs().put(taskId, 1);
		}

		return IFuture.DONE;
	}

	public IFuture<Void> informFinished(String taskId) {
		agent.getFinishedTasks().put(taskId, Boolean.TRUE);
		agent.checkFinish();

		return IFuture.DONE;
	}

	public IFuture<Void> handleConsoleMsg(ConsoleMessage message) {
		agent.printConsoleMsg(message);

		return IFuture.DONE;
	}
}