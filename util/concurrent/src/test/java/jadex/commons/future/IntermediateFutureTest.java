package jadex.commons.future;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import org.junit.Test;

/**
 *  Test intermediate futures / listeners.
 */
public class IntermediateFutureTest
{
	/**
	 *  Test that parallel intermediate result notifications are consistently sequentialized. 
	 */
	@Test
	public void testConcurrency() throws InterruptedException
	{
		// 7 also works without stack compaction
		int	sccount	= 30;
		int nosccount	= 7;
		boolean	sc	= Future.NO_STACK_COMPACTION;	// remember for resetting
		
		try
		{
			// Test with and without stack compaction
			doTestConcurrency(Future.NO_STACK_COMPACTION ? nosccount : sccount);
			Future.NO_STACK_COMPACTION	= !Future.NO_STACK_COMPACTION;	// switch stack compaction
			doTestConcurrency(Future.NO_STACK_COMPACTION ? nosccount : sccount);
		}
		finally
		{
			Future.NO_STACK_COMPACTION	= !sc;
		}
	}
	
	/**
	 *  Perform the actual test.
	 *  @param count Number of futures/threads/listeners/results -> complexity ~ count^4 
	 */
	protected void	doTestConcurrency(int count) throws InterruptedException
	{
		// Create |count*count| lists to store results received by each listener
		List<List<String>>	resultss	= new ArrayList<>();
		for(int r=0; r<count*count; r++)
			resultss.add(new ArrayList<>());
		
		// Create |count| futures and create |count| threads each adding |count| results each.
		List<Thread>	threads	= new ArrayList<>();
		for(int f=0; f<count; f++)
		{
			IntermediateFuture<String>	fut	= new IntermediateFuture<>();
			for(int t=0; t<count; t++)
			{
				threads.add(createAdderThread(t+";", count, fut, resultss.get(f*count+t)));
			}
		}
		
		// Start all threads and wait for them to finish
		for(Thread thread: threads)
			thread.start();
		for(Thread thread: threads)
			thread.join();
		
		System.out.println("results: "+resultss.get(0));
		
		// Check that all listeners received all results in the same order for each future
		for(int f=0; f<count; f++)
		{
			for(int t=0; t<count; t++)
			{
				// Results for one future are stored from |f*count| to |f*count+(count-1)|
				assertEquals(resultss.get(f*count), resultss.get(f*count+t));
			}
		}
	}
	
	//-------- helper methods --------
	
	/**
	 *  Create a thread and a listener for a future to add |count| results.
	 *  Adds a listener at some random point during addition of results.
	 *  @param prefix	Prefix for the results to be added to identify different threads.
	 *  @param count	The number of results ("1", "2", ...) to add to the future.
	 *  @param results	The list where the listener will store received results.
	 *  @return The thread that still needs to be start()-ed. 
	 */
	protected Thread	createAdderThread(String prefix, int count, IntermediateFuture<String> fut, List<String> results)
	{
		return new Thread(() -> 
		{
			int	addnum	= new Random().nextInt(count);
			for(int i=0; i<count; i++)
			{
				fut.addIntermediateResult(prefix+i);
				
				if(i==addnum)
				{
					fut.addResultListener(new TestListener<>(results));
				}
			}
		});
	}
	
	//-------- helper classes --------
	
	/**
	 *  Collect results into a list.
	 *
	 */
	static class TestListener<E> implements IIntermediateResultListener<E>
	{
		private final List<E> results1;

		TestListener(List<E> results1)
		{
			this.results1 = results1;
		}

		public void intermediateResultAvailable(E result)
		{
			results1.add(result);
		}

		// NOP methods
		public void resultAvailable(Collection<E> result) {}

		public void exceptionOccurred(Exception exception) {}

		public void finished() {}

		public void maxResultCountAvailable(int max) {}
	}
}