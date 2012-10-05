package jadex.backup;

import jadex.backup.resource.ILocalResourceService;
import jadex.backup.resource.IResourceService;
import jadex.base.Starter;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.deployment.FileData;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IntermediateDefaultResultListener;
import jadex.commons.future.ThreadSuspendable;

import java.io.File;
import java.util.Iterator;

/**
 *  Starter class for Jadex Backup.
 */
public class JadexBackup
{
	/**
	 *  Launch platform and backup components.
	 */
	public static void main(String[] args)
	{
		String[] defargs = new String[]
		{
//			"-logging", "true",
			"-gui", "false",
			"-welcome", "false",
			"-cli", "false",
			"-printpass", "false",
			"-networkname", "\"jadexbackup\""
		};
		String[] newargs = new String[defargs.length+args.length];
		System.arraycopy(defargs, 0, newargs, 0, defargs.length);
		System.arraycopy(args, 0, newargs, defargs.length, args.length);
		
		ThreadSuspendable	sus	= new ThreadSuspendable();
		IExternalAccess	platform	= Starter.createPlatform(newargs).get(sus);
		IComponentManagementService	cms	= SServiceProvider.getService(platform.getServiceProvider(),
			IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM).get(sus);
		cms.createComponent(null, "jadex/backup/JadexBackup.component.xml", null, null).get(sus);
		
		
		// Simple test synchonization.
		ILocalResourceService	local	= SServiceProvider.getService(platform.getServiceProvider(),
			ILocalResourceService.class, RequiredServiceInfo.SCOPE_PLATFORM).get(sus);
		
		Iterator<IResourceService>	remotes	= SServiceProvider.getServices(platform.getServiceProvider(),
			IResourceService.class, RequiredServiceInfo.SCOPE_PLATFORM).get(sus).iterator();
		while(remotes.hasNext())
		{
			IIntermediateFuture<FileData>	files	= local.update(remotes.next());
			files.addResultListener(new IntermediateDefaultResultListener<FileData>()
			{
				public void intermediateResultAvailable(FileData result)
				{
					System.out.println("Update: "+new File(result.getPath()).getAbsolutePath());
				}
			});
			files.get(sus);
		}
	}
}
