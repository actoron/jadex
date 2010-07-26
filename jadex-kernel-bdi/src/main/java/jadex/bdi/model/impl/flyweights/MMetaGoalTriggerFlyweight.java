package jadex.bdi.model.impl.flyweights;

import jadex.bdi.model.IMMetaGoalTrigger;
import jadex.bdi.model.IMTriggerReference;
import jadex.bdi.model.OAVBDIMetaModel;
import jadex.rules.state.IOAVState;

import java.util.Collection;
import java.util.Iterator;

/**
 * 
 */
public class MMetaGoalTriggerFlyweight extends MTriggerFlyweight implements IMMetaGoalTrigger
{
	//-------- constructors --------
	
	/**
	 *  Create a new element flyweight.
	 */
	public MMetaGoalTriggerFlyweight(IOAVState state, Object scope, Object handle)
	{
		super(state, scope, handle);
	}
	
	//-------- methods --------
	
	/**
	 *  Get the goal events.
	 */
	public IMTriggerReference[]	getGoals()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Collection elems = (Collection)getState().getAttributeValue(getScope(), OAVBDIMetaModel.metagoaltrigger_has_goals);
					IMTriggerReference[] ret = new IMTriggerReference[elems==null? 0: elems.size()];
					if(elems!=null)
					{
						int i=0;
						for(Iterator it=elems.iterator(); it.hasNext(); )
						{
							ret[i++] = new MTriggerReferenceFlyweight(getState(), getScope(), it.next());
						}
					}
					object = ret;
				}
			};
			return (IMTriggerReference[])invoc.object;
		}
		else
		{
			Collection elems = (Collection)getState().getAttributeValue(getScope(), OAVBDIMetaModel.metagoaltrigger_has_goals);
			IMTriggerReference[] ret = new IMTriggerReference[elems==null? 0: elems.size()];
			if(elems!=null)
			{
				int i=0;
				for(Iterator it=elems.iterator(); it.hasNext(); )
				{
					ret[i++] = new MTriggerReferenceFlyweight(getState(), getScope(), it.next());
				}
			}
			return ret;
		}
	}
}
