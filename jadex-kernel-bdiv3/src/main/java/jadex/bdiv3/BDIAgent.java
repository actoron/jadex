package jadex.bdiv3;

import jadex.bdiv3.model.BDIModel;
import jadex.bdiv3.model.MBelief;
import jadex.bdiv3.model.MCapability;
import jadex.bdiv3.model.MPlan;
import jadex.bdiv3.runtime.ChangeEvent;
import jadex.bdiv3.runtime.IBeliefListener;
import jadex.bdiv3.runtime.impl.BDIAgentInterpreter;
import jadex.bdiv3.runtime.impl.RGoal;
import jadex.bdiv3.runtime.impl.RPlan;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.commons.IResultCommand;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.commons.beans.PropertyChangeEvent;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.IPojoMicroAgent;
import jadex.micro.MicroAgent;
import jadex.rules.eca.Event;
import jadex.rules.eca.EventType;
import jadex.rules.eca.IAction;
import jadex.rules.eca.ICondition;
import jadex.rules.eca.IEvent;
import jadex.rules.eca.IRule;
import jadex.rules.eca.Rule;
import jadex.rules.eca.RuleSystem;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  Base class for application agents.
 */
public class BDIAgent extends MicroAgent
{
	/**
	 *  Get the goals of a given type.
	 */
	public <T> Collection<T> getGoals(Class<T> clazz)
	{
		Collection<RGoal>	rgoals	= ((BDIAgentInterpreter)getInterpreter()).getCapability().getGoals(clazz);
		List<T>	ret	= new ArrayList<T>();
		for(RGoal rgoal: rgoals)
		{
			ret.add((T)rgoal.getPojoElement());
		}
		
		return ret;
	}

	/**
	 *  Dispatch a goal wait for its result.
	 */
	public <T, E> IFuture<E> dispatchTopLevelGoal(T goal)
	{
		BDIAgentInterpreter ip = (BDIAgentInterpreter)getInterpreter();
		return ip.dispatchTopLevelGoal(goal);
	}
	
	/**
	 *  Dispatch a goal wait for its result.
	 */
	public <T> void adoptPlan(T plan)
	{
		BDIAgentInterpreter ip = (BDIAgentInterpreter)getInterpreter();
		MPlan mplan = ip.getBDIModel().getCapability().getPlan(plan.getClass().getName());
		if(mplan==null)
			throw new RuntimeException("Plan model not found for: "+plan);
		
		RPlan rplan = RPlan.createRPlan(mplan, mplan, null, ip.getInternalAccess());
		RPlan.executePlan(rplan, getInterpreter().getInternalAccess());
	}
	
	/**
	 *  Add a belief listener.
	 *  @param name The belief name.
	 *  @param listener The belief listener.
	 */
	public void addBeliefListener(String name, final IBeliefListener listener)
	{
		BDIModel	bdimodel	= (BDIModel)getInterpreter().getMicroModel();
		final String	fname	= bdimodel.getBeliefMappings().containsKey(name) ? bdimodel.getBeliefMappings().get(name) : name;
		
		List<EventType> events = new ArrayList<EventType>();
		BDIAgentInterpreter.addBeliefEvents(this, events, fname);

		final boolean multi = ((MCapability)((BDIAgentInterpreter)getInterpreter()).getCapability().getModelElement())
			.getBelief(fname).isMulti(getClassLoader());
		
		String rulename = fname+"_belief_listener_"+System.identityHashCode(listener);
		Rule<Void> rule = new Rule<Void>(rulename, 
			ICondition.TRUE_CONDITION, new IAction<Void>()
		{
			public IFuture<Void> execute(IEvent event, IRule<Void> rule, Object context)
			{
				if(!multi)
				{
					listener.beliefChanged(event.getContent());
				}
				else
				{
					if(ChangeEvent.FACTADDED.equals(event.getType().getType(0)))
					{
						listener.factAdded(event.getContent());
					}
					else if(ChangeEvent.FACTREMOVED.equals(event.getType().getType(0)))
					{
						listener.factAdded(event.getContent());
					}
					else if(ChangeEvent.FACTCHANGED.equals(event.getType().getType(0)))
					{
						Object[] vals = (Object[])event.getContent();
						listener.factChanged(vals[0], vals[1], vals[2]);
					}
				}
				return IFuture.DONE;
			}
		});
		rule.setEvents(events);
		BDIAgentInterpreter ip = (BDIAgentInterpreter)getInterpreter();
		ip.getRuleSystem().getRulebase().addRule(rule);
	}
	
