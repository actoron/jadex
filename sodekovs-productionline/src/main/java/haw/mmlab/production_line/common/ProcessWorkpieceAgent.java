package haw.mmlab.production_line.common;

import haw.mmlab.production_line.configuration.Role;
import haw.mmlab.production_line.configuration.Task;
import haw.mmlab.production_line.configuration.Workpiece;
import haw.mmlab.production_line.domain.HelpReply;
import haw.mmlab.production_line.domain.HelpRequest;
import haw.mmlab.production_line.domain.MediumMessage;
import haw.mmlab.production_line.dropout.DropoutAgent;
import haw.mmlab.production_line.dropout.config.Action;
import haw.mmlab.production_line.robot.RobotAgent;
import haw.mmlab.production_line.service.IManagerService;
import haw.mmlab.production_line.service.IProcessWorkpieceService;
import haw.mmlab.production_line.service.ManagerService;
import haw.mmlab.production_line.state.DeficientState;
import haw.mmlab.production_line.state.MainState;
import haw.mmlab.production_line.transport.TransportAgent;
import jadex.bridge.ComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.ServiceIdentifier;
import jadex.bridge.service.search.SServiceProvider;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.MicroAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.logging.Logger;

import deco4mas.coordinate.annotation.CoordinationParameter;
import deco4mas.coordinate.annotation.CoordinationStep;
import deco4mas.coordinate.interpreter.agent_state.CoordinationComponentStep;

/**
 * Abstract class which holds all the similarities of workpiece processing agents ({@link RobotAgent} and {@link TransportAgent}).
 * 
 * @author thomas
 */
public abstract class ProcessWorkpieceAgent extends MicroAgent {

	/** The agents id */
	protected String id = null;

	/** Input is needed for the reconfiguration */
	protected String input = null;

	/** Output is needed for the reconfiguration */
	protected String output = null;

	/** List of all the capabilities the agent can apply */
	protected List<String> capabilities = null;

	/** List of the all {@link Role}s which are assigned to the agent */
	protected List<Role> assignedRoles = null;

	/** List of all the role for which help is requested */
	protected List<Role> requestedRoles = new ArrayList<Role>();

	/** The workpiece the agent currently transports */
	protected Workpiece workpiece = null;

	/** The agents main state */
	protected int mainState = MainState.RUNNING_IDLE;

	/** The agents deficient state */
	protected int deficientState = DeficientState.NOT_DEFICIENT;

	/** A map with all the tasks and their ids */
	protected Map<String, Task> taskMap = null;

	protected Map<Role, IProcessWorkpieceService> processWPServices = new HashMap<Role, IProcessWorkpieceService>();

	protected Integer reconfDelay = null;

	protected IFuture<IProcessWorkpieceService> getProcessWPService(final Role role) {
		IFuture<IProcessWorkpieceService> fut = null;

		if (processWPServices.containsKey(role)) {
			fut = new Future<IProcessWorkpieceService>(processWPServices.get(role));
		} else {
			ComponentIdentifier ci = new ComponentIdentifier(role.getPostcondition().getTargetAgent(), getParent().getComponentIdentifier());
			IServiceIdentifier sid = new ServiceIdentifier(ci, IProcessWorkpieceService.class, role.getPostcondition().getTargetAgent());
			fut = SServiceProvider.getService(getServiceProvider(), sid);
			fut.addResultListener(new DefaultResultListener<IProcessWorkpieceService>() {

				@Override
				public void resultAvailable(IProcessWorkpieceService result) {
					processWPServices.put(role, result);
				}
			});
		}

		return fut;
	}

	/**
	 * This method is called if the agent receives a new workpieces and tries to process it.
	 * 
	 * @param workpiece
	 *            the received workpiece
	 * @param agentId
	 *            the id of the agent from whom the workpiece was received
	 * @return <code>true</code> if the workpiece could be processed else <code>false</code>
	 */
	public abstract boolean receiveWorkpiece(final Workpiece workpiece, String agentId);

