package jadex.rules.eca;

import jadex.commons.IResultCommand;
import jadex.commons.SReflect;
import jadex.commons.Tuple2;
import jadex.rules.eca.annotations.Action;
import jadex.rules.eca.annotations.Condition;
import jadex.rules.eca.annotations.RuleObject;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.ProxyFactory;

/**
 *  The rule system is the main entry point. It contains the rulebase
 *  with all rules and knows about the observed objects.
 */
public class RuleSystem
{
	/** The argument types for property change listener adding/removal (cached for speed). */
	protected static Class[]	PCL	= new Class[]{PropertyChangeListener.class};
	
	//-------- attributes --------
	
	/** The event list. */
	protected List<IEvent> events;
	
	/** The rulebase. */
	protected IRulebase rulebase;
	
	/** The rules generated for an object. */
	protected IdentityHashMap<Object, Tuple2<Object, IRule[]>> rules;

	/** The Java beans property change listeners. */
	protected Map<Object, PropertyChangeListener> pcls;
	
	//-------- constructors --------
	
	/**
	 *  Create a new rule system.
	 */
	public RuleSystem()
	{
		this.events = new ArrayList<IEvent>();
		this.rulebase = new Rulebase();
		this.rules = new IdentityHashMap<Object, Tuple2<Object, IRule[]>>(); // objects may change
	}

	//-------- methods --------
	
