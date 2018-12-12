package jadex.rules.state.javaimpl;

/**
 *  Container for queue attribute values.
 */
class RingBufferQueue
{
	//-------- constants --------
	
	/** The default inital capacity of the agenda buffer. */
	protected static final int	DEFAULT_INITIAL_CAPACITY	= 16;  
	
	//-------- attributes --------
	
	/** 
	 *  The list of values.
	 *  For efficiency we use  a custom non-blocking, non-synchronized queue.
	 *  The implementation is based on an array used as buffer, an integer for
	 *  the current size, and a cursor indicating the current start of the
	 *  queue. When the cursor moves beyond the end of the array it starts
	 *  again from the beginning. When the size gets larger than the buffer,
	 *  the array will be resized. Shrinking of the array has not been
	 *  implemented, and is probably not needed(?).
	 */
	protected Object[]	values;
	
	/** The size of the queue. */
	protected int	size;
	
	/** The pointer to the currently first value. */
	protected int	current;
	
	//-------- constructors --------
	
	/**
	 *  Create a new ring buffer queue.
	 */
	public RingBufferQueue()
	{
		this.values	= new Object[DEFAULT_INITIAL_CAPACITY];
		this.size	= 0;
		this.current	= 0;
	}
	
	//-------- methods --------
	
	/**
	 *  Remove the first value from the queue, if any.
	 *  @return The first value from the queue, or null if the queue is empty.
	 */
	public Object	removeFirstValue()
	{
		Object	ret	= null;
		// Check if there is a current value.
		if(size>0)
		{
			// Save the return value.
			ret	= values[current];
		
			// Move to next element in buffer.
			values[current]	= null;	// To allow garbage collection.
			current	= (current+1)%values.length;
			size--;
		}
		
		return ret;
	}

	/**
	 *  Add a value to the queue.
	 *  @param value	The value to add.
	 */
	public void	addValue(Object value)
	{
		// Resize buffer, if necessary.
		if(size==values.length)
		{
			// Create new buffer with doubled size.
			Object[]	newvalues	= new Object[values.length*2];
			
			// Copy into new buffer the elements from cursor to the end of the old buffer.
			System.arraycopy(values, current, newvalues, 0, values.length-current);
			
			// When there ere elements to the left of the cursor... 
			if(current>0)
			{
				// Copy remaining elements to new buffer.
				System.arraycopy(values, 0, newvalues, values.length-current, current);
			}
			
			// Switch to new buffer, which starts at index 0.
			values	= newvalues;
			current	= 0;
			
//			System.out.println("New buffersize: "+values.length);
		}
		
		// Add entry to end of the buffer, and increment size.		
		this.values[(current+(size++))%values.length]	= value;
	}

	/**
	 *  Test if the queue is empty.
	 *  @return True, if the queue is empty.
	 */
	public boolean	isEmpty()
	{
		return size==0;
	}
}
