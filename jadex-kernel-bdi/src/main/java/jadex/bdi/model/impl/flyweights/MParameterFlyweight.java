package jadex.bdi.model.impl.flyweights;

import jadex.bdi.model.IMExpression;
import jadex.bdi.model.IMParameter;
import jadex.bdi.model.OAVBDIMetaModel;
import jadex.bdi.model.editable.IMEExpression;
import jadex.bdi.model.editable.IMEParameter;
import jadex.rules.state.IOAVState;

/**
 *  Flyweight for parameter model.
 */
public class MParameterFlyweight extends MTypedElementFlyweight implements IMParameter, IMEParameter
{
	//-------- constructors --------
	
	/**
	 *  Create a new parameter flyweight.
	 */
	public MParameterFlyweight(IOAVState state, Object scope, Object handle)
	{
		super(state, scope, handle);
	}
	
	//-------- methods --------
	
	/**
	 *  Get the parameter value.
	 *  @return The value.
	 */
	public IMExpression getValue()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Object handle = getState().getAttributeValue(getHandle(), OAVBDIMetaModel.parameter_has_value);
					if(handle!=null)
						object = new MExpressionFlyweight(getState(), getScope(), handle);
				}
			};
			return (IMExpression)invoc.object;
		}
		else
		{
			IMExpression ret = null;
			Object handle = getState().getAttributeValue(getHandle(), OAVBDIMetaModel.parameter_has_value);
			if(handle!=null)
				ret = new MExpressionFlyweight(getState(), getScope(), handle);
			return ret;
		}
	}
	
	/**
	 *  Get the binding options.
	 *  @return The binding options.
	 */
	public IMExpression getBindingOptions()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Object handle = getState().getAttributeValue(getHandle(), OAVBDIMetaModel.parameter_has_bindingoptions);
					if(handle!=null)
						object = new MExpressionFlyweight(getState(), getScope(), handle);
				}
			};
			return (IMExpression)invoc.object;
		}
		else
		{
			IMExpression ret = null;
			Object handle = getState().getAttributeValue(getHandle(), OAVBDIMetaModel.parameter_has_bindingoptions);
			if(handle!=null)
				ret = new MExpressionFlyweight(getState(), getScope(), handle);
			return ret;
		}
	}
	
	/**
	 *  Get the parameter direction.
	 *  @return The direction.
	 */
	public String getDirection()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					string = (String)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.parameter_has_direction);
				}
			};
			return invoc.string;
		}
		else
		{
			return (String)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.parameter_has_direction);
		}
	}
	
	/**
	 *  Flag if parameter is optional.
	 *  @return True if optional.
	 */
	public boolean isOptional()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					bool = ((Boolean)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.parameter_has_optional)).booleanValue();
				}
			};
			return invoc.bool;
		}
		else
		{
			return ((Boolean)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.parameter_has_optional)).booleanValue();
		}
	}

	/**
	 *  Create the parameter value.
	 *  @param expression	The expression.
	 *  @param language	The expression language (or null for default java-like language).
	 *  @return The value.
	 */
	public IMExpression createValue(final String expression, final String language)
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					MExpressionFlyweight mexp = MExpressionbaseFlyweight.createExpression(expression, language, getState(), getHandle());
					getState().setAttributeValue(getHandle(), OAVBDIMetaModel.parameter_has_value, mexp.getHandle());
					object	= mexp;
				}
			};
			return (IMEExpression)invoc.object;
		}
		else
		{
			MExpressionFlyweight mexp = MExpressionbaseFlyweight.createExpression(expression, language, getState(), getHandle());
			getState().setAttributeValue(getHandle(), OAVBDIMetaModel.parameter_has_value, mexp.getHandle());			
			return mexp;
		}
	}
	
	/**
	 *  Create the binding options.
	 *  @param expression	The expression.
	 *  @param language	The expression language (or null for default java-like language).
	 *  @return The binding options.
	 */
	public IMExpression createBindingOptions(final String expression, final String language)
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					MExpressionFlyweight mexp = MExpressionbaseFlyweight.createExpression(expression, language, getState(), getHandle());
					getState().setAttributeValue(getHandle(), OAVBDIMetaModel.parameter_has_bindingoptions, mexp.getHandle());
					object	= mexp;
				}
			};
			return (IMEExpression)invoc.object;
		}
		else
		{
			MExpressionFlyweight mexp = MExpressionbaseFlyweight.createExpression(expression, language, getState(), getHandle());
			getState().setAttributeValue(getHandle(), OAVBDIMetaModel.parameter_has_bindingoptions, mexp.getHandle());			
			return mexp;
		}
	}
	
	/**
	 *  Set the parameter direction.
	 *  @param dir The direction.
	 */
	public void setDirection(final String dir)
	{
		if(isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					getState().setAttributeValue(getHandle(), OAVBDIMetaModel.parameter_has_direction, dir);
				}
			};
		}
		else
		{
			getState().setAttributeValue(getHandle(), OAVBDIMetaModel.parameter_has_direction, dir);
		}
	}
	
	/**
	 *  Flag if parameter is optional.
	 *  @param optional True if optional.
	 */
	public void setOptional(final boolean optional)
	{
		if(isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					getState().setAttributeValue(getHandle(), OAVBDIMetaModel.parameter_has_optional, optional ? Boolean.TRUE : Boolean.FALSE);
				}
			};
		}
		else
		{
			getState().setAttributeValue(getHandle(), OAVBDIMetaModel.parameter_has_optional, optional ? Boolean.TRUE : Boolean.FALSE);
		}
	}
}
