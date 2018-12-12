package jadex.commons.collection;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import jadex.commons.TimeoutException;


/**
 *  A blocking queue allows to enqueue or dequeue
 *  elements. It blocks, when it is tried to dequeue
 *  an element, but the queue is empty.
 */
public class BlockingQueue<T> implements IBlockingQueue<T>
{
	//-------- attributes --------

	/** The element storage. */
	protected List<T>	elems;

	/** The queue state. */
	protected volatile boolean closed;

	/** The monitor. */
	protected Object	monitor;
	
	//-------- constructors --------

	/**
	 *  Create a blocking queue.
	 */
	public BlockingQueue()
	{
		this.elems	= new LinkedList<T>();
		this.monitor	= new Object();
	}

	//-------- methods --------

	/**
	 *  Enqueue an element.
	 *  @param element The element.
	 */
	public void enqueue(T element)
	{
		if(closed)
			throw new IBlockingQueue.ClosedException("Queue closed.");

		synchronized(monitor)
		{
			this.elems.add(element);
			monitor.notify();
		}
	}

	/**
	 *  Dequeue an element.
	 *  @param timeout	the time to wait (in millis) or -1 for no timeout.
	 *  @return The element. When queue is empty
	 *  the methods blocks until an element is added or the timeout occurs.
	 */
    public T dequeue(long timeout) throws ClosedException, TimeoutException
    {
		if(closed)
			throw new IBlockingQueue.ClosedException("Queue closed.");

		synchronized(monitor)
        {
			if(timeout!=-1)
			{
				if(elems.isEmpty())
				{
		            try
					{
						monitor.wait(timeout);
					}
					catch(InterruptedException e){}
		
					if(closed)
						throw new IBlockingQueue.ClosedException("Queue closed.");
					if(elems.isEmpty())
						throw new TimeoutException("Timeout during dequeue().");
				}
			}
			else
			{
				// Uses a spin lock, because wait must not be implemented atomically.
		        while(elems.isEmpty())
		        {
		            try
					{
						monitor.wait();
					}
					catch(InterruptedException e){}
		
					if(closed)
						throw new IBlockingQueue.ClosedException("Queue closed.");
		        }
			}

			return elems.remove(0);
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
			ret = !elems.isEmpty()? elems.get(0): null;
        }
		return ret;
	}

   	/**
	 *  Dequeue an element.
	 *  @return The element. When queue is empty
	 *  the methods blocks until an element is added.
	 */
	public T dequeue()	throws ClosedException
	{
		return dequeue(-1);
	}
	
	/**
	 *  Check if the queue is closed.
	 */
	public boolean	isClosed()
	{
		return closed;
	}

	/**
	 *  Open/close the queue.
	 *  @param closed The closed state.
	 *  @return The remaining elements after the queue has been closed.
	 */
	public List	setClosed(boolean closed)
	{
		List	ret;
		if(!this.closed)
		{
			synchronized(monitor)
			{
				this.closed = closed;
				monitor.notifyAll();
			}
			ret	= this.elems;
		}
		else
		{
			ret	= Collections.emptyList();
		}
		return ret;
	}

	/**
	 *  Return the size of the queue.
	 */
	public int	size()
	{
		synchronized(monitor)
		{
			return this.elems.size();
		}
	}

	//-------- static part --------
	
	/**
	 *  Main for testing.
	 * @throws InterruptedException 
	 */
	public static void	main(String[] args) throws InterruptedException
	{
		test1(args);
		
		Thread.sleep(2000);

		test1b(args);
		
		Thread.sleep(2000);
		
		test2(args);
	}

