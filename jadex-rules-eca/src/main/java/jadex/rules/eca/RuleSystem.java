package jadex.rules.eca;

import jadex.commons.ICommand;
import jadex.commons.IResultCommand;
import jadex.rules.eca.annotations.Action;
import jadex.rules.eca.annotations.Condition;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.ProxyFactory;

/**
 * 
 */
public class RuleSystem
{
	//-------- attributes --------
	
//	/** The conditions by event type. */
//	protected Map<String, ICommand> conditions;
//	
//	/** The actions by event type. */
//	protected Map<String, ICommand> actions;

	/** The event list. */
	protected List<IEvent> events;
	
	/** The rulebase. */
	protected IRulebase rulebase;
	
	//-------- constructors --------
	
	/**
	 *  Create a new rule system.
	 */
	public RuleSystem()
	{
		this.events = new ArrayList<IEvent>();
		this.rulebase = new Rulebase();
	}

	//-------- methods --------
	
	/**
	 *  Monitor an object to the rule engine.
	 *  - Extracts conditions
	 *  - Extracts actions
	 *  - Subscribes for events
	 */
	public Object monitorObject(final Object object)
	{
		Class clazz = object.getClass();

		final Map eventcreators = new HashMap();
		final Map<String, Rule> rules = new HashMap<String, Rule>();
//		final Map conditionevaluators = new HashMap();
//		final Map actions = new HashMap();
		
		while(!clazz.equals(Object.class))
		{
			Method[] methods = clazz.getDeclaredMethods();
			for(int i=0; i<methods.length; i++)
			{
				if(methods[i].isAnnotationPresent(jadex.rules.eca.annotations.Event.class))
				{
					jadex.rules.eca.annotations.Event event = methods[i].getAnnotation(jadex.rules.eca.annotations.Event.class);
					final String type = event.value();
					IResultCommand com = new IResultCommand()
					{
						public Object execute(Object args)
						{
							try
							{
								Field f = object.getClass().getDeclaredField(type);
								f.setAccessible(true);
								Object content = f.get(object);
								return new Event(type, content);
							}
							catch(Exception e)
							{
								throw new RuntimeException(e);
							}
						}
					};
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
					
					// find event type
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
					
					rule.setCondition(new ICondition()
					{
						public boolean evaluate(IEvent event)
						{
							boolean ret = false;
							try
							{
								m.setAccessible(true);
								Object result = m.invoke(object, ((Event)event).getContent());
								ret = ((Boolean)result).booleanValue();
							}
							catch(Exception e)
							{
								throw new RuntimeException(e);
							}
							return ret;
						}
					});
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
					
					rule.setAction(new IAction()
					{
						public void execute(IEvent event)
						{
							try
							{
								m.setAccessible(true);
								Object result = m.invoke(object, new Object[0]);
							}
							catch(Exception e)
							{
								throw new RuntimeException(e);
							}
						}
					});
				}
			}
			clazz = clazz.getSuperclass();
		}
		
		for(Iterator<Rule> it=rules.values().iterator(); it.hasNext(); )
		{
			rulebase.addRule(it.next());
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
					events.add(event);
//					System.out.println("created event: "+event);
				}
				return ret;
		    }
		});
		
		Object proxy = pf.getProxy();
		return proxy;
	}

	/**
	 * 
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
	
}
