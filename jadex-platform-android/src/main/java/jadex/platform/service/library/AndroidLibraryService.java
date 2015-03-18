package jadex.platform.service.library;

import jadex.android.commons.Logger;
import jadex.android.service.JadexPlatformManager;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.service.annotation.CheckNotNull;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.types.context.IContextService;
import jadex.bridge.service.types.library.ILibraryService;
import jadex.commons.SUtil;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

import java.net.URL;

import android.util.Log;

/**
 * Library service for loading classpath elements on Android devices.
 */
@Service(ILibraryService.class)
public class AndroidLibraryService extends LibraryService
{
	private static final String LOG_TAG = "AndroidLibraryService";

	private DelegationClassLoader androidBaseLoader;

	public AndroidLibraryService()
	{
		super();
		DelegationClassLoader androidBaseLoader = new DelegationClassLoader(baseloader != null ? baseloader : getClass().getClassLoader());
		this.androidBaseLoader = androidBaseLoader;
		this.baseloader = androidBaseLoader;
		this.rootloader = new DelegationURLClassLoader(this.baseloader, null);
	}
	
	

	@Override
	public IFuture<Void> addTopLevelURL(@CheckNotNull URL url)
	{
		ClassLoader classLoader = JadexPlatformManager.getInstance().getClassLoader(url.getFile());
		DelegationClassLoader baseDelegate = new DelegationClassLoader(classLoader);
		androidBaseLoader.setDelegate(baseDelegate);
		return IFuture.DONE;
	}
	
	

	@Override
	public IFuture<IResourceIdentifier> addURL(IResourceIdentifier parid, URL purl)
	{
		Logger.d("libservice: adding Url: " + purl);
		IFuture<IResourceIdentifier> addURL = super.addURL(parid, purl);
		return addURL;
	}


	@Override
	protected DelegationURLClassLoader createNewDelegationClassLoader(IResourceIdentifier rid, ClassLoader baseloader,
			DelegationURLClassLoader[] delegates)
	{
		DelegationURLClassLoader result;
		Logger.d("Creating new delegation ClassLoader for: " + rid);
		if (rid.getLocalIdentifier().getUri().getPath().endsWith("apk")) {
			String path = SUtil.androidUtils().apkPathFromUrl(SUtil.toURL(rid.getLocalIdentifier().getUri()));
			ClassLoader cl = JadexPlatformManager.getInstance().getClassLoader(path);
			if (cl == null) {
				Log.e(LOG_TAG, "Got null classloader for path: " + path);
			}
			result = new DexDelegationClassLoader(rid, baseloader, cl);
		} else {
			result = super.createNewDelegationClassLoader(rid, baseloader, delegates);
		}
		return result;
	}
	
	

}
