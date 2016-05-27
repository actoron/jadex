package jadex.rules.eca;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jadex.commons.IResultCommand;
import jadex.commons.Tuple2;
import jadex.commons.beans.PropertyChangeEvent;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.FutureHelper;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.commons.future.IntermediateFuture;
import jadex.rules.eca.annotations.Action;
import jadex.rules.eca.annotations.Condition;
import jadex.rules.eca.propertychange.PropertyChangeManager;

/**
 *  The rule system is the main entry point. It contains the rulebase
 *  with all rules and knows about the observed objects.
 */
public class RuleSystem
{
	//-------- attributes --------
	
	/** The rulebase. */
	protected IRulebase rulebase;
	
	/** The rules generated for an object. */
	protected IdentityHashMap<Object, Tuple2<Object, IRule<?>[]>> rules;

	/** The context for rule action execution. */
	protected Object context;
	
	/** The PropertyChangeManager to add/remove handlers and manage events */
	protected PropertyChangeManager pcman;
	
	/** The execution mode (direct vs queue). */
	protected boolean queueevents = true;
	
//	/** Flag if ruleengine is currently in processing (to avoid interleaved calls). */
//	protected boolean processing = false;
	
	//-------- constructors --------
	
	/**
	 *  Create a new rule system.
	 */
	public RuleSystem(Object context)
	{
		this(context, true);
	}
	
	/**
	 *  Create a new rule system.
	 */
	public RuleSystem(Object context, boolean queueevents)
	{
		this.context = context;
		this.rulebase = new Rulebase();
		this.rules = new IdentityHashMap<Object, Tuple2<Object, IRule<?>[]>>(); // objects may change
		this.pcman = PropertyChangeManager.createInstance();
		this.queueevents = queueevents;
	}

	//-------- methods --------
		
	/**
	 *  Get the rulebase.
	 *  @return The rule base.
	 */
	public IRulebase getRulebase()
	{
		return rulebase;
	}
	
	/**
	 *  Process the next event by
	 *  - finding rules that are sensible to the event type
	 *  - evaluate the conditions of these conditions
	 *  - fire actions of triggered rules.
	 */
	public IIntermediateFuture<RuleEvent> processEvent()
	{
		final IntermediateFuture<RuleEvent> ret = new IntermediateFuture<RuleEvent>();
		
		if(pcman.hasEvents())
		{
			IEvent event = pcman.removeEvent(0);
			
//			if(event.getType().toString().indexOf("factchanged.myself")!=-1)
//				System.out.println("sdgfsdgf");
			
//			if(event.getType().getType(0).indexOf("factadded")!=-1)// && event.getType().getType(1).indexOf("mybean")!=-1)
//				&& event.getType().getType(1).indexOf("Ambu")!=-1)
//				System.out.println("proc ev: "+event);
				
			List<IRule<?>> rules = rulebase.getRules(event.getType());
			
			if(rules!=null)
			{
				IRule<?>[] rs = rules.toArray(new IRule<?>[rules.size()]);
				processRules(rs, 0, event, ret).addResultListener(new IResultListener<Void>()
				{
					public void resultAvailable(Void result)
					{
						ret.setFinished();
					}
					
					public void exceptionOccurred(Exception exception)
					{
						ret.setException(exception);
					}
				});
			}
			else
			{
//				System.out.println("found no rules for: "+event.getType());
				
				ret.setFinished();
			}
		}
		else
		{
			ret.setFinished();
		}
		
		return ret;
	}
	
