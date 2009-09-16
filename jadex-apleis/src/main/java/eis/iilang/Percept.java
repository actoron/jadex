package eis.iilang;

/**
 * A percept.
 * <p/>
 * A percept consists of a name and some parameters.
 * 
 * @author tristanbehrens
 *
 */
public class Percept extends DataContainer {

	/** 
	 * Contructs a percept from a name and an array of parameters.
	 * 
	 * @param name the name.
	 * @param parameters the parameters.
	 */
	public Percept(String name, Parameter...parameters) {
		super(name, parameters);
	}

	@Override
	protected String toXML(int depth) {

		String xml = "";
		
		xml += indent(depth) + "<percept name=\"" + name + "\">" + "\n";
		
		for( Parameter p : params ) {
			
			xml += indent(depth+1) + "<perceptParameter>" + "\n";
			xml += p.toXML(depth+2);
			xml += indent(depth+1) + "</perceptParameter>" + "\n";
			
		}

		xml += indent(depth) + "</percept>" + "\n";

		return xml;

	}

	@Override
	public String toProlog() {
		
		String ret = "percept";
		
		ret+="(";
		
		ret+=name;
		
		for( Parameter p : params ) 
			ret += "," + p.toProlog();
		
		ret+=")";
		
		return ret;
	
	}
	
}
