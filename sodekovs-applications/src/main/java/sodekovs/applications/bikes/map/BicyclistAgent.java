package sodekovs.applications.bikes.map;

import jadex.bridge.IComponentManagementService;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

import org.jdesktop.swingx.mapviewer.GeoPosition;

@RequiredServices({ 
		@RequiredService(name = "mapservice", type = IMapService.class),
		@RequiredService(name = "cmsservice", type = IComponentManagementService.class, binding = @Binding(scope = RequiredServiceInfo.SCOPE_PLATFORM))
		})
public class BicyclistAgent extends MicroAgent {

	/* (non-Javadoc)
	 * @see jadex.micro.MicroAgent#agentCreated()
	 */
	@Override
	public IFuture agentCreated() {
		return super.agentCreated();
	}

	/* (non-Javadoc)
	 * @see jadex.micro.MicroAgent#executeBody()
	 */
	@Override
	public void executeBody() {
		this.getRequiredService("mapservice").addResultListener(new IResultListener() {
			
			@Override
			public void resultAvailable(Object result) {
				IMapService mapService = (IMapService) result;
				
				if (getAgentName().equals("Bicyclist1")) {
					waitForTick(new TravelStep(mapService, MapViewer.POSITION_HAMBURG));
					waitFor(30000, new TravelStep(mapService, MapViewer.POSITION_WIEN));
				} else if (getAgentName().equals("Bicyclist2")) {
					waitForTick(new TravelStep(mapService, MapViewer.POSITION_LONDON));
					waitFor(30000, new TravelStep(mapService, MapViewer.POSITION_PARIS));
				} else if (getAgentName().equals("Bicyclist3")) {
					waitForTick(new TravelStep(mapService, MapViewer.POSITION_WASHINGTON));
					waitFor(30000, new TravelStep(mapService, MapViewer.POSITION_MONTREAL));
				}
			}
			
			@Override
			public void exceptionOccurred(Exception exception) {
				exception.printStackTrace();
			}
		});
	}
	
	/* (non-Javadoc)
	 * @see jadex.micro.MicroAgent#agentKilled()
	 */
	@Override
	public IFuture agentKilled() {
		return super.agentKilled();
	}
	
	private class TravelStep implements IComponentStep {
		
		private IMapService mapService = null;
		private GeoPosition position = null;
		
		public TravelStep(IMapService mapService, GeoPosition position) {
			this.mapService = mapService;
			this.position = position;
		}

		@Override
		public Object execute(IInternalAccess ia) {
			mapService.registerComponent(getComponentIdentifier(), position);
			
			return null;
		}		
	}
}
