package jadex.bdiv3.testcases.semiautomatic;

import jadex.base.PlatformConfigurationHandler;
import jadex.base.Starter;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.types.cms.CreationInfo;

public class Main 
{
	/**
	 *  Start a platform and the example.
	 */
	public static void main(String[] args) 
	{
		IExternalAccess platform = Starter.createPlatform(PlatformConfigurationHandler.getDefaultNoGui()).get();
		// note: this does not work as it loads the unenhanced BDI agent class in the JVM
		//CreationInfo ci = new CreationInfo().setFilenameClass(BlocksworldAgent.class);
		CreationInfo ci = new CreationInfo().setFilename("jadex/bdiv3/testcases/semiautomatic/BasicTypeConditionBDI.class");
		platform.createComponent(ci).get();
		//platform.waitForDelay(2000);
		//ci.setConfiguration("2");
		//platform.createComponent(ci).get();
	}
}
