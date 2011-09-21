package haw.mmlab.production_line.transport;

import haw.mmlab.production_line.common.AgentConstants;
import haw.mmlab.production_line.common.ConsoleMessage;
import haw.mmlab.production_line.common.LoggingHelper;
import haw.mmlab.production_line.common.ProcessWorkpieceAgent;
import haw.mmlab.production_line.common.Sequencer;
import haw.mmlab.production_line.configuration.Condition;
import haw.mmlab.production_line.configuration.Role;
import haw.mmlab.production_line.configuration.Task;
import haw.mmlab.production_line.configuration.Transport;
import haw.mmlab.production_line.configuration.Workpiece;
import haw.mmlab.production_line.service.IDatabaseService;
import haw.mmlab.production_line.service.IManagerService;
import haw.mmlab.production_line.service.IProcessWorkpieceService;
import haw.mmlab.production_line.service.ProcessWorkpieceService;
import haw.mmlab.production_line.state.MainState;
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
import java.util.HashMap;
import java.util.Map;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;

/**
 * The transport agent transports workpiece from one robot to another.
 * 
 * @author thomas
 */
@Description("Transport agent.")
@Arguments({ @Argument(clazz = Transport.class, name = "config"), @Argument(clazz = Map.class, name = "taskMap") })
@ProvidedServices(@ProvidedService(implementation = @Implementation(ProcessWorkpieceService.class), type = IProcessWorkpieceService.class))
@RequiredServices({ @RequiredService(name = "processWorkpieceServices", type = IProcessWorkpieceService.class, multiple = true, binding = @Binding(scope = RequiredServiceInfo.SCOPE_GLOBAL)),
		@RequiredService(name = "managerService", type = IManagerService.class), @RequiredService(name = "dbService", type = IDatabaseService.class) })
public class TransportAgent extends ProcessWorkpieceAgent {

	/**
	 * The number of workpieces the agent has produced or consumed for the given task (only if the agent is a producer or consumer)
	 */
	private Map<String, Integer> wpCount = null;

	/** The used reconfiguration strategety */
	@SuppressWarnings("unused")
	private IStrategy strategy = null;

	private IDatabaseService dbService = null;

	@SuppressWarnings("unchecked")
	@Override
	public IFuture<Void> agentCreated() {
		getRequiredService("dbService").addResultListener(new DefaultResultListener<IDatabaseService>() {

			@Override
			public void resultAvailable(IDatabaseService result) {
				dbService = result;
			}
		});

		Transport conf = (Transport) getArgument("config");

		id = conf.getAgentId();
		input = conf.getInput();
		output = conf.getOutput();
		capabilities = conf.getCapabilityStrings();
		assignedRoles = conf.getRoles();

		taskMap = (Map<String, Task>) getArgument("taskMap");
		wpCount = new HashMap<String, Integer>();

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
		for (Role role : assignedRoles) {
			// is producer role
			if (role.getPrecondition().getTargetAgent() == null) {
				getLogger().info(id + " starts producing workpieces");

				ProduceStep step = new ProduceStep(role);
				waitForTick(step);
			}
		}
	}

	/**
	 * Private class which periodically produces new workpieces.
	 * 
	 * @author thomas
	 */
	private class ProduceStep implements IComponentStep {

		private Role role = null;

		public ProduceStep(Role role) {
			this.role = role;
		}

		public Object execute(IInternalAccess ia) {
			final Task task = taskMap.get(role.getPrecondition().getTaskId());
			Integer max = task.getMaxWorkpieceCount();
			Integer count = wpCount.get(task.getId());

			if (count == null) {
				count = 0;
			}

			if (count < max) {
				Workpiece workpiece = new Workpiece(Sequencer.getNextNumber());
				workpiece.setTask(task);
				String msg = id + " has produced new " + workpiece;
				getLogger().fine(msg);
				handleConsoleMsg(new ConsoleMessage(ConsoleMessage.TYPE_PRODLINE, msg), getLogger());

				informWPProduced(ia, task);

				sendWorkpiece(ia, task, workpiece);
			}
			return null;
		}

