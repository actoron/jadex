package jadex.android.service;

import jadex.android.AndroidContextManager;
import jadex.android.commons.Logger;
import jadex.android.exception.JadexAndroidError;
import jadex.base.PlatformConfiguration;
import jadex.base.RootComponentConfiguration;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.library.ILibraryService;
import jadex.bridge.service.types.platform.IJadexPlatformManager;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ThreadSuspendable;
import jadex.commons.transformation.annotations.Classname;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

import android.util.Log;

/**
 * Singleton to manage all running jadex platforms and start new ones.
 */
public class JadexPlatformManager implements IJadexPlatformManager
{
	public static final String DEFAULT_OPTIONS = "-logging_level java.util.logging.Level.INFO" 
			+ " -extensions null" 
			//+ " -awareness false"
			+ " -wspublish false" + " -rspublish false" + " -android true" + " -binarymessages true"
			+ " -conf jadex.platform.PlatformAgent" +
			// " -tcptransport false" +
			// " -niotcptransport false" +
			// " -relaytransport true" +
			// " -relayaddress \"http://134.100.11.200:8080/jadex-platform-relay-web/\""
			// +
			" -autoshutdown false" + " -saveonexit true" + " -gui false" + " -chat false" + " -debugfutures true";
	
	private PlatformConfiguration defaultConfiguration;
	
	// --------- attributes -----------
	private Map<IComponentIdentifier, IExternalAccess> runningPlatforms;

	private Map<String,ClassLoader> platformClassLoaders;
	
	private Map<String, IResourceIdentifier> platformRIDs;
	
	private String defaultAppPath;
	
	private IExternalAccess sharedPlatformAccess;

	private static JadexPlatformManager instance = new JadexPlatformManager();

	public static JadexPlatformManager getInstance()
	{
		return instance;
	}

	private static Map<RootComponentConfiguration.KERNEL, String> kernelClassNames = new HashMap<RootComponentConfiguration.KERNEL, String>();

	static {
		kernelClassNames.put(RootComponentConfiguration.KERNEL.micro, "jadex.micro.MicroAgentFactory");
		kernelClassNames.put(RootComponentConfiguration.KERNEL.v3, "jadex.bdiv3.BDIAgentFactory");
		kernelClassNames.put(RootComponentConfiguration.KERNEL.bpmn, "jadex.bpmn.BpmnFactory");
		kernelClassNames.put(RootComponentConfiguration.KERNEL.component, "jadex.component.ComponentComponentFactory");
		kernelClassNames.put(RootComponentConfiguration.KERNEL.bdi, "jadex.bdiv3x.BDIXComponentFactory");
		kernelClassNames.put(RootComponentConfiguration.KERNEL.multi, "jadex.kernelbase.MultiFactory");
	}

	private JadexPlatformManager()
	{
		runningPlatforms = new HashMap<IComponentIdentifier, IExternalAccess>();
		platformClassLoaders = new HashMap<String, ClassLoader>();
		platformRIDs = new HashMap<String, IResourceIdentifier>();
		defaultConfiguration = createDefaultConfiguration();
		instance = this;
	}

	private PlatformConfiguration createDefaultConfiguration()
	{
		PlatformConfiguration config = new PlatformConfiguration();
		RootComponentConfiguration rootConfig = config.getRootConfig();
		rootConfig.setLoggingLevel(Level.INFO);
		rootConfig.setWsPublish(false);
		rootConfig.setRsPublish(false);
		rootConfig.setBinaryMessages(true);
		config.setConfigurationFile("jadex.platform.PlatformAgent");
		config.setAutoShutdown(false);
		rootConfig.setSaveOnExit(true);
		rootConfig.setGui(false);
		rootConfig.setChat(false);
		
		return config;
	}

	/**
	 * Returns the Jadex External Platform Access object for a given platform Id
	 * 
	 * @param platformID
	 * @return IExternalAccess
	 */
	public IExternalAccess getExternalPlatformAccess(IComponentIdentifier platformID)
	{
		return runningPlatforms.get(platformID);
	}

	/**
	 * Returns true if given jadex platform is running.
	 * 
	 */
	public boolean isPlatformRunning(IComponentIdentifier platformID)
	{
		return runningPlatforms.containsKey(platformID);
	}

	/**
	 * Sets a classLoader that will be used to load custom Jadex Components.
	 * 
	 * @param cl
	 */
	public void setAppClassLoader(final String appPath, ClassLoader cl)
	{
		platformClassLoaders.put(appPath, cl);
		defaultAppPath = appPath;
		if (sharedPlatformAccess != null) {
			// add classloader to shared platform
			addLibServiceUrl(sharedPlatformAccess, appPath);
		}
	}

