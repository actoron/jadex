package jadex.xml;

import javax.xml.namespace.QName;

/**
 *  Constants for xml handling.
 */
public class SXML
{
	//-------- constants --------
	
	/** The ID attribute constant. */
	public static final String ID = "__ID";
	
	/** The IDREF attribute constant. */
	public static final String IDREF = "__IDREF";
	
	/** The package protocol constant. */
	public static final String PROTOCOL_TYPEINFO = "typeinfo:";

	/** The null tag. */
	public static QName NULL = new QName(SXML.PROTOCOL_TYPEINFO, "null");
	
	/** Constant for anonymous inner classes. */
	public static final String XML_CLASSNAME = "XML_CLASSNAME";

}