	/**
	 *  Monitor an object to the rule engine.
	 *  - Extracts conditions
	 *  - Extracts actions
	 *  - Creates rules from condition/action pairs 
	 *      and adds them to the rulebase.
	 *  - Subscribes for events
	 */
	public Object observeObject(final Object object)
	{
		Class clazz = object.getClass();
		
//		addPropertyChangeListener(object);
		
		final Map<Method, IResultCommand> eventcreators = new HashMap<Method, IResultCommand>();
		final Map<String, Rule> rules = new HashMap<String, Rule>();
		
		while(!clazz.equals(Object.class))
		{
			Method[] methods = clazz.getDeclaredMethods();
			for(int i=0; i<methods.length; i++)
			{
				if(methods[i].isAnnotationPresent(jadex.rules.eca.annotations.Event.class))
				{
					jadex.rules.eca.annotations.Event event = methods[i].getAnnotation(jadex.rules.eca.annotations.Event.class);
					final String type = event.value();
					FetchFieldCommand com = new FetchFieldCommand(object, type);
					eventcreators.put(methods[i], com);
				}
				else if(methods[i].isAnnotationPresent(Condition.class))
				{
					Condition cond = methods[i].getAnnotation(Condition.class);
					final String name = cond.value();
					final Method m = methods[i];

					Rule rule = rules.get(name);
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
						throw new RuntimeException("Event type not found: "+methods[i]);
					
					rule.setEvents(events);
					
					rule.setCondition(new jadex.rules.eca.Condition(object, m));
				}
				else if(methods[i].isAnnotationPresent(Action.class))
				{
					Action cond = methods[i].getAnnotation(Action.class);
					final String name = cond.value();
					final Method m = methods[i];
					
					Rule rule = rules.get(name);
					if(rule==null)
					{
						rule = new Rule(name);
						rules.put(name, rule);
					}
					
					rule.setAction(new jadex.rules.eca.Action(object, m));
				}
			}
			clazz = clazz.getSuperclass();
		}
		
		for(Iterator<Rule> it=rules.values().iterator(); it.hasNext(); )
		{
			Rule rule = it.next();
			if(rule.getAction()==null || rule.getCondition()==null 
				|| rule.getEvents()==null || rule.getEvents().size()==0)
			{
				throw new RuntimeException("Rule is incomplete: "+rule.getName());
			}
			rulebase.addRule(rule);
		}
		
		ProxyFactory pf = new ProxyFactory(object);
		pf.addAdvice(new MethodInterceptor()
		{
			public Object invoke(MethodInvocation mi) throws Throwable
			{
				Object ret = mi.getMethod().invoke(mi.getThis(), mi.getArguments());
				IResultCommand creator = (IResultCommand)eventcreators.get(mi.getMethod());
				if(creator!=null)
				{
					Event event = (Event)creator.execute(null);
					addEvent(event);
//					System.out.println("created event: "+event);
				}
				return ret;
		    }
		});
		
		Object proxy = pf.getProxy();

		this.rules.put(object, new Tuple2(proxy, rules.values().toArray(new IRule[rules.size()])));

		// Recusrively call observe object on all direct monitored fields.
		clazz = object.getClass();
		Field[] fields = clazz.getDeclaredFields();
		for(int i=0; i<fields.length; i++)
		{
			if(fields[i].isAnnotationPresent(RuleObject.class))
			{
				fields[i].setAccessible(true);
				try
				{
					Object subobject = fields[i].get(object);
					observeObject(subobject);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		
		return proxy;
	}

//	/**
//	 * 
//	 */
//	protected void test(Method method)
//	{
//		if(method.isAnnotationPresent(jadex.rules.eca.annotations.Event.class))
//		{
//			jadex.rules.eca.annotations.Event event = method.getAnnotation(jadex.rules.eca.annotations.Event.class);
//			final String type = event.value();
//			FetchFieldCommand com = new FetchFieldCommand(object, type);
//			eventcreators.put(methods[i], com);
//		}
//		else if(methods[i].isAnnotationPresent(Condition.class))
//		{
//			Condition cond = methods[i].getAnnotation(Condition.class);
//			final String name = cond.value();
//			final Method m = methods[i];
//
//			Rule rule = rules.get(name);
//			if(rule==null)
//			{
//				rule = new Rule(name);
//				rules.put(name, rule);
//			}
//			
//			// Find event types
//			Annotation[][] paramannos = m.getParameterAnnotations();
//			List<String> events = new ArrayList<String>();
//			for(int j=0; j<paramannos.length; j++)
//			{
//				Annotation[] annos = paramannos[j];
//				for(int k=0; k<annos.length; k++)
//				{
//					if(annos[k] instanceof jadex.rules.eca.annotations.Event)
//					{
//						String type = ((jadex.rules.eca.annotations.Event)annos[k]).value();
//						events.add(type);
//					}
//				}
//			}
//			if(events.size()==0)
//				throw new RuntimeException("Event type not found: "+methods[i]);
//			
//			rule.setEvents(events);
//			
//			rule.setCondition(new jadex.rules.eca.Condition(object, m));
//		}
//		else if(methods[i].isAnnotationPresent(Action.class))
//		{
//			Action cond = methods[i].getAnnotation(Action.class);
//			final String name = cond.value();
//			final Method m = methods[i];
//			
//			Rule rule = rules.get(name);
//			if(rule==null)
//			{
//				rule = new Rule(name);
//				rules.put(name, rule);
//			}
//			
//			rule.setAction(new jadex.rules.eca.Action(object, m));
//		}
//	}
//	clazz = clazz.getSuperclass();
//}
//
//for(Iterator<Rule> it=rules.values().iterator(); it.hasNext(); )
//{
//	Rule rule = it.next();
//	if(rule.getAction()==null || rule.getCondition()==null 
//		|| rule.getEvents()==null || rule.getEvents().size()==0)
//	{
//		throw new RuntimeException("Rule is incomplete: "+rule.getName());
//	}
//	rulebase.addRule(rule);
//}
//
//ProxyFactory pf = new ProxyFactory(object);
//pf.addAdvice(new MethodInterceptor()
//{
//	public Object invoke(MethodInvocation mi) throws Throwable
//	{
//		Object ret = mi.getMethod().invoke(mi.getThis(), mi.getArguments());
//		IResultCommand creator = (IResultCommand)eventcreators.get(mi.getMethod());
//		if(creator!=null)
//		{
//			Event event = (Event)creator.execute(null);
//			addEvent(event);
////			System.out.println("created event: "+event);
//		}
//		return ret;
//    }
//});
//
//Object proxy = pf.getProxy();
//
//this.rules.put(object, new Tuple2(proxy, rules.values().toArray(new IRule[rules.size()])));
//
//	}
	
	/**
	 *  Unobserve an object.
	 */
	public void unobserveObject(final Object object)
	{
//		removePropertyChangeListener(object);
		Tuple2<Object, IRule[]> tup = rules.remove(object);
		if(tup!=null)
		{
			IRule[] rls = tup.getSecondEntity();
			for(int i=0; i<rls.length; i++)
			{
				rulebase.removeRule(rls[i]);
			}
		}
		
		// Recusrively call unobserve object on all direct monitored fields.
		Class clazz = object.getClass();
		Field[] fields = clazz.getDeclaredFields();
		for(int i=0; i<fields.length; i++)
		{
			if(fields[i].isAnnotationPresent(RuleObject.class))
			{
				fields[i].setAccessible(true);
				try
				{
					Object subobject = fields[i].get(object);
					unobserveObject(subobject);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	/**  
	 *  Add a property change listener.
	 */
	protected void	addPropertyChangeListener(Object object)
	{
		if(object!=null)
		{
			// Invoke addPropertyChangeListener on value
			try
			{
				if(pcls==null)
					pcls = new IdentityHashMap<Object, PropertyChangeListener>(); // values may change, therefore identity hash map
				PropertyChangeListener pcl = (PropertyChangeListener)pcls.get(object);
				
				if(pcl==null)
				{
					pcl = new PropertyChangeListener()
					{
						public void propertyChange(PropertyChangeEvent evt)
						{
							// todo: problems:
							// - may be called on wrong thread (-> synchronizator)
							// - how to create correct event with type and value
							
							Event event = new Event(evt.getPropertyName(), evt.getNewValue());
							addEvent(event);
						}
					};
				}
				
				// Do not use Class.getMethod (slow).
				Method	meth = SReflect.getMethod(object.getClass(), "addPropertyChangeListener", PCL);
				if(meth!=null)
					meth.invoke(object, new Object[]{pcl});				
	
				pcls.put(object, pcl);
			}
			catch(IllegalAccessException e){e.printStackTrace();}
			catch(InvocationTargetException e){e.printStackTrace();}
		}
	}
	
	/**
	 *  Deregister a value for observation.
	 *  if its a bean then remove the property listener.
	 */
	protected void	removePropertyChangeListener(Object object)
	{
		if(object!=null)
		{
//			System.out.println("deregister ("+cnt[0]+"): "+value);
			// Stop listening for bean events.
			if(pcls!=null)
			{
				PropertyChangeListener pcl = (PropertyChangeListener)pcls.remove(object);
				if(pcl!=null)
				{
					try
					{
//						System.out.println(getTypeModel().getName()+": Deregister: "+value+", "+type);						
						// Do not use Class.getMethod (slow).
						Method	meth = SReflect.getMethod(object.getClass(), "removePropertyChangeListener", PCL);
						if(meth!=null)
							meth.invoke(object, new Object[]{pcl});
					}
					catch(IllegalAccessException e){e.printStackTrace();}
					catch(InvocationTargetException e){e.printStackTrace();}
				}
			}
		}
	}
	
	/**
	 *  Process the next event by
	 *  - finding rules that are sensible to the event type
	 *  - evaluate the conditions of these conditions
	 *  - fire actions of triggered rules.
	 */
	public void processEvent()
	{
		if(events.size()>0)
		{
			IEvent event = events.remove(0);
			List<IRule> rules = rulebase.getRules(event.getType());
			for(int i=0; i<rules.size(); i++)
			{
				IRule rule = rules.get(i);
				if(rule.getCondition().evaluate(event))
				{
					rule.getAction().execute(event);
				}
			}
		}
	}
	
	/**
	 *  Process events until the event queue is empty.
	 */
	public void processAllEvents()
	{
		while(events.size()>0)
		{
			processEvent();
		}
	}
	
	/**
	 * 
	 */
	public void addEvent(IEvent event)
	{
		events.add(event);
	}
	
}

class FetchFieldCommand implements IResultCommand
{
	protected Object object;
	
	protected String name;
	
	public FetchFieldCommand(Object object, String name)
	{
		this.object = object;
		this.name = name;
	}

	public Object execute(Object args)
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
