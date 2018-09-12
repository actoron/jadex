package jadex.micro.testcases.blocking;

import java.util.Map;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.commons.SReflect;
import jadex.commons.future.DefaultTuple2ResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.ComponentType;
import jadex.micro.annotation.ComponentTypes;

/**
 * 
 */
@Agent
@Service
@ComponentTypes(@ComponentType(name="block", clazz=BlockAgent.class))
public class ShutdownAgent
{
	//-------- attributes --------
	
	/** The agent. */
	@Agent
	protected IInternalAccess agent;
	
	/**
	 * 
	 */
	@AgentBody
	public IFuture<Void> body()
	{
		final Future<Void> ret = new Future<Void>();
		
		agent.createComponent(new CreationInfo(agent.getId()).setFilename(BlockAgent.class.getName()+".class"))
			.addResultListener(new DefaultTuple2ResultListener<IComponentIdentifier, Map<String, Object>>()
		{
			public void firstResultAvailable(final IComponentIdentifier cid)
			{
				// call several times a blocking method on the agent and then terminate it
				
				agent.getFeature(IRequiredServicesFeature.class).searchService(new ServiceQuery<>(IBlockService.class).setProvider(cid))
					.addResultListener(new ExceptionDelegationResultListener<IBlockService, Void>(ret)
				{
					public void customResultAvailable(IBlockService bs)
					{
						int numBlocks = 1000;
						if (SReflect.isAndroid()) {
							numBlocks = 100;
						}
						for(int i=0; i<numBlocks; i++) {
							bs.block(-1);
						}
						
						agent.getFeature(IExecutionFeature.class).waitForDelay(1000).addResultListener(new DelegationResultListener<Void>(ret)
						{
							public void customResultAvailable(Void result)
							{
								agent.killComponent(cid).addResultListener(new ExceptionDelegationResultListener<Map<String,Object>, Void>(ret)
								{
									public void customResultAvailable(Map<String, Object> result)
									{
										System.out.println("fini1: "+result);
									}
								});
							}
						});
					}
				});
			}
			
			public void secondResultAvailable(Map<String, Object> result)
			{
				System.out.println("fini2: "+result);
//						String model = ShutdownAgent.class.getName()+".class";
//						cms.createComponent(model, new CreationInfo(agent.getModel().getResourceIdentifier()))
//							.addResultListener(new DefaultTuple2ResultListener<IComponentIdentifier, Map<String, Object>>()
//						{
//							public void firstResultAvailable(IComponentIdentifier result)
//							{
						agent.killComponent();
//							}
//							
//							public void secondResultAvailable(Map<String, Object> result)
//							{
//							}
//							
//							public void exceptionOccurred(Exception exception)
//							{
//								System.out.println("except: "+exception);
//							}
//						});
			}
			
			public void exceptionOccurred(Exception exception)
			{
				ret.setException(exception);
			}
		});
	
		return ret;
	}
}
