package deco4mas.distributed.coordinate.environment;

import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.commons.future.IFuture;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.kernelbase.StatelessAbstractInterpreter;
import jadex.micro.IMicroExternalAccess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import deco.distributed.lang.dynamics.AgentElementType;
import deco.distributed.lang.dynamics.Causalities;
import deco.distributed.lang.dynamics.MASDynamics;
import deco.distributed.lang.dynamics.causalities.DecentralMechanismLink;
import deco.distributed.lang.dynamics.convergence.Agent;
import deco.distributed.lang.dynamics.mechanism.AgentElement;
import deco.distributed.lang.dynamics.mechanism.DecentralizedCausality;
import deco.distributed.lang.dynamics.mechanism.DirectCausality;
import deco4mas.distributed.convergence.ConvergenceMicroAgent;
import deco4mas.distributed.convergence.MicroConvergenceService;
import deco4mas.distributed.coordinate.DecentralCoordinationInformation;
import deco4mas.distributed.coordinate.DirectCoordinationInformation;
import deco4mas.distributed.coordinate.ProcessMASDynamics;
import deco4mas.distributed.coordinate.interpreter.agent_state.MicroBehaviourObservationComponent;
import deco4mas.distributed.helper.Constants;

/**
 * This Class is used to init the observer for the micro agents in order to do the publications and perceptions.
 * 
 * @author Thomas Preisler
 */
public class InitMicroAgentForCoordination {

	private IComponentDescription ai = null;

	private IMicroExternalAccess extAccess = null;

	private CoordinationSpace space = null;

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
	public void startInits(IComponentDescription ai, IMicroExternalAccess exta, CoordinationSpace space, MASDynamics masDyn) {
		this.ai = ai;
		this.extAccess = exta;
		this.space = space;
		this.masDyn = masDyn;

		if (!initAvatar()) {
			initExternalAccess();
		}
	}