	/**
	 *  Process a given rule set.
	 */
	protected IFuture<Void> processRules(final IRule<?>[] rules, final int i, final IEvent event, final IntermediateFuture<RuleEvent> res)
	{
		final Future<Void> ret = new Future<Void>();
		
		if(i<rules.length)
		{
			if(rules[i].getCondition()!=null)
			{
				rules[i].getCondition().evaluate(event).addResultListener(new IResultListener<Tuple2<Boolean,Object>>()
				{
					public void resultAvailable(Tuple2<Boolean, Object> result)
					{
						if(result.getFirstEntity().booleanValue())
						{
//							System.out.println("Rule triggered: "+rules[i]+", "+event);
							
							IFuture fut = (IFuture<Object>)rules[i].getAction().execute(event, (IRule)rules[i], context, result.getSecondEntity());
							
							if(fut instanceof IIntermediateFuture)
							{
								((IIntermediateFuture<Object>)fut).addResultListener(new IIntermediateResultListener<Object>()
								{
									public void intermediateResultAvailable(Object result)
									{
										RuleIntermediateEvent ev = new RuleIntermediateEvent(rules[i].getName(), result);
										res.addIntermediateResult(ev);
									}
									
									public void finished()
									{
										processRules(rules, i+1, event, res).addResultListener(new DelegationResultListener<Void>(ret));
									}
									
									public void resultAvailable(Collection<Object> result)
									{
										RuleEvent ev = new RuleEvent(rules[i].getName(), result);
										res.addIntermediateResult(ev);
										processRules(rules, i+1, event, res).addResultListener(new DelegationResultListener<Void>(ret));
									}
									
									public void exceptionOccurred(Exception exception)
									{
										exception.printStackTrace();
										processRules(rules, i+1, event, res).addResultListener(new DelegationResultListener<Void>(ret));
									}
								});
							}
							else
							{
								fut.addResultListener(new IResultListener<Object>()
								{
									public void resultAvailable(Object result) 
									{
										RuleEvent ev = new RuleEvent(rules[i].getName(), result);
										res.addIntermediateResult(ev);
										processRules(rules, i+1, event, res).addResultListener(new DelegationResultListener<Void>(ret));
									}
									
									public void exceptionOccurred(Exception exception)
									{
										exception.printStackTrace();
										processRules(rules, i+1, event, res).addResultListener(new DelegationResultListener<Void>(ret));
									}
								});
							}
						}
						else
						{
							processRules(rules, i+1, event, res).addResultListener(new DelegationResultListener<Void>(ret));
						}
					}
					
					public void exceptionOccurred(Exception exception)
					{
					}
				});
			}
			else
			{
				ret.setResult(null);
			}
		}
		else
		{
			ret.setResult(null);
		}
		
		return ret;
	}
	
//	/**
//	 * 
//	 */
//	protected IFuture<Void> processRules(final IRule<?>[] rules, final int i, final IEvent event, final IntermediateFuture<RuleEvent> res)
//	{
//		final Future<Void> ret = new Future<Void>();
//		
//		if(i<rules.length)
//		{
//			Tuple2<Boolean, Object> cres = rules[i].getCondition()==null? ICondition.TRUE: rules[i].getCondition().evaluate(event);
//			if(cres.getFirstEntity().booleanValue())
//			{
//				IFuture<Object> fut = (IFuture<Object>)rules[i].getAction().execute(event, (IRule)rules[i], context, cres.getSecondEntity());
//				
//				fut.addResultListener(new IResultListener<Object>()
//				{
//					public void resultAvailable(Object result) 
//					{
//						RuleEvent ev = new RuleEvent(rules[i].getName(), result);
//						res.addIntermediateResult(ev);
//						processRules(rules, i+1, event, res).addResultListener(new DelegationResultListener<Void>(ret));
//					}
//					
//					public void exceptionOccurred(Exception exception)
//					{
//						exception.printStackTrace();
//						processRules(rules, i+1, event, res).addResultListener(new DelegationResultListener<Void>(ret));
//					}
//				});
//			}
//			else
//			{
//				processRules(rules, i+1, event, res).addResultListener(new DelegationResultListener<Void>(ret));
//			}
//		}
//		else
//		{
//			ret.setResult(null);
//		}
//		
//		return ret;
//	}
	
//	/**
//	 *  Process events until the event queue is empty or max
//	 *  events have been processed.
//	 */
//	public IFuture<Void> processAllEvents()
//	{
////		return processAllEvents(-1);
//		
//		final Future<Void> ret = new Future<Void>();
//		
//		final int[] opencalls = new int[1];
//		
//		while(pcman.hasEvents())
//		{
//			opencalls[0]++;
//			
//			processEvent().addResultListener(new IResultListener<Collection<RuleEvent>>()
//			{
//				Exception ex = null;
//				public void resultAvailable(Collection<RuleEvent> result)
//				{
//					proceed();
//				}
//				
//				public void exceptionOccurred(Exception exception)
//				{
//					ex = exception;
//					proceed();
//				}
//				
//				protected void proceed()
//				{
//					// When all events have been processed and no opencalls
//					if(--opencalls[0]==0 && pcman.getSize()==0)
//					{
//						if(ex==null)
//						{
//							ret.setResult(null);
//						}
//						else
//						{
//							ret.setException(ex);
//						}
//					}
//				}
//			});
//		}
//		
//		return ret;
//	}
	
