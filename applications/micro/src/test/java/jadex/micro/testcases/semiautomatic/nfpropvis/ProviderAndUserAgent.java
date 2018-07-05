package jadex.micro.testcases.semiautomatic.nfpropvis;

import java.util.ArrayList;
import java.util.List;

import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceIdentifier;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

/**
 *  Waits for a fixed random time before a service completes. 
 *  
 *  As in normal mode the waiting service would not defer further
 *  calls to the service, a busy state is introduced. If the service
 *  is busy it will just store the call and return only after
 *  all preceeding calls have been served.
 */
@Agent
@Service
@ProvidedServices(@ProvidedService(type=ICryptoService.class))
public class ProviderAndUserAgent extends UserAgent implements ICryptoService
{
	@ServiceIdentifier
	protected IServiceIdentifier sid;
	
	/** The test string. */
	protected long wait = (long)(Math.random()*1000);
	
	/** The invocation counter. */
	protected int cnt;
	
	/** The call queue. */
	protected List<Future<Void>> callqueue = new ArrayList<Future<Void>>();
	protected boolean busy = false;
	
	/** Defer flag. If turned off parallel execution as normal. */
	protected boolean defer = true;
	
	/**
	 *  Test method.
	 */
	public IFuture<String> encrypt(String text)
//	public IFuture<String> test()
	{
		if(!busy)
		{
			if(defer)
			{
				busy = true;
			}
//			System.out.println("invoked service: "+sid.getProviderId()+" cnt="+(++cnt)+" wait="+wait);
		}
		else
		{
//			System.out.println("callqueue is: "+sid.getProviderId()+" size="+callqueue.size());
			Future<Void> fut = new Future<Void>();
			callqueue.add(fut);
			fut.get();
		}
		
		agent.getFeature(IExecutionFeature.class).waitForDelay(wait).get();
		
		if(!callqueue.isEmpty())
		{
			callqueue.remove(0).setResult(null);
		}
		else
		{
			// busy is false after last call has been served
			busy = false; 
		}
		
		return new Future<String>(sid.toString());
	}
}
