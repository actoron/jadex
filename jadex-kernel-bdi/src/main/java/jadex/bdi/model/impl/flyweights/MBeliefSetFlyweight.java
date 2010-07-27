package jadex.bdi.model.impl.flyweights;

import jadex.bdi.model.IMBeliefSet;
import jadex.bdi.model.IMExpression;
import jadex.bdi.model.OAVBDIMetaModel;
import jadex.bdi.model.editable.IMEBeliefSet;
import jadex.bdi.model.editable.IMEExpression;
import jadex.rules.state.IOAVState;

import java.util.Collection;
import java.util.Iterator;

/**
 *  Flyweight for belief model element.
 */
public class MBeliefSetFlyweight extends MTypedElementFlyweight implements IMBeliefSet, IMEBeliefSet
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
	public IMExpression getFactsExpression()
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
	
	
	/**
	 *  Create the fact.
	 *  @return The fact. 
	 */
	public IMEExpression createFact()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Object elem = getState().createObject(OAVBDIMetaModel.expression_type);
					getState().addAttributeValue(getHandle(), OAVBDIMetaModel.beliefset_has_facts, elem);
					object = new MExpressionFlyweight(getState(), getScope(), elem);
				}
			};
			return (IMEExpression)invoc.object;
		}
		else
		{
			Object elem = getState().createObject(OAVBDIMetaModel.expression_type);
			getState().addAttributeValue(getHandle(), OAVBDIMetaModel.beliefset_has_facts, elem);
			return new MExpressionFlyweight(getState(), getScope(), elem);
		}
	}

	/**
	 *  Create a facts expression.
	 *  @param The facts expression. 
	 */
	public IMExpression createFactsExpression()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Object elem = getState().createObject(OAVBDIMetaModel.expression_type);
					getState().setAttributeValue(getHandle(), OAVBDIMetaModel.beliefset_has_factsexpression, elem);
					object = new MExpressionFlyweight(getState(), getScope(), elem);
				}
			};
			return (IMEExpression)invoc.object;
		}
		else
		{
			Object elem = getState().createObject(OAVBDIMetaModel.expression_type);
			getState().setAttributeValue(getHandle(), OAVBDIMetaModel.beliefset_has_factsexpression, elem);
			return new MExpressionFlyweight(getState(), getScope(), elem);
		}
	}

	
	/**
	 *  Set the belief is used as argument.
	 *  @param arg The argument flag. 
	 */
	public void setArgument(final boolean argu)
	{
		if(isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					getState().setAttributeValue(getHandle(), OAVBDIMetaModel.beliefset_has_argument, argu? Boolean.TRUE: Boolean.FALSE);
				}
			};
		}
		else
		{
			getState().setAttributeValue(getHandle(), OAVBDIMetaModel.beliefset_has_argument, argu? Boolean.TRUE: Boolean.FALSE);
		}
	}
	
	/**
	 *  Set the belief is used as argument.
	 *  @param res The result flag. 
	 */
	public void setResult(final boolean res)
	{
		if(isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					getState().setAttributeValue(getHandle(), OAVBDIMetaModel.beliefset_has_result, res? Boolean.TRUE: Boolean.FALSE);
				}
			};
		}
		else
		{
			getState().setAttributeValue(getHandle(), OAVBDIMetaModel.beliefset_has_result, res? Boolean.TRUE: Boolean.FALSE);
		}
	}
}
