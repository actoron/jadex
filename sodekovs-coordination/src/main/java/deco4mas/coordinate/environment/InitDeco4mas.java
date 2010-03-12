package deco4mas.coordinate.environment;


import jadex.application.space.envsupport.environment.AbstractEnvironmentSpace;
import jadex.application.space.envsupport.environment.IEnvironmentSpace;
import jadex.application.space.envsupport.environment.IPerceptGenerator;
import jadex.application.space.envsupport.environment.IPerceptProcessor;
import jadex.application.space.envsupport.environment.PerceptType;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.JAXBException;

import deco.lang.dynamics.MASDynamics;
import deco.lang.dynamics.mechanism.AgentElement;
import deco.lang.dynamics.mechanism.DecentralizedCausality;
import deco4mas.coordinate.interpreter.coordination_information.BDICoordinationInformationInterpreter;
import deco4mas.coordinate.interpreter.coordination_information.DefaultCoordinationEventGenerator;
import deco4mas.mechanism.v2.tspaces.TSpacesMechanism;

/**
 * Responsible for starting all things necessary for deco4mas
 * 
 * @author Ante Vilenica
 * 
 */
public class InitDeco4mas {

	private IEnvironmentSpace space = null;

	/** Reference to the used MAS-File. */
	private String masFileName;

	/**
	 * 
	 * @param space
	 */
	public InitDeco4mas(IEnvironmentSpace space) {
		this.space = space;
	}

