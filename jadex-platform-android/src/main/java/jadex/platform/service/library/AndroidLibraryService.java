package jadex.platform.service.library;

import jadex.android.service.JadexPlatformManager;
import jadex.bridge.service.annotation.CheckNotNull;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.types.library.ILibraryService;
import jadex.commons.future.IFuture;

import java.net.URL;


/**
 *  Library service for loading classpath elements on Android devices.
 */
@Service(ILibraryService.class)
public class AndroidLibraryService extends LibraryService
{
	private DelegationClassLoader androidBaseLoader;

	public AndroidLibraryService()
	{
		super();
		DelegationClassLoader androidBaseLoader = new DelegationClassLoader(baseloader!=null? baseloader : getClass().getClassLoader());
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

	
}
