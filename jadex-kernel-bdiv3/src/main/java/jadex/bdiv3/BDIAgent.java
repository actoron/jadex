package jadex.bdiv3;

import jadex.bdiv3.model.BDIModel;
import jadex.bdiv3.model.MCapability;
import jadex.bdiv3.model.MGoal;
import jadex.bdiv3.runtime.ChangeEvent;
import jadex.bdiv3.runtime.IBeliefListener;
import jadex.bdiv3.runtime.ICapability;
import jadex.bdiv3.runtime.impl.BDIAgentInterpreter;
import jadex.bdiv3.runtime.impl.RGoal;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.commons.IResultCommand;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.commons.beans.PropertyChangeEvent;
import jadex.commons.future.ExceptionDelegationResultListener;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  Base class for application agents.
 */
public class BDIAgent extends MicroAgent
{
	/**
	 *  Get the capability.
	 *  @return the capability.
	 */
	public ICapability getCapability()
	{
		return ((BDIAgentInterpreter)getInterpreter()).getCapability();
	}
	
//	/**
//	 *  Dispatch a goal wait for its result.
//	 */
//	public <T> IFuture<T> dispatchTopLevelGoal(final T goal)
//	{
//		final Future<T> ret = new Future<T>();
//		
//		BDIAgentInterpreter ip = (BDIAgentInterpreter)getInterpreter();
//		BDIModel bdim = ip.getBDIModel();
//		MGoal mgoal = bdim.getCapability().getGoal(goal.getClass().getName());
//		if(mgoal==null)
//			throw new RuntimeException("Unknown goal type: "+goal);
//		final RGoal rgoal = new RGoal(mgoal, goal, null);
//		rgoal.addGoalListener(new ExceptionDelegationResultListener<Void, T>(ret)
//		{
//			public void customResultAvailable(Void result)
//			{
//				ret.setResult(goal);
//			}
//		});
//
////		System.out.println("adopt goal");
//		RGoal.adoptGoal(rgoal, getInterpreter().getInternalAccess());
//		return ret;
//	}
	
	/**
	 *  Dispatch a goal wait for its result.
	 */
	public <T, E> IFuture<E> dispatchTopLevelGoal(final T goal)
	{
		final Future<E> ret = new Future<E>();
		
		BDIAgentInterpreter ip = (BDIAgentInterpreter)getInterpreter();
		BDIModel bdim = ip.getBDIModel();
		final MGoal mgoal = bdim.getCapability().getGoal(goal.getClass().getName());
		if(mgoal==null)
			throw new RuntimeException("Unknown goal type: "+goal);
		final RGoal rgoal = new RGoal(ip.getInternalAccess(), mgoal, goal, null);
		rgoal.addGoalListener(new ExceptionDelegationResultListener<Void, E>(ret)
		{
			public void customResultAvailable(Void result)
			{
				Object res = RGoal.getGoalResult(goal, mgoal, getClassLoader());
				ret.setResult((E)res);
			}
		});

//		System.out.println("adopt goal");
		RGoal.adoptGoal(rgoal, getInterpreter().getInternalAccess());
		
		return ret;
	}
	
	/**
	 *  Add a belief listener.
	 *  @param name The belief name.
	 *  @param listener The belief listener.
	 */
	public void addBeliefListener(final String name, final IBeliefListener listener)
	{
		List<EventType> events = new ArrayList<EventType>();
		BDIAgentInterpreter.addBeliefEvents(this, events, name);

		final boolean multi = ((MCapability)getCapability().getModelElement()).getBelief(name).isMulti(getClassLoader());
		
		String rulename = name+"_belief_listener_"+System.identityHashCode(listener);
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
		BDIAgentInterpreter ip = (BDIAgentInterpreter)getInterpreter();
		String rulename = name+"_belief_listener_"+System.identityHashCode(listener);
		ip.getRuleSystem().getRulebase().removeRule(rulename);
	}
	
	//-------- internal method used for rewriting field access -------- 
	
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
			if(val!=null)
			{
				rs.observeObject(val, true, false, new IResultCommand<IFuture<IEvent>, PropertyChangeEvent>()
				{
					public IFuture<IEvent> execute(final PropertyChangeEvent event)
					{
						return scheduleStep(new IComponentStep<IEvent>()
						{
							public IFuture<IEvent> execute(IInternalAccess ia)
							{
								Event ev = new Event(new EventType(new String[]{ChangeEvent.FACTCHANGED, fieldname}), event.getNewValue());
								return new Future<IEvent>(ev);
							}
						});
					}
				});
			}
			
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
			//							Event ev = new Event(ChangeEvent.FACTCHANGED+"."+fieldname+"."+event.getPropertyName(), event.getNewValue());
										Event ev = new Event(ChangeEvent.FACTCHANGED+"."+fieldname, event.getNewValue());
										return new Future<IEvent>(ev);
									}
								});
							}
						});
					}
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
//			System.out.println("write array index: "+val+" "+index+" "+array+" "+agent+" "+fieldname);
			
			Object oldval = Array.get(array, index);
			
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
			
			if(!SUtil.equals(val, oldval))
			{
				BDIAgentInterpreter ip = (BDIAgentInterpreter)agent.getInterpreter();
				RuleSystem rs = ip.getRuleSystem();
				
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
}
