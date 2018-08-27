package jadex.micro.testcases.semiautomatic.remoteservice;

import java.util.Collection;

import jadex.base.IPlatformConfiguration;
import jadex.base.PlatformConfigurationHandler;
import jadex.base.Starter;
import jadex.bridge.ComponentIdentifier;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 *  Start two Jadex platforms and one agent on each platform.
 *  On the 'remote' platform the 'math' agent is created, which offers an 
 *  IMathService interface via its service provider.
 *  On the 'local' platform the 'user' agent is created, which fetches the
 *  add service via the remote management service (by knowing the remote platform name/address). 
 */
public class StartScenario
{
	/**
	 *  Main for starting hello world agent.
	 */
	public static void main(String[] args)
	{
		startScenario(null);
	}
	
	/**
	 *  Start the scenario.
	 */
	public static IFuture<IExternalAccess[]> startScenario(final String[] libpaths)
	{
		final Future<IExternalAccess[]> ret = new Future<IExternalAccess[]>();
		
//		String[] defargs = new String[]{"-logging", "true", "-platformname", "local", "-tcpport", "10000", "-tcpport", "10001", "-printpass", "false", "-networkname", "abc"};
		
		
//		Starter.createPlatform(createArguments(defargs, libpaths))
		IPlatformConfiguration conf1 = PlatformConfigurationHandler.getMinimal();
		conf1.setGui(true);
		Starter.createPlatform(conf1)
			.addResultListener(new DefaultResultListener<IExternalAccess>()
		{
			public void resultAvailable(final IExternalAccess lplat)
			{
				String[] defargs = new String[]{"-platformname", "remote", "-tcpport", "11000", "-tcpport", "11001", "-printpass", "false", "-networkname", "abc"};
				
//				Starter.createPlatform(createArguments(defargs, libpaths))
				Starter.createPlatform(PlatformConfigurationHandler.getMinimal())
					.addResultListener(new DefaultResultListener<IExternalAccess>()
				{
					public void resultAvailable(final IExternalAccess rplat)
					{
//						rplat.searchService(new ServiceQuery<>( IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM))
//							.addResultListener(new DefaultResultListener<IComponentManagementService>()
//						{
//							public void resultAvailable(final IComponentManagementService rcms)
//							{
								rplat.createComponent(null, new CreationInfo().setName("math").setFilename("MathAgent.class"), null)
									.addResultListener(new DefaultResultListener<IExternalAccess>()
								{
									public void resultAvailable(IExternalAccess result)
									{
//										System.out.println("started remote: "+result);
										
										IComponentIdentifier rrms = new ComponentIdentifier("rms@remote", 
											new String[]{"tcp-mtp://127.0.0.1:11000", "nio-mtp://127.0.0.1:11001"});
										
										lplat.createComponent(null,
											new CreationInfo(SUtil.createHashMap(new String[]{"component"}, new Object[]{rrms})).setName("proxy").setFilename("jadex.platform.service.remote.ProxyAgent.class"), null)
											.addResultListener(new DefaultResultListener<IExternalAccess>()
										{
											public void resultAvailable(IExternalAccess result)
											{
												lplat.createComponent(null, new CreationInfo().setName("user").setFilename("UserAgent.class"), new DefaultResultListener<Collection<Tuple2<String, Object>>>()
												{
													public void resultAvailable(Collection<Tuple2<String, Object>> res)
													{
														//System.out.println("killed local user: "+result);
													
														ret.setResult(new IExternalAccess[]{lplat, rplat});
													}
												});
											}
										});
									}
								});
							//}
						//});
					}
				});			
			}
		});
		
		return ret;
	}
	
	/**
	 *  Merge arguments.
	 */
	public static String[] createArguments(String[] defargs, String[] libpaths)
	{
		String[] args = defargs;
		if(libpaths!=null)
		{
			args = new String[defargs.length+2];
			System.arraycopy(defargs, 0, args, 0, defargs.length);
			
			StringBuffer lib = new StringBuffer();
			lib.append("new String[]{");
			for(int i=0; i<libpaths.length; i++)
			{
				lib.append("\"");
				lib.append(libpaths[i]);
				lib.append("\"");
				if(i+1<libpaths.length)
				lib.append(", ");
			}
			lib.append("}");
			
			args[defargs.length] = "-libpath";
			args[defargs.length+1] = lib.toString();
		}
		
		return args;
	}
}
