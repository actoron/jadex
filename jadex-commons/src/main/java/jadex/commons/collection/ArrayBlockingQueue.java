package jadex.commons.collection;

import java.util.Arrays;
import java.util.List;

import jadex.commons.SUtil;
import jadex.commons.concurrent.TimeoutException;



/**
 *  Blocking queue implemented as array.
 *  The array is expanded automatically when the queue grows.
 */
public class ArrayBlockingQueue<T>	implements IBlockingQueue<T>
{
	//-------- attributes --------
	
	/** The elements in the queue. */
	protected Object[]	elements;
	
	/** Pointer to the first element. */
	protected int	start;
	
	/** Insert position for the next element. */
	protected int	end;
	
	/** The size (cached for speed). */
	protected int	size;
	
	/** The monitor. */
	protected Object	monitor;

	/** The queue state. */
	protected boolean closed;
	
	//-------- constructors --------
	
	/**
	 *  Create a new blocking queue.
	 */
	public ArrayBlockingQueue()
	{
        elements = new Object[255];
        monitor	= new Object();
    }
    
	//-------- methods --------
	
	/**
	 *  Get the number of elements in the queue. 
	 */
    public int size()
    {
        return size;
    }
    
    /**
     *  Add an element to the end of the queue.
     */
    public void enqueue(Object o)
    {
		if(closed)
			throw new IBlockingQueue.ClosedException("Queue closed.");

		synchronized(monitor)
        {
        	// Expand array if necessary.
            if(size==elements.length)
            {
            	Object[]	newelements	= new Object[elements.length*2];
            	if(start<end)
            	{
            		System.arraycopy(elements, start, newelements, 0, size);
            	}
            	else
            	{
            		System.arraycopy(elements, start, newelements, 0, elements.length-start);
            		System.arraycopy(elements, 0, newelements, elements.length-start, end);
            	}
            	elements	= newelements;
        		start	= 0;
        		end	= size;
            }
            
            // Add element to end of queue.
            elements[end]=o;
            end	= (end+1)%elements.length;
            size++;
            monitor.notify();
        }
    }
    
	/**
	 *  Dequeue an element.
	 *  @param timeout	the time to wait (in millis) or -1 for no timeout.
	 *  @return The element. When queue is empty
	 *  the methods blocks until an element is added or the timeout occurs.
	 */
    public T dequeue(final long timeout) throws ClosedException, TimeoutException
    {
		if(closed)
			throw new IBlockingQueue.ClosedException("Queue closed.");

		synchronized(monitor)
        {
			// Uses a spin lock, because wait must not be implemented atomically.
			long to = timeout;
	        while(size==0 && (to>0 || to==-1))
	        {
	            try
				{
	            	if(to==-1)
	            	{
	            		monitor.wait();
	            	}
	            	else
	            	{
	            		// Hack!!! Java does not distinguish between notify() and timeout (grrr).
	            		long	starttime	= System.currentTimeMillis();
	            		monitor.wait(to);
	            		if(size==0)
	            		{
		            		to = Math.max(0, timeout + starttime - System.currentTimeMillis());
//		            		System.out.println("Remaining timeout: "+timeout);
	            		}
	            	}
				}
				catch(InterruptedException e){}
	
				if(closed)
					throw new IBlockingQueue.ClosedException("Queue closed.");
				if(size==0 && to==0)
					throw new TimeoutException("Timeout during dequeue().");
			}
            
            Object	ret	= elements[start];
            elements[start]	= null;	// Allow object to be garbage collected.
            start	= (start+1)%elements.length;
            size--;
            return (T)ret;
        }
    }
    
	/**
	 *  Peek the topmost element without dequeuing it.
	 *  @return The element. When queue is empty
	 *  the methods blocks until an element is added.
	 */
	public T peek()	throws ClosedException
	{
		if(closed)
			throw new IBlockingQueue.ClosedException("Queue closed.");

		T ret;
		synchronized(monitor)
        {
			ret = size>0? (T)elements[start]: null;
        }
		return ret;
	}
    
    /**
     *  Remove an object from the queue
     *  (blocks until an element is available).
     */
    public T dequeue()
    {
    	return dequeue(-1);
    }

	/**
	 *  Open/close the queue.
	 *  @param closed The closed state.
	 *  @return The remaining elements after the queue has been closed.
	 */
	public List<T>	setClosed(boolean closed)
	{
		Object[]	ret;
		if(!this.closed && closed)
		{
			synchronized(monitor)
			{
				this.closed = closed;
				monitor.notifyAll();
			}
			
			
			ret	= new Object[size];
			if(start<end)
        	{
        		System.arraycopy(elements, start, ret, 0, size);
        	}
        	else if(start>end)
        	{
        		System.arraycopy(elements, start, ret, 0, elements.length-start);
        		System.arraycopy(elements, 0, ret, elements.length-start, end);
        	}

		}
		else
		{
			ret	= new Object[0];
		}
		return (List<T>)Arrays.asList(ret);
	}
	
	/**
	 *  Check if the queue is closed.
	 */
	public boolean	isClosed()
	{
		return closed;
	}
	
	/**
	 *  Create a string representation.
	 */
	public String toString()
	{
		synchronized(monitor)
		{
			return SUtil.arrayToString(elements);
		}
	}
}