		/**
		 * Informs the manager agent that a workpiece was produced in the given task.
		 * 
		 * @param ia
		 *            the {@link IInternalAccess}
		 * @param task
		 *            the given {@link Task}
		 */
		@SuppressWarnings("unchecked")
		private void informWPProduced(final IInternalAccess ia, final Task task) {
			getRequiredService("managerService").addResultListener(new DefaultResultListener<IManagerService>() {

				public void resultAvailable(IManagerService service) {
					service.informWPProduced(task.getId());
				}
			});
		}

		/**
		 * Sends the given produced {@link Workpiece} to the first robot in the given {@link Task}.
		 * 
		 * @param ia
		 *            the {@link IInternalAccess}
		 * @param task
		 *            the given {@link Task}
		 * @param workpiece
		 *            the given {@link Workpiece}
		 */
		@SuppressWarnings("unchecked")
		private void sendWorkpiece(final IInternalAccess ia, final Task task, final Workpiece workpiece) {
			getRequiredServices("processWorkpieceServices").addResultListener(new DefaultResultListener<Collection<IProcessWorkpieceService>>() {

				public void resultAvailable(Collection<IProcessWorkpieceService> services) {
					for (final IProcessWorkpieceService service : services) {
						final String target = role.getPostcondition().getTargetAgent();

						if (service.getId().equals(target)) {
							String msg = id + " tries to send workpieces to " + target;
							getLogger().fine(msg);
							handleConsoleMsg(new ConsoleMessage(ConsoleMessage.TYPE_PRODLINE, msg), getLogger());
							IFuture<Boolean> future = service.process(workpiece, id);
							future.addResultListener(new DefaultResultListener<Boolean>() {

								public void resultAvailable(Boolean result) {
									if (result) {
										String msg = id + " has successfully sended workpiece to " + target;
										getLogger().fine(msg);
										handleConsoleMsg(new ConsoleMessage(ConsoleMessage.TYPE_PRODLINE, msg), getLogger());
										if (wpCount.get(task.getId()) != null) {
											wpCount.put(task.getId(), wpCount.get(task.getId()) + 1);
										} else {
											wpCount.put(task.getId(), 1);
										}
										waitFor(role.getProcessingTime(), ProduceStep.this);
									} else {
										String msg = id + " has failed to send workpiece to " + target;
										getLogger().fine(msg);
										handleConsoleMsg(new ConsoleMessage(ConsoleMessage.TYPE_PRODLINE, msg), getLogger());
										waitForTick(new RetrySendStep(task, role, workpiece));
									}
								}
							});

							break;
						}
					}
				}
			});
		}
	}

	/**
	 * Private class which retries to send a {@link Workpiece} to according target agent specified by the given {@link Role}.
	 * 
	 * @author thomas
	 */
	private class RetrySendStep implements IComponentStep {

		private Task task = null;
		private Role role = null;
		private Workpiece workpiece = null;

		public RetrySendStep(Task task, Role role, Workpiece workpiece) {
			this.task = task;
			this.role = role;
			this.workpiece = workpiece;
		}

		@SuppressWarnings("unchecked")
		public Object execute(IInternalAccess ia) {
			final String target = role.getPostcondition().getTargetAgent();
			getRequiredServices("processWorkpieceServices").addResultListener(new DefaultResultListener<Collection<IProcessWorkpieceService>>() {

				@Override
				public void resultAvailable(Collection<IProcessWorkpieceService> services) {
					for (IProcessWorkpieceService service : services) {
						if (service.getId().equals(target)) {
							service.process(workpiece, id).addResultListener(new DefaultResultListener<Boolean>() {

								public void resultAvailable(Boolean result) {
									if (result) {
										String msg = id + " has successfully sended workpiece to " + target;
										getLogger().fine(msg);
										handleConsoleMsg(new ConsoleMessage(ConsoleMessage.TYPE_PRODLINE, msg), getLogger());
										if (wpCount.get(task.getId()) != null) {
											wpCount.put(task.getId(), wpCount.get(task.getId()) + 1);
										} else {
											wpCount.put(task.getId(), 1);
										}
										waitFor(role.getProcessingTime(), new ProduceStep(role));
									} else {
										String msg = id + " has failed to send workpiece to " + target;
										getLogger().fine(msg);
										handleConsoleMsg(new ConsoleMessage(ConsoleMessage.TYPE_PRODLINE, msg), getLogger());
										waitForTick(RetrySendStep.this);
									}
								}
							});
						}
					}
				}
			});

			return role;
		}
	}

