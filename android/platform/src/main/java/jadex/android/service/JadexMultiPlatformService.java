package jadex.android.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.IBinder;

import java.util.HashMap;
import java.util.Map;

import jadex.android.AndroidContextManager;
import jadex.android.commons.JadexPlatformOptions;
import jadex.android.commons.Logger;
import jadex.android.exception.JadexAndroidPlatformNotStartedError;
import jadex.base.IPlatformConfiguration;
import jadex.base.PlatformConfigurationHandler;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.platform.IJadexMultiPlatformBinder;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DefaultTuple2ResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ITuple2Future;

/**
 * Android Service to start/stop Jadex Platforms. Platforms are terminated on
 * destroy.
 * 
 * Every ClientApp has access to it's own instance of this Service.
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
		// TODO: check for shared platform
		new Thread() {
			@Override
			public void run() {
				jadexPlatformManager.shutdownJadexPlatforms();
				Context base = getBaseContext();
				if (base != null) {
					// if base is null, this service was not started through android
					AndroidContextManager.getInstance().setAndroidContext(null);
				}
			}
		}.start();
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
	
	public boolean isSharedPlatform()
	{
		return useSharedPlatform;
	}

	public void setSharedPlatform(boolean useSharedPlatform)
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

	public <S> S getsService(IComponentIdentifier platformId, Class<S> serviceClazz)
	{
		return jadexPlatformManager.getsService(platformId, serviceClazz);
	}

	public <S> IFuture<S> getService(IComponentIdentifier platformId, Class<S> serviceClazz)
	{
		return getService(platformId, serviceClazz, RequiredServiceInfo.SCOPE_PLATFORM);
	}

	public <S> IFuture<S> getService(IComponentIdentifier platformId, Class<S> serviceClazz, String scope)
	{
		return jadexPlatformManager.getService(platformId, serviceClazz, scope);
	}

//	@Override
//	public IFuture<IExternalAccess> startJadexPlatform(String[] kernels) {
//		IPlatformConfiguration config = PlatformConfigurationHandler.getDefault();
//		config.setKernels(kernels);
//		return startJadexPlatform(config);
//	}
//
//	@Override
//	public IFuture<IExternalAccess> startJadexPlatform(String[] kernels, String platformId) {
//		IPlatformConfiguration config = PlatformConfigurationHandler.getDefault();
//		config.setKernels(kernels);
//		config.setPlatformName(platformId);
//		return startJadexPlatform(config);
//	}

	public IFuture<IExternalAccess> startJadexPlatform() {
		return startJadexPlatform(PlatformConfigurationHandler.getAndroidDefault());
	}

	public IFuture<IExternalAccess> startJadexPlatform(IPlatformConfiguration config)
	{
		onPlatformStarting();
		final Future<IExternalAccess> ret = new Future<IExternalAccess>();
		
		IFuture<IExternalAccess> internalFut = null;
		if (useSharedPlatform) {
			Logger.i("Using shared Platform - options will be ignored!");
			Logger.e("Shared platform not supported in Jadex 3.0!");
			System.exit(1);
//			internalFut = jadexPlatformManager.startSharedJadexPlatform();
		} else {
			internalFut = jadexPlatformManager.startJadexPlatform(config);
		}
		
		internalFut.addResultListener(new DefaultResultListener<IExternalAccess>() {
			@Override
			public void resultAvailable(final IExternalAccess result) {
				// new thread to reset IComponentIdentifier.LOCAL which is set to the platform now
//				new Thread(new Runnable() {
//					@Override
//					public void run() {
//						ISuspendable.SUSPENDABLE.set(new ThreadSuspendable());
//						JadexMultiPlatformService.this.onPlatformStarted(result);
//						ret.setResult(result);
//					}
//				}).start();
				JadexMultiPlatformService.this.onPlatformStarted(result);
				ret.setResult(result);
			}
			
			@Override
			public void exceptionOccurred(Exception exception) {
				exception.printStackTrace();
				super.exceptionOccurred(exception);
			}
		});
		
		return ret;
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
	
	public IFuture<IComponentIdentifier> startComponent(final IComponentIdentifier platformId, final String name, final String modelPath, final CreationInfo creationInfo) {
		return startComponent(platformId, name, modelPath, creationInfo, null);
	}

	public IFuture<IComponentIdentifier> startComponent(final IComponentIdentifier platformId, final String name, final String modelPath, final CreationInfo creationInfo, final IResultListener<Map<String,Object>> terminationListener)
	{
		checkIfPlatformIsRunning(platformId, "startComponent()");
		Map<String, Object> arguments = creationInfo.getArguments();
		if (arguments == null) {
			arguments = new HashMap<String, Object>();
			creationInfo.setArguments(arguments);
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
			public void customResultAvailable(final IComponentManagementService cms)
			{
				ITuple2Future<IComponentIdentifier,Map<String,Object>> fut = cms.createComponent(name, modelPath, creationInfo);
				
				fut.addResultListener(new DefaultTuple2ResultListener<IComponentIdentifier, Map<String,Object>>() {

					IComponentIdentifier cid;
					@Override
					public void firstResultAvailable(final IComponentIdentifier cid) {
						this.cid = cid;
						// schedule to component to allow suspending
//						cms.getExternalAccess(cid).addResultListener(new  DefaultResultListener<IExternalAccess>()
//						{
//							public void resultAvailable(IExternalAccess access)
//							{
//								access.scheduleStep(new IComponentStep<Void>()
//								{
//
//									@Override
//									public IFuture<Void> execute(IInternalAccess ia)
//									{
//										ret.setResult(cid);
//										return Future.DONE;
//									}
//
//								});
//							}
//						});

						// new thread to reset IComponentIdentifier.LOCAL which is set to the platform now
//						new Thread(new Runnable() {
//							@Override
//							public void run() {
////								ISuspendable.SUSPENDABLE.set(new ThreadSuspendable());
//								ret.setResult(result);
//							}
//						}).start();

						ret.setResult(cid);
					}

					@Override
					public void secondResultAvailable(final Map<String, Object> result) {
						// occurs when execution is terminated.
//						new Thread(new Runnable() {
//							@Override
//							public void run() {
////								ISuspendable.SUSPENDABLE.set(new ThreadSuspendable());
//								if (terminationListener != null) {
//									terminationListener.resultAvailable(result);
//								}
//							}
//						}).start();
//						cms.getExternalAccess(cid).addResultListener(new  DefaultResultListener<IExternalAccess>()
//						{
//							public void resultAvailable(IExternalAccess access)
//							{
//								access.scheduleStep(new IComponentStep<Void>()
//								{
//
//									public IFuture<Void> execute(IInternalAccess ia)
//									{
//										if (terminationListener != null) {
//											terminationListener.resultAvailable(result);
//										}
//										return Future.DONE;
//									}
//
//								});
//							}
//						});
						if (terminationListener != null) {
							terminationListener.resultAvailable(result);
						}
					}

					@Override
					public void exceptionOccurred(Exception exception) {
						ret.setException(exception);
					}
				});
			}
		});

		return ret;
	}
	
	@Override
	public IResourceIdentifier getResourceIdentifier() {
		return jadexPlatformManager.getRID(appInfo.sourceDir);
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
	 * @param platform
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
