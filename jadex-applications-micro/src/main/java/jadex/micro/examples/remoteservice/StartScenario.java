package jadex.micro.examples.remoteservice;

import jadex.base.Starter;
import jadex.bridge.ComponentIdentifier;
import jadex.bridge.CreationInfo;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IExternalAccess;
import jadex.commons.SUtil;
import jadex.commons.concurrent.DefaultResultListener;
import jadex.service.SServiceProvider;

/**
 *  Start two Jadex platforms and one agent on each platform.
 *  On the 'remote' platform the 'add' agent is created, which offers an 
 *  IAdd service interface via its service provider.
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
		Starter.createPlatform(new String[]{"-configname", "all_kernels", "-platformname", "local", "-tcpport", "10000", "-niotcpport", "10001"})
			.addResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				final IExternalAccess lplat = (IExternalAccess)result;
				
				Starter.createPlatform(new String[]{"-configname", "all_kernels", "-platformname", "remote", "-tcpport", "11000", "-niotcpport", "11001"})
					.addResultListener(new DefaultResultListener()
				{
					public void resultAvailable(Object source, Object result)
					{
						final IExternalAccess rplat = (IExternalAccess)result;
						
						SServiceProvider.getServiceUpwards(lplat.getServiceProvider(), IComponentManagementService.class)
							.addResultListener(new DefaultResultListener()
						{
							public void resultAvailable(Object source, Object result)
							{
								final IComponentManagementService lcms = (IComponentManagementService)result;
					
								SServiceProvider.getServiceUpwards(rplat.getServiceProvider(), IComponentManagementService.class)
									.addResultListener(new DefaultResultListener()
								{
									public void resultAvailable(Object source, Object result)
									{
										IComponentManagementService rcms = (IComponentManagementService)result;
										
										rcms.createComponent("add", "jadex.micro.examples.remoteservice.AddAgent.class", null, null)
											.addResultListener(new DefaultResultListener()
										{
											public void resultAvailable(Object source, Object result)
											{
//												System.out.println("started remote: "+result);
												
												IComponentIdentifier rrms = new ComponentIdentifier("rms@remote", 
													new String[]{"tcp-mtp://127.0.0.1:11000", "nio-mtp://127.0.0.1:11001"});
												
												lcms.createComponent("proxy", "jadex.base.service.remote.ProxyAgent.class", 
													new CreationInfo(SUtil.createHashMap(new String[]{"componentid"}, new Object[]{rrms})), null)
													.addResultListener(new DefaultResultListener()
												{
													public void resultAvailable(Object source, Object result)
													{
														lcms.createComponent("user", "jadex.micro.examples.remoteservice.UserAgent.class", null, null)
															.addResultListener(new DefaultResultListener()
														{
															public void resultAvailable(Object source, Object result)
															{
																//System.out.println("started local: "+result);
															}
														});
													}
												});
											}
										});
									}
								});
							}
						});			
					}
				});
			}
		});
	}
}
