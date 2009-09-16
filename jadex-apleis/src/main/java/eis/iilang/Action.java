package eis.iilang;

/**
 * An action that can be performed by an agent through its associated entity/ies.
 * @author tristanbehrens
 *
 */
public class Action extends DataContainer {

	/**
	 * Constructs an action.
	 * 
	 * @param name
	 * @param parameters
	 */
	public Action(String name, Parameter...parameters) {
		super(name, parameters);
	}

	@Override
	protected String toXML(int depth) {

		String xml = "";
		
		xml += indent(depth) + "<action name=\"" + name + "\">" + "\n";
		
		for( Parameter p : params ) {
			
			xml += indent(depth+1) + "<actionParameter>" + "\n";
			xml += p.toXML(depth+2);
			xml += indent(depth+1) + "</actionParameter>" + "\n";
			
		}

		xml += indent(depth) + "</action>" + "\n";

		return xml;
	
	}

	@Override
	public String toProlog() {
		
		String ret = "action";
		
		ret+="(";
		
		ret+=name;
		
		for( Parameter p : params ) 
			ret += "," + p.toProlog();
		
		ret+=")";
		
		return ret;
	
	}

}
