package jadex.bdi.model.impl.flyweights;

import jadex.bdi.model.IMExpression;
import jadex.bdi.model.IMParameterSet;
import jadex.bdi.model.OAVBDIMetaModel;
import jadex.rules.state.IOAVState;

import java.util.Collection;
import java.util.Iterator;

/**
 *  Flyweight for parameter model.
 */
public class MParameterSetFlyweight extends MTypedElementFlyweight implements IMParameterSet
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
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Collection elems = (Collection)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.beliefset_has_facts);
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
			Collection elems = (Collection)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.beliefset_has_facts);
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
		if(getInterpreter().isExternalThread())
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
		if(getInterpreter().isExternalThread())
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
		if(getInterpreter().isExternalThread())
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
}