	public MASDynamics start() {

		/** The dynamics model. */
		MASDynamics masDyn = null;

		// Make sure that the space has been initialized... (Kind of Hack)
//		while (space.getProperty("dynamics_configuration") == null) {
//			try {
//				wait(500);
//				System.out.println("#InitCoordinationSpace-Thread# Waiting");
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
		
//		long t2,t3;
//        t2=System.currentTimeMillis();
//        do{
//            t3=System.currentTimeMillis();
//        }
//        while (t3-t2<8000);
//		
//		
//		while (space.getProperty("dynamics_configuration") == null) {			
//				long t0,t1;
//		        t0=System.currentTimeMillis();
//		        do{
//		            t1=System.currentTimeMillis();
//		        }
//		        while (t1-t0<1000);
//				System.out.println("#InitCoordinationSpace-Thread# Waiting");			
//		}
		
		
//		ams.getExternalAccess(ai, new IResultListener() {
//			public void exceptionOccurred(Exception exception) {
//				exception.printStackTrace();
//			}
//
//			public void resultAvailable(Object result) {
//				exta = (IExternalAccess) result;
//				behObserver = new BDIBehaviorObservationComponent(exta);
//				agentType = exta.getApplicationContext().getAgentType(ai);
//				initPublishAndPercept();
//			}
//		});
		
		

		// Get dynamics_configuration file
//		masFileName = space.getProperty("dynamics_configuration").toString();
		//ToDo: HACK!!!! 
//		masFileName = "src//deco4mas//examples//V2//tspaces//belief_set.dynamics.xml";		              
//		masFileName = "jadex//sodekovs-coordination//src//main//java//deco4mas//examples//V2//tspaces//belief_set.dynamics.xml";
		
		masFileName = "D:\\Workspaces\\Jadex-V2\\jadex\\sodekovs-coordination\\src\\main\\java\\deco4mas\\examples\\V2\\tspaces\\belief_set.dynamics.xml";		
		System.out.println("#InitCoordinationSpace-Thread# Started processing deco4mas-file: " + masFileName);

		
		// ------ INIT COORDINATION MEDIA! -----------//

		// TODO: Init media according to the deco4Mas file!
		TSpacesMechanism tspaceMechanism = new TSpacesMechanism((CoordinationSpace) space);
		tspaceMechanism.start();

		((CoordinationSpace) space).activeCoordinationMechanisms.add(tspaceMechanism);

		// Process deco4MAS-File:
		try {
			if (masFileName.length() > 0) { // 1: fetch conf.:
				masDyn = (MASDynamics) deco4mas.util.xml.XmlUtil.retrieveFromXML(MASDynamics.class, masFileName);
			}

		} catch (FileNotFoundException e) {
			System.out.println("#ProcessMASDynamics#" + ":");
			System.out.println("\t file: " + masFileName + " could not be found...");
			e.printStackTrace();
		} catch (JAXBException e) {
			System.out.println("#ProcessMASDynamics#" + ":");
			System.out.println("\t file: " + masFileName + " could not be processed...");
			e.printStackTrace();
		}
		
		// these lists contain a list of agent names and each list has a unique set of agent names itself.
		ArrayList<String> allReferencedAgentTypesList = new ArrayList<String>();
		ArrayList<String> fromReferencedAgentTypesList = new ArrayList<String>();
		ArrayList<String> toReferencedAgentTypesList = new ArrayList<String>();		
		getAgentLists(masDyn, allReferencedAgentTypesList, fromReferencedAgentTypesList, toReferencedAgentTypesList);
		
 
		// -------------------- INIT Space Object TYPES ------------------------------//
		// Init all types that may be needed for the deco4mas coordination.
		for (String typeName : allReferencedAgentTypesList) {
			space.addSpaceObjectType(typeName, null);
		}

		
		
		
		
		
		
		
		// -------------------- INIT Avatars ------------------------------//

		// Wait till all agents are initialtzied? In order to make sure to create all needed avatars???

		// Object[] obj = ((Space2D)space).getSpaceObjects();
		// System.out.println("Got SpaceObjects. Before...: Nr: " + obj.length);
		// for (int i = 0; i < obj.length; i++) {
		// System.out.println((ISpaceObject) obj[i]);
		// }

//		IAgentIdentifier[] aif = ((AbstractEnvironmentSpace) space).getContext().getAgents();
//		// Create Avatars for those agents that haven't one yet.
//		for (int i = 0; i < aif.length; i++) {
//			String agentName = aif[i].getName();
//			if (space.getAvatar(aif[i]) == null) {
////				System.out.println("No avatar for: " + agentName + " - " + ((IApplicationContext) space.getContext()).getAgentType(aif[i]));
//				// TODO: add those mappings that doesn't exit yet, i.e those
//				// which haven't been declared in the application.xml and
//				// those that haven't been add within these loop
//				// AvatarMapping avatarMapping = new AvatarMapping("Receiver",
//				// "receiver");
//				// space.addAvatarMappings(avatarMapping);
//
//				Map props = new HashMap();
//				props.put(ISpaceObject.PROPERTY_OWNER, aif[i]);
//				space.createSpaceObject(((IApplicationContext) space.getContext()).getAgentType(aif[i]), props, null);
//			} else {
//				ISpaceObject[] avatars = space.getAvatars(aif[i]);
////				System.out.println(agentName + " has " + avatars.length + " -  avatars. Avatar: " + avatars[0].toString());
//			}
//		}

		
		
		
		
		
		
		
		
		
		
		
		
		
		// -------------- INIT Space Percepts! ------------//

		// Percept Type for usual coordination events: coordination_event
		Set agenttypes = new HashSet();
		for (int i = 0; i < toReferencedAgentTypesList.size(); i++) {
			// add the "toAgents"
			agenttypes.add(toReferencedAgentTypesList.get(i));
		}

		PerceptType perceptType = new PerceptType();
		perceptType.setName("coordination_event");
		perceptType.setComponentTypes(agenttypes);
		perceptType.setObjectTypes(null);
		space.addPerceptType(perceptType);

		// Percept Type needed to initialize participating agents
		agenttypes = new HashSet();
		for (int i = 0; i < allReferencedAgentTypesList.size(); i++) {
			// add all Agents
			agenttypes.add(allReferencedAgentTypesList.get(i));
		}

//		perceptType = new PerceptType();
//		perceptType.setName("coordinate_init:participants");
//		perceptType.setAgentTypes(agenttypes);
//		perceptType.setObjectTypes(null);
//		space.addPerceptType(perceptType);

		IPerceptGenerator perceptGenerator = new DefaultCoordinationEventGenerator();
		// put the percepts in the property as:
		// 1: every percept is a String[] which consists of the name of the
		// percept (the first position within the Array) followed by the actionTypes.
//		perceptGenerator.setProperty("percepttypes", new Object[] { new String[] { "coordinate_init:participants", "coordination_init_participants" },
//				new String[] { "coordination_event", "coordinate_percept" } });
		perceptGenerator.setProperty("percepttypes", new Object[] { new String[] { "coordination_event", "coordinate_percept" } });

		space.addPerceptGenerator(new String("generator"), perceptGenerator);
		// TODO: Check which perceptProcessors fits which AgentType?
		IPerceptProcessor perceptProcessor = new BDICoordinationInformationInterpreter();
//		perceptProcessor.setProperty("percepttypes", new Object[] { new String[] { "coordinate_init:participants", "coordinate:Init_Participants", "init_deco4mas_coordination" },
//				new String[] { "coordination_event", "coordinate", "anything..." } });
		perceptProcessor.setProperty("percepttypes", new Object[] { new String[] { "coordination_event", "coordinate", "anything..." } });

		// Add Percept Processor to all Agents
		for (String agentType : allReferencedAgentTypesList) {
			((AbstractEnvironmentSpace) space).addPerceptProcessor(agentType, null, perceptProcessor);
		}


		//TODO: Switch/Change this with respect to the "new" architectture: every agent is processing himself the MASModell!! to find out which things have to be initialized.
//		((CoordinationSpace) space).initParticipatingAgents(masDyn, fromReferencedAgentTypesList, toReferencedAgentTypesList);
//		((CoordinationSpace) space).initParticipatingAgents(masDyn);

		// TODO: Be able to do a few calls "((CoordinationSpace)space).initParticipatingAgents();"
		// without initializing (--> new BDIBehaviorObservationComponent(getExternalAccess());) every time!
		
		return masDyn;
	}

//	/**
//	 * Returns a list, that contains each agent name at most one time. Method can apply this filter on all "fromAgent" or all
//	 * "toAgents".
//	 * 
//	 * @param list
//	 * @param agentTypes
//	 * @return the filtered list.
//	 */
//	private ArrayList<String> filterAgentNames(ArrayList<CoordinationInfo> list, String agentTypes) {
//		HashMap<String, Object> tmp = new HashMap<String, Object>();
//		ArrayList<String> res = new ArrayList<String>();
//
//		for (int i = 0; i < list.size(); i++) {
//			CoordinationInfo coordInfo = list.get(i);
//			if (agentTypes.equals("fromAgents")) {
//				// HashMap doesn't contain agent type, yet.
//				if (tmp.get(coordInfo.getValueByName(CoordinationInfo.AGENT_TYPE)) == null) {
//					res.add(coordInfo.getValueByName(CoordinationInfo.AGENT_TYPE).toString());
//					tmp.put(coordInfo.getValueByName(CoordinationInfo.AGENT_TYPE).toString(), "anything");
//				}
//			} else if (agentTypes.equals("toAgents")) {
//				ArrayList<AgentElement> toAgentsList = (ArrayList<AgentElement>) coordInfo.getValueByName(CoordinationInfo.COLLECTION_OF_TOAGENTS);
//				for (int j = 0; j < toAgentsList.size(); j++) {
//					AgentElement toAgent = toAgentsList.get(j);
//					// HashMap doesn't contain agent type, yet.
//					if (tmp.get(toAgent.getAgent_id()) == null) {
//						res.add(toAgent.getAgent_id());
//						tmp.put(toAgent.getAgent_id(), "anything");
//					}
//				}
//			}
//		}
//		return res;
//	}

//	/**
//	 * This method takes two lists and returns one list that contains the union from the members of both list.
//	 * 
//	 * @param a
//	 * @param b
//	 * @return the union
//	 */
//	private ArrayList<String> filterLists(ArrayList<String> a, ArrayList<String> b) {
//		HashMap<String, Object> tmp = new HashMap<String, Object>();
//		ArrayList<String> res = new ArrayList<String>();
//
//		// copy all values from list "a" to a HashMap and resultList
//		for (int i = 0; i < a.size(); i++) {
//			tmp.put(a.get(i), "anything");
//			res.add(a.get(i));
//		}
//
//		// compare whether the elements from list "b" are already in list "a". if not, add element to list "a".
//		for (int i = 0; i < b.size(); i++) {
//			if (tmp.get(b.get(i)) == null) {
//				res.add(b.get(i));
//			}
//		}
//		return res;
//	}

