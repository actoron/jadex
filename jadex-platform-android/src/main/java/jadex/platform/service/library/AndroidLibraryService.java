package jadex.platform.service.library;

import jadex.android.commons.Logger;
import jadex.android.service.JadexPlatformManager;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.service.IServiceProvider;
import jadex.bridge.service.annotation.CheckNotNull;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.context.IContextService;
import jadex.bridge.service.types.library.ILibraryService;
import jadex.commons.SUtil;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.AgentService;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

import java.io.File;
import java.net.URL;

import dalvik.system.DexClassLoader;

/**
 * Library service for loading classpath elements on Android devices.
 */
@Service(ILibraryService.class)
@RequiredServices(
{@RequiredService(name = "context", type = IContextService.class)})
public class AndroidLibraryService extends LibraryService
{
	@AgentService
	private IContextService contextService;

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
//		if (purl.getFile().endsWith("apk")) {
//			// is android apk file
//		} else {
//		}
		IFuture<IResourceIdentifier> addURL = super.addURL(parid, purl);
		return addURL;
	}


	@SuppressWarnings("resource")
	@Override
	protected DelegationURLClassLoader createNewDelegationClassLoader(IResourceIdentifier rid, ClassLoader baseloader,
			DelegationURLClassLoader[] delegates)
	{
		Logger.d("Creating new delegation ClassLoader for: " + rid);
		if (rid.getLocalIdentifier().getUrl().getPath().endsWith("apk")) {
			if (contextService == null) {
				IExternalAccess externalAccess = component.getExternalAccess();
				IServiceProvider serviceProvider = externalAccess.getServiceProvider();
				IContextService contextService = SServiceProvider.getService(serviceProvider, IContextService.class).get();
				this.contextService = contextService;
			}

			String path = SUtil.androidUtils().apkPathFromUrl(rid.getLocalIdentifier().getUrl());
			ClassLoader cl = JadexPlatformManager.getInstance().getClassLoader(path);
			DexDelegationClassLoader dexDelegation = new DexDelegationClassLoader(rid, baseloader, (DexClassLoader) cl);
			return dexDelegation;
			
//			File file = contextService.getFile("out").get();
//			file.mkdir();
//			return new DexDelegationClassLoader(rid, baseloader, delegates, file);
		} else {
			return super.createNewDelegationClassLoader(rid, baseloader, delegates);
		}
	}

}
