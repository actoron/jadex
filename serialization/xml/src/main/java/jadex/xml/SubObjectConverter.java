package jadex.xml;

/**
 *  Converter for subobjects. Consist of two object-object converter.
 *  The first for reading the second for writing.
 */
public class SubObjectConverter implements ISubObjectConverter
{
	//-------- attributes --------

	/** The object object read converter. */
	protected IObjectObjectConverter rconv;
	
	/** The object object write converter. */
	protected IObjectObjectConverter wconv;

	//-------- constructors --------

	/**
	 *  Create a new attribute converter.
	 */
	public SubObjectConverter(IObjectObjectConverter rconv, IObjectObjectConverter wconv)
	{
		this.rconv = rconv;
		this.wconv = wconv;
	}

	//-------- methods --------

	/**
	 *  Convert an object to another object.
	 *  @param val The value to convert.
	 */
	public Object convertObjectForRead(Object val, IContext context) throws Exception
	{
		return rconv!=null? rconv.convertObject(val, context): val;
	}
	
	/**
	 *  Convert an object to another object.
	 *  @param val The value to convert.
	 */
	public Object convertObjectForWrite(Object val, IContext context) throws Exception
	{
		return wconv!=null? wconv.convertObject(val, context): val;
	}
}
