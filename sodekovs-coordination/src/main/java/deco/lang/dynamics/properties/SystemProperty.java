package deco.lang.dynamics.properties;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The representations of a MASDynamics property.<br>
 * These can be:
 * <ul>
 * 	<li>Environment Property (EnvironmentProperty)</li>
 * 	<li>Role occupation count (RoleOccupation)</li>
 * 	<li>Group count (Group)</li>
 * 	<li>Group Membership count (GroupMembership)</li>
 * </ul>
 * 
 * @author Jan Sudeikat
 *
 */
@XmlRootElement
public class SystemProperty {

	//----------attributes----------

	/** The property name. */
	private String name;
	
	/** Multiplicity of property. */
	private boolean multiple;

	//----------methods-------------
	
	@XmlAttribute(name="multiple")
	public boolean isMultiple() {
		return multiple;
	}

	public void setMultiple(boolean multiple) {
		this.multiple = multiple;
	}

	@XmlAttribute(name="name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}