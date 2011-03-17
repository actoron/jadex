package deco4mas.coordinate.interpreter.agent_state;

import jadex.application.runtime.IApplicationExternalAccess;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.commons.ChangeEvent;
import jadex.commons.IChangeListener;
import jadex.micro.ExternalAccess;
import jadex.micro.IMicroExternalAccess;
import jadex.micro.MicroAgent;
import jadex.micro.MicroAgentInterpreter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import deco.lang.dynamics.AgentElementType;
import deco.lang.dynamics.MASDynamics;
import deco.lang.dynamics.mechanism.AgentElement;
import deco4mas.coordinate.environment.CoordinationSpace;
import deco4mas.coordinate.environment.CoordinationSpaceObject;
import deco4mas.coordinate.interpreter.coordination_information.CheckRole;
import deco4mas.helper.Constants;
import deco4mas.mechanism.CoordinationInfo;

/**
 * This component is called on agent init and observes the agent. If an event occurs that is relevant for the coordination this event is dispatched to the "Coordination Event Publication".
 * 
 * @author Thomas Preisler
 */
public class MicroBehaviourObservationComponent extends BehaviorObservationComponent {

	/**
	 * An {@link ArrayList} containing the names of the coordination spaces.
	 */
	private ArrayList<String> spaces;

	/**
	 * Constructor.
	 * 
	 * @param extAccess
	 *            the external access to the observed agent
	 * @param masDynamics
	 *            the representation of the MASDynamics language
	 * @param spaces
	 *            The spaces used for coordination
	 */
	public MicroBehaviourObservationComponent(IMicroExternalAccess extAccess, MASDynamics masDynamics, ArrayList<String> spaces) {
		super(extAccess, masDynamics);
		this.spaces = spaces;
	}

	/**
	 * This method initializes the {@link IComponentStep}-Listener for the agent. When ever an {@link IComponentStep} is scheduled in the agent ( {@link IChangeListener} on "addStep"-Events in the
	 * according {@link MicroAgentInterpreter}) the {@link IChangeListener} is called and checks whether the {@link IComponentStep} is mapped in the Decomas-File.
	 * 
	 * @param agentElement
	 * @param mechanismRealizationId
	 */
	public void initMicroStepListener(final AgentElement agentElement, String mechanismRealizationId) {
		addValueToMap(agentEventDCMRealizationMappings, AgentElementType.MICRO_STEP + "::" + agentElement.getElement_id(), mechanismRealizationId);

		ExternalAccess externalAccess = (ExternalAccess) extAccess;
		MicroAgentInterpreter interpreter = externalAccess.getInterpreter();
		interpreter.setHistoryEnabled(true);
		interpreter.addChangeListener(new IChangeListener() {

			@Override
			public void changeOccurred(ChangeEvent event) {
				if (event.getType().equals("addStep")) {
					Object[] values = (Object[]) event.getValue();
					IComponentStep step = (IComponentStep) values[0];
					if (step instanceof MicroAgent.ExecuteWaitForStep) {
						MicroAgent.ExecuteWaitForStep waitForStep = (MicroAgent.ExecuteWaitForStep) step;
						IComponentStep runStep = waitForStep.getComponentStep();
						String nameOfElement = runStep.getClass().getSimpleName();

						if (agentElement.getAgentElementType().equals(AgentElementType.MICRO_STEP.toString()) && agentElement.getElement_id().equals(nameOfElement)) {
							checkAndPublishIfApplicable(runStep, AgentElementType.MICRO_STEP, nameOfElement);
						}
					}
				}
			}
		});
	}

	/**
	 * Check for each received event whether the role definition is active for ALL DCM that are interested in this event (i.e. that have registered an listener) and dispatch event to medium if role
	 * definition is satisfied.
	 * 
	 * @param runStep
	 * @param agentElementType
	 * @param nameOfElement
	 */
	private void checkAndPublishIfApplicable(final IComponentStep runStep, final AgentElementType agentElementType, final String nameOfElement) {
		extAccess.scheduleStep(new IComponentStep() {

			@Override
			public Object execute(IInternalAccess ia) {
				MicroAgent ma = (MicroAgent) ia;

				// get all the DCM Realizations that have the current AgentEvent
				// as initiator for a PUBLISH-Event
				List<String> realizationNames = agentEventDCMRealizationMappings.get(agentElementType.toString() + "::" + nameOfElement);
				for (String dmlRealizationName : realizationNames) {
					// Check whether role is active.
					HashMap<String, Object> parameterDataMappings = publishWhenApplicable(dmlRealizationName + "::" + nameOfElement, runStep, agentElementType, ma);

					if (parameterDataMappings != null) {
						publishEvent(nameOfElement, parameterDataMappings, nameOfElement, agentElementType, dmlRealizationName, ma);
					} else {
						System.out.println("#MicroBehaviorObservationComponent# Role inactive. Event not published to medium or direct publish.");
					}
				}

				return null;
			}
		});
	}

	/**
	 * Check the role condition for this Event. If the Agent has the specified role than publish the event to the coordination medium.
	 * 
	 * @param key
	 * @param runStep
	 * @param agentElementType
	 * @param ma
	 *            partial key name
	 * @return if !=null then applicable. contains then a map of the parameter and data mappings
	 */
	private HashMap<String, Object> publishWhenApplicable(String key, IComponentStep runStep, AgentElementType agentElementType, MicroAgent ma) {
		return CheckRole.checkForPublishMicro(roleDefinitionsForPublish.get(Constants.PUBLISH + "::" + key + "::" + agentElementType.toString()),
				parameterAndDataMappings.get(Constants.PUBLISH + "::" + key + "::" + agentElementType.toString()), runStep, ma);
	}

	/**
	 * Publish/Dispatch the occurred event to the "Coordination Event Publication".
	 * 
	 * @param value
	 * @param parameterDataMappings
	 * @param agentElementName
	 * @param agentElementType
	 * @param dmlRealizationName
	 * @param ma
	 */
	private void publishEvent(Object value, HashMap<String, Object> parameterDataMappings, String agentElementName, AgentElementType agentElementType, String dmlRealizationName, MicroAgent ma) {
		CoordinationInfo coordInfo = createCoordinationInfo(value, parameterDataMappings, agentElementName, agentElementType, dmlRealizationName);
		coordInfo.addValue(CoordinationSpaceObject.AGENT_ARCHITECTURE, "Micro");

		IApplicationExternalAccess app = (IApplicationExternalAccess) ma.getParent();
		for (String spaceName : spaces) {
			CoordinationSpace space = (CoordinationSpace) app.getSpace(spaceName);
			eventPublication.publishEvent(coordInfo, space);
		}
	}
}