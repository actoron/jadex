package deco.lang.dynamics;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import deco.lang.dynamics.properties.AgentReference;
import deco.lang.dynamics.properties.EnvrionmentProperty;
import deco.lang.dynamics.properties.Group;
import deco.lang.dynamics.properties.GroupMembership;
import deco.lang.dynamics.properties.RoleOccupation;
import deco.lang.dynamics.properties.SystemProperty;

/**
 * System properties. Four system property types are distinguished.
 *  
 * @author Jan Sudeikat
 *
 */
@XmlRootElement(name="system_properties")
public class Properties {

	//----------attributes----------
	
	/** The system properties (nodes). */
	private ArrayList<EnvrionmentProperty> env_properties = new ArrayList<EnvrionmentProperty>();
	
	/** The system properties (nodes). */
	private ArrayList<RoleOccupation> role_properties = new ArrayList<RoleOccupation>();

	/** The system properties (nodes). */
	private ArrayList<GroupMembership> gm_properties = new ArrayList<GroupMembership>();
	
	/** The system properties (nodes). */
	private ArrayList<Group> g_properties = new ArrayList<Group>();
	
	//----------methods-------------
	
	@XmlElementWrapper(name="environment_properties")
	@XmlElement(name="environment_property")
	public ArrayList<EnvrionmentProperty> getEnv_properties() {
		return env_properties;
	}

	public void setEnv_properties(ArrayList<EnvrionmentProperty> env_properties) {
		this.env_properties = env_properties;
	}

	@XmlElementWrapper(name="role_occupations")
	@XmlElement(name="role_occupation")
	public ArrayList<RoleOccupation> getRole_properties() {
		return role_properties;
	}

	public void setRole_properties(ArrayList<RoleOccupation> role_properties) {
		this.role_properties = role_properties;
	}

	@XmlElementWrapper(name="group_membership_counts")
	@XmlElement(name="group_membership_count")
	public ArrayList<GroupMembership> getGm_properties() {
		return gm_properties;
	}

	public void setGm_properties(ArrayList<GroupMembership> gm_properties) {
		this.gm_properties = gm_properties;
	}

	@XmlElementWrapper(name="group_counts")
	@XmlElement(name="group_count")
	public ArrayList<Group> getG_properties() {
		return g_properties;
	}

	public void setG_properties(ArrayList<Group> g_properties) {
		this.g_properties = g_properties;
	}
	
	public void addEnvironmentProperty(EnvrionmentProperty ep){
		this.env_properties.add(ep);
	}
	
	public void addRoleOccupation(RoleOccupation ro){
		this.role_properties.add(ro);
	}
	
	public void addGroupMembership(GroupMembership gm){
		this.gm_properties.add(gm);
	}
	
	public void addGroupProperty(Group g){
		this.g_properties.add(g);
	}
	
	//----------utility-------------
	
	/**
	 * Get all PropertyTypes that may involve agents.
	 */
	public ArrayList<SystemProperty> getAgentProperties(){
		ArrayList<SystemProperty> ret = new ArrayList<SystemProperty>();
		ret.addAll(this.getG_properties());
		ret.addAll(this.getGm_properties());
		ret.addAll(this.getRole_properties());
		return ret;
	}
	
	/**
	 * Get all PropertyTypes.
	 */
	public ArrayList<SystemProperty> getSystemProperties(){
		ArrayList<SystemProperty> ret = new ArrayList<SystemProperty>();
		ret.addAll(this.getG_properties());
		ret.addAll(this.getGm_properties());
		ret.addAll(this.getRole_properties());
		ret.addAll(this.getEnv_properties());
		return ret;
	}

	/**
	 * Get property by name.
	 * (names are considered to be unique)
	 * 
	 * @param name		The property name
	 * @return			The property element (null if not found)
	 */
	public SystemProperty getProperty(String name) {
		// check all property types:
		for (EnvrionmentProperty ep : this.env_properties){
			if (ep.getName().equals(name)) return ep;
		}
		for (Group g : this.g_properties){
			if (g.getName().equals(name)) return g;
		}
		for (GroupMembership gm : this.gm_properties){
			if (gm.getName().equals(name)) return gm;
		}
		for (RoleOccupation ro : this.role_properties){
			for (AgentReference ar : ro.getAgentReferences())
			{
				if (ar.getAgent_id().equals(name)) return ro;
			}
		}
		return null;
	}
}
