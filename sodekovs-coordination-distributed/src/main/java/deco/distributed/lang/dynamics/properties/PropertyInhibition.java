package deco.distributed.lang.dynamics.properties;


/**
 * Constraint to the coordination.
 *  
 * @author Jan Sudeikat
 *
 */
public class PropertyInhibition {
	
	//-------- attributes ----------

	/** The name of the inhibiting element. */
	private String element_name;
	
	/** The type of the inhibiting element. */
	private String element_type;

	//-------- methods ----------
	
	public String getElement_name() {
		return element_name;
	}

	public void setElement_name(String element_name) {
		this.element_name = element_name;
	}

	public String getElement_type() {
		return element_type;
	}

	public void setElement_type(String element_type) {
		this.element_type = element_type;
	}
	
}
