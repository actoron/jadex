package jadex.bdiv3x.runtime;

import jadex.bdiv3.model.IBDIModel;
import jadex.bdiv3.model.MBelief;
import jadex.bdiv3.model.MConfiguration;
import jadex.bdiv3.runtime.ChangeEvent;
import jadex.bdiv3.runtime.impl.RElement;
import jadex.bdiv3.runtime.wrappers.EventPublisher;
import jadex.bdiv3.runtime.wrappers.ListWrapper;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.modelinfo.UnparsedExpression;
import jadex.commons.SUtil;
import jadex.javaparser.IMapAccess;
import jadex.javaparser.SJavaParser;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  Runtime element for storing beliefs.
 */
public class RBeliefbase extends RElement implements IBeliefbase, IMapAccess
{
	/** The beliefs. */
	protected Map<String, IBelief> beliefs;
	
	/** The belief sets. */
	protected Map<String, IBeliefSet> beliefsets;
	
	/**
	 *  Create a new beliefbase.
	 */
	public RBeliefbase(IInternalAccess agent)
	{
		super(null, agent);
	}
	
	/**
	 *  
	 */
	public void init()
	{	
		Map<String, Object> args = getAgent().getComponentFeature(IArgumentsResultsFeature.class).getArguments();
		Map<String, Object> vals = args==null? new HashMap<String, Object>(): new HashMap<String, Object>(args);
		
		String confname = getAgent().getConfiguration();
		if(confname!=null)
		{
			IBDIModel bdimodel = (IBDIModel)getAgent().getModel().getRawModel();
			MConfiguration mconf = bdimodel.getCapability().getConfiguration(confname);
			
			if(mconf!=null)
			{
				// Set initial belief values
				List<UnparsedExpression> ibels = mconf.getInitialBeliefs();
				if(ibels!=null)
				{
					for(UnparsedExpression uexp: ibels)
					{
						try
						{
							MBelief mbel = bdimodel.getCapability().getBelief(uexp.getName());
							Object val = SJavaParser.parseExpression(uexp, getAgent().getModel().getAllImports(), getAgent().getClassLoader()).getValue(getAgent().getFetcher());
							vals.put(mbel.getName(), val);
						}
						catch(RuntimeException e)
						{
							throw e;
						}
						catch(Exception e)
						{
							throw new RuntimeException(e);
						}
					}
				}
			}
		}
		
		List<MBelief> mbels = getMCapability().getBeliefs();
		if(mbels!=null)
		{
			for(MBelief mbel: mbels)
			{
				if(!mbel.isMulti(agent.getClassLoader()))
				{
					if(vals.containsKey(mbel.getName())) // mbel.isExported() && 
					{	
						addBelief(new RBelief(mbel, getAgent(), vals.get(mbel.getName())));
					}
					else
					{
						addBelief(new RBelief(mbel, getAgent()));
					}
				}
				else
				{
					if(vals.containsKey(mbel.getName())) // mbel.isExported() && 
					{	
						addBeliefSet(new RBeliefSet(mbel, getAgent(), (Object[])vals.get(mbel.getName())));
					}
					else
					{
						addBeliefSet(new RBeliefSet(mbel, getAgent()));
					}
				}
			}
		}
	}
	
	/**
	 *  Get a belief for a name.
	 *  @param name	The belief name.
	 */
	public IBelief getBelief(String name)
	{
		if(beliefs==null || !beliefs.containsKey(name))
			throw new RuntimeException("Belief not found: "+name);
		return beliefs.get(name);
	}

	/**
	 *  Get a belief set for a name.
	 *  @param name	The belief set name.
	 */
	public IBeliefSet getBeliefSet(String name)
	{
		if(beliefsets==null || !beliefsets.containsKey(name))
			throw new RuntimeException("Beliefset not found: "+name);
		return beliefsets.get(name);
	}

