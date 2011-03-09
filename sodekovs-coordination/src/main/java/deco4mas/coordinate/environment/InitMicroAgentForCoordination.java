package deco4mas.coordinate.environment;

import jadex.application.runtime.IApplication;
import jadex.application.space.envsupport.environment.AbstractEnvironmentSpace;
import jadex.application.space.envsupport.environment.ISpaceObject;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.micro.IMicroExternalAccess;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import deco.lang.dynamics.AgentElementType;
import deco.lang.dynamics.MASDynamics;
import deco.lang.dynamics.mechanism.AgentElement;
import deco.lang.dynamics.mechanism.DecentralizedCausality;
import deco.lang.dynamics.mechanism.DirectCausality;
import deco4mas.coordinate.DecentralCoordinationInformation;
import deco4mas.coordinate.DirectCoordinationInformation;
import deco4mas.coordinate.ProcessMASDynamics;
import deco4mas.coordinate.interpreter.agent_state.MicroBehaviourObservationComponent;
import deco4mas.helper.Constants;

/**
 * This Class is used to init the observer for the micro agents in order to do
 * the publications and perceptions.
 * 
 * @author Thomas Preisler
 */
public class InitMicroAgentForCoordination {

	private IComponentIdentifier ai = null;

	private IMicroExternalAccess extAccess = null;

	private IApplication context = null;

	private AbstractEnvironmentSpace space = null;

	private MASDynamics masDyn = null;

	private String agentType = null;

	private MicroBehaviourObservationComponent behaviourObserver = null;

	private int numberOfPublishPercepts = 0;

	private int numberOfPerceivePercepts = 0;

	/**
	 * Starts the Initialization.
	 * 
	 * @param ai
	 * @param exta
	 * @param context
	 * @param space
	 * @param masDyn
	 */
	public void startInits(IComponentIdentifier ai, IMicroExternalAccess exta, IApplication context,
			AbstractEnvironmentSpace space, MASDynamics masDyn) {
		this.ai = ai;
		this.extAccess = exta;
		this.context = context;
		this.space = space;
		this.masDyn = masDyn;

		if (!initAvatar()) {
			initExternalAccess();
		}
	}

	/**
	 * Initiates the avatar for the space.
	 * 
	 * @return <code>false</code> if no errors occurs during the initiation else
	 *         <code>true</code>
	 */
	private boolean initAvatar() {
		boolean exeption = false;

		if (space.getAvatar(ai) == null) {
			try {
				Map<String, IComponentIdentifier> props = new HashMap<String, IComponentIdentifier>();
				props.put(ISpaceObject.PROPERTY_OWNER, ai);
				space.createSpaceObject(((IApplication) space.getContext()).getComponentType(ai), props, null);
			} catch (RuntimeException e) {
				if (e.getMessage().contains("Unknown space object type")) {
					exeption = true;
				} else {
					e.printStackTrace();
				}
			}

		}

		return exeption;
	}

	/**
	 * Initializes the external access.
	 */
	private void initExternalAccess() {
		extAccess.scheduleStep(new IComponentStep() {
			@Override
			public Object execute(IInternalAccess ia) {
				behaviourObserver = new MicroBehaviourObservationComponent(extAccess);
				agentType = context.getComponentType(ai);
				initPublishAndPercept();
				return null;
			}
		});
	}

