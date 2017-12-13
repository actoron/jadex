package jadex.micro.testcases.semiautomatic;

import java.util.Collection;

import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.annotation.Service;
import jadex.commons.future.IIntermediateFuture;
import jadex.micro.annotation.Agent;

/**
 *  This agent provides a choosable number of intermediate results.
 */
@Agent
@Service
public class IntermediateResultAgent	implements IIntermediateResultsService
{
	//-------- attributes --------
	
	/** The agent. */
	@Agent
	protected IInternalAccess	agent;
	
	//-------- IIntermediateResultService interface --------
	
	/**
	 *  The method provides the integers 1..number as intermediate results.
	 */
	public IIntermediateFuture<Integer>	getResults(int number)
	{
		return (IIntermediateFuture<Integer>)agent.getExternalAccess().scheduleStep(new IComponentStep<Collection<Integer>>()
		{
			public IIntermediateFuture<Integer> execute(IInternalAccess ia)
			{
				return null;
			}
		});
	}
}