	/**
	 * Takes the current MAS-Model and returns a list which contains each referenced agent only one time.
	 * 
	 * @return
	 */
	private void getAgentLists(MASDynamics dyn, ArrayList<String> allAgentNames, ArrayList<String> fromAgents, ArrayList<String> toAgents) {
		ArrayList<DecentralizedCausality> decentralCausalities = dyn.getCausalities().getRealizations();
		HashMap<String, Object> tmpAll = new HashMap<String, Object>();
		HashMap<String, Object> tmpFrom = new HashMap<String, Object>();
		HashMap<String, Object> tmpTo = new HashMap<String, Object>();
		ArrayList<String> res = new ArrayList<String>();

		for (DecentralizedCausality decCause : dyn.getCausalities().getRealizations()) {
			
			// get unique list of "fromAgents"
			for (AgentElement fromAgent : decCause.getFrom_agents()) {
				if (!tmpAll.containsKey(fromAgent.getAgent_id())) {
					allAgentNames.add(fromAgent.getAgent_id());
					tmpAll.put(fromAgent.getAgent_id(), "anything...");
				}
				
				if (!tmpFrom.containsKey(fromAgent.getAgent_id())) {
					fromAgents.add(fromAgent.getAgent_id());
					tmpFrom.put(fromAgent.getAgent_id(), "anything...");
				}
			}
		
			// get unique list of "toAgents"
			for (AgentElement toAgent : decCause.getTo_agents()) {
				if (!tmpAll.containsKey(toAgent.getAgent_id())) {
					allAgentNames.add(toAgent.getAgent_id());
					tmpAll.put(toAgent.getAgent_id(), "anything...");
				}
				
				if (!tmpTo.containsKey(toAgent.getAgent_id())) {
					toAgents.add(toAgent.getAgent_id());
					tmpTo.put(toAgent.getAgent_id(), "anything...");
				}
			}
		}
	}

}
