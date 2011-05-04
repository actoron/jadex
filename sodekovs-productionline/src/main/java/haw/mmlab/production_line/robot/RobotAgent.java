package haw.mmlab.production_line.robot;

import haw.mmlab.production_line.common.AgentConstants;
import haw.mmlab.production_line.common.ConsoleMessage;
import haw.mmlab.production_line.common.LoggingHelper;
import haw.mmlab.production_line.common.ProcessWorkpieceAgent;
import haw.mmlab.production_line.configuration.Buffer;
import haw.mmlab.production_line.configuration.BufferElement;
import haw.mmlab.production_line.configuration.Condition;
import haw.mmlab.production_line.configuration.Robot;
import haw.mmlab.production_line.configuration.Role;
import haw.mmlab.production_line.configuration.Task;
import haw.mmlab.production_line.configuration.Workpiece;
import haw.mmlab.production_line.domain.HelpReply;
import haw.mmlab.production_line.domain.HelpRequest;
import haw.mmlab.production_line.logging.database.DatabaseLogger;
import haw.mmlab.production_line.service.IProcessWorkpieceService;
import haw.mmlab.production_line.service.ProcessWorkpieceService;
import haw.mmlab.production_line.service.ServiceHelper;
import haw.mmlab.production_line.state.MainState;
import haw.mmlab.production_line.strategies.AgentData;
import haw.mmlab.production_line.strategies.EvaluationResult;
import haw.mmlab.production_line.strategies.IStrategy;
import haw.mmlab.production_line.strategies.StrategyFactory;
import jadex.bridge.Argument;
import jadex.bridge.IArgument;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.ProvidedServiceInfo;
import jadex.bridge.service.SServiceProvider;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.micro.MicroAgentMetaInfo;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;

/**
 * The robot agents, processes workpieces, stores them in his {@link Buffer} and sends them further to the next transport agent.
 * 
 * @author thomas
 */
public class RobotAgent extends ProcessWorkpieceAgent {

	/** The output {@link Buffer} */
	private Buffer buffer = null;

	/** The used reconfiguration strategety */
	private IStrategy strategy = null;

	@SuppressWarnings("unchecked")
	@Override
	public IFuture agentCreated() {
		// initialize the agents variables
		Robot conf = (Robot) getArgument("config");

		id = conf.getAgentId();
		input = conf.getInput();
		output = conf.getOutput();
		capabilities = conf.getCapabilityStrings();
		assignedRoles = conf.getRoles();
		int bufferSize = conf.getBufferSize();
		if (bufferSize < conf.getRoles().size())
			bufferSize = conf.getRoles().size();
		buffer = new Buffer(bufferSize);

		taskMap = (Map<String, Task>) getArgument("taskMap");

		String strategyName = (String) getArgument("strategy");
		strategy = StrategyFactory.getInstance(strategyName);

		// initialize the logger
		getLogger().setLevel(Level.ALL);
		ConsoleHandler handler = new ConsoleHandler();
		handler.setLevel(Level.parse(LoggingHelper.getLevel()));
		getLogger().addHandler(handler);
		getLogger().setUseParentHandlers(false);

		getLogger().info(id + " created");

		// add the service
		addService(new ProcessWorkpieceService(this, id, AgentConstants.AGENT_TYPE_ROBOT, getLogger()));

		return IFuture.DONE;
	}

	@Override
	public void executeBody() {
		waitForTick(new SendWorkpieceStep());
	}

	@Override
	public IFuture agentKilled() {
		return super.agentKilled();
	}

	/**
	 * Returns the {@link MicroAgentMetaInfo}.
	 * 
	 * @return the {@link MicroAgentMetaInfo}
	 */
	public static MicroAgentMetaInfo getMetaInfo() {
		MicroAgentMetaInfo meta = new MicroAgentMetaInfo();
		meta.setDescription("Robot agent");
		meta.setArguments(new IArgument[] { new Argument("config", "The robot's configuration", "Robot"), new Argument("taskMap", "A map with all the tasks", "Map"),
				new Argument("strategy", "The reconfiguration strategy", "IStrategy") });
		meta.setProvidedServices(new ProvidedServiceInfo[] { new ProvidedServiceInfo(IProcessWorkpieceService.class) });
		return meta;
	}

