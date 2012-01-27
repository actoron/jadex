package deco4mas.coordinate.interpreter.agent_state;

import jadex.bridge.IComponentChangeEvent;
import jadex.bridge.IComponentListener;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.commons.IChangeListener;
import jadex.commons.IFilter;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.IFuture;
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
		MicroAgentInterpreter interpreter = (MicroAgentInterpreter) externalAccess.getInterpreter();
		interpreter.addComponentListener(new IComponentListener() {

			@Override
			public IFilter getFilter() {
				return new IFilter() {
					public boolean filter(Object obj) {
						// Nur EVENT_TYPE_CREATION Events sind interessant
						IComponentChangeEvent cce = (IComponentChangeEvent) obj;
						return IComponentChangeEvent.EVENT_TYPE_CREATION.equals(cce.getEventType());
					}
				};
			}

			@Override
			public IFuture<Void> eventOccured(IComponentChangeEvent cce) {
				if (cce.getSourceCategory().equals(MicroAgentInterpreter.TYPE_STEP)) {
					if (cce.getDetails() instanceof CoordinationStepDetails) {
						CoordinationStepDetails details = (CoordinationStepDetails) cce.getDetails();
						String nameOfElement = details.getSimpleClassName();
						IComponentStep<?> runStep = details.getStep();

						if (agentElement.getElement_id().equals(nameOfElement)) {
							checkAndPublishIfApplicable(runStep, AgentElementType.MICRO_STEP, nameOfElement);
						}
					}
				}

				return IFuture.DONE;
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
	private void checkAndPublishIfApplicable(final IComponentStep<?> runStep, final AgentElementType agentElementType, final String nameOfElement) {
		extAccess.scheduleStep(new IComponentStep<Void>() {

			@Override
			public IFuture<Void> execute(IInternalAccess ia) {
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

				return IFuture.DONE;
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
	private HashMap<String, Object> publishWhenApplicable(String key, IComponentStep<?> runStep, AgentElementType agentElementType, MicroAgent ma) {
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
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void publishEvent(Object value, HashMap<String, Object> parameterDataMappings, String agentElementName, AgentElementType agentElementType, String dmlRealizationName, MicroAgent ma) {
		final CoordinationInfo coordInfo = createCoordinationInfo(value, parameterDataMappings, agentElementName, agentElementType, dmlRealizationName);
		coordInfo.addValue(CoordinationSpaceObject.AGENT_ARCHITECTURE, "Micro");

		IExternalAccess parent = ma.getParentAccess();
		for (String spaceName : spaces) {
			parent.getExtension(spaceName).addResultListener(new DefaultResultListener() {

				@Override
				public void resultAvailable(Object result) {
					CoordinationSpace space = (CoordinationSpace) result;
					eventPublication.publishEvent(coordInfo, space);
				}
			});
		}
	}
}