package jadex.micro.testcases.nfservicetags;

import jadex.base.IPlatformConfiguration;
import jadex.base.PlatformConfigurationHandler;
import jadex.base.Starter;
import jadex.bridge.IExternalAccess;

public class Main
{
	public static void main(String[] args)
	{
//		IPlatformConfiguration conf = STest.getDefaultTestConfig(Main.class);
		IPlatformConfiguration conf = PlatformConfigurationHandler.getDefault();
		System.out.println(conf.getGui());
		conf.setGui(true);
		IExternalAccess plat = Starter.createPlatform(conf).get();
		System.out.println("hiier: "+plat);
	}
}
