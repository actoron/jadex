package deco4mas.coordinate;

import jadex.bdi.runtime.IBDIExternalAccess;
import jadex.bdi.runtime.impl.AgentFlyweight;
import jadex.bridge.IComponentManagementService;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import javax.xml.bind.JAXBException;

import deco.lang.dynamics.MASDynamics;
import deco.lang.dynamics.causalities.DecentralMechanismLink;
import deco.lang.dynamics.causalities.DirectLink;
import deco.lang.dynamics.mechanism.AgentElement;
import deco.lang.dynamics.mechanism.DecentralizedCausality;
import deco.lang.dynamics.properties.AgentReference;
import deco.lang.dynamics.properties.ElementReference;
import deco.lang.dynamics.properties.GroupMembership;
import deco.lang.dynamics.properties.PropertyCondition;
import deco.lang.dynamics.properties.RoleOccupation;
import deco.lang.dynamics.properties.SystemProperty;
import deco4mas.annotation.agent.AgentCoordinationConfiguration;
import deco4mas.annotation.agent.CoordinationAnnotation;
import deco4mas.annotation.agent.CoordinationInhibition;
import deco4mas.annotation.agent.DataMapping;
import deco4mas.annotation.agent.ParameterMapping;
import deco4mas.annotation.agent.CoordinationAnnotation.DirectionType;
import deco4mas.coordinate.environment.CoordinationSpaceObject;
import deco4mas.mechanism.CoordinationInfo;

/**
 * Processing a given MASDynamic model: <br>
 * - MASDynamics are read in, <br>
 * - the agent relevant information are extracted, <br>
 * - the required capabilities are loaded <br>
 * - and parameterized.
 * 
 * @author Ante Vilenica & Jan Sudeikat
 * 
 */
@SuppressWarnings("serial")
public class ProcessMASDynamics {

	// /**
	// * A reference to the file that should be processed.
	// *
	// * @param fileName
	// */
	// public ProcessMASDynamics(String deco4MASFileName) {
	// this.deco4MASFileName = deco4MASFileName;
	// }

	/**
	 * The processed model and exta of the agent.
	 * 
	 * @param fileName
	 */
	public ProcessMASDynamics(IBDIExternalAccess exta, MASDynamics masDynamics) {
		this.exta = exta;
		this.dyn = masDynamics;
	}

	// ----------attributes----------

	// /** A reference to the file that should be processed. */
	// private String deco4MASFileName = null;

	/** The dynamics model. */
	private MASDynamics dyn;

	/** The surrounding agent model. */
	private AgentFlyweight rbdia;

	/** External access to the agent. */
	private IBDIExternalAccess exta;

	/** The id of the surrounding agent (unique). */
	private String agent_model_id;

	/** The (read-in) perceptions: incoming (decentralized) links. */
	private ArrayList<DecentralCoordinationInformation> perceptions = new ArrayList<DecentralCoordinationInformation>();

	/** The (read-in) publications: outgoing (decentralized) links. */
	private ArrayList<DecentralCoordinationInformation> publications = new ArrayList<DecentralCoordinationInformation>();

	/** The (read-in) perceptions: incoming (direct) links. */
	private ArrayList<DirectCoordinationInformation> direct_perceptions = new ArrayList<DirectCoordinationInformation>();

	/** The (read-in) publications: outgoing (direct) links. */
	private ArrayList<DirectCoordinationInformation> direct_publications = new ArrayList<DirectCoordinationInformation>();

	/** The complete annotation model for the agent. */
	private AgentCoordinationConfiguration acc;

	/**
	 * The identifiers of the loaded coordination mechanisms. This list is used to ensure that each coordination link (unique
	 * realization id) is only loaded once.
	 */
	private ArrayList<String> loaded_coordination_realizations = new ArrayList<String>();

	// ----------methods-------------

	/**
	 * configuring agent coordination....
	 * 
	 */

