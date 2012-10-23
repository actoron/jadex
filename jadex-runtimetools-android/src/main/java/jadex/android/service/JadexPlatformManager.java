package jadex.android.service;

import jadex.android.AndroidContextManager;
import jadex.android.IEventReceiver;
import jadex.android.exception.JadexAndroidPlatformNotStartedError;
import jadex.android.exception.WrongEventClassException;
import jadex.base.Starter;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.context.IJadexAndroidEvent;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ThreadSuspendable;
import jadex.commons.transformation.annotations.Classname;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import android.util.Log;

/**
 * Singleton to manage all running jadex platforms and start new ones.
 */
public class JadexPlatformManager
{

	// --------- static fields -----------
	public static final String KERNEL_COMPONENT = "component";
	public static final String KERNEL_MICRO = "micro";
	public static final String KERNEL_BPMN = "bpmn";
	public static final String KERNEL_BDI = "bdi";

	public static final String[] DEFAULT_KERNELS = new String[]
	{ KERNEL_COMPONENT, KERNEL_MICRO, KERNEL_BPMN };

	// --------- attributes -----------
	private Map<IComponentIdentifier, IExternalAccess> runningPlatforms;
	
	private static JadexPlatformManager instance = new JadexPlatformManager();
	
	public static JadexPlatformManager getInstance() {
		return instance;
	}

	private JadexPlatformManager()
	{
		runningPlatforms = new HashMap<IComponentIdentifier, IExternalAccess>();
		instance = this;
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
	 * Retrieves the CMS of the Platform with the given ID.
	 */
	public IFuture<IComponentManagementService> getCMS(IComponentIdentifier platformID)
	{
		return getExternalPlatformAccess(platformID).scheduleStep(new IComponentStep<IComponentManagementService>()
		{
			@Classname("create-component")
			public IFuture<IComponentManagementService> execute(IInternalAccess ia)
			{
				Future<IComponentManagementService> ret = new Future<IComponentManagementService>();
				SServiceProvider.getService(ia.getServiceContainer(), IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM).addResultListener(
						ia.createResultListener(new DelegationResultListener<IComponentManagementService>(ret)));

				return ret;
			}
		});
	}

//	public IFuture<IExternalAccess> startJadexPlatform()
//	{
//		return startJadexPlatform(DEFAULT_KERNELS);
//	}
//
//	public IFuture<IExternalAccess> startJadexPlatform(final String[] kernels)
//	{
//		return startJadexPlatform(kernels, getRandomPlatformID());
//	}
//
//	public IFuture<IExternalAccess> startJadexPlatform(final String[] kernels, final String platformId)
//	{
//		return startJadexPlatform(kernels, platformId, "");
//	}
	
	public IFuture<IExternalAccess> startJadexPlatform(final String[] kernels, final String platformId, final String options) {
		return startJadexPlatform(kernels, platformId, options, true);
	}

	public synchronized IFuture<IExternalAccess> startJadexPlatform(final String[] kernels, final String platformId, final String options, final boolean chat)
	{
		final Future<IExternalAccess> ret = new Future<IExternalAccess>();
		new Thread(new Runnable()
		{
			public void run()
			{
				final StringBuffer kernelString = new StringBuffer("\"");
				String sep = "";
				for (int i = 0; i < kernels.length; i++)
				{
					kernelString.append(sep);
					kernelString.append(kernels[i]);
					sep = ",";
				}
				kernelString.append("\"");
				
				final String defOptions = "-logging_level java.util.logging.Level.INFO" + " -extensions null" + " -awareness false" + " -wspublish false" + " -rspublish false" + " -android true" + " -kernels "
						+ kernelString.toString() + " -binarymessages true" +
						" -conf jadex.platform.PlatformAgent" +
						// " -tcptransport false" +
						// " -niotcptransport false" +
						// " -relaytransport true" +
						// " -relayaddress \"http://134.100.11.200:8080/jadex-platform-relay-web/\""
						// +
						// " -saveonexit false -gui false" +
						" -autoshutdown false" + " -platformname " + (platformId != null ? platformId : getRandomPlatformID()) + " -saveonexit true -gui false" + " ";
				
				IFuture<IExternalAccess> future = Starter.createPlatform((defOptions + options).split("\\s+"));
				future.addResultListener(new IResultListener<IExternalAccess>()
				{
					public void resultAvailable(IExternalAccess result)
					{
						runningPlatforms.put(result.getComponentIdentifier(), result);
						ret.setResult(result);
					}

					public void exceptionOccurred(Exception exception)
					{
						ret.setException(exception);
					}
				});
			}
		}).start();
		
//		if (chat) {
//			ret.addResultListener(new DefaultResultListener<IExternalAccess>()
//			{
//
//				@Override
//				public void resultAvailable(IExternalAccess result)
//				{
//					JadexChat jadexChat = new JadexChat(result);
//					jadexChat.start();
//				}
//				
//			});
//		}

		return ret;
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
		ThreadSuspendable sus = new ThreadSuspendable();
//		long start = System.currentTimeMillis();
//		long timeout = 4500;
		runningPlatforms.get(platformID).killComponent().get(sus);
		runningPlatforms.remove(platformID);
		Log.d("jadex-android", "Platform shutdown completed: " + platformID.toString());
	}
	
	public String getRandomPlatformID()
	{
		StringBuilder sb = new StringBuilder(AndroidContextManager.getInstance().getUniqueDeviceName());
		UUID randomUUID = UUID.randomUUID();
		sb.append("_");
		sb.append(randomUUID.toString().substring(0, 3));
		return sb.toString();
	}
	
	public IComponentIdentifier getFirstRunningPlatformId() {
		Set<IComponentIdentifier> platformIds = runningPlatforms.keySet();
		if (!platformIds.isEmpty()) {
			IComponentIdentifier next = platformIds.iterator().next();
			return next;
		} else {
			throw new JadexAndroidPlatformNotStartedError("getCMS()");
		}
	}
}