	private IFuture<Void> addLibServiceUrl(IExternalAccess platformAccess, final String appPath)
	{
		final Future<Void> result = new Future<Void>();
		IFuture<ILibraryService> libService = getService(platformAccess.getComponentIdentifier(), ILibraryService.class);
		Logger.d("Getting LibraryService...");
		libService.addResultListener(new DefaultResultListener<ILibraryService>()
		{

			@Override
			public void resultAvailable(ILibraryService libService)
			{
				try
				{
					Logger.d("Found Libservice. Adding Ressource / Classloader for App: " + appPath);
					URL url = SUtil.androidUtils().urlFromApkPath(appPath);
//						libService.addTopLevelURL(url);
					IFuture<IResourceIdentifier> addURL = libService.addURL(null, url);
					addURL.addResultListener(new DefaultResultListener<IResourceIdentifier>()
					{

						@Override
						public void resultAvailable(IResourceIdentifier rid)
						{
							Logger.d("Got back RID: " + rid.toString());
							platformRIDs.put(appPath, rid);
							result.setResult(null);
						}
					});
				}
				catch (MalformedURLException e)
				{
					e.printStackTrace();
					result.setException(e);
				}
			}
		});
		return result;
	}
	
	public ClassLoader getClassLoader(String apkPath)
	{
		if (apkPath == null) {
			apkPath = defaultAppPath;
		}
		return platformClassLoaders.get(apkPath);
	}
	
	public IResourceIdentifier getRID(String apkPath) {
		IResourceIdentifier rid = platformRIDs.get(apkPath);
		if (rid == null) {
			Logger.e("PlatformManager: returning null RID!");
		}
		return rid;
	}
	
	/**
	 * Looks up a service and returns it synchronously.
	 * 
	 * @param platformId Id of the platform to use for lookup
	 * @param serviceClazz Class of the service (interface) to find
	 * @return the service
	 */
	public <S> S getsService(IComponentIdentifier platformId, final Class<S> serviceClazz) {
//		ThreadSuspendable caller = new ThreadSuspendable();
		IFuture<S> fut = getService(platformId, serviceClazz);
		S s = fut.get();
		return s;
	}
	
	/**
	 * Looks up a service using RequiredServiceInfo.SCOPE_PLATFORM as scope.
	 * 
	 * @see getsService
	 * 
	 * @param platformId Id of the platform to use for lookup
	 * @param serviceClazz Class of the service (interface) to find
	 * @return Future of the service.
	 */
	public <S> IFuture<S> getService(IComponentIdentifier platformId, final Class<S> serviceClazz) {
		return getService(platformId, serviceClazz, RequiredServiceInfo.SCOPE_PLATFORM);
	}
	
	/**
	 * Looks up a service.
	 * 
	 * @see getsService
	 * 
	 * @param platformId Id of the platform to use for lookup
	 * @param serviceClazz Class of the service (interface) to find
	 * @param scope Search scope. See {@link RequiredServiceInfo} constants.
	 * @return Future of the service.
	 */
	public <S> IFuture<S> getService(IComponentIdentifier platformId, final Class<S> serviceClazz, final String scope) {
		return getExternalPlatformAccess(platformId).scheduleStep(new IComponentStep<S>() {
			@Classname("create-component")
			public IFuture<S> execute(IInternalAccess ia) {
				Future<S> ret = new Future<S>();
				SServiceProvider
						.getService(ia, serviceClazz, scope)
						.addResultListener(ia.getComponentFeature(IExecutionFeature.class).createResultListener(new DelegationResultListener<S>(ret)));

				return ret;
			}
		});
	}
	
	
	public IFuture<IExternalAccess> startSharedJadexPlatform() {
		final Future<IExternalAccess> result = new Future<IExternalAccess>();
		
		if (sharedPlatformAccess != null) {
			Logger.i("Found running platform - using it.");
			result.setResult(sharedPlatformAccess);
		} else {
			Logger.i("Starting up new shared platform.");
			startJadexPlatform(ALL_KERNELS, getRandomPlatformName(), DEFAULT_OPTIONS).addResultListener(new DefaultResultListener<IExternalAccess>()
			{
				@Override
				public void resultAvailable(IExternalAccess ea)
				{
					sharedPlatformAccess = ea;
					result.setResult(sharedPlatformAccess);
				}
			});
		}
		
		return result;
		
	}

	public IFuture<IExternalAccess> startJadexPlatform(final String[] kernels, final String platformName, final String options)
	{
		return startJadexPlatform(kernels, platformName, options, true);
	}

