package jadex.bdiv3;

import jadex.bridge.service.IServiceProvider;

public class BDIClassGeneratorFactoryAndroid extends BDIClassGeneratorFactory
{

	@Override
	public BDIClassReader createBDIClassReader(BDIModelLoader loader)
	{
		return new BDIClassReaderAndroid(loader);
	}

	@Override
	public IBDIClassGenerator createBDIClassGenerator()
	{
		return new AsmDexBdiClassGenerator();
	}

	@Override
	public BDIAgentFactory createBDIAgentFactory(IServiceProvider provider)
	{
		return new BDIAgentFactoryAndroid(provider);
	}
	
	

}
