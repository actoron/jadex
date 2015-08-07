package jadex.bdiv3x.runtime;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jadex.bdiv3.features.impl.IInternalBDIAgentFeature;
import jadex.bdiv3.model.IBDIModel;
import jadex.bdiv3.model.MBelief;
import jadex.bdiv3.model.MConfigBeliefElement;
import jadex.bdiv3.model.MConfiguration;
import jadex.bdiv3.model.MElement;
import jadex.bdiv3.model.MParameter;
import jadex.bdiv3.runtime.ChangeEvent;
import jadex.bdiv3.runtime.IBeliefListener;
import jadex.bdiv3.runtime.impl.RElement;
import jadex.bdiv3.runtime.wrappers.EventPublisher;
import jadex.bdiv3.runtime.wrappers.ListWrapper;
import jadex.bdiv3x.BDIXModel;
import jadex.bdiv3x.features.IBDIXAgentFeature;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.modelinfo.UnparsedExpression;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.javaparser.IMapAccess;
import jadex.javaparser.SJavaParser;
import jadex.rules.eca.ChangeInfo;
import jadex.rules.eca.Event;
import jadex.rules.eca.RuleSystem;

/**
 *  Runtime element for storing beliefs.
 */
public class RBeliefbase extends RElement implements IBeliefbase, IMapAccess
{
	/** The beliefs. */
	protected Map<String, IBelief> beliefs;
	
	/** The belief sets. */
	protected Map<String, IBeliefSet> beliefsets;
	
	/** The local belief names (cached on first access). */
	protected String[]	names;
	
	/** The local belief set names (cached on first access). */
	protected String[]	setnames;
	
	/**
	 *  Create a new beliefbase.
	 */
	public RBeliefbase(IInternalAccess agent)
	{
		super(null, agent);
	}
	