	/**
	 *  Returns <tt>true</tt> if this beliefbase contains a belief with the
	 *  specified name.
	 *  @param name the name of a belief.
	 *  @return <code>true</code> if contained, <code>false</code> is not contained, or
	 *          the specified name refer to a belief set.
	 *  @see #containsBeliefSet(java.lang.String)
	 */
	public boolean containsBelief(String name)
	{
		return beliefs==null? false: beliefs.containsKey(name);
	}

	/**
	 *  Returns <tt>true</tt> if this beliefbase contains a belief set with the
	 *  specified name.
	 *  @param name the name of a belief set.
	 *  @return <code>true</code> if contained, <code>false</code> is not contained, or
	 *          the specified name refer to a belief.
	 *  @see #containsBelief(java.lang.String)
	 */
	public boolean containsBeliefSet(String name)
	{
		return beliefsets==null? false: beliefsets.containsKey(name);
	}

	/**
	 *  Returns the names of all beliefs.
	 *  @return the names of all beliefs.
	 */
	public String[] getBeliefNames()
	{
		return beliefs==null? SUtil.EMPTY_STRING_ARRAY: beliefs.keySet().toArray(new String[beliefs.size()]);
	}

	/**
	 *  Returns the names of all belief sets.
	 *  @return the names of all belief sets.
	 */
	public String[] getBeliefSetNames()
	{
		return beliefsets==null? SUtil.EMPTY_STRING_ARRAY: beliefsets.keySet().toArray(new String[beliefsets.size()]);
	}

	/**
	 *  Add a belief.
	 *  @param bel The belief.
	 */
	public void addBelief(RBelief bel)
	{
		if(beliefs==null)
			beliefs = new HashMap<String, IBelief>();
		beliefs.put(bel.getName(), bel);
	}
	
	/**
	 *  Add a beliefset.
	 *  @param bel The beliefset.
	 */
	public void addBeliefSet(RBeliefSet belset)
	{
		if(beliefsets==null)
			beliefsets = new HashMap<String, IBeliefSet>();
		beliefsets.put(belset.getName(), belset);
	}
	
	/**
	 *  Get an object from the map.
	 *  @param key The key
	 *  @return The value.
	 */
	public Object get(Object key)
	{
		String name = (String)key;
		Object ret = null;
		if(containsBelief(name))
		{
			ret = getBelief(name).getFact();
		}
		else if(containsBeliefSet(name))
		{
			ret = getBeliefSet(name).getFacts();
		}
		else
		{
			throw new RuntimeException("Unknown belief/set: "+name);
		}
		return ret;
	}
	
	/**
	 *  Create a belief with given key and class.
	 *  @param key The key identifying the belief.
	 *  @param clazz The class.
	 *  @deprecated
	 */
//		public void createBelief(String key, Class clazz, int update);

	/**
	 *  Create a belief with given key and class.
	 *  @param key The key identifying the belief.
	 *  @param clazz The class.
	 *  @deprecated
	 */
//		public void createBeliefSet(String key, Class clazz, int update);

	/**
	 *  Delete a belief with given key.
	 *  @param key The key identifying the belief.
	 *  @deprecated
	 */
//		public void deleteBelief(String key);

	/**
	 *  Delete a belief with given key.
	 *  @param key The key identifying the belief.
	 *  @deprecated
	 */
//		public void deleteBeliefSet(String key);

	/**
	 *  Register a new belief.
	 *  @param mbelief The belief model.
	 */
//		public void registerBelief(IMBelief mbelief);

	/**
	 *  Register a new beliefset model.
	 *  @param mbeliefset The beliefset model.
	 */
//		public void registerBeliefSet(IMBeliefSet mbeliefset);

	/**
	 *  Register a new belief reference.
	 *  @param mbeliefref The belief reference model.
	 */
//		public void registerBeliefReference(IMBeliefReference mbeliefref);

