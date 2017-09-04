package jadex.commons.transformation.traverser;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

import jadex.commons.SUtil;

/**
 *  A set that transforms itself, i.e. makes a copy of itself.
 *  This ensures that the serializer has no concurrent access to the base object.
 */
public class TransformSet<E> extends HashSet<E> implements ITransformableObject
{
//	final Collection<E> c; 
    final Object mutex;    

    public TransformSet() 
    {
//        this.c = new HashSet<E>();
        mutex = this;
    }
    
    public TransformSet(Collection<E> c) 
    {
//        this.c = Objects.requireNonNull(c);
        mutex = this;
    }

    public int size() 
    {
        synchronized (mutex) {return super.size();}
    }
    
    public boolean isEmpty() 
    {
        synchronized (mutex) {return super.isEmpty();}
    }
    
    public boolean contains(Object o) 
    {
        synchronized (mutex) {return super.contains(o);}
    }
    
    public Object[] toArray() 
    {
        synchronized (mutex) {return super.toArray();}
    }
    
    public <T> T[] toArray(T[] a) 
    {
        synchronized (mutex) {return super.toArray(a);}
    }

    public Iterator<E> iterator() 
    {
        return super.iterator(); // Must be manually synched by user!
    }

    public boolean add(E e) 
    {
        synchronized (mutex) {return super.add(e);}
    }
    
    public boolean remove(Object o) 
    {
        synchronized (mutex) {return super.remove(o);}
    }

    public boolean containsAll(Collection<?> coll) 
    {
        synchronized (mutex) {return super.containsAll(coll);}
    }
    
    public boolean addAll(Collection<? extends E> coll) 
    {
        synchronized (mutex) {return super.addAll(coll);}
    }
    
    public boolean removeAll(Collection<?> coll) 
    {
        synchronized (mutex) {return super.removeAll(coll);}
    }
    
    public boolean retainAll(Collection<?> coll) 
    {
        synchronized (mutex) {return super.retainAll(coll);}
    }
    
    public void clear() 
    {
        synchronized (mutex) {super.clear();}
    }
    
    public String toString() 
    {
        synchronized (mutex) {return super.toString();}
    }
    
    // todo: these methods need to be synchronized 
    
//    // Override default methods in Collection
//    @Override
	  public void forEach(Consumer<? super E> consumer) 
	  {
		  throw new UnsupportedOperationException();
//		  synchronized (mutex) {c.forEach(consumer);}
	  }
//    
//    @Override
	  public boolean removeIf(Predicate<? super E> filter) 
	  {
	  	throw new UnsupportedOperationException();
//        synchronized (mutex) {return c.removeIf(filter);}
	  }
    
    
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
}