	/**
	 *  Process events until the event queue is empty or max
	 *  events have been processed.
	 */
	public IFuture<Void> processAllEvents()
	{
		if(pcman.hasEvents())
		{
			final Future<Void> ret = new Future<Void>();
			processEvent().addResultListener(new IResultListener<Collection<RuleEvent>>()
			{
//				Exception ex = null;
				public void resultAvailable(Collection<RuleEvent> result)
				{
					processAllEvents().addResultListener(new DelegationResultListener<Void>(ret));
				}
				
				public void exceptionOccurred(Exception exception)
				{
					exception.printStackTrace();
//					ex = exception;
					processAllEvents().addResultListener(new DelegationResultListener<Void>(ret));
				}
			});
			
			return ret;
		}
		else
		{
			return IFuture.DONE;
		}
	}
	
//	/**
//	 *  Process events until the event queue is empty or max
//	 *  events have been processed.
//	 *  @return True if was aborted due to reaching max events.
//	 */
//	public boolean processAllEvents(int max)
//	{
//		int i=0;
//		
//		for(i=0; pcman.hasEvents() && (max==-1 || i<max); i++)
//		{
//			processEvent();
//		}
//		
//		return i==max;
//	}
	
//	/**
//	 *  Process events until the event queue is empty or max
//	 *  events have been processed.
//	 *  @return True if was aborted due to reaching max events.
//	 */
//	public boolean processAllEvents(int max)
//	{
//		int i=0;
//		
//		for(i=0; pcman.hasEvents() && (max==-1 || i<max); i++)
//		{
//			processEvent();
//		}
//		
//		return i==max;
//	}
	
	/**
	 *  Add an event.
	 */
	public IFuture<Void> addEvent(IEvent event)
	{
//		if(event.getType().toString().indexOf("factchanged.myself")!=-1)
//			System.out.println("added: "+event.getType()+" "+event.getContent());
//		if(event.getType().getTypes().length==1)
//			System.out.println("herer: "+event.getType());
//		if(event.getType().getType(0).indexOf("goaloption")!=-1 && event.getType().getType(1).indexOf("Treat")!=-1
//			&& event.getType().getType(1).indexOf("Ambu")!=-1)
//			System.out.println("add event: "+event);
//		if(event.getType().getType(0).indexOf("factadded")!=-1 && event.getType().getType(1).indexOf("wastebins")!=-1)
//			System.out.println("add event: "+event);

		final IFuture<Void> ret;
		
		pcman.addEvent(event);
		
		if(!queueevents)
		{
			// If actions add further events they will be processed as well
//			ret = new Future<Void>();
//			processEvent().addResultListener(new ExceptionDelegationResultListener<Collection<RuleEvent>, Void>(ret)
//			{
//				public void customResultAvailable(Collection<RuleEvent> result)
//				{
//					ret.setResult(null);
//				}
//			});
			
			// This works also if the mode is changed during execution and some events are in the queue
			// execute rulesystem immediately to ensure that variable values are not changed afterwards
			ret = processAllEvents();

			
//			// Simulate microplansteps by executing all effects immediately (hack: allow configuration sync/async)
			FutureHelper.notifyStackedListeners();
		}
		else
		{
//			 ret = (Future<Void>)IFuture.DONE;
			 ret = IFuture.DONE;
		}
		
		return ret;
	}
	
	/**
	 *  Test if at least one event is available.
	 */
	public boolean isEventAvailable()
	{
		return pcman.hasEvents();
	}

