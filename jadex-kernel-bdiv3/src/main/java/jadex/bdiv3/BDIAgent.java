package jadex.bdiv3;

import jadex.bdiv3.model.BDIModel;
import jadex.bdiv3.model.MBelief;
import jadex.bdiv3.model.MCapability;
import jadex.bdiv3.model.MGoal;
import jadex.bdiv3.model.MParameter;
import jadex.bdiv3.model.MPlan;
import jadex.bdiv3.runtime.ChangeEvent;
import jadex.bdiv3.runtime.IBeliefListener;
import jadex.bdiv3.runtime.IGoal;
import jadex.bdiv3.runtime.IPlanListener;
import jadex.bdiv3.runtime.impl.BDIAgentInterpreter;
import jadex.bdiv3.runtime.impl.BeliefInfo;
import jadex.bdiv3.runtime.impl.RGoal;
import jadex.bdiv3.runtime.impl.RPlan;
import jadex.bdiv3.runtime.impl.RProcessableElement;
import jadex.bdiv3.runtime.wrappers.ListWrapper;
import jadex.bdiv3.runtime.wrappers.MapWrapper;
import jadex.bdiv3.runtime.wrappers.SetWrapper;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.types.monitoring.IMonitoringEvent;
import jadex.bridge.service.types.monitoring.IMonitoringService.PublishEventLevel;
import jadex.bridge.service.types.monitoring.IMonitoringService.PublishTarget;
import jadex.bridge.service.types.monitoring.MonitoringEvent;
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
import java.util.Set;

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
		for(RProcessableElement rgoal: rgoals)
		{
			ret.add((T)rgoal.getPojoElement());
		}
		return ret;
	}
	
	/**
	 *  Get the current goals.
	 */
	public Collection<IGoal> getGoals()
	{
		return (Collection)((BDIAgentInterpreter)getInterpreter()).getCapability().getGoals();
	}
	
	/**
	 *  Get the goal api representation.
	 */
	public IGoal getGoal(Object goal)
	{
		return ((BDIAgentInterpreter)getInterpreter()).getCapability().getRGoal(goal);
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
	 *  Drop a goal.
	 */
	public void dropGoal(Object goal)
	{
		BDIAgentInterpreter ip = (BDIAgentInterpreter)getInterpreter();
		ip.dropGoal(goal);
	}

	/**
	 *  Dispatch a goal wait for its result.
	 */
	public <T, E> IFuture<E> adoptPlan(T plan)
	{
		return adoptPlan(plan, null);
	}
	
	/**
	 *  Dispatch a goal wait for its result.
	 */
	public <T, E> IFuture<E> adoptPlan(T plan, Object[] args)
	{
		final Future<E> ret = new Future<E>();
		BDIAgentInterpreter ip = (BDIAgentInterpreter)getInterpreter();
		MPlan mplan = ip.getBDIModel().getCapability().getPlan(plan instanceof String? (String)plan: plan.getClass().getName());
		if(mplan==null)
			throw new RuntimeException("Plan model not found for: "+plan);
		
		final RPlan rplan = RPlan.createRPlan(mplan, plan, null, ip.getInternalAccess());
		rplan.addPlanListener(new IPlanListener<E>()
		{
			public void planFinished(E result)
			{
				if(rplan.getException()!=null)
				{
					ret.setException(rplan.getException());
				}
				else
				{
					ret.setResult(result);
				}
			}
		});
		rplan.setReason(new ChangeEvent(null, null, args));
		RPlan.executePlan(rplan, getInterpreter().getInternalAccess(), null);
		return ret;
	}
	
	/**
	 *  Add a belief listener.
	 *  @param name The belief name.
	 *  @param listener The belief listener.
	 */
	public void addBeliefListener(String name, final IBeliefListener listener)
	{
		BDIModel	bdimodel	= (BDIModel)getInterpreter().getMicroModel();
		String fname = bdimodel.getBeliefMappings().containsKey(name) ? bdimodel.getBeliefMappings().get(name) : name;
		
		List<EventType> events = new ArrayList<EventType>();
		BDIAgentInterpreter.addBeliefEvents(this, events, fname);

		final boolean multi = ((MCapability)((BDIAgentInterpreter)getInterpreter()).getCapability().getModelElement())
			.getBelief(fname).isMulti(getClassLoader());
		
		String rulename = fname+"_belief_listener_"+System.identityHashCode(listener);
		Rule<Void> rule = new Rule<Void>(rulename, 
			ICondition.TRUE_CONDITION, new IAction<Void>()
		{
			public IFuture<Void> execute(IEvent event, IRule<Void> rule, Object context, Object condresult)
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
					else if(ChangeEvent.BELIEFCHANGED.equals(event.getType().getType(0)))
					{
						listener.beliefChanged(event.getContent());
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
	 *  
	 *  @param obj object instance that owns the field __initargs
	 *  @param clazz Class definition of the obj object
	 *  @param argtypes Signature of the init method
	 *  @param args Actual argument values for the init method
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
	protected void writeField(Object val, String belname, String fieldname, Object obj)
	{
		writeField(val, belname, fieldname, obj, new EventType(ChangeEvent.BELIEFCHANGED+"."+belname), new EventType(ChangeEvent.FACTCHANGED+"."+belname));
	}
	
	/**
	 *  Method that is called automatically when a belief 
	 *  is written as field access.
	 */
	protected void writeField(Object val, String belname, String fieldname, Object obj, EventType ev1, EventType ev2)
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

			MBelief	mbel = ((MCapability)ip.getCapability().getModelElement()).getBelief(belname);
		
			if(!SUtil.equals(val, oldval))
			{
				publishToolBeliefEvent(ip, mbel);
//				rs.addEvent(new Event(ChangeEvent.BELIEFCHANGED+"."+belname, val));
				rs.addEvent(new Event(ev1, val));
				// execute rulesystem immediately to ensure that variable values are not changed afterwards
				rs.processAllEvents(); 
			}
			
			// observe new value for property changes
//			observeValue(rs, val, ip, ChangeEvent.FACTCHANGED+"."+belname, mbel);
			observeValue(rs, val, ip, ev2, mbel);
			
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
	public static void writeField(Object val, String fieldname, Object obj, BDIAgent agent)
	{
//		System.out.println("write: "+val+" "+fieldname+" "+obj+" "+agent);
		
		// This is the case in inner classes
		if(agent==null)
		{
			try
			{
				Tuple2<Field, Object> res = findFieldWithOuterClass(obj, "__agent");
//				System.out.println("res: "+res);
				agent = (BDIAgent)res.getFirstEntity().get(res.getSecondEntity());
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
		
		String belname	= getBeliefName(obj, fieldname);

		BDIAgentInterpreter ip = (BDIAgentInterpreter)agent.getInterpreter();
		MBelief mbel = ip.getBDIModel().getCapability().getBelief(belname);
		
		// Wrap collections of multi beliefs (if not already a wrapper)
		if(mbel.isMulti(ip.getClassLoader()))
		{
			String addev = ChangeEvent.FACTADDED+"."+belname;
			String remev = ChangeEvent.FACTREMOVED+"."+belname;
			String chev = ChangeEvent.FACTCHANGED+"."+belname;
			if(val instanceof List && !(val instanceof ListWrapper))
			{
				val = new ListWrapper((List<?>)val, ip, addev, remev, chev, mbel);
			}
			else if(val instanceof Set && !(val instanceof SetWrapper))
			{
				val = new SetWrapper((Set<?>)val, ip, addev, remev, chev, mbel);
			}
			else if(val instanceof Map && !(val instanceof MapWrapper))
			{
				val = new MapWrapper((Map<?,?>)val, ip, addev, remev, chev, mbel);
			}
		}
		
		// agent is not null any more due to deferred exe of init expressions but rules are
		// available only after startBehavior
		if(ip.isInited())
		{
			agent.writeField(val, belname, fieldname, obj);
		}
		else
		{
			// In init set field immediately but throw events later, when agent is available.
			
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
				List<Object[]> inits = initwrites.get(agent);
				if(inits==null)
				{
					inits = new ArrayList<Object[]>();
					initwrites.put(agent, inits);
				}
				inits.add(new Object[]{val, belname});
			}
		}
	}
	
	/** Saved init writes. */
	protected final static Map<Object, List<Object[]>> initwrites = new HashMap<Object, List<Object[]>>();
	
	/**
	 * 
	 */
	public static void performInitWrites(BDIAgent agent)
	{
		synchronized(initwrites)
		{
			List<Object[]> writes = initwrites.remove(agent);
			if(writes!=null)
			{
				for(Object[] write: writes)
				{
//					System.out.println("initwrite: "+write[0]+" "+write[1]+" "+write[2]);
//					agent.writeField(write[0], (String)write[1], write[2]);
					BDIAgentInterpreter ip = (BDIAgentInterpreter)agent.getInterpreter();
					RuleSystem rs = ip.getRuleSystem();
					final String belname = (String)write[1];
					Object val = write[0];
					rs.addEvent(new Event(ChangeEvent.BELIEFCHANGED+"."+belname, val));
					MBelief	mbel = ((MCapability)ip.getCapability().getModelElement()).getBelief(belname);
					observeValue(rs, val, (BDIAgentInterpreter)agent.getInterpreter(), ChangeEvent.FACTCHANGED+"."+belname, mbel);
				}
			}
		}
	}
	
	/**
	 *  Method that is called automatically when a belief 
	 *  is written as array access.
	 */
	// todo: allow init writes in constructor also for arrays
	public static void writeArrayField(Object array, final int index, Object val, Object agentobj, String fieldname)
	{
		// This is the case in inner classes
		BDIAgent agent = null;
		if(agentobj instanceof BDIAgent)
		{
			agent = (BDIAgent)agentobj;
		}
		else
		{
			try
			{
				Tuple2<Field, Object> res = findFieldWithOuterClass(agentobj, "__agent");
//				System.out.println("res: "+res);
				agent = (BDIAgent)res.getFirstEntity().get(res.getSecondEntity());
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
		
		final BDIAgentInterpreter ip = (BDIAgentInterpreter)agent.getInterpreter();
		
		// Test if array store is really a belief store instruction by
		// looking up the current belief value and comparing it with the
		// array that is written
		
		String belname	= getBeliefName(agentobj, fieldname);
		MBelief	mbel = ((MCapability)ip.getCapability().getModelElement()).getBelief(belname);
		
		Object curval = mbel.getValue(ip);
		boolean isbeliefwrite = curval==array;
		
		RuleSystem rs = ip.getRuleSystem();
//		System.out.println("write array index: "+val+" "+index+" "+array+" "+agent+" "+fieldname);
		
		Object oldval = null;
		if(isbeliefwrite)
		{
			oldval = Array.get(array, index);
			rs.unobserveObject(oldval);	
		}
		
		Class<?> ct = array.getClass().getComponentType();
		if(boolean.class.equals(ct))
		{
			val = ((Integer)val)==1? Boolean.TRUE: Boolean.FALSE;
		}
		else if(byte.class.equals(ct))
		{
//			val = new Byte(((Integer)val).byteValue());
			val = Byte.valueOf(((Integer)val).byteValue());
		}
		Array.set(array, index, val);
		
		if(isbeliefwrite)
		{
			observeValue(rs, val, ip, new EventType(new String[]{ChangeEvent.FACTCHANGED, belname}), mbel);
			
			if(!SUtil.equals(val, oldval))
			{
				publishToolBeliefEvent(ip, mbel);

				Event ev = new Event(new EventType(new String[]{ChangeEvent.FACTCHANGED, belname}), val); // todo: index
				rs.addEvent(ev);
				// execute rulesystem immediately to ensure that variable values are not changed afterwards
				rs.processAllEvents(); 
			}
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
	public static void observeValue(RuleSystem rs, Object val, final BDIAgentInterpreter agent, final String etype, final MBelief mbel)
	{
		observeValue(rs, val, agent, new EventType(etype), mbel);
	}
	
	/**
	 * 
	 */
	public static void observeValue(RuleSystem rs, Object val, final BDIAgentInterpreter agent, final EventType etype, final MBelief mbel)
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
							publishToolBeliefEvent(agent, mbel);
							
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
		MBelief	bel	= bdimodel.getCapability().getBelief(belname);
		Object	ret	= bel.getValue((BDIAgentInterpreter)getInterpreter());
		
		if(ret==null)
		{
			if(type.equals(boolean.class))
			{
				ret	= Boolean.FALSE;
			}
			else if(type.equals(char.class))
			{
				ret	= Character.valueOf((char)0);
			}
			else if(SReflect.getWrappedType(type)!=type)	// Number type
			{
				ret	= Integer.valueOf(0);
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
		MBelief	mbel = bdimodel.getCapability().getBelief(belname);

		// Maybe unobserve old value
		Object	old	= mbel.getValue((BDIAgentInterpreter)getInterpreter());

		boolean	field = mbel.setValue((BDIAgentInterpreter)getInterpreter(), value);
		
		if(field)
		{
			BDIAgentInterpreter ip = (BDIAgentInterpreter)getInterpreter();
			RuleSystem rs = (ip).getRuleSystem();
			rs.unobserveObject(old);	
			createChangeEvent(value, this, mbel.getName());
			observeValue(rs, value, ip, ChangeEvent.FACTCHANGED+"."+mbel.getName(), mbel);
		}
	}
	
	/**
	 *  Create a belief changed event.
	 *  @param val The new value.
	 *  @param agent The agent.
	 *  @param belname The belief name.
	 */
	public static void createChangeEvent(Object val, final BDIAgent agent, final String belname)
//	public static void createChangeEvent(Object val, final BDIAgent agent, MBelief mbel)
	{
//		System.out.println("createEv: "+val+" "+agent+" "+belname);
		BDIAgentInterpreter ip = (BDIAgentInterpreter)agent.getInterpreter();
		
		MBelief mbel = ip.getBDIModel().getCapability().getBelief(belname);
		
		RuleSystem rs = (ip).getRuleSystem();
		rs.addEvent(new Event(ChangeEvent.BELIEFCHANGED+"."+belname, val));
		
		publishToolBeliefEvent(ip, mbel);
	}
	
	/**
	 * 
	 */
	public static void publishToolBeliefEvent(BDIAgentInterpreter ip, MBelief mbel)//, String evtype)
	{
		if(mbel!=null && ip.hasEventTargets(PublishTarget.TOSUBSCRIBERS, PublishEventLevel.FINE))
		{
			long time = System.currentTimeMillis();//getClockService().getTime();
			MonitoringEvent mev = new MonitoringEvent();
			mev.setSourceIdentifier(ip.getComponentIdentifier());
			mev.setTime(time);
			
			BeliefInfo info = BeliefInfo.createBeliefInfo(ip, mbel, ip.getClassLoader());
//			mev.setType(evtype+"."+IMonitoringEvent.SOURCE_CATEGORY_FACT);
			mev.setType(IMonitoringEvent.EVENT_TYPE_MODIFICATION+"."+IMonitoringEvent.SOURCE_CATEGORY_FACT);
//			mev.setProperty("sourcename", element.toString());
			mev.setProperty("sourcetype", info.getType());
			mev.setProperty("details", info);
			mev.setLevel(PublishEventLevel.FINE);
			
			ip.publishEvent(mev, PublishTarget.TOSUBSCRIBERS);
		}
	}
	
	/**
	 * 
	 */
	protected static String getBeliefName(Object obj, String fieldname)
	{
		String	gn	= null;
		try
		{
			Field	gnf	= obj.getClass().getField("__globalname");
			gnf.setAccessible(true);
			gn	= (String)gnf.get(obj);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		String belname	= gn!=null ? gn + BDIAgentInterpreter.CAPABILITY_SEPARATOR + fieldname : fieldname;
		return belname;
	}
	
	//-------- methods for goal/plan parameter rewrites --------
	
	/**
	 *  Method that is called automatically when a parameter 
	 *  is written as field access.
	 */
	public static void writeParameterField(Object val, String fieldname, Object obj, BDIAgent agent)
	{
//		System.out.println("write: "+val+" "+fieldname+" "+obj+" "+agent);
		
		// This is the case in inner classes
		if(agent==null)
		{
			try
			{
				Tuple2<Field, Object> res = findFieldWithOuterClass(obj, "__agent");
//					System.out.println("res: "+res);
				agent = (BDIAgent)res.getFirstEntity().get(res.getSecondEntity());
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

		BDIAgentInterpreter ip = (BDIAgentInterpreter)agent.getInterpreter();
		String elemname = obj.getClass().getName();
		MGoal mgoal = ip.getBDIModel().getCapability().getGoal(elemname);
		
//		String paramname = elemname+"."+fieldname; // ?

		if(mgoal!=null)
		{
			MParameter mparam = mgoal.getParameter(fieldname);
			if(mparam!=null)
			{
				// Wrap collections of multi beliefs (if not already a wrapper)
				if(mparam.isMulti(ip.getClassLoader()))
				{
					EventType addev = new EventType(new String[]{ChangeEvent.VALUEADDED, elemname, fieldname});
					EventType remev = new EventType(new String[]{ChangeEvent.VALUEREMOVED, elemname, fieldname});
					EventType chev = new EventType(new String[]{ChangeEvent.VALUECHANGED, elemname, fieldname});
					if(val instanceof List && !(val instanceof ListWrapper))
					{
						val = new ListWrapper((List<?>)val, ip, addev, remev, chev, null);
					}
					else if(val instanceof Set && !(val instanceof SetWrapper))
					{
						val = new SetWrapper((Set<?>)val, ip, addev, remev, chev, null);
					}
					else if(val instanceof Map && !(val instanceof MapWrapper))
					{
						val = new MapWrapper((Map<?,?>)val, ip, addev, remev, chev, null);
					}
				}
			}
		}
		
		// agent is not null any more due to deferred exe of init expressions but rules are
		// available only after startBehavior
		if(ip.isInited())
		{
			EventType chev1 = new EventType(new String[]{ChangeEvent.PARAMETERCHANGED, elemname, fieldname});
			EventType chev2 = new EventType(new String[]{ChangeEvent.VALUECHANGED, elemname, fieldname});
			agent.writeField(val, null, fieldname, obj, chev1, chev2);
		}
//			else
//			{
//				// In init set field immediately but throw events later, when agent is available.
//				
//				try
//				{
//					setFieldValue(obj, fieldname, val);
//				}
//				catch(Exception e)
//				{
//					e.printStackTrace();
//					throw new RuntimeException(e);
//				}
//				synchronized(initwrites)
//				{
//					List<Object[]> inits = initwrites.get(agent);
//					if(inits==null)
//					{
//						inits = new ArrayList<Object[]>();
//						initwrites.put(agent, inits);
//					}
//					inits.add(new Object[]{val, belname});
//				}
//			}
	}
	
	/**
	 *  Method that is called automatically when a belief 
	 *  is written as array access.
	 */
	// todo: allow init writes in constructor also for arrays
	public static void writeArrayParameterField(Object array, final int index, Object val, Object agentobj, String fieldname)
	{
		// This is the case in inner classes
		BDIAgent agent = null;
		if(agentobj instanceof BDIAgent)
		{
			agent = (BDIAgent)agentobj;
		}
		else
		{
			try
			{
				Tuple2<Field, Object> res = findFieldWithOuterClass(agentobj, "__agent");
//				System.out.println("res: "+res);
				agent = (BDIAgent)res.getFirstEntity().get(res.getSecondEntity());
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
		
		final BDIAgentInterpreter ip = (BDIAgentInterpreter)agent.getInterpreter();
		
		// Test if array store is really a belief store instruction by
		// looking up the current belief value and comparing it with the
		// array that is written
		
		boolean isparamwrite = false;
		
		MGoal mgoal = ((MCapability)ip.getCapability().getModelElement()).getGoal(agentobj.getClass().getName());
		if(mgoal!=null)
		{
			MParameter mparam = mgoal.getParameter(fieldname);
			if(mparam!=null)
			{
				Object curval = mparam.getValue(agentobj, ip.getClassLoader());
				isparamwrite = curval==array;
			}
		}
		RuleSystem rs = ip.getRuleSystem();
//		System.out.println("write array index: "+val+" "+index+" "+array+" "+agent+" "+fieldname);
		
		Object oldval = null;
		if(isparamwrite)
		{
			oldval = Array.get(array, index);
			rs.unobserveObject(oldval);	
		}
		
		Class<?> ct = array.getClass().getComponentType();
		if(boolean.class.equals(ct))
		{
			val = ((Integer)val)==1? Boolean.TRUE: Boolean.FALSE;
		}
		else if(byte.class.equals(ct))
		{
//			val = new Byte(((Integer)val).byteValue());
			val = Byte.valueOf(((Integer)val).byteValue());
		}
		Array.set(array, index, val);
		
		if(isparamwrite)
		{
			observeValue(rs, val, ip, new EventType(new String[]{ChangeEvent.VALUECHANGED, mgoal.getName(), fieldname}), null);
			
			if(!SUtil.equals(val, oldval))
			{
				Event ev = new Event(new EventType(new String[]{ChangeEvent.VALUECHANGED, mgoal.getName(), fieldname}), val); // todo: index
				rs.addEvent(ev);
				// execute rulesystem immediately to ensure that variable values are not changed afterwards
				rs.processAllEvents(); 
			}
		}
	}
}
