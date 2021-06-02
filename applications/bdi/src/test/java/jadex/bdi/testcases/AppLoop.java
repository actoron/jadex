package jadex.bdi.testcases;

import jadex.base.IPlatformConfiguration;
import jadex.base.Starter;
import jadex.base.test.util.STest;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.commons.future.IFuture;

/**
 *  Start hunter prey in a loop to find heisenbug.
 *
 */
public class AppLoop
{
	/**
	 *  start
	 */
	public static void main(String[] args)
	{
		IPlatformConfiguration config = STest.getLocalTestConfig(AppLoop.class);
		config.getExtendedPlatformConfiguration().setDf(true);
		IExternalAccess	platform = Starter.createPlatform(config, args).get();
		for(int i=0; ; i++)
		{
			if(i%100==0)
				System.out.println(i);
			IFuture<IExternalAccess> fut
				= platform.createComponent(new CreationInfo().setFilename("jadex/bdi/examples/hunterprey_classic/HunterPrey.application.xml"));
			fut.get().killComponent().get();
			fut.get().getResultsAsync().get();
		}
	}
}
