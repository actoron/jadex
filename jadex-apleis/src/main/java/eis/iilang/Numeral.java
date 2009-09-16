package eis.iilang;

/**
 * Encapsulates a number.
 * 
 * @author tristanbehrens
 *
 */
public class Numeral extends Parameter {

	/** The value of the numner. */
	private Number value;

	/**
	 * Contructs a number.
	 * 
	 * @param value
	 */
	public Numeral(double value) {
		
		this.value = value;
		
	}
	
	@Override
	protected String toXML(int depth) {

		return indent(depth) + "<number value=\"" + value + "\"/>" + "\n";

	}
	
	@Override
	public String toProlog() {
		
		String ret = "";
		
		ret += value;
		
		return ret;
	
	}
	public Number getValue() {
		
		return value;
		
	}
	
/*	public int toInt() {
		
		return (int)value.t;
		
	}


	public long toLong() {
		
		return (long)value;
		
	}*/
}