	/**
	 *  Get the queueevents.
	 *  @return The queueevents
	 */
	public boolean isQueueEvents()
	{
		return queueevents;
	}

	/**
	 *  The queueevents to set.
	 *  @param queueevents The queueevents to set
	 */
	public void setQueueEvents(boolean queueevents)
	{
		this.queueevents = queueevents;
	}
	
	
	/**
	 *  Monitor an object to the rule engine.
	 *  - Extracts conditions
	 *  - Extracts actions
	 *  - Creates rules from condition/action pairs 
	 *      and adds them to the rulebase.
	 *  - Subscribes for events
	 */
	public Object observeObject(final Object object, boolean bean, boolean hasrules, 
		IResultCommand<IFuture<Void>, PropertyChangeEvent> eventadder)
	{
		// Create proxy object if eventcreators are present
		Object proxy = object;
					
//		if(object.getClass().getName().indexOf("Wastebin")!=-1)
//			System.out.println("sdfsdff");
		
		Class<?> clazz = object.getClass();

		if(bean && !(object instanceof Class))
		{
			pcman.addPropertyChangeListener(object, eventadder);
		}
		
		if(hasrules)
		{
			final Map<Method, IResultCommand<?,?>> eventcreators = new HashMap<Method, IResultCommand<?,?>>();
			final Map<String, Rule<?>> rules = new HashMap<String, Rule<?>>();
			
			// Analyze the dynamic or static methods of the object (static if object is a class)
			// todo: what about using constructors of classes
			if(!(object instanceof Class))
			{
				while(!clazz.equals(Object.class))
				{
					Method[] methods = clazz.getDeclaredMethods();
					for(int i=0; i<methods.length; i++)
					{
						if(!Modifier.isStatic(methods[i].getModifiers()))
						{
							analyzeMethod(methods[i], object, eventcreators, rules);
						}
					}
					clazz = clazz.getSuperclass();
				}
			}
			else
			{
				Method[] methods = ((Class<?>)object).getDeclaredMethods();
				for(int i=0; i<methods.length; i++)
				{
					if(Modifier.isStatic(methods[i].getModifiers()))
					{
						analyzeMethod(methods[i], object, eventcreators, rules);
					}
				}
			}
			
			// Add rules to rulebase
			for(Iterator<Rule<?>> it=rules.values().iterator(); it.hasNext(); )
			{
				Rule<?> rule = it.next();
				if(rule.getAction()==null || rule.getCondition()==null 
					|| rule.getEvents()==null || rule.getEvents().size()==0)
				{
					throw new RuntimeException("Rule is incomplete: "+rule.getName());
				}
				rulebase.addRule(rule);
			}
			
			//todo: fixme
			
	//		if(eventcreators.size()>0)
	//		{
	//			ProxyFactory pf = new ProxyFactory(object);
	//			pf.addAdvice(new MethodInterceptor()
	//			{
	//				public Object invoke(MethodInvocation mi) throws Throwable
	//				{
	//					Object ret = mi.getMethod().invoke(mi.getThis(), mi.getArguments());
	//					IResultCommand creator = (IResultCommand)eventcreators.get(mi.getMethod());
	//					if(creator!=null)
	//					{
	//						Event event = (Event)creator.execute(null);
	//						addEvent(event);
	//	//					System.out.println("created event: "+event);
	//					}
	//					return ret;
	//			    }
	//			});
	//			proxy = pf.getProxy();
	//		}
	
			this.rules.put(object, new Tuple2(proxy, rules.values().toArray(new IRule[rules.size()])));
	
			// Recusrively call observe object on all direct monitored fields.
			// todo: do we want this?
	//		if(!(object instanceof Class))
	//		{
	//			clazz = object.getClass();
	//			Field[] fields = clazz.getDeclaredFields();
	//			for(int i=0; i<fields.length; i++)
	//			{
	//				if(fields[i].isAnnotationPresent(RuleObject.class))
	//				{
	//					fields[i].setAccessible(true);
	//					try
	//					{
	//						Object subobject = fields[i].get(object);
	//						observeObject(subobject);
	//					}
	//					catch(Exception e)
	//					{
	//						e.printStackTrace();
	//					}
	//				}
	//			}
	//		}
		}
		
		return proxy;
	}
	