	/**
	 * Returns the MASDynamics Model for the specified file name.
	 */
	public static MASDynamics getMASDynamicsModell(String fileName) {
		if (fileName.length() > 0)
			try {
				return (MASDynamics) deco4mas.util.xml.XmlUtil.retrieveFromXML(MASDynamics.class, fileName);
			} catch (FileNotFoundException e) {
				System.out.println("#ProcessMASDynamics#" + ":");
				System.out.println("\t file: " + fileName + " could not be found...");
				e.printStackTrace();
			} catch (JAXBException e) {
				System.out.println("#ProcessMASDynamics#" + ":");
				System.out.println("\t file: " + fileName + " could not be processed...");
				e.printStackTrace();
			}
		return null;
	}

	public void process() {

//		System.out.println("#ProcessMASDynamicsPlan# Started processDynamics initialization for : " + exta.getAgentName());
		ArrayList<CoordinationInfo> res = new ArrayList<CoordinationInfo>();
		
		if (exta != null) {
//			agent_model_id = exta.getApplicationContext().getAgentType(exta.getAgentIdentifier());
			agent_model_id = exta.getModel().getName();
		}
//		System.out.println("Roles...");
//		deco.lang.dynamics.Properties myProp = dyn.getProperties();
//		for (RoleOccupation role : myProp.getRole_properties()) {
//			for (AgentReference ref : role.getAgentReferences()) {
//
//				for (ElementReference elRef : ref.getElements()) {
//					System.out.println("AgentID: " + ref.getAgent_id() + ": " + elRef.getAgent_element_type() + " - " + elRef.getElement_id());
//				}
//				ref.getAgent_id();
//			}
//		}
//		System.out.println("....Roles");

//		ArrayList<DecentralizedCausality> decentralCausalities = dyn.getCausalities().getRealizations();
//
//		for (int i = 0; i < decentralCausalities.size(); i++) {
//			DecentralizedCausality causality = decentralCausalities.get(i);
//			ArrayList<AgentElement> fromAgents = causality.getFrom_agents();
//			ArrayList<AgentElement> toAgents = causality.getTo_agents();
//			System.out.println("Right id? : " + causality.getId());
//			// getAgentDeatails(fromAgents);
//			// getAgentDeatails(toAgents);
//
//			// HACK!!! Put this outside the for-loop!!!
//			res = extractInformationForAgentInit(fromAgents, toAgents);
//		}

//		// ------------------TEST ---------------------
//		ArrayList<DecentralMechanismLink> decentralMechanismLinks = dyn.getCausalities().getDml();
//		for (DecentralMechanismLink directLink : decentralMechanismLinks) {
//			System.out.println("#TEST1# Name: " + directLink.getName());
//			System.out.println("#TEST1# Realization: " + directLink.getRealization());
//			System.out.println("#TEST1# Flow: " + directLink.getFlow());
//			System.out.println("#TEST1# Rate: " + directLink.getRate());
//			System.out.println("#TEST1# Type: " + directLink.getType());
//
//			ArrayList<String> tmpFrom = directLink.getFrom();
//			ArrayList<String> tmpTo = directLink.getTo();
//
//			for (String ab : tmpFrom) {
//				System.out.println("#TEST1# From: " + ab);
//			}
//
//			for (String ab : tmpTo) {
//				System.out.println("#TEST1# To: " + ab);
//			}
//
//		}
		// ------------------TEST


		// 1: create the overall configuration model for the agent (filled
		// later):

//		acc = new AgentCoordinationConfiguration();

		// acc.setAgent_name(agent_model_id);

		// 2: search for relevant links, i.e. where the agent model is
		// participating (as sender/receiver):

		// 2.a handle decentralized links:
		for (DecentralMechanismLink dml : dyn.getCausalities().getDml()) {

			if (dml.getRealization().length() > 0) {

				System.out.println("#ProccssMASDynamics#" +
				// this.getCapability().getName() + ") in agent: " +
						// this.getAgentName() +
						" Processes decentralized interdependency: " + dml.getName() + " at AgentType: " + this.agent_model_id);
				// TODO: use logging framework to control the output of debug information

				for (String s : dml.getFrom()) {
//					System.out.println("Yes: " + this.dyn.getProperties().getProperty(s));
					extractDecentralCoordinationInformation(publications, dml, this.dyn.getProperties().getProperty(s), DirectionType.PUBLICATION);
				}

				for (String s : dml.getTo()) {
					extractDecentralCoordinationInformation(perceptions, dml, this.dyn.getProperties().getProperty(s), DirectionType.PERCEPTION);
				}
			}
		}
		// TODO: Check this part!

		// process the found (decentralized) coordination information:
//		for (DecentralCoordinationInformation ci : publications) {
			// System.out.println("configuring publications:" +
			// ci.getDml().getName() );
			// loadAndConfigureCapability(ci);
//		}
//		for (DecentralCoordinationInformation ci : perceptions) {
			// System.out.println("configuring perceptions:" +
			// ci.getDml().getName() );
			// loadAndConfigureCapability(ci);
//		}

		// 2.b handle direct links:
//		for (DirectLink dl : dyn.getCausalities().getDirectLinks()) {
//			if (dl.getRealization() != null) {
//				// System.out.println(this.getCapability().getName() + ":");
//				System.out.println("\t Processing direct interdependency: " + dl.getName());
//				for (String s : dl.getFrom()) {
//					System.out.println("\t add (direct) perception: " + dl.getName() + " - Sender: " + s);
//					extractDirectCoordinationInformation(direct_publications, dl, this.dyn.getProperties().getProperty(s), DirectionType.PUBLICATION);
//				}
//
//				for (String s : dl.getTo()) {
//					System.out.println("\t add (direct) publication: " + dl.getName() + " - Receiver: " + s);
//					extractDirectCoordinationInformation(direct_perceptions, dl, this.dyn.getProperties().getProperty(s), DirectionType.PERCEPTION);
//				}
//			}
//		}

		// TODO: Check this part!

		// process the found (direct) coordination information:
//		for (DirectCoordinationInformation ci : direct_publications) {
			// System.out.println("configuring publications (direct) :" +
			// ci.getDirectLink().getName() );
			// loadDirectCoordinationCapability(ci);
//		}
//		for (DirectCoordinationInformation ci : direct_perceptions) {
			// System.out.println("configuring perceptions (direct) :" +
			// ci.getDirectLink().getName() );
			// loadDirectCoordinationCapability(ci);
//		}

		// 3: handle additional agent annotations, which are present in
		// the agent model:
		// these are appended to the annotations that are generated by an
		// explicitly specified coordination model.
		// coordination mechanisms are not loaded explicitly for these
		// annotations.
		// These need to be specified explicitly in the agent type
		// declaration.

		// if (rbdia.getPropertybase() != null) { // search for annotations
		// // in the property base:
		// for (String s : rbdia.getPropertybase().getPropertyNames("")) {
		// if (rbdia.getPropertybase().getProperty(s) != null
		// && this.getPropertybase().getProperty(s) instanceof
		// CoordinationAnnotation) {
		// CoordinationAnnotation ca = (CoordinationAnnotation)
		// rbdia.getPropertybase().getProperty(s);
		//			
		// if (ca.getDirection().equals(DirectionType.PERCEPTION)){ // book
		// // keeping for complete coordination model
		// this.acc.addPerception(ca);
		// }
		// if (ca.getDirection().equals(DirectionType.PUBLICATION)){
		// this.acc.addPuplication(ca);
		// }
		// this.annotateAgent(ca,rbdia);
		// }
		// }
		// }

		// 5: store the complete annotation model:

		// this.getBeliefbase().getBelief("agent::coordination::configuration").setFact(acc);

		// console output:
//		System.out.println(": (of type \"" + agent_model_id + "\")");
//		System.out.println("\t decentralized coordination configuration processed [" + exta.getAgentName() + "]!");
//		System.out.println("\t loaded/configured coordination mechanisms:");
//		int index = 0;
//		for (CoordinationAnnotation ca : acc.getPerceptions()) {
//			System.out.println("\t\t" + index++ + ": " + " direction: " + ca.getDirection());
//		}
//		for (CoordinationAnnotation ca : acc.getPublications()) {
//
//			System.out.println("\t\t" + index++ + ": " + " direction: " + ca.getDirection());
//		}
//		//
//		return res;
	}

