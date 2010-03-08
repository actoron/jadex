package deco4mas.mechanism;

import java.util.HashMap;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Coordination Information. <br>
 * Coordination Information elements are exchanged between agent instances 
 * to indicate the occurrences of agent-internal events 
 * as well as to transfer agent-internal data.
 * 
 * Note: the jaxb annotations are for documentation only. 
 * The annotations in  * this interface only provide 
 * suggestions for the annotations in implementing classes. 
 * 
 * @author Jan Sudeikat
 *
 */
@XmlRootElement(name="coordination_information")
public interface CoordinationInformation  {

	/** Coordination Information type. */
	@XmlAttribute(name="type")
	public abstract String getType();

	public abstract void setType(String type);

	/** Coordination Information element name. */
	@XmlAttribute(name="name")
	public abstract String getName();

	public abstract void setName(String name);

	public abstract HashMap<String, Object> getValues();

	public abstract Object getValueByName(String key);

	public abstract void addValue(String key, Object value);

}