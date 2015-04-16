package jadex.platform.service.dht;

import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.types.dht.IID;
import jadex.bridge.service.types.dht.IKVStore;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.AgentService;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

@Agent
@RequiredServices({
	@RequiredService(name = "kvstore", type = IKVStore.class, binding=@Binding(scope = RequiredServiceInfo.SCOPE_GLOBAL, dynamic = true))
})
public class DistributedHashUserAgent
{

	@Agent
	private IInternalAccess access;
	
	@AgentService(name = "kvstore", required = true)
	private IKVStore storeService;
	
	@AgentCreated
	private void onCreate() {
		access.getExternalAccess().scheduleStep(new IComponentStep<Void>()
		{

			@Override
			public IFuture<Void> execute(IInternalAccess ia)
			{
//				IKVStore service = SServiceProvider.getLocalService(access, IKVStore.class);
				IKVStore service = (IKVStore)access.getComponentFeature(IRequiredServicesFeature.class).getRequiredService("kvstore").get();
				
				final Future<Void> future = new Future<Void>();
				
				publishRetrieve("Dies ist String #1").addResultListener(new DefaultResultListener<Void>()
				{

					@Override
					public void resultAvailable(Void result)
					{
						publishRetrieve("Dies ist String #2").addResultListener(new DefaultResultListener<Void>()
						{

							@Override
							public void resultAvailable(Void result)
							{
								publishRetrieve("Dies ist String #3").addResultListener(new DefaultResultListener<Void>()
								{

									@Override
									public void resultAvailable(Void result)
									{
										future.setResult(null);
									}
								});
							}
						});						
					}
				});
				
				return future;
				
			}

			
		},1000).addResultListener(new DefaultResultListener<Void>()
		{

			@Override
			public void resultAvailable(Void result)
			{
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	private IFuture<Void> publishRetrieve(final String string)
	{
		final Future<Void> ret = new Future<Void>();
		storeService.publish(string, string).addResultListener(new DefaultResultListener<IID>()
		{

			@Override
			public void resultAvailable(IID result)
			{
				IFuture<String> lookup = storeService.lookup(string);
//				System.out.println("getting: " + lookup);
				String string = lookup.get();
				System.out.println("Retrieved: " + string);
				ret.setResult(null);
			}
		});
		
		return ret;
	}
}
