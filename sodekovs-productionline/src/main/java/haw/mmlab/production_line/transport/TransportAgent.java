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
import haw.mmlab.production_line.service.IManagerService;
import haw.mmlab.production_line.service.IProcessWorkpieceService;
import haw.mmlab.production_line.service.ProcessWorkpieceService;
import haw.mmlab.production_line.state.MainState;
import haw.mmlab.production_line.strategies.IStrategy;
import haw.mmlab.production_line.strategies.StrategyFactory;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;

/**
 * The transport agent transports workpiece from one robot to another.
 * 
 * @author thomas
 */
@SuppressWarnings("unchecked")
@Description("Transport agent.")
@Arguments({ @Argument(clazz = Transport.class, name = "config"), @Argument(clazz = Map.class, name = "taskMap"), @Argument(clazz = Integer.class, name = "reconfDelay") })
@RequiredServices({ @RequiredService(name = "managerService", type = IManagerService.class) })
public class TransportAgent extends ProcessWorkpieceAgent {

	/**
	 * The number of workpieces the agent has produced or consumed for the given task (only if the agent is a producer or consumer)
	 */
	private Map<String, Integer> wpCount = null;

	/** The used reconfiguration strategety */
	@SuppressWarnings("unused")
	private IStrategy strategy = null;

	private IManagerService managerService = null;

