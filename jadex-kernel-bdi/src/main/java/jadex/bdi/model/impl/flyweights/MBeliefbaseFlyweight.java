package jadex.bdi.model.impl.flyweights;

import jadex.bdi.model.IMBelief;
import jadex.bdi.model.IMBeliefReference;
import jadex.bdi.model.IMBeliefSet;
import jadex.bdi.model.IMBeliefSetReference;
import jadex.bdi.model.IMBeliefbase;
import jadex.bdi.model.OAVBDIMetaModel;
import jadex.bdi.model.editable.IMEBelief;
import jadex.bdi.model.editable.IMEBeliefReference;
import jadex.bdi.model.editable.IMEBeliefSet;
import jadex.bdi.model.editable.IMEBeliefSetReference;
import jadex.bdi.model.editable.IMEBeliefbase;
import jadex.rules.state.IOAVState;

import java.util.Collection;
import java.util.Iterator;

/**
 *  Flyweight for the belief base model.
 */
public class MBeliefbaseFlyweight extends MElementFlyweight implements IMBeliefbase, IMEBeliefbase
{
	//-------- constructors --------
	
	/**
	 *  Create a new beliefbase flyweight.
	 *  @param state	The state.
	 *  @param scope	The scope handle.
	 */
	public MBeliefbaseFlyweight(IOAVState state, Object scope)
	{
		super(state, scope, scope);
	}
	
	//-------- methods --------

