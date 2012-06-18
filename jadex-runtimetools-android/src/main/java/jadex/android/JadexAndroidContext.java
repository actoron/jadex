package jadex.android;

import jadex.android.exception.WrongEventClassException;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.android.IJadexAndroidEvent;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.transformation.annotations.Classname;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;

public class JadexAndroidContext {

	private static final JadexAndroidContext instance = new JadexAndroidContext();
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

	// public IFuture getAndroidContextService() {
	// IFuture<IAndroidContextService> service = SServiceProvider.getService(
	// access.getServiceContainer(), IAndroidContextService.class);
	// service.addResultListener(new
	// DefaultResultListener<IAndroidContextService>() {
	// @Override
	// public void resultAvailable(IAndroidContextService result) {
	// contextService = result;
	// }
	// });
	// }

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

}
