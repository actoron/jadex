package jadex.commons.collection.wrappers;

import java.util.Collection;
import java.util.Iterator;

/**
 *  Wrapper for collections. Creates rule events on add/remove/change operation calls.
 */
public abstract class CollectionWrapper <T> implements Collection<T>
{
	/** The delegate list. */
	protected Collection<T> delegate;

	/**
	 *  Create a new wrapper.
	 *  @param delegate The delegate.
	 */
	public CollectionWrapper(Collection<T> delegate)
	{
		this.delegate = delegate;
	}
	
	/**
	 *  Get the size.
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
	public boolean contains(Object o)
	{
		return delegate.contains(o);
	}

	/**
	 *  
	 */
	public Iterator<T> iterator()
	{
		return delegate.iterator();
	}

	/**
	 *  
	 */
	public Object[] toArray()
	{
		return delegate.toArray();
	}

	/**
	 *  
	 */
	public <T> T[] toArray(T[] a)
	{
		return delegate.toArray(a);
	}

	/**
	 *  
	 */
	public boolean add(T e)
	{
		boolean ret = delegate.add(e);
		if(ret)
		{
			entryAdded(e, -1);
//			observeValue(e);
//			getRuleSystem().addEvent(new Event(addevent, new ChangeInfo<T>(e, null, delegate.size())));
//			getRuleSystem().addEvent(new Event(addevent, new CollectionEntry<T>(e, null, delegate.size())));
//			publishToolBeliefEvent();
		}
		return ret;
	}

	/**
	 *  
	 */
	public boolean remove(Object o)
	{
		boolean ret = delegate.remove(o);
		if(ret)
		{
			entryRemoved((T)o, -1);
//			unobserveValue(o);
//			getRuleSystem().addEvent(new Event(remevent, new ChangeInfo<T>(null, (T)o, null)));
//			publishToolBeliefEvent();
		}
		return ret;
	}

	/**
	 *  
	 */
	public boolean containsAll(Collection<?> c)
	{
		return delegate.containsAll(c);
	}

	/**
	 *  
	 */
	public boolean addAll(Collection<? extends T> c)
	{
		boolean ret = delegate.addAll(c);
		if(ret)
		{
			for(T t: c)
			{
				entryAdded(t, -1);
//				observeValue(t);
//				getRuleSystem().addEvent(new Event(addevent, new ChangeInfo<T>(t, null, null)));
//				publishToolBeliefEvent();
			}
		}	
		return ret;
	}

	/**
	 *  
	 */
	public boolean removeAll(Collection<?> c)
	{
		boolean ret = delegate.removeAll(c);
		if(ret)
		{
			for(Object t: c)
			{
				entryRemoved((T)t, -1);
//				unobserveValue(t);
//				getRuleSystem().addEvent(new Event(remevent, new ChangeInfo<T>((T)t, null, null)));
//				publishToolBeliefEvent();
			}
		}	
		return ret;
	}

	/**
	 *  
	 */
	public boolean retainAll(Collection< ? > c)
	{
		// todo:
		return delegate.retainAll(c);
	}

	/**
	 *  
	 */
	public void clear()
	{
		T[] clone = delegate.toArray((T[])new Object[delegate.size()]);
		delegate.clear();
		for(Object t: clone)
		{
			entryRemoved((T)t, -1);
//			unobserveValue(t);
//			getRuleSystem().addEvent(new Event(addevent, new ChangeInfo<Object>(null, t, null)));
//			publishToolBeliefEvent();
		}
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
		if(obj instanceof CollectionWrapper)
		{
			ret = delegate.equals(((CollectionWrapper)obj).delegate);
		}
		else if(obj instanceof Collection)
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
	
//	/**
//	 *  Get the interpreter.
//	 *  @return The interpreter.
//	 */
//	public BDIAgentInterpreter getInterpreter()
//	{
//		return interpreter;
//	}
//	
//	/**
//	 *  Get the rule system.
//	 *  @return The rule system.
//	 */
//	public RuleSystem getRuleSystem()
//	{
//		return interpreter.getRuleSystem();
//	}
	
//	/**
//	 * 
//	 */
//	public void observeValue(final Object val)
//	{
//		if(val!=null)
//		{
//			getRuleSystem().observeObject(val, true, false, new IResultCommand<IFuture<Void>, PropertyChangeEvent>()
//			{
//				public IFuture<Void> execute(final PropertyChangeEvent event)
//				{
//					final Future<Void> ret = new Future<Void>();
//					try
//					{
//						IFuture<Void> fut = getInterpreter().scheduleStep(new IComponentStep<Void>()
//						{
//							public IFuture<Void> execute(IInternalAccess ia)
//							{
//								publishToolBeliefEvent();
//								Event ev = new Event(changeevent, new ChangeInfo<Object>(event.getNewValue(), event.getOldValue(), null));
//								getRuleSystem().addEvent(ev);
//								return IFuture.DONE;
////								return new Future<IEvent>(ev);
//							}
//						});
//						fut.addResultListener(new DelegationResultListener<Void>(ret)
//						{
//							public void exceptionOccurred(Exception exception)
//							{
//								if(exception instanceof ComponentTerminatedException)
//								{
////									System.out.println("Ex in observe: "+exception.getMessage());
//									getRuleSystem().unobserveObject(val);
//									ret.setResult(null);
//								}
//								else
//								{
//									super.exceptionOccurred(exception);
//								}
//							}
//						});
//					}
//					catch(Exception e)
//					{
//						if(!(e instanceof ComponentTerminatedException))
//							System.out.println("Ex in observe: "+e.getMessage());
//						getRuleSystem().unobserveObject(val);
//						ret.setResult(null);
//					}
//					return ret;
//				}
//			});
//		}
//	}
	
//	/**
//	 * 
//	 */
//	public void unobserveValue(Object val)
//	{
//		getRuleSystem().unobserveObject(val);
//	}
//	
//	/**
//	 * 
//	 */
//	public void publishToolBeliefEvent()//String evtype)
//	{
//		((BDIAgent)getInterpreter().getAgent()).publishToolBeliefEvent(getInterpreter(), mbel);//, evtype);
//	}

	/**
	 *  An entry was added to the collection.
	 */
	protected abstract void	entryAdded(T value, int index);
	
	/**
	 *  An entry was removed from the collection.
	 */
	protected abstract void	entryRemoved(T value, int index);
	
	/**
	 *  An entry was changed in the collection.
	 */
	protected abstract void	entryChanged(T oldvalue, T newvalue, int index);
}
