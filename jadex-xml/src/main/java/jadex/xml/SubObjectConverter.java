package jadex.xml;

/**
 *
 */
public class SubObjectConverter implements ISubObjectConverter
{
	/** The object object read converter. */
	protected IObjectObjectConverter rconv;
	
	/** The object object write converter. */
	protected IObjectObjectConverter wconv;

	/**
	 *  Create a new attribute converter.
	 */
	public SubObjectConverter(IObjectObjectConverter rconv, IObjectObjectConverter wconv)
	{
		this.rconv = rconv;
		this.wconv = wconv;
	}
	
	/**
	 *  Convert an object to another object.
	 *  @param val The value to convert.
	 */
	public Object convertObjectForRead(Object val, IContext context)
	{
		return rconv!=null? rconv.convertObject(val, context): val;
	}
	
	/**
	 *  Convert an object to another object.
	 *  @param val The value to convert.
	 */
	public Object convertObjectForWrite(Object val, IContext context)
	{
		return wconv!=null? wconv.convertObject(val, context): val;
	}
}