	/**
	 *  Remove a belief listener.
	 *  @param name The belief name.
	 *  @param listener The belief listener.
	 */
	public void removeBeliefListener(String name, IBeliefListener listener)
	{
		BDIModel	bdimodel	= (BDIModel)getInterpreter().getMicroModel();
		name	= bdimodel.getBeliefMappings().containsKey(name) ? bdimodel.getBeliefMappings().get(name) : name;
		BDIAgentInterpreter ip = (BDIAgentInterpreter)getInterpreter();
		String rulename = name+"_belief_listener_"+System.identityHashCode(listener);
		ip.getRuleSystem().getRulebase().removeRule(rulename);
	}

	
	//-------- internal method used for rewriting field access -------- 
	
	/**
	 *  Add an entry to the init calls.
	 */
	public static void	addInitArgs(Object obj, Class<?> clazz, Class<?>[] argtypes, Object[] args)
	{
		try
		{
			Field f	= clazz.getDeclaredField("__initargs");
//			System.out.println(f+", "+SUtil.arrayToString(args));
			f.setAccessible(true);
			List<Tuple2<Class<?>[], Object[]>> initcalls	= (List<Tuple2<Class<?>[], Object[]>>)f.get(obj);
			if(initcalls==null)
			{
				initcalls	= new ArrayList<Tuple2<Class<?>[], Object[]>>();
				f.set(obj, initcalls);
			}
			initcalls.add(new Tuple2<Class<?>[], Object[]>(argtypes, args));
		}
		catch(Exception e)
		{
			throw e instanceof RuntimeException ? (RuntimeException)e : new RuntimeException(e);
		}
	}
	
	/**
	 *  Get the init calls.
	 *  Cleans the initargs field on return.
	 */
	public static List<Tuple2<Class<?>[], Object[]>>	getInitCalls(Object obj, Class<?> clazz)
	{
		try
		{
			Field f	= clazz.getDeclaredField("__initargs");
			f.setAccessible(true);
			List<Tuple2<Class<?>[], Object[]>> initcalls	= (List<Tuple2<Class<?>[], Object[]>>)f.get(obj);
			f.set(obj, null);
			return initcalls;
		}
		catch(Exception e)
		{
			throw e instanceof RuntimeException ? (RuntimeException)e : new RuntimeException(e);
		}
	}
	
