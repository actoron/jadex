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
import haw.mmlab.production_line.service.IDatabaseService;
import haw.mmlab.production_line.service.IManagerService;
import haw.mmlab.production_line.service.IProcessWorkpieceService;
import haw.mmlab.production_line.service.ProcessWorkpieceService;
import haw.mmlab.production_line.state.MainState;
import haw.mmlab.production_line.strategies.AgentData;
import haw.mmlab.production_line.strategies.EvaluationResult;
import haw.mmlab.production_line.strategies.IStrategy;
import haw.mmlab.production_line.strategies.StrategyFactory;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

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
@Description("Robot agent.")
@Arguments({ @Argument(clazz = Robot.class, name = "config"), @Argument(clazz = Map.class, name = "taskMap"), @Argument(clazz = IStrategy.class, name = "strategy") })
@ProvidedServices(@ProvidedService(implementation = @Implementation(ProcessWorkpieceService.class), type = IProcessWorkpieceService.class))
@RequiredServices({ @RequiredService(name = "processWorkpieceServices", type = IProcessWorkpieceService.class, multiple = true, binding = @Binding(scope = RequiredServiceInfo.SCOPE_GLOBAL)),
		@RequiredService(name = "managerService", type = IManagerService.class), @RequiredService(name = "dbService", type = IDatabaseService.class) })
public class RobotAgent extends ProcessWorkpieceAgent {

	/** The output {@link Buffer} */
	private Buffer buffer = null;

	/** The used reconfiguration strategety */
	private IStrategy strategy = null;

	private IDatabaseService dbService = null;

	private IManagerService managerService = null;

	@SuppressWarnings("unchecked")
	@Override
	public IFuture<Void> agentCreated() {
		getRequiredService("dbService").addResultListener(new DefaultResultListener<IDatabaseService>() {

			@Override
			public void resultAvailable(IDatabaseService result) {
				dbService = result;
			}
		});

		getRequiredService("managerService").addResultListener(new DefaultResultListener<IManagerService>() {

			@Override
			public void resultAvailable(IManagerService result) {
				managerService = result;
			}
		});

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

		return IFuture.DONE;
	}

	@Override
	public void executeBody() {
		waitForTick(new SendWorkpieceStep());
	}

