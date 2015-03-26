package jadex.android.service;

import jadex.android.IEventReceiver;
import jadex.android.exception.WrongEventClassError;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.context.IJadexAndroidEvent;
import jadex.bridge.service.types.message.IMessageService;
import jadex.bridge.service.types.platform.IJadexPlatformBinder;
import jadex.commons.future.IFuture;

public class JadexPlatformBinder extends JadexMultiPlatformBinder implements IJadexPlatformBinder
{

	private JadexPlatformService service;

	public JadexPlatformBinder(JadexPlatformService service)
	{
		super(service);
		this.service = service;
	}

	public boolean isPlatformRunning(IComponentIdentifier platformId)
	{
		return service.isPlatformRunning(platformId);
	}

	public IFuture<IExternalAccess> startJadexPlatform()
	{
		return service.startJadexPlatform(service.getPlatformKernels());
	}

	public IFuture<IExternalAccess> startJadexPlatform(String[] kernels)
	{
		return service.startJadexPlatform(kernels);
	}

	public IFuture<IExternalAccess> startJadexPlatform(String[] kernels, String platformId)
	{
		return service.startJadexPlatform(kernels, platformId);
	}

	public final IFuture<IExternalAccess> startJadexPlatform(String[] kernels, String platformId, String options)
	{
		return service.startJadexPlatform(kernels, platformId, options);
	}

	public void shutdownJadexPlatforms()
	{
		service.shutdownJadexPlatforms();
	}

	public void shutdownJadexPlatform(IComponentIdentifier platformId)
	{
		service.shutdownJadexPlatform(platformId);
	}

	public IFuture<IComponentIdentifier> startComponent(IComponentIdentifier platformId, String name, Class<?> clazz)
	{
		return service.startComponent(platformId, name, clazz);
	}

	public IFuture<IComponentIdentifier> startComponent(IComponentIdentifier platformId, String name, Class<?> clazz,
			CreationInfo creationInfo)
	{
		return service.startComponent(platformId, name, clazz, creationInfo);
	}

	public IFuture<IComponentIdentifier> startComponent(IComponentIdentifier platformId, String name, String modelPath)
	{
		return service.startComponent(platformId, name, modelPath);
	}

	public IFuture<IComponentIdentifier> startComponent(IComponentIdentifier platformId, String name, String modelPath,
			CreationInfo creationInfo)
	{
		return service.startComponent(platformId, name, modelPath, creationInfo);
	}

	public void shutdownJadexPlatform()
	{
		service.shutdownJadexPlatform();
	}

	public IFuture<IComponentIdentifier> startComponent(String name, String modelPath)
	{
		return service.startComponent(name, modelPath);
	}
	
	@Override
	public IFuture<IComponentIdentifier> startComponent(String name, String modelPath, CreationInfo creationInfo)
	{
		return service.startComponent(name, modelPath, creationInfo);
	}

	@Override
	public IFuture<IComponentIdentifier> startComponent(String name, Class<?> clazz, CreationInfo creationInfo)
	{
		return service.startComponent(name, clazz, creationInfo);
	}

	@Override
	public IFuture<IComponentIdentifier> startComponent(String name, Class<?> clazz)
	{
		return service.startComponent(name, clazz);
	}

	public IFuture<IComponentIdentifier> startMicroAgent(String name, Class<?> clazz)
	{
		return service.startMicroAgent(name, clazz);
	}

	public boolean isPlatformRunning()
	{
		return service.isPlatformRunning();
	}

	public IExternalAccess getExternalPlatformAccess(IComponentIdentifier platformId)
	{
		return service.getExternalPlatformAccess(platformId);
	}

	public IFuture<IComponentManagementService> getCMS(IComponentIdentifier platformId)
	{
		return service.getCMS(platformId);
	}

	public IFuture<IMessageService> getMS(IComponentIdentifier platformId)
	{
		return service.getMS(platformId);
	}

	public <S> S getsService(IComponentIdentifier platformId, Class<S> serviceClazz)
	{
		return service.getsService(platformId, serviceClazz);
	}

	public <S> IFuture<S> getService(IComponentIdentifier platformId, Class<S> serviceClazz)
	{
		return service.getService(platformId, serviceClazz);
	}

	public <S> IFuture<S> getService(IComponentIdentifier platformId, Class<S> serviceClazz, String scope)
	{
		return service.getService(platformId, serviceClazz, scope);
	}

	public String getPlatformName()
	{
		return service.getPlatformName();
	}

	public IComponentIdentifier getPlatformId()
	{
		return service.getPlatformId();
	}

	public void setPlatformId(IComponentIdentifier platformId)
	{
		service.setPlatformId(platformId);
	}

	public IFuture<IMessageService> getMS()
	{
		return service.getMS();
	}

	public IFuture<IComponentManagementService> getCMS()
	{
		return service.getCMS();
	}

	public IExternalAccess getExternalPlatformAccess()
	{
		return service.getExternalPlatformAccess();
	}

	public <S> S getsService(Class<S> serviceClazz)
	{
		return service.getsService(serviceClazz);
	}

	public <S> IFuture<S> getService(Class<S> serviceClazz)
	{
		return service.getService(serviceClazz);
	}

	public <S> IFuture<S> getService(Class<S> serviceClazz, String scope)
	{
		return service.getService(serviceClazz, scope);
	}

	public void registerEventReceiver(IEventReceiver<?> rec)
	{
		service.registerEventReceiver(rec);
	}

	public boolean unregisterEventReceiver(IEventReceiver<?> rec)
	{
		return service.unregisterEventReceiver(rec);
	}

	public boolean dispatchEvent(IJadexAndroidEvent event) throws WrongEventClassError
	{
		return service.dispatchEvent(event);
	}


}