    /**
	 *  Get a belief for a name.
	 *  @param name	The belief name.
	 */
	public IMBelief getBelief(final String name)
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation(name)
			{
				public void run()
				{
					Object handle = getState().getAttributeValue(getScope(), OAVBDIMetaModel.capability_has_beliefs, name);
					if(handle==null)
						throw new RuntimeException("Belief not found: "+name);
					object = new MBeliefFlyweight(getState(), getScope(), handle);
				}
			};
			return (IMBelief)invoc.object;
		}
		else
		{
			Object handle = getState().getAttributeValue(getScope(), OAVBDIMetaModel.capability_has_beliefs, name);
			if(handle==null)
				throw new RuntimeException("Belief not found: "+name);
			return new MBeliefFlyweight(getState(), getScope(), handle);
		}
	}

	/**
	 *  Get a belief set for a name.
	 *  @param name	The belief set name.
	 */
	public IMBeliefSet getBeliefSet(final String name)
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation(name)
			{
				public void run()
				{
					Object handle = getState().getAttributeValue(getScope(), OAVBDIMetaModel.capability_has_beliefsets, name);
					if(handle==null)
						throw new RuntimeException("Beliefset not found: "+name);
					object = new MBeliefFlyweight(getState(), getScope(), handle);
				}
			};
			return (IMBeliefSet)invoc.object;
		}
		else
		{
			Object handle = getState().getAttributeValue(getScope(), OAVBDIMetaModel.capability_has_beliefsets, name);
			if(handle==null)
				throw new RuntimeException("Beliefset not found: "+name);
			return new MBeliefSetFlyweight(getState(), getScope(), handle);
		}
	}

	/**
	 *  Returns all beliefs.
	 *  @return All beliefs.
	 */
	public IMBelief[] getBeliefs()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Collection elems = (Collection)getState().getAttributeValues(getScope(), OAVBDIMetaModel.capability_has_beliefs);
					IMBelief[] ret = new IMBelief[elems==null? 0: elems.size()];
					if(elems!=null)
					{
						int i=0;
						for(Iterator it=elems.iterator(); it.hasNext(); )
						{
							ret[i++] = new MBeliefFlyweight(getState(), getScope(), it.next());
						}
					}
					object = ret;
				}
			};
			return (IMBelief[])invoc.object;
		}
		else
		{
			Collection elems = (Collection)getState().getAttributeValues(getScope(), OAVBDIMetaModel.capability_has_beliefs);
			IMBelief[] ret = new IMBelief[elems==null? 0: elems.size()];
			if(elems!=null)
			{
				int i=0;
				for(Iterator it=elems.iterator(); it.hasNext(); )
				{
					ret[i++] = new MBeliefFlyweight(getState(), getScope(), it.next());
				}
			}
			return ret;
		}
	}

	/**
	 *  Return all belief sets.
	 *  @return All belief sets.
	 */
	public IMBeliefSet[] getBeliefSets()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Collection elems = (Collection)getState().getAttributeValues(getScope(), OAVBDIMetaModel.capability_has_beliefsets);
					IMBeliefSet[] ret = new IMBeliefSet[elems==null? 0: elems.size()];
					if(elems!=null)
					{
						int i=0;
						for(Iterator it=elems.iterator(); it.hasNext(); )
						{
							ret[i++] = new MBeliefSetFlyweight(getState(), getScope(), it.next());
						}
					}
					object = ret;
				}
			};
			return (IMBeliefSet[])invoc.object;
		}
		else
		{
			Collection elems = (Collection)getState().getAttributeValues(getScope(), OAVBDIMetaModel.capability_has_beliefsets);
			IMBeliefSet[] ret = new IMBeliefSet[elems==null? 0: elems.size()];
			if(elems!=null)
			{
				int i=0;
				for(Iterator it=elems.iterator(); it.hasNext(); )
				{
					ret[i++] = new MBeliefSetFlyweight(getState(), getScope(), it.next());
				}
			}
			return ret;
		}
	}
	
	/**
	 *  Get a belief for a name.
	 *  @param name	The belief name.
	 */
	public IMBeliefReference getBeliefReference(final String name)
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Object handle = getState().getAttributeValue(getScope(), OAVBDIMetaModel.capability_has_beliefrefs, name);
					if(handle==null)
						throw new RuntimeException("Belief reference not found: "+name);
					object = new MBeliefReferenceFlyweight(getState(), getScope(), handle);
				}
			};
			return (IMBeliefReference)invoc.object;
		}
		else
		{
			Object handle = getState().getAttributeValue(getScope(), OAVBDIMetaModel.capability_has_beliefrefs, name);
			if(handle==null)
				throw new RuntimeException("Belief reference not found: "+name);
			return new MBeliefReferenceFlyweight(getState(), getScope(), handle);
		}
	}

	/**
	 *  Returns all belief references.
	 *  @return All belief references.
	 */
	public IMBeliefReference[] getBeliefReferences()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Collection elems = (Collection)getState().getAttributeValues(getScope(), OAVBDIMetaModel.capability_has_beliefrefs);
					IMBeliefReference[] ret = new IMBeliefReference[elems==null? 0: elems.size()];
					if(elems!=null)
					{
						int i=0;
						for(Iterator it=elems.iterator(); it.hasNext(); )
						{
							ret[i++] = new MBeliefReferenceFlyweight(getState(), getScope(), it.next());
						}
					}
					object = ret;
				}
			};
			return (IMBeliefReference[])invoc.object;
		}
		else
		{
			Collection elems = (Collection)getState().getAttributeValues(getScope(), OAVBDIMetaModel.capability_has_beliefrefs);
			IMBeliefReference[] ret = new IMBeliefReference[elems==null? 0: elems.size()];
			if(elems!=null)
			{
				int i=0;
				for(Iterator it=elems.iterator(); it.hasNext(); )
				{
					ret[i++] = new MBeliefReferenceFlyweight(getState(), getScope(), it.next());
				}
			}
			return ret;
		}
	}
	
	/**
	 *  Get a beliefset reference for a name.
	 *  @param name	The beliefset reference name.
	 */
	public IMBeliefSetReference getBeliefSetReference(final String name)
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Object handle = getState().getAttributeValue(getScope(), OAVBDIMetaModel.capability_has_beliefsetrefs, name);
					if(handle==null)
						throw new RuntimeException("Beliefset reference not found: "+name);
					object = new MBeliefReferenceFlyweight(getState(), getScope(), handle);
				}
			};
			return (IMBeliefSetReference)invoc.object;
		}
		else
		{
			Object handle = getState().getAttributeValue(getScope(), OAVBDIMetaModel.capability_has_beliefsetrefs, name);
			if(handle==null)
				throw new RuntimeException("Beliefset reference not found: "+name);
			return new MBeliefSetReferenceFlyweight(getState(), getScope(), handle);
		}
	}

	/**
	 *  Returns all beliefset references.
	 *  @return All beliefset references.
	 */
	public IMBeliefSetReference[] getBeliefSetReferences()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Collection elems = (Collection)getState().getAttributeValues(getScope(), OAVBDIMetaModel.capability_has_beliefsetrefs);
					IMBeliefSetReference[] ret = new IMBeliefSetReference[elems==null? 0: elems.size()];
					if(elems!=null)
					{
						int i=0;
						for(Iterator it=elems.iterator(); it.hasNext(); )
						{
							ret[i++] = new MBeliefSetReferenceFlyweight(getState(), getScope(), it.next());
						}
					}
					object = ret;
				}
			};
			return (IMBeliefSetReference[])invoc.object;
		}
		else
		{
			Collection elems = (Collection)getState().getAttributeValues(getScope(), OAVBDIMetaModel.capability_has_beliefsetrefs);
			IMBeliefSetReference[] ret = new IMBeliefSetReference[elems==null? 0: elems.size()];
			if(elems!=null)
			{
				int i=0;
				for(Iterator it=elems.iterator(); it.hasNext(); )
				{
					ret[i++] = new MBeliefSetReferenceFlyweight(getState(), getScope(), it.next());
				}
			}
			return ret;
		}
	}
	
	//-------- editable interface --------
	
	/**
	 *  Create a belief for a name.
	 *  @param name	The belief name.
	 */
	public IMEBelief createBelief(final String name)
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Object elem = getState().createObject(OAVBDIMetaModel.belief_type);
					getState().setAttributeValue(elem, OAVBDIMetaModel.modelelement_has_name, name);
					getState().addAttributeValue(getHandle(), OAVBDIMetaModel.capability_has_beliefs, elem);
					object	= new MBeliefFlyweight(getState(), getHandle(), elem);
				}
			};
			return (IMEBelief)invoc.object;
		}
		else
		{
			Object elem = getState().createObject(OAVBDIMetaModel.belief_type);
			getState().setAttributeValue(elem, OAVBDIMetaModel.modelelement_has_name, name);
			getState().addAttributeValue(getHandle(), OAVBDIMetaModel.capability_has_beliefs, elem);
			return new MBeliefFlyweight(getState(), getHandle(), elem);
		}
	}

	/**
	 *  Create a belief set for a name.
	 *  @param name	The belief set name.
	 */
	public IMEBeliefSet createBeliefSet(final String name)
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Object elem = getState().createObject(OAVBDIMetaModel.beliefset_type);
					getState().setAttributeValue(elem, OAVBDIMetaModel.modelelement_has_name, name);
					getState().addAttributeValue(getHandle(), OAVBDIMetaModel.capability_has_beliefsets, elem);
					object	= new MBeliefFlyweight(getState(), getHandle(), elem);
				}
			};
			return (IMEBeliefSet)invoc.object;
		}
		else
		{
			Object elem = getState().createObject(OAVBDIMetaModel.beliefset_type);
			getState().setAttributeValue(elem, OAVBDIMetaModel.modelelement_has_name, name);
			getState().addAttributeValue(getHandle(), OAVBDIMetaModel.capability_has_beliefsets, elem);
			return new MBeliefSetFlyweight(getState(), getHandle(), elem);
		}
	}
	
	/**
	 *  Create a belief reference for a name.
	 *  @param name	The belief reference name.
	 */
	public IMEBeliefReference createBeliefReference(final String name, final String ref)
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Object elem = getState().createObject(OAVBDIMetaModel.beliefreference_type);
					getState().setAttributeValue(elem, OAVBDIMetaModel.modelelement_has_name, name);
					if(ref!=null)
						getState().setAttributeValue(elem, OAVBDIMetaModel.elementreference_has_concrete, ref);
					getState().addAttributeValue(getHandle(), OAVBDIMetaModel.capability_has_beliefrefs, elem);
					object	= new MBeliefReferenceFlyweight(getState(), getHandle(), elem);
				}
			};
			return (IMEBeliefReference)invoc.object;
		}
		else
		{
			Object elem = getState().createObject(OAVBDIMetaModel.beliefreference_type);
			getState().setAttributeValue(elem, OAVBDIMetaModel.modelelement_has_name, name);
			if(ref!=null)
				getState().setAttributeValue(elem, OAVBDIMetaModel.elementreference_has_concrete, ref);
			getState().addAttributeValue(getHandle(), OAVBDIMetaModel.capability_has_beliefrefs, elem);
			return new MBeliefReferenceFlyweight(getState(), getHandle(), elem);

		}
	}

	/**
	 *  Create a beliefset reference for a name.
	 *  @param name	The beliefset reference name.
	 */
	public IMEBeliefSetReference createBeliefSetReference(final String name, final String ref)
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Object elem = getState().createObject(OAVBDIMetaModel.beliefsetreference_type);
					getState().setAttributeValue(elem, OAVBDIMetaModel.modelelement_has_name, name);
					if(ref!=null)
						getState().setAttributeValue(elem, OAVBDIMetaModel.elementreference_has_concrete, ref);
					getState().addAttributeValue(getHandle(), OAVBDIMetaModel.capability_has_beliefsetrefs, elem);
					object	= new MBeliefReferenceFlyweight(getState(), getHandle(), elem);
				}
			};
			return (IMEBeliefSetReference)invoc.object;
		}
		else
		{
			Object elem = getState().createObject(OAVBDIMetaModel.beliefsetreference_type);
			getState().setAttributeValue(elem, OAVBDIMetaModel.modelelement_has_name, name);
			if(ref!=null)
				getState().setAttributeValue(elem, OAVBDIMetaModel.elementreference_has_concrete, ref);
			getState().addAttributeValue(getHandle(), OAVBDIMetaModel.capability_has_beliefsetrefs, elem);
			return new MBeliefSetReferenceFlyweight(getState(), getHandle(), elem);
		}
	}
	
}