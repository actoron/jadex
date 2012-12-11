package deco4mas.distributed.coordinate.environment;

import jadex.bdi.runtime.IBDIExternalAccess;
import jadex.bdi.runtime.impl.flyweights.ExternalAccessFlyweight;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.commons.future.IFuture;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.kernelbase.StatelessAbstractInterpreter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import deco.distributed.lang.dynamics.AgentElementType;
import deco.distributed.lang.dynamics.MASDynamics;
import deco.distributed.lang.dynamics.mechanism.AgentElement;
import deco.distributed.lang.dynamics.mechanism.DecentralizedCausality;
import deco.distributed.lang.dynamics.mechanism.DirectCausality;
import deco4mas.distributed.convergence.BDIConvergenceService;
import deco4mas.distributed.coordinate.DecentralCoordinationInformation;
import deco4mas.distributed.coordinate.DirectCoordinationInformation;
import deco4mas.distributed.coordinate.ProcessMASDynamics;
import deco4mas.distributed.coordinate.interpreter.agent_state.BDIBehaviorObservationComponent;
import deco4mas.distributed.helper.Constants;


/**
 * This Class is used to init the observer for the agents in order to do the publications and perceptions.
 * 
 * @author Ante Vilenica
 */

public class InitBDIAgentForCoordination {

	private deco4mas.distributed.coordinate.interpreter.agent_state.BDIBehaviorObservationComponent behObserver = null;
	private IBDIExternalAccess exta = null;
	private String agentType;
	private IComponentDescription ai;
	private deco.distributed.lang.dynamics.MASDynamics masDyn;
	private CoordinationSpace space;
	private int numberOfPublishPercepts = 0;
	private int numberOfPerceivePercepts = 0;

	public void startInits(IComponentDescription ai, IBDIExternalAccess exta, CoordinationSpace space, MASDynamics masDyn) {
		this.ai = ai;
		this.exta = exta;
		this.masDyn = masDyn;
		this.space = space;

		boolean exeption = initAvatar();
		if (!exeption)
			initExternalAccess();
	}

	/**
	 * Init the percepts for "publish" and "percept"
	 * 
	 * @param coordInfo
	 */
	private void initPublishAndPercept() {
		// System.out.println("#InitBDIAgentCoordinationPlan-" +
		// this.getAgentName() + "# Started initialization.");

		// MASDynamics dynamicsModel = (MASDynamics)
		// coordInfo.get("MASDynamics");
		ProcessMASDynamics processMASDynamics = new ProcessMASDynamics(exta, masDyn);
		processMASDynamics.process();

		// init the publications
		for (DecentralCoordinationInformation dci : processMASDynamics.getPublications()) {

			DecentralizedCausality causality = masDyn.getCausalities().getDCMRealizationByName(dci.getDml().getRealization());
			for (AgentElement ae : causality.getFrom_agents()) {
				if (ae.getAgent_id().equals(agentType)) {
					// initListener(ae, causality.getTo_agents());
					initListener(ae, dci.getDml().getRealization());

					// updateMappings(ae, dci.getRef(), "PUBLISH");
					updateMappingsDecentral(ae, dci, Constants.PUBLISH);
					// behObserver.setDecentralCoordInfoMapping(dci);
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
					// behObserver.setDecentralCoordInfoMapping(dci);
					numberOfPerceivePercepts++;
				}
			}
		}

		// init the direct publications
		for (DirectCoordinationInformation dci : processMASDynamics.getDirectPublications()) {
			DirectCausality causality = masDyn.getCausalities().getDirectLinkRealizationByName(dci.getDirectLink().getRealization());
			for (AgentElement ae : causality.getFrom_agents()) {
				if (ae.getAgent_id().equals(agentType)) {
					// initListener(ae, causality.getTo_agents());
					initListener(ae, dci.getDirectLink().getRealization());

					// updateMappings(ae, dci.getRef(), "PUBLISH");
					updateMappingsDirect(ae, dci, Constants.PUBLISH);
					// behObserver.setDecentralCoordInfoMapping(dci);
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
					// behObserver.setDecentralCoordInfoMapping(dci);
					numberOfPerceivePercepts++;
				}
			}
		}

		System.out.println("#InitBDIAgentCoordinationPlan-" + exta.getComponentIdentifier().getName() + "# Completed initialization: " + numberOfPublishPercepts + " PublishPercepts and "
				+ numberOfPerceivePercepts + " PerceivePercepts");

		((CoordinationSpace) space).getAgentData().put(ai.getLocalType(), behObserver.getRoleDefinitionsForPerceive());
	}

