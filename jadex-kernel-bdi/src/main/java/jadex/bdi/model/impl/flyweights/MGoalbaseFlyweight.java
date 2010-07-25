package jadex.bdi.model.impl.flyweights;

import jadex.bdi.model.IMGoal;
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
					object = new MGoalFlyweight(getState(), getScope(), handle);
				}
			};
			return (IMGoal)invoc.object;
		}
		else
		{
			Object handle = getState().getAttributeValue(getScope(), OAVBDIMetaModel.capability_has_goals, name);
			if(handle==null)
				throw new RuntimeException("Goal not found: "+name);
			return new MGoalFlyweight(getState(), getScope(), handle);
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
							ret[i++] = new MGoalFlyweight(getState(), getScope(), it.next());
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
					ret[i++] = new MGoalFlyweight(getState(), getScope(), it.next());
				}
			}
			return ret;
		}
	}
}