	/**
	 *  Method that is called automatically when a belief 
	 *  is written as field access.
	 */
	protected void writeField(Object val, final String fieldname, Object obj)
	{
		assert isComponentThread();
		
		// todo: support for belief sets (un/observe values? insert mappers when setting value etc.
		
		try
		{
//			System.out.println("write: "+val+" "+fieldname+" "+obj);
			BDIAgentInterpreter ip = (BDIAgentInterpreter)getInterpreter();
			RuleSystem rs = ip.getRuleSystem();

			Object oldval = setFieldValue(obj, fieldname, val);
			
			// unobserve old value for property changes
			rs.unobserveObject(oldval);
			
			if(!SUtil.equals(val, oldval))
			{
				rs.addEvent(new Event(ChangeEvent.BELIEFCHANGED+"."+fieldname, val));
				// execute rulesystem immediately to ensure that variable values are not changed afterwards
				rs.processAllEvents(); 
			}
			
			// observe new value for property changes
			observeValue(rs, val, ip, ChangeEvent.FACTCHANGED+"."+fieldname);
			
			// initiate a step to reevaluate the conditions
			scheduleStep(new IComponentStep()
			{
				public IFuture execute(IInternalAccess ia)
				{
					return IFuture.DONE;
				}
			});
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	/**
	 *  Set the value of a field.
	 *  @param obj The object.
	 *  @param fieldname The name of the field.
	 *  @return The old field value.
	 */
	protected static Object setFieldValue(Object obj, String fieldname, Object val) throws IllegalAccessException
	{
		Tuple2<Field, Object> res = findFieldWithOuterClass(obj, fieldname);
		Field f = res.getFirstEntity();
		if(f==null)
			throw new RuntimeException("Field not found: "+fieldname);
		
		Object tmp = res.getSecondEntity();
		f.setAccessible(true);
		Object oldval = f.get(tmp);
		f.set(tmp, val);
	
		return oldval;
	}
	
	/**
	 * 
	 * @param obj
	 * @param fieldname
	 * @return
	 */
	protected static Tuple2<Field, Object> findFieldWithOuterClass(Object obj, String fieldname)
	{
		Field f = null;
		Object tmp = obj;
		while(f==null && tmp!=null)
		{
			f = findFieldWithSuperclass(tmp.getClass(), fieldname);
			if(f==null)
			{
				try
				{
					Field fi = tmp.getClass().getDeclaredField("this$0");
					fi.setAccessible(true);
					tmp = fi.get(tmp);
				}
				catch(Exception e)
				{
//					e.printStackTrace();
					tmp=null;
				}
			}
		}
		return new Tuple2<Field, Object>(f, tmp);
	}
	
	/**
	 * 
	 * @param cl
	 * @param fieldname
	 * @return
	 */
	protected static Field findFieldWithSuperclass(Class<?> cl, String fieldname)
	{
		Field ret = null;
		while(ret==null && !Object.class.equals(cl))
		{
			try
			{
				ret = cl.getDeclaredField(fieldname);
			}
			catch(Exception e)
			{
				cl = cl.getSuperclass();
			}
		}
		return ret;
	}
	
	/**
	 *  Method that is called automatically when a belief 
	 *  is written as field access.
	 */
	public static void writeField(Object val, final String fieldname, Object obj, BDIAgent agent)
	{
//		System.out.println("write: "+val+" "+fieldname+" "+obj+" "+agent);

		if(agent!=null)
		{
			agent.writeField(val, fieldname, obj);
		}
		else
		{
			try
			{
				setFieldValue(obj, fieldname, val);
			}
			catch(Exception e)
			{
				e.printStackTrace();
				throw new RuntimeException(e);
			}
			synchronized(initwrites)
			{
				List<Object[]> inits = initwrites.get(obj);
				if(inits==null)
				{
					inits = new ArrayList<Object[]>();
					initwrites.put(obj, inits);
				}
				inits.add(new Object[]{val, fieldname, obj});	
			}
		}
	}
	
	/** Saved init writes. */
	protected static Map<Object, List<Object[]>> initwrites = new HashMap<Object, List<Object[]>>();
	
	/**
	 * 
	 */
	public static void performInitWrites(final BDIAgent agent)
	{
		Object pojo = ((IPojoMicroAgent)agent).getPojoAgent();

		synchronized(initwrites)
		{
			List<Object[]> writes = initwrites.remove(pojo);
			if(writes!=null)
			{
				for(Object[] write: writes)
				{
//					System.out.println("initwrite: "+write[0]+" "+write[1]+" "+write[2]);
//					agent.writeField(write[0], (String)write[1], write[2]);
					RuleSystem rs = ((BDIAgentInterpreter)agent.getInterpreter()).getRuleSystem();
					final String fieldname = (String)write[1];
					Object val = write[0];
					rs.addEvent(new Event(ChangeEvent.BELIEFCHANGED+"."+fieldname, val));
					observeValue(rs, val, (BDIAgentInterpreter)agent.getInterpreter(), ChangeEvent.FACTCHANGED+"."+fieldname);
				}
			}
		}
	}
	
	/**
	 *  Method that is called automatically when a belief 
	 *  is written as array access.
	 */
	// todo: allow init writes in constructor also for arrays
	public static void writeArrayField(Object array, final int index , Object val, BDIAgent agent, String fieldname)
	{
		try
		{
			final BDIAgentInterpreter ip = (BDIAgentInterpreter)agent.getInterpreter();
			RuleSystem rs = ip.getRuleSystem();
//			System.out.println("write array index: "+val+" "+index+" "+array+" "+agent+" "+fieldname);
			
			Object oldval = Array.get(array, index);
			rs.unobserveObject(oldval);	
			
			Class<?> ct = array.getClass().getComponentType();
			if(boolean.class.equals(ct))
			{
				val = ((Integer)val)==1? Boolean.TRUE: Boolean.FALSE;
			}
			else if(byte.class.equals(ct))
			{
				val = new Byte(((Integer)val).byteValue());
			}
			
			Array.set(array, index, val);
			observeValue(rs, val, ip, ChangeEvent.FACTCHANGED+"."+fieldname);
			
			if(!SUtil.equals(val, oldval))
			{
				Event ev = new Event(new EventType(new String[]{ChangeEvent.FACTCHANGED, fieldname}), val); // todo: index
				rs.addEvent(ev);
				// execute rulesystem immediately to ensure that variable values are not changed afterwards
				rs.processAllEvents(); 
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 *  Unobserving an old belief value.
	 *  @param agent The agent.
	 *  @param belname The belief name.
	 */
	public static void unobserveValue(BDIAgent agent, final String belname)
	{
//		System.out.println("unobserve: "+agent+" "+belname);
		
		try
		{
			Object pojo = ((IPojoMicroAgent)agent).getPojoAgent();
		
			Method getter = pojo.getClass().getMethod("get"+belname.substring(0,1).toUpperCase()+belname.substring(1), new Class[0]);
			Object oldval = getter.invoke(pojo, new Object[0]);
		
			RuleSystem rs = ((BDIAgentInterpreter)agent.getInterpreter()).getRuleSystem();
			rs.unobserveObject(oldval);	
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 */
	public static void observeValue(RuleSystem rs, Object val, final BDIAgentInterpreter agent, final String etype)
	{
		if(val!=null)
		{
			rs.observeObject(val, true, false, new IResultCommand<IFuture<IEvent>, PropertyChangeEvent>()
			{
				public IFuture<IEvent> execute(final PropertyChangeEvent event)
				{
					return agent.scheduleStep(new IComponentStep<IEvent>()
					{
						public IFuture<IEvent> execute(IInternalAccess ia)
						{
	//						Event ev = new Event(ChangeEvent.FACTCHANGED+"."+fieldname+"."+event.getPropertyName(), event.getNewValue());
	//						Event ev = new Event(ChangeEvent.FACTCHANGED+"."+fieldname, event.getNewValue());
							Event ev = new Event(etype, event.getNewValue());
							return new Future<IEvent>(ev);
						}
					});
				}
			});
		}
	}

	/**
	 *  Create a belief changed event.
	 *  @param val The new value.
	 *  @param agent The agent.
	 *  @param belname The belief name.
	 */
	public static void createEvent(Object val, final BDIAgent agent, final String belname)
	{
//		System.out.println("createEv: "+val+" "+agent+" "+belname);
		
		RuleSystem rs = ((BDIAgentInterpreter)agent.getInterpreter()).getRuleSystem();
		rs.addEvent(new Event(ChangeEvent.BELIEFCHANGED+"."+belname, val));
		observeValue(rs, val, (BDIAgentInterpreter)agent.getInterpreter(), ChangeEvent.FACTCHANGED+"."+belname);
	}
	
	/**
	 *  Get the value of an abstract belief.
	 */
	public Object	getAbstractBeliefValue(String capa, String name, Class<?> type)
	{
//		System.out.println("getAbstractBeliefValue(): "+capa+BDIAgentInterpreter.CAPABILITY_SEPARATOR+name+", "+type);
		BDIModel	bdimodel	= (BDIModel)getInterpreter().getMicroModel();
		String	belname	= bdimodel.getBeliefMappings().get(capa+BDIAgentInterpreter.CAPABILITY_SEPARATOR+name);
		if(belname==null)
		{
			throw new RuntimeException("No mapping for abstract belief: "+capa+BDIAgentInterpreter.CAPABILITY_SEPARATOR+name);
		}
		String	capaname	= belname.indexOf(BDIAgentInterpreter.CAPABILITY_SEPARATOR)==-1
			? null : belname.substring(0, belname.lastIndexOf(BDIAgentInterpreter.CAPABILITY_SEPARATOR));
		MBelief	bel	= bdimodel.getCapability().getBelief(belname);
		Object	ocapa	= ((BDIAgentInterpreter)getInterpreter()).getCapabilityObject(capaname);
		Object	ret	= bel.getValue(ocapa, getClassLoader());
		
		if(ret==null)
		{
			if(type.equals(boolean.class))
			{
				ret	= Boolean.FALSE;
			}
			else if(type.equals(char.class))
			{
				ret	= new Character((char)0);
			}
			else if(SReflect.getWrappedType(type)!=type)	// Number type
			{
				ret	= new Integer(0);
			}
		}
		
		return ret;
	}
	
	/**
	 *  Set the value of an abstract belief.
	 */
	public void	setAbstractBeliefValue(String capa, String name, Object value)
	{
//		System.out.println("setAbstractBeliefValue(): "+capa+BDIAgentInterpreter.CAPABILITY_SEPARATOR+name);
		BDIModel	bdimodel	= (BDIModel)getInterpreter().getMicroModel();
		String	belname	= bdimodel.getBeliefMappings().get(capa+BDIAgentInterpreter.CAPABILITY_SEPARATOR+name);
		if(belname==null)
		{
			throw new RuntimeException("No mapping for abstract belief: "+capa+BDIAgentInterpreter.CAPABILITY_SEPARATOR+name);
		}
		String	capaname	= belname.indexOf(BDIAgentInterpreter.CAPABILITY_SEPARATOR)==-1
			? null : belname.substring(0, belname.lastIndexOf(BDIAgentInterpreter.CAPABILITY_SEPARATOR));
		MBelief	bel	= bdimodel.getCapability().getBelief(belname);
		Object	ocapa	= ((BDIAgentInterpreter)getInterpreter()).getCapabilityObject(capaname);

		// Maybe unobserve old value
		Object	old	= bel.getValue(ocapa, getClassLoader());

		boolean	field	= bel.setValue(ocapa, value, getClassLoader());
		
		if(field)
		{
			RuleSystem rs = ((BDIAgentInterpreter)getInterpreter()).getRuleSystem();
			rs.unobserveObject(old);	
			createEvent(value, this, belname);
		}
	}
}
