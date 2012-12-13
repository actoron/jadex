package deco4mas.distributed.coordinate;

import jadex.bridge.IExternalAccess;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import javax.xml.bind.JAXBException;

import deco.distributed.lang.dynamics.MASDynamics;
import deco.distributed.lang.dynamics.causalities.DecentralMechanismLink;
import deco.distributed.lang.dynamics.causalities.DirectLink;
import deco.distributed.lang.dynamics.properties.AgentReference;
import deco.distributed.lang.dynamics.properties.GroupMembership;
import deco.distributed.lang.dynamics.properties.RoleOccupation;
import deco.distributed.lang.dynamics.properties.SystemProperty;
import deco4mas.distributed.annotation.agent.CoordinationAnnotation.DirectionType;

/**
 * Processing a given MASDynamic model: <br>
 * - MASDynamics are read in, <br>
 * - the agent relevant information are extracted, <br>
 * - the required capabilities are loaded <br>
 * - and parameterized.
 * 
 * @author Ante Vilenica & Jan Sudeikat
 */
public class ProcessMASDynamics {

	/**
	 * The processed model and exta of the agent.
	 * 
	 * @param fileName
	 */
	public ProcessMASDynamics(IExternalAccess exta, MASDynamics masDynamics) {
		this.exta = exta;
		this.dyn = masDynamics;
	}

	// ----------attributes----------

	// /** A reference to the file that should be processed. */
	// private String deco4MASFileName = null;

	/** The dynamics model. */
	private MASDynamics dyn;

	/** External access to the agent. */
	private IExternalAccess exta;

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

	// ----------methods-------------

	/**
	 * configuring agent coordination....
	 */

	/**
	 * Returns the MASDynamics Model for the specified file name.
	 */
	public static MASDynamics getMASDynamicsModell(String fileName) {
		if (fileName.length() > 0)
			try {
				return (MASDynamics) deco4mas.distributed.util.xml.XmlUtil.retrieveFromXML(MASDynamics.class, fileName);
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
		if (exta != null) {
			agent_model_id = exta.getModel().getName();
		}

		for (DecentralMechanismLink dml : dyn.getCausalities().getDml()) {

			if (dml.getRealization().length() > 0) {

				System.out.println("#ProccssMASDynamics#" +
				// this.getCapability().getName() + ") in agent: " +
				// this.getAgentName() +
						" Processes decentralized interdependency: " + dml.getName() + " at AgentType: " + this.agent_model_id);
				// TODO: use logging framework to control the output of debug
				// information

				for (String s : dml.getFrom()) {
					// System.out.println("Yes: " +
					// this.dyn.getProperties().getProperty(s));
					extractDecentralCoordinationInformation(publications, dml, this.dyn.getProperties().getProperty(s), DirectionType.PUBLICATION);
				}

				for (String s : dml.getTo()) {
					extractDecentralCoordinationInformation(perceptions, dml, this.dyn.getProperties().getProperty(s), DirectionType.PERCEPTION);
				}
			}
		}

		for (DirectLink dl : dyn.getCausalities().getDirectLinks()) {
			if (dl.getRealization() != null) {
				// System.out.println(this.getCapability().getName() + ":");
				System.out.println("#ProccssMASDynamics#" + " Processes decentralized interdependency: " + dl.getName() + " at AgentType: " + this.agent_model_id);
				for (String s : dl.getFrom()) {

					extractDirectCoordinationInformation(direct_publications, dl, this.dyn.getProperties().getProperty(s), DirectionType.PUBLICATION);
				}

				for (String s : dl.getTo()) {
					extractDirectCoordinationInformation(direct_perceptions, dl, this.dyn.getProperties().getProperty(s), DirectionType.PERCEPTION);
				}
			}
		}
	}

	/**
	 * Retrieve coordination information from "System Properties" connections (decentralized). Here, it is checked whether the surrounding agent has to participate in the declared coordination. The
	 * relevant link configurations (only) are stored in ArrayLists for later processing.
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
					if (ar.getAgent_id().equals(agent_model_id)) { // filter for elements of the surrounding agent; store perception
						co_inf_list.add(new DecentralCoordinationInformation(direction, ar, dml));
					}
				}
			}
			if (sp instanceof GroupMembership) { // "Group" are GroupMembership as well
				GroupMembership gm = (GroupMembership) sp;
				for (AgentReference ar : gm.getAgent_elements()) {
					if (ar.getAgent_id().equalsIgnoreCase(agent_model_id)) { // filter for elements of this agent; store perception
						co_inf_list.add(new DecentralCoordinationInformation(direction, ar, dml));
					}
				}
			}
		}
	}

	/**
	 * Retrieve coordination information from "System Property" connections (direct). Here, it is checked whether the surrounding agent has to participate in the declared coordination. The relevant
	 * link configurations (only) are stored in ArrayLists for later processing.
	 * 
	 * @param direct_coord_informations
	 * @param dl
	 * @param sp
	 * @param direction
	 */
	private void extractDirectCoordinationInformation(ArrayList<DirectCoordinationInformation> direct_coord_informations, DirectLink dl, SystemProperty sp, DirectionType direction) {

		if (sp != null) { // exists:
			if (sp instanceof RoleOccupation) {

				RoleOccupation ro = (RoleOccupation) sp;
				for (AgentReference ar : ro.getAgentReferences()) {
					if (ar.getAgent_id().equals(agent_model_id)) {// filter for elements of the surrounding agent; store perception
						direct_coord_informations.add(new DirectCoordinationInformation(direction, ar, dl));
					}
				}

			}
			if (sp instanceof GroupMembership) { // "Group" are GroupMembership as well
				GroupMembership gm = (GroupMembership) sp;
				for (AgentReference ar : gm.getAgent_elements()) {
					if (ar.getAgent_id().equalsIgnoreCase(agent_model_id)) { // filter for elements of this agent; store perception
						direct_coord_informations.add(new DirectCoordinationInformation(direction, ar, dl));
					}
				}
			}
		}
	}

	/**
	 * @return the direct perceptions
	 */
	public ArrayList<DirectCoordinationInformation> getDirectPerceptions() {
		return direct_perceptions;
	}

	/**
	 * @return the direct publications
	 */
	public ArrayList<DirectCoordinationInformation> getDirectPublications() {
		return direct_publications;
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