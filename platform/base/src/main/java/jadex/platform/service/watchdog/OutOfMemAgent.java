package jadex.platform.service.watchdog;

import java.util.LinkedList;
import java.util.List;

import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;

/**
 *  An agent used to provoke out-of-memory-errors fur testing
 *  platform robustness.
 */
@Agent
public class OutOfMemAgent	implements IComponentStep<Void>
{
	//-------- attributes --------
	
	/** The list of memory-hogging objects. */
	protected List<Object>	objects	= new LinkedList<Object>();
	
	//-------- methods --------
		
	/**
	 *  Main behavior just accumulates 1MB-sized objects.
	 */
	@AgentBody
	public IFuture<Void> execute(IInternalAccess ia)
	{
		if(objects.size()%100==0)
		{
			System.out.println(getClass().getSimpleName()+" accumulated "+objects.size()+" MB");
		}
		objects.add(new byte[1024*1024]);
		return ia.getFeature(IExecutionFeature.class).waitForDelay(10, this, false);
	}
}