	/**
	 * Check which kind of listener has to activated
	 * 
	 * @param ae
	 */
	// private void initListener(AgentElement ae, ArrayList<AgentElement>
	// toAgents) {
	private void initListener(AgentElement ae, String mechanismRealizationId) {
		if (ae.getAgentElementType().equals(AgentElementType.BDI_BELIEFSET.toString())) {
			// System.out.println("#InitBDIAgentCoordinationPlan# Init Belief_Set-Listener");
			behObserver.initBeliefSetListener(ae, mechanismRealizationId);
		} else if (ae.getAgentElementType().equals(AgentElementType.BDI_BELIEF.toString())) {
			// System.out.println("#InitBDIAgentCoordinationPlan# Init Belief-Listener");
			behObserver.initBeliefListener(ae, mechanismRealizationId);
		} else if (ae.getAgentElementType().equals(AgentElementType.BDI_GOAL.toString())) {
			// System.out.println("#InitBDIAgentCoordinationPlan# Init Goal-Listener");
			behObserver.initGoalListener(ae, mechanismRealizationId);
		} else if (ae.getAgentElementType().equals(AgentElementType.BDI_PLAN.toString())) {
			// System.out.println("#InitBDIAgentCoordinationPlan# Init Goal-Listener");
			behObserver.initPlanListener(ae, mechanismRealizationId);
		} else if (ae.getAgentElementType().equals(AgentElementType.INTERNAL_EVENT.toString())) {
			// System.out.println("#InitBDIAgentCoordinationPlan# Init Goal-Listener");
			behObserver.initInternalEventListener(ae, mechanismRealizationId);
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
			behObserver.getRoleDefinitionsForPublish().put(perceptType + "::" + dci.getDml().getRealization() + "::" + ae.getElement_id() + "::" + ae.getAgentElementType(), dci.getRef());
			behObserver.getParameterAndDataMappings().put(perceptType + "::" + dci.getDml().getRealization() + "::" + ae.getElement_id() + "::" + ae.getAgentElementType(), ae);
		} else {
			// System.out.println(ae.getElement_id());
			// Add support for more perceive per role CH
			if (!behObserver.getRoleDefinitionsForPerceive().containsKey(dci.getDml().getRealization())) {
				behObserver.getRoleDefinitionsForPerceive().put(dci.getDml().getRealization(), new HashSet<Object[]>());
			}
			Set<Object[]> dciSet = behObserver.getRoleDefinitionsForPerceive().get(dci.getDml().getRealization());
			dciSet.add((Object[]) new Object[] { dci, ae });
		}
	}

	/**
	 * Updates all the necessary mappings for "PUBLISH" or "PERCEIVE": 1) the Role Mappings for this Agent. Means: Which event belongs to which role 2) the parameter and data mappings Direct
	 * 
	 * @param ae
	 * @param agentReference
	 */
	// private void updateMappings(AgentElement ae, AgentReference
	// agentReference, String perceptType) {
	private void updateMappingsDirect(AgentElement ae, DirectCoordinationInformation dci, String perceptType) {
		if (perceptType.equals(Constants.PUBLISH)) {
			behObserver.getRoleDefinitionsForPublish().put(perceptType + "::" + dci.getDirectLink().getRealization() + "::" + ae.getElement_id() + "::" + ae.getAgentElementType(), dci.getRef());
			behObserver.getParameterAndDataMappings().put(perceptType + "::" + dci.getDirectLink().getRealization() + "::" + ae.getElement_id() + "::" + ae.getAgentElementType(), ae);
		} else {
			// System.out.println(ae.getElement_id());
			// Add support for more perceive per role CH
			if (!behObserver.getRoleDefinitionsForPerceive().containsKey(dci.getDirectLink().getRealization())) {
				behObserver.getRoleDefinitionsForPerceive().put(dci.getDirectLink().getRealization(), new HashSet<Object[]>());
			}
			Set<Object[]> dciSet = behObserver.getRoleDefinitionsForPerceive().get(dci.getDirectLink().getRealization());
			dciSet.add((Object[]) new Object[] { dci, ae });
		}
	}

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

	private void initExternalAccess() {
		exta.scheduleStep(new IComponentStep<Void>() {
			@Override
			public IFuture<Void> execute(IInternalAccess ia) {
				behObserver = new BDIBehaviorObservationComponent(exta, masDyn);
				agentType = ai.getLocalType();
				initPublishAndPercept();
						
				// init the convergence service for the agent if the agent type is referenced in convergence part of the masdynamics
				if (masDyn.getConvergence() != null && masDyn.getConvergence().getAgents().contains(agentType)) {
					// get the coordination context id
					StatelessAbstractInterpreter interpreter = (StatelessAbstractInterpreter) space.getApplicationInternalAccess();
					// If it's a distributed application, then it has a contextID.
					HashMap<String, Object> appArgs = (HashMap<String, Object>) interpreter.getArguments();
					String coordinationContextID = (String) appArgs.get("CoordinationContextID");
					
					BDIConvergenceService service = new BDIConvergenceService((ExternalAccessFlyweight) exta, masDyn.getConvergence(), coordinationContextID);
					service.start();
				}
				
				return IFuture.DONE;
			}
		});
	}
}