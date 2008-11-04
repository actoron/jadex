package jadex.bdi.interpreter;

import jadex.bridge.IArgument;
import jadex.commons.SReflect;
import jadex.javaparser.IParsedExpression;
import jadex.rules.state.IOAVState;

import java.util.Collection;
import java.util.Iterator;


public class MBeliefArgument	implements IArgument
{
	//-------- attributes --------
	
	/** The state. */
	protected IOAVState	state;
	
	/** The object handle for the element. */
	protected Object	handle;

	/** The object handle for the element's scope. */
	protected Object	scope;

	//-------- constructors --------
	
	/**
	 *  Create a new belief model flyweight.
	 *  @param state	The state.
	 *  @param scope	The scope handle.
	 *  @param handle	The element handle.
	 */
	public MBeliefArgument(IOAVState state, Object scope, Object handle)
	{
		this.state	= state;
		this.handle	= handle;
		this.scope	= scope;
	}

	//-------- IArgument --------
	
	/**
	 *  Get the element name.
	 *  @return The element name.
	 */
	public String	getName()
	{
		return (String)state.getAttributeValue(handle, OAVBDIMetaModel.modelelement_has_name);
	}

	/**
	 *  Get the description (i.e. a natural language text)
	 *  of the element.
	 *  @return The description text.
	 */
	public String	getDescription()
	{
		return (String)state.getAttributeValue(handle, OAVBDIMetaModel.modelelement_has_description);
	}
	
	/**
	 *  Get the typename.
	 *  @return The typename. 
	 */
	public String getTypename()
	{
		return SReflect.getInnerClassName((Class)state.getAttributeValue(
			handle, OAVBDIMetaModel.typedelement_has_class));
	}
	
	/**
	 *  Get the default value.
	 *  @return The default value.
	 */
	public Object getDefaultValue(String configname)
	{
		Object ret = null;
		boolean found = false;
		
		Object config;
		if(configname==null)
		{
			config = state.getAttributeValue(scope, OAVBDIMetaModel.capability_has_defaultconfiguration);
		}
		else
		{
			config = state.getAttributeValue(scope, OAVBDIMetaModel.capability_has_configurations, configname);
		}
		
		if(config!=null)
		{
			Collection inibels = state.getAttributeValues(config, OAVBDIMetaModel.configuration_has_initialbeliefs);
			if(inibels!=null)
			{
				for(Iterator it=inibels.iterator(); it.hasNext(); )
				{
					Object inibel = it.next();
					if(state.getAttributeValue(inibel, OAVBDIMetaModel.configbelief_has_ref).equals(handle))
					{	
						Object exp = state.getAttributeValue(inibel, OAVBDIMetaModel.belief_has_fact);
						// todo: string rep?
						IParsedExpression parsedexp = (IParsedExpression)state.getAttributeValue(exp, OAVBDIMetaModel.expression_has_content);
						ret = parsedexp.getExpressionText();
						found = true;
					}
				}
			}
		}
		
		if(!found)
		{
			Object exp = state.getAttributeValue(handle, OAVBDIMetaModel.belief_has_fact);
			if(exp!=null)
			{
				IParsedExpression parsedexp = (IParsedExpression)state.getAttributeValue(exp, OAVBDIMetaModel.expression_has_content);
				ret = parsedexp.getExpressionText();
			}
		}
		
		return ret;
	}
	
	/**
	 *  Check the validity of an input.
	 *  @param input The input.
	 *  @return True, if valid.
	 */
	public boolean validate(String input)
	{
		// todo
		return true;
	}
}
