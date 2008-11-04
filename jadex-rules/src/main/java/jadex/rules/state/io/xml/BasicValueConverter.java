package jadex.rules.state.io.xml;

import jadex.rules.state.IOAVState;
import jadex.rules.state.OAVAttributeType;
import jadex.rules.state.OAVJavaType;
import jadex.rules.state.OAVObjectType;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

/**
 *  Converter for basic values, such as integers or strings.
 */
public class BasicValueConverter implements IValueConverter
{
	/**
	 *  Flag to indicate that the converter requires two-pass
	 *  processing, i.e. attribute values are evaluated in 2nd pass.
	 */
	public boolean	isTwoPass()
	{
		return false;
	}
	
	/**
	 *	Convert the given XML string value to an
	 *  OAV object value.
	 *
	 *  @param state	The current OAV state.
	 *  @param stack	The current stack of OAV objects, created from XML.
	 *  @param attribute	The OAV attribute type.
	 *  @param value	The XML string value.
	 *  @return	The OAV object value.
	 */
	public Object	convertValue(IOAVState state, List stack, OAVAttributeType attr, String svalue)
	{
		Object	value;
		OAVObjectType	vtype	= attr.getType();
		if(vtype instanceof OAVJavaType)
		{
			Class	clazz	= ((OAVJavaType)vtype).getClazz();
			if(clazz==String.class)
			{
				value	= svalue;
			}
			else if(clazz==Integer.class)
			{
				value	= new Integer(svalue);
			}
			else if(clazz==Boolean.class)
			{
				value	= new Boolean(svalue);
			}
			else if(clazz==Long.class)
			{
				value	= new Long(svalue);
			}
			// todo: other basic or "fromstringable" classes.
			else
			{
				throw new RuntimeException("Value class not supported: "+clazz);
			}
		}
		else
		{
			throw new RuntimeException("Value type not supported: "+vtype);
		}
		return getValue(value);
	}
    
    //-------- static part --------
    
    /** The value lookup table. */
    protected static Map	VALUES	= new WeakHashMap();
    
    /**
     *  Lookup a value object (i.e. an Integer or a String).
     *  Because value objects are immutable,
     *  an existing copy will be used instead of a new one
     *  to save some memory.
     *  Known values are stored in a weak hash map, such that
     *  they are available as long they are in use, but can
     *  be garbage collected at any time.
     */
    protected static Object	getValue(Object value)
    {
    	Object	ret	= VALUES.get(value);
    	if(ret==null)
    	{
    		ret	= value;
    		VALUES.put(value, value);
    	}
    	return ret;
    }
}
