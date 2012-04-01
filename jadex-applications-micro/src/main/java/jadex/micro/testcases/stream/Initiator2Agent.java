package jadex.micro.testcases.stream;

import java.util.Collection;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInputConnection;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.message.IMessageService;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.ComponentType;
import jadex.micro.annotation.ComponentTypes;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

/**
 *  Agent that provides a service with a stream.
 */
@Agent
@RequiredServices(
{
	@RequiredService(name="msgservice", type=IMessageService.class, 
		binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM)),
	@RequiredService(name="cms", type=IComponentManagementService.class, 
		binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM))
})
@ComponentTypes(
	@ComponentType(name="receiver", filename="jadex/micro/testcases/stream/Receiver2Agent.class")
)
public class Initiator2Agent
{
	@Agent
	protected MicroAgent agent;
	
	protected IInputConnection icon;
	
	/**
	 * 
	 */
	@AgentBody
	public IFuture<Void> body()
	{
		final Future<Void> ret = new Future<Void>();
		IFuture<IComponentManagementService> cmsfut = agent.getServiceContainer().getRequiredService("cms");
		cmsfut.addResultListener(new ExceptionDelegationResultListener<IComponentManagementService, Void>(ret)
		{
			public void customResultAvailable(IComponentManagementService cms)
			{
				cms.createComponent(null, "receiver", new CreationInfo(agent.getComponentIdentifier()), null)
					.addResultListener(new ExceptionDelegationResultListener<IComponentIdentifier, Void>(ret)
				{
					public void customResultAvailable(final IComponentIdentifier cid) 
					{
						IFuture<IMessageService> msfut = agent.getServiceContainer().getRequiredService("msgservice");
						msfut.addResultListener(new ExceptionDelegationResultListener<IMessageService, Void>(ret)
						{
							public void customResultAvailable(IMessageService ms)
							{
								ms.createInputConnection(agent.getComponentIdentifier(), cid)
									.addResultListener(new ExceptionDelegationResultListener<IInputConnection, Void>(ret)
								{
									public void customResultAvailable(final IInputConnection icon) 
									{
										Initiator2Agent.this.icon = icon;
										icon.aread().addResultListener(new IIntermediateResultListener<byte[]>()
										{
											public void resultAvailable(Collection<byte[]> result)
											{
												System.out.println("Result: "+result);
											}
											public void intermediateResultAvailable(byte[] result)
											{
												System.out.println("Intermediate result: "+result);
											}
											public void finished()
											{
												System.out.println("finished");
											}
											public void exceptionOccurred(Exception exception)
											{
												System.out.println("ex:"+exception);
											}
										});
									}
								});
							}
						});
					}
				});
			}
		});
		
		return ret;
	}
}