	/**
	 *  Register a new beliefset reference model.
	 *  @param mbeliefsetref The beliefset reference model.
	 */
//		public void registerBeliefSetReference(IMBeliefSetReference mbeliefsetref);

	/**
	 *  Deregister a belief model.
	 *  @param mbelief The belief model.
	 */
//		public void deregisterBelief(IMBelief mbelief);

	/**
	 *  Deregister a beliefset model.
	 *  @param mbeliefset The beliefset model.
	 */
//		public void deregisterBeliefSet(IMBeliefSet mbeliefset);

	/**
	 *  Deregister a belief reference model.
	 *  @param mbeliefref The belief reference model.
	 */
//		public void deregisterBeliefReference(IMBeliefReference mbeliefref);

	/**
	 *  Deregister a beliefset reference model.
	 *  @param mbeliefsetref The beliefset reference model.
	 */
//		public void deregisterBeliefSetReference(IMBeliefSetReference mbeliefsetref);

	/**
	 * 
	 */
	public class RBelief extends RElement implements IBelief
	{
		/** The value. */
		protected Object value;
		
		/** The publisher. */
		protected EventPublisher publisher;

		/**
		 *  Create a new parameter.
		 *  @param modelelement The model element.
		 *  @param name The name.
		 */
		public RBelief(MBelief modelelement, IInternalAccess agent)
		{
			super(modelelement, agent);
			String name = getModelElement().getName();
			this.value = modelelement.getDefaultFact()==null? null: SJavaParser.parseExpression(modelelement.getDefaultFact(), agent.getModel().getAllImports(), agent.getClassLoader()).getValue(agent.getFetcher());
			this.publisher = new EventPublisher(agent, ChangeEvent.FACTCHANGED+"."+name, (MBelief)getModelElement());
		}
		
		/**
		 *  Create a new parameter.
		 *  @param modelelement The model element.
		 *  @param name The name.
		 */
		public RBelief(MBelief modelelement, IInternalAccess agent, Object value)
		{
			super(modelelement, agent);
			String name = getModelElement().getName();
			this.value = value;
			this.publisher = new EventPublisher(agent, ChangeEvent.FACTCHANGED+"."+name, (MBelief)getModelElement());
		}

		/**
		 *  Get the name.
		 *  @return The name
		 */
		public String getName()
		{
			return getModelElement().getName();
		}
		
		/**
		 *  Set a value of a parameter.
		 *  @param value The new value.
		 */
		public void setFact(Object value)
		{
			System.out.println("belief set val: "+value);
			publisher.entryChanged(this.value, value, -1);
			this.value = value;
//			publisher.unobserveValue(this.value);
//			publisher.getRuleSystem().addEvent(new Event(publisher.getChangeEvent(), new ChangeInfo<Object>(value, this.value, null)));
//			this.value = value;
//			publisher.observeValue(value);
//			publisher.publishToolBeliefEvent();
		}

		/**
		 *  Get the value of a parameter.
		 *  @return The value.
		 */
		public Object getFact()
		{
			return value;
		}
		
		/**
		 *  Get the value class.
		 *  @return The valuec class.
		 */
		public Class<?>	getClazz()
		{
			return ((MBelief)getModelElement()).getType(agent.getClassLoader());
		}
	}
	
	/**
	 * 
	 */
	public class RBeliefSet extends RElement implements IBeliefSet
	{
		/** The value. */
		protected List<Object> facts;
		
		/**
		 *  Create a new parameter.
		 *  @param modelelement The model element.
		 *  @param name The name.
		 */
		public RBeliefSet(MBelief modelelement, IInternalAccess agent, Object[] vals)
		{
			super(modelelement, agent);
			
			String name = modelelement.getName();
			this.facts = new ListWrapper<Object>(vals!=null? SUtil.arrayToList(vals): new ArrayList<Object>(), getAgent(), ChangeEvent.VALUEADDED+"."+name, 
				ChangeEvent.VALUEREMOVED+"."+name, ChangeEvent.VALUECHANGED+"."+name, getModelElement());
		}
		
