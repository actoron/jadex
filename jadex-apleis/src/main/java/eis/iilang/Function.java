package eis.iilang;

import java.util.LinkedList;

/**
 * Represents a function over parameters.
 * 
 * @author tristanbehrens
 */
public class Function extends Parameter {
	
	/** The name of the function. */
	private String name = null;
	
	/** A list of parameters. */
	private LinkedList<Parameter> params = new LinkedList<Parameter>();

	/**
	 * Instantiates a function.
	 * 
	 * @param name the name of the function.
	 * @param parameters the parameters.
	 */
	public Function(String name, Parameter... parameters) {
		
		this.name = name;
		
		for( Parameter p : parameters )
			this.params.add(p);
		
	}
	
	/**
	 * Returns the name of the function.
	 * 
	 * @return the name of the function.
	 */
	public String getName() {
		
		return name;
		
	}

	/**
	 * Returns the parameters of the function.
	 * 
	 * @return the parameters of the function.
	 */
	public LinkedList<Parameter> getParameters() {
		
		return params;
		
	}

	@Override
	protected String toXML(int depth) {

		String xml = "";
		
		xml += indent(depth) + "<function name=\"" + name +"\">" + "\n";

		for( Parameter p : params ) {
			
			xml += p.toXML(depth+1);
			
		}
		
		xml += indent(depth) + "</function>" + "\n";
		
		return xml;

	}
	
	@Override
	public String toProlog() {
		
		String ret = name;
		
		ret += "(";
		
		ret += params.getFirst().toProlog();
		
		for( int a = 1 ; a < params.size() ; a++ )
			ret += "," + params.get(a).toProlog();
		
		ret+=")";
		
		return ret;
	
	}

}
