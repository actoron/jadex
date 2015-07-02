package jadex.bridge.service.search;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import jadex.bridge.ClassInfo;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.IService;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.dht.IDistributedServiceRegistryService;
import jadex.bridge.service.types.dht.ServiceRegistration;
import jadex.bridge.service.types.remote.IRemoteServiceManagementService;
import jadex.commons.IAsyncFilter;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ITerminableIntermediateFuture;
import jadex.commons.future.IntermediateDelegationResultListener;
import jadex.commons.future.TerminableIntermediateFuture;

public class DistributedServiceRegistry extends PlatformServiceRegistry
{

	private static final int	PUBLISH_DELAY	= ServiceRegistration.LEASE_TIME;
	private IInternalAccess	access;
	private Map<ClassInfo, IService>	delayed;
	private IComponentStep<Void>	publishDelayedStep;
	private boolean	provideOnly;

	public DistributedServiceRegistry(final IInternalAccess access, final boolean provideOnly)
	{
		this.access = access;
		this.provideOnly = provideOnly;
		this.delayed = new HashMap<ClassInfo, IService>();
		System.out.println("Distributed mode");
		if (provideOnly) {
			System.out.println("Provider mode: Only publishing ring services in DHT");
		}
		final IComponentStep<Void> publishStep = new IComponentStep<Void>()
		{

			IComponentStep<Void> step = this;
			
			@Override
			public IFuture<Void> execute(IInternalAccess ia)
			{
				System.out.println("Re-publishing services...");
				if (isValid(kvService)) {
					for(Entry<ClassInfo, Set<IService>> entry : services.entrySet())
					{
						Set<IService> value = entry.getValue();
						for(IService service : value)
						{
							kvService.publish(entry.getKey().getTypeName(), service.getServiceIdentifier()).addResultListener(new InvalidateServiceListener<Void>());
						}
					}
				}
				access.getExternalAccess().scheduleStep(step, PUBLISH_DELAY);
				
				return Future.DONE;
			}
		};
		Timer timer = new Timer();
		timer.schedule(new TimerTask()
		{
			
			@Override
			public void run()
			{
				searchService();
				if (publishDelayedStep != null) {
					access.getExternalAccess().scheduleStep(publishDelayedStep, 5000);
				} else {
					System.out.println("No delayed services.. error!?");
				}
				if (!provideOnly) {
					access.getComponentFeature(IExecutionFeature.class).waitForDelay(PUBLISH_DELAY, publishStep);
				}
			}
		}, 10000);
	}
	
	protected boolean isValid(IDistributedServiceRegistryService kvService)
	{
		boolean result = false;
		if (kvService != null) {
			try {
				result = kvService.isInitialized();
			} catch (Exception e) {
				// proxy exception -> invalid service
				this.kvService = null;
				searchService();
			}
		}
		return result;
	}

	@Override
	public synchronized void addService(final ClassInfo key, final IService service)
	{
//		System.out.println("AddService called: " + key.getTypeName());
		if (!provideOnly || key.getTypeName().startsWith("jadex.bridge.service.types.dht.")) {
			if (isValid(kvService)) {
				System.out.println("Publishing service to DHT: " + key.getTypeName());
				kvService.publish(key.getTypeName(), service.getServiceIdentifier()).addResultListener(new InvalidateServiceListener<Void>());
			} else {
				delayed.put(key, service);
				if (publishDelayedStep == null) {
					publishDelayedStep = new IComponentStep<Void>()
					{
						public IFuture<Void> execute(IInternalAccess ia)
						{
							if (!delayed.isEmpty()) {
								if (isValid(kvService)) {
									Set<Entry<ClassInfo,IService>> entrySet = delayed.entrySet();
									
									for(Entry<ClassInfo, IService> entry : entrySet)
									{
										System.out.println("(Delayed) Publishing service to DHT: " + entry.getKey().getTypeName());
										kvService.publish(entry.getKey().getTypeName(), entry.getValue().getServiceIdentifier()).addResultListener(new InvalidateServiceListener<Void>());
									}
									
									delayed.clear();
									publishDelayedStep = null;
								} else {
									access.getExternalAccess().scheduleStep(publishDelayedStep, 5000);
								}
							}
							return Future.DONE;
						}
					};
				}
			}
		}
		super.addService(key, service);
	}