	@SuppressWarnings("unchecked")
	@Override
	public IFuture<Void> agentKilled() {
		return super.agentKilled();
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

					IComponentStep<Void> sendStep = new IComponentStep<Void>() {

						public IFuture<Void> execute(IInternalAccess ia) {
							setWorkpiece(null);
							setMainState(MainState.RUNNING_IDLE);
							buffer.enqueue(new BufferElement(workpiece, role));

							return IFuture.DONE;
						}
					};

					// wait for simulating the processing time
					Integer processTime = role.getProcessingTime() == null ? 0 : role.getProcessingTime();
					if (processTime.equals(0)) {
						// optimized for event driven simulation if processing time is 0
						waitForTick(sendStep);
					} else {
						waitFor(processTime, sendStep);
					}

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
	protected void setMainState(final int mainState) {
		this.mainState = mainState;

		dbService.getCurrentTime().addResultListener(new DefaultResultListener<Integer>() {

			@Override
			public void resultAvailable(Integer result) {
				dbService.insertLog(id, AgentConstants.AGENT_TYPE_ROBOT, result, mainState, 0, assignedRoles.size(), buffer.size(), buffer.capacity());
			}
		});
	}

	/**
	 * Private class which sends periodically all the workpiece from the {@link Buffer} to the next transport agent.
	 * 
	 * @author thomas
	 */
	private class SendWorkpieceStep implements IComponentStep<Void> {

		public IFuture<Void> execute(IInternalAccess ia) {
			BufferElement element = buffer.peek();

			if (element != null) {
				sendWorkpiece(element, ia);
			} else {
				waitForTick(this);
			}

			return IFuture.DONE;
		}

		/**
		 * Sends the given {@link BufferElement} to the according transport agent.
		 * 
		 * @param element
		 *            the given {@link BufferElement}
		 * @param ia
		 *            the given {@link IInternalAccess}
		 */
		@SuppressWarnings("unchecked")
		private void sendWorkpiece(final BufferElement element, IInternalAccess ia) {
			// Rolle aus Pufferelement holen
			final Role role = element.getRole();

			getRequiredServices("processWorkpieceServices").addResultListener(new DefaultResultListener<Collection<IProcessWorkpieceService>>() {

				public void resultAvailable(Collection<IProcessWorkpieceService> services) {
					for (IProcessWorkpieceService service : services) {
						final String target = role.getPostcondition().getTargetAgent();
						if (service.getId().equals(target)) {
							// Workpiece aus Pufferelement holen
							final Workpiece wp = element.getWorkpiece();

							service.process(wp, id).addResultListener(new DefaultResultListener<Boolean>() {

								public void resultAvailable(Boolean result) {
									if (result) {
										String msg = id + " successfully delivered " + wp + " to " + target;
										// getLogger().fine(msg);
										handleConsoleMsg(new ConsoleMessage(ConsoleMessage.TYPE_PRODLINE, msg), getLogger());
										// Element aus Puffer entfernen
										buffer.dequeue();
									} else {
										String msg = id + " could not hand over workpiece " + wp + " to " + target;
										// getLogger().fine(msg);
										handleConsoleMsg(new ConsoleMessage(ConsoleMessage.TYPE_PRODLINE, msg), getLogger());
										// Element neu queuen
										buffer.enqueue(buffer.dequeue());
									}

									waitForTick(SendWorkpieceStep.this);
								}
							});
						}
					}
				}
			});
		}
	}

	@SuppressWarnings("unchecked")
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

				handleConsoleMsg(new ConsoleMessage(ConsoleMessage.TYPE_ADAPTIVITY, id + " takes " + takeRoles.size() + " roles " + takeRoles), getLogger());
				handleConsoleMsg(new ConsoleMessage(ConsoleMessage.TYPE_ADAPTIVITY, id + " gives away " + giveAwayRoles.size() + " roles " + giveAwayRoles), getLogger());

				assignedRoles.removeAll(giveAwayRoles);
				assignedRoles.addAll(takeRoles);

				request.getDeficientRoles().removeAll(takeRoles);

				dbService.incrementMessageCountBy(1);
				dbService.incrementRoleChangeAction(takeRoles.size() + giveAwayRoles.size());

				answerHelpRequest(takeRoles, giveAwayRoles, request);
			}

			if (!request.getDeficientRoles().isEmpty()) {
				waitForTick(new SendMediumMessageStep(request));
			} else {
				dbService.storeRoleChangeDistance(request);
			}
		}
		// received its own help request
		else {
			// if the max escalation level is not reached, increment the escalation level and resend the request
			if (request.getEscalationLevel() < strategy.getMaximumEscalationLevel()) {
				request.incrementEscalationLevel();
				handleConsoleMsg(new ConsoleMessage(ConsoleMessage.TYPE_ADAPTIVITY, id + " resends HelpRequest after increment excalation level to " + request.getEscalationLevel() + " for "
						+ request.getDeficientRoles().size() + " roles: " + request.getDeficientRoles()), getLogger());
				waitForTick(new SendMediumMessageStep(request));
			}
			// if the max escalation level is reached and their are still deficient roles, the reconfiguration failed
			else {
				handleConsoleMsg(new ConsoleMessage(ConsoleMessage.TYPE_ADAPTIVITY, id + ": received its own request. Nothing will be done anymore"), getLogger());
				managerService.informReconfError();
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void answerHelpRequest(List<Role> takeRoles, List<Role> giveAwayRoles, HelpRequest request) {
		HelpReply reply = new HelpReply();
		reply.setDefectAgentId(request.getAgentId());
		reply.setReplaceAgentId(id);
		reply.setTakenRoles(takeRoles);
		reply.setVacantRoles(giveAwayRoles);
		addReceivers(reply, takeRoles);
		addReceivers(reply, giveAwayRoles);
		reply.addReceiver(request.getAgentId());

		dbService.incrementHopCount(1);

		waitForTick(new SendMediumMessageStep(reply));
	}

	private void addReceivers(HelpReply reply, List<Role> roles) {
		for (Role role : roles) {
			reply.addReceiver(role.getPrecondition().getTargetAgent());
			reply.addReceiver(role.getPostcondition().getTargetAgent());
		}
	}
}