	// /**
	// *
	// * 3.a: load the direct coordination capability <br>
	// * 3.b: create the required coordination annotations
	// *
	// * @param ci
	// */
	// private void
	// loadDirectCoordinationCapability(DirectCoordinationInformation ci) {
	//	
	// // fetch link configuration:
	// String direct_link_configuration_id =
	// ci.getDirectLink().getRealization();
	// DirectCausality direct_link_configuration =
	// dyn.getCausalities().getDirectLinkRealizationByName(direct_link_configuration_id);
	//			
	// // store in the right list:
	// if (ci.getDirection().equals(DirectionType.PERCEPTION)){
	// for (AgentElement ae : direct_link_configuration.getTo_agents()){
	// System.out.println("agent: " + ae.getAgent_id());
	// System.out.println("agent: " + agent_model_id);
	// if (ae.getAgent_id().equals(agent_model_id)){ // right agent involved
	// CoordinationAnnotation ca = createCoordinationAnnotation(ci, ae);
	// acc.addPerception(ca); // book keeping for complete agent model
	// annotateAgent(ca, rbdia,direct_link_configuration_id);
	// }
	// }
	// }
	// else if (ci.getDirection().equals(DirectionType.PUBLICATION)){
	// for (AgentElement ae : direct_link_configuration.getFrom_agents()){
	// if (ae.getAgent_id().equals(agent_model_id)){ // right agent involved
	// CoordinationAnnotation ca = createCoordinationAnnotation(ci, ae);
	// acc.addPuplication(ca); // book keeping for complete agent model
	// annotateAgent(ca, rbdia,direct_link_configuration_id);
	// }
	// }
	// }
	//			
	// // load an configure the mechanism-specific coordination end-point
	// implementation:
	// if (!
	// this.loaded_coordination_realizations.contains(direct_link_configuration_id))
	// { // this is done only once for the specific end-point implementation
	//				
	// // fetch configuration:
	// DirectLinkConfiguration mc_model =
	// direct_link_configuration.getConfiguration();
	//	
	// // store configuration in surrounding agent:
	// IMBeliefbase b_base =
	// (IMBeliefbase)rbdia.getBeliefbase().getModelElement();
	// IMBelief b_type = b_base.createBelief(direct_link_configuration_id +
	// "::mech_conf", DirectLinkConfiguration.class, -1, "true");
	// rbdia.getBeliefbase().registerBelief(b_type);
	// rbdia.getCapability(direct_link_configuration_id).getBeliefbase().getBelief(direct_link_configuration_id+
	// "::mech_conf").setFact(mc_model);
	//	
	// // load capability [includes automated initialization]:
	// String direct_coord_model =
	// "deco4mas.mechanism.direct.messagebased.Direct"; // fully qualified
	// capability name
	// IMCapability cap = (IMCapability)rbdia.getModelElement();
	// IMCapabilityReference subcap =
	// cap.createCapabilityReference(direct_link_configuration_id,direct_coord_model);
	// rbdia.getScope().registerSubcapability(subcap);
	//			
	// }
	//			
	// if (ci.getDirection().equals(DirectionType.PUBLICATION)){ // register:
	// the goals for publication:
	//				
	// IMGoalbase g_base = (IMGoalbase) rbdia.getGoalbase().getModelElement();
	// IMAchieveGoalReference g_ref =
	// g_base.createAchieveGoalReference("publish", "true",
	// direct_link_configuration_id + ".publish");
	// rbdia.getGoalbase().registerGoalReference(g_ref);
	// }
	//			
	// // finally, remember that his model link/mechanism has already been
	// configured:
	// this.loaded_coordination_realizations.add(direct_link_configuration_id);
	//			
	// }

