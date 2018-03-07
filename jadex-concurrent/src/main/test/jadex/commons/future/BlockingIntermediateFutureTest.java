package jadex.commons.future;

import java.util.Iterator;

import org.junit.Assert;
import org.junit.Test;

/**
 *  Test blocking access to intermediate future.
 */
public class BlockingIntermediateFutureTest //extends TestCase
{
	@Test
	public void testBlockingIteration()
	{
		IntermediateFuture<String>	fut	= new IntermediateFuture<String>();
		Object	mon1	= new Object();
		Object	mon2	= new Object();
		Object	mon3	= new Object();
		Object	mon4	= new Object();
		Run	run1	= new Run(fut, mon1, true);
		Run	run2	= new Run(fut, mon2, false);
		RunIterator	run3	= new RunIterator(fut, mon3, true);
		RunIterator	run4	= new RunIterator(fut, mon4, false);
		
		synchronized(mon1)
		{
			synchronized(mon2)
			{
				synchronized(mon3)
				{
					synchronized(mon4)
					{
						try
						{
							fut.addIntermediateResult("result1");
							fut.addIntermediateResult("result2");
							
							new Thread(run1).start();
							new Thread(run2).start();
							new Thread(run3).start();
							new Thread(run4).start();
							
							Thread.sleep(500);
							fut.addIntermediateResult("result3");
							
							Thread.sleep(500);
							fut.addIntermediateResult("result4");					
		
							Thread.sleep(500);
							fut.setFinished();
							
							mon1.wait();
							mon2.wait();
							mon3.wait();
							mon4.wait();
						}
						catch(InterruptedException e)
						{
							throw new RuntimeException(e);
						}
					}
				}
			}
		}

		if(run1.failure!=null)
		{
			if(run1.failure instanceof Error)
				throw (Error)run1.failure;
			else
				throw (RuntimeException)run1.failure; 
		}
		
		if(run2.failure!=null)
		{
			if(run2.failure instanceof Error)
				throw (Error)run2.failure;
			else
				throw (RuntimeException)run2.failure; 
		}
		
		if(run3.failure!=null)
		{
			if(run3.failure instanceof Error)
				throw (Error)run3.failure;
			else
				throw (RuntimeException)run3.failure; 
		}
		
		if(run4.failure!=null)
		{
			if(run4.failure instanceof Error)
				throw (Error)run4.failure;
			else
				throw (RuntimeException)run4.failure; 
		}
	}
	
	public static class Run	implements Runnable
	{
		IIntermediateFuture<String> fut;
		Object mon;
		boolean exit;
		boolean	suspended;
		Throwable	failure;
		
		public Run(IIntermediateFuture<String> fut, Object mon, boolean exit)
		{
			this.fut	= fut;
			this.mon	= mon;
			this.exit	= exit;
		}
		
		public void run()
		{
			try
			{
				ISuspendable.SUSPENDABLE.set(new ThreadSuspendable()
				{
					@Override
					public void suspend(Future<?> future, long timeout, boolean realtime)
					{
						suspended	= true;
						super.suspend(future, timeout, realtime);
					}
				});
				
				// Retrieve first two results (should be non-blocking)
				for(int i=1; i<=2 && fut.hasNextIntermediateResult(); i++)
				{
					Assert.assertEquals("result"+i, fut.getNextIntermediateResult());
				}
				Assert.assertEquals(false, suspended);
	
				// Retrieve next result (should be blocking)
				Assert.assertEquals("result3", fut.getNextIntermediateResult());
				Assert.assertEquals(true, suspended);
	
				// Check for next result (should be blocking on check)
				suspended	= false;
				Assert.assertEquals(true, fut.hasNextIntermediateResult());
				Assert.assertEquals(true, suspended);
				suspended	= false;
				Assert.assertEquals("result4", fut.getNextIntermediateResult());
				Assert.assertEquals(false, suspended);
				
				// Check for graceful exit (should block until finished)
				if(exit)
				{
					Assert.assertEquals(false, fut.hasNextIntermediateResult());					
					Assert.assertEquals(true, suspended);
					suspended	= false;
				}
				
				// Check for exception exit (should block until finished)
				else
				{
					try
					{
						fut.getNextIntermediateResult();
						Assert.assertTrue(false);	// Should throw exception above.
					}
					catch(RuntimeException e)
					{
						Assert.assertEquals(true, suspended);
						suspended	= false;
					}
				}
				
				// Check for additional access after finished (should not block).
				try
				{
					fut.getNextIntermediateResult();
					Assert.assertTrue(false);	// Should throw exception above.
				}
				catch(RuntimeException e)
				{
					Assert.assertEquals(false, suspended);
				}
				Assert.assertEquals(false, fut.hasNextIntermediateResult());					
				Assert.assertEquals(false, suspended);
			}
			catch(Throwable t)
			{
				failure	= t;
			}

			// Notify main thread.
			synchronized(mon)
			{
				mon.notify();
			}
		}
	}
	
	public static class RunIterator	implements Runnable
	{
		IIntermediateFuture<String> fut;
		Object mon;
		boolean exit;
		boolean	suspended;
		Throwable	failure;
		
		public RunIterator(IIntermediateFuture<String> fut, Object mon, boolean exit)
		{
			this.fut	= fut;
			this.mon	= mon;
			this.exit	= exit;
		}
		
		public void run()
		{
			try
			{
				ISuspendable.SUSPENDABLE.set(new ThreadSuspendable()
				{
					@Override
					public void suspend(Future<?> future, long timeout, boolean realtime)
					{
						suspended	= true;
						super.suspend(future, timeout, realtime);
					}
				});
				Iterator<String>	it	= new IntermediateFutureIterator<String>(fut);

				
				// Retrieve first two results (should be non-blocking)
				for(int i=1; i<=2 && it.hasNext(); i++)
				{
					Assert.assertEquals("result"+i, it.next());
				}
				Assert.assertEquals(false, suspended);
	
				// Retrieve next result (should be blocking)
				Assert.assertEquals("result3", it.next());
				Assert.assertEquals(true, suspended);
	
				// Check for next result (should be blocking on check)
				suspended	= false;
				Assert.assertEquals(true, it.hasNext());
				Assert.assertEquals(true, suspended);
				suspended	= false;
				Assert.assertEquals("result4", it.next());
				Assert.assertEquals(false, suspended);
				
				// Check for graceful exit (should block until finished)
				if(exit)
				{
					Assert.assertEquals(false, it.hasNext());
					Assert.assertEquals(true, suspended);
					suspended	= false;
				}
				
				// Check for exception exit (should block until finished)
				else
				{
					try
					{
						it.next();
						Assert.assertTrue(false);	// Should throw exception above.
					}
					catch(RuntimeException e)
					{
						Assert.assertEquals(true, suspended);
						suspended	= false;
					}
				}
				
				// Check for additional access after finished (should not block).
				try
				{
					it.next();
					Assert.assertTrue(false);	// Should throw exception above.
				}
				catch(RuntimeException e)
				{
					Assert.assertEquals(false, suspended);
				}
				Assert.assertEquals(false, it.hasNext());					
				Assert.assertEquals(false, suspended);
			}
			catch(Throwable t)
			{
				failure	= t;
			}

			// Notify main thread.
			synchronized(mon)
			{
				mon.notify();
			}
		}
	}
}
