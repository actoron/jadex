package jadex.micro.examples.mandelbrot;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import jadex.bridge.SFuture;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.SubscriptionIntermediateFuture;

/**
 *  The service allows displaying results in the frame
 *  managed by the service providing agent.
 */
@Service
public class DisplayService implements IDisplayService
{
	//-------- attributes --------
	
	/** The agent. */
	@ServiceComponent
	protected DisplayAgent agent;
	
	/** The display subscribers. */
	protected Map<String, SubscriptionIntermediateFuture<Object>> subscribers = new HashMap<String, SubscriptionIntermediateFuture<Object>>();

	//-------- IDisplayService interface --------

	/**
	 *  Display the result of a calculation.
	 */
	public IFuture<Void> displayResult(AreaData result)
	{
//		System.out.println("displayRes: "+agent.getComponentIdentifier());
//		agent.getPanel().setResults(result);
		String id = result.getDisplayId();
		if(id!=null)
		{
			SubscriptionIntermediateFuture<Object> sub = subscribers.get(id);
			sub.addIntermediateResult(result);
		}
		else
		{
			// todo: use default display
			for(Iterator<SubscriptionIntermediateFuture<Object>> it=subscribers.values().iterator(); it.hasNext(); )
			{
				SubscriptionIntermediateFuture<Object> sub = it.next();
				sub.addIntermediateResult(result);
			}
		}
		return IFuture.DONE;
	}


	/**
	 *  Display intermediate calculation results.
	 */
	public IFuture<Void> displayIntermediateResult(ProgressData progress)
	{
//		System.out.println("displayInRes");
//		agent.getPanel().addProgress(progress);
		String id = progress.getDisplayId();
		if(id!=null)
		{
			SubscriptionIntermediateFuture<Object> sub = subscribers.get(id);
			sub.addIntermediateResult(progress);
		}
		else
		{
			// todo: use default display
			for(Iterator<SubscriptionIntermediateFuture<Object>> it=subscribers.values().iterator(); it.hasNext(); )
			{
				SubscriptionIntermediateFuture<Object> sub = it.next();
				sub.addIntermediateResult(progress);
			}
		}
		
		return IFuture.DONE;
	}
	
	/**
	 *  Subscribe to display events.
	 */
	public ISubscriptionIntermediateFuture<Object> subscribeToDisplayUpdates(String displayid)
	{
//		SubscriptionIntermediateFuture<Object> ret = new SubscriptionIntermediateFuture<Object>();
		final SubscriptionIntermediateFuture<Object>	ret	= (SubscriptionIntermediateFuture<Object>)SFuture.getNoTimeoutFuture(SubscriptionIntermediateFuture.class, agent.agent);

		subscribers.put(displayid, ret);
		return ret;
	}
}
