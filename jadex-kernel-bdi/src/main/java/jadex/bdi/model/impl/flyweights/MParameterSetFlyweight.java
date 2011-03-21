package jadex.bdi.model.impl.flyweights;

import jadex.bdi.model.IMExpression;
import jadex.bdi.model.IMParameterSet;
import jadex.bdi.model.OAVBDIMetaModel;
import jadex.bdi.model.editable.IMEExpression;
import jadex.bdi.model.editable.IMEParameterSet;
import jadex.rules.state.IOAVState;

import java.util.Collection;
import java.util.Iterator;

/**
 *  Flyweight for parameter model.
 */
public class MParameterSetFlyweight extends MTypedElementFlyweight implements IMParameterSet, IMEParameterSet
{
	//-------- constructors --------
	
	/**
	 *  Create a new typed element flyweight.
	 */
	public MParameterSetFlyweight(IOAVState state, Object scope, Object handle)
	{
		super(state, scope, handle);
	}
	
	//-------- methods --------
	
	/**
	 *  Get the parameter value.
	 *  @return The value.
	 */
	public IMExpression[] getValues()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Collection elems = (Collection)getState().getAttributeValues(getHandle(), OAVBDIMetaModel.beliefset_has_facts);
					IMExpression[] ret = new IMExpression[elems==null? 0: elems.size()];
					if(elems!=null)
					{
						int i=0;
						for(Iterator it=elems.iterator(); it.hasNext(); )
						{
							ret[i++] = new MExpressionFlyweight(getState(), getScope(), it.next());
						}
					}
					object = ret;
				}
			};
			return (IMExpression[])invoc.object;
		}
		else
		{
			Collection elems = (Collection)getState().getAttributeValues(getHandle(), OAVBDIMetaModel.beliefset_has_facts);
			IMExpression[] ret = new IMExpression[elems==null? 0: elems.size()];
			if(elems!=null)
			{
				int i=0;
				for(Iterator it=elems.iterator(); it.hasNext(); )
				{
					ret[i++] = new MExpressionFlyweight(getState(), getScope(), it.next());
				}
			}
			return ret;
		}
	}
	
	/**
	 *  Get the values expression.
	 *  @return The values expression.
	 */
	public IMExpression getValuesExpression()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					object = getState().getAttributeValue(getHandle(), OAVBDIMetaModel.parameterset_has_valuesexpression);
				}
			};
			return (IMExpression)invoc.object;
		}
		else
		{
			return (IMExpression)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.parameterset_has_valuesexpression);
		}
	}
	
	/**
	 *  Get the parameter set direction.
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
					string = (String)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.parameterset_has_direction);
				}
			};
			return invoc.string;
		}
		else
		{
			return (String)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.parameterset_has_direction);
		}
	}
	
	/**
	 *  Flag if parameter set is optional.
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
					bool = ((Boolean)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.parameterset_has_optional)).booleanValue();
				}
			};
			return invoc.bool;
		}
		else
		{
			return ((Boolean)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.parameterset_has_optional)).booleanValue();
		}
	}
	
	/**
	 *  Add a parameter value.
	 *  @param expression	The expression.
	 *  @param language	The expression language (or null for default java-like language).
	 *  @return The value.
	 */
	public IMExpression addValue(final String expression, final String language)	
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					MExpressionFlyweight mexp = MExpressionbaseFlyweight.createExpression(expression, language, getState(), getHandle());
					getState().addAttributeValue(getHandle(), OAVBDIMetaModel.parameterset_has_values, mexp.getHandle());
					object	= mexp;
				}
			};
			return (IMEExpression)invoc.object;
		}
		else
		{
			MExpressionFlyweight mexp = MExpressionbaseFlyweight.createExpression(expression, language, getState(), getHandle());
			getState().addAttributeValue(getHandle(), OAVBDIMetaModel.parameterset_has_values, mexp.getHandle());			
			return mexp;
		}
	}
	
	/**
	 *  @return The values expression.
	 *  @param expression	The expression.
	 *  @param language	The expression language (or null for default java-like language).
	 *  Create the values expression.
	 */
	public IMExpression createValuesExpression(final String expression, final String language)
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					MExpressionFlyweight mexp = MExpressionbaseFlyweight.createExpression(expression, language, getState(), getHandle());
					getState().setAttributeValue(getHandle(), OAVBDIMetaModel.parameterset_has_valuesexpression, mexp.getHandle());
					object	= mexp;
				}
			};
			return (IMEExpression)invoc.object;
		}
		else
		{
			MExpressionFlyweight mexp = MExpressionbaseFlyweight.createExpression(expression, language, getState(), getHandle());
			getState().setAttributeValue(getHandle(), OAVBDIMetaModel.parameterset_has_valuesexpression, mexp.getHandle());			
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
