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
import haw.mmlab.production_line.logging.database.DatabaseLogger;
import haw.mmlab.production_line.service.IManagerService;
import haw.mmlab.production_line.service.IProcessWorkpieceService;
import haw.mmlab.production_line.service.ProcessWorkpieceService;
import haw.mmlab.production_line.service.ServiceHelper;
import haw.mmlab.production_line.state.MainState;
import haw.mmlab.production_line.strategies.IStrategy;
import haw.mmlab.production_line.strategies.StrategyFactory;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.modelinfo.Argument;
import jadex.bridge.modelinfo.IArgument;
import jadex.bridge.service.ProvidedServiceInfo;
import jadex.bridge.service.SServiceProvider;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.micro.MicroAgentMetaInfo;

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
public class TransportAgent extends ProcessWorkpieceAgent {

	/**
	 * The number of workpieces the agent has produced or consumed for the given task (only if the agent is a producer or consumer)
	 */
	private Map<String, Integer> wpCount = null;

	/** The used reconfiguration strategety */
	@SuppressWarnings("unused")
	private IStrategy strategy = null;

	@SuppressWarnings("unchecked")
	@Override
	public IFuture agentCreated() {
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

		addService("ProcessWorkpieceService", IProcessWorkpieceService.class, new ProcessWorkpieceService(this, id, AgentConstants.AGENT_TYPE_TRANSPORT, getLogger()));
		
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
	 * Returns the {@link MicroAgentMetaInfo}.
	 * 
	 * @return the {@link MicroAgentMetaInfo}
	 */
	public static MicroAgentMetaInfo getMetaInfo() {
		MicroAgentMetaInfo meta = new MicroAgentMetaInfo();
		meta.setDescription("Transport agent");
		meta.setArguments(new IArgument[] { new Argument("config", "The transport's configuration", "Robot"), new Argument("taskMap", "A map with all the tasks", "Map") });
		meta.setProvidedServices(new ProvidedServiceInfo[] { new ProvidedServiceInfo(IProcessWorkpieceService.class) });

		return meta;
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
				ServiceHelper.handleConsoleMsg(getServiceProvider(), new ConsoleMessage(ConsoleMessage.TYPE_PRODLINE, msg), getLogger());

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
		private void informWPProduced(final IInternalAccess ia, final Task task) {
			SServiceProvider.getService(getServiceProvider(), IManagerService.class).addResultListener(new IResultListener() {

				public void resultAvailable(Object result) {
					IManagerService service = (IManagerService) result;
					service.informWPProduced(task.getId());
				}

				public void exceptionOccurred(Exception exception) {
					getLogger().severe(exception.getMessage());
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
		private void sendWorkpiece(final IInternalAccess ia, final Task task, final Workpiece workpiece) {
			SServiceProvider.getServices(getServiceProvider(), IProcessWorkpieceService.class).addResultListener(new IResultListener() {

				public void resultAvailable(Object result) {
					@SuppressWarnings("unchecked")
					Collection<IProcessWorkpieceService> services = (Collection<IProcessWorkpieceService>) result;

					for (final IProcessWorkpieceService service : services) {
						final String target = role.getPostcondition().getTargetAgent();

						if (service.getId().equals(target)) {
							String msg = id + " tries to send workpieces to " + target;
							getLogger().fine(msg);
							ServiceHelper.handleConsoleMsg(getServiceProvider(), new ConsoleMessage(ConsoleMessage.TYPE_PRODLINE, msg), getLogger());
							IFuture future = service.process(workpiece, id);
							future.addResultListener(new IResultListener() {

								public void resultAvailable(Object result) {
									if ((Boolean) result) {
										String msg = id + " has successfully sended workpiece to " + target;
										getLogger().fine(msg);
										ServiceHelper.handleConsoleMsg(getServiceProvider(), new ConsoleMessage(ConsoleMessage.TYPE_PRODLINE, msg), getLogger());
										if (wpCount.get(task.getId()) != null) {
											wpCount.put(task.getId(), wpCount.get(task.getId()) + 1);
										} else {
											wpCount.put(task.getId(), 1);
										}
										waitFor(role.getProcessingTime(), ProduceStep.this);
									} else {
										String msg = id + " has failed to send workpiece to " + target;
										getLogger().fine(msg);
										ServiceHelper.handleConsoleMsg(getServiceProvider(), new ConsoleMessage(ConsoleMessage.TYPE_PRODLINE, msg), getLogger());
										waitForTick(new RetrySendStep(service, task, role, workpiece));
									}
								}

								public void exceptionOccurred(Exception exception) {
									getLogger().severe(exception.getMessage());
								}
							});

							break;
						}
					}
				}

				public void exceptionOccurred(Exception exception) {
					getLogger().severe(exception.getMessage());
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

		private IProcessWorkpieceService service = null;
		private Task task = null;
		private Role role = null;
		private Workpiece workpiece = null;

		public RetrySendStep(IProcessWorkpieceService service, Task task, Role role, Workpiece workpiece) {
			this.service = service;
			this.task = task;
			this.role = role;
			this.workpiece = workpiece;
		}

		public Object execute(IInternalAccess ia) {
			final String target = role.getPostcondition().getTargetAgent();
			service.process(workpiece, id).addResultListener(new IResultListener() {

				public void resultAvailable(Object result) {
					if ((Boolean) result) {
						String msg = id + " has successfully sended workpiece to " + target;
						getLogger().fine(msg);
						ServiceHelper.handleConsoleMsg(getServiceProvider(), new ConsoleMessage(ConsoleMessage.TYPE_PRODLINE, msg), getLogger());
						if (wpCount.get(task.getId()) != null) {
							wpCount.put(task.getId(), wpCount.get(task.getId()) + 1);
						} else {
							wpCount.put(task.getId(), 1);
						}
						waitFor(role.getProcessingTime(), new ProduceStep(role));
					} else {
						String msg = id + " has failed to send workpiece to " + target;
						getLogger().fine(msg);
						ServiceHelper.handleConsoleMsg(getServiceProvider(), new ConsoleMessage(ConsoleMessage.TYPE_PRODLINE, msg), getLogger());
						waitForTick(RetrySendStep.this);
					}
				}

				public void exceptionOccurred(Exception exception) {
					getLogger().severe(exception.getMessage());
					waitForTick(new ProduceStep(role));
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
	private void consume(final Workpiece workpiece) {
		setWorkpiece(null);
		setMainState(MainState.RUNNING_IDLE);

		SServiceProvider.getService(getServiceProvider(), IManagerService.class).addResultListener(new IResultListener() {

			public void resultAvailable(Object result) {
				IManagerService service = (IManagerService) result;
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

			public void exceptionOccurred(Exception exception) {
				getLogger().severe(exception.getMessage());
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

		public Object execute(IInternalAccess ia) {
			SServiceProvider.getServices(getServiceProvider(), IProcessWorkpieceService.class).addResultListener(new IResultListener() {

				public void resultAvailable(Object result) {
					@SuppressWarnings("unchecked")
					Collection<IProcessWorkpieceService> services = (Collection<IProcessWorkpieceService>) result;
					for (IProcessWorkpieceService service : services) {
						final String target = role.getPostcondition().getTargetAgent();
						if (service.getId().equals(target)) {
							service.process(workpiece, id).addResultListener(new IResultListener() {

								public void resultAvailable(Object result) {
									Boolean processResult = (Boolean) result;
									if (processResult) {
										String msg = id + " successfully delivered " + workpiece + " to " + target;
										getLogger().fine(msg);
										ServiceHelper.handleConsoleMsg(getServiceProvider(), new ConsoleMessage(ConsoleMessage.TYPE_PRODLINE, msg), getLogger());
										setWorkpiece(null);
										setMainState(MainState.RUNNING_IDLE);
									} else {
										String msg = id + " could not hand over workpiece to agent " + workpiece + " to " + target;
										getLogger().fine(msg);
										ServiceHelper.handleConsoleMsg(getServiceProvider(), new ConsoleMessage(ConsoleMessage.TYPE_PRODLINE, msg), getLogger());
										waitForTick(SendWorkpieceStep.this);
									}
								}

								public void exceptionOccurred(Exception exception) {
									getLogger().severe(exception.getMessage());
									waitForTick(SendWorkpieceStep.this);
								}
							});
						}
					}
				}

				public void exceptionOccurred(Exception exception) {
					getLogger().severe(exception.getMessage());
					waitForTick(SendWorkpieceStep.this);
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
	protected void setMainState(int mainState) {
		this.mainState = mainState;

		DatabaseLogger logger = DatabaseLogger.getInstance();
		logger.insertLog(id, AgentConstants.AGENT_TYPE_TRANSPORT, logger.getCurrentTime(), mainState, 0, assignedRoles.size(), 0, 0);
	}
}