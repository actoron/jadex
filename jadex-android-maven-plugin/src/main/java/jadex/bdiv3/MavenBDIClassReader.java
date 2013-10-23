package jadex.bdiv3;

import jadex.bdiv3.BDIClassReader;
import jadex.bdiv3.BDIModelLoader;
import jadex.bdiv3.IBDIClassGenerator;

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
