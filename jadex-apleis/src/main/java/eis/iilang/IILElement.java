package eis.iilang;

/**
 * Represents an element of the <i>Interface Immediate Language</i>. 
 * 
 * @author tristanbehrens
 *
 */
public abstract class IILElement {

	/** 
	 * Returns a string-representation.
	 */
	public final String toString() { 
	
		return toXML(); 
	
	}
	
	/**
	 * Returns an XML-representation encoded in a string.
	 * @param depth is the depth of indendation.
	 * @return an XML-string.
	 */
	protected abstract String toXML(int depth);
	
	/**
	 * Returns an XML-string including the header.
	 * 
	 * @return an XML-string including the header.
	 */
	public final String toXMLWithHeader() {
		
		String xml = "";
		
		xml += "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "\n"; 
		xml += toXML(0);
		
		return xml;
		
	}
	
	/** 
	 * Returns an XML-representation encoded in a string.
	 * 
	 * @return an XML-string.
	 */
	public final String toXML() {
		
		return toXML(0);
		
	}

	/**
	 * Returns a Prolog-representation encoded in a string.
	 * @return a Prolog-string
	 */
	public abstract String toProlog();

	/**
	 * Returns an indentation.
	 * @param depth is the depth of the indentation.
	 * @return
	 */
	protected String indent(int depth) {
		
		String ret = "";
		
		for( int a = 0 ; a < depth ; a++ )
			ret += "  ";
		
		return ret;
		
	}
	
}
