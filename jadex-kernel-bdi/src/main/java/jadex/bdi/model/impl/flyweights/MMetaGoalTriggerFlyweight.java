package jadex.bdi.model.impl.flyweights;

import jadex.bdi.model.IMMetaGoalTrigger;
import jadex.bdi.model.IMTriggerReference;
import jadex.bdi.model.OAVBDIMetaModel;
import jadex.bdi.model.editable.IMEMetaGoalTrigger;
import jadex.bdi.model.editable.IMETriggerReference;
import jadex.rules.state.IOAVState;

import java.util.Collection;
import java.util.Iterator;

/**
 * 
 */
public class MMetaGoalTriggerFlyweight extends MTriggerFlyweight implements IMMetaGoalTrigger, IMEMetaGoalTrigger
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
					Collection elems = (Collection)getState().getAttributeValues(getScope(), OAVBDIMetaModel.metagoaltrigger_has_goals);
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
			Collection elems = (Collection)getState().getAttributeValues(getScope(), OAVBDIMetaModel.metagoaltrigger_has_goals);
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
	
	/**
	 *  Add a goal.
	 *  @param reference	The referenced goal.
	 */
	public IMETriggerReference	createGoal(final String reference)
	{
		if(isExternalThread())
		{
			AgentInvocation	invoc	= new AgentInvocation()
			{
				public void run()
				{
					Object	mtr	= getState().createObject(OAVBDIMetaModel.triggerreference_type);
					getState().setAttributeValue(mtr, OAVBDIMetaModel.triggerreference_has_ref, reference);
					getState().addAttributeValue(getHandle(), OAVBDIMetaModel.metagoaltrigger_has_goals, mtr);
					object	= new MTriggerReferenceFlyweight(getState(), getScope(), mtr);
				}
			};
			return (IMETriggerReference)invoc.object;
		}
		else
		{
			Object	mtr	= getState().createObject(OAVBDIMetaModel.triggerreference_type);
			getState().setAttributeValue(mtr, OAVBDIMetaModel.triggerreference_has_ref, reference);
			getState().addAttributeValue(getHandle(), OAVBDIMetaModel.metagoaltrigger_has_goals, mtr);
			return new MTriggerReferenceFlyweight(getState(), getScope(), mtr);
		}
		
	}
}
