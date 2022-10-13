package jadex.micro.examples.mandelbrot_new;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jadex.bridge.IInternalAccess;
import jadex.bridge.SFuture;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.SubscriptionIntermediateFuture;
import jadex.commons.future.TerminationCommand;

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
	protected IInternalAccess agent;
	
	/** The display subscribers. */
	protected Map<String, SubscriptionIntermediateFuture<Object>> subscribers = new HashMap<String, SubscriptionIntermediateFuture<Object>>();

	/** Store results till display subscribed */
	protected List<Object> storedresults = new ArrayList<>();
	
	//-------- IDisplayService interface --------

	/**
	 *  Display the result of a calculation.
	 */
	public IFuture<Void> displayResult(AreaData result)
	{
		internalDisplayResult(result, true);
		return IFuture.DONE;
	}
	
	/**
	 *  Display the result of a calculation.
	 */
	protected boolean internalDisplayResult(AreaData result, boolean store)
	{
		boolean consumed = false;
//		System.out.println("displayRes: "+agent.getComponentIdentifier());
//		agent.getPanel().setResults(result);
		String id = result.getDisplayId();
		if(id!=null)
		{
			SubscriptionIntermediateFuture<Object> sub = subscribers.get(id);
			if(sub==null)
			{
				if(store)
					storedresults.add(result);
			}
			else
			{
				sub.addIntermediateResult(result);
			}
		}
		else
		{			
			if(subscribers.values().isEmpty())
			{
				if(store)
					storedresults.add(result);
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
		}
		return consumed;
	}

	/**
	 *  Display intermediate calculation results.
	 */
	public IFuture<Void> displayIntermediateResult(ProgressData progress)
	{
		//System.out.println("displayInRes: "+progress);
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
	 *  Display intermediate calculation results.
	 */
	public IFuture<Void> displayIntermediateResult(PartDataChunk data)
	{
		internalDisplayIntermediateResult(data, true);
		return IFuture.DONE;
	}
	
	/**
	 *  Display intermediate calculation results.
	 */
	protected boolean internalDisplayIntermediateResult(PartDataChunk data, boolean store)
	{
		boolean consumed = false;
		
		//System.out.println("displayInRes: "+progress);
//		agent.getPanel().addProgress(progress);
		String id = data.getDisplayId();
		if(id!=null)
		{
			SubscriptionIntermediateFuture<Object> sub = subscribers.get(id);
			if(sub==null)
			{
				if(store)
					storedresults.add(data);
			}
			else
			{
				sub.addIntermediateResult(data);
				consumed = true;
			}
		}
		else
		{
			if(subscribers.values().isEmpty())
			{
				if(store)
					storedresults.add(data);
			}
			else
			{
				// todo: use default display
				for(Iterator<SubscriptionIntermediateFuture<Object>> it=subscribers.values().iterator(); it.hasNext(); )
				{
					SubscriptionIntermediateFuture<Object> sub = it.next();
					sub.addIntermediateResult(data);
				}
				consumed = true;
			}
		}
		
		return consumed;
	}
	
	/**
	 *  Subscribe to display events.
	 */
	public ISubscriptionIntermediateFuture<Object> subscribeToDisplayUpdates(String displayid)
	{
		//System.out.println("subscribeToDisplay: "+displayid);
//		SubscriptionIntermediateFuture<Object> ret = new SubscriptionIntermediateFuture<Object>();
		final SubscriptionIntermediateFuture<Object> ret = (SubscriptionIntermediateFuture<Object>)SFuture.getNoTimeoutFuture(SubscriptionIntermediateFuture.class, agent);
		ret.setTerminationCommand(new TerminationCommand()
		{
			public void terminated(Exception reason)
			{
				subscribers.remove(displayid);
				//System.out.println("removed display: "+displayid+" "+reason);
			}
		});
		subscribers.put(displayid, ret);
		
		// Send out stored results in case of first subscription
		if(subscribers.size()==1 && storedresults.size()>0)
		{
			//System.out.println("sending old results");
			List<Object> toremove = new ArrayList<>();
			storedresults.stream()
			.forEach(o -> 
			{
				if(o instanceof AreaData)
					if(internalDisplayResult((AreaData)o, false))
						toremove.add(o);
				else if(o instanceof PartDataChunk)
					if(internalDisplayIntermediateResult((PartDataChunk)o, false))
						toremove.add(o);
			});
			storedresults.removeAll(toremove);
		}
		
		return ret;
	}
	
	/**
	 *  Get info about an algorithm (for web). todo: move?!
	 *  @return The info.
	 */
	public IFuture<AreaData> getAlgorithmDefaultSettings(Class<IFractalAlgorithm> clazz)
	{
		try
		{
			return new Future<AreaData>(clazz.getDeclaredConstructor().newInstance().getDefaultSettings());
		}
		catch(Exception e)
		{
			return new Future<AreaData>(e);
		}
	}
}
