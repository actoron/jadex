package jadex.bdi.model.impl.flyweights;

import jadex.bdi.model.IMBelief;
import jadex.bdi.model.IMCondition;
import jadex.bdi.model.IMGoal;
import jadex.bdi.model.IMTypedElement;
import jadex.bdi.model.OAVBDIMetaModel;
import jadex.rules.state.IOAVState;

import java.util.Collection;
import java.util.Iterator;

/**
 *  Flyweight for goal model.
 */
public class MGoalFlyweight extends MProcessableElementFlyweight implements IMGoal
{
	//-------- constructors --------
	
	/**
	 *  Create a new element flyweight.
	 */
	public MGoalFlyweight(IOAVState state, Object scope, Object handle)
	{
		super(state, scope, handle);
	}
	
	//-------- methods --------
	
	/**
	 *  Get the creation condition.
	 *  @return The creation condition.
	 */
	public IMCondition getCreationCondition()
	{
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Object handle = getState().getAttributeValue(getHandle(), OAVBDIMetaModel.goal_has_creationcondition);
					if(handle!=null)
						object = new MConditionFlyweight(getState(), getScope(), handle);
				}
			};
			return (IMCondition)invoc.object;
		}
		else
		{
			IMCondition ret = null;
			Object handle = getState().getAttributeValue(getHandle(), OAVBDIMetaModel.goal_has_creationcondition);
			if(handle!=null)
				ret = new MConditionFlyweight(getState(), getScope(), handle);
			return ret;
		}
	}
	
	/**
	 *  Get the context condition.
	 *  @return The context condition.
	 */
	public IMCondition getContextCondition()
	{
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Object handle = getState().getAttributeValue(getHandle(), OAVBDIMetaModel.goal_has_contextcondition);
					if(handle!=null)
						object = new MConditionFlyweight(getState(), getScope(), handle);
				}
			};
			return (IMCondition)invoc.object;
		}
		else
		{
			IMCondition ret = null;
			Object handle = getState().getAttributeValue(getHandle(), OAVBDIMetaModel.goal_has_contextcondition);
			if(handle!=null)
				ret = new MConditionFlyweight(getState(), getScope(), handle);
			return ret;
		}
	}
	
	/**
	 *  Get the drop condition.
	 *  @return The drop condition.
	 */
	public IMCondition getDropCondition()
	{
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Object handle = getState().getAttributeValue(getHandle(), OAVBDIMetaModel.goal_has_dropcondition);
					if(handle!=null)
						object = new MConditionFlyweight(getState(), getScope(), handle);
				}
			};
			return (IMCondition)invoc.object;
		}
		else
		{
			IMCondition ret = null;
			Object handle = getState().getAttributeValue(getHandle(), OAVBDIMetaModel.goal_has_dropcondition);
			if(handle!=null)
				ret = new MConditionFlyweight(getState(), getScope(), handle);
			return ret;
		}
	}
	
	/**
	 *  Test if is retry.
	 *  @return True, if is retry.
	 */
	public boolean isRetry()
	{
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					bool = ((Boolean)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.goal_has_retry)).booleanValue();
				}
			};
			return invoc.bool;
		}
		else
		{
			return ((Boolean)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.goal_has_retry)).booleanValue();
		}
	}
	
	/**
	 *  Get the retry delay.
	 *  @return The retry delay.
	 */
	public long getRetryDelay()
	{
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					longint = ((Long)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.goal_has_retrydelay)).longValue();
				}
			};
			return invoc.longint;
		}
		else
		{
			return ((Long)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.goal_has_retrydelay)).longValue();
		}
	}
	
	/**
	 *  Test if is recur.
	 *  @return True, if is recur.
	 */
	public boolean isRecur()
	{
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					bool = ((Boolean)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.goal_has_recur)).booleanValue();
				}
			};
			return invoc.bool;
		}
		else
		{
			return ((Boolean)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.goal_has_recur)).booleanValue();
		}
	}
	
	/**
	 *  Get the retry delay.
	 *  @return The retry delay.
	 */
	public long getRecurDelay()
	{
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					longint = ((Long)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.goal_has_recurdelay)).longValue();
				}
			};
			return invoc.longint;
		}
		else
		{
			return ((Long)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.goal_has_recurdelay)).longValue();
		}
	}
	
	/**
	 *  Get the recur condition.
	 *  @return The recur condition.
	 */
	public IMCondition getRecurCondition()
	{
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Object handle = getState().getAttributeValue(getHandle(), OAVBDIMetaModel.goal_has_recurcondition);
					if(handle!=null)
						object = new MConditionFlyweight(getState(), getScope(), handle);
				}
			};
			return (IMCondition)invoc.object;
		}
		else
		{
			IMCondition ret = null;
			Object handle = getState().getAttributeValue(getHandle(), OAVBDIMetaModel.goal_has_recurcondition);
			if(handle!=null)
				ret = new MConditionFlyweight(getState(), getScope(), handle);
			return ret;
		}
	}

	/**
	 *  Get the exlcude mode.
	 *  @retur The exclude mode.
	 */
	public String getExcludeMode()
	{
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					string = (String)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.goal_has_exclude);
				}
			};
			return invoc.string;
		}
		else
		{
			return (String)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.goal_has_exclude);
		}
	}
	
	/**
	 *  Test if rebuild APL.
	 *  @retur True, if rebuild.
	 */
	public boolean isRebuild()
	{
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					bool = ((Boolean)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.goal_has_rebuild)).booleanValue();
				}
			};
			return invoc.bool;
		}
		else
		{
			return ((Boolean)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.goal_has_rebuild)).booleanValue();
		}
	}
	
	/**
	 *  Test if goal should be unique.
	 *  @retur True, if unique.
	 */
	public boolean isUnique()
	{
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					bool = ((Boolean)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.goal_has_unique)).booleanValue();
				}
			};
			return invoc.bool;
		}
		else
		{
			return ((Boolean)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.goal_has_unique)).booleanValue();
		}
	}
	
	/**
	 *  Get excluded parameters.
	 *  @retur The excluded parameters.
	 */
	public IMTypedElement[] getExcludedParameters()
	{
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Collection params = (Collection)getState().getAttributeValue(getScope(), OAVBDIMetaModel.goal_has_excludedparameter);
					IMTypedElement[] ret = new IMTypedElement[params==null? 0: params.size()];
					if(params!=null)
					{
						int i=0;
						for(Iterator it=params.iterator(); it.hasNext(); )
						{
							ret[i++] = new MTypedElementFlyweight(getState(), getScope(), it.next());
						}
					}
					object = ret;
				}
			};
			return (IMBelief[])invoc.object;
		}
		else
		{
			Collection params = (Collection)getState().getAttributeValue(getScope(), OAVBDIMetaModel.goal_has_excludedparameter);
			IMTypedElement[] ret = new IMTypedElement[params==null? 0: params.size()];
			if(params!=null)
			{
				int i=0;
				for(Iterator it=params.iterator(); it.hasNext(); )
				{
					ret[i++] = new MTypedElementFlyweight(getState(), getScope(), it.next());
				}
			}
			return ret;
		}
	}
	
	/**
	 *  Get inhibited goals.
	 *  @retur The inhibited goals.
	 * /
	public IMInhibitedElement getInhibitedGoals()
	{
		
	}*/
	
	/**
	 *  Get the cardinality.
	 *  @retur The cardinality.
	 */
	public int getCardinality()
	{
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					integer = ((Integer)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.goal_has_cardinality)).intValue();
				}
			};
			return invoc.integer;
		}
		else
		{
			return ((Integer)getState().getAttributeValue(getHandle(), OAVBDIMetaModel.goal_has_cardinality)).intValue();
		}
	}
}
