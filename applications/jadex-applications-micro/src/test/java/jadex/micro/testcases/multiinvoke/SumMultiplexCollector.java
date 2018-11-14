package jadex.micro.testcases.multiinvoke;

import java.util.ArrayList;
import java.util.List;

import jadex.bridge.service.component.multiinvoke.FlattenMultiplexCollector;
import jadex.commons.future.IntermediateFuture;

/**
 * 
 */
public class SumMultiplexCollector extends FlattenMultiplexCollector
{
	/** The list of results (if ret is not intermediate future). */
	protected List<Object> results = new ArrayList<Object>();
	
	/**
	 *  Add a result.
	 *  @param result The result.
	 */
	protected void addResult(Object result)
	{
		results.add(result);
	}
	
	/**
	 *  Set finished.
	 */
	protected void setFinished()
	{
		int sum = 0;
		for(Object res: results)
		{
			sum += ((Number)res).intValue();
		}
		
		if(fut instanceof IntermediateFuture)
		{
			((IntermediateFuture)fut).addIntermediateResult(Integer.valueOf(sum));
			((IntermediateFuture)fut).setFinished();
		}
		else
		{
			fut.setResult(Integer.valueOf(sum));
		}
	}
}
