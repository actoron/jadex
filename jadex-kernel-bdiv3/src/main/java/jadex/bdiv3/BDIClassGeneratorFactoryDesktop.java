package jadex.bdiv3;

import jadex.bridge.service.IServiceProvider;

import java.util.Map;

public class BDIClassGeneratorFactoryDesktop extends BDIClassGeneratorFactory
{

	@Override
	public BDIClassReader createBDIClassReader(BDIModelLoader loader)
	{
		return new BDIClassReader(loader);
	}

	@Override
	public IBDIClassGenerator createBDIClassGenerator()
	{
		return new ASMBDIClassGenerator();
	}

	@Override
	public BDIAgentFactory createBDIAgentFactory(IServiceProvider provider, Map properties)
	{
		return new BDIAgentFactory(provider, properties);
	}

}
