package jadex.rules.state.io.xml;

import jadex.rules.state.IOAVState;
import jadex.rules.state.OAVAttributeType;

import java.util.List;

/**
 *  Object to hold information about attributes that
 *  need to be evaluated in 2nd pass.
 */
public class DeferredValueConversion
{
	//-------- attributes --------
	
	/** The object. */
	protected Object	object;

	/** The attribute. */
	protected OAVAttributeType	attribute;

	/** The value. */
	protected String	value;

	/** The value converter. */
	protected IValueConverter	converter;

	/** The stack. */
	protected List	stack;
	
	//-------- constructors --------
	
	/**
	 *  Create a deferred value conversion object.
	 */
	public DeferredValueConversion(Object object, OAVAttributeType attribute, String value, IValueConverter converter,List	stack)
	{
		this.object	= object;
		this.attribute	= attribute;
		this.value	= value;
		this.converter	= converter;
		this.stack	= stack;
	}
	
	//-------- methods --------
	
	/**
	 *  Perform the value conversion in the state.
	 */
	public void	convertValue(IOAVState state)
	{
		if(OAVAttributeType.NONE.equals(attribute.getMultiplicity()))
		{
			state.setAttributeValue(object, attribute, converter.convertValue(state, stack, attribute, value));
		}
		else
		{
			state.addAttributeValue(object, attribute, converter.convertValue(state, stack, attribute, value));
		}
	}
}
