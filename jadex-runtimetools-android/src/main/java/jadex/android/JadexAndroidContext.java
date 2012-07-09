package jadex.android;

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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import android.content.Context;
import android.util.Log;

public class JadexAndroidContext extends AndroidContext
{

	private static final JadexAndroidContext instance = new JadexAndroidContext();

	// --------- static fields -----------
	public static final String KERNEL_COMPONENT = "component";
	public static final String KERNEL_MICRO = "micro";
	public static final String KERNEL_BPMN = "bpmn";
	public static final String KERNEL_BDI = "bdi";

	public static final String[] DEFAULT_KERNELS = new String[]
	{ KERNEL_COMPONENT, KERNEL_MICRO, KERNEL_BPMN };

	// --------- attributes -----------
	private Map<IComponentIdentifier, IExternalAccess> runningPlatforms;

	private Map<String, List<IEventReceiver<?>>> eventReceivers;

	/**
	 * Listener Interface
	 */
	public interface AndroidContextChangeListener
	{
		/**
		 * Called when an Android Context is destroyed
		 * 
		 * @param ctx
		 */
		void onContextDestroy(Context ctx);

		/**
		 * Called when an Android Context is created
		 * 
		 * @param ctx
		 */
		void onContextCreate(Context ctx);
	}

	/**
	 * Private Constructor - Singleton
	 */
	private JadexAndroidContext()
	{
		runningPlatforms = new HashMap<IComponentIdentifier, IExternalAccess>();
		eventReceivers = new HashMap<String, List<IEventReceiver<?>>>();
	}

	/**
	 * Returns the Instance of this JadexAndroidContext
	 * 
	 * @return
	 */
	public static JadexAndroidContext getInstance()
	{
		return instance;
	}

	/**
	 * Sets the External Access for the Jadex Platform
	 * 
	 * @param extAcc
	 *            IExternalAccess
	 */
	private void setExternalPlattformAccess(IComponentIdentifier platformID, IExternalAccess extAcc)
	{
		runningPlatforms.put(platformID, extAcc);
	}