	// /**
	// * 3.a: load the referenced capabilities <br>
	// * 3.b: create the required coordination annotations
	// *
	// * @param ci
	// */
	// private void loadAndConfigureCapability(DecentralCoordinationInformation
	// ci) {
	//		
	// // fetch the model data for the utilized coordination mechanism:
	// String mech_id = ci.getDml().getRealization();
	//		
	// DecentralizedCausality dcm_realization =
	// dyn.getCausalities().getDCMRealizationByName(mech_id);
	// if (dcm_realization == null) { // check if realization is available
	// throw new
	// IllegalArgumentException("No mechanism realization with the name: " +
	// mech_id + " is available");
	// }
	//		
	// String mech_model =
	// dcm_realization.getMechanismConfiguration().getMechanism_id();
	//		
	// // here, Jadex-based implementation -> check agent type:
	// if
	// (dcm_realization.getMechanismConfiguration().getAgent_type().equals(AgentModelType.JADEX)){
	//
	// // store in the right list:
	// if (ci.getDirection().equals(DirectionType.PERCEPTION)){
	// for (AgentElement ae : dcm_realization.getTo_agents()){
	// if (ae.getAgent_id().equals(agent_model_id)){ // right agent involved
	// CoordinationAnnotation ca = createCoordinationAnnotation(ci, ae);
	// acc.addPerception(ca); // book keeping for complete agent model
	// annotateAgent(ca, rbdia, mech_id);
	// }
	// }
	// }
	// else if (ci.getDirection().equals(DirectionType.PUBLICATION)){
	// for (AgentElement ae : dcm_realization.getFrom_agents()){
	// if (ae.getAgent_id().equals(agent_model_id)){ // right agent involved
	// CoordinationAnnotation ca = createCoordinationAnnotation(ci, ae);
	// acc.addPuplication(ca); // book keeping for complete agent model
	// annotateAgent(ca, rbdia, mech_id);
	// }
	// }
	// }
	//			
	// // load an configure the mechanism-specific coordination end-point
	// implementation:
	// if (! this.loaded_coordination_realizations.contains(mech_id)) { // this
	// is done only once for the specific end-point implementation
	//			
	// // create the mechanism configuration:
	// deco.lang.dynamics.mechanism.MechanismConfiguration mc_model =
	// dcm_realization.getMechanismConfiguration();
	//
	// // store configuration in surrounding agent model:
	// IMBeliefbase b_base =
	// (IMBeliefbase)rbdia.getBeliefbase().getModelElement();
	// IMBelief b_type = b_base.createBelief(mech_id+ "::mech_conf",
	// deco.lang.dynamics.mechanism.MechanismConfiguration.class, -1, "true");
	// rbdia.getBeliefbase().registerBelief(b_type);
	// rbdia.getCapability(mech_id).getBeliefbase().getBelief(mech_id+
	// "::mech_conf").setFact(mc_model);
	//
	// // load capability [includes automated initialization]:
	// IMCapability cap = (IMCapability)rbdia.getModelElement();
	// IMCapabilityReference subcap =
	// cap.createCapabilityReference(mech_id,mech_model);
	// rbdia.getScope().registerSubcapability(subcap);
	//			
	// }
	//						
	// if (ci.getDirection().equals(DirectionType.PUBLICATION)){ // register:
	// the goals for publication:
	// IMGoalbase g_base = (IMGoalbase) rbdia.getGoalbase().getModelElement();
	// IMAchieveGoalReference g_ref =
	// g_base.createAchieveGoalReference("publish", "true", mech_id +
	// ".publish");
	// rbdia.getGoalbase().registerGoalReference(g_ref);
	// }
	//			
	// // finally, remember that his model link/mechanism has already been
	// configured:
	// this.loaded_coordination_realizations.add(mech_id);
	//			
	// }
	// }
	//
	// /**
	// * Annotate an agent model.
	// * With an externally specified mechanism id.
	// *
	// * The annotation is appended to existing configurations.
	// *
	// * @param ca The annotation to write.
	// * @param rbdia2 The agent to write to.
	// * @param mechanismConfiguration
	// */
	// private void annotateAgent(CoordinationAnnotation ca, AgentFlyweight
	// rbdia2, String mech_id) {
	// if (! rbdia.getBeliefbase().containsBeliefSet(mech_id +
	// "::agent_annotations")){
	// IMBeliefbase b_base =
	// (IMBeliefbase)rbdia.getBeliefbase().getModelElement();
	// IMBeliefSet b_type = b_base.createBeliefSet(mech_id +
	// "::agent_annotations", CoordinationAnnotation.class, -1, "true");
	// rbdia.getBeliefbase().registerBeliefSet(b_type);
	// }
	// rbdia.getBeliefbase().getBeliefSet(mech_id +
	// "::agent_annotations").addFact(ca);
	// }

