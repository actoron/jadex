package jadex.bdi.planlib.simsupport.observer.capability;

import jadex.bdi.planlib.simsupport.common.graphics.IViewport;
import jadex.bdi.planlib.simsupport.common.graphics.drawable.DrawableCombiner;
import jadex.bdi.planlib.simsupport.common.graphics.drawable.IDrawable;
import jadex.bdi.planlib.simsupport.common.math.IVector2;
import jadex.bdi.planlib.simsupport.environment.ISimulationEngine;
import jadex.bdi.planlib.simsupport.environment.simobject.SimObject;
import jadex.bdi.planlib.simsupport.environment.simobject.task.MoveObjectTask;
import jadex.bdi.runtime.IBeliefbase;
import jadex.bdi.runtime.Plan;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class LocalUpdateDisplayPlan extends Plan
{
	public void body()
	{
		IBeliefbase b = getBeliefbase();
		
		int fps = ((Integer) b.getBelief("frame_rate").getFact()).intValue();
		int delay = 0;
		if (fps > 0)
		{
			delay = 1000 / fps;
		}
		waitFor(delay);
		
		ISimulationEngine engine = (ISimulationEngine) b.getBelief("local_simulation_engine").getFact();
		IViewport viewport = (IViewport) b.getBelief("viewport").getFact();
		if (!viewport.isShowing())
		{
			return;
		}
		
		List themes = (List) b.getBelief("object_themes").getFact();
		Integer themeId = (Integer) b.getBelief("selected_theme").getFact();
		Map theme = (Map) themes.get(themeId.intValue());
		
		Map objectAccess = engine.getSimObjectAccess();
		Map typedAccess = engine.getTypedSimObjectAccess();
		
		List objectList = null;
		synchronized(objectAccess)
		{
			synchronized(typedAccess)
			{
				objectList = new ArrayList(objectAccess.size());
				Set entrySet = typedAccess.entrySet();
				for (Iterator it = entrySet.iterator(); it.hasNext(); )
				{
					Map.Entry entry = (Entry) it.next();
					DrawableCombiner d = (DrawableCombiner) theme.get((String) entry.getKey());
					List objects = (List) entry.getValue();
					for (Iterator it2 = objects.iterator(); it2.hasNext(); )
					{
						Object[] viewObj = new Object[3];
						SimObject so = (SimObject) it2.next();
						viewObj[0] = so.getPosition();
						IVector2 vel = ((IVector2) so.getProperty("velocity"));
						if (vel != null)
						{
							viewObj[1] = vel.copy();
						}
						viewObj[2] = d;
						objectList.add(viewObj);
					}
				}
			}
			
			Integer markedObject = (Integer) b.getBelief("marked_object").getFact();
			SimObject mObj = null;
			if (markedObject != null)
			{
				mObj = (SimObject) objectAccess.get(markedObject);
			}
			if (mObj != null)
			{
				IVector2 size = ((DrawableCombiner) theme.get(mObj.getType())).getSize();
				size.multiply(2.0);
				Object[] viewObj = new Object[3];
				DrawableCombiner marker = (DrawableCombiner) b.getBelief("object_marker").getFact();;
				marker.setDrawableSizes(size);
				viewObj[0] = mObj.getPosition();
				viewObj[2] = marker;
				objectList.add(viewObj);
			}
			else
			{
				b.getBelief("marked_object").setFact(null);
			}
		}
		
		Comparator drawOrder = (Comparator) b.getBelief("draw_order").getFact();
		if (drawOrder != null)
		{
			Collections.sort(objectList, drawOrder);
		}
		
		viewport.setObjectList(objectList);
		viewport.refresh();
	}
}
