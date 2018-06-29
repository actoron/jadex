package jadex.bdiv3;

import jadex.bdiv3.model.BDIModel;

import java.util.List;

public class DummyBDIClassGenerator implements IBDIClassGenerator
{

	@Override
	public List<Class< ? >> generateBDIClass(String clname, BDIModel micromodel, ClassLoader cl)
	{
		return null;
	}

}
