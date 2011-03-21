package jadex.bdi.model.impl.flyweights;

import jadex.bdi.model.IMCondition;
import jadex.bdi.model.IMGoal;
import jadex.bdi.model.IMInhibited;
import jadex.bdi.model.OAVBDIMetaModel;
import jadex.bdi.model.editable.IMECondition;
import jadex.bdi.model.editable.IMEGoal;
import jadex.commons.SUtil;
import jadex.rules.state.IOAVState;

import java.util.Collection;
import java.util.Iterator;

/**
 *  Flyweight for goal model.
 */
public class MGoalFlyweight extends MProcessableElementFlyweight implements IMGoal, IMEGoal
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
		if(isExternalThread())
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
		if(isExternalThread())
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
		if(isExternalThread())
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
		if(isExternalThread())
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
		if(isExternalThread())
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
		if(isExternalThread())
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
		if(isExternalThread())
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
		if(isExternalThread())
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
		if(isExternalThread())
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
		if(isExternalThread())
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
	 *  @return True, if unique.
	 */
	public boolean isUnique()
	{
		if(isExternalThread())
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
	 *  @return The excluded parameters.
	 */
	public String[] getExcludedParameters()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Collection params = (Collection)getState().getAttributeValues(getScope(), OAVBDIMetaModel.goal_has_excludedparameter);
					object = params!=null ? params.toArray(new String[params.size()]) : SUtil.EMPTY_STRING_ARRAY;
				}
			};
			return (String[])invoc.object;
		}
		else
		{
			Collection params = (Collection)getState().getAttributeValues(getScope(), OAVBDIMetaModel.goal_has_excludedparameter);
			return params!=null ? (String[])params.toArray(new String[params.size()]) : SUtil.EMPTY_STRING_ARRAY;
		}
	}
	
	/**
	 *  Get inhibited goals.
	 *  @retur The inhibited goals.
	 */
	public IMInhibited[] getInhibitedGoals()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Collection params = (Collection)getState().getAttributeValues(getScope(), OAVBDIMetaModel.goal_has_inhibits);
					IMInhibited[] ret = new IMInhibited[params==null? 0: params.size()];
					if(params!=null)
					{
						int i=0;
						for(Iterator it=params.iterator(); it.hasNext(); )
						{
							ret[i++] = new MInhibitedFlyweight(getState(), getScope(), it.next());
						}
					}
					object = ret;
				}
			};
			return (IMInhibited[])invoc.object;
		}
		else
		{
			Collection params = (Collection)getState().getAttributeValues(getScope(), OAVBDIMetaModel.goal_has_inhibits);
			IMInhibited[] ret = new IMInhibited[params==null? 0: params.size()];
			if(params!=null)
			{
				int i=0;
				for(Iterator it=params.iterator(); it.hasNext(); )
				{
					ret[i++] = new MInhibitedFlyweight(getState(), getScope(), it.next());
				}
			}
			return ret;
		}
	}
	
	/**
	 *  Get the cardinality.
	 *  @retur The cardinality.
	 */
	public int getCardinality()
	{
		if(isExternalThread())
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

	/**
	 *  Create the creation condition.
	 *  @param expression	The expression.
	 *  @param language	The expression language (or null for default java-like language).
	 *  @return The creation condition.
	 */
	public IMECondition createCreationCondition(final String expression, final String language)
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					MConditionFlyweight mcond = MExpressionbaseFlyweight.createCondition(expression, language, getState(), getHandle());
					getState().setAttributeValue(getHandle(), OAVBDIMetaModel.goal_has_creationcondition, mcond.getHandle());
					object	= mcond;
				}
			};
			return (IMECondition)invoc.object;
		}
		else
		{
			MConditionFlyweight mcond = MExpressionbaseFlyweight.createCondition(expression, language, getState(), getHandle());
			getState().setAttributeValue(getHandle(), OAVBDIMetaModel.goal_has_creationcondition, mcond.getHandle());
			return mcond;
		}
	}
	
	/**
	 *  Create the context condition.
	 *  @param expression	The expression.
	 *  @param language	The expression language (or null for default java-like language).
	 *  @return The context condition.
	 */
	public IMECondition createContextCondition(final String expression, final String language)
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					MConditionFlyweight mcond = MExpressionbaseFlyweight.createCondition(expression, language, getState(), getHandle());
					getState().setAttributeValue(getHandle(), OAVBDIMetaModel.goal_has_contextcondition, mcond.getHandle());
					object	= mcond;
				}
			};
			return (IMECondition)invoc.object;
		}
		else
		{
			MConditionFlyweight mcond = MExpressionbaseFlyweight.createCondition(expression, language, getState(), getHandle());
			getState().setAttributeValue(getHandle(), OAVBDIMetaModel.goal_has_contextcondition, mcond.getHandle());
			return mcond;
		}
	}
	
	/**
	 *  Create the drop condition.
	 *  @param expression	The expression.
	 *  @param language	The expression language (or null for default java-like language).
	 *  @return The drop condition.
	 */
	public IMECondition createDropCondition(final String expression, final String language)
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					MConditionFlyweight mcond = MExpressionbaseFlyweight.createCondition(expression, language, getState(), getHandle());
					getState().setAttributeValue(getHandle(), OAVBDIMetaModel.goal_has_dropcondition, mcond.getHandle());
					object	= mcond;
				}
			};
			return (IMECondition)invoc.object;
		}
		else
		{
			MConditionFlyweight mcond = MExpressionbaseFlyweight.createCondition(expression, language, getState(), getHandle());
			getState().setAttributeValue(getHandle(), OAVBDIMetaModel.goal_has_dropcondition, mcond.getHandle());
			return mcond;
		}
	}
	
	/**
	 *  Set the retry flag.
	 *  @param retry The retry flag.
	 */
	public void setRetry(final boolean retry)
	{
		if(isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					getState().setAttributeValue(getHandle(), OAVBDIMetaModel.goal_has_retry, retry);
				}
			};
		}
		else
		{
			getState().setAttributeValue(getHandle(), OAVBDIMetaModel.goal_has_retry, retry);
		}
	}
	
	/**
	 *  Set the retry delay.
	 *  @param retry The retry delay.
	 */
	public void setRetryDelay(final long retrydelay)
	{
		if(isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					getState().setAttributeValue(getHandle(), OAVBDIMetaModel.goal_has_retrydelay, retrydelay);
				}
			};
		}
		else
		{
			getState().setAttributeValue(getHandle(), OAVBDIMetaModel.goal_has_retrydelay, retrydelay);
		}
	}
	
	/**
	 *  Set the recur flag.
	 *  @param recur The recur flag.
	 */
	public void setRecur(final boolean recur)
	{
		if(isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					getState().setAttributeValue(getHandle(), OAVBDIMetaModel.goal_has_recur, recur);
				}
			};
		}
		else
		{
			getState().setAttributeValue(getHandle(), OAVBDIMetaModel.goal_has_recur, recur);
		}
	}
	
	/**
	 *  Set the recur delay.
	 *  @param recur The retry delay.
	 */
	public void setRecurDelay(final long recurdelay)
	{
		if(isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					getState().setAttributeValue(getHandle(), OAVBDIMetaModel.goal_has_recurdelay, recurdelay);
				}
			};
		}
		else
		{
			getState().setAttributeValue(getHandle(), OAVBDIMetaModel.goal_has_recurdelay, recurdelay);
		}
	}
	
	/**
	 *  Create the recur condition.
	 *  @param expression	The expression.
	 *  @param language	The expression language (or null for default java-like language).
	 *  @return The recur condition.
	 */
	public IMECondition createRecurCondition(final String expression, final String language)
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					MConditionFlyweight mcond = MExpressionbaseFlyweight.createCondition(expression, language, getState(), getHandle());
					getState().setAttributeValue(getHandle(), OAVBDIMetaModel.goal_has_recurcondition, mcond.getHandle());
					object	= mcond;
				}
			};
			return (IMECondition)invoc.object;
		}
		else
		{
			MConditionFlyweight mcond = MExpressionbaseFlyweight.createCondition(expression, language, getState(), getHandle());
			getState().setAttributeValue(getHandle(), OAVBDIMetaModel.goal_has_recurcondition, mcond.getHandle());
			return mcond;
		}
	}

	/**
	 *  Set the exlcude mode.
	 *  @param excludemode The exclude mode.
	 */
	public void setExcludeMode(final String excludemode)
	{
		if(isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					getState().setAttributeValue(getHandle(), OAVBDIMetaModel.goal_has_exclude, excludemode);
				}
			};
		}
		else
		{
			getState().setAttributeValue(getHandle(), OAVBDIMetaModel.goal_has_exclude, excludemode);
		}
	}
	
	/**
	 *  Set the rebuild APL flag.
	 *  @param rebuild Rebuild flag.
	 */
	public void setRebuild(final boolean rebuild)
	{
		if(isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					getState().setAttributeValue(getHandle(), OAVBDIMetaModel.goal_has_rebuild, rebuild);
				}
			};
		}
		else
		{
			getState().setAttributeValue(getHandle(), OAVBDIMetaModel.goal_has_rebuild, rebuild);
		}
	}
	
	/**
	 *  Set the unique flag.
	 *  @param unique The unique flag.
	 */
	public void setUnique(final boolean unique)
	{
		if(isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					getState().setAttributeValue(getHandle(), OAVBDIMetaModel.goal_has_unique, unique);
				}
			};
		}
		else
		{
			getState().setAttributeValue(getHandle(), OAVBDIMetaModel.goal_has_unique, unique);
		}
	}
	
	/**
	 *  Add a excluded parameter.
	 *  @param name The name of the excluded parameter.
	 */
	public void addExcludedParameter(final String name)
	{
		if(isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					Object	param	= getState().getAttributeValue(getHandle(), OAVBDIMetaModel.parameterelement_has_parameters, name);
					if(param==null)
						throw new RuntimeException("Parameter not found: "+name);
					getState().addAttributeValue(getHandle(), OAVBDIMetaModel.goal_has_excludedparameter, name);
				}
			};
		}
		else
		{
			Object	param	= getState().getAttributeValue(getHandle(), OAVBDIMetaModel.parameterelement_has_parameters, name);
			if(param==null)
				throw new RuntimeException("Parameter not found: "+name);
			getState().addAttributeValue(getHandle(), OAVBDIMetaModel.goal_has_excludedparameter, name);
		}
	}
	
	/**
	 *  Get inhibited goals.
	 *  @retur The inhibited goals.
	 */
//	public IMInhibitedElement getInhibitedGoals();
	
	/**
	 *  Get the cardinality.
	 *  @retur The cardinality.
	 */
	public void setCardinality(final int card)
	{
		if(isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					getState().setAttributeValue(getHandle(), OAVBDIMetaModel.goal_has_cardinality, card);
				}
			};
		}
		else
		{
			getState().setAttributeValue(getHandle(), OAVBDIMetaModel.goal_has_cardinality, card);
		}
	}
}
