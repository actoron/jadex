package eis.jadex;

import eis.EnvironmentInterfaceStandard;
import jadex.adapter.base.appdescriptor.ApplicationContext;
import jadex.adapter.base.appdescriptor.MApplicationType;
import jadex.adapter.base.appdescriptor.MSpaceInstance;
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
