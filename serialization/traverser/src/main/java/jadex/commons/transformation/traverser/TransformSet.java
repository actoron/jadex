package jadex.commons.transformation.traverser;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import jadex.commons.ChangeEvent;
import jadex.commons.IChangeListener;
import jadex.commons.SUtil;
import jadex.commons.collection.wrappers.SetWrapper;

/**
 *  A set that transforms itself, i.e. makes a copy of itself.
 *  This ensures that the serializer has no concurrent access to the base object.
 */
//public class TransformSet<E> extends HashSet<E> implements ITransformableObject
public class TransformSet<E> extends SetWrapper<E> implements ITransformableObject
{
	public static final String ADDED = "added";
	public static final String REMOVED = "removed";
	
	/** The listeners. */
	protected List<IChangeListener<E>> listeners;
	
//	final Collection<E> c; 
    protected final Object mutex;    

    /**
     *  Create a new TransformSet.
     */
    public TransformSet() 
    {
    	super(new HashSet<E>());
//        this.c = new HashSet<E>();
        mutex = this;
    }
    
//    public TransformSet(Collection<E> c) 
//    {
////        this.c = Objects.requireNonNull(c);
//        mutex = this;
//    }

    public int size() 
    {
        synchronized(mutex) {return super.size();}
    }
    
    public boolean isEmpty() 
    {
        synchronized(mutex) {return super.isEmpty();}
    }
    
    public boolean contains(Object o) 
    {
        synchronized(mutex) {return super.contains(o);}
    }
    
    public Object[] toArray() 
    {
        synchronized(mutex) {return super.toArray();}
    }
    
    public <T> T[] toArray(T[] a) 
    {
        synchronized(mutex) {return super.toArray(a);}
    }

    public Iterator<E> iterator() 
    {
        return super.iterator(); // Must be manually synched by user!
    }

    public boolean add(E e) 
    {
        synchronized(mutex) {return super.add(e);}
    }
    
    public boolean remove(Object o) 
    {
        synchronized(mutex) {return super.remove(o);}
    }

    public boolean containsAll(Collection<?> coll) 
    {
        synchronized(mutex) {return super.containsAll(coll);}
    }
    
    public boolean addAll(Collection<? extends E> coll) 
    {
        synchronized(mutex) {return super.addAll(coll);}
    }
    
    public boolean removeAll(Collection<?> coll) 
    {
        synchronized(mutex) {return super.removeAll(coll);}
    }
    
    public boolean retainAll(Collection<?> coll) 
    {
        synchronized(mutex) {return super.retainAll(coll);}
    }
    
    public void clear() 
    {
        synchronized(mutex) {super.clear();}
    }
    
    public String toString() 
    {
        synchronized(mutex) {return super.toString();}
    }
    
    // todo: these methods need to be synchronized 
    
//    // Override default methods in Collection
//    @Override
//	  public void forEach(Consumer<? super E> consumer) 
//	  {
//		  throw new UnsupportedOperationException();
//		  synchronized (mutex) {c.forEach(consumer);}
//	  }
//    
//    @Override
//	  public boolean removeIf(Predicate<? super E> filter) 
//	  {
//	  	throw new UnsupportedOperationException();
//        synchronized (mutex) {return c.removeIf(filter);}
//	  }
    
    
//    @Override
//    public Spliterator<E> spliterator() 
//    {
//        return c.spliterator(); // Must be manually synched by user!
//    }
//    
//    @Override
//    public Stream<E> stream() 
//    {
//        return c.stream(); // Must be manually synched by user!
//    }
//    
//    @Override
//    public Stream<E> parallelStream() 
//    {
//        return c.parallelStream(); // Must be manually synched by user!
//    }
    
    public boolean equals(Object o) 
    {
        if (this == o)
            return true;
        synchronized (mutex) {return super.equals(o);}
    }
    
    public int hashCode() 
    {
        synchronized (mutex) {return super.hashCode();}
    }
    
    private void writeObject(ObjectOutputStream s) throws IOException 
    {
        synchronized (mutex) {s.defaultWriteObject();}
    }
    
    /**
	 *  Return a transformed object.
	 *  @return A transformed version of the object.
	 */
	public Object transform()
	{
		 synchronized (mutex) 
		 {
			 Object[] copy = toArray(new Object[0]);
			 return SUtil.createHashSet(copy);
		 }
	}
	
	/**
	 *  An entry was added to the collection.
	 */
	protected void entryAdded(E value, int index)
	{
		ChangeEvent<E> event = new ChangeEvent<E>(null, ADDED, value);
		notifyListeners(event);
	}
	
	/**
	 *  An entry was removed from the collection.
	 */
	protected void entryRemoved(E value, int index)
	{
		ChangeEvent<E> event = new ChangeEvent<E>(null, REMOVED, value);
		notifyListeners(event);
	}
	
	/**
	 *  An entry was changed in the collection.
	 */
	protected void entryChanged(E oldvalue, E newvalue, int index)
	{
		// not used in set
	}
	
	/**
	 *  Notify listeners of a change event.
	 *  @param event The event.
	 */
	protected void notifyListeners(ChangeEvent<E> event)
	{
		IChangeListener<E>[] liss;
		synchronized(mutex) 
		{
			liss = listeners.toArray(new IChangeListener[listeners.size()]);
		}

		// Do not call listener with lock held
		for(IChangeListener<E> lis: liss)
		{
			lis.changeOccurred(event);
		}
	}
	
	/** 
	 *  Add a change listener.
	 *  @param lis The listener.
	 */
	public void addChangeListener(IChangeListener<E> lis)
	{
		synchronized(mutex) 
		{
			if(listeners==null)
				listeners = new ArrayList<IChangeListener<E>>();
			listeners.add(lis);
		}
	}
	
	/** 
	 *  Add a change listener.
	 *  @param lis The listener.
	 */
	public void removeChangeListener(IChangeListener<E> lis)
	{
		synchronized(mutex) 
		{
			if(listeners!=null)
				listeners.remove(lis);
		}
	}
}
