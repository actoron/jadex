package jadex.android.service;

import jadex.android.AndroidContextManager;
import jadex.android.commons.JadexPlatformOptions;
import jadex.android.commons.Logger;
import jadex.android.exception.JadexAndroidPlatformNotStartedError;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.message.IMessageService;
import jadex.bridge.service.types.platform.IJadexMultiPlatformBinder;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

import java.util.HashMap;
import java.util.Map;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.IBinder;
import android.util.Log;

/**
 * Android Service to start/stop Jadex Platforms. Platforms are terminated on
 * destroy.
 */
public class JadexMultiPlatformService extends Service implements IJadexMultiPlatformBinder, JadexPlatformOptions
{
	
	protected JadexPlatformManager jadexPlatformManager;
	
	/** Indicates whether to use a shared Platform for all Clients */
	private boolean useSharedPlatform;
	
	/** Info from the application this service belongs to */
	private ApplicationInfo appInfo;
	
	public JadexMultiPlatformService()
	{
		jadexPlatformManager = JadexPlatformManager.getInstance();
	}

	@Override
	public IBinder onBind(Intent intent)
	{
		return new JadexMultiPlatformBinder(this);
	}
	
	@Override
	public void onCreate()
	{
		super.onCreate();
		Context base = this.getBaseContext();
		if (base != null) {
			// if base is null, this service was not started through android
			AndroidContextManager.getInstance().setAndroidContext(base);
		}
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		jadexPlatformManager.shutdownJadexPlatforms();
		Context base = this.getBaseContext();
		if (base != null) {
			// if base is null, this service was not started through android
			AndroidContextManager.getInstance().setAndroidContext(null);
		}
	}
	
	@Override
	public void attachBaseContext(Context baseContext)
	{
		super.attachBaseContext(baseContext);
	}
	
	public void setApplicationInfo(ApplicationInfo appInfo)
	{
		this.appInfo = appInfo;
	}
	
	public boolean isUseSharedPlatform()
	{
		return useSharedPlatform;
	}

	public void setUseSharedPlatform(boolean useSharedPlatform)
	{
		this.useSharedPlatform = useSharedPlatform;
	}

	/**
	 * @deprecated use getExternalPlatformAccess()
	 */
	public IExternalAccess getPlatformAccess(IComponentIdentifier platformId) {
		return getExternalPlatformAccess(platformId);
	}
	
	public IExternalAccess getExternalPlatformAccess(IComponentIdentifier platformId) {
		checkIfPlatformIsRunning(platformId, "getPlatformAccess");
		return jadexPlatformManager.getExternalPlatformAccess(platformId);
	}

	
	public boolean isPlatformRunning(IComponentIdentifier platformId)
	{
		return jadexPlatformManager.isPlatformRunning(platformId);
	}

	public IFuture<IComponentManagementService> getCMS(IComponentIdentifier platformId)
	{
		return getService(platformId, IComponentManagementService.class);
	}

	public IFuture<IMessageService> getMS(IComponentIdentifier platformId)
	{
		return getService(platformId, IMessageService.class);
	}

	public <S> S getsService(IComponentIdentifier platformId, Class<S> serviceClazz)
	{
		return jadexPlatformManager.getsService(platformId, serviceClazz);
	}

	public <S> IFuture<S> getService(IComponentIdentifier platformId, Class<S> serviceClazz)
	{
		return jadexPlatformManager.getService(platformId, serviceClazz);
	}

	public <S> IFuture<S> getService(IComponentIdentifier platformId, Class<S> serviceClazz, String scope)
	{
		return jadexPlatformManager.getService(platformId, serviceClazz, scope);
	}

	public IFuture<IExternalAccess> startJadexPlatform()
	{
		return startJadexPlatform(DEFAULT_KERNELS);
	}

	public final IFuture<IExternalAccess> startJadexPlatform(String[] kernels)
	{
		return startJadexPlatform(kernels, jadexPlatformManager.getRandomPlatformName());
	}

	public final IFuture<IExternalAccess> startJadexPlatform(String[] kernels, String platformId)
	{
		return startJadexPlatform(kernels, platformId, null);
	}

