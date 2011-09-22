package haw.mmlab.production_line.manager;

import haw.mmlab.production_line.common.ConsoleMessage;
import haw.mmlab.production_line.common.LoggingHelper;
import haw.mmlab.production_line.configuration.ProductionLineConfiguration;
import haw.mmlab.production_line.configuration.Robot;
import haw.mmlab.production_line.configuration.Task;
import haw.mmlab.production_line.configuration.Transport;
import haw.mmlab.production_line.service.IDatabaseService;
import haw.mmlab.production_line.service.IManagerService;
import haw.mmlab.production_line.service.ManagerService;
import jadex.bridge.CreationInfo;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.IFuture;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;

/**
 * The Manager agent. Starts and manages the simulation by starting and killing the other agents, maintaining the number of runs and creating statistics for the database.
 * 
 * @author thomas
 */
@Description("The Manager Agent who manages the whole application.")
@ProvidedServices(@ProvidedService(type = IManagerService.class, implementation = @Implementation(ManagerService.class)))
@RequiredServices({ @RequiredService(name = "cmsservice", type = IComponentManagementService.class, binding = @Binding(scope = RequiredServiceInfo.SCOPE_PLATFORM)),
		@RequiredService(name = "dbService", type = IDatabaseService.class) })
@Arguments(@Argument(clazz = String.class, name = "configuration_modell"))
public class ManagerAgent extends MicroAgent {

	/** The path to the configuration file */
	private String confFile = null;

	/** The path to the the dropout configuration file */
	private String dropOutConfFile = null;

	/** The name of the reconfiguration strategy */
	private String strategyName = null;

	/**
	 * A map with the number of produced workpieces in one task (key: taskId, value: No. produced workpieces
	 */
	private Map<String, Integer> producedWPs = null;

	/**
	 * A map with the number of consumed workpieces in one task (key: taskId, value: No. consumed workpieces
	 */
	private Map<String, Integer> consumedWPs = null;

	/** The production line configuration */
	private ProductionLineConfiguration plc = null;

	/** The current run */
	private Integer run = null;

	/** The number of runs which should be started */
	private Integer numberOfRuns = null;

	/** The timeout for an error condition */
	private Integer errorTimeout = null;

	/** A map with all the tasks and their ids */
	private Map<String, Task> tasks = null;

	/** A list of all the started agents */
	private List<IComponentIdentifier> startedAgents = null;

	/** A Map with the information which task is already finished */
	private Map<String, Boolean> finishedTasks = null;

	/** Production Line Writer */
	private BufferedWriter plWriter = null;

	/** Adaptivity Writer */
	private BufferedWriter adaptivityWriter = null;

	/** The log manager */
	private LogManager logManager = null;

	/** Flag indicating that a run is markes as finished */
	private boolean finished = false;

	/** Flag indicating whether at least one workpiece was consumed */
	private boolean firstWPConsumed = false;

	private IDatabaseService dbService = null;

	@Override
	public IFuture<Void> agentCreated() {
		// initialize the logger
		getLogger().setLevel(Level.ALL);
		ConsoleHandler handler = new ConsoleHandler();
		handler.setLevel(Level.parse(LoggingHelper.getLevel()));
		getLogger().addHandler(handler);
		getLogger().setUseParentHandlers(false);

		// initialize the variables
		try {
			producedWPs = new HashMap<String, Integer>();
			consumedWPs = new HashMap<String, Integer>();
			confFile = (String) getArgument("configuration_model");
			dropOutConfFile = (String) getArgument("dropout_configuration_model");
			strategyName = (String) getArgument("strategy");
			plc = (ProductionLineConfiguration) deco4mas.util.xml.XmlUtil.retrieveFromXML(ProductionLineConfiguration.class, confFile);
			numberOfRuns = plc.getRunCount();
			errorTimeout = plc.getErrorTimeout();
			run = 1;

			tasks = new HashMap<String, Task>();
			for (Task task : plc.getTasks()) {
				tasks.put(task.getId(), task);
			}

			startedAgents = new ArrayList<IComponentIdentifier>();

			finishedTasks = new HashMap<String, Boolean>();
			for (Task task : plc.getTasks()) {
				finishedTasks.put(task.getId(), Boolean.FALSE);
				consumedWPs.put(task.getId(), 0);
			}

			plWriter = new BufferedWriter(new FileWriter("pl_console.log"));
			adaptivityWriter = new BufferedWriter(new FileWriter("adaptivity_console.log"));

			logManager = new LogManager(plc);

		} catch (Exception e) {
			getLogger().severe(e.getMessage());
			killAgent();
		}

		return IFuture.DONE;
	}

	/**
	 * @return the finishedTasks
	 */
	public Map<String, Boolean> getFinishedTasks() {
		return finishedTasks;
	}

