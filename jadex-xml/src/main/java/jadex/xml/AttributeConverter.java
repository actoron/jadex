package jadex.xml;

/**
 * 
 */
public class AttributeConverter implements IAttributeConverter
{
	
	/** The string object converter. */
	protected IStringObjectConverter soconv;
	
	/** The object string converter. */
	protected IObjectStringConverter osconv;

	/**
	 *  Create a new attribute converter.
	 */
	public AttributeConverter(IStringObjectConverter soconv, IObjectStringConverter osconv)
	{
		this.soconv = soconv;
		this.osconv = osconv;
	}
	
	/**
	 *  Convert a string value to another type.
	 *  @param val The value to convert.
	 */
	public Object convertString(String val, IContext context)
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
