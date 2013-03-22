package sodekovs.bikesharing.pedestrian;

import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.modelinfo.IExtensionInstance;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.ContinuousSpace2D;
import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.math.Vector2Double;
import jadex.micro.MicroAgent;

import java.util.HashMap;
import java.util.Map;

import sodekovs.bikesharing.pedestrian.movement.MoveTask;

/**
 * Micro agent implementation for pedestrians.
 * 
 * @author Thomas Preisler
 */
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
				// get the important variables
				environment = (ContinuousSpace2D) result;
				myself = environment.getAvatar(getComponentDescription());
				int behaviourStrategy = ((Integer) myself.getProperty("behaviour_strategy")).intValue();

				// choose the behaviour
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
	
	

	/**
	 * Component step for the realization of the station to random station default behavior.
	 */
	private class WalkAroundStep implements IComponentStep<Void> {

		@Override
		public IFuture<Void> execute(IInternalAccess ia) {
			throw new RuntimeException("WalkAroundStep and behaviour not yet implemented");
		}

	}

	/**
	 * Component step for the realization of station to station behavior.
	 */
	private class StationToStationStep implements IComponentStep<Void> {

		@Override
		public IFuture<Void> execute(IInternalAccess ia) {
			// get destination
			final Vector2Double dest = (Vector2Double) myself.getProperty("destination_station_pos");
			// rent bike
			rentBike(new DefaultResultListener<Object>() {

				@Override
				public void resultAvailable(Object result) {
					// move to destination
					moveToDestination(dest, new DefaultResultListener<Object>() {

						@Override
						public void resultAvailable(Object result) {
							// return bike
							returnBike(new DefaultResultListener<Object>() {

								@Override
								public void resultAvailable(Object result) {
									//Kill Agent via ISpaceObject
									environment.destroySpaceObject(myself.getId());
									// get yourself killed
									killAgent();
								}

							});
						}
					});
				}
			});

			return IFuture.DONE;
		}
	}

	/**
	 * Component step for the realization of the adaptable station to station behavior.
	 */
	private class AdaptableStationToStationStep implements IComponentStep<Void> {

		/**
		 * The destination
		 */
		private Vector2Double dest = null;
		
		/**
		 * Result listener for the rent bike task
		 */
		private DefaultResultListener<Object> rentListener =  new DefaultResultListener<Object>() {

			@Override
			public void resultAvailable(Object result) {
				// after renting move to destination
				moveToDestination(dest, new DefaultResultListener<Object>() {

					@Override
					public void resultAvailable(Object result) {
						// check there for alternative stations
						String checkProposedArrivalStation = checkStation("proposed_arrival_station");
						if (checkProposedArrivalStation != null) {
							// go to alternative arrival station
							ISpaceObject[] allBikestations = environment.getSpaceObjectsByType("bikestation");
							for (ISpaceObject bikestation : allBikestations) {
								if (bikestation.getProperty("stationID").equals(checkProposedArrivalStation)) {
									// move to alternative station
									moveToDestination((IVector2) bikestation.getProperty("position"), new DefaultResultListener<Object>() {

										@Override
										public void resultAvailable(Object result) {
											// return bike there
											returnBike(returnListener);
										}

										@Override
										public void exceptionOccurred(Exception exception) {
											exception.printStackTrace();
										}
									});
								}
							}
						} else {
							//return bike at destination
							returnBike(returnListener);
						}
					}
				});
			}
		};
		
		/**
		 * Result listener for the return bike task
		 */
		private DefaultResultListener<Object> returnListener = new DefaultResultListener<Object>() {

			@Override
			public void resultAvailable(Object result) {
				//Kill Agent via ISpaceObject
				environment.destroySpaceObject(myself.getId());
				// get yourself killed after returning
				killAgent();
			}

			@Override
			public void exceptionOccurred(Exception exception) {
				exception.printStackTrace();
			}
		};

		@Override
		public IFuture<Void> execute(IInternalAccess ia) {
			// get destination
			dest = (Vector2Double) myself.getProperty("destination_station_pos");
			
			// check alternative stations
			String checkProposedDepartureStation = checkStation("proposed_departure_station");
			if (checkProposedDepartureStation != null) {
				// go to alternative departure station
				ISpaceObject[] allBikestations = environment.getSpaceObjectsByType("bikestation");
				for (ISpaceObject bikestation : allBikestations) {
					if (bikestation.getProperty("stationID").equals(checkProposedDepartureStation)) {
						// if available move to alternative station
						moveToDestination((IVector2) bikestation.getProperty("position"), new DefaultResultListener<Object>() {

							@Override
							public void resultAvailable(Object result) {
								// rent a bike there
								rentBike(rentListener);
							}

						});
					}
				}
			} else {
				// rent a bike
				rentBike(rentListener);
			}
			
			return IFuture.DONE;
		}

		/**
		 * Check if the station at the current position (of myself) offers alternative arrival or departure stations.
		 * 
		 * @param propertyToCheck
		 * @return
		 */
		private String checkStation(String propertyToCheck) {
			ISpaceObject[] allBikestations = environment.getSpaceObjectsByType("bikestation");
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
	}

	/**
	 * Call the {@link MoveTask}.
	 * 
	 * @param dest the given destination
	 * @param res the result listener to call after executing the task
	 */
	private void moveToDestination(IVector2 dest, IResultListener<Object> res) {
		Map<String, Object> props = new HashMap<String, Object>();
		props.put(MoveTask.PROPERTY_DESTINATION, dest);
		props.put(MoveTask.ACTOR_ID, myself.getId());
		props.put(MoveTask.PROPERTY_SCOPE, getExternalAccess());
		Object taskid = environment.createObjectTask(MoveTask.PROPERTY_TYPENAME, props, myself.getId());
		environment.addTaskListener(taskid, myself.getId(), res);
	}

	/**
	 * Call the {@link ReturnBikeTask}.
	 * 
	 * @param res the result listener to call after executing the task
	 */
	private void rentBike(IResultListener<Object> res) {
		Map<String, Object> props = new HashMap<String, Object>();
		props.put(RentBikeTask.ACTOR_ID, myself.getId());
		Object taskid = environment.createObjectTask(RentBikeTask.PROPERTY_TYPENAME, props, myself.getId());
		environment.addTaskListener(taskid, myself.getId(), res);
	}

	/**
	 * Call the {@link ReturnBikeTask}.
	 * 
	 * @param res the result listener to call after executing the task
	 */
	private void returnBike(IResultListener<Object> res) {
		Map<String, Object> props = new HashMap<String, Object>();
		props.put(ReturnBikeTask.ACTOR_ID, myself.getId());
		Object taskid = environment.createObjectTask(ReturnBikeTask.PROPERTY_TYPENAME, props, myself.getId());
		environment.addTaskListener(taskid, myself.getId(), res);
	}
}