	/**
	 * This method is called if the transport receives a new workpieces and tries to process it.
	 * 
	 * @param workpiece
	 *            the received workpiece
	 * @param senderId
	 *            the id of the agent from whom the workpiece was received
	 * @return <code>true</code> if the workpiece could be processed else <code>false</code>
	 */
	public boolean receiveWorkpiece(Workpiece workpiece, String senderId) {
		if (mainState == MainState.RUNNING_IDLE && this.workpiece == null) {
			Role role = getMatchingRole(workpiece, senderId);
			if (role != null) {
				setWorkpiece(workpiece);
				setMainState(MainState.RUNNING);
				workpiece.addOperation(role.getCapability());
				Integer processTime = role.getProcessingTime() == null ? 0 : role.getProcessingTime();
				if (role.getPostcondition().getTargetAgent() != null) {
					waitFor(processTime, new SendWorkpieceStep(role));
				} else {
					// consume
					consume(workpiece);
				}

				return true;
			}
		}

		return false;
	}

	/**
	 * This method is called when the transport acts as a consumer and consumes the given {@link Workpiece}.
	 * 
	 * @param workpiece
	 *            the given {@link Workpiece}
	 */
	@SuppressWarnings("unchecked")
	private void consume(final Workpiece workpiece) {
		setWorkpiece(null);
		setMainState(MainState.RUNNING_IDLE);

		getRequiredService("managerService").addResultListener(new DefaultResultListener<IManagerService>() {

			public void resultAvailable(IManagerService service) {
				service.informWPConsumed(workpiece.getTask().getId());

				String taskId = workpiece.getTask().getId();
				if (wpCount.containsKey(taskId)) {
					wpCount.put(taskId, wpCount.get(taskId) + 1);
				} else {
					wpCount.put(taskId, 1);
				}

				if (wpCount.get(taskId) >= workpiece.getTask().getMaxWorkpieceCount()) {
					service.informFinished(taskId);
				}
			}
		});
	}

	/**
	 * Private class which tries to send the current {@link Workpiece} ( {@link TransportAgent#workpiece}) to the target agent according to the applied {@link Role}.
	 * 
	 * @author thomas
	 */
	private class SendWorkpieceStep implements IComponentStep {

		private Role role = null;

		public SendWorkpieceStep(Role role) {
			this.role = role;
		}

		@SuppressWarnings("unchecked")
		public Object execute(IInternalAccess ia) {
			getRequiredServices("processWorkpieceServices").addResultListener(new DefaultResultListener<Collection<IProcessWorkpieceService>>() {

				public void resultAvailable(Collection<IProcessWorkpieceService> services) {
					for (IProcessWorkpieceService service : services) {
						final String target = role.getPostcondition().getTargetAgent();
						if (service.getId().equals(target)) {
							service.process(workpiece, id).addResultListener(new DefaultResultListener<Boolean>() {

								public void resultAvailable(Boolean result) {
									if (result) {
										String msg = id + " successfully delivered " + workpiece + " to " + target;
										getLogger().fine(msg);
										handleConsoleMsg(new ConsoleMessage(ConsoleMessage.TYPE_PRODLINE, msg), getLogger());
										setWorkpiece(null);
										setMainState(MainState.RUNNING_IDLE);
									} else {
										String msg = id + " could not hand over workpiece to agent " + workpiece + " to " + target;
										getLogger().fine(msg);
										handleConsoleMsg(new ConsoleMessage(ConsoleMessage.TYPE_PRODLINE, msg), getLogger());
										waitForTick(SendWorkpieceStep.this);
									}
								}
							});
						}
					}
				}
			});

			return null;
		}
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
		if (id.equals("Transport27")) {
			System.out.println("break here");
		}

		for (Role role : assignedRoles) {
			Condition preCond = role.getPrecondition();
			if (taskMap.get(preCond.getTaskId()) != null && taskMap.get(preCond.getTaskId()).equals(workpiece.getTask())) {
				if (workpiece.getOperations().equals(preCond.getState()) && workpiece.isNextOperation(role.getCapability())) {
					return role;
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
				dbService.insertLog(id, AgentConstants.AGENT_TYPE_TRANSPORT, result, mainState, 0, assignedRoles.size(), 0, 0);
			}
		});

	}
}
