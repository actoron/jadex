package jadex.commons.future;

import static org.junit.Assert.assertArrayEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import jadex.commons.future.IntermediateFutureTest.TestListener;

/**
 *  Test notification of quiet and non-quiet listeners.
 */
public class SubscriptionFutureTest
{
	@Test
	public void testListenerNotification()
	{
		List<String>	results1	= new ArrayList<String>();
		List<String>	results2	= new ArrayList<String>();
		List<String>	results3	= new ArrayList<String>();
		SubscriptionIntermediateFuture<String>	fut	= new SubscriptionIntermediateFuture<>();
		fut.addQuietListener(new TestListener<String>(results1));
		
		fut.addIntermediateResult("A");
		fut.addIntermediateResult("B");
		
		fut.addResultListener(new TestListener<String>(results2));

		fut.addIntermediateResult("C");
		fut.addIntermediateResult("D");
		
		fut.addResultListener(new TestListener<String>(results3));
		
		fut.addIntermediateResult("E");
		fut.addIntermediateResult("F");

		assertArrayEquals("quiet results",  results1.toArray(), "ABCDEF".split(""));
		assertArrayEquals("first nonquiet results",  results2.toArray(), "ABCDEF".split(""));
		assertArrayEquals("second nonquiet results",  results3.toArray(), "EF".split(""));
	}
}