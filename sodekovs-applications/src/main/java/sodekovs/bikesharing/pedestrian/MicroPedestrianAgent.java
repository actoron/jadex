package sodekovs.bikesharing.pedestrian;

import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.modelinfo.IExtensionInstance;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.extension.envsupport.environment.IEnvironmentSpace;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.ContinuousSpace2D;
import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.math.Vector2Double;
import jadex.micro.MicroAgent;

import java.util.HashMap;
import java.util.Map;

import sodekovs.bikesharing.pedestrian.movement.MoveTask;

public class MicroPedestrianAgent extends MicroAgent {

	private ContinuousSpace2D environment = null;
	private ISpaceObject myself = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see jadex.micro.MicroAgent#agentCreated()
	 */
	@Override
	public IFuture<Void> agentCreated() {
		getParentAccess().getExtension("my2dspace").addResultListener(new DefaultResultListener<IExtensionInstance>() {

			@Override
			public void resultAvailable(IExtensionInstance result) {
				environment = (ContinuousSpace2D) result;
				myself = environment.getAvatar(getComponentDescription());
				int behaviourStrategy = ((Integer) myself.getProperty("behaviour_strategy")).intValue();

				if (behaviourStrategy == 0) {
					// Default-Behaviour-Strategy
					scheduleStep(new WalkAroundStep());
				} else if (behaviourStrategy == 1) {
					// Drive from station to station strategy
					scheduleStep(new StationToStationStep());
				} else if (behaviourStrategy == 2) {
					scheduleStep(new AdaptableStationToStationStep());
				} else {
					System.out.println("#MicroPedestrian# Error: No strategy found for pedestrian....");
				}

			}

			@Override
			public void exceptionOccurred(Exception exception) {
				exception.printStackTrace();
			}
		});
		
		return IFuture.DONE;
	}
	
	private class WalkAroundStep implements IComponentStep<Void> {

		@Override
		public IFuture<Void> execute(IInternalAccess ia) {
			throw new RuntimeException("WalkAroundStep and behaviour not yet implemented");
		}
		
	}
	
	private class StationToStationStep implements IComponentStep<Void> {

		@Override
		public IFuture<Void> execute(IInternalAccess ia) {
			throw new RuntimeException("StationToStationStep and behaviour not yet implemented");
		}
		
	}
	
	private class AdaptableStationToStationStep implements IComponentStep<Void> {

		private Vector2Double dest = null;
		
		@Override
		public IFuture<Void> execute(IInternalAccess ia) {
			dest = (Vector2Double) myself.getProperty("destination_station_pos");
			String checkProposedDepartureStation = checkStation(environment, myself, "proposed_departure_station");
			
			if (checkProposedDepartureStation != null) {
				// go to alternative departure station
				ISpaceObject[] allBikestations = environment.getSpaceObjectsByType("bikestation");
				for (ISpaceObject bikestation : allBikestations) {
					if (bikestation.getProperty("stationID").equals(checkProposedDepartureStation)) {
						moveToDestination((IVector2) bikestation.getProperty("position"), new DefaultResultListener<Object>() {

							@Override
							public void resultAvailable(Object result) {
								rentBike();
							}
							
							@Override
							public void exceptionOccurred(Exception exception) {
								exception.printStackTrace();
							}
						});
					}
				}
			}
			
			rentBike();
			return IFuture.DONE;
		}
		
		private void rentBike() {
			Map<String, Object> props = new HashMap<String, Object>();
			props.put(RentBikeTask.ACTOR_ID, myself.getId());
			Object taskid = environment.createObjectTask(RentBikeTask.PROPERTY_TYPENAME, props, myself.getId());
			environment.addTaskListener(taskid, myself.getId(), new DefaultResultListener<Object>() {

				@Override
				public void resultAvailable(Object result) {
					moveToDestination(dest, new DefaultResultListener<Object>() {

						@Override
						public void resultAvailable(Object result) {
							String checkProposedArrivalStation = checkStation(environment, myself, "proposed_arrival_station");
							if (checkProposedArrivalStation != null) {
								// go to alternative arrival station
								ISpaceObject[] allBikestations = environment.getSpaceObjectsByType("bikestation");
								for (ISpaceObject bikestation : allBikestations) {
									if (bikestation.getProperty("stationID").equals(checkProposedArrivalStation)) {
										moveToDestination((IVector2) bikestation.getProperty("position"), new DefaultResultListener<Object>() {

											@Override
											public void resultAvailable(Object result) {
												returnBike();
											}
											
											@Override
											public void exceptionOccurred(Exception exception) {
												exception.printStackTrace();
											}
										});
									}
								}
							}
							returnBike();
						}
					});
				}
			});
		}
		
		/**
		 * Return a bike at a station.
		 * 
		 * @param env
		 * @param myself
		 */
		private void returnBike() {
			Map<String, Object> props = new HashMap<String, Object>();
			props.put(ReturnBikeTask.ACTOR_ID, myself.getId());
			Object taskid = environment.createObjectTask(ReturnBikeTask.PROPERTY_TYPENAME, props, myself.getId());
			environment.addTaskListener(taskid, myself.getId(), new DefaultResultListener<Object>() {

				@Override
				public void resultAvailable(Object result) {
					killAgent();
				}
				
				@Override
				public void exceptionOccurred(Exception exception) {
					exception.printStackTrace();
				}
			});
		}
		
		private String checkStation(IEnvironmentSpace space, ISpaceObject myself, String propertyToCheck) {
			ContinuousSpace2D contSpace = (ContinuousSpace2D) space;
			ISpaceObject[] allBikestations = contSpace.getSpaceObjectsByType("bikestation");
			String res = null;

			// Get the "right" station.
			for (ISpaceObject bikestation : allBikestations) {
				if (bikestation.getProperty(ContinuousSpace2D.PROPERTY_POSITION).equals(myself.getProperty(ContinuousSpace2D.PROPERTY_POSITION))) {
					// If res == null: nothing to do; If res = "some station id" then adapt
					res = (String) bikestation.getProperty(propertyToCheck);
					break;
				}
			}
			return res;
		}
		
		/**
		 * Move to a destination task.
		 * 
		 * @param dest
		 * @param env
		 * @param myself
		 */
		private void moveToDestination(IVector2 dest, IResultListener<Object> res) {
			Map<String, Object> props = new HashMap<String, Object>();
			props.put(MoveTask.PROPERTY_DESTINATION, dest);
			props.put(MoveTask.ACTOR_ID, myself.getId());
			props.put(MoveTask.PROPERTY_SCOPE, getExternalAccess());
			Object taskid = environment.createObjectTask(MoveTask.PROPERTY_TYPENAME, props, myself.getId());
			environment.addTaskListener(taskid, myself.getId(), res);
		}
	}
}