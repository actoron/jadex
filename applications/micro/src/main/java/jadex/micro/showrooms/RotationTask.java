package jadex.micro.showrooms;

import jadex.bridge.service.types.clock.IClockService;
import jadex.extension.envsupport.environment.AbstractTask;
import jadex.extension.envsupport.environment.IEnvironmentSpace;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.SpaceObject;
import jadex.extension.envsupport.environment.space3d.Space3D;
import jadex.extension.envsupport.math.Vector3Double;

public class RotationTask extends AbstractTask
{
	public void execute(IEnvironmentSpace space, ISpaceObject obj, long progress, IClockService cl)
	{

		
		ISpaceObject[] ispaceObjects = space.getSpaceObjectsByType("rotationplattform");
		int distance = ispaceObjects.length;
		
		SpaceObject me = (SpaceObject)((Space3D)space).getSpaceObject(obj.getId());
		me.setProperty("rotation", new Vector3Double(0, 0, 0));

		
		Long intme = (Long)me.getId();
		
		int integerme = intme.intValue();

		int tmp = 360/distance*integerme;

		int clock = ((int)cl.getTick()+tmp)%360;
		
		
		
		double radius=0.5;
		double xcenter = 0.5;
		double ycenter= 0.5;
	


		double x=(double)(Math.cos(clock*Math.PI/180)*radius+xcenter);
		double y=(double)(Math.sin(clock*Math.PI/180)*radius+ycenter);
		
		if(cl.getTick()%200==50)
		{
			me.setProperty("status", "Idle");
		}
		if(cl.getTick()%200==100)
		{
			me.setProperty("status", "Walk");
		}
		if(cl.getTick()%200==0)
		{
			me.setProperty("status", "Attack");
		}
		if(cl.getTick()%200==150)
		{
			me.setProperty("status", "Nix");
		}
		me.setProperty("position", new Vector3Double(x, (Math.sin(clock*Math.PI/360))/2, y));
	}
}
