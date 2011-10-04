package haw.mmlab.production_line.service;

import haw.mmlab.production_line.common.ConsoleMessage;
import haw.mmlab.production_line.common.ProcessWorkpieceAgent;
import haw.mmlab.production_line.configuration.Workpiece;
import haw.mmlab.production_line.dropout.config.Action;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

import java.util.logging.Logger;

/**
 * The service for agents that process workpieces.
 * 
 * @author thomas
 */
@Service
public class ProcessWorkpieceService implements IProcessWorkpieceService {

	/** Reference to the agent */
	@ServiceComponent
	protected ProcessWorkpieceAgent agent = null;

	private String id = null;

	private String type = null;

	/**
	 * 
	 * @param agent
	 * @param type
	 */
	public ProcessWorkpieceService(ProcessWorkpieceAgent agent, String type) {
		this.agent = agent;
		this.id = agent.getId();
		this.type = type;
	}

	/**
	 * Get the services id.
	 * 
	 * @return the services id
	 * 
	 * @directcall (Is called on caller thread).
	 */
	public String getId() {
		return id;
	}

	/**
	 * Get the type of the agent offering this service.
	 * 
	 * @return the type of the agent offering this service
	 * 
	 * @directcall (Is called on caller thread).
	 */
	public String getType() {
		return type;
	}

	/**
	 * Processes of the given workpiece.
	 * 
	 * @param workpiece
	 *            the given workpiece
	 * @param senderId
	 *            the agent id of the sender
	 * @return
	 */
	public IFuture<Boolean> process(Workpiece workpiece, String agentId) {
		Future<Boolean> result = new Future<Boolean>();

		String id = agent.getId();
		Logger logger = agent.getLogger();

		String msg = id + " has received " + workpiece + " from " + agentId;
		logger.fine(msg);
		agent.handleConsoleMsg(new ConsoleMessage(ConsoleMessage.TYPE_PRODLINE, msg), logger);

		if (agent.receiveWorkpiece(workpiece, agentId)) {
			msg = id + " successfully processed " + workpiece + " from " + agentId;
			logger.fine(msg);
			agent.handleConsoleMsg(new ConsoleMessage(ConsoleMessage.TYPE_PRODLINE, msg), logger);
			result.setResult(Boolean.TRUE);
			return result;
		}

		msg = id + " could not process " + workpiece + " from " + agentId;
		logger.fine(msg);
		agent.handleConsoleMsg(new ConsoleMessage(ConsoleMessage.TYPE_PRODLINE, msg), logger);
		result.setResult(Boolean.FALSE);
		return result;
	}

	/**
	 * Executes a Dropout-{@link Action}.
	 * 
	 * @param action
	 *            the {@link Action} to be executed
	 */
	public IFuture<Void> executeDropoutAction(Action action) {
		agent.getLogger().fine("executeDropoutAction was called in " + agent.getId());
		agent.handleDropout(action);

		return IFuture.DONE;
	}
}