	/**
	 *  Main for testing.
	 * @throws InterruptedException 
	 */
	public static void	test1(String[] args) throws InterruptedException
	{
		final int	max	= 5000000;
		final int[]	stat	= new int[3];

		final IBlockingQueue	queue	= new BlockingQueue();
//		final java.util.concurrent.BlockingQueue	queue	= new LinkedBlockingQueue();
		new Thread(new Runnable()
		{
			public void run()
			{
				while(true)
				{
					try
					{
						/*Object	item	=*/ queue.dequeue();
//						Object	item	= queue.take();
//						System.out.println("Processing: "+item);
						stat[1]	= Math.max(queue.size(), stat[1]);
						if(queue.size()==0)
						{
							stat[0]++;
							if(queue.size()==0 && stat[2]!=0)
							{
								System.out.println("Queue is now empty.");
								break;
							}
						}
					}
					catch(Exception e)
					{
						StringWriter	sw	= new StringWriter();
						e.printStackTrace(new PrintWriter(sw));
						throw new RuntimeException(sw.toString());
					}
				}
			}
		}).start();

		for(int i=0; i<max; i++)
		{
//			System.out.println("Enqueing: Item "+i);
			queue.enqueue("Item");
//			queue.put("Item");
			stat[1]	= Math.max(queue.size(), stat[1]);
		}

		stat[2]	= 1;	// Flag to indicate end of test.
		
		System.out.println("Queue was empty "+stat[0]+" times.\n"
			+"Queue size is now "+queue.size()+".\n"
			+"Max queue size was "+stat[1]+".");
	}

	/**
	 *  Main for testing.
	 * @throws InterruptedException 
	 */
	public static void	test1b(String[] args) throws InterruptedException
	{
		final int	max	= 5000000;
		final int[]	stat	= new int[3];

		final ArrayBlockingQueue	queue	= new ArrayBlockingQueue();
//		final java.util.concurrent.BlockingQueue	queue	= new LinkedBlockingQueue();
		new Thread(new Runnable()
		{
			public void run()
			{
				while(true)
				{
					try
					{
						Object	item	= queue.dequeue();
//						Object	item	= queue.take();
						System.out.println("Processing: "+item);
						stat[1]	= Math.max(queue.size(), stat[1]);
						if(queue.size()==0)
						{
							stat[0]++;
							if(queue.size()==0 && stat[2]!=0)
							{
								System.out.println("Queue is now empty.");
								break;
							}
						}
					}
					catch(Exception e)
					{
						StringWriter	sw	= new StringWriter();
						e.printStackTrace(new PrintWriter(sw));
						throw new RuntimeException(sw.toString());
					}
				}
			}
		}).start();

		for(int i=0; i<max; i++)
		{
//			System.out.println("Enqueing: Item "+i);
			queue.enqueue("Item");
//			queue.put("Item");
			stat[1]	= Math.max(queue.size(), stat[1]);
		}

		stat[2]	= 1;	// Flag to indicate end of test.
		
		System.out.println("Queue was empty "+stat[0]+" times.\n"
			+"Queue size is now "+queue.size()+".\n"
			+"Max queue size was "+stat[1]+".");
	}

	/**
	 *  Main for testing.
	 * @throws InterruptedException 
	 */
	public static void	test2(String[] args) throws InterruptedException
	{
		final Object	monitor	= new Object();
		final int	num	= 5000000;
		final int[]	max	= new int[1];
		final int[]	counter	= new int[1];
		final boolean[]	finished	= new boolean[1];

		new Thread(new Runnable()
		{
			public void run()
			{
				int	wait	= 0;
				System.out.println("Thread started.");
				while(counter[0]>0 || !finished[0])
				{
					synchronized(monitor)
					{
						try
						{
							if(counter[0]==0)
							{
								monitor.wait();
								wait++;
							}
							if(counter[0]==0)
								throw new RuntimeException();
							counter[0]--;
						}
						catch(InterruptedException e)
						{
							StringWriter	sw	= new StringWriter();
							e.printStackTrace(new PrintWriter(sw));
							throw new RuntimeException(sw.toString());
						}

					}
				}
				System.out.println("Thread finished. Waits:  "+wait);
			}
		}).start();

		System.out.println("Main started.");
		for(int i=0; i<num; i++)
		{
			synchronized(monitor)
			{
				counter[0]++;
				max[0]	= Math.max(counter[0], max[0]);
				monitor.notify();
//				Thread.yield();
			}
		}
		finished[0]	= true;
		System.out.println("Main finished. Max is "+max[0]);
	}
}
