package jadex.micro.testcases.distributedservicepool;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.annotation.OnEnd;
import jadex.commons.Boolean3;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IntermediateFuture;
import jadex.micro.annotation.Agent;

@Agent(autoprovide = Boolean3.TRUE)
public class WorkerAgent implements ITestService
{
	/** The agent. */
	@Agent
	protected IInternalAccess agent;
	
	public IFuture<Void> ok()
	{
		System.out.println("called ok on: "+agent.getId());
		return IFuture.DONE;
	}
	
	public IFuture<Void> ex()
	{
		return new Future<Void>(new RuntimeException("Some error on: "+agent.getId()));
	}
	
	public IIntermediateFuture<String> inter()
	{
		IntermediateFuture<String> ret = new IntermediateFuture<String>();
		
		for(int i=0; i<10; i++)
		{
			ret.addIntermediateResult(""+i);
			agent.waitForDelay(1000).get();
		}
		ret.setFinished();
		
		return ret;
	}

	public IIntermediateFuture<String> interex()
	{
		IntermediateFuture<String> ret = new IntermediateFuture<String>();
		
		for(int i=0; i<5; i++)
		{
			ret.addIntermediateResult(""+i);
			agent.waitForDelay(1000).get();
		}
		ret.setException(new RuntimeException("Some error"));
		
		return ret;
	}
	
	@OnEnd
	public void end(Exception e)
	{
		e.printStackTrace();
		System.out.println("end");
	}

}