	/**
	 * Initiates the avatar for the space.
	 * 
	 * @return <code>false</code> if no errors occurs during the initiation else <code>true</code>
	 */
	private boolean initAvatar() {
		boolean exeption = false;

		if (space.getAvatar(ai) == null) {
			try {
				Map<String, IComponentDescription> props = new HashMap<String, IComponentDescription>();
				props.put(ISpaceObject.PROPERTY_OWNER, ai);
				space.createSpaceObject(ai.getLocalType(), props, null);
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
		extAccess.scheduleStep(new IComponentStep<Void>() {
			@Override
			public IFuture<Void> execute(IInternalAccess ia) {
				ArrayList<String> spaces = getSpaces();
				behaviourObserver = new MicroBehaviourObservationComponent(extAccess, masDyn, spaces);
				agentType = ai.getLocalType();
				initPublishAndPercept();
				
				// init the convergence service for the agent if the agent type is referenced in convergence part of the masdynamics
				Agent agent = new Agent();
				agent.setId(agentType);
				if (masDyn.getConvergence() != null && masDyn.getConvergence().getAgents().contains(agent)) {
					// get the coordination context id
					StatelessAbstractInterpreter interpreter = (StatelessAbstractInterpreter) space.getApplicationInternalAccess();
					// If it's a distributed application, then it has a contextID.
					HashMap<String, Object> appArgs = (HashMap<String, Object>) interpreter.getArguments();
					String coordinationContextID = (String) appArgs.get("CoordinationContextID");
					
					MicroConvergenceService service = new MicroConvergenceService((ConvergenceMicroAgent) ia, masDyn.getConvergence(), coordinationContextID);
					service.start();
				}
				
				return IFuture.DONE;
			}
		});
	}

	/**
	 * Returns all the names of the used coordination spaces in this application.
	 * 
	 * @return the names of the used coordination spaces
	 */
	private ArrayList<String> getSpaces() {
		ArrayList<String> spaces = new ArrayList<String>();

		Causalities causalities = masDyn.getCausalities();
		List<DecentralMechanismLink> dml = causalities.getDml();
		for (DecentralMechanismLink decentralMechanismLink : dml) {
			spaces.addAll(decentralMechanismLink.getSpaces());
		}

		return spaces;
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

			DecentralizedCausality causality = masDyn.getCausalities().getDCMRealizationByName(dci.getDml().getRealization());
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
			DecentralizedCausality causality = masDyn.getCausalities().getDCMRealizationByName(dci.getDml().getRealization());
			for (AgentElement ae : causality.getTo_agents()) {
				if (ae.getAgent_id().equals(agentType)) {
					updateMappingsDecentral(ae, dci, Constants.PERCEIVE);
					numberOfPerceivePercepts++;
				}
			}
		}

		// init the direct publications
		for (DirectCoordinationInformation dci : processMASDynamics.getDirectPublications()) {
			DirectCausality causality = masDyn.getCausalities().getDirectLinkRealizationByName(dci.getDirectLink().getRealization());
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
			DirectCausality causality = masDyn.getCausalities().getDirectLinkRealizationByName(dci.getDirectLink().getRealization());
			for (AgentElement ae : causality.getTo_agents()) {
				if (ae.getAgent_id().equals(agentType)) {
					updateMappingsDirect(ae, dci, Constants.PERCEIVE);
					numberOfPerceivePercepts++;
				}
			}
		}

		System.out.println("#InitMicroAgentCoordinationPlan-" + extAccess.getComponentIdentifier().getName() + "# Completed initialization: " + numberOfPublishPercepts + " PublishPercepts and "
				+ numberOfPerceivePercepts + " PerceivePercepts");

		((CoordinationSpace) space).getAgentData().put(ai.getLocalType(), behaviourObserver.getRoleDefinitionsForPerceive());
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
	 * Updates all the necessary mappings for "PUBLISH" or "PERCEIVE": 1) the Role Mappings for this Agent. Means: Which event belongs to which role 2) the parameter and data mappings Decentral
	 * 
	 * @param ae
	 * @param dci
	 * @param perceptType
	 */
	private void updateMappingsDecentral(AgentElement ae, DecentralCoordinationInformation dci, String perceptType) {
		if (perceptType.equals(Constants.PUBLISH)) {
			behaviourObserver.getRoleDefinitionsForPublish().put(perceptType + "::" + dci.getDml().getRealization() + "::" + ae.getElement_id() + "::" + ae.getAgentElementType(), dci.getRef());
			behaviourObserver.getParameterAndDataMappings().put(perceptType + "::" + dci.getDml().getRealization() + "::" + ae.getElement_id() + "::" + ae.getAgentElementType(), ae);
		} else {
			if (!behaviourObserver.getRoleDefinitionsForPerceive().containsKey(dci.getDml().getRealization())) {
				behaviourObserver.getRoleDefinitionsForPerceive().put(dci.getDml().getRealization(), new HashSet<Object[]>());
			}
			Set<Object[]> dciSet = behaviourObserver.getRoleDefinitionsForPerceive().get(dci.getDml().getRealization());
			dciSet.add((Object[]) new Object[] { dci, ae });
		}
	}

	/**
	 * Updates all the necessary mappings for "PUBLISH" or "PERCEIVE": 1) the Role Mappings for this Agent. Means: Which event belongs to which role 2) the parameter and data mappings Direct
	 * 
	 * @param ae
	 * @param dci
	 * @param perceptType
	 */
	private void updateMappingsDirect(AgentElement ae, DirectCoordinationInformation dci, String perceptType) {
		if (perceptType.equals(Constants.PUBLISH)) {
			behaviourObserver.getRoleDefinitionsForPublish().put(perceptType + "::" + dci.getDirectLink().getRealization() + "::" + ae.getElement_id() + "::" + ae.getAgentElementType(), dci.getRef());
			behaviourObserver.getParameterAndDataMappings().put(perceptType + "::" + dci.getDirectLink().getRealization() + "::" + ae.getElement_id() + "::" + ae.getAgentElementType(), ae);
		} else {
			if (!behaviourObserver.getRoleDefinitionsForPerceive().containsKey(dci.getDirectLink().getRealization())) {
				behaviourObserver.getRoleDefinitionsForPerceive().put(dci.getDirectLink().getRealization(), new HashSet<Object[]>());
			}
			Set<Object[]> dciSet = behaviourObserver.getRoleDefinitionsForPerceive().get(dci.getDirectLink().getRealization());
			dciSet.add((Object[]) new Object[] { dci, ae });
		}
	}
}