package jadex.android.service;

import jadex.android.AndroidContextChangeListener;
import jadex.android.JadexAndroidActivity;
import jadex.android.JadexAndroidContextNotFoundError;
import jadex.bridge.ComponentIdentifier;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.BasicService;
import jadex.bridge.service.IServiceProvider;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Map;

import android.content.Context;


public class AndroidContextService extends BasicService implements AndroidContextChangeListener, IAndroidContextService{

	private Context context;

	public AndroidContextService(IServiceProvider provider) {
		super(provider.getId(), IAndroidContextService.class, null);
		JadexAndroidActivity.addContextChangeListener(this);
	}
	
	@Override
	public IFuture<Void> startService() {
		return super.startService();
	}
	
	@Override
	public IFuture<Void> shutdownService() {
		JadexAndroidActivity.removeContextChangeListener(this);
		return super.shutdownService();
	}
	
	/* (non-Javadoc)
	 * @see jadex.android.service.IAndroidContextService#openFileOutputStream(java.lang.String)
	 */
	@Override
	public FileOutputStream openFileOutputStream(String name) throws FileNotFoundException {
		if (context == null) {
			throw new JadexAndroidContextNotFoundError();
		}
		return context.openFileOutput(name, Context.MODE_PRIVATE);
	}
	
	/* (non-Javadoc)
	 * @see jadex.android.service.IAndroidContextService#openFileInputStream(java.lang.String)
	 */
	@Override
	public FileInputStream openFileInputStream(String name) throws FileNotFoundException {
		if (context == null) {
			throw new JadexAndroidContextNotFoundError();
		}
		return context.openFileInput(name);
	}
	
	@Override
	public File getFile(String name) {
		if (context == null) {
			throw new JadexAndroidContextNotFoundError();
		}
		return context.getFileStreamPath(name);
	}
	

	@Override
	public void onContextDestroy(Context ctx) {
		this.context = null;
	}

	@Override
	public void onContextCreate(Context ctx) {
		this.context = ctx;
	}

	
}