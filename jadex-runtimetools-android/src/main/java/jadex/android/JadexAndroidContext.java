package jadex.android;

import jadex.android.exception.JadexAndroidError;
import jadex.android.exception.WrongEventClassException;
import jadex.base.Starter;
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

public class JadexAndroidContext {

	private static final JadexAndroidContext instance = new JadexAndroidContext();

	// --------- static fields -----------
	public static final String KERNEL_COMPONENT = "component";
	public static final String KERNEL_MICRO = "micro";
	public static final String KERNEL_BPMN = "bpmn";
	public static final String KERNEL_BDI = "bdi";
	
	public static final String[] DEFAULT_KERNELS = new String[]{KERNEL_COMPONENT, KERNEL_MICRO, KERNEL_BPMN};
	private static Set<String> identifiers;
	
	static {
		identifiers = new HashSet<String>();
		identifiers.add(android.os.Build.BRAND.toLowerCase());
		identifiers.add(android.os.Build.MANUFACTURER.toLowerCase());
		if (!android.os.Build.MANUFACTURER.equals(android.os.Build.UNKNOWN)) {
			// stupid full text on x86 emulator, so skip it if emulated
			identifiers.add(android.os.Build.MODEL.toLowerCase());
			identifiers.add(android.os.Build.DEVICE.toLowerCase());
		}
		identifiers.add(android.os.Build.PRODUCT.toLowerCase());
		identifiers.add(android.os.Build.BOARD.toLowerCase());
	}

	// --------- attributes -----------
	private IExternalAccess extAcc;
	private Context lastContext;
	
	private List<AndroidContextChangeListener> contextListeners;
	private Map<String, List<IEventReceiver<?>>> eventReceivers;
	
	/**
	 * Listener Interface
	 */
	public interface AndroidContextChangeListener {
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
	private JadexAndroidContext() {
		contextListeners = new ArrayList<AndroidContextChangeListener>();
		eventReceivers = new HashMap<String, List<IEventReceiver<?>>>();
	}

	/**
	 * Returns the Instance of this JadexAndroidContext
	 * 
	 * @return
	 */
	public static JadexAndroidContext getInstance() {
		return instance;
	}

	/**
	 * Sets the External Access for the Jadex Platform
	 * 
	 * @param extAcc
	 *            IExternalAccess
	 */
	public void setExternalPlattformAccess(IExternalAccess extAcc) {
		this.extAcc = extAcc;
	}

	/**
	 * Returns the Jadex External Platfrom Access object
	 * 
	 * @return IExternalAccess
	 */
	public IExternalAccess getExternalPlattformAccess() {
		return this.extAcc;
	}

	public boolean isJadexRunning() {
		return this.extAcc != null;
	}

	/**
	 * Sets a new Android Application Context. Pass <code>null</code> to unset
	 * the previous application context.
	 * 
	 * @param contextProvidingActivity
	 */
	public void setAndroidContext(Context contextProvidingActivity) {
		if (contextProvidingActivity == null) {
			informContextDestroy(lastContext);
		} else {
			informContextCreate(lastContext);
		}
		lastContext = contextProvidingActivity;
	}

	/**
	 * Returns the last known Android Context or <code>null</code>
	 * 
	 * @return Context
	 */
	public Context getAndroidContext() {
		return lastContext;
	}

	/**
	 * Adds a new Context Change Listener
	 * 
	 * @param l
	 */
	public void addContextChangeListener(AndroidContextChangeListener l) {
		contextListeners.add(l);
		if (lastContext != null) {
			l.onContextCreate(lastContext);
		}
	}

	/**
	 * Removes a Context Change Listener
	 * 
	 * @param l
	 */
	public void removeContextChangeListener(AndroidContextChangeListener l) {
		contextListeners.remove(l);
		l.onContextDestroy(lastContext);
	}

	private void informContextDestroy(Context ctx) {
		synchronized (contextListeners) {
			for (AndroidContextChangeListener l : contextListeners) {
				l.onContextDestroy(ctx);
			}
		}
	}

	private void informContextCreate(Context ctx) {
		synchronized (contextListeners) {
			for (AndroidContextChangeListener l : contextListeners) {
				l.onContextCreate(ctx);
			}
		}
	}

	public void registerEventListener(String eventName, IEventReceiver<?> rec) {
		List<IEventReceiver<?>> receivers = this.eventReceivers.get(eventName);
		if (receivers == null) {
			receivers = new ArrayList<IEventReceiver<?>>();
			eventReceivers.put(eventName, receivers);
		}
		receivers.add(rec);
	}