	// /**
	// * Annotate an agent model.
	// * With an internally specified mechanism id.
	// * Only works if a mechanism ID has been specified.
	// *
	// * The annotation is appended to existing configurations.
	// *
	// * @param ca The annotation to write.
	// * @param rbdia2 The agent to write to.
	// */
	// private void annotateAgent(CoordinationAnnotation ca, AgentFlyweight
	// rbdia2) {
	// if (ca.getMechID() != null && ca.getMechID().length() > 0) {
	// if (! rbdia.getBeliefbase().containsBeliefSet(ca.getMechID() +
	// "::agent_annotations")){
	// IMBeliefbase b_base =
	// (IMBeliefbase)rbdia.getBeliefbase().getModelElement();
	// IMBeliefSet b_type = b_base.createBeliefSet(ca.getMechID() +
	// "::agent_annotations", CoordinationAnnotation.class, -1, "true");
	// rbdia.getBeliefbase().registerBeliefSet(b_type);
	// }
	// rbdia.getBeliefbase().getBeliefSet(ca.getMechID() +
	// "::agent_annotations").addFact(ca);
	// }
	//	
	// }
	//	
	/**
	 * Create annotation to the agent model:
	 * 
	 * @param ci
	 * @param ae
	 * @return
	 */
	private CoordinationAnnotation createCoordinationAnnotation(DecentralCoordinationInformation ci, AgentElement ae) {

		CoordinationAnnotation ca = new CoordinationAnnotation();

		ca.setType(ci.getDml().getName());
		ca.setElement_name(ae.getElement_id());
		ca.setElement_type(ae.getAgentElementType());
		ca.setDirection(ci.getDirection().toString());
		ca.setCoordinationType(ci.getCoordinationType());

		if (ci.getRef().getContraints() != null) { // constraints exist
			if (ci.getRef().getContraints().getCondition() != null) {
				PropertyCondition pc = ci.getRef().getContraints().getCondition();
				ca.setConditionName(pc.getName());
				ca.setConditionExpression(pc.getExpression());
			}

			if (ci.getRef().getContraints() != null) {
				if (ci.getRef().getContraints().getInhibitions() != null) { //
					// inhibitions exist
					ArrayList<ElementReference> inhibitions = ci.getRef().getContraints().getInhibitions();
					for (ElementReference e : inhibitions) {
						CoordinationInhibition coordinationInhibition = new CoordinationInhibition();
						coordinationInhibition.setElement_name(e.getElement_id());
						coordinationInhibition.setElement_type(e.getAgent_element_type().toString());
						ca.getConstraint().addInhibition(coordinationInhibition);

					}
				}
			}
		}

		for (ParameterMapping pm : ae.getParameter_mappings()) {
			ca.addParameter_mapping(pm);
		}

		for (DataMapping dm : ae.getData_mappings()) {
			ca.addDataMapping(dm);
		}

		if (ci.getDirection().equals(DirectionType.PERCEPTION)) {
			ca.setDirection(DirectionType.PERCEPTION.toString());
		}
		if (ci.getDirection().equals(DirectionType.PUBLICATION)) {
			ca.setDirection(DirectionType.PUBLICATION.toString());
		}

		return ca;
	}

