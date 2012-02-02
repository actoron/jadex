package deco4mas.annotation.agent;

/**
 * Store a list of parameter mappings.  
 * 
 * @author Jan Sudeikat
 *
 */
public class Parameter {

	//----------attributes----------

	/** Parameter name. */
	private String name;
	
	/** Parameter value. */
	private Object value;

	//----------constructors--------
	
	public Parameter(String name, Object value) {
		super();
		this.name = name;
		this.value = value;
	}

	//----------methods-------------
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	} 

}