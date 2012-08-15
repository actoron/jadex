package deco4mas.distributed.coordinate.environment;

import jadex.extension.envsupport.MObjectType;
import jadex.extension.envsupport.environment.AbstractEnvironmentSpace;
import jadex.extension.envsupport.environment.IEnvironmentSpace;
import jadex.extension.envsupport.environment.IPerceptGenerator;
import jadex.extension.envsupport.environment.IPerceptProcessor;
import jadex.extension.envsupport.environment.PerceptType;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.JAXBException;

import deco.distributed.lang.dynamics.MASDynamics;
import deco.distributed.lang.dynamics.causalities.DecentralMechanismLink;
import deco.distributed.lang.dynamics.causalities.DirectLink;
import deco.distributed.lang.dynamics.mechanism.AgentElement;
import deco.distributed.lang.dynamics.mechanism.DecentralizedCausality;
import deco.distributed.lang.dynamics.mechanism.MechanismConfiguration;
import deco4mas.distributed.coordinate.interpreter.coordination_information.DefaultCoordinationEventGenerator;
import deco4mas.distributed.coordinate.interpreter.coordination_information.DefaultCoordinationInformationInterpreter;
import deco4mas.distributed.mechanism.CoordinationMechanism;

/**
 * Responsible for starting all things necessary for deco4mas
 * 
 * @author Ante Vilenica & Thomas Preisler
 * 
 */
public class InitDeco4mas {

	private IEnvironmentSpace space = null;

	/** Reference to the used MAS-File. */
	private File masFile;

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

		masFile = new File("../" + space.getProperty("dynamics_configuration"));
		if (!masFile.exists()) {
			masFile = new File("./" + space.getProperty("dynamics_configuration"));
		}

		System.out.println("#InitCoordinationSpace-Thread# Started processing deco4mas-file: " + masFile.getPath());

		// Process deco4MAS-File:
		try {
			if (masFile.exists()) {
				// 1: fetch conf.:
				masDyn = (MASDynamics) deco4mas.distributed.util.xml.XmlUtil.retrieveFromXML(MASDynamics.class, masFile);
			}

		} catch (FileNotFoundException e) {
			System.out.println("#ProcessMASDynamics#" + ":");
			System.out.println("\t file: " + masFile.getPath() + " could not be found...");
			e.printStackTrace();
		} catch (JAXBException e) {
			System.out.println("#ProcessMASDynamics#" + ":");
			System.out.println("\t file: " + masFile.getPath() + " could not be processed...");
			e.printStackTrace();
		}

		for (DecentralMechanismLink dml : masDyn.getCausalities().getDml()) {
			DecentralizedCausality dc = masDyn.getCausalities().getRealizationByName(dml.getRealization());
			if (dc != null) {
				MechanismConfiguration mechanismConfiguration = dc.getMechanismConfiguration();
				CoordinationMechanism mechanism = getMechanism(mechanismConfiguration.getMechanism_id(), (CoordinationSpace) space);
				mechanism.setMechanismConfiguration(mechanismConfiguration);
				mechanism.setRealisationName(dml.getRealization());

				if (dc.getActive())
					((CoordinationSpace) space).getActiveCoordinationMechanisms().put(mechanism.getRealisationName(), mechanism);

				else if (!dc.getActive())
					((CoordinationSpace) space).getInactiveCoordinationMechanisms().put(mechanism.getRealisationName(), mechanism);
			}
		}

		// TODO do the same as above for the direct links, right now this did not seem to be supported...
		for (DirectLink dml : masDyn.getCausalities().getDirectLinks()) {
			CoordinationMechanism mechanism = getMechanism(dml.getRealization(), (CoordinationSpace) space);
			((CoordinationSpace) space).getActiveCoordinationMechanisms().put(mechanism.getRealisationName(), mechanism);
		}

		ArrayList<String> allReferencedAgentTypesList = new ArrayList<String>();
		ArrayList<String> fromReferencedAgentTypesList = new ArrayList<String>();
		ArrayList<String> toReferencedAgentTypesList = new ArrayList<String>();
		getAgentLists(masDyn, allReferencedAgentTypesList, fromReferencedAgentTypesList, toReferencedAgentTypesList);

		// -------------------- INIT Space Object TYPES
		// ------------------------------//
		// Init all types that may be needed for the deco4mas coordination.
		for (String typeName : allReferencedAgentTypesList) {
			MObjectType mobject = new MObjectType();
			mobject.setName(typeName);
			space.addSpaceObjectType(typeName, mobject);
		}

		// -------------- INIT Space Percepts! ------------//
		// Percept Type for usual coordination events: coordination_event
		Set<String> agenttypes = new HashSet<String>();
		for (int i = 0; i < toReferencedAgentTypesList.size(); i++) {
			// add the "toAgents"
			agenttypes.add(toReferencedAgentTypesList.get(i));
		}

		PerceptType perceptType = new PerceptType();
		perceptType.setName("coordination_event");
		perceptType.setComponentTypes(agenttypes);
		perceptType.setObjectTypes(null);
		space.addPerceptType(perceptType);

		IPerceptGenerator perceptGenerator = new DefaultCoordinationEventGenerator();
		perceptGenerator.setProperty("percepttypes", new Object[] { new String[] { "coordination_event", "coordinate_percept" } });

		space.addPerceptGenerator(new String("generator"), perceptGenerator);
		// TODO: Check which perceptProcessors fits which AgentType?
		IPerceptProcessor perceptProcessor = new DefaultCoordinationInformationInterpreter(masDyn);
		perceptProcessor.setProperty("percepttypes", new Object[] { new String[] { "coordination_event", "coordinate", "anything..." } });

		// Add Percept Processor to all Agents
		for (String agentType : allReferencedAgentTypesList) {
			((AbstractEnvironmentSpace) space).addPerceptProcessor(agentType, null, perceptProcessor);
		}

		return masDyn;
	}

	/**
	 * Takes the current MAS-Model and returns a list which contains each referenced agent only one time.
	 * 
	 * @return
	 */
	private void getAgentLists(MASDynamics dyn, ArrayList<String> allAgentNames, ArrayList<String> fromAgents, ArrayList<String> toAgents) {
		HashMap<String, Object> tmpAll = new HashMap<String, Object>();
		HashMap<String, Object> tmpFrom = new HashMap<String, Object>();
		HashMap<String, Object> tmpTo = new HashMap<String, Object>();

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

	/**
	 * Tries to load the {@link CoordinationMechanism} specified by the given full qualifies class name.
	 * 
	 * @param mechanismId
	 *            the full qualified class name of the coordination mechanism
	 * @param space
	 *            the coordination space
	 * @return the load {@link CoordinationMechanism} or <code>null</code> if no coordination mechanism could be loaded
	 */
	private CoordinationMechanism getMechanism(String mechanismId, CoordinationSpace space) {
		try {
			@SuppressWarnings("unchecked")
			Class<CoordinationMechanism> mechanismClass = (Class<CoordinationMechanism>) Class.forName(mechanismId);
			CoordinationMechanism mechanism = mechanismClass.getConstructor(CoordinationSpace.class).newInstance(space);
			return mechanism;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}

		return null;
	}
}