	/**
	 *  Unobserve an object.
	 */
	public void unobserveObject(final Object object, IResultCommand<IFuture<Void>, PropertyChangeEvent> eventadder)
	{
		if(object==null)
			return;
		
//		if(object.getClass().getName().indexOf("Wastebin")!=-1)
//			System.out.println("sdfsdff");
		
		pcman.removePropertyChangeListener(object, eventadder);

		Tuple2<Object, IRule<?>[]> tup = rules.remove(object);
		if(tup!=null)
		{
			IRule<?>[] rls = tup.getSecondEntity();
			for(int i=0; i<rls.length; i++)
			{
				rulebase.removeRule(rls[i].getName());
			}
		}
		
//		// Recusrively call unobserve object on all direct monitored fields.
//		Class<?> clazz = object.getClass();
//		Field[] fields = clazz.getDeclaredFields();
//		for(int i=0; i<fields.length; i++)
//		{
//			if(fields[i].isAnnotationPresent(RuleObject.class))
//			{
//				fields[i].setAccessible(true);
//				try
//				{
//					Object subobject = fields[i].get(object);
//					unobserveObject(subobject);
//				}
//				catch(Exception e)
//				{
//					e.printStackTrace();
//				}
//			}
//		}
	}

	/**
	 *  Inspects a method for
	 *  - condition annotation
	 *  - action annotation
	 */
	protected void analyzeMethod(Method method, Object object, Map<Method, IResultCommand<?,?>> eventcreators,
		Map<String, Rule<?>> rules)
	{
		if(method.isAnnotationPresent(jadex.rules.eca.annotations.Event.class))
		{
			jadex.rules.eca.annotations.Event event = method.getAnnotation(jadex.rules.eca.annotations.Event.class);
			final String type = event.value();
			FetchFieldCommand com = new FetchFieldCommand(object, type);
			eventcreators.put(method, com);
		}
		else if(method.isAnnotationPresent(Condition.class))
		{
			Condition cond = method.getAnnotation(Condition.class);
			final String name = cond.value();
			final Method m = method;

			Rule<?> rule = rules.get(name);
			if(rule==null)
			{
				rule = new Rule(name);
				rules.put(name, rule);
			}
			
			// Find event types
			Annotation[][] paramannos = m.getParameterAnnotations();
			List<String> events = new ArrayList<String>();
			for(int j=0; j<paramannos.length; j++)
			{
				Annotation[] annos = paramannos[j];
				for(int k=0; k<annos.length; k++)
				{
					if(annos[k] instanceof jadex.rules.eca.annotations.Event)
					{
						String type = ((jadex.rules.eca.annotations.Event)annos[k]).value();
						events.add(type);
					}
				}
			}
			if(events.size()==0)
				throw new RuntimeException("Event type not found: "+method);
			
			rule.setEventNames(events);
			
			rule.setCondition(new jadex.rules.eca.MethodCondition(object, m));
		}
		else if(method.isAnnotationPresent(Action.class))
		{
			Action cond = method.getAnnotation(Action.class);
			final String name = cond.value();
			final Method m = method;
			
			Rule<?> rule = rules.get(name);
			if(rule==null)
			{
				rule = new Rule(name);
				rules.put(name, rule);
			}
			
			rule.setAction(new MethodAction(object, m));
		}
	}
}

/**
 *  Creates a new event based on a field name and value.
 */
class FetchFieldCommand implements IResultCommand<IEvent, Object>
{
	/** The object. */
	protected Object object;
	
	/** The name. */
	protected String name;
	
	/**
	 *  Create a new FetchFieldCommand.
	 */
	public FetchFieldCommand(Object object, String name)
	{
		this.object = object;
		this.name = name;
	}

	/**
	 *  Execute the command.
	 *  
	 *  Fetches the field value and return an event with
	 *  type = field name
	 *  content = field value
	 */
	public IEvent execute(Object args)
	{
		try
		{
			Field f = object.getClass().getDeclaredField(name);
			f.setAccessible(true);
			Object content = f.get(object);
			return new Event(name, content);
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
}