	public boolean dispatchEvent(IJadexAndroidEvent event)
			throws WrongEventClassException {
		boolean result = false;
		List<IEventReceiver<?>> list = eventReceivers.get(event.getType());
		if (list != null) {
			for (IEventReceiver<?> eventReceiver : list) {
				Class<?> eventClass = eventReceiver.getEventClass();
				if (eventClass.equals(event.getClass())) {
					try {
						Method method = eventReceiver.getClass().getMethod(
								"receiveEvent", eventClass);
						method.invoke(eventReceiver, eventClass.cast(event));
					} catch (SecurityException e) {
						e.printStackTrace();
					} catch (NoSuchMethodException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}
					result = true;
				} else {
					throw new WrongEventClassException(
							eventReceiver.getEventClass(), event.getClass(), "");
				}
			}
		}
		return result;
	}

	public boolean unregisterEventListener(String eventName,
			IEventReceiver<?> rec) {
		boolean removed = false;
		List<IEventReceiver<?>> list = eventReceivers.get(eventName);
		if (list != null) {
			removed = list.remove(rec);
		}
		return removed;
	}

	public IFuture<IComponentManagementService> getCMS()
	{
		return getExternalPlattformAccess().scheduleStep(new IComponentStep<IComponentManagementService>() {
			@Classname("create-component")
			public IFuture<IComponentManagementService> execute(
					IInternalAccess ia) {
				Future<IComponentManagementService> ret = new Future<IComponentManagementService>();
				SServiceProvider
						.getService(ia.getServiceContainer(),
								IComponentManagementService.class,
								RequiredServiceInfo.SCOPE_PLATFORM)
						.addResultListener(
								ia.createResultListener(new DelegationResultListener<IComponentManagementService>(
										ret)));

				return ret;
			}
		});
	}
	
	protected String createRandomPlatformID() {
		UUID randomUUID = UUID.randomUUID();
		StringBuilder sb = new StringBuilder();
		for (String identifier: identifiers) {
			if (!identifier.equals(android.os.Build.UNKNOWN)) {
				sb.append(identifier);
			}
		}
		// ** Uncomment for unique device names **
//		int deviceFingerPrint = android.os.Build.FINGERPRINT.hashCode();
//		String hexString = Integer.toHexString(deviceFingerPrint);
//		if (hexString.length() > 1) {
//			sb.append(hexString.substring(0,2));
//		} else {
//		}
		
		sb.append("_");
		sb.append(randomUUID.toString().substring(0,3));
		return sb.toString();
	}

	public IFuture<IExternalAccess> startJadexPlatform() {
		return startJadexPlatform(DEFAULT_KERNELS);
	}
	
	public IFuture<IExternalAccess> startJadexPlatform(final String[] kernels) {
		return startJadexPlatform(kernels, createRandomPlatformID());
	}
	
	public IFuture<IExternalAccess> startJadexPlatform(final String[] kernels, final String platformId) {
		return startJadexPlatform(kernels, platformId, "");
		
	}
	
	public synchronized IFuture<IExternalAccess> startJadexPlatform(final String[] kernels, final String platformId, final String options) {
		if (isJadexRunning()){
			throw new JadexAndroidError("Platform was already started!");
		}
		
		final Future<IExternalAccess> ret = new Future<IExternalAccess>();
		
		final StringBuffer kernelString = new StringBuffer("\"");
		String sep = "";
		for (int i = 0; i < kernels.length; i++) {
			kernelString.append(sep);
			kernelString.append(kernels[i]);
			sep = ",";
		}
		kernelString.append("\"");
		
		final String defOptions = "-logging_level java.util.logging.Level.INFO" +
		" -extensions null" +
		" -wspublish false" +
		" -android true" +
		" -kernels " + kernelString.toString() +
		" -binarymessages true" +
//		" -tcptransport false" +
//		" -niotcptransport false" +
//		" -relaytransport true" +
//		" -relayaddress \"http://134.100.11.200:8080/jadex-platform-relay-web/\"" +				
//		" -saveonexit false -gui false" +
		" -autoshutdown false" +
		" -platformname " + platformId +
		" -saveonexit true -gui false" +
		" ";
		
		new Thread(new Runnable() {
			public void run() {
				IFuture<IExternalAccess> future = Starter
						.createPlatform((defOptions + options).split("\\s+"));
				future.addResultListener(new IResultListener<IExternalAccess>() {

					@Override
					public void resultAvailable(IExternalAccess result) {
						setExternalPlattformAccess(result);
						ret.setResult(result);
					}

					@Override
					public void exceptionOccurred(Exception exception) {
						ret.setException(exception);
					}
				});
			}
		}).start();
		
		return ret;
	}
	
	public synchronized void shutdownJadexPlatform() {
		Log.d("jadex-android","Starting platform shutdown");
		ThreadSuspendable	sus	= new ThreadSuspendable();
		long start	= System.currentTimeMillis();
		long timeout	= 4500;	// Android issues hard kill (ANR) after 5 secs!
		//IExternalAccess	ea = platform.get(sus, timeout);
		extAcc.killComponent().get(sus, start+timeout-System.currentTimeMillis());
		setExternalPlattformAccess(null);
		Log.d("jadex-android", "Platform shutdown completed");
	}

}
