package jadex.rules.eca;

import jadex.commons.ICommand;
import jadex.commons.IResultCommand;
import jadex.rules.eca.annotations.Action;
import jadex.rules.eca.annotations.Condition;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
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
	
	/** The state. */
//	protected Object state;
	
	/** The rule base containing all the rules. */
//	protected IRulebase rulebase;
	
	/** The conditions by event type. */
	protected Map<String, ICommand> conditions;
	
	/** The actions by event type. */
	protected Map<String, ICommand> actions;

	
	//-------- constructors --------
	
	/**
	 *  Create a new rule system.
	 */
	public RuleSystem()
	{
		this.conditions = new HashMap<String, ICommand>();
		this.actions = new HashMap<String, ICommand>();
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
					
					// find event type
					Annotation[][] paramannos = m.getParameterAnnotations();
					String type = null;
					for(int j=0; j<paramannos.length; j++)
					{
						Annotation[] annos = paramannos[j];
						for(int k=0; k<annos.length; k++)
						{
							if(annos[k] instanceof jadex.rules.eca.annotations.Event)
							{
								type = ((jadex.rules.eca.annotations.Event)annos[k]).value();
							}
						}
					}
					if(type==null)
						throw new RuntimeException("Event type not found: "+methods[i]);
					
					ICommand com = new ICommand()
					{
						public void execute(Object event)
						{
							try
							{
								m.setAccessible(true);
								Object result = m.invoke(object, ((Event)event).getContent());
								if(result instanceof Boolean && ((Boolean)result).booleanValue())
								{
									// fire action
									ICommand action = (ICommand)actions.get(name);
									if(action!=null)
									{
										// todo: args
										action.execute(null); 
									}
									else
									{
										System.out.println("No action found: "+name);
									}
								}
							}
							catch(Exception e)
							{
								throw new RuntimeException(e);
							}
						}
					};
//					conditionevaluators.put(name, com);
					conditions.put(type, com);
				}
				else if(methods[i].isAnnotationPresent(Action.class))
				{
					Action cond = methods[i].getAnnotation(Action.class);
					final String name = cond.value();
					final Method m = methods[i];
					ICommand com = new ICommand()
					{
						public void execute(Object args)
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
					};
					actions.put(name, com);
				}
			}
			clazz = clazz.getSuperclass();
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
					ICommand com = (ICommand)conditions.get(event.getType());
					if(com!=null)
						com.execute(event);
//					System.out.println("created event: "+event);
				}
				return ret;
		    }
		});
		
		Object proxy = pf.getProxy();
		return proxy;
	}
	
//	/**
//	 *  Get the memory.
//	 *  @return The memory.
//	 */
//	public Object getState()
//	{
//		return state;
//	}
//
//	/**
//	 *  Get the rulebase.
//	 *  @return The rulebase.
//	 */
//	public IRulebase getRulebase()
//	{
//		return rulebase;
//	}

}
