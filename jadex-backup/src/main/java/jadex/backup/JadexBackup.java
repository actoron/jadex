package jadex.backup;

import jadex.base.Starter;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.ThreadSuspendable;

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
	}
}
