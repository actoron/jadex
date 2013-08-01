package jadex.bdiv3.runtime.wrappers;

import jadex.bdiv3.runtime.impl.BDIAgentInterpreter;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.commons.IResultCommand;
import jadex.commons.Tuple2;
import jadex.commons.Tuple3;
import jadex.commons.beans.PropertyChangeEvent;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.rules.eca.Event;
import jadex.rules.eca.IEvent;
import jadex.rules.eca.RuleSystem;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * 
 */
public class MapWrapper<T, E> implements Map<T, E>
{
	/** The delegate map. */
	protected Map<T, E> delegate;
	
	/** The interpreter. */
	protected BDIAgentInterpreter interpreter;
	
	/** The add event name. */
	protected String addevent;
	
	/** The remove event name. */
	protected String remevent;
	
	/** The change event name. */
	protected String changeevent;

	/**
	 *  Create a new collection wrapper.
	 */
	public MapWrapper(Map<T, E> delegate, BDIAgentInterpreter interpreter, 
		String addevent, String remevent, String changeevent)
	{
		this.delegate = delegate;
		this.interpreter = interpreter;
		this.addevent = addevent;
		this.remevent = remevent;
		this.changeevent = changeevent;
	}
	
	/** 
	 * 
	 */
	public int size()
	{
		return delegate.size();
	}

	/** 
	 * 
	 */
	public boolean isEmpty()
	{
		return delegate.isEmpty();
	}

	/** 
	 * 
	 */
	public boolean containsKey(Object key)
	{
		return delegate.containsKey(key);
	}

	/** 
	 * 
	 */
	public boolean containsValue(Object value)
	{
		return delegate.containsValue(value);
	}

	/** 
	 * 
	 */
	public E get(Object key)
	{
		return delegate.get(key);
	}

	/** 
	 * 
	 */
	public E put(T key, E value)
	{
		E ret = delegate.put(key, value);
		unobserveValue(ret);
		observeValue(value);
		if(ret==null)
		{
			getRuleSystem().addEvent(new Event(addevent, new Tuple3<T, E, E>(key, value, ret)));
		}
		else
		{
			getRuleSystem().addEvent(new Event(changeevent, new Tuple3<T, E, E>(key, value, ret)));
		}
		return ret;
	}

	/** 
	 * 
	 */
	public E remove(Object key)
	{
		E ret = delegate.remove(key);
		unobserveValue(ret);
		getRuleSystem().addEvent(new Event(remevent, new Tuple2<T, E>((T)key, ret)));
		return ret;
	}

	/** 
	 * 
	 */
	public void putAll(Map<? extends T, ? extends E> m)
	{
		for(Map.Entry<? extends T, ? extends E> e : m.entrySet())
		{
			observeValue(e.getValue());
            put(e.getKey(), e.getValue());
		}
	}

	/** 
	 * 
	 */
	public void clear()
	{
		Set<java.util.Map.Entry<T, E>> s = entrySet();
		delegate.clear();
		for(Map.Entry<? extends T, ? extends E> e : s)
		{
			unobserveValue(e.getValue());
			getRuleSystem().addEvent(new Event(remevent, new Tuple2<T, E>(e.getKey(), e.getValue())));
		}
	}

	/** 
	 * 
	 */
	public Set<T> keySet()
	{
		return delegate.keySet();
	}

	/** 
	 * 
	 */
	public Collection<E> values()
	{
		return delegate.values();
	}

	/** 
	 * 
	 */
	public Set<java.util.Map.Entry<T, E>> entrySet()
	{
		return delegate.entrySet();
	}

	/** 
	 *  Get the hashcode of the object.
	 *  @return The hashcode.
	 */
	public int hashCode()
	{
		return delegate.hashCode();
	}

	/** 
	 *  Test if this object equals another.
	 *  @param obj The other object.
	 *  @return True, if equal.
	 */
	public boolean equals(Object obj)
	{
		boolean ret = false;
		if(obj instanceof MapWrapper)
		{
			ret = delegate.equals(((MapWrapper)obj).delegate);
		}
		else if(obj instanceof Map)
		{
			ret = delegate.equals(obj);
		}
		return ret;
	}
	
	/**
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return delegate.toString();
	}
	
	/**
	 *  Get the interpreter.
	 *  @return The interpreter.
	 */
	public BDIAgentInterpreter getInterpreter()
	{
		return interpreter;
	}
	
	/**
	 *  Get the rule system.
	 *  @return The rule system.
	 */
	public RuleSystem getRuleSystem()
	{
		return interpreter.getRuleSystem();
	}

	/**
	 * 
	 */
	public void observeValue(Object val)
	{
		if(val!=null)
		{
			getRuleSystem().observeObject(val, true, false, new IResultCommand<IFuture<IEvent>, PropertyChangeEvent>()
			{
				public IFuture<IEvent> execute(final PropertyChangeEvent event)
				{
					return getInterpreter().scheduleStep(new IComponentStep<IEvent>()
					{
						public IFuture<IEvent> execute(IInternalAccess ia)
						{
							Event ev = new Event(changeevent, event.getNewValue());
							return new Future<IEvent>(ev);
						}
					});
				}
			});
		}
	}
	
	/**
	 * 
	 */
	public void unobserveValue(Object val)
	{
		getRuleSystem().unobserveObject(val);
	}
}