	/**
	 * Create annotation to the agent model:
	 * 
	 * @param ci
	 * @param ae
	 * @return
	 */
	private CoordinationAnnotation createCoordinationAnnotation(DirectCoordinationInformation ci, AgentElement ae) {

		CoordinationAnnotation ca = new CoordinationAnnotation();

		ca.setType(ci.getDirectLink().getName());
		ca.setElement_name(ae.getElement_id());
		ca.setElement_type(ae.getAgentElementType());
		ca.setDirection(ci.getDirection().toString());
		ca.setCoordinationType(ci.getCoordinationType());

		if (ci.getRef().getContraints() != null) { // constraints exist
			if (ci.getRef().getContraints().getCondition() != null) {
				PropertyCondition pc = ci.getRef().getContraints().getCondition();
				ca.setConditionName(pc.getName());
				ca.setConditionExpression(pc.getExpression());
			}

			if (ci.getRef().getContraints() != null) {
				if (ci.getRef().getContraints().getInhibitions() != null) { //
					// inhibitions exist
					ArrayList<ElementReference> inhibitions = ci.getRef().getContraints().getInhibitions();
					for (ElementReference e : inhibitions) {
						CoordinationInhibition coordinationInhibition = new CoordinationInhibition();
						coordinationInhibition.setElement_name(e.getElement_id());
						coordinationInhibition.setElement_type(e.getAgent_element_type().toString());
						ca.getConstraint().addInhibition(coordinationInhibition);

					}
				}
			}
		}

		for (ParameterMapping pm : ae.getParameter_mappings()) {
			ca.addParameter_mapping(pm);
		}

		for (DataMapping dm : ae.getData_mappings()) {
			ca.addDataMapping(dm);
		}

		if (ci.getDirection().equals(DirectionType.PERCEPTION)) {
			ca.setDirection(DirectionType.PERCEPTION.toString());
		}
		if (ci.getDirection().equals(DirectionType.PUBLICATION)) {
			ca.setDirection(DirectionType.PUBLICATION.toString());
		}

		return ca;
	} // TODO: refactor createCoordinationAnnotation()

