package haw.mmlab.production_line.service;

import haw.mmlab.production_line.common.ConsoleMessage;
import haw.mmlab.production_line.common.ProcessWorkpieceAgent;
import haw.mmlab.production_line.configuration.Workpiece;
import haw.mmlab.production_line.dropout.config.Action;
import haw.mmlab.production_line.robot.RobotAgent;
import jadex.bridge.service.BasicService;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

import java.util.logging.Logger;

/**
 * The service for agents that process workpieces.
 * 
 * @author thomas
 */
public class ProcessWorkpieceService extends BasicService implements IProcessWorkpieceService {

	/** The agents id */
	protected String id = null;

	/** The logger */
	protected Logger logger = null;

	/** The agents type */
	protected String type = null;

	/** Reference to the agent */
	protected ProcessWorkpieceAgent agent = null;

	/**
	 * Constructor.
	 * 
	 * @param agent
	 *            reference to the {@link RobotAgent}
	 * @param id
	 *            the agents id
	 * @param type
	 *            the agents type
	 * @param logger
	 *            reference to the agents {@link Logger}
	 */
	public ProcessWorkpieceService(ProcessWorkpieceAgent agent, String id, String type, Logger logger) {
		super(agent.getServiceProvider().getId(), IProcessWorkpieceService.class, null);

		this.agent = agent;
		this.id = id;
		this.type = type;
		this.logger = logger;
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
	public IFuture process(Workpiece workpiece, String agentId) {
		Future result = new Future();

		String msg = id + " has received " + workpiece + " from " + agentId;
		logger.fine(msg);
		ServiceHelper.handleConsoleMsg(agent.getServiceProvider(), new ConsoleMessage(ConsoleMessage.TYPE_PRODLINE, msg), logger);

		if (agent.receiveWorkpiece(workpiece, agentId)) {
			msg = id + " successfully processed " + workpiece + " from " + agentId;
			logger.fine(msg);
			ServiceHelper.handleConsoleMsg(agent.getServiceProvider(), new ConsoleMessage(ConsoleMessage.TYPE_PRODLINE, msg), logger);
			result.setResult(Boolean.TRUE);
			return result;
		}

		msg = id + " could not process " + workpiece + " from " + agentId;
		logger.fine(msg);
		ServiceHelper.handleConsoleMsg(agent.getServiceProvider(), new ConsoleMessage(ConsoleMessage.TYPE_PRODLINE, msg), logger);
		result.setResult(Boolean.FALSE);
		return result;
	}

	/**
	 * Executes a Dropout-{@link Action}.
	 * 
	 * @param action
	 *            the {@link Action} to be executed
	 */
	public void executeDropoutAction(Action action) {
		logger.fine("executeDropoutAction was called in " + id);
		agent.handleDropout(action);
	}
}