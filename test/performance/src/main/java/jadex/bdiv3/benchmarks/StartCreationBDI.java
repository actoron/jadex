package jadex.bdiv3.benchmarks;

import jadex.base.Starter;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.types.cms.CreationInfo;

/**
 * 
 */
public class StartCreationBDI 
{
	/**
	 * 
	 */
	public static void main(String[] args) throws Exception
	{
		IExternalAccess ea = Starter.createPlatform(new String[]
		{
//			"-logging", "true",
			"-gui", "false",
			"-extensions", "null",
			"-cli", "false",
//			"-awareness", "false"
		}).get();
		ea.createComponent(null, new CreationInfo().setFilename("CreationBDI.class")).get();
	}
}
