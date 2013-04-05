package jadex.micro.examples.helloworld;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Description;

import java.util.Iterator;

/**
 *  The micro version of the hello world agent.
 */
@Description("This agent prints out a hello message.")
@Arguments(@Argument(name="welcome text", description= "This parameter is the text printed by the agent.", 
	clazz=String.class, defaultvalue="\"Hello world, this is a Jadex micro agent.\""))
public class HelloWorldAgent extends MicroAgent
{
	//-------- methods --------
	
	/**
	 *  Execute an agent step.
	 */
	public IFuture<Void> executeBody()
	{
		final Future<Void> ret = new Future<Void>();
		System.out.println(getArgument("welcome text"));
		
		waitFor(2000, new IComponentStep<Void>()
		{			
			public IFuture<Void> execute(IInternalAccess ia)
			{
				System.out.println("Good bye world.");
//				killAgent();
//				ret.setResult(null);
				return IFuture.DONE;
			}
		});
		
		return ret;
	}
	
	public IFuture<Void> loop(final Iterator<IComponentManagementService> it)
	{
		final Future<Void> ret = new Future<Void>();
		
		if(it.hasNext())
		{
			IComponentManagementService cms = it.next();
			cms.createComponent(null, "HelloWorldAgent.class", null, null)
				.addResultListener(new ExceptionDelegationResultListener<IComponentIdentifier, Void>(ret)
			{
				public void customResultAvailable(IComponentIdentifier result)
				{
					loop(it).addResultListener(new DelegationResultListener<Void>(ret));
				}
			});
		}
		else
		{
			ret.setResult(null);
		}
		
		return ret;
	}
	
	//-------- static methods --------
	
//	/**
//	 *  Get the meta information about the agent.
//	 */
//	public static MicroAgentMetaInfo getMetaInfo()
//	{
//		return new MicroAgentMetaInfo("This agent prints out a hello message.", 
//			null, new IArgument[]{
//			new Argument("welcome text", "This parameter is the text printed by the agent.", "String", "Hello world, this is a Jadex micro agent."),	
//			}, null);
//	}

}