	/**
	 * This method is called if the robot receives a new workpieces and tries to process it.
	 * 
	 * @param workpiece
	 *            the received workpiece
	 * @param agentId
	 *            the id of the agent from whom the workpiece was received
	 * @return <code>true</code> if the workpiece could be processed else <code>false</code>
	 */
	public boolean receiveWorkpiece(final Workpiece workpiece, String agentId) {
		if (mainState == MainState.RUNNING_IDLE && this.workpiece == null) {
			final Role role = getMatchingRole(workpiece, agentId);
			if (role != null) {
				int bufferSpace = buffer.getAvailableSlots(role, assignedRoles);
				if (bufferSpace > 0) {
					setWorkpiece(workpiece);
					setMainState(MainState.RUNNING);
					workpiece.addOperation(role.getCapability());

					// wait for simulating the processing time
					Integer processTime = role.getProcessingTime() == null ? 0 : role.getProcessingTime();
					waitFor(processTime, new IComponentStep() {

						public Object execute(IInternalAccess ia) {
							setWorkpiece(null);
							setMainState(MainState.RUNNING_IDLE);
							return buffer.enqueue(new BufferElement(workpiece, role));
						}
					});

					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Fetches the appropriate role out of assignedRoles and returns is. If no role matches, <code>null</code> is returned.
	 * 
	 * @param workpiece
	 *            The workpiece to process.
	 * @param source
	 *            The source agent of the workpiece.
	 * @return The role to process the workpiece or <code>null</code>.
	 */
	private Role getMatchingRole(Workpiece workpiece, String source) {
		for (Role role : assignedRoles) {
			Condition preCond = role.getPrecondition();
			if ((source == null && preCond.getTargetAgent() == null) || (source != null && source.equals(preCond.getTargetAgent()))) {
				if (taskMap.get(preCond.getTaskId()) != null && taskMap.get(preCond.getTaskId()).equals(workpiece.getTask())) {
					if (workpiece.getOperations().equals(preCond.getState()) && workpiece.isNextOperation(role.getCapability())) {
						return role;
					}
				}
			}
		}

		return null;
	}

	/**
	 * @param mainState
	 *            the mainState to set
	 */
	protected void setMainState(int mainState) {
		this.mainState = mainState;

		DatabaseLogger logger = DatabaseLogger.getInstance();
		logger.insertLog(id, AgentConstants.AGENT_TYPE_ROBOT, logger.getCurrentTime(), mainState, 0, assignedRoles.size(), buffer.size(), buffer.capacity());
	}

	/**
	 * Private class which sends periodically all the workpiece from the {@link Buffer} to the next transport agent.
	 * 
	 * @author thomas
	 */
	private class SendWorkpieceStep implements IComponentStep {

		public Object execute(IInternalAccess ia) {
			BufferElement element = buffer.peek();

			if (element != null) {
				sendWorkpiece(element, ia);
			} else {
				waitFor(200, this);
			}

			return null;
		}

		/**
		 * Sends the given {@link BufferElement} to the according transport agent.
		 * 
		 * @param element
		 *            the given {@link BufferElement}
		 * @param ia
		 *            the given {@link IInternalAccess}
		 */
		private void sendWorkpiece(final BufferElement element, IInternalAccess ia) {
			// Rolle aus Pufferelement holen
			final Role role = element.getRole();

			SServiceProvider.getServices(ia.getServiceProvider(), IProcessWorkpieceService.class).addResultListener(new IResultListener() {

				public void resultAvailable(Object result) {
					@SuppressWarnings("unchecked")
					Collection<IProcessWorkpieceService> services = (Collection<IProcessWorkpieceService>) result;
					for (IProcessWorkpieceService service : services) {
						final String target = role.getPostcondition().getTargetAgent();
						if (service.getId().equals(target)) {
							// Workpiece aus Pufferelement holen
							final Workpiece wp = element.getWorkpiece();

							service.process(wp, id).addResultListener(new IResultListener() {

								public void resultAvailable(Object result) {
									Boolean processResult = (Boolean) result;
									if (processResult) {
										String msg = id + " successfully delivered " + wp + " to " + target;
										getLogger().fine(msg);
										ServiceHelper.handleConsoleMsg(getServiceProvider(), new ConsoleMessage(ConsoleMessage.TYPE_PRODLINE, msg), getLogger());
										// Element aus Puffer entfernen
										buffer.dequeue();
									} else {
										String msg = id + " could not hand over workpiece " + wp + " to " + target;
										getLogger().fine(msg);
										ServiceHelper.handleConsoleMsg(getServiceProvider(), new ConsoleMessage(ConsoleMessage.TYPE_PRODLINE, msg), getLogger());
										// Element neu queuen
										buffer.enqueue(buffer.dequeue());
									}

									waitFor(200, SendWorkpieceStep.this);
								}

								public void exceptionOccurred(Exception exception) {
									getLogger().severe(exception.getMessage());
								}
							});
						}
					}
				}

				public void exceptionOccurred(Exception exception) {
					getLogger().severe(exception.getMessage());
				}
			});
		}
	}

	@Override
	protected void handleHelpRequest(HelpRequest request) {
		request.incrementHopCount();

		// receive a help request
		if (!id.equals(request.getAgentId())) {
			AgentData data = new AgentData();
			data.setBuffer(buffer);
			data.setCapabilities(capabilities.toArray(new String[capabilities.size()]));
			data.setRoles(assignedRoles);

			EvaluationResult result = strategy.evaluate(request, data);
			if (result.isReconfigure()) {
				List<Role> takeRoles = result.getTakeRoles();
				List<Role> giveAwayRoles = result.getGiveAwayRoles();

				ServiceHelper.handleConsoleMsg(getServiceProvider(), new ConsoleMessage(ConsoleMessage.TYPE_ADAPTIVITY, id + " takes " + takeRoles.size() + " roles " + takeRoles), getLogger());
				ServiceHelper.handleConsoleMsg(getServiceProvider(), new ConsoleMessage(ConsoleMessage.TYPE_ADAPTIVITY, id + " gives away " + giveAwayRoles.size() + " roles " + giveAwayRoles),
						getLogger());

				assignedRoles.removeAll(giveAwayRoles);
				assignedRoles.addAll(takeRoles);

				request.getDeficientRoles().removeAll(takeRoles);

				DatabaseLogger.getInstance().incrementMessageCountBy(1);
				DatabaseLogger.getInstance().incrementRoleChangeAction(takeRoles.size() + giveAwayRoles.size());

				answerHelpRequest(takeRoles, giveAwayRoles, request);
			}

			if (!request.getDeficientRoles().isEmpty()) {
				waitForTick(new SendMediumMessageStep(request));
			} else {
				DatabaseLogger.getInstance().storeRoleChangeDistance(request);
			}
		}
		// received its own help request
		else {
			// if the max escalation level is not reached, increment the escalation level and resend the request
			if (request.getEscalationLevel() < strategy.getMaximumEscalationLevel()) {
				request.incrementEscalationLevel();
				ServiceHelper.handleConsoleMsg(getServiceProvider(),
						new ConsoleMessage(ConsoleMessage.TYPE_ADAPTIVITY, id + " resends HelpRequest after increment excalation level to " + request.getEscalationLevel() + " for "
								+ request.getDeficientRoles().size() + " roles: " + request.getDeficientRoles()), getLogger());
				waitForTick(new SendMediumMessageStep(request));
			}
			// if the max escalation level is reached and their are still deficient roles, the reconfiguration failed
			else {
				ServiceHelper.handleConsoleMsg(getServiceProvider(), new ConsoleMessage(ConsoleMessage.TYPE_ADAPTIVITY, id + ": received its own request. Nothing will be done anymore"), getLogger());
			}
		}
	}

	private void answerHelpRequest(List<Role> takeRoles, List<Role> giveAwayRoles, HelpRequest request) {
		HelpReply reply = new HelpReply();
		reply.setDefectAgentId(request.getAgentId());
		reply.setReplaceAgentId(id);
		reply.setTakenRoles(takeRoles);
		reply.setVacantRoles(giveAwayRoles);
		addReceivers(reply, takeRoles);
		addReceivers(reply, giveAwayRoles);
		reply.addReceiver(request.getAgentId());

		DatabaseLogger.getInstance().incrementHopCount(1);

		waitForTick(new SendMediumMessageStep(reply));
	}

	private void addReceivers(HelpReply reply, List<Role> roles) {
		for (Role role : roles) {
			reply.addReceiver(role.getPrecondition().getTargetAgent());
			reply.addReceiver(role.getPostcondition().getTargetAgent());
		}
	}
}