	/**
	 * @param mainState
	 *            the mainState to set
	 * @param deficientState
	 *            the deficientState to set
	 */
	protected abstract void setStates(int mainState, int deficientState);

	/**
	 * @return the workpiece
	 */
	public Workpiece getWorkpiece() {
		return workpiece;
	}

	/**
	 * @param workpiece
	 *            the workpiece to set
	 */
	public void setWorkpiece(Workpiece workpiece) {
		this.workpiece = workpiece;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	public void handleDropout(Action action) {
		List<String> affectedCaps = new ArrayList<String>();

		if (action.getMode().equals(DropoutAgent.ACTIONMODE_STATIC)) {
			affectedCaps.addAll(action.getCapabilities());
		} else if (action.getMode().equals(DropoutAgent.ACTIONMODE_RANDOM)) {
			Random r = new Random();
			int i = r.nextInt(capabilities.size());
			affectedCaps.add(capabilities.get(i));
		} else if (action.getMode().equals(DropoutAgent.ACTIONMODE_ACTIVE_ALL)) {
			affectedCaps.addAll(getActiveCaps());
		} else if (action.getMode().equals(DropoutAgent.ACTIONMODE_ACTIVE_RANDOM)) {
			List<String> activeCaps = getActiveCaps();
			Random r = new Random();
			int i = r.nextInt(activeCaps.size());
			affectedCaps.add(activeCaps.get(i));
		}

		if (action.getType().equals(DropoutAgent.ACTIONTYPE_DROP)) {
			getLogger().info(affectedCaps + " was/were dropped in " + id);
			capabilities.removeAll(affectedCaps);

			checkForDeficient();
		} else if (action.getType().equals(DropoutAgent.ACTIONTYPE_ADD)) {
			getLogger().info(affectedCaps + " was/were added in " + id);
			capabilities.addAll(affectedCaps);
		}
	}

	private void checkForDeficient() {
		List<Role> deficientRoles = new ArrayList<Role>();

		for (Role role : assignedRoles) {
			if (!capabilities.contains(role.getCapabilityAsString())) {
				getLogger().info(id + " is missing the required capability " + role.getCapabilityAsString() + " for " + role);
				deficientRoles.add(role);
				setStates(MainState.WAITING_FOR_RECONF, DeficientState.DEFICIENT_BY_BREAK);
			}
		}

		sendHelpRequest(deficientRoles);
	}

	@SuppressWarnings("unchecked")
	protected void sendHelpRequest(List<Role> deficientRoles) {
		HelpRequest request = new HelpRequest();
		request.setAgentId(id);
		String[] array = new String[capabilities.size()];
		request.setCapabilities(capabilities.toArray(array));
		request.setDeficientRoles(deficientRoles);
		request.setEscalationLevel(HelpRequest.MIN_ESCALATION_LEVEL);
		request.setHopCount(0);

		requestedRoles.addAll(deficientRoles);

		if (reconfDelay == 0) {
			waitForTick(new SendMediumMessageStep(request));
		} else {
			waitFor(reconfDelay, new SendMediumMessageStep(request));
		}
	}

	private List<String> getActiveCaps() {
		List<String> activeCaps = new ArrayList<String>();

		for (Role role : assignedRoles) {
			activeCaps.add(role.getCapabilityAsString());
		}

		return activeCaps;
	}

	@CoordinationStep
	public class SendMediumMessageStep extends CoordinationComponentStep {

		public MediumMessage message = null;

		public SendMediumMessageStep(MediumMessage message) {
			this.message = message;
		}

		public IFuture<Void> execute(IInternalAccess ia) {
			if (message instanceof HelpRequest) {
				HelpRequest request = (HelpRequest) message;

				String msg = id + " sends HelpRequest for " + request.getDeficientRoles().size() + " roles: " + request.getDeficientRoles();
				handleConsoleMsg(new ConsoleMessage(ConsoleMessage.TYPE_ADAPTIVITY, msg), getLogger());
			} else if (message instanceof HelpReply) {
				HelpReply reply = (HelpReply) message;

				String msg = id + " sends HelpReply for " + reply.getTakenRoles().size() + " roles: " + reply.getTakenRoles();
				handleConsoleMsg(new ConsoleMessage(ConsoleMessage.TYPE_ADAPTIVITY, msg), getLogger());
			}

			return IFuture.DONE;
		}
	}

	@CoordinationStep
	public class ReceiveMediumMessageStep extends CoordinationComponentStep {

		@CoordinationParameter
		public MediumMessage message = null;

		public IFuture<Void> execute(IInternalAccess ia) {
			if (message instanceof HelpRequest) {
				HelpRequest request = (HelpRequest) message;

				String msg = id + " receives HelpRequest from " + request.getAgentId();
				handleConsoleMsg(new ConsoleMessage(ConsoleMessage.TYPE_ADAPTIVITY, msg), getLogger());
				handleHelpRequest(request);
			} else if (message instanceof HelpReply) {
				HelpReply reply = (HelpReply) message;

				String msg = id + " receives HelpReply from " + reply.getReplaceAgentId();
				handleConsoleMsg(new ConsoleMessage(ConsoleMessage.TYPE_ADAPTIVITY, msg), getLogger());
				handleHelpReply(reply);
			}

			return IFuture.DONE;
		}

	}

	@SuppressWarnings("unchecked")
	protected void handleHelpRequest(HelpRequest request) {
		if (reconfDelay == 0) {
			waitForTick(new SendMediumMessageStep(request));
		} else {
			waitFor(reconfDelay, new SendMediumMessageStep(request));
		}
	}

	protected void handleHelpReply(HelpReply reply) {
		if (reply.getReceiverIds().contains(id)) {
			// agent is defect agent
			if (reply.getDefectAgentId().equals(id)) {
				forwardHelpReply(reply);

				handleConsoleMsg(new ConsoleMessage(ConsoleMessage.TYPE_ADAPTIVITY, id + " removes " + reply.getTakenRoles().size() + " deficient roles " + reply.getTakenRoles()), getLogger());
				handleConsoleMsg(new ConsoleMessage(ConsoleMessage.TYPE_ADAPTIVITY, id + " adds " + reply.getVacantRoles().size() + " new roles " + reply.getVacantRoles()), getLogger());

				assignedRoles.removeAll(reply.getTakenRoles());
				requestedRoles.removeAll(reply.getTakenRoles());

				processWPServices.keySet().removeAll(reply.getTakenRoles());

				assignedRoles.addAll(reply.getVacantRoles());

				if (requestedRoles.isEmpty()) {
					List<Role> defRoles = getDeficientRoles();

					if (defRoles.isEmpty()) {
						setStates(MainState.RUNNING_IDLE, DeficientState.NOT_DEFICIENT);
					} else {
						setStates(MainState.WAITING_FOR_RECONF, DeficientState.DEFICIENT_BY_CHANGE);

						sendHelpRequest(defRoles);
					}
				}
			} else {
				boolean vacantInput = false, vacantOutput = false, takenInput = false, takenOutput = false;

				for (Role vacantRole : reply.getVacantRoles()) {
					if (vacantRole.getPrecondition().getTargetAgent().equals(id)) {
						vacantInput = true;
					}
					if (vacantRole.getPostcondition().getTargetAgent().equals(id)) {
						vacantOutput = true;
					}

					if (vacantInput && vacantOutput) {
						break;
					}
				}
				for (Role takenRole : reply.getTakenRoles()) {
					if (takenRole.getPrecondition().getTargetAgent().equals(id)) {
						takenInput = true;
					}
					if (takenRole.getPostcondition().getTargetAgent().equals(id)) {
						takenOutput = true;
					}

					if (takenInput && takenOutput) {
						break;
					}
				}

				if (vacantInput || vacantOutput || takenInput || takenOutput) {
					for (Role role : assignedRoles) {
						// replacement agent was input
						if (vacantOutput && reply.getReplaceAgentId().equals(role.getPrecondition().getTargetAgent())) {
							String newInput = reply.getDefectAgentId();

							handleConsoleMsg(new ConsoleMessage(ConsoleMessage.TYPE_ADAPTIVITY, id + " changes input from " + role.getPrecondition().getTargetAgent() + " to " + newInput), getLogger());
							role.getPrecondition().setTargetAgent(newInput);
						}
						// defect agent was input
						if (takenOutput && reply.getDefectAgentId().equals(role.getPrecondition().getTargetAgent())) {
							String newInput = reply.getReplaceAgentId();

							handleConsoleMsg(new ConsoleMessage(ConsoleMessage.TYPE_ADAPTIVITY, id + " changes input from " + role.getPrecondition().getTargetAgent() + " to " + newInput), getLogger());
							role.getPrecondition().setTargetAgent(newInput);
						}
						// replacement agent was output
						if (vacantInput && reply.getReplaceAgentId().equals(role.getPostcondition().getTargetAgent())) {
							String newOutput = reply.getDefectAgentId();

							handleConsoleMsg(new ConsoleMessage(ConsoleMessage.TYPE_ADAPTIVITY, id + " changes output from " + role.getPostcondition().getTargetAgent() + " to " + newOutput),
									getLogger());
							role.getPostcondition().setTargetAgent(newOutput);

							processWPServices.remove(role);
						}
						// defect agent was output
						if (takenInput && reply.getDefectAgentId().equals(role.getPostcondition().getTargetAgent())) {
							String newOutput = reply.getReplaceAgentId();

							handleConsoleMsg(new ConsoleMessage(ConsoleMessage.TYPE_ADAPTIVITY, id + " changes output from " + role.getPostcondition().getTargetAgent() + " to " + newOutput),
									getLogger());
							role.getPostcondition().setTargetAgent(newOutput);

							processWPServices.remove(role);
						}
					}
				}

				forwardHelpReply(reply);
			}
		} else {
			forwardHelpReply(reply);
		}
	}

	/**
	 * Removes this agent from the {@link Set} of receivers in the given {@link HelpReply} ({@link HelpReply#getReceiverIds()}) if this agent was one of the receivers. If the {@link HelpReply} still
	 * has receivers the it is forwarded over the medium.
	 * 
	 * @param reply
	 *            the given {@link HelpReply}
	 */
	@SuppressWarnings("unchecked")
	private void forwardHelpReply(HelpReply reply) {
		if (reply.getReceiverIds().contains(id)) {
			reply.getReceiverIds().remove(id);
		}

		if (!reply.getReceiverIds().isEmpty()) {
			if (reconfDelay == 0) {
				waitForTick(new SendMediumMessageStep(reply));
			} else {
				waitFor(reconfDelay, new SendMediumMessageStep(reply));
			}
		}
	}

	/**
	 * Returns a {@link List} with all the deficient {@link Role}s.
	 * 
	 * @return the deficient {@link Role}s
	 */
	private List<Role> getDeficientRoles() {
		List<Role> defRoles = new ArrayList<Role>();

		for (Role role : assignedRoles) {
			if (!capabilities.contains(role.getCapabilityAsString())) {
				defRoles.add(role);
			}
		}

		return defRoles;
	}

	/**
	 * Sends the given {@link ConsoleMessage} to the {@link ManagerService} by calling {@link ManagerService#handleConsoleMsg(ConsoleMessage)}.
	 * 
	 * @param msg
	 *            the given {@link ConsoleMessage}
	 * @param logger
	 */
	@SuppressWarnings("unchecked")
	public void handleConsoleMsg(final ConsoleMessage msg, final Logger logger) {
		logger.info(msg.getOutMsg());

		this.getRequiredService("managerService").addResultListener(new DefaultResultListener<IManagerService>() {

			public void resultAvailable(IManagerService service) {
				service.handleConsoleMsg(msg);
			}
		});
	}
}