package jadex.android.service;

import java.util.Map;

import jadex.base.IPlatformConfiguration;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.ServiceScope;
import jadex.bridge.service.types.platform.IJadexMultiPlatformBinder;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import android.os.Binder;

/**
 * Provides access to the Platform service.
 */
public class JadexMultiPlatformBinder extends Binder implements IJadexMultiPlatformBinder
{
	//-------- attributes --------
	
	/** The Jadex platform service Holder */
	private IJadexMultiPlatformBinder service;

	/**
	 * Constructor
	 * @param service
	 */
	public JadexMultiPlatformBinder(IJadexMultiPlatformBinder service)
	{
		this.service = service;
	}
	
	public IExternalAccess getExternalPlatformAccess(IComponentIdentifier platformID)
	{
		return service.getExternalPlatformAccess(platformID);
	}

	public boolean isPlatformRunning(IComponentIdentifier platformID)
	{
		return service.isPlatformRunning(platformID);
	}

//	public IFuture<IComponentManagementService> getCMS(IComponentIdentifier platformID)
//	{
//		return service.getCMS(platformID);
//	}
	
	public void shutdownJadexPlatforms()
	{
		service.shutdownJadexPlatforms();
	}

	public void shutdownJadexPlatform(IComponentIdentifier platformID)
	{
		service.shutdownJadexPlatform(platformID);
	}

	public <S> S getsService(IComponentIdentifier platformId, Class<S> serviceClazz)
	{
		return service.getsService(platformId, serviceClazz);
	}

	public <S> IFuture<S> getService(IComponentIdentifier platformId, Class<S> serviceClazz)
	{
		return service.getService(platformId, serviceClazz);
	}

	public <S> IFuture<S> getService(IComponentIdentifier platformId, Class<S> serviceClazz, ServiceScope scope)
	{
		return service.getService(platformId, serviceClazz, scope);
	}
	
	
	@Override
	public IFuture<IComponentIdentifier> startComponent(IComponentIdentifier platformId, String name, String modelPath,
			CreationInfo creationInfo)
	{
		return service.startComponent(platformId, name, modelPath, creationInfo);
	}
	
	@Override
	public IFuture<IComponentIdentifier> startComponent(IComponentIdentifier platformId, String name, String modelPath, 
			CreationInfo creationInfo, IResultListener<Map<String,Object>> terminationListener)
	{
		return service.startComponent(platformId, name, modelPath, creationInfo, terminationListener);
	}

	@Override
	public IFuture<IComponentIdentifier> startComponent(IComponentIdentifier platformId, String name, Class<?> clazz,
			CreationInfo creationInfo)
	{
		return service.startComponent(platformId, name, clazz, creationInfo);
	}

	@Override
	public IFuture<IComponentIdentifier> startComponent(IComponentIdentifier platformId, String name, Class<?> clazz)
	{
		return service.startComponent(platformId, name, clazz);
	}

	public IFuture<IComponentIdentifier> startComponent(final IComponentIdentifier platformId, String name, String modelPath)
	{
		return service.startComponent(platformId, name, modelPath);
	}

	public IFuture<IExternalAccess> startJadexPlatform()
	{
		return service.startJadexPlatform();
	}

//	public IFuture<IExternalAccess> startJadexPlatform(String[] kernels)
//	{
//		return service.startJadexPlatform(kernels);
//	}
//
//	public IFuture<IExternalAccess> startJadexPlatform(String[] kernels, String platformId)
//	{
//		return service.startJadexPlatform(kernels, platformId);
//	}

	public IFuture<IExternalAccess> startJadexPlatform(IPlatformConfiguration config)
	{
		return service.startJadexPlatform(config);
	}

	public IResourceIdentifier getResourceIdentifier() {
		return service.getResourceIdentifier();
	}
	
	

}
