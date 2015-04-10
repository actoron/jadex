package jadex.backup;

import jadex.base.Starter;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *  Starter class for Jadex Backup.
 */
public class JadexBackup
{
	/** The config file argument. */
	public static final String CFG_FILE = "-cfgfile";

	
	/**
	 *  Launch platform and backup components.
	 */
	public static void main(String[] args)
	{
		Set<String> reserved = new HashSet<String>();
		reserved.add(CFG_FILE);
		List<String> jargs = new ArrayList<String>(); // Jadex args
		List<String> bargs = new ArrayList<String>(); // Backup args
		
		for(int i=0; i<args.length; i++)
		{
			if(reserved.contains(args[i]))
			{
				bargs.add(args[i++]);
				bargs.add(args[i]);
			}
			else
			{
				jargs.add(args[i++]);
				jargs.add(args[i]);
			}
		}
		
		String[] defargs = new String[]
		{
//			"-logging", "true",
			"-gui", "false",
			"-welcome", "false",
			"-cli", "false",
			"-printpass", "false",
			"-networkname", "\"jadexbackup\"",
			"-relayaddress", "\"http://jadex.informatik.uni-hamburg.de/relay\""
		};
		String[] newargs = new String[defargs.length+jargs.size()];
		System.arraycopy(defargs, 0, newargs, 0, defargs.length);
		System.arraycopy(jargs.toArray(), 0, newargs, defargs.length, jargs.size());
		
		IExternalAccess	platform	= Starter.createPlatform(newargs).get();
		IComponentManagementService	cms	= SServiceProvider.getService(platform,
			IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM).get();
		Map<String, Object> baargs = new HashMap<String, Object>();
		baargs.put("cmdargs", (String[])bargs.toArray(new String[bargs.size()]));
		CreationInfo ci = new CreationInfo(baargs);
		cms.createComponent(null, "jadex/backup/JadexBackup.component.xml", ci, null).get();
		
		
		// Simple test synchonization.
//		ILocalResourceService	local	= SServiceProvider.getService(platform.getServiceProvider(),
//			ILocalResourceService.class, RequiredServiceInfo.SCOPE_PLATFORM).get(sus);
//		
//		IIntermediateFuture<BackupEvent>	files	= local.updateAll();
//		files.addResultListener(new IntermediateDefaultResultListener<BackupEvent>()
//		{
//			public void intermediateResultAvailable(BackupEvent result)
//			{
//				System.out.println(result);
//			}
//		});
//		files.get(sus);
//		System.out.println("Update finished.");
	}
}
