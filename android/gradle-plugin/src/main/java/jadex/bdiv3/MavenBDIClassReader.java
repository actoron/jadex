package jadex.bdiv3;

public class MavenBDIClassReader extends BDIClassReader
{

	public MavenBDIClassReader(BDIModelLoader loader)
	{
		super(loader);
	}

	public void setGenerator(IBDIClassGenerator gen)
	{
		this.gen = gen;
	}

}
