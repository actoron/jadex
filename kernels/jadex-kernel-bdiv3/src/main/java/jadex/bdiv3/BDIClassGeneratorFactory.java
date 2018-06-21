package jadex.bdiv3;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import jadex.bridge.IInternalAccess;
import jadex.commons.SReflect;

public abstract class BDIClassGeneratorFactory
{
	// -------- attributes --------

	/** The instance of this factory */
	private static BDIClassGeneratorFactory INSTANCE;

	// -------- constructors --------
	protected BDIClassGeneratorFactory()
	{
	}

	// -------- methods --------
	/**
	 * Returns the instance of this factory.
	 * 
	 * @return the factory instance
	 */
	public static BDIClassGeneratorFactory getInstance()
	{
		if(INSTANCE == null)
		{
			if(SReflect.isAndroid())
			{
				Class<?> clz;
				try
				{
					clz = SReflect.classForName("jadex.bdiv3.BDIClassGeneratorFactoryAndroid", null);
					if(clz != null) 
					{
						INSTANCE = (BDIClassGeneratorFactory)clz.newInstance();
					}
				} 
				catch (ClassNotFoundException e)
				{
					Logger.getLogger("jadex").log(Level.WARNING, "BDIClassGeneratorFactory not available.");
				} 
				catch (InstantiationException e)
				{
					e.printStackTrace();
				} 
				catch (IllegalAccessException e)
				{
					e.printStackTrace();
				}
			}
			else
			{
				INSTANCE = new BDIClassGeneratorFactoryDesktop();
			}
		}
		return INSTANCE;
	}
	
	/**
	 * Create a new, platform-specific BDI Class Reader object.
	 * @param loader
	 * @return {@link BDIClassReader}
	 */
	public abstract BDIClassReader createBDIClassReader(BDIModelLoader loader);
	
	/**
	 * Create a new, platform-specific BDI Class Generator object.
	 * @return {@link IBDIClassGenerator}
	 */
	public abstract IBDIClassGenerator createBDIClassGenerator();
	
	/**
	 * Create a new, platform-specific BDIAgentFactory object.
	 * @return {@link BDIAgentFactory}
	 */
	public abstract BDIAgentFactory createBDIAgentFactory(IInternalAccess provider, Map properties);

}