	@Override
	public void executeBody() {
		getLogger().info("Manager agent is writing the statistics to the database and creating the log files");
		logManager.beforeSimulation(confFile);
		getLogger().info("Manager agent is starting the agents");
		startAgents();

		WaitForErrorStep errorStep = new WaitForErrorStep(new HashMap<String, Integer>(consumedWPs));
		waitFor(errorTimeout, errorStep);
	}

	@Override
	public IFuture<Void> agentKilled() {
		closeWriters();
		getParent().killComponent();

		return IFuture.DONE;
	}

	/**
	 * @return the producedWPs
	 */
	public Map<String, Integer> getProducedWPs() {
		return producedWPs;
	}

	/**
	 * @return the consumedWPs
	 */
	public Map<String, Integer> getConsumedWPs() {
		return consumedWPs;
	}

	/**
	 * @return the firstWPConsumed
	 */
	public boolean isFirstWPConsumed() {
		return firstWPConsumed;
	}

	/**
	 * @param firstWPConsumed
	 *            the firstWPConsumed to set
	 */
	public void setFirstWPConsumed(boolean firstWPConsumed) {
		this.firstWPConsumed = firstWPConsumed;
	}

	/**
	 * Starts all the participating agents.
	 */
	@SuppressWarnings("unchecked")
	private void startAgents() {
		this.getRequiredService("cmsservice").addResultListener(new DefaultResultListener<IComponentManagementService>() {

			public void resultAvailable(IComponentManagementService cms) {
				IExternalAccess parent = ManagerAgent.this.getParent();

				// getLogger().info("Manager agent is starting database agent...");
				// cms.createComponent("DatabaseAgent", "haw/mmlab/production_line/logging/database/DatabaseAgent.class", null, null).addResultListener(new
				// DefaultResultListener<IComponentIdentifier>() {
				//
				// @Override
				// public void resultAvailable(IComponentIdentifier identifier) {
				// startedAgents.add(identifier);
				// getRequiredService("dbService").addResultListener(new DefaultResultListener<IDatabaseService>() {
				//
				// @Override
				// public void resultAvailable(IDatabaseService result) {
				// dbService = result;
				// }
				// });
				// }
				// });

				getRequiredService("dbService").addResultListener(new DefaultResultListener<IDatabaseService>() {

					@Override
					public void resultAvailable(IDatabaseService result) {
						dbService = result;
					}
				});

				// start the robot agents
				getLogger().info("Manager agent is starting robots...");
				for (Robot robot : plc.getRobots()) {
					Map<String, Object> args = new HashMap<String, Object>();
					args.put("config", robot);
					args.put("taskMap", tasks);
					args.put("strategy", strategyName);
					CreationInfo cr = new CreationInfo(args, parent.getComponentIdentifier());

					cms.createComponent(robot.getAgentId(), "haw/mmlab/production_line/robot/RobotAgent.class", cr, null).addResultListener(new DefaultResultListener<IComponentIdentifier>() {

						public void resultAvailable(IComponentIdentifier identifier) {
							startedAgents.add(identifier);
						}
					});
				}

				// start the transport agents
				getLogger().info("Manager agent is starting transports...");
				for (Transport transport : plc.getTransports()) {
					Map<String, Object> args = new HashMap<String, Object>();
					args.put("config", transport);
					args.put("taskMap", tasks);
					CreationInfo cr = new CreationInfo(args, parent.getComponentIdentifier());

					cms.createComponent(transport.getAgentId(), "haw/mmlab/production_line/transport/TransportAgent.class", cr, null).addResultListener(
							new DefaultResultListener<IComponentIdentifier>() {

								public void resultAvailable(IComponentIdentifier identifier) {
									startedAgents.add(identifier);
								}
							});
				}

				// start the timelord agent
				getLogger().info("Manager agent is starting the timelord agent...");
				cms.createComponent("Timelord", "haw/mmlab/production_line/timelord/TimelordAgent.class", null, null).addResultListener(new DefaultResultListener<IComponentIdentifier>() {

					public void resultAvailable(IComponentIdentifier identifier) {
						startedAgents.add(identifier);
					}
				});
			}
		});
	}

	public boolean checkFinish() {
		for (Boolean finish : finishedTasks.values()) {
			if (!finish) {
				return false;
			}
		}

		finishRun();

		return true;
	}

