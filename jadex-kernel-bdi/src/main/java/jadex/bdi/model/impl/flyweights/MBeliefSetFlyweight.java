package jadex.bdi.model.impl.flyweights;

import java.util.Collection;
import java.util.Iterator;

import jadex.bdi.model.IMBeliefSet;
import jadex.bdi.model.IMExpression;
import jadex.bdi.model.IMGoal;
import jadex.bdi.model.OAVBDIMetaModel;
import jadex.bdi.model.impl.flyweights.MElementFlyweight.AgentInvocation;
import jadex.rules.state.IOAVState;

/**
 *  Flyweight for belief model element.
 */
public class MBeliefSetFlyweight extends MTypedElementFlyweight implements IMBeliefSet
{
	//-------- constructors --------
	
	/**
	 *  Create a new element flyweight.
	 */
	public MBeliefSetFlyweight(IOAVState state, Object scope, Object handle)
	{
		super(state, scope, handle);
	}
	
	//-------- methods --------
	
	/**
	 *  Get the facts.
	 *  @return The facts. 
	 */
	public IMExpression[] getFacts()
	{
		if(isExternalThread())
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
	 *  Get the facts expression.
	 *  @return The facts expression. 
	 */
	public IMExpression getFactExpression()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					object = getState().getAttributeValue(getHandle(), OAVBDIMetaModel.beliefset_has_factsexpression);
				}
			};
			return (IMExpression)invoc.object;
		}
		else
		{
			return (IMExpression)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.beliefset_has_factsexpression);
		}
	}
	
	/**
	 *  Test if the belief is used as argument.
	 *  @return True if used as argument. 
	 */
	public boolean isArgument()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					bool = ((Boolean)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.beliefset_has_argument)).booleanValue();
				}
			};
			return invoc.bool;
		}
		else
		{
			return ((Boolean)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.beliefset_has_argument)).booleanValue();
		}
	}
	
	/**
	 *  Test if the belief is used as result.
	 *  @return True if used as result. 
	 */
	public boolean isResult()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					bool = ((Boolean)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.beliefset_has_result)).booleanValue();
				}
			};
			return invoc.bool;
		}
		else
		{
			return ((Boolean)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.beliefset_has_result)).booleanValue();
		}
	}
}
