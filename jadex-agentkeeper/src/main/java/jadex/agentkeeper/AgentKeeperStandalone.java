package jadex.agentkeeper;

import jadex.base.Starter;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.clock.IClockService;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ThreadSuspendable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *  Starter class for AgentKeeper
 */
public class AgentKeeperStandalone
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
			
			boolean test = Math.random() > 0.5;
		}
		
		String[] defargs = new String[]
		{
//			"-logging", "true",
			"-gui", "false",
			"-welcome", "false",
			"-cli", "false",
			"-printpass", "false",
			"-networkname", "\"agentkeeper\"",
			"-kernels", "\"micro,bdibpmn,bdiv3,application,component\""
		};
		String[] newargs = new String[defargs.length+jargs.size()];
		System.arraycopy(defargs, 0, newargs, 0, defargs.length);
		System.arraycopy(jargs.toArray(), 0, newargs, defargs.length, jargs.size());
		
		ThreadSuspendable	sus	= new ThreadSuspendable();
		IExternalAccess	platform	= Starter.createPlatform(newargs).get(sus);
		IComponentManagementService	cms	= SServiceProvider.getService(platform.getServiceProvider(),
			IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM).get(sus);
		Map<String, Object> baargs = new HashMap<String, Object>();
		baargs.put("cmdargs", (String[])bargs.toArray(new String[bargs.size()]));
		CreationInfo ci = new CreationInfo(baargs);
		cms.createComponent(null, "jadex/bdiv3/KernelBDIV3.component.xml", null, null).get(sus);
		cms.createComponent(null, "jadex/agentkeeper/AgentKeeper3d.application.xml", ci, null).get(sus);
		
		SServiceProvider.getService(platform.getServiceProvider(), IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(new IResultListener<IClockService>()
		{
			public void resultAvailable(IClockService cs)
			{
				cs.setDelta(30);
			}
			
			public void exceptionOccurred(Exception exception)
			{
			}
		});
	}
}