	public IFuture<IExternalAccess> startJadexPlatform(String[] kernels, String platformId, String options)
	{
		onPlatformStarting();
		IFuture<IExternalAccess> fut;
		if (useSharedPlatform) {
			Logger.i("Using shared Platform - options will be ignored!");
			fut = jadexPlatformManager.startSharedJadexPlatform();
		} else {
			fut = jadexPlatformManager.startJadexPlatform(kernels, platformId, options);
		}
		fut.addResultListener(new DefaultResultListener<IExternalAccess>()
		{
			public void resultAvailable(IExternalAccess result)
			{
				JadexMultiPlatformService.this.onPlatformStarted(result);
			}
			
			public void exceptionOccurred(Exception exception)
			{
				exception.printStackTrace();
				super.exceptionOccurred(exception);
			}
		});
		return fut;
	}

	/**
	 * @deprecated use shutdownJadexPlatforms()
	 */
	public void stopPlatforms() {
		shutdownJadexPlatforms();
	}
	
	public void shutdownJadexPlatforms()
	{
		jadexPlatformManager.shutdownJadexPlatforms();
	}

	public void shutdownJadexPlatform(IComponentIdentifier platformId)
	{
		jadexPlatformManager.shutdownJadexPlatform(platformId);
	}

	public IFuture<IComponentIdentifier> startComponent(final IComponentIdentifier platformId, final String name, final Class<?> clazz)
	{
		return startComponent(platformId, name, clazz, new CreationInfo());
	}

	public IFuture<IComponentIdentifier> startComponent(final IComponentIdentifier platformId, final String name, final Class<?> clazz, final CreationInfo creationInfo)
	{
		String modelPath = clazz.getName().replaceAll("\\.", "/") + ".class";
		return startComponent(platformId, name, modelPath, creationInfo);
	}

	public IFuture<IComponentIdentifier> startComponent(final IComponentIdentifier platformId, final String name, final String modelPath)
	{
		return startComponent(platformId, name, modelPath, new CreationInfo());
	}

	public IFuture<IComponentIdentifier> startComponent(final IComponentIdentifier platformId, final String name, final String modelPath, final CreationInfo creationInfo)
	{
		checkIfPlatformIsRunning(platformId, "startComponent()");
		Map<String, Object> arguments = creationInfo.getArguments();
		if (arguments == null) {
			arguments = new HashMap<String, Object>();
			creationInfo.setArguments(arguments);
		}
		if (!arguments.containsKey("androidContext")) {
			arguments.put("androidContext", this);
		}
		
		// Add RID to show jadex the class location
		IResourceIdentifier rid = creationInfo.getResourceIdentifier();
		if (rid == null && appInfo != null) {
			// only try to get RID in multi-app mode where we have an ApplicationInfo
			rid = jadexPlatformManager.getRID(appInfo.sourceDir);
			Logger.d("Setting RID before starting Component: " + rid);
			creationInfo.setResourceIdentifier(rid);
		}
		
		final Future<IComponentIdentifier> ret = new Future<IComponentIdentifier>();
		getCMS(platformId)
			.addResultListener(new ExceptionDelegationResultListener<IComponentManagementService, IComponentIdentifier>(ret)
		{
			public void customResultAvailable(IComponentManagementService cms)
			{
				cms.createComponent(name, modelPath, creationInfo, null)
					.addResultListener(new DelegationResultListener<IComponentIdentifier>(ret));
			}
		});

		return ret;
	}
	
	
	/**
	 * Called right before the platform startup.
	 */
	protected void onPlatformStarting()
	{
	}
	
	/**
	 * Called right after the platform is started.
	 * 
	 * @param result
	 *            The external access to the platform
	 */
	protected void onPlatformStarted(IExternalAccess platform)
	{
	}

	
	// -------------- helper -------------------
	
	protected void checkIfPlatformIsRunning(final IComponentIdentifier platformId, String caller)
	{
		if (!isPlatformRunning(platformId))
		{
			throw new JadexAndroidPlatformNotStartedError(caller);
		}
	}
	

}