	/**
	 * Init the percepts for "publish" and "percept"
	 * 
	 * @param coordInfo
	 */
	private void initPublishAndPercept() {
		ProcessMASDynamics processMASDynamics = new ProcessMASDynamics(extAccess, masDyn);
		processMASDynamics.process();

		// init the publications
		for (DecentralCoordinationInformation dci : processMASDynamics.getPublications()) {

			DecentralizedCausality causality = masDyn.getCausalities().getDCMRealizationByName(
					dci.getDml().getRealization());
			for (AgentElement ae : causality.getFrom_agents()) {
				if (ae.getAgent_id().equals(agentType)) {
					initListener(ae, dci.getDml().getRealization());

					updateMappingsDecentral(ae, dci, Constants.PUBLISH);
					numberOfPublishPercepts++;
				}
			}
		}

		// init perceptions.....
		for (DecentralCoordinationInformation dci : processMASDynamics.getPerceptions()) {
			DecentralizedCausality causality = masDyn.getCausalities().getDCMRealizationByName(
					dci.getDml().getRealization());
			for (AgentElement ae : causality.getTo_agents()) {
				if (ae.getAgent_id().equals(agentType)) {
					updateMappingsDecentral(ae, dci, Constants.PERCEIVE);
					numberOfPerceivePercepts++;
				}
			}
		}

		// init the direct publications
		for (DirectCoordinationInformation dci : processMASDynamics.getDirectPublications()) {
			DirectCausality causality = masDyn.getCausalities().getDirectLinkRealizationByName(
					dci.getDirectLink().getRealization());
			for (AgentElement ae : causality.getFrom_agents()) {
				if (ae.getAgent_id().equals(agentType)) {
					initListener(ae, dci.getDirectLink().getRealization());

					updateMappingsDirect(ae, dci, Constants.PUBLISH);
					numberOfPublishPercepts++;
				}
			}
		}

		// init perceptions.....
		for (DirectCoordinationInformation dci : processMASDynamics.getDirectPerceptions()) {
			DirectCausality causality = masDyn.getCausalities().getDirectLinkRealizationByName(
					dci.getDirectLink().getRealization());
			for (AgentElement ae : causality.getTo_agents()) {
				if (ae.getAgent_id().equals(agentType)) {
					updateMappingsDirect(ae, dci, Constants.PERCEIVE);
					numberOfPerceivePercepts++;
				}
			}
		}

		System.out.println("#InitMicroAgentCoordinationPlan-" + extAccess.getComponentIdentifier().getName()
				+ "# Completed initialization: " + numberOfPublishPercepts + " PublishPercepts and "
				+ numberOfPerceivePercepts + " PerceivePercepts");

		((CoordinationSpace) space).getAgentData().put(((IApplication) context).getComponentType(ai),
				behaviourObserver.getRoleDefinitionsForPerceive());
	}

	/**
	 * Check which kind of listener has to activated
	 * 
	 * @param ae
	 */
	private void initListener(AgentElement ae, String mechanismRealizationId) {
		if (ae.getAgentElementType().equals(AgentElementType.MICRO_STEP.toString())) {
			behaviourObserver.initMicroStepListener(ae, mechanismRealizationId);
		}
	}

	/**
	 * Updates all the necessary mappings for "PUBLISH" or "PERCEIVE": 1) the
	 * Role Mappings for this Agent. Means: Which event belongs to which role 2)
	 * the parameter and data mappings Decentral
	 * 
	 * @param ae
	 * @param dci
	 * @param perceptType
	 */
	private void updateMappingsDecentral(AgentElement ae, DecentralCoordinationInformation dci, String perceptType) {
		if (perceptType.equals(Constants.PUBLISH)) {
			behaviourObserver.getRoleDefinitionsForPublish().put(
					perceptType + "::" + dci.getDml().getRealization() + "::" + ae.getElement_id() + "::"
							+ ae.getAgentElementType(), dci.getRef());
			behaviourObserver.getParameterAndDataMappings().put(
					perceptType + "::" + dci.getDml().getRealization() + "::" + ae.getElement_id() + "::"
							+ ae.getAgentElementType(), ae);
		} else {
			if (!behaviourObserver.getRoleDefinitionsForPerceive().containsKey(dci.getDml().getRealization())) {
				behaviourObserver.getRoleDefinitionsForPerceive().put(dci.getDml().getRealization(),
						new HashSet<Object[]>());
			}
			Set<Object[]> dciSet = behaviourObserver.getRoleDefinitionsForPerceive().get(dci.getDml().getRealization());
			dciSet.add((Object[]) new Object[] { dci, ae });
		}
	}

	/**
	 * Updates all the necessary mappings for "PUBLISH" or "PERCEIVE": 1) the
	 * Role Mappings for this Agent. Means: Which event belongs to which role 2)
	 * the parameter and data mappings Direct
	 * 
	 * @param ae
	 * @param dci
	 * @param perceptType
	 */
	private void updateMappingsDirect(AgentElement ae, DirectCoordinationInformation dci, String perceptType) {
		if (perceptType.equals(Constants.PUBLISH)) {
			behaviourObserver.getRoleDefinitionsForPublish().put(
					perceptType + "::" + dci.getDirectLink().getRealization() + "::" + ae.getElement_id() + "::"
							+ ae.getAgentElementType(), dci.getRef());
			behaviourObserver.getParameterAndDataMappings().put(
					perceptType + "::" + dci.getDirectLink().getRealization() + "::" + ae.getElement_id() + "::"
							+ ae.getAgentElementType(), ae);
		} else {
			if (!behaviourObserver.getRoleDefinitionsForPerceive().containsKey(dci.getDirectLink().getRealization())) {
				behaviourObserver.getRoleDefinitionsForPerceive().put(dci.getDirectLink().getRealization(),
						new HashSet<Object[]>());
			}
			Set<Object[]> dciSet = behaviourObserver.getRoleDefinitionsForPerceive().get(
					dci.getDirectLink().getRealization());
			dciSet.add((Object[]) new Object[] { dci, ae });
		}
	}
}