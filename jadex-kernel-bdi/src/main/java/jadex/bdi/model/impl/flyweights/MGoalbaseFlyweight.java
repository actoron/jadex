package jadex.bdi.model.impl.flyweights;

import jadex.bdi.model.IMGoal;
import jadex.bdi.model.IMGoalReference;
import jadex.bdi.model.IMGoalbase;
import jadex.bdi.model.OAVBDIMetaModel;
import jadex.rules.state.IOAVState;

import java.util.Collection;
import java.util.Iterator;

/**
 *  Flyweight for the belief base model.
 */
public class MGoalbaseFlyweight extends MElementFlyweight implements IMGoalbase 
{
	//-------- constructors --------
	
	/**
	 *  Create a new beliefbase flyweight.
	 */
	public MGoalbaseFlyweight(IOAVState state, Object scope)
	{
		super(state, scope, scope);
	}
	
	//-------- methods concerning beliefs --------

    /**
	 *  Get a belief for a name.
	 *  @param name	The belief name.
	 */
	public IMGoal getGoal(final String name)
	{
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation(name)
			{
				public void run()
				{
					Object handle = getState().getAttributeValue(getScope(), OAVBDIMetaModel.capability_has_goals, name);
					if(handle==null)
						throw new RuntimeException("Goal not found: "+name);
					object = createFlyweight(getState(), getScope(), handle);
				}
			};
			return (IMGoal)invoc.object;
		}
		else
		{
			Object handle = getState().getAttributeValue(getScope(), OAVBDIMetaModel.capability_has_goals, name);
			if(handle==null)
				throw new RuntimeException("Goal not found: "+name);
			return createFlyweight(getState(), getScope(), handle);
		}
	}

	/**
	 *  Returns all goals.
	 *  @return All goals.
	 */
	public IMGoal[] getGoals()
	{
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Collection elems = (Collection)getState().getAttributeValue(getScope(), OAVBDIMetaModel.capability_has_goals);
					IMGoal[] ret = new IMGoal[elems==null? 0: elems.size()];
					if(elems!=null)
					{
						int i=0;
						for(Iterator it=elems.iterator(); it.hasNext(); )
						{
							ret[i++] = createFlyweight(getState(), getScope(), it.next());
						}
					}
					object = ret;
				}
			};
			return (IMGoal[])invoc.object;
		}
		else
		{
			Collection elems = (Collection)getState().getAttributeValue(getScope(), OAVBDIMetaModel.capability_has_goals);
			IMGoal[] ret = new IMGoal[elems==null? 0: elems.size()];
			if(elems!=null)
			{
				int i=0;
				for(Iterator it=elems.iterator(); it.hasNext(); )
				{
					ret[i++] = createFlyweight(getState(), getScope(), it.next());
				}
			}
			return ret;
		}
	}
	
	/**
	 *  Get a goal reference for a name.
	 *  @param name	The goal reference name.
	 */
	public IMGoalReference getGoalReference(final String name)
	{
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Object handle = getState().getAttributeValue(getScope(), OAVBDIMetaModel.capability_has_goalrefs, name);
					if(handle==null)
						throw new RuntimeException("Goal reference not found: "+name);
					object = new MGoalReferenceFlyweight(getState(), getScope(), handle);
				}
			};
			return (IMGoalReference)invoc.object;
		}
		else
		{
			Object handle = getState().getAttributeValue(getScope(), OAVBDIMetaModel.capability_has_goalrefs, name);
			if(handle==null)
				throw new RuntimeException("Goal reference not found: "+name);
			return new MGoalReferenceFlyweight(getState(), getScope(), handle);
		}
	}

	/**
	 *  Get all goal references.
	 *  @param name	Goal references.
	 */
	public IMGoalReference[] getGoalReferences()
	{
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Collection elems = (Collection)getState().getAttributeValue(getScope(), OAVBDIMetaModel.capability_has_goalrefs);
					IMGoalReference[] ret = new IMGoalReference[elems==null? 0: elems.size()];
					if(elems!=null)
					{
						int i=0;
						for(Iterator it=elems.iterator(); it.hasNext(); )
						{
							ret[i++] = new MGoalReferenceFlyweight(getState(), getScope(), it.next());
						}
					}
					object = ret;
				}
			};
			return (IMGoalReference[])invoc.object;
		}
		else
		{
			Collection elems = (Collection)getState().getAttributeValue(getScope(), OAVBDIMetaModel.capability_has_goalrefs);
			IMGoalReference[] ret = new IMGoalReference[elems==null? 0: elems.size()];
			if(elems!=null)
			{
				int i=0;
				for(Iterator it=elems.iterator(); it.hasNext(); )
				{
					ret[i++] = new MGoalReferenceFlyweight(getState(), getScope(), it.next());
				}
			}
			return ret;
		}
	}
	
	/**
	 *  Create a goal flyweight.
	 */
	public static IMGoal createFlyweight(IOAVState state, Object scope, Object handle)
	{
		IMGoal ret = null;
		
		if(OAVBDIMetaModel.metagoal_type.equals(state.getType(handle)))
		{
			ret = new MMetaGoalFlyweight(state, scope, handle);
		}
		else if(OAVBDIMetaModel.performgoal_type.equals(state.getType(handle)))
		{
			ret = new MPerformGoalFlyweight(state, scope, handle);
		}
		else if(OAVBDIMetaModel.achievegoal_type.equals(state.getType(handle)))
		{
			ret = new MAchieveGoalFlyweight(state, scope, handle);
		}
		else if(OAVBDIMetaModel.querygoal_type.equals(state.getType(handle)))
		{
			ret = new MQueryGoalFlyweight(state, scope, handle);
		}
		else if(OAVBDIMetaModel.maintaingoal_type.equals(state.getType(handle)))
		{
			ret = new MMaintainGoalFlyweight(state, scope, handle);
		}
		else
		{
			throw new RuntimeException("Unknown goal type: "+handle);
		}
		
		return ret;
	}
}