		/**
		 *  Create a new parameter.
		 *  @param modelelement The model element.
		 *  @param name The name.
		 */
		public RBeliefSet(MBelief modelelement, IInternalAccess agent)
		{
			super(modelelement, agent);
			
			String name = getModelElement().getName();
			List<Object> tmpfacts;
			if(modelelement.getDefaultFact()!=null)
			{
				tmpfacts = (List<Object>)SJavaParser.parseExpression(modelelement.getDefaultFact(), agent.getModel().getAllImports(), agent.getClassLoader()).getValue(agent.getFetcher());
			}
			else 
			{
				tmpfacts = new ArrayList<Object>();
				if(modelelement.getDefaultFacts()!=null)
				{
					for(UnparsedExpression uexp: modelelement.getDefaultFacts())
					{
						Object fact = SJavaParser.parseExpression(uexp, agent.getModel().getAllImports(), agent.getClassLoader()).getValue(agent.getFetcher());
						tmpfacts.add(fact);
					}
				}
			}
			
			this.facts = new ListWrapper<Object>(tmpfacts, agent, ChangeEvent.FACTADDED+"."+name, 
				ChangeEvent.FACTREMOVED+"."+name, ChangeEvent.FACTCHANGED+"."+name, (MBelief)getModelElement());
		}

		/**
		 *  Get the name.
		 *  @return The name
		 */
		public String getName()
		{
			return getModelElement().getName();
		}
		
		/**
		 *  Add a fact to a belief.
		 *  @param fact The new fact.
		 */
		public void addFact(Object fact)
		{
			facts.add(fact);
		}

		/**
		 *  Remove a fact to a belief.
		 *  @param fact The new fact.
		 */
		public void removeFact(Object fact)
		{
			facts.remove(fact);
		}

		/**
		 *  Add facts to a parameter set.
		 */
		public void addFacts(Object[] facts)
		{
			if(facts!=null)
			{
				for(Object fact: facts)
				{
					addFact(fact);
				}
			}
		}

		/**
		 *  Remove all facts from a belief.
		 */
		public void removeFacts()
		{
			if(facts!=null)
				facts.clear();
		}

		/**
		 *  Get a value equal to the given object.
		 *  @param oldval The old value.
		 */
		public Object getFact(Object oldval)
		{
			Object ret = null;
			if(facts!=null)
			{
				for(Object fact: facts)
				{
					if(SUtil.equals(fact, oldval))
						ret = fact;
				}
			}
			return ret;
		}

		/**
		 *  Test if a fact is contained in a belief.
		 *  @param fact The fact to test.
		 *  @return True, if fact is contained.
		 */
		public boolean containsFact(Object fact)
		{
			return facts.contains(fact);
		}

		/**
		 *  Get the facts of a beliefset.
		 *  @return The facts.
		 */
		public Object[]	getFacts()
		{
			Object ret;
			
			Class<?> type = ((MBelief)getModelElement()).getType(getAgent().getClassLoader());
			int size = facts==null? 0: facts.size();
			ret = type!=null? ret = Array.newInstance(type, size): new Object[size];
			
			if(facts!=null)
				System.arraycopy(facts.toArray(new Object[facts.size()]), 0, ret, 0, facts.size());
			
			return (Object[])ret;
		}

		/**
		 *  Update a fact to a new fact. Searches the old
		 *  value with equals, removes it and stores the new fact.
		 *  @param newfact The new fact.
		 */
//		public void updateFact(Object newfact);

		/**
		 *  Get the number of values currently
		 *  contained in this set.
		 *  @return The values count.
		 */
		public int size()
		{
			return facts==null? 0: facts.size();
		}
		
		/**
		 *  Get the value class.
		 *  @return The valuec class.
		 */
		public Class<?>	getClazz()
		{
			return ((MBelief)getModelElement()).getType(agent.getClassLoader());
		}
	}
}
