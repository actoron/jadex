package jadex.xml;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import jadex.commons.transformation.annotations.Classname;
import jadex.xml.stax.QName;

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

	/** The value of this attribute is used as idref. */
	public static final String ARRAYLEN = "__len";
	
	/** The null tag. */
	public static final QName NULL = new QName(SXML.PROTOCOL_TYPEINFO, "null");
	
	/** Constant for anonymous inner classes. */
	public static final String XML_CLASSNAME = "XML_CLASSNAME";
	
	/** The linefeed separator. */
	public static final String lf = (String) System.getProperty("line.separator");

	/** The default encoding. */
	public static final String DEFAULT_ENCODING = "utf-8";

	/**
	 *  Get the xmlclassname annotation.
	 */
	public static Classname getXMLClassnameAnnotation(Class clazz)
	{
		Classname	xmlc	= null;
		// Find annotation in fields or methods of class, because annotations are not supported on anonymous classes directly.
		Field[] fields = clazz.getDeclaredFields();
		for(int i=0; xmlc==null && i<fields.length; i++)
		{
			xmlc	= fields[i].getAnnotation(Classname.class);
		}
		if(xmlc==null)
		{
			Method[]	methods	= clazz.getDeclaredMethods();
			for(int i=0; xmlc==null && i<methods.length; i++)
			{
				xmlc	= methods[i].getAnnotation(Classname.class);
			}
		}
		return xmlc;
	}
	
}
