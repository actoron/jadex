package eis.iilang;

/**
 * An identifier is a string.
 * 
 * @author tristanbehrens
 *
 */
public class Identifier extends Parameter {

	/** The identifier itself. */
	private String value = null;

	/** 
	 * Constructs an identifier.
	 * 
	 * @param value
	 */
	public Identifier(String value) {
		
		this.value = value;
		
	}
	
	/**
	 * Returns the identifier.
	 * 
	 * @return
	 */
	public String getValue() {
		
		return value;
		
	}
	
	@Override
	protected String toXML(int depth) {
		
		return indent(depth) + "<identifier value=\"" + value + "\"/>" + "\n";
	
	}
	
	@Override
	public String toProlog() {
		
		String ret = value;
		
		return ret;
	
	}
	
}
