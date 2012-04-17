package jadex.android;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.android.IAndroidContextService;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.IFuture;

public class JadexAndroidContext {

	private static final JadexAndroidContext instance = new JadexAndroidContext();
	private IExternalAccess extAcc;
	private Context lastContext;

	private List<AndroidContextChangeListener> listeners = new ArrayList<AndroidContextChangeListener>();

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
	
//	public IFuture getAndroidContextService() {
//		IFuture<IAndroidContextService> service = SServiceProvider.getService(
//				access.getServiceContainer(), IAndroidContextService.class);
//		service.addResultListener(new DefaultResultListener<IAndroidContextService>() {
//			@Override
//			public void resultAvailable(IAndroidContextService result) {
//				contextService = result;
//			}
//		});
//	}
	
	public boolean isJadexRunning() {
		return this.extAcc != null;
	}

	/**
	 * Sets a new Android Application Context.
	 * Pass <code>null</code> to unset the previous application context.
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
		listeners.add(l);
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
		listeners.remove(l);
		l.onContextDestroy(lastContext);
	}

	private void informContextDestroy(Context ctx) {
		synchronized (listeners) {
			for (AndroidContextChangeListener l : listeners) {
				l.onContextDestroy(ctx);
			}
		}
	}

	private void informContextCreate(Context ctx) {
		synchronized (listeners) {
			for (AndroidContextChangeListener l : listeners) {
				l.onContextCreate(ctx);
			}
		}
	}
	

}
