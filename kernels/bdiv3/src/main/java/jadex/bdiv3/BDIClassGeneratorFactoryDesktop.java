package jadex.bdiv3;

import java.util.Map;

import jadex.bridge.IInternalAccess;

/**
 * 
 */
public class BDIClassGeneratorFactoryDesktop extends BDIClassGeneratorFactory
{
	/**
	 * 
	 */
	public BDIClassReader createBDIClassReader(BDIModelLoader loader)
	{
		return new BDIClassReader(loader);
	}

	/**
	 * 
	 */
	public IBDIClassGenerator createBDIClassGenerator()
	{
		return new ASMBDIClassGenerator();
	}

	/**
	 * 
	 */
	public BDIAgentFactory createBDIAgentFactory(IInternalAccess provider, Map properties)
	{
		return new BDIAgentFactory(provider, properties);
	}
}
