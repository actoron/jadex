package deco4mas.distributed.annotation.agent;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import deco4mas.distributed.annotation.agent.CoordinationAnnotation.DirectionType;

/**
 * The coordination configuration of a single agent.<br>
 * <br>
 * Annotations specify which publications / perceptions are to be enabled.
 * These actions describe how individual agents participate in decentralized coordination.
 * 
 * @author Jan Sudeikat
 *
 */
@XmlRootElement(name="agent_coordination_configuration")
public class AgentCoordinationConfiguration {
	
	//----------attributes----------
	
	/** The perceptions. */
	protected ArrayList<CoordinationAnnotation> perceptions;
	
	/** The perceptions. */
	protected ArrayList<CoordinationAnnotation> publications;
	
	/** The name of the configuration. */
	protected String name;
	
	/** The name of the agent that is configured. */
	protected String agent_name;
	
	//----------constructors--------

	public AgentCoordinationConfiguration() {
		super();
		this.perceptions = new ArrayList<CoordinationAnnotation>();
		this.publications = new ArrayList<CoordinationAnnotation>();
	}
	
	public AgentCoordinationConfiguration(String name) {
		super();
		this.name = name;
		this.perceptions = new ArrayList<CoordinationAnnotation>();
		this.publications = new ArrayList<CoordinationAnnotation>();
	}
	
	public AgentCoordinationConfiguration(
			ArrayList<CoordinationAnnotation> perceptions,
			ArrayList<CoordinationAnnotation> publications, String name) {
		super();
		this.perceptions = perceptions;
		this.publications = publications;
		this.name = name;
	}
	
	public AgentCoordinationConfiguration getXMLConfiguration(String file_name){
		try {
			return (AgentCoordinationConfiguration) deco4mas.distributed.util.xml.XmlUtil.retrieveFromXML(AgentCoordinationConfiguration.class, file_name);
		} catch (FileNotFoundException e) {
			System.err.println("Error: can not read specified configuration file....");
			e.printStackTrace();
		} catch (JAXBException e) {
			System.err.println("Error: can not extract configuration information from specified file....");
			e.printStackTrace();
		}
		return null;
	}

	//----------methods-------------
	
	@XmlElementWrapper(name="perceptions")
	@XmlElement(name="perception")
	public ArrayList<CoordinationAnnotation> getPerceptions() {
		return perceptions;
	}

	public void setPerceptions(ArrayList<CoordinationAnnotation> perceptions) {
		this.perceptions = perceptions;
	}
	
	public void addPerception(CoordinationAnnotation ca){
		this.perceptions.add(ca);
	}
	
	public void removePerception(CoordinationAnnotation ca) {
		this.perceptions.remove(ca);
	}

	@XmlElementWrapper(name="publications")
	@XmlElement(name="publication")
	public ArrayList<CoordinationAnnotation> getPublications() {
		return publications;
	}

	public void setPublications(ArrayList<CoordinationAnnotation> publications) {
		this.publications = publications;
	}
	
	public void addPuplication(CoordinationAnnotation ac){
		this.publications.add(ac);
	}
	
	public void removePuplication(CoordinationAnnotation ac){
		this.publications.remove(ac);
	}

	@XmlAttribute(name="name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getAgent_name() {
		return agent_name;
	}

	public void setAgent_name(String agent_name) {
		this.agent_name = agent_name;
	}

	/**
	 * Check whether an annotation is present in this configuration.
	 * [Automatically distinguishes the directions (publish / perceive)]. 
	 * 
	 * @param co_an			the annotation to search for
	 * @return						whether the annotation is present
	 */
	public boolean containsAnnotation(CoordinationAnnotation co_an){
		ArrayList<CoordinationAnnotation> cas = new ArrayList<CoordinationAnnotation>();
		if (cas.size() < 1) return false;
		if (co_an.getDirection().equals(DirectionType.PERCEPTION.toString())){
			cas = this.perceptions;
		}
		if (co_an.getDirection().equals(DirectionType.PUBLICATION.toString())){
			cas = this.publications;
		}
		for (CoordinationAnnotation ca : cas){
			if (! ca.getDirection().equals(co_an.getDirection()) || 
					! ca.getType().equals(co_an.getType()) || 
					 ! ca.getElement_type().equals(co_an.getElement_type()) || 
					  ! ca.getElement_name().equals(co_an.getElement_name())){
				return false;
			}
		}
		return true;
	}
	
}