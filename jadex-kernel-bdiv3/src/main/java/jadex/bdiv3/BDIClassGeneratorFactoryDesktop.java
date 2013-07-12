package jadex.bdiv3;

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

}
