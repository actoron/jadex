package jadex.micro.tutorial;

import javax.management.ServiceNotFoundException;

import jadex.bridge.FactoryFilter;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.factory.IComponentFactory;
import jadex.commons.IAsyncFilter;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ITerminableIntermediateFuture;
import jadex.commons.future.IntermediateExceptionDelegationResultListener;
import jadex.micro.MicroAgentFactory;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentFeature;
import jadex.micro.annotation.Description;

/**
 *  Chat micro agent that search the factory for micro agents. 
 */
@Description("This agent search the factory for micro agents.")
@Agent
public class ChatC4Agent
{
	/** The underlying mirco agent. */
	@Agent
	protected IInternalAccess agent;

	/**
	 *  Execute the functional body of the agent.
	 *  Is only called once.
	 */
	@AgentBody
	public void executeBody()
	{
		IAsyncFilter<IComponentFactory>	filter	= new FactoryFilter(MicroAgentFactory.FILETYPE_MICROAGENT);
		Future<IComponentFactory> factory = new Future<>();
		ITerminableIntermediateFuture<IComponentFactory> search	= agent.getFeature(IRequiredServicesFeature.class).searchServices(new ServiceQuery<>(IComponentFactory.class));
		search.addResultListener(new IntermediateExceptionDelegationResultListener<IComponentFactory, IComponentFactory>(factory)
		{
			@Override
			public void intermediateResultAvailable(IComponentFactory fac)
			{
				System.out.println("factory: "+fac);
				filter.filter(fac).addResultListener(new ExceptionDelegationResultListener<Boolean, IComponentFactory>(factory)
				{
					@Override
					public void customResultAvailable(Boolean result) throws Exception
					{
						if(result)
						{
							factory.setResultIfUndone(fac);
							search.terminate();
						}
					}
				});
			}
			
			@Override
			public void finished()
			{
				factory.setExceptionIfUndone(new ServiceNotFoundException());
			}
		});
		
		//factory.addResultListener(agent.getCompocreateResultListener(new IResultListener<IComponentFactory>()
		factory.addResultListener(new IResultListener<IComponentFactory>()
		{
			public void resultAvailable(IComponentFactory result)
			{
				System.out.println("Found: "+result);
			}
			public void	exceptionOccurred(Exception e)
			{
				System.out.println("Not found: "+e);				
			}
		});
	}
}