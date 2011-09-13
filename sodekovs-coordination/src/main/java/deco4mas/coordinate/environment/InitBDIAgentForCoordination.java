package deco4mas.coordinate.environment;

import jadex.bdi.runtime.IBDIExternalAccess;
import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.extension.envsupport.environment.AbstractEnvironmentSpace;
import jadex.extension.envsupport.environment.ISpaceObject;

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
import deco4mas.coordinate.interpreter.agent_state.BDIBehaviorObservationComponent;
import deco4mas.helper.Constants;

/**
 * This Class is used to init the observer for the agents in order to do the publications and perceptions.
 * 
 * @author Ante Vilenica
 */

public class InitBDIAgentForCoordination {

	private BDIBehaviorObservationComponent behObserver = null;
	private IBDIExternalAccess exta = null;
	private String agentType;
	private IComponentDescription ai;
	private MASDynamics masDyn;
	private AbstractEnvironmentSpace space;
	private int numberOfPublishPercepts = 0;
	private int numberOfPerceivePercepts = 0;

	// public InitBDIAgentForCoordination(IAgentIdentifier ai, IContext context,
	// AbstractEnvironmentSpace space, MASDynamics masDyn) {
	// startInits(ai, context, space, masDyn);
	// }

	/**
	 * @return the RoleDefinitionsForPerceive
	 */
	// public Map<String, Object[]> startInits(IAgentIdentifier ai, IContext
	// context, AbstractEnvironmentSpace space, MASDynamics masDyn) {
	public void startInits(IComponentDescription ai, IBDIExternalAccess exta, AbstractEnvironmentSpace space, MASDynamics masDyn) {
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

		// store role definitions and DecentralCoordinationInfoMappings
		// Map<String, Object> decom4MasMap = new HashMap<String, Object>();
		// decom4MasMap.put(Constants.ROLE_DEFINITIONS_FOR_PUBLISH,
		// behObserver.getRoleDefinitionsForPublish());
		// decom4MasMap.put(Constants.ROLE_DEFINITIONS_FOR_PERCEIVE,
		// behObserver.getRoleDefinitionsForPerceive());
		// decom4MasMap.put(Constants.PARAMETER_DATA_MAPPING,
		// behObserver.getParameterAndDataMappings());
		// decom4MasMap.put(Constants.IOAV_STATE, this.getState());
		// decom4MasMap.put(Constants.R_CAPABILITY, this.getRCapability());

		System.out.println("#InitBDIAgentCoordinationPlan-" + exta.getComponentIdentifier().getName() + "# Completed initialization: " + numberOfPublishPercepts + " PublishPercepts and "
				+ numberOfPerceivePercepts + " PerceivePercepts");

		// agentData.put(getAgentType(ai, this.getContext()), res);
		((CoordinationSpace) space).getAgentData().put(ai.getLocalType(), behObserver.getRoleDefinitionsForPerceive());
		// return behObserver.getRoleDefinitionsForPerceive();
		// getBeliefbase().getBelief(Constants.DECO4MAS_BELIEF_NAME).setFact(decom4MasMap);
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
		// Object[] obj = ((Space2D)space).getSpaceObjects();
		// System.out.println("Got SpaceObjects. Before...: Nr: " + obj.length);
		// for (int i = 0; i < obj.length; i++) {
		// System.out.println((ISpaceObject) obj[i]);
		// }

		// // Create Avatar for those agent that haven't one yet.
		// String agentName = aif.getName();
		if (space.getAvatar(ai) == null) {
			// // System.out.println("No avatar for: " + agentName + " - " +
			// ((IApplicationContext) space.getContext()).getAgentType(aif[i]));
			// // TODO: add those mappings that doesn't exit yet, i.e those
			// // which haven't been declared in the application.xml and
			// // those that haven't been add within these loop
			// // AvatarMapping avatarMapping = new AvatarMapping("Receiver",
			// // "receiver");
			// // space.addAvatarMappings(avatarMapping);
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

			// } else {
			// ISpaceObject[] avatars = space.getAvatars(aif[i]);
			// // System.out.println(agentName + " has " + avatars.length +
			// " -  avatars. Avatar: " + avatars[0].toString());
		}
		return exeption;
		// }
		// }
	}

	private void initExternalAccess() {
		exta.scheduleStep(new IComponentStep() {
			@Override
			public Object execute(IInternalAccess ia) {
				behObserver = new BDIBehaviorObservationComponent(exta, masDyn);
				agentType = ai.getLocalType();
				initPublishAndPercept();
				return null;
			}
		});
	}

	// private void newStuff() {
	//
	// // NEws Stuff
	//
	// IApplicationContext iac = (IApplicationContext) this.getContext();
	// IAMS ams = ((IAMS) iac.getPlatform().getService(IAMS.class));
	// ams.getExternalAccess(ai, new IResultListener() {
	// public void exceptionOccurred(Exception exception) {
	// // exception.printStackTrace();
	// }
	//
	// public void resultAvailable(Object result) {
	// IExternalAccess exta = (IExternalAccess) result;
	// System.out.println("###AgentName: " + exta.getAgentName());
	// }
	// });
	//
	// HashMap<String, Object> values = new HashMap<String, Object>();
	// values.put("MASDynamics", masDyn);
	// // values.put("FromAgents", fromAgents);
	// // values.put("ToAgents", toAgents);
	//
	// // Produce initial percept that causes the initialization of the
	// // participating agents.
	// CoordinationInfo coordInfo = new CoordinationInfo();
	// coordInfo.setName("Init-Deco4MAS-Coordination");
	// coordInfo
	// .setType(CoordinationSpaceObject.COORDINATION_INFORMATION_TYPE);
	// coordInfo.addValue(CoordinationSpaceObject.AGENT_ARCHITECTURE,
	// "BDI");
	// coordInfo.addValue(CoordinationSpaceObject.AGENT_ELEMENT_TYPE,
	// AgentElementType.INTERNAL_EVENT);
	// coordInfo.addValue("VALUES", values);
	//
	// ISpaceObject newObj =
	// this.createSpaceObject("CoordinationSpaceObject",
	// ((CoordinationInformation) coordInfo).getValues(), null);
	//
	// /** HACK !!!!!!!!!!!!!! */
	// this.fireEnvironmentEvent(new EnvironmentEvent(
	// CoordinationEvent.COORDINATE_INIT_PARTICIPANTS, this, newObj,
	// null));
	// }

	// /**
	// * Updates all the necessary mappings for "PERCEIVE":
	// * 1) Updates the Role Mappings for this Agent. Means: Which event belongs
	// to which role
	// * 2) And the parameter and data mappings
	// * @param perceptType
	// * @param agentReference
	// */
	// private void updateMappingsForPerceive(String perceptType, AgentReference
	// agentReference, AgentElement ae) {
	// behObserver.getRoleDefinitions().put(perceptType + "::" + agentType,
	// agentReference);
	// behObserver.getParameterAndDataMappings().put(perceptType + "::" +
	// agentType, ae);
	// }

	// /**
	// * Checks whether this AgentType is named within the list
	// *
	// * @return
	// */
	// private boolean containsAgentType(ArrayList<String> list) {
	// String agentName =
	// this.getExternalAccess().getApplicationContext().getAgentType(this.getAgentIdentifier());
	//
	// for (String name : list) {
	// if (name.equals(agentName)) {
	// return true;
	// }
	// }
	// return false;
	// }
}