	/**
	 * Finishes the current run and starts the next runs if specified.
	 */
	private void finishRun() {
		if (!finished) {
			finished = true;
			getLogger().info("Manager agent is finishing run " + run);
			killAgents();

			logManager.afterSimulation(producedWPs, consumedWPs);

			if (run < numberOfRuns) {
				getLogger().info("Manager agent is starting next run " + ++run);

				producedWPs = new HashMap<String, Integer>();
				consumedWPs = new HashMap<String, Integer>();
				startedAgents = new ArrayList<IComponentIdentifier>();
				finishedTasks = new HashMap<String, Boolean>();
				for (Task task : plc.getTasks()) {
					finishedTasks.put(task.getId(), Boolean.FALSE);
					consumedWPs.put(task.getId(), 0);
				}

				try {
					plWriter = new BufferedWriter(new FileWriter("pl_console.log"));
					adaptivityWriter = new BufferedWriter(new FileWriter("adaptivity_console.log"));
				} catch (IOException e) {
					getLogger().severe(e.getMessage());
				}

				logManager = new LogManager(plc);
				finished = false;

				// wait for 5s so the agents could be killed properly before
				// restarting new ones.
				waitFor(5000, new IComponentStep() {

					public IFuture<Void> execute(IInternalAccess ia) {
						executeBody();
						return IFuture.DONE;
					}
				});
			} else {
				killAgent();
			}
		}
	}

	/**
	 * Kills all the started agents.
	 */
	@SuppressWarnings("unchecked")
	private void killAgents() {
		this.getRequiredService("cmsservice").addResultListener(new DefaultResultListener<IComponentManagementService>() {

			public void resultAvailable(IComponentManagementService cms) {
				getLogger().info("Manager agent is killing agents...");
				for (IComponentIdentifier identifier : startedAgents) {
					cms.destroyComponent(identifier);
				}
				getLogger().info("Manager agent is done killing agents!");
			}
		});
	}

	/**
	 * Private class which periodically checks if an error occurred in the current run and ends him if necessary.
	 * 
	 * @author thomas
	 */
	private class WaitForErrorStep implements IComponentStep {

		private Map<String, Integer> oldConsumedWPs = null;

		public WaitForErrorStep(Map<String, Integer> oldConsumedWPs) {
			this.oldConsumedWPs = oldConsumedWPs;
		}

		public Object execute(IInternalAccess ia) {
			boolean consumed = false;

			for (String taskId : oldConsumedWPs.keySet()) {
				// check if a workpiece was consumed in at least one of the
				// tasks
				if (consumedWPs.get(taskId) > oldConsumedWPs.get(taskId)) {
					consumed = true;
					break;
				}
			}

			if (consumed) {
				WaitForErrorStep step = new WaitForErrorStep(new HashMap<String, Integer>(consumedWPs));
				waitFor(errorTimeout, step);
			} else {
				getLogger().warning("Manager agent has detected a error condition timeout");
				dbService.setErrorRun();
				finishRun();
			}

			return consumed;
		}
	}

	/**
	 * Prints a {@link ConsoleMessage} to the according output file.
	 * 
	 * @param message
	 *            the given {@link ConsoleMessage}
	 */
	public void printConsoleMsg(ConsoleMessage message) {
		if (message.isProdLineMessage()) {
			writeToPLFile(message.getOutMsg() + "\n");
		} else if (message.isAdaptivityMessage()) {
			writeToAdaptivityFile(message.getOutMsg() + "\n");
		}
	}

	/**
	 * Writes the given message to the production line console output.
	 * 
	 * @param message
	 *            the given message
	 */
	private void writeToPLFile(String message) {
		if (plWriter != null) {
			try {
				plWriter.write(message);
				plWriter.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Writes the given message to the adaptivity console output.
	 * 
	 * @param message
	 *            the given message
	 */
	private void writeToAdaptivityFile(String message) {
		if (adaptivityWriter != null) {
			try {
				adaptivityWriter.write(message);
				adaptivityWriter.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Closes the writers.
	 */
	private void closeWriters() {
		try {
			if (plWriter != null)
				plWriter.close();
			if (adaptivityWriter != null)
				adaptivityWriter.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Starts the Dropout Agent
	 */
	@SuppressWarnings("unchecked")
	public void startDropout() {
		this.getRequiredService("cmsservice").addResultListener(new DefaultResultListener<IComponentManagementService>() {

			public void resultAvailable(IComponentManagementService cms) {
				// start the dropout agent
				getLogger().info("Manager agent is starting the dropout agent...");
				Map<String, Object> args = new HashMap<String, Object>();
				args.put("configuration_model", dropOutConfFile);
				CreationInfo ci = new CreationInfo(args);

				cms.createComponent("Dropout", "haw/mmlab/production_line/dropout/DropoutAgent.class", ci, null).addResultListener(new DefaultResultListener<IComponentIdentifier>() {

					public void resultAvailable(IComponentIdentifier identifier) {
						startedAgents.add(identifier);
					}
				});
			}
		});
	}
}