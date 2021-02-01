package jadex.commons.future;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
		// Number threads/listeners/results -> complexity = count³ 
		int count	= 30;
		
		// Create lists to store results received by each listener
		List<List<String>>	resultss	= new ArrayList<>();
		for(int i=0; i<count; i++)
			resultss.add(new ArrayList<>());
		
		// Add |count| listeners to future.
		IntermediateFuture<String>	fut	= new IntermediateFuture<>();
		for(List<String> results: resultss)
			fut.addResultListener(new TestListener<>(results));
		
		// Create |count| threads adding |count| results each.
		List<Thread>	threads	= new ArrayList<>();
		for(int i=0; i<count; i++)
		{
			final int num	= i;
			threads.add(new Thread(() -> 
			{
				for(int j=0; j<count; j++)
				{
					Thread.yield();
					fut.addIntermediateResult(num+";"+j);
				}
			}));
		}
		
		// Start all threads and wait for them to finish
		for(Thread thread: threads)
			thread.start();
		for(Thread thread: threads)
			thread.join();
		
		// Check that all listeners received all results in the same order
		for(List<String> results: resultss)
			assertEquals(resultss.get(0), results);
		
		System.out.println("results: "+resultss.get(0));
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