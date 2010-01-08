package jadex.bdi.simulation.client;


import jadex.application.runtime.IApplicationExternalAccess;
import jadex.application.space.envsupport.environment.ISpaceObject;
import jadex.application.space.envsupport.environment.space2d.ContinuousSpace2D;
import jadex.application.space.envsupport.math.Vector2Double;
import jadex.bdi.runtime.Plan;

import java.util.HashMap;

public class ObserveApplicationPlan extends Plan{

	public void body() {
		ContinuousSpace2D space = (ContinuousSpace2D)((IApplicationExternalAccess)getScope().getParent()).getSpace("my2dspace");
//		 IApplicationExternalAccess app = (IApplicationExternalAccess)getScope().getServiceContainer();		
//			AGRSpace agrs = (AGRSpace)app.getSpace("myagrspace");		 
//		 ContinuousSpace2D space = (ContinuousSpace2D) app.getSpace("my2dspace"); 
		
		 ISpaceObject homebase = space.getSpaceObjectsByType("homebase")[0];
			 ISpaceObject[] targets = space.getSpaceObjectsByType("target");
			 
//			 Integer ore = (Integer) homebase.getProperty("ore");
//			 Long missiontime = (Long) homebase.getProperty("missiontime");
		
		while(true){
			Long timestamp = new Long (System.currentTimeMillis());
			String res = "Homebase: " + (Integer) homebase.getProperty("ore") + ";;";
			for(int i=0; i < targets.length; i++){
				Integer ore = (Integer) targets[i].getProperty("ore");
				Vector2Double pos = (Vector2Double) targets[i].getProperty("position");
				res += "Target: " + pos.toString() + " - " + ore.intValue() + ";" ;
			}
			HashMap map = (HashMap) getBeliefbase().getBelief("simulationResults").getFact();
			map.put(timestamp, res);
			getBeliefbase().getBelief("simulationResults").setFact(map);
//			System.out.println("Plan alife...");
		
			waitFor(1000);
		}
		
	}

	public void aborted()
	{
		System.out.println("Plan aborted...");
	}
}