	public synchronized IFuture<IExternalAccess> startJadexPlatform(final String[] kernels, final String platformName, final String options,
			final boolean chat)
	{
		final Future<IExternalAccess> ret = new Future<IExternalAccess>();
		new Thread(new Runnable()
		{
			public void run()
			{

				checkKernels(kernels);

				final StringBuffer kernelString = new StringBuffer("\"");
				String sep = "";
				for (int i = 0; i < kernels.length; i++)
				{
					kernelString.append(sep);
					kernelString.append(kernels[i]);
					sep = ",";
				}
				kernelString.append("\"");

				Logger.i("Used kernels: " + kernelString.toString());

				final String usedOptions = DEFAULT_OPTIONS + " -kernels " + kernelString.toString() + " -platformname "
						+ (platformName != null ? platformName : getRandomPlatformName()) + (options == null ? "" : " " + options);

				IFuture<IExternalAccess> future = createPlatformWithClassloader((usedOptions).split("\\s+"), this.getClass().getClassLoader());
				Logger.d("Waiting for platform startup...");
				
				future.addResultListener(new IResultListener<IExternalAccess>()
				{
					public void resultAvailable(final IExternalAccess platformAccess)
					{
						runningPlatforms.put(platformAccess.getComponentIdentifier(), platformAccess);
						if (defaultAppPath != null) {
							Logger.d("Platform started in multi-app mode, now adding lib url...");
							addLibServiceUrl(platformAccess, defaultAppPath).addResultListener(new DefaultResultListener<Void>()
							{
	
								@Override
								public void resultAvailable(Void result)
								{
									ret.setResult(platformAccess);
								}
							});
						} else {
							// no rid handling necessary if platform is embedded in app.
							Logger.d("Platform started in embedded mode.");
							ret.setResult(platformAccess);
						}
						
					}

					public void exceptionOccurred(Exception exception)
					{
						ret.setException(exception);
					}
				});
			}
		}).start();

		// if (chat) {
		// ret.addResultListener(new DefaultResultListener<IExternalAccess>()
		// {
		//
		// @Override
		// public void resultAvailable(IExternalAccess result)
		// {
		// JadexChat jadexChat = new JadexChat(result);
		// jadexChat.start();
		// }
		//
		// });
		// }

		return ret;
	}

	private void checkKernels(String[] kernels) {
		// make sure default kernels all exist in classpath
		ArrayList<String> kernelList = new ArrayList<String>();
		ClassLoader myCL = getClass().getClassLoader();

		for (String kernel : kernels) {
			RootComponentConfiguration.KERNEL k = RootComponentConfiguration.KERNEL.valueOf(kernel);
			String className = kernelClassNames.get(k);
			boolean found = false;
			if (className != null) {
				Class<?> clazz = SReflect.classForName0(className, myCL);
				if (clazz != null) {
					found = true;
				}
			}
			if (!found) {
				throw new JadexAndroidError("Could not find factory for requested kernel: " + kernel);
			}
		}
	}

	@SuppressWarnings("unchecked")
	protected static IFuture<IExternalAccess> createPlatformWithClassloader(String[] options, ClassLoader cl)
	{
		try
		{
			Class<?> starter = cl.loadClass("jadex.base.Starter");
			Method createPlatform = starter.getMethod("createPlatform", String[].class); // =
																							// Starter.createPlatform();
			return (IFuture<IExternalAccess>) createPlatform.invoke(null, (Object) options);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Terminates all running jadex platforms.
	 */
	public synchronized void shutdownJadexPlatforms()
	{
		Log.d("jadex-android", "Shutting down all platforms");
		IComponentIdentifier[] platformIds = runningPlatforms.keySet().toArray(new IComponentIdentifier[runningPlatforms.size()]);
		for (IComponentIdentifier platformId : platformIds)
		{
			shutdownJadexPlatform(platformId);
		}
		sharedPlatformAccess = null;
	}

	/**
	 * Terminates the running jadex platform with the given ID.
	 * 
	 * @param platformID
	 *            Platform to terminate.
	 */
	public synchronized void shutdownJadexPlatform(IComponentIdentifier platformID)
	{
		Log.d("jadex-android", "Starting platform shutdown: " + platformID.toString());
		// long start = System.currentTimeMillis();
		// long timeout = 4500;
		runningPlatforms.get(platformID).killComponent().get();
		runningPlatforms.remove(platformID);
		Log.d("jadex-android", "Platform shutdown completed: " + platformID.toString());
	}

	public String getRandomPlatformName()
	{
		StringBuilder sb = new StringBuilder(AndroidContextManager.getInstance().getUniqueDeviceName());
		UUID randomUUID = UUID.randomUUID();
		sb.append("_");
		sb.append(randomUUID.toString().substring(0, 3));
		return sb.toString();
	}

}