	/**
	 * Retrieve coordination information from "System Properties" connections (decentralized). Here, it is checked whether the
	 * surrounding agent has to participate in the declared coordination. The relevant link configurations (only) are stored in
	 * ArrayLists for later processing.
	 * 
	 * @param co_inf_list
	 * @param dml
	 * @param sp
	 */
	private void extractDecentralCoordinationInformation(ArrayList<DecentralCoordinationInformation> co_inf_list, DecentralMechanismLink dml, SystemProperty sp, DirectionType direction) {

		if (sp != null) { // exists:
			if (sp instanceof RoleOccupation) {
				RoleOccupation ro = (RoleOccupation) sp;
				for (AgentReference ar : ro.getAgentReferences()) {
					if (ar.getAgent_id().equals(agent_model_id)) {// filter for elements of the surrounding agent store
						// perception:
						co_inf_list.add(new DecentralCoordinationInformation(direction, ar, dml));
					}
				}
			}
			if (sp instanceof GroupMembership) { // "Group" are GroupMembership
				// as well
				GroupMembership gm = (GroupMembership) sp;
				for (AgentReference ar : gm.getAgent_elements()) {
					if (ar.getAgent_id().equalsIgnoreCase(agent_model_id)) { // filter
						// for
						// elements
						// of
						// this
						// agent
						// store perception:
						co_inf_list.add(new DecentralCoordinationInformation(direction, ar, dml));
					}
				}
			}
		}
	} // TODO: refactor extractCoordinationInformation()

