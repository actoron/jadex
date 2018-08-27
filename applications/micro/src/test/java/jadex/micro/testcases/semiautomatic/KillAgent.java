package jadex.micro.testcases.semiautomatic;

import jadex.base.Starter;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentCreated;

/**
 * 
 */
// Todo: what is this agent supposed to test!?
@Agent
public class KillAgent
{
	@Agent
	protected IInternalAccess agent;
	
	@AgentCreated
	public IFuture<Void> body()
	{
		final Future<Void> ret = new Future<Void>();
		agent.createComponent(null, new CreationInfo(agent.getId()).setFilename("jadex.micro.MicroAgent.class"), null)
			.addResultListener(agent.getFeature(IExecutionFeature.class).createResultListener(new IResultListener<IExternalAccess>()
		{
			public void resultAvailable(IExternalAccess result) 
			{
				System.out.println("Micro agent started: "+result);
			}
			
			public void exceptionOccurred(Exception exception)
			{
//				exception.printStackTrace();
			}
		}));
		
		ret.setResult(null);
		agent.killComponent();
		
		return IFuture.DONE;
	}
	
	/**
	 *  Main for testing.
	 */
	public static void main(String[] args)
	{
//		ThreadSuspendable sus = new ThreadSuspendable();
		IExternalAccess pl = Starter.createPlatform(new String[]{"-gui", "false", "-autoshutdown", "false"}).get();
		
		for(int i=0; i<1000; i++)
		{
			IComponentIdentifier cid = pl.createComponent(null, new CreationInfo().setFilename(KillAgent.class.getName()+".class")).getFirstResult();
			try
			{
				pl.killComponent(cid).get();
			}
			catch(Exception e)
			{
				System.out.println("Ex: "+e.getMessage());
			}
		}
		
		try
		{
			Thread.currentThread().sleep(30000);
		}
		catch(Exception e)
		{
		}
		
		System.out.println("fini");
	}
}
