package jadex.commons.future;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.junit.Test;

/**
 *  Test blocking access to subscription futures.
 */
public class SubscriptionFutureBlockingTest
{
	@Test
	public void testNonBlocking()
	{
		// Initialize future
		SubscriptionIntermediateFuture<String>	fut	= new SubscriptionIntermediateFuture<>();
		assertEquals("initially empty", Collections.emptySet(), new HashSet<>(fut.getIntermediateResults()));
		
		// Add and get some results
		List<String>	results	= new ArrayList<>();
		results.add("A");
		results.add("B");
		for(String result: results)
			fut.addIntermediateResult(result);
		assertEquals("nonblocking results", results, new ArrayList<>(fut.getIntermediateResults()));
		assertEquals("nonblocking results stay available",  results, new ArrayList<>(fut.getIntermediateResults()));

		// Blocking fetch -> consumes results
		for(String result: results)
			assertEquals("fetch result", result, fut.getNextIntermediateResult());
		assertEquals("empty after blocking acess", Collections.emptySet(), new HashSet<>(fut.getIntermediateResults()));

		// Add and get some more results
		results.clear();
		results.add("A");
		results.add("B");
		for(String result: results)
			fut.addIntermediateResult(result);
		assertEquals("nonblocking results", results, new ArrayList<>(fut.getIntermediateResults()));
		assertEquals("nonblocking results stay available",  results, new ArrayList<>(fut.getIntermediateResults()));
	}
}