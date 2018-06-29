package jadex.bdiv3;

import jadex.bridge.IInternalAccess;

import java.util.Map;

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
//		System.err.println("Requested BDIClassGenerator on Android!");
//		throw new Error("Requested BDIClassGenerator on Android!");
		return new DummyBDIClassGenerator();
	}

	@Override
	public BDIAgentFactory createBDIAgentFactory(IInternalAccess provider, Map properties)
	{
		System.err.println("Requested BDIAgentFactory on Android!");
		throw new Error("Requested BDIAgentFactory on Android!");
	}
}
