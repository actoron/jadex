package jadex.xml;

/**
 *  Converter for attributes. Consist of a string-object and a object-string converter.
 *  The first for reading the second for writing.
 */
public class AttributeConverter implements IAttributeConverter
{
	//-------- attributes --------
	
	/** The string object converter. */
	protected IStringObjectConverter soconv;
	
	/** The object string converter. */
	protected IObjectStringConverter osconv;

	//-------- constructors --------
	
	/**
	 *  Create a new attribute converter.
	 */
	public AttributeConverter(IStringObjectConverter soconv, IObjectStringConverter osconv)
	{
		this.soconv = soconv;
		this.osconv = osconv;
	}
	
	//-------- methods --------
	
	/**
	 *  Convert a string value to another type.
	 *  @param val The value to convert.
	 */
	public Object convertString(String val, IContext context) throws Exception
	{
		return soconv!=null? soconv.convertString(val, context): val;
	}
	
	/**
	 *  Convert a value to a string type.
	 *  @param val The value to convert.
	 */
	public String convertObject(Object val, IContext context)
	{
		return osconv!=null? osconv.convertObject(val, context): ""+val;
	}
}
