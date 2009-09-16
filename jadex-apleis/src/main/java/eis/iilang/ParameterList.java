package eis.iilang;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * A list of parameters.
 * 
 * @author tristanbehrens
 *
 */
public class ParameterList extends Parameter implements Iterable<Parameter> {

	/** The list of parameters. */
	private LinkedList<Parameter> list = null;

	/** 
	 * Constructs an empty list of parameters.
	 */
	public ParameterList() {
	
		this.list = new LinkedList<Parameter>();
		
	}
	
	/**
	 * Contructs a list of parameters from an array.
	 * 
	 * @param parameters
	 */
	public ParameterList(Parameter...parameters) {
		
		this();
		
		for( Parameter param : parameters )
			list.addLast(param);
		
	}
	
	/** 
	 * Constructs a list of parameters from a collection.
	 * 
	 * @param parameters
	 */
	public ParameterList(Collection<Parameter> parameters) {
		
		this();
		
		for( Parameter param : parameters )
			list.addLast(param);
		
	}

	/**
	 * Returns an iterator.
	 */
	public Iterator<Parameter> iterator() {

		return list.iterator();

	}

	/**
	 * Checks for emptyness.
	 * @return
	 */
	public boolean isEmpty() {
		
		return list.isEmpty();
		
	}

	@Override
	protected String toXML(int depth) {

		String xml = "";
		
		xml += indent(depth) + "<parameterList>" + "\n";

		for( Parameter p : list ) {
			
			xml += p.toXML(depth+1);
			
		}
		
		xml += indent(depth) + "</parameterList>" + "\n";
		
		return xml;
	
	}
	
	public void add(Parameter parameter) {
	
			list.add(parameter);
		
	}
	
	@Override
	public String toProlog() {
		
		String ret = "";
		
		ret+="[";
		
		ret+=list.getFirst().toProlog();
		
		for( int a = 1 ; a < list.size() ; a++ )
			ret += "," + list.get(a).toProlog();
		
		ret+="]";
		
		return ret;
	
	}
}
