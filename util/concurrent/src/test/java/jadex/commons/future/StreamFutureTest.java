package jadex.commons.future;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.stream.Stream;

import org.junit.Test;

/**
 *  Test using intermediate futures via java 8 stream API.
 */
public class StreamFutureTest
{
	/**
	 *  Test that results of a finished future can be accumulated. 
	 */
	@Test
	public void testFinishedFuture()
	{
		IntermediateFuture<Integer>	fut	= new IntermediateFuture<Integer>();
		Stream<Integer>	stream	= fut.asStream();
		
		fut.addIntermediateResult(1);
		fut.addIntermediateResult(2);
		fut.addIntermediateResult(3);
		fut.addIntermediateResult(4);
		fut.addIntermediateResult(5);
		fut.setFinished();
		
		assertEquals("12345", stream.map(num -> num.toString()).reduce((result, next) -> result+next).get());	
	}
	
	/**
	 *  Test if results of an unfinished future are processed.
	 */
	@Test
	public void testUnfinishedFuture()
	{
		IntermediateFuture<Integer>	fut	= new IntermediateFuture<Integer>();
		Stream<Integer>	stream	= fut.asStream();
		
		fut.addIntermediateResult(1);
		fut.addIntermediateResult(2);
		fut.addIntermediateResult(3);
		fut.addIntermediateResult(4);
		fut.addIntermediateResult(5);
		new Thread(()->
		{
			try{ Thread.sleep(300); } catch (InterruptedException e) {}
			fut.setFinished();
		}).start();
		
		assertFalse(fut.isDone());	
		assertEquals("12345", stream.map(num -> num.toString()).reduce((result, next) -> result+next).get());	
	}

}