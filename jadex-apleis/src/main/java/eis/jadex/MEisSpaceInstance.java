package eis.jadex;

import eis.EnvironmentInterfaceStandard;
import jadex.base.appdescriptor.ApplicationContext;
import jadex.base.appdescriptor.MApplicationType;
import jadex.base.appdescriptor.MSpaceInstance;
import jadex.bridge.IApplicationContext;
import jadex.bridge.ISpace;

/**
 * 
 */
public class MEisSpaceInstance extends MSpaceInstance
{
	/**
	 *  Create a space.
	 */
	public ISpace createSpace(IApplicationContext app)
	{
		try
		{
			MApplicationType mapt = ((ApplicationContext)app).getApplicationType();
			MEisSpaceType mspacetype = (MEisSpaceType)mapt.getMSpaceType(getTypeName());
			
			Class eisclazz = mspacetype.getEisClazz();
			EnvironmentInterfaceStandard eis = (EnvironmentInterfaceStandard)eisclazz.newInstance();
			
			EisSpace ret = new EisSpace(getName(), app, eis);
			
			return ret;
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
}