	/**
	 * Returns the Jadex External Platform Access object for a random running platform
	 * 
	 * @param platformID
	 * @return IExternalAccess
	 */
	public IExternalAccess getExternalPlatformAccess()
	{
		return runningPlatforms.get(getFirstRunningPlatformId());
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
	 * Returns true if ANY jadex platform is running.
	 * 
	 * @return
	 */
	public boolean isPlatformRunning()
	{
		return !runningPlatforms.isEmpty();
	}

	/**
	 * Returns true if given jadex platform is running.
	 * 
	 * @param platformID
	 * @return
	 */
	public boolean isPlatformRunning(IComponentIdentifier platformID)
	{
		return runningPlatforms.containsKey(platformID);
	}

	

	public void registerEventListener(String eventName, IEventReceiver<?> rec)
	{
		List<IEventReceiver<?>> receivers = this.eventReceivers.get(eventName);
		if (receivers == null)
		{
			receivers = new ArrayList<IEventReceiver<?>>();
			eventReceivers.put(eventName, receivers);
		}
		receivers.add(rec);
	}

	public boolean dispatchEvent(IJadexAndroidEvent event) throws WrongEventClassException
	{
		boolean result = false;
		List<IEventReceiver<?>> list = eventReceivers.get(event.getType());
		if (list != null)
		{
			for (IEventReceiver<?> eventReceiver : list)
			{
				Class<?> eventClass = eventReceiver.getEventClass();
				if (eventClass.equals(event.getClass()))
				{
					try
					{
						Method method = eventReceiver.getClass().getMethod("receiveEvent", eventClass);
						method.invoke(eventReceiver, eventClass.cast(event));
					} catch (SecurityException e)
					{
						e.printStackTrace();
					} catch (NoSuchMethodException e)
					{
						e.printStackTrace();
					} catch (IllegalArgumentException e)
					{
						e.printStackTrace();
					} catch (IllegalAccessException e)
					{
						e.printStackTrace();
					} catch (InvocationTargetException e)
					{
						e.printStackTrace();
					}
					result = true;
				} else
				{
					throw new WrongEventClassException(eventReceiver.getEventClass(), event.getClass(), "");
				}
			}
		}
		return result;
	}

	public boolean unregisterEventListener(String eventName, IEventReceiver<?> rec)
	{
		boolean removed = false;
		List<IEventReceiver<?>> list = eventReceivers.get(eventName);
		if (list != null)
		{
			removed = list.remove(rec);
		}
		return removed;
	}
	
	/**
	 * Retrieves the CMS of a random running platform.
	 * @param platformID
	 * @return
	 */
	public IFuture<IComponentManagementService> getCMS() {
		return getCMS(getFirstRunningPlatformId());
	}

	/**
	 * Retrieves the CMS of the Platform with the given ID.
	 * @param platformID
	 * @return
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

	public IFuture<IExternalAccess> startJadexPlatform()
	{
		return startJadexPlatform(DEFAULT_KERNELS);
	}

	public IFuture<IExternalAccess> startJadexPlatform(final String[] kernels)
	{
		return startJadexPlatform(kernels, getRandomPlatformID());
	}

	public IFuture<IExternalAccess> startJadexPlatform(final String[] kernels, final String platformId)
	{
		return startJadexPlatform(kernels, platformId, "");

	}

	public synchronized IFuture<IExternalAccess> startJadexPlatform(final String[] kernels, final String platformId, final String options)
	{
		// if (isJadexRunning()){
		// throw new JadexAndroidError("Platform was already started!");
		// }

		final Future<IExternalAccess> ret = new Future<IExternalAccess>();

		final StringBuffer kernelString = new StringBuffer("\"");
		String sep = "";
		for (int i = 0; i < kernels.length; i++)
		{
			kernelString.append(sep);
			kernelString.append(kernels[i]);
			sep = ",";
		}
		kernelString.append("\"");

		final String defOptions = "-logging_level java.util.logging.Level.INFO" + " -extensions null" + " -wspublish false" + " -android true" + " -kernels "
				+ kernelString.toString() + " -binarymessages true" +
				// " -tcptransport false" +
				// " -niotcptransport false" +
				// " -relaytransport true" +
				// " -relayaddress \"http://134.100.11.200:8080/jadex-platform-relay-web/\""
				// +
				// " -saveonexit false -gui false" +
				" -autoshutdown false" + " -platformname " + platformId + " -saveonexit true -gui false" + " ";

		new Thread(new Runnable()
		{
			public void run()
			{
				IFuture<IExternalAccess> future = Starter.createPlatform((defOptions + options).split("\\s+"));
				future.addResultListener(new IResultListener<IExternalAccess>()
				{

					@Override
					public void resultAvailable(IExternalAccess result)
					{
						setExternalPlattformAccess(result.getComponentIdentifier(), result);
						ret.setResult(result);
					}

					@Override
					public void exceptionOccurred(Exception exception)
					{
						ret.setException(exception);
					}
				});
			}
		}).start();

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
		setExternalPlattformAccess(platformID, null);
		runningPlatforms.remove(platformID);
		Log.d("jadex-android", "Platform shutdown completed: " + platformID.toString());
	}
	
	protected String getRandomPlatformID()
	{
		StringBuilder sb = new StringBuilder(getUniqueDeviceName());
		UUID randomUUID = UUID.randomUUID();
		sb.append("_");
		sb.append(randomUUID.toString().substring(0, 3));
		return sb.toString();
	}
	
	private IComponentIdentifier getFirstRunningPlatformId() {
		Set<IComponentIdentifier> platformIds = runningPlatforms.keySet();
		if (!platformIds.isEmpty()) {
			IComponentIdentifier next = platformIds.iterator().next();
			return next;
		} else {
			throw new JadexAndroidPlatformNotStartedError("getCMS()");
		}
	}
}