	@Override
	public IFuture<Void> agentCreated() {
		reconfDelay = (Integer) getArgument("reconfDelay");

		// databaseLogger = new DatabaseLogger();

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

		addService(id, IProcessWorkpieceService.class, new ProcessWorkpieceService(this, AgentConstants.AGENT_TYPE_TRANSPORT));

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
	private class ProduceStep implements IComponentStep<Void> {

		private Role role = null;

		public ProduceStep(Role role) {
			this.role = role;
		}

		public IFuture<Void> execute(IInternalAccess ia) {
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
				// getLogger().fine(msg);
				handleConsoleMsg(new ConsoleMessage(ConsoleMessage.TYPE_PRODLINE, msg), getLogger());

				informWPProduced(ia, task);

				sendWorkpiece(ia, task, workpiece);
			}
			return IFuture.DONE;
		}

		/**
		 * Informs the manager agent that a workpiece was produced in the given task.
		 * 
		 * @param ia
		 *            the {@link IInternalAccess}
		 * @param task
		 *            the given {@link Task}
		 */
		private void informWPProduced(final IInternalAccess ia, final Task task) {
			if (managerService == null) {
				getRequiredService("managerService").addResultListener(new DefaultResultListener<IManagerService>() {

					public void resultAvailable(IManagerService service) {
						managerService = service;
						managerService.informWPProduced(task.getId());
					}
				});
			} else {
				managerService.informWPProduced(task.getId());
			}
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
		private void sendWorkpiece(final IInternalAccess ia, final Task task, final Workpiece workpiece) {
			getProcessWPService(role).addResultListener(new DefaultResultListener<IProcessWorkpieceService>() {

				@Override
				public void resultAvailable(IProcessWorkpieceService service) {
					if (service != null) {
						final String target = role.getPostcondition().getTargetAgent();
						String msg = id + " tries to send workpiece to " + target;
						// getLogger().fine(msg);
						handleConsoleMsg(new ConsoleMessage(ConsoleMessage.TYPE_PRODLINE, msg), getLogger());
						IFuture<Boolean> future = service.process(workpiece, id);
						future.addResultListener(new DefaultResultListener<Boolean>() {

							public void resultAvailable(Boolean result) {
								if (result) {
									String msg = id + " has successfully sended workpiece to " + target;
									// getLogger().fine(msg);
									handleConsoleMsg(new ConsoleMessage(ConsoleMessage.TYPE_PRODLINE, msg), getLogger());
									if (wpCount.get(task.getId()) != null) {
										wpCount.put(task.getId(), wpCount.get(task.getId()) + 1);
									} else {
										wpCount.put(task.getId(), 1);
									}

									if (role.getProcessingTime().equals(0)) {
										// optimized for a event driven simulation if processing time is 0
										waitForTick(ProduceStep.this);
									} else {
										waitFor(role.getProcessingTime(), ProduceStep.this);
									}
								} else {
									String msg = id + " has failed to send workpiece to " + target;
									// getLogger().fine(msg);
									handleConsoleMsg(new ConsoleMessage(ConsoleMessage.TYPE_PRODLINE, msg), getLogger());
									waitForTick(new RetrySendStep(task, role, workpiece));
								}
							}
						});
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
	private class RetrySendStep implements IComponentStep<Void> {

		private Task task = null;
		private Role role = null;
		private Workpiece workpiece = null;

		public RetrySendStep(Task task, Role role, Workpiece workpiece) {
			this.task = task;
			this.role = role;
			this.workpiece = workpiece;
		}

		public IFuture<Void> execute(IInternalAccess ia) {
			final String target = role.getPostcondition().getTargetAgent();
			getProcessWPService(role).addResultListener(new DefaultResultListener<IProcessWorkpieceService>() {

				@Override
				public void resultAvailable(IProcessWorkpieceService service) {
					if (service != null) {
						service.process(workpiece, id).addResultListener(new DefaultResultListener<Boolean>() {

							public void resultAvailable(Boolean result) {
								if (result) {
									String msg = id + " has successfully sended workpiece to " + target;
									// getLogger().fine(msg);
									handleConsoleMsg(new ConsoleMessage(ConsoleMessage.TYPE_PRODLINE, msg), getLogger());
									if (wpCount.get(task.getId()) != null) {
										wpCount.put(task.getId(), wpCount.get(task.getId()) + 1);
									} else {
										wpCount.put(task.getId(), 1);
									}

									if (role.getProcessingTime().equals(0)) {
										// optimized for a event driven simulation if the processing time is 0
										waitForTick(new ProduceStep(role));
									} else {
										waitFor(role.getProcessingTime(), new ProduceStep(role));
									}
								} else {
									String msg = id + " has failed to send workpiece to " + target;
									// getLogger().fine(msg);
									handleConsoleMsg(new ConsoleMessage(ConsoleMessage.TYPE_PRODLINE, msg), getLogger());
									waitForTick(RetrySendStep.this);
								}
							}
						});
					}
				}
			});

			return IFuture.DONE;
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
		if (workpiece == null) {
			throw new RuntimeException("Workpiece should not be null");
		}

		if (mainState == MainState.RUNNING_IDLE && this.workpiece == null) {
			Role role = getMatchingRole(workpiece, senderId);
			if (role != null) {
				String taskId = workpiece.getTask().getId();
				if (wpCount.containsKey(taskId)) {
					wpCount.put(taskId, wpCount.get(taskId) + 1);
				} else {
					wpCount.put(taskId, 1);
				}
				setWorkpiece(workpiece);
				setStates(MainState.RUNNING, deficientState);
				workpiece.addOperation(role.getCapability());
				Integer processTime = role.getProcessingTime() == null ? 0 : role.getProcessingTime();
				if (role.getPostcondition().getTargetAgent() != null) {
					if (processTime.equals(0)) {
						// optimized for event driven simulation if processing time is 0
						waitForTick(new SendWorkpieceStep(role));
					} else {
						waitFor(processTime, new SendWorkpieceStep(role));
					}
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
	private void consume(final Workpiece workpiece) {
		setWorkpiece(null);
		setStates(MainState.RUNNING_IDLE, deficientState);

		final String taskId = workpiece.getTask().getId();
		if (wpCount.containsKey(taskId)) {
			wpCount.put(taskId, wpCount.get(taskId) + 1);
		} else {
			wpCount.put(taskId, 1);
		}

		if (managerService == null) {
			getRequiredService("managerService").addResultListener(new DefaultResultListener<IManagerService>() {

				@Override
				public void resultAvailable(IManagerService result) {
					managerService = result;
					managerService.informWPConsumed(workpiece.getTask().getId());

					if (wpCount.get(taskId) >= workpiece.getTask().getMaxWorkpieceCount()) {
						managerService.informFinished(taskId);
					}
				}
			});
		} else {
			managerService.informWPConsumed(workpiece.getTask().getId());

			if (wpCount.get(taskId) >= workpiece.getTask().getMaxWorkpieceCount()) {
				managerService.informFinished(taskId);
			}
		}
	}

	/**
	 * Private class which tries to send the current {@link Workpiece} ( {@link TransportAgent#workpiece}) to the target agent according to the applied {@link Role}.
	 * 
	 * @author thomas
	 */
	private class SendWorkpieceStep implements IComponentStep<Void> {

		private Role role = null;

		public SendWorkpieceStep(Role role) {
			this.role = role;
		}

		public IFuture<Void> execute(IInternalAccess ia) {
			getProcessWPService(role).addResultListener(new DefaultResultListener<IProcessWorkpieceService>() {

				@Override
				public void resultAvailable(IProcessWorkpieceService service) {
					if (service != null) {
						final String target = role.getPostcondition().getTargetAgent();
						service.process(workpiece, id).addResultListener(new DefaultResultListener<Boolean>() {

							public void resultAvailable(Boolean result) {
								if (result) {
									String msg = id + " successfully delivered " + workpiece + " to " + target;
									getLogger().fine(msg);
									handleConsoleMsg(new ConsoleMessage(ConsoleMessage.TYPE_PRODLINE, msg), getLogger());
									setWorkpiece(null);
									setStates(MainState.RUNNING_IDLE, deficientState);
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
			});

			return IFuture.DONE;
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

	@Override
	protected void setStates(int mainState, int deficientState) {
		this.mainState = mainState;
		this.deficientState = deficientState;

		// int time = databaseLogger.getCurrentTime();
		// databaseLogger.insertLog(id, AgentConstants.AGENT_TYPE_TRANSPORT, time, mainState, deficientState, assignedRoles.size(), 0, 0);
	}
}