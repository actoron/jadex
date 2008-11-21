package jadex.bdi.planlib.simsupport.observer.agent;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import jadex.bdi.planlib.simsupport.common.graphics.IViewport;
import jadex.bdi.planlib.simsupport.common.graphics.drawable.IDrawable;
import jadex.bdi.planlib.simsupport.common.graphics.layer.ILayer;
import jadex.bdi.planlib.simsupport.environment.ISimulationEngine;
import jadex.bdi.planlib.simsupport.environment.simobject.SimObject;
import jadex.bdi.planlib.simsupport.environment.simobject.task.MoveObjectTask;
import jadex.bdi.runtime.IBeliefbase;
import jadex.bdi.runtime.Plan;

public class LocalUpdateDisplayPlan extends Plan
{
	public void body()
	{
		IBeliefbase b = getBeliefbase();
		ISimulationEngine engine = (ISimulationEngine) b.getBelief("local_simulation_engine").getFact();
		IViewport viewport = (IViewport) b.getBelief("viewport").getFact();
		
		List preLayers = (List) b.getBelief("prelayers").getFact();
		List preLayerAccess = engine.getPreLayerAccess();
		
		synchronized(preLayerAccess)
		{
			if (!preLayerAccess.equals(preLayers))
			{
				preLayers.clear();
				for (Iterator it = preLayerAccess.iterator(); it.hasNext(); )
				{
					ILayer layer = (ILayer) it.next();
					preLayers.add(layer.copy());
				}
				viewport.setPreLayers(preLayers);
			}
		}
		
		List postLayers = (List) b.getBelief("postlayers").getFact();
		List postLayerAccess = engine.getPostLayerAccess();
		
		synchronized(postLayerAccess)
		{
			if (!postLayerAccess.equals(postLayers))
			{
				postLayers.clear();
				for (Iterator it = postLayerAccess.iterator(); it.hasNext(); )
				{
					ILayer layer = (ILayer) it.next();
					postLayers.add(layer.copy());
				}
				viewport.setPostLayers(postLayers);
			}
		}
		
		Map drawables = (Map) b.getBelief("drawables").getFact();
		
		Map objectAccess = engine.getSimObjectAccess();
		
		synchronized(objectAccess)
		{
			Set engineObjIds = objectAccess.keySet();
			HashSet destroyedObjects = new HashSet(drawables.keySet());
			destroyedObjects.removeAll(engineObjIds);
			
			for (Iterator it = destroyedObjects.iterator(); it.hasNext(); )
			{
				Integer objectId = (Integer) it.next();
				IDrawable d = (IDrawable) drawables.remove(objectId);
				viewport.removeDrawable(d);
			}
			destroyedObjects = null;
			
			HashSet createdObjects = new HashSet(engineObjIds);
			createdObjects.removeAll(drawables.keySet());
			
			for (Iterator it = createdObjects.iterator(); it.hasNext(); )
			{
				Integer objectId = (Integer) it.next();
				IDrawable d = ((SimObject) objectAccess.get(objectId)).getDrawable().copy();
				drawables.put(objectId, d);
				viewport.addDrawable(d);
			}
			
			for (Iterator it = objectAccess.entrySet().iterator(); it.hasNext(); )
			{
				Map.Entry entry = (Map.Entry) it.next();
				Integer objectId = (Integer) entry.getKey();
				SimObject so = (SimObject) entry.getValue();
				IDrawable d = (IDrawable) drawables.get(objectId);
				synchronized (so)
				{
					d.setPosition(so.getPositionAccess());
				}
				MoveObjectTask moveTask = (MoveObjectTask) so.getTask(MoveObjectTask.DEFAULT_NAME);
				if (moveTask != null)
				{
					synchronized (moveTask)
					{
						d.setVelocity(moveTask.getVelocityAccess());
					}
				}
			}
		}
		
		viewport.refresh();
	}
}
