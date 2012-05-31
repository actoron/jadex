package jadex.base.service.android;

import jadex.android.JadexAndroidContext;
import jadex.android.JadexAndroidContext.AndroidContextChangeListener;
import jadex.android.exception.JadexAndroidContextNotFoundError;
import jadex.android.exception.WrongEventClassException;
import jadex.bridge.service.BasicService;
import jadex.bridge.service.IServiceProvider;
import jadex.bridge.service.types.android.IAndroidContextService;
import jadex.bridge.service.types.android.IJadexAndroidEvent;
import jadex.bridge.service.types.android.IPreferences;
import jadex.commons.future.IFuture;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import android.content.Context;
import android.util.Log;

/**
 * Provides Access to the Android Application Context and 
 * Android Resources such as Files and Properties 
 *
 */
public class AndroidContextService extends BasicService implements AndroidContextChangeListener, IAndroidContextService{

	private Context context;
	private JadexAndroidContext jadexAndroidContext;

	/**
	 * Constructor
	 * @param provider
	 */
	public AndroidContextService(IServiceProvider provider) {
		super(provider.getId(), IAndroidContextService.class, null);
		jadexAndroidContext = JadexAndroidContext.getInstance();
		jadexAndroidContext.addContextChangeListener(this);
	}
	
	@Override
	public IFuture<Void> startService() {
		return super.startService();
	}
	
	@Override
	public IFuture<Void> shutdownService() {
		JadexAndroidContext.getInstance().removeContextChangeListener(this);
		return super.shutdownService();
	}
	
	/* (non-Javadoc)
	 * @see jadex.android.service.IAndroidContextService#openFileOutputStream(java.lang.String)
	 */
	@Override
	public FileOutputStream openFileOutputStream(String name) throws FileNotFoundException {
		checkContext();
		return context.openFileOutput(name, Context.MODE_PRIVATE);
	}
	
	/* (non-Javadoc)
	 * @see jadex.android.service.IAndroidContextService#openFileInputStream(java.lang.String)
	 */
	@Override
	public FileInputStream openFileInputStream(String name) throws FileNotFoundException {
		checkContext();
		return context.openFileInput(name);
	}
	
	@Override
	public File getFile(String name) {
		checkContext();
		return context.getFileStreamPath(name);
	}


	@Override
	public IPreferences getSharedPreferences(String name) {
		checkContext();
		return AndroidSharedPreferencesWrapper.wrap(context.getSharedPreferences(name, Context.MODE_PRIVATE));
	}

	@Override
	public void onContextDestroy(Context ctx) {
		this.context = null;
	}

	@Override
	public void onContextCreate(Context ctx) {
		this.context = ctx;
	}
	
	private void checkContext() throws JadexAndroidContextNotFoundError {
		if (context == null) {
			throw new JadexAndroidContextNotFoundError();
		}
	}

	@Override
	public boolean dispatchUiEvent(IJadexAndroidEvent event) {
		try {
			return jadexAndroidContext.dispatchEvent(event);
		} catch (WrongEventClassException e) {
			Log.e("AndroidContextService", e.getMessage());
			return false;
		}
	}
	
}