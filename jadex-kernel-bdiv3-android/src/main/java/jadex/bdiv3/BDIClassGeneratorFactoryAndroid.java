package jadex.bdiv3;

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

}
