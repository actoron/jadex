package sodekovs.applications.bikes.map;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.IService;

import org.jdesktop.swingx.mapviewer.GeoPosition;

public interface IMapService extends IService {
	
	public void registerComponent(IComponentIdentifier identifier, GeoPosition position);
}