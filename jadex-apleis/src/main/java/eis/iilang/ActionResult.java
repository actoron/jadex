package eis.iilang;

/**
 * The result of an action. An action consists of an action name and some parameters.
 * <p/>
 * This is usually returned when an action is performed.
 * 
 * @author tristanbehrens
 *
 */
public class ActionResult extends DataContainer {
	
	/**
	 * Construcs an action-result.
	 * 
	 * @param name
	 * @param parameters
	 */
	public ActionResult(String name, Parameter...parameters) {
		super(name, parameters);
	}

	@Override
	public String toXML(int depth) {

		String xml = "";
		
		xml += indent(depth) + "<actionResult name=\"" + name + "\">" + "\n";
		
		for( Parameter p : params ) {
			
			xml += indent(depth+1) + "<actionResultParameter>" + "\n";
			xml += p.toXML(depth+2);
			xml += indent(depth+1) + "</actionResultParameter>" + "\n";
			
		}

		xml += indent(depth) + "</actionResult>" + "\n";

		return xml;

	}

	@Override
	public String toProlog() {
		
		String ret = "actionresult";
		
		ret+="(";
		
		ret+=name;
		
		for( Parameter p : params ) 
			ret += "," + p.toProlog();
		
		ret+=")";
		
		return ret;
	
	}

}
