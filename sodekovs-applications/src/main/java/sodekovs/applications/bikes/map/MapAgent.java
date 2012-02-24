package sodekovs.applications.bikes.map;

import jadex.bridge.IComponentIdentifier;
import jadex.commons.future.IFuture;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Description;

import java.util.HashMap;
import java.util.Map;

import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.jdesktop.swingx.mapviewer.Waypoint;

@Description("Agent offering services to display agents on a OpenStreetMaps based Map.")
//@ProvidedServices({@ProvidedService(type=IMapService.class, implementation=@Implementation(MapService.class))})
public class MapAgent extends MicroAgent {
	
	private Map<IComponentIdentifier, Waypoint> positions = null;
	
	private MapViewer map = null;

	/* (non-Javadoc)
	 * @see jadex.micro.MicroAgent#agentCreated()
	 */
	@Override
	public IFuture agentCreated() {
		positions = new HashMap<IComponentIdentifier, Waypoint>();
		
		map = new MapViewer();
		map.setVisible(true);
		
		addService("mapservice", IMapService.class, new MapService(this));
		
		return IFuture.DONE;
	}

	/* (non-Javadoc)
	 * @see jadex.micro.MicroAgent#executeBody()
	 */
	@Override
	public IFuture<Void> executeBody() {
		// TODO Auto-generated method stub
		super.executeBody();
		return IFuture.DONE;
	}

	/* (non-Javadoc)
	 * @see jadex.micro.MicroAgent#agentKilled()
	 */
	@Override
	public IFuture agentKilled() {
		// TODO Auto-generated method stub
		return super.agentKilled();
	}
	
	public Waypoint putPosition(IComponentIdentifier identifier, GeoPosition position) {
		Waypoint waypoint = this.positions.get(identifier);
		if (waypoint != null) {
			map.removeWaypoint(waypoint);			
		}
		waypoint = new Waypoint(position);
		map.addWaypoint(waypoint);		
		
		return this.positions.put(identifier, waypoint);
	}	
}