	@Override
	protected <T> ITerminableIntermediateFuture<T> searchRemoteServices(final IComponentIdentifier caller, final Class<T> type, IAsyncFilter<T> filter)
	{
		final TerminableIntermediateFuture<T> myret = new TerminableIntermediateFuture<T>();
		ITerminableIntermediateFuture<T> ret = myret;
		
		if (!provideOnly) {
	//		IDistributedServiceRegistryService kvService = getKvService().get();
			if (isValid(kvService) && !caller.getName().contains("diststore")) {
				System.out.println("Searching in DHT for: " + type + ", caller: " + caller.getName());
				IFuture<Collection<ServiceRegistration>> lookup = kvService.lookup(type.getName());
				lookup.addResultListener(new InvalidateServiceListener<Collection<ServiceRegistration>>()
				{
		
					@Override
					public void resultAvailable(Collection<ServiceRegistration> regs)
					{
						System.out.println("services found in dht store: " + type.getName() + ":");
						if (regs!= null) {
							
							for(ServiceRegistration reg : regs)
							{
								IServiceIdentifier sid = reg.getSid();
								System.out.println("\t" + sid.getServiceName() + " on component: " + sid.getProviderId());
							}
							
							TerminableIntermediateFuture<T> serviceProxies = getServiceProxies(regs, type, caller);
							serviceProxies.addResultListener(new IntermediateDelegationResultListener<T>(myret));
						} else {
							myret.setResult(Collections.EMPTY_SET);
						}
					}
					
					@Override
					public void exceptionOccurred(Exception exception)
					{
						super.exceptionOccurred(exception);
						myret.setException(exception);
					}
				});
			} else {
				System.out.println("Fallback for bootstrapping...");
				ret = super.searchRemoteServices(caller, type, filter);
			}
		} else {
			ret = super.searchRemoteServices(caller, type, filter);
		}
//		ret = super.searchRemoteServices(caller, type, filter);
		return ret;
	}

	protected <T> TerminableIntermediateFuture<T> getServiceProxies(Collection<ServiceRegistration> regs, Class<T> type, IComponentIdentifier caller) {
//		System.out.println("getproxies start.");
		final TerminableIntermediateFuture<T> ret = new TerminableIntermediateFuture<T>();
		final CounterResultListener<Void> counter = new CounterResultListener<Void>(regs.size(), new DefaultResultListener<Void>()
		{
			public void resultAvailable(Void result)
			{
				ret.setFinished();
//				System.out.println("getproxies finished.");
			}
		});
		
		for(ServiceRegistration reg : regs)
		{
			IServiceIdentifier sid = reg.getSid();
			if (!sid.getProviderId().getRoot().equals(access.getComponentIdentifier().getRoot())) {
				getServiceProxy(sid, type, caller).addResultListener(new DefaultResultListener<T>() {
					
					public void resultAvailable(T result)
					{
						ret.addIntermediateResult(result);
						counter.resultAvailable(null);
					}
					
					@Override
					public void exceptionOccurred(Exception exception)
					{
						// sid is no longer valid!
						counter.resultAvailable(null);
					}
					
				});
			} else {
				counter.resultAvailable(null);
//				System.out.println("Is local: " + sid);
			}
		}
		return ret;
	}
	
	protected <T> IFuture<T> getServiceProxy(final IServiceIdentifier sid, Class<T> type, IComponentIdentifier caller)
	{
		final Future<T> ret = new Future<T>();
		
		final IRemoteServiceManagementService rms = getService(IRemoteServiceManagementService.class);
		
		final IComponentManagementService cms = getService(IComponentManagementService.class);
		
		rms.getExternalAccessProxy(caller).addResultListener(new ExceptionDelegationResultListener<IExternalAccess, T>(ret)
		{

			public void customResultAvailable(IExternalAccess result)
			{
				IFuture<T> service = SServiceProvider.getService(result, sid);
				service.addResultListener(new DelegationResultListener<T>(ret));
			}
		});
		
		
		return ret;
		
	}

	@Override
	protected <T> IFuture<T> searchRemoteService(IComponentIdentifier caller, final Class<T> type, IAsyncFilter<T> filter)
	{
		// TODO: single service lookup
		return super.searchRemoteService(caller, type, filter);
	}
	
	private IDistributedServiceRegistryService kvService;
	
	private IFuture<IDistributedServiceRegistryService> searchService()
	{
		final Future<IDistributedServiceRegistryService> future = new Future<IDistributedServiceRegistryService>();
		
		if (kvService == null) {
			System.out.println("Searching service...");
			kvService = getService(IDistributedServiceRegistryService.class);
			if (kvService == null) {
				IFuture<IDistributedServiceRegistryService> search = super.searchRemoteService(access.getComponentIdentifier(), IDistributedServiceRegistryService.class, null);
				search.addResultListener(new DefaultResultListener<IDistributedServiceRegistryService>()
				{

					@Override
					public void resultAvailable(IDistributedServiceRegistryService result)
					{
						System.out.println("Found service!");
						kvService = result;
						future.setResult(kvService);
					}
					
					@Override
					public void exceptionOccurred(Exception exception)
					{
						exception.printStackTrace();
						future.setResult(null);
						access.getComponentFeature(IExecutionFeature.class).waitForDelay(PUBLISH_DELAY, new IComponentStep<Void>()
						{

							@Override
							public IFuture<Void> execute(IInternalAccess ia)
							{
								searchService();
								return Future.DONE;
							}
						});
					}
				});
			} else {
				future.setResult(kvService);
			}
		}
		return future;
	}
	
	public class InvalidateServiceListener<T> implements IResultListener<T>
	{
		public void exceptionOccurred(Exception exception) {
			kvService = null;
			searchService();
		}

		@Override
		public void resultAvailable(T result)
		{
		};

	}
}
