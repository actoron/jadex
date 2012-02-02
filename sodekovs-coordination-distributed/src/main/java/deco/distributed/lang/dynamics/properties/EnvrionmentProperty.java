package deco.distributed.lang.dynamics.properties;

import javax.xml.bind.annotation.XmlRootElement;


/**
 * MASDynamics property type.<br>
 * <br>
 * Environment properties describe quantifiable environment properties.
 * 
 * @author Jan Sudeikat
 *
 */
@XmlRootElement(name="environment_property")
public class EnvrionmentProperty extends SystemProperty {

	//----------attributes----------

	/** The quantified value of the property. (added for future support / read only for the user) */
	private Integer value;

	//----------methods-------------
	
	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}
	
}
