package jadex.platform.service.library;

import jadex.android.service.JadexPlatformManager;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.service.IServiceProvider;
import jadex.bridge.service.annotation.CheckNotNull;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.context.IContextService;
import jadex.bridge.service.types.library.ILibraryService;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.AgentService;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

import java.io.File;
import java.net.URL;

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
	protected DelegationURLClassLoader createNewDelegationClassLoader(IResourceIdentifier rid, ClassLoader baseloader,
			DelegationURLClassLoader[] delegates)
	{
		System.out.println(rid);
		if (rid.getLocalIdentifier().getUrl().getPath().endsWith("apk")) {
			if (contextService == null) {
				IExternalAccess externalAccess = component.getExternalAccess();
				IServiceProvider serviceProvider = externalAccess.getServiceProvider();
				IContextService contextService = SServiceProvider.getService(serviceProvider, IContextService.class).get();
				this.contextService = contextService;
			}
			
			File file = contextService.getFile("out").get();
			file.mkdir();
			return new DexDelegationClassLoader(rid, baseloader, delegates, file);
		} else {
			return super.createNewDelegationClassLoader(rid, baseloader, delegates);
		}
	}

}
