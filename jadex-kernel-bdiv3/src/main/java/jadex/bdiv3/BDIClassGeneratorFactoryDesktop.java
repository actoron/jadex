package jadex.bdiv3;

import jadex.bridge.service.IServiceProvider;

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
	public BDIAgentFactory createBDIAgentFactory(IServiceProvider provider)
	{
		return new BDIAgentFactory(provider);
	}

}
