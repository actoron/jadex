/**
 * 
 */
package deco.lang.dynamics.properties;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Property Constraints. These control when an agent is considered to be part of the property, 
 * i.e. when it adopted the behavior that is described by the property.
 * They need to hold in order that coordination information are to be send.
 * 
 * @author Jan Sudeikat
 *
 */
@XmlRootElement(name="constraints")
public class PropertyConstraints {
	
	//-------- attributes ----------

	/** Property condition. The agent is in the state if the condition holds. */
	private PropertyCondition condition;
	
	/** Inhibiting agent element reference(s). */
	private ArrayList<ElementReference> inhibitions;
	
	//-------- methods -------------
	
	@XmlElement(name="condition")
	public PropertyCondition getCondition() {
		return condition;
	}

	public void setCondition(PropertyCondition condition) {
		this.condition = condition;
	}
	
	@XmlElementWrapper(name="inhibitions")
	@XmlElement(name="element")
	public ArrayList<ElementReference> getInhibitions() {
		return inhibitions;
	}

	public void setInhibitions(ArrayList<ElementReference> inhibitions) {
		this.inhibitions = inhibitions;
	}
	
	public void addInhibition(ElementReference ref) {
		this.inhibitions.add(ref);
	}
	
}
