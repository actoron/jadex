package jadex.extension.envsupport.environment;

import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.commons.SimplePropertyObject;


public class ServiceVisionProcessor	extends SimplePropertyObject implements IPerceptProcessor
{
	public ServiceVisionProcessor()
	{
//		System.out.println("created: "+this);
	}
	
	public void processPercept(IEnvironmentSpace space, String type, Object percept, IComponentDescription component, ISpaceObject avatar)
	{
		System.out.println("percept: "+type+", "+percept);
	}
}