	/**
	 * Retrieve coordination information from "System Property" connections (direct). Here, it is checked whether the surrounding
	 * agent has to participate in the declared coordination. The relevant link configurations (only) are stored in ArrayLists for
	 * later processing.
	 * 
	 * @param direct_coord_informations
	 * @param dl
	 * @param property
	 * @param direction
	 */
	private void extractDirectCoordinationInformation(ArrayList<DirectCoordinationInformation> direct_coord_informations, DirectLink dl, SystemProperty property, DirectionType direction) {

		if (property != null) { // exists:
			if (property instanceof RoleOccupation) {
				RoleOccupation ro = (RoleOccupation) property;
				for (AgentReference ar : ro.getAgentReferences()) {
					if (ar.getAgent_id().equals(agent_model_id)) { // filter for
						// elements
						// of the
						// surrounding
						// agent
						// store perception:
						direct_coord_informations.add(new DirectCoordinationInformation(direction, ar, dl));
					}
				}
			}
			if (property instanceof GroupMembership) { // "Group" are
				// GroupMembership as
				// well
				GroupMembership gm = (GroupMembership) property;
				for (AgentReference ar : gm.getAgent_elements()) {
					if (ar.getAgent_id().equalsIgnoreCase(agent_model_id)) { // filter
						// for
						// elements
						// of
						// this
						// agent
						// store perception:
						direct_coord_informations.add(new DirectCoordinationInformation(direction, ar, dl));
					}
				}
			}
		}

	} // TODO: refactor extractCoordinationInformation()

	// private void getAgentDeatails(ArrayList<AgentElement> agents) {
	// for (int i = 0; i < agents.size(); i++) {
	// AgentElement agent = agents.get(i);
	// System.out.println("#InitMgr# XML file: " + agent.getAgent_id() + " , " +
	// agent.getAgentElementType() + " , " + agent.getElement_id());
	// }
	// }

	private ArrayList<CoordinationInfo> extractInformationForAgentInit(ArrayList<AgentElement> fromAgents, ArrayList<AgentElement> toAgents) {
		ArrayList<CoordinationInfo> res = new ArrayList<CoordinationInfo>();

		for (int i = 0; i < fromAgents.size(); i++) {
			AgentElement agent = fromAgents.get(i);
			// System.out.println("#InitMgr# XML file: " + agent.getAgent_id() +
			// " , " + agent.getAgentElementType() + " , " +
			// agent.getElement_id());
			CoordinationInfo coordInfo = new CoordinationInfo();
			coordInfo.setName("Init-Deco4MAS-Coordination");
			coordInfo.setType(CoordinationSpaceObject.COORDINATION_INFORMATION_TYPE);
			coordInfo.addValue(CoordinationInfo.AGENT_TYPE, agent.getAgent_id());
			coordInfo.addValue(CoordinationInfo.AGENT_ELEMENT_TYPE, agent.getAgentElementType());
			coordInfo.addValue(CoordinationInfo.AGENT_ELEMENT_NAME, agent.getElement_id());
			coordInfo.addValue(CoordinationInfo.COLLECTION_OF_TOAGENTS, toAgents);
			res.add(coordInfo);
		}
		return res;
	}

	/**
	 * @return the perceptions
	 */
	public ArrayList<DecentralCoordinationInformation> getPerceptions() {
		return perceptions;
	}

	/**
	 * @return the publications
	 */
	public ArrayList<DecentralCoordinationInformation> getPublications() {
		return publications;
	}

}