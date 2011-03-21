package jadex.bdi.model.impl.flyweights;

import jadex.bdi.model.IMGoal;
import jadex.bdi.model.IMGoalReference;
import jadex.bdi.model.IMGoalbase;
import jadex.bdi.model.OAVBDIMetaModel;
import jadex.bdi.model.editable.IMEAchieveGoal;
import jadex.bdi.model.editable.IMEGoalReference;
import jadex.bdi.model.editable.IMEGoalbase;
import jadex.bdi.model.editable.IMEMaintainGoal;
import jadex.bdi.model.editable.IMEMetaGoal;
import jadex.bdi.model.editable.IMEPerformGoal;
import jadex.bdi.model.editable.IMEQueryGoal;
import jadex.rules.state.IOAVState;

import java.util.Collection;
import java.util.Iterator;

/**
 *  Flyweight for the belief base model.
 */
public class MGoalbaseFlyweight extends MElementFlyweight implements IMGoalbase, IMEGoalbase
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
		if(isExternalThread())
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
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Collection elems = (Collection)getState().getAttributeValues(getScope(), OAVBDIMetaModel.capability_has_goals);
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
			Collection elems = (Collection)getState().getAttributeValues(getScope(), OAVBDIMetaModel.capability_has_goals);
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
		if(isExternalThread())
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
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Collection elems = (Collection)getState().getAttributeValues(getScope(), OAVBDIMetaModel.capability_has_goalrefs);
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
			Collection elems = (Collection)getState().getAttributeValues(getScope(), OAVBDIMetaModel.capability_has_goalrefs);
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
	 *  Create a perform goal for a name.
	 *  @param name	The goal name.
	 */
	public IMEPerformGoal createPerformGoal(final String name)
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Object elem = getState().createObject(OAVBDIMetaModel.performgoal_type);
					getState().setAttributeValue(elem, OAVBDIMetaModel.modelelement_has_name, name);
					getState().addAttributeValue(getHandle(), OAVBDIMetaModel.capability_has_goals, elem);
					object = new MPerformGoalFlyweight(getState(), getScope(), elem);
				}
			};
			return (IMEPerformGoal)invoc.object;
		}
		else
		{
			Object elem = getState().createObject(OAVBDIMetaModel.performgoal_type);
			getState().setAttributeValue(elem, OAVBDIMetaModel.modelelement_has_name, name);
			getState().addAttributeValue(getHandle(), OAVBDIMetaModel.capability_has_goals, elem);
			return new MPerformGoalFlyweight(getState(), getScope(), elem);
		}
	}
	
	/**
	 *  Create a achieve goal for a name.
	 *  @param name	The goal name.
	 */
	public IMEAchieveGoal createAchieveGoal(final String name)
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Object elem = getState().createObject(OAVBDIMetaModel.achievegoal_type);
					getState().setAttributeValue(elem, OAVBDIMetaModel.modelelement_has_name, name);
					getState().addAttributeValue(getHandle(), OAVBDIMetaModel.capability_has_goals, elem);
					object = new MAchieveGoalFlyweight(getState(), getScope(), elem);
				}
			};
			return (IMEAchieveGoal)invoc.object;
		}
		else
		{
			Object elem = getState().createObject(OAVBDIMetaModel.achievegoal_type);
			getState().setAttributeValue(elem, OAVBDIMetaModel.modelelement_has_name, name);
			getState().addAttributeValue(getHandle(), OAVBDIMetaModel.capability_has_goals, elem);
			return new MAchieveGoalFlyweight(getState(), getScope(), elem);
		}
	}
	
	/**
	 *  Create a query goal for a name.
	 *  @param name	The goal name.
	 */
	public IMEQueryGoal createQueryGoal(final String name)
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Object elem = getState().createObject(OAVBDIMetaModel.querygoal_type);
					getState().setAttributeValue(elem, OAVBDIMetaModel.modelelement_has_name, name);
					getState().addAttributeValue(getHandle(), OAVBDIMetaModel.capability_has_goals, elem);
					object = new MQueryGoalFlyweight(getState(), getScope(), elem);
				}
			};
			return (IMEQueryGoal)invoc.object;
		}
		else
		{
			Object elem = getState().createObject(OAVBDIMetaModel.querygoal_type);
			getState().setAttributeValue(elem, OAVBDIMetaModel.modelelement_has_name, name);
			getState().addAttributeValue(getHandle(), OAVBDIMetaModel.capability_has_goals, elem);
			return new MQueryGoalFlyweight(getState(), getScope(), elem);
		}
	}
	
	/**
	 *  Create a maintain goal for a name.
	 *  @param name	The goal name.
	 */
	public IMEMaintainGoal createMaintainGoal(final String name)
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Object elem = getState().createObject(OAVBDIMetaModel.maintaingoal_type);
					getState().setAttributeValue(elem, OAVBDIMetaModel.modelelement_has_name, name);
					getState().addAttributeValue(getHandle(), OAVBDIMetaModel.capability_has_goals, elem);
					object = new MMaintainGoalFlyweight(getState(), getScope(), elem);
				}
			};
			return (IMEMaintainGoal)invoc.object;
		}
		else
		{
			Object elem = getState().createObject(OAVBDIMetaModel.maintaingoal_type);
			getState().setAttributeValue(elem, OAVBDIMetaModel.modelelement_has_name, name);
			getState().addAttributeValue(getHandle(), OAVBDIMetaModel.capability_has_goals, elem);
			return new MMaintainGoalFlyweight(getState(), getScope(), elem);
		}
	}
	
	/**
	 *  Create a meta goal for a name.
	 *  @param name	The goal name.
	 */
	public IMEMetaGoal createMetaGoal(final String name)
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Object elem = getState().createObject(OAVBDIMetaModel.metagoal_type);
					getState().setAttributeValue(elem, OAVBDIMetaModel.modelelement_has_name, name);
					getState().addAttributeValue(getHandle(), OAVBDIMetaModel.capability_has_goals, elem);
					object = new MMetaGoalFlyweight(getState(), getScope(), elem);
				}
			};
			return (IMEMetaGoal)invoc.object;
		}
		else
		{
			Object elem = getState().createObject(OAVBDIMetaModel.metagoal_type);
			getState().setAttributeValue(elem, OAVBDIMetaModel.modelelement_has_name, name);
			getState().addAttributeValue(getHandle(), OAVBDIMetaModel.capability_has_goals, elem);
			return new MMetaGoalFlyweight(getState(), getScope(), elem);
		}
	}

	/**
	 *  Get a goal reference for a name.
	 *  @param name	The goal reference name.
	 *  @param ref The referenced element name.
	 */
	public IMEGoalReference createGoalReference(final String name, final String ref)
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Object elem = getState().createObject(OAVBDIMetaModel.goalreference_type);
					getState().setAttributeValue(elem, OAVBDIMetaModel.modelelement_has_name, name);
					if(ref!=null)
						getState().setAttributeValue(elem, OAVBDIMetaModel.elementreference_has_concrete, ref);
					getState().addAttributeValue(getHandle(), OAVBDIMetaModel.capability_has_goalrefs, elem);
					object = new MGoalReferenceFlyweight(getState(), getScope(), elem);
				}
			};
			return (IMEGoalReference)invoc.object;
		}
		else
		{
			Object elem = getState().createObject(OAVBDIMetaModel.goalreference_type);
			getState().setAttributeValue(elem, OAVBDIMetaModel.modelelement_has_name, name);
			if(ref!=null)
				getState().setAttributeValue(elem, OAVBDIMetaModel.elementreference_has_concrete, ref);
			getState().addAttributeValue(getHandle(), OAVBDIMetaModel.capability_has_goalrefs, elem);
			return new MGoalReferenceFlyweight(getState(), getScope(), elem);
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