	/**
	 *  Init the beliefbase.
	 */
	public void init()
	{	
		Map<String, Object> args = getAgent().getComponentFeature(IArgumentsResultsFeature.class).getArguments();
		Map<String, MConfigBeliefElement> inibels = new HashMap<String, MConfigBeliefElement>();
		
		String confname = getAgent().getConfiguration();
		if(confname!=null)
		{
			IBDIModel bdimodel = (IBDIModel)getAgent().getModel().getRawModel();
			MConfiguration mconf = bdimodel.getCapability().getConfiguration(confname);
			
			if(mconf!=null)
			{
				// Set initial belief values
				List<MConfigBeliefElement> ibels = mconf.getInitialBeliefs();
				if(ibels!=null)
				{
					for(MConfigBeliefElement ibel: ibels)
					{
						inibels.put(bdimodel.getCapability().getBeliefReferences().containsKey(ibel.getName())
							? bdimodel.getCapability().getBeliefReferences().get(ibel.getName()) : ibel.getName(), ibel);
					}
				}
			}
		}
		
		List<MBelief> mbels = getMCapability().getBeliefs();
		if(mbels!=null)
		{
			for(MBelief mbel: mbels)
			{
				Object	inival	= null;
				boolean	hasinival	= false;
				
				if(MParameter.EvaluationMode.STATIC.equals(mbel.getEvaluationMode()))
//					|| MParameter.EvaluationMode.PUSH.equals(mbel.getEvaluationMode()))
				{
					if(args.containsKey(mbel.getName())) // mbel.isExported() && 
					{	
						inival	= args.get(mbel.getName());
						hasinival	= true;
					}
					else if(inibels.containsKey(mbel.getName()))
					{
						try
						{
							MConfigBeliefElement	inibel	= inibels.get(mbel.getName());
							if(mbel.isMulti(agent.getClassLoader()))
							{
								List<Object>	inivals	= new ArrayList<Object>();
								for(UnparsedExpression upex: inibel.getFacts())
								{
									Object	value	= SJavaParser.parseExpression(upex, getAgent().getModel().getAllImports(), getAgent().getClassLoader()).getValue(CapabilityWrapper.getFetcher(getAgent(), inibel));
									if(SReflect.isIterable(value) && inibel.getFacts().size()==1)	// Hack!!! support beliefset of iterable type with one initial element?
									{
										for(Object val: SReflect.getIterable(value))
										{
											inivals.add(val);											
										}
									}
									else
									{
										inivals.add(value);
									}
								}
								inival	= inivals;
							}
							else if(!inibel.getFacts().isEmpty())
							{
								inival	= SJavaParser.parseExpression(inibel.getFacts().get(0), getAgent().getModel().getAllImports(), getAgent().getClassLoader()).getValue(CapabilityWrapper.getFetcher(getAgent(), inibel));								
							}
							hasinival	= true;
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
				
				if(!mbel.isMulti(agent.getClassLoader()))
				{
					if(hasinival)
					{	
						addBelief(new RBelief(mbel, getAgent(), inival));
					}
					else
					{
						addBelief(new RBelief(mbel, getAgent()));
					}
				}
				else
				{
					if(hasinival)
					{
						addBeliefSet(new RBeliefSet(mbel, getAgent(), inival));
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
	public IBelief getBelief0(String name)
	{
		IBelief	ret;
		if(beliefs!=null)
		{
			ret	= ((IBDIModel)getAgent().getModel()).getCapability().getBeliefReferences().containsKey(name)
				? beliefs.get(((IBDIModel)getAgent().getModel()).getCapability().getBeliefReferences().get(name))
				: beliefs.get(name);
			if(ret==null)
			{
				name	= MElement.internalName(name);
				ret	= ((IBDIModel)getAgent().getModel()).getCapability().getBeliefReferences().containsKey(name)
					? beliefs.get(((IBDIModel)getAgent().getModel()).getCapability().getBeliefReferences().get(name))
					: beliefs.get(name);
			}
		}
		else
		{
			ret	= null;
		}
		return ret;
	}

	/**
	 *  Get a belief set for a name.
	 *  @param name	The belief set name.
	 */
	public IBeliefSet getBeliefSet0(String name)
	{
		IBeliefSet	ret;
		if(beliefsets!=null)
		{
			ret	= ((IBDIModel)getAgent().getModel()).getCapability().getBeliefReferences().containsKey(name)
				? beliefsets.get(((IBDIModel)getAgent().getModel()).getCapability().getBeliefReferences().get(name))
				: beliefsets.get(name);
			if(ret==null)
			{
				name	= MElement.internalName(name);
				ret	= ((IBDIModel)getAgent().getModel()).getCapability().getBeliefReferences().containsKey(name)
					? beliefsets.get(((IBDIModel)getAgent().getModel()).getCapability().getBeliefReferences().get(name))
					: beliefsets.get(name);
			}
		}
		else
		{
			ret	= null;
		}
		return ret;
	}
	
	/**
	 *  Get a belief for a name.
	 *  @param name	The belief name.
	 */
	public IBelief getBelief(String name)
	{
		IBelief	ret	= getBelief0(name);
		if(ret==null)
			throw new RuntimeException("Belief not found: "+name);
		return ret;
	}

	/**
	 *  Get a belief set for a name.
	 *  @param name	The belief set name.
	 */
	public IBeliefSet getBeliefSet(String name)
	{
		IBeliefSet	ret	= getBeliefSet0(name);
		if(ret==null)
			throw new RuntimeException("Belief set not found: "+name);
		return ret;
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
		return getBelief0(name)!=null;
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
		return getBeliefSet0(name)!=null;
	}

	/**
	 *  Returns the names of all beliefs.
	 *  @return the names of all beliefs.
	 */
	public String[] getBeliefNames()
	{
		if(names==null && beliefs!=null)
		{
			List<String>	lnames	= new ArrayList<String>();
			for(String name: beliefs.keySet())
			{
				if(name.indexOf(MElement.CAPABILITY_SEPARATOR)==-1)
				{
					lnames.add(name);
				}
			}
			names	= lnames.toArray(new String[lnames.size()]);
		}
		
		return names;
	}

	/**
	 *  Returns the names of all belief sets.
	 *  @return the names of all belief sets.
	 */
	public String[] getBeliefSetNames()
	{
		if(setnames==null && beliefsets!=null)
		{
			List<String>	lnames	= new ArrayList<String>();
			for(String name: beliefsets.keySet())
			{
				if(name.indexOf(MElement.CAPABILITY_SEPARATOR)==-1)
				{
					lnames.add(name);
				}
			}
			setnames	= lnames.toArray(new String[lnames.size()]);
		}
		
		return setnames;
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
	 *  static: belief is evaluated once on init, afterwards set manually
	 *  pull: belief is reevaluated on each read access
	 *  push: reevaluates on each event and sets the new value and throws change event
	 *  polling/updaterate: reevaluates in intervals and and sets the new value and throws change event
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
			this.publisher = new EventPublisher(agent, ChangeEvent.FACTCHANGED+"."+name, (MBelief)getModelElement());
			if(modelelement.getDefaultFact()!=null)
				setFact(SJavaParser.parseExpression(modelelement.getDefaultFact(), agent.getModel().getAllImports(), agent.getClassLoader()).getValue(CapabilityWrapper.getFetcher(getAgent(), getModelElement())));
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
			this.publisher = new EventPublisher(agent, ChangeEvent.FACTCHANGED+"."+name, (MBelief)getModelElement());
			setFact(value);
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
		 *  Set a fact of a belief.
		 *  Only changes the belief, if the new value is not equal to the old value.
		 *  @param value The new fact.
		 *  @return True, if the value was changed.
		 */
		public boolean setFact(Object value)
		{
			boolean	changed	= !SUtil.equals(this.value, value);
			if(changed)
			{
				if(value!=null)
				{
					Class<?>	clazz	= ((MBelief)getModelElement()).getClazz().getType(getAgent().getClassLoader(), getAgent().getModel().getAllImports());
					if(!SReflect.isSupertype(clazz, value.getClass()))
					{
						throw new IllegalArgumentException("Incompatible value for belief "+getName()+": "+value);
					}
					value	= SReflect.convertWrappedValue(value, clazz);
				}
				
				Object oldvalue = this.value;
				this.value = value;
				publisher.entryChanged(oldvalue, value, -1);
				
				// Push to result, if any.
				String	result	= ((BDIXModel)getAgent().getModel()).getCapability().getResultMappings().get(getName());
				if(result!=null && getAgent().getComponentFeature0(IArgumentsResultsFeature.class)!=null)
				{
					getAgent().getComponentFeature(IArgumentsResultsFeature.class)
						.getResults().put(result, value);
				}
			}
			return changed;
		}

		/**
		 *  Get the value of a parameter.
		 *  @return The value.
		 */
		public Object getFact()
		{
			Object ret = value;
			// In case of push the last evaluated value is returned
			if(((MBelief)getModelElement()).getDefaultFact()!=null && MParameter.EvaluationMode.PULL.equals(((MBelief)getModelElement()).getEvaluationMode()))
			{
				ret = SJavaParser.parseExpression(((MBelief)getModelElement()).getDefaultFact(), 
					getAgent().getModel().getAllImports(), getAgent().getClassLoader()).getValue(CapabilityWrapper.getFetcher(getAgent(), getModelElement()));
			}
			return ret;
		}
		
		/**
		 *  Get the value class.
		 *  @return The valuec class.
		 */
		public Class<?>	getClazz()
		{
			return ((MBelief)getModelElement()).getType(agent.getClassLoader());
		}
		
		/**
		 *  Indicate that the fact of this belief was modified.
		 *  Calling this method causes an internal fact changed
		 *  event that might cause dependent actions.
		 */
		public void modified()
		{
			publisher.entryChanged(value, value, -1);
		}
		
		/**
		 *  Add a belief set listener.
		 *  @param listener The belief set listener.
		 */
		public <T> void addBeliefListener(IBeliefListener<T> listener)
		{
			IInternalBDIAgentFeature bdif = getAgent().getComponentFeature(IInternalBDIAgentFeature.class);
			bdif.addBeliefListener(getName(), listener);
		}
		
		/**
		 *  Remove a belief set listener.
		 *  @param listener The belief set listener.
		 */
		public <T> void removeBeliefListener(IBeliefListener<T> listener)
		{
			IInternalBDIAgentFeature bdif = getAgent().getComponentFeature(IInternalBDIAgentFeature.class);
			bdif.removeBeliefListener(getName(), listener);
		}
	}
	
	/**
	 * 
	 */
	public class RBeliefSet extends RElement implements IBeliefSet
	{
		/** The value. */
		protected ListWrapper<Object> facts;
		
		/**
		 *  Create a new parameter.
		 *  @param modelelement The model element.
		 *  @param name The name.
		 *  @param vals	The values as array, list, iterable...
		 */
		public RBeliefSet(MBelief modelelement, IInternalAccess agent, Object vals)
		{
			super(modelelement, agent);
			
			List<Object>	inifacts	= new ArrayList<Object>();
			String name = modelelement.getName();
			if(vals!=null)
			{
				Iterator<?>	it	= SReflect.getIterator(vals);
				while(it.hasNext())
				{
					inifacts.add(it.next());
				}
			}
			this.facts = new ListWrapper<Object>(inifacts, agent, ChangeEvent.FACTADDED+"."+name, 
				ChangeEvent.FACTREMOVED+"."+name, ChangeEvent.FACTCHANGED+"."+name, getModelElement());
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
			this.facts = new ListWrapper<Object>(evaluateValues(), agent, ChangeEvent.FACTADDED+"."+name, 
				ChangeEvent.FACTREMOVED+"."+name, ChangeEvent.FACTCHANGED+"."+name, getModelElement());
		}
		
		/**
		 *  Evaluate the default values.
		 */
		protected List<Object> evaluateValues()
		{
			MBelief mbel = (MBelief)getModelElement();
			List<Object> tmpfacts = new ArrayList<Object>();
			if(mbel.getDefaultFact()!=null)
			{
				Object tmp = SJavaParser.parseExpression(mbel.getDefaultFact(), agent.getModel().getAllImports(), agent.getClassLoader()).getValue(CapabilityWrapper.getFetcher(getAgent(), getModelElement()));
				Iterator<?>	it	= SReflect.getIterator(tmp);
				while(it.hasNext())
				{
					tmpfacts.add(it.next());
				}
			}
			else 
			{
				if(mbel.getDefaultFacts()!=null)
				{
					for(UnparsedExpression uexp: mbel.getDefaultFacts())
					{
						Object fact = SJavaParser.parseExpression(uexp, agent.getModel().getAllImports(), agent.getClassLoader()).getValue(CapabilityWrapper.getFetcher(getAgent(), getModelElement()));
						tmpfacts.add(fact);
					}
				}
			}
			return tmpfacts;
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
			if(fact!=null)
			{
				Class<?>	clazz	= ((MBelief)getModelElement()).getClazz().getType(getAgent().getClassLoader(), getAgent().getModel().getAllImports());
				if(!SReflect.isSupertype(clazz, fact.getClass()))
				{
					throw new IllegalArgumentException("Incompatible value for belief set "+getName()+": "+fact);
				}
				fact	= SReflect.convertWrappedValue(fact, clazz);
			}
			
			internalGetValues().add(fact);
			
			// Push to result, if any.
			String	result	= ((BDIXModel)getAgent().getModel()).getCapability().getResultMappings().get(getName());
			if(result!=null && getAgent().getComponentFeature0(IArgumentsResultsFeature.class)!=null)
			{
				getAgent().getComponentFeature(IArgumentsResultsFeature.class)
					.getResults().put(result, internalGetValues());
			}
		}

		/**
		 *  Remove a fact to a belief.
		 *  @param fact The new fact.
		 */
		public void removeFact(Object fact)
		{
			internalGetValues().remove(fact);
			
			// Push to result, if any.
			String	result	= ((BDIXModel)getAgent().getModel()).getCapability().getResultMappings().get(getName());
			if(result!=null && getAgent().getComponentFeature0(IArgumentsResultsFeature.class)!=null)
			{
				getAgent().getComponentFeature(IArgumentsResultsFeature.class)
					.getResults().put(result, internalGetValues());
			}
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
			internalGetValues().clear();
			
			// Push to result, if any.
			String	result	= ((BDIXModel)getAgent().getModel()).getCapability().getResultMappings().get(getName());
			if(result!=null && getAgent().getComponentFeature0(IArgumentsResultsFeature.class)!=null)
			{
				getAgent().getComponentFeature(IArgumentsResultsFeature.class)
					.getResults().put(result, internalGetValues());
			}
		}

		/**
		 *  Get a value equal to the given object.
		 *  @param oldval The old value.
		 */
		public Object getFact(Object oldval)
		{
			Object ret = null;
			List<Object> facts = internalGetValues();
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
			return internalGetValues().contains(fact);
		}

		/**
		 *  Get the facts of a beliefset.
		 *  @return The facts.
		 */
		public Object[]	getFacts()
		{
			Object ret;
			
			List<Object> facts = internalGetValues();
			
			Class<?> type = ((MBelief)getModelElement()).getType(getAgent().getClassLoader());
			int size = facts==null? 0: facts.size();
			ret = type!=null? ret = Array.newInstance(SReflect.getWrappedType(type), size): new Object[size];
			
			if(facts!=null)
			{
				System.arraycopy(facts.toArray(new Object[facts.size()]), 0, ret, 0, facts.size());
			}
			
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
			return internalGetValues().size();
		}
		
		/**
		 *  Get the value class.
		 *  @return The valuec class.
		 */
		public Class<?>	getClazz()
		{
			return ((MBelief)getModelElement()).getType(agent.getClassLoader());
		}
		
		/**
		 *  Indicate that the fact of this belief was modified.
		 *  Calling this method causes an internal fact changed
		 *  event that might cause dependent actions.
		 */
		public void modified(Object fact)
		{
			if(fact!=null)
			{
				facts.entryChanged(fact, fact, facts.indexOf(fact));
			}
			else
			{
				RuleSystem rs = ((IInternalBDIAgentFeature)getAgent().getComponentFeature(IBDIXAgentFeature.class)).getRuleSystem();
				rs.addEvent(new Event(ChangeEvent.BELIEFCHANGED+"."+getName(), new ChangeInfo<Object>(facts, facts, null)));
			}

			// Push to result, if any.
			String	result	= ((BDIXModel)getAgent().getModel()).getCapability().getResultMappings().get(getName());
			if(result!=null && getAgent().getComponentFeature0(IArgumentsResultsFeature.class)!=null)
			{
				getAgent().getComponentFeature(IArgumentsResultsFeature.class)
					.getResults().put(result, internalGetValues());
			}
		}
		
		/**
		 *  Add a belief set listener.
		 *  @param listener The belief set listener.
		 */
		public <T> void addBeliefSetListener(IBeliefListener<T> listener)
		{
			IInternalBDIAgentFeature bdif = getAgent().getComponentFeature(IInternalBDIAgentFeature.class);
			bdif.addBeliefListener(getName(), listener);
		}
		
		/**
		 *  Remove a belief set listener.
		 *  @param listener The belief set listener.
		 */
		public <T> void removeBeliefSetListener(IBeliefListener<T> listener)
		{
			IInternalBDIAgentFeature bdif = getAgent().getComponentFeature(IInternalBDIAgentFeature.class);
			bdif.removeBeliefListener(getName(), listener);
		}
		
		/**
		 * 
		 */
		protected List<Object> internalGetValues()
		{
			// In case of push the last saved/evaluated value is returned
			return MParameter.EvaluationMode.PULL.equals(((MBelief)getModelElement()).getEvaluationMode())? evaluateValues(): facts;
		}
	}
}
