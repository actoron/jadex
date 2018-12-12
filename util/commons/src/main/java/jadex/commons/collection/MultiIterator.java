package jadex.commons.collection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 *  Iterator that operates over multiple collections.
 */
public class MultiIterator<T> implements Iterator<T>
{
	/** The data collections. */
	protected List<Iterator<T>> its;

	/** The counter for the collection. */
	protected int collcnt;
	
	/** The current iterator. */
	protected Iterator<T> curit;
	
	/**
	 *  Create a new MultiCollectionIterator.
	 */
	public MultiIterator()
	{
	}
	
	/**
	 *  Add a collection.
	 *  @param coll The collection.
	 */
	public void addIterator(Iterator<T> it)
	{
		if(its==null)
			its = new ArrayList<Iterator<T>>();
		its.add(it);
	}
	
	/**
     * Returns {@code true} if the iteration has more elements.
     * (In other words, returns {@code true} if {@link #next} would
     * return an element rather than throwing an exception.)
     *
     * @return {@code true} if the iteration has more elements
     */
    public boolean hasNext()
    {
    	return getCurrentIterator()==null? false: getCurrentIterator().hasNext();
    }

    /**
     * Returns the next element in the iteration.
     *
     * @return the next element in the iteration
     * @throws NoSuchElementException if the iteration has no more elements
     */
    public T next()
    {
    	return getCurrentIterator().next();
    }

    /**
     * Removes from the underlying collection the last element returned
     * by this iterator (optional operation).  This method can be called
     * only once per call to {@link #next}.  The behavior of an iterator
     * is unspecified if the underlying collection is modified while the
     * iteration is in progress in any way other than by calling this
     * method.
     *
     * @throws UnsupportedOperationException if the {@code remove}
     *         operation is not supported by this iterator
     *
     * @throws IllegalStateException if the {@code next} method has not
     *         yet been called, or the {@code remove} method has already
     *         been called after the last call to the {@code next}
     *         method
     */
    public void remove()
    {
    	throw new UnsupportedOperationException();
    }
    
    /**
     *  Get the current iterator.
     */
    protected Iterator<T> getCurrentIterator()
    {
    	if(curit==null && its!=null)
    	{
    		curit = its.get(collcnt);
    	}
    	else if(curit!=null && its!=null && !curit.hasNext() && collcnt+1<its.size())
    	{
    		curit = its.get(++collcnt);
    	}
    	return curit;
    }
}
