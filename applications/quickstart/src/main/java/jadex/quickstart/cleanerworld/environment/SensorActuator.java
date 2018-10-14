package jadex.quickstart.cleanerworld.environment;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.component.impl.ExecutionComponentFeature;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.clock.IClockService;
import jadex.bridge.service.types.clock.ITimedObject;
import jadex.commons.ErrorException;
import jadex.commons.SUtil;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.quickstart.cleanerworld.environment.impl.Cleaner;
import jadex.quickstart.cleanerworld.environment.impl.Environment;
import jadex.quickstart.cleanerworld.environment.impl.Location;
import jadex.quickstart.cleanerworld.environment.impl.LocationObject;
import jadex.quickstart.cleanerworld.environment.impl.Pheromone;
import jadex.quickstart.cleanerworld.environment.impl.Waste;
import jadex.quickstart.cleanerworld.environment.impl.Wastebin;

/**
 *  The sensor / actuator gives access to the perceived environment
 *  and provides operations to manipulate the environment.
 *  Each cleaner agent should create its own sensor/actuator.
 */
public class SensorActuator
{
	//-------- attributes --------
	
	/** The agent. */
	private IInternalAccess agent;
	
	/** The cleaner. */
	private Cleaner	self;
	
	/** The current movement target, if any. */
	private Location	target;
	
	/** The pheromone to disperse (if any). */
	private String	pheromone;
	
	/** The known other cleaners. */
	private Set<ICleaner>	cleaners;
	
	/** The known waste pieces. */
	private Set<IWaste>	wastes;
	
	/** The known charging stations. */
	private Set<IChargingstation>	chargingstations;
	
	/** The known waste boins. */
	private Set<IWastebin>	wastebins;
	
	//-------- constructors --------
	
	/**
	 *  Create a sensor for a new cleaner robot.
	 */
	public SensorActuator()
	{
		this(null, null, null, null);
	}
	
	/**
	 *  Create a sensor for a new cleaner robot.
	 */
	public SensorActuator(Set<IWaste> wastes, Set<IWastebin> wastebins, Set<IChargingstation> stations, Set<ICleaner> cleaners)
	{
		this.agent	= ExecutionComponentFeature.LOCAL.get();
		if(agent==null)
		{
			throw new IllegalStateException("Must be called on agent thread. Failed to find agent for sensor/actuator.");
		}

		self	= Environment.getInstance().createCleaner(agent);
		this.cleaners	= cleaners!=null ? cleaners : new LinkedHashSet<>();
		this.wastes	= wastes!=null ? wastes : new LinkedHashSet<>();
		this.chargingstations	= stations!=null ? stations : new LinkedHashSet<>();
		this.wastebins	= wastebins!=null ? wastebins : new LinkedHashSet<>();
	}
	
	//-------- sensor methods --------
	
	/**
	 *  Get the knowledge about the cleaner itself.
	 *  @return The cleaner object.
	 */
	public ICleaner getSelf()
	{
		if(!agent.getFeature(IExecutionFeature.class).isComponentThread())
		{
			throw new IllegalStateException("Error: Must be called on agent thread.");
		}

		return self;
	}
	
	/**
	 *  Check, if it is at day or at night.
	 *  @return true, if at day.
	 */
	public boolean isDaytime()
	{
		return Environment.getInstance().getDaytime();
	}
	
	/**
	 *  Get the known other cleaners.
	 *  @return a Set of Cleaner objects. 
	 */
	public Set<ICleaner>	getCleaners()
	{
		if(!agent.getFeature(IExecutionFeature.class).isComponentThread())
		{
			throw new IllegalStateException("Error: Must be called on agent thread.");
		}
		return cleaners;
	}
	
	/**
	 *  Get the known waste pieces.
	 *  @return a Set of Waste objects. 
	 */
	public Set<IWaste>	getWastes()
	{
		if(!agent.getFeature(IExecutionFeature.class).isComponentThread())
		{
			throw new IllegalStateException("Error: Must be called on agent thread.");
		}
		return wastes;
	}
		
	/**
	 *  Get the known charging stations.
	 *  @return a Set of Chargingstation objects. 
	 */
	public Set<IChargingstation>	getChargingstations()
	{
		if(!agent.getFeature(IExecutionFeature.class).isComponentThread())
		{
			throw new IllegalStateException("Error: Must be called on agent thread.");
		}
		return chargingstations;
	}
		
	/**
	 *  Get the known waste pieces.
	 *  @return a Set of Waste objects. 
	 */
	public Set<IWastebin>	getWastebins()
	{
		if(!agent.getFeature(IExecutionFeature.class).isComponentThread())
		{
			throw new IllegalStateException("Error: Must be called on agent thread.");
		}
		return wastebins;
	}
		
	/**
	 *  Get the currently perceived pheromones.
	 *  @return a Set of Pheromone objects. 
	 */
	public Set<IPheromone>	getPheromones()
	{
		if(!agent.getFeature(IExecutionFeature.class).isComponentThread())
		{
			throw new IllegalStateException("Error: Must be called on agent thread.");
		}
		
		Set<IPheromone>	ret	= new LinkedHashSet<>(Arrays.asList(Environment.getInstance().getPheromones()));
		for(Iterator<IPheromone> phi= ret.iterator(); phi.hasNext(); )
		{
			IPheromone	ph	= phi.next();
			if(ph.getLocation().getDistance(self.getLocation())>self.getVisionRange())
			{
				phi.remove();
			}
		}
		
		return ret;
	}
		
	//-------- actuator methods --------
	
	/**
	 *  Move to the given location.
	 *  Blocks until the location is reached or a failure occurs.
	 *  @param location	The location.
	 */
	public void moveTo(ILocation location)
	{
		moveTo(location.getX(), location.getY());
	}
	
	/**
	 *  Move to the given location.
	 *  Blocks until the location is reached or a failure occurs.
	 *  @param x	X coordinate.
	 *  @param y	Y coordinate.
	 */
	public void moveTo(double x, double y)
	{
		if(!agent.getFeature(IExecutionFeature.class).isComponentThread())
		{
			throw new IllegalStateException("Error: Must be called on agent thread.");
		}
		if(target!=null)
		{
			throw new IllegalStateException("Cannot move to multiple targets simultaneously. Target exists: "+target);
		}
		this.target	= new Location(x, y);
		
		// Signal variable to check when location is reached.
		final Future<Void>	reached	= new Future<>();

		// Finish or repeat?
		if(self.getLocation().isNear(target))
		{
			// Release block.
			reached.setResultIfUndone(null);
		}
		else
		{
			// Wait for clock ticks and move the cleaner. (asynchronous!)
			final IClockService	clock	= agent.getFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>(IClockService.class));
			clock.createTickTimer(new ITimedObject()
			{
				long lasttime	= clock.getTime();
				ITimedObject	timer	= this;
				
				@Override
				public void timeEventOccurred(long currenttime)
				{
					if(!reached.isDone())	// no new timer when future is terminated from outside (e.g. agent killed)
					{
						// Run update on agent thread to avoid synchronization issues.
						agent.getFeature(IExecutionFeature.class).scheduleStep(new IComponentStep<Void>()
						{
							@Override
							public IFuture<Void> execute(IInternalAccess ia)
							{
								// Calculate time passed as fraction of a second.
								double	delta	= (currenttime-lasttime)/1000.0;
								
								// Set new charge state
								double	chargestate	= self.getChargestate()-delta/100; 	// drop ~ 1%/sec while moving.
								if(chargestate<0)
								{
									self.setChargestate(0);
									throw new IllegalStateException("Out of battery!");
								}
								self.setChargestate(chargestate);
								
								// Set new location
								double total_dist	= self.getLocation().getDistance(target);
								double move_dist	= Math.min(total_dist, 0.1*delta);	// speed ~ 0.1 units/sec 
								double dx = (target.getX()-self.getLocation().getX())*move_dist/total_dist;
								double dy = (target.getY()-self.getLocation().getY())*move_dist/total_dist;
								self.setLocation(new Location(self.getLocation().getX()+dx, self.getLocation().getY()+dy));
								
								// Post new own state to environment
								Environment.getInstance().updateCleaner(self);
								
								// Add pheromone (if any).
								if(pheromone!=null)
								{
									Pheromone	ph	= new Pheromone(self.getLocation(), pheromone);
									Environment.getInstance().addPheromone(ph);
								}
	
								// Get new external state from environment.
								update();
								
								// Finish or repeat?
								if(self.getLocation().isNear(target))
								{
									// Release block.
									reached.setResultIfUndone(null);
								}
								else
								{
									// Wait for next tick.
									lasttime	= currenttime;
									clock.createTickTimer(timer);
								}
								return IFuture.DONE;
							}
						}).addResultListener(new IResultListener<Void>()
						{
							@Override
							public void exceptionOccurred(Exception exception)
							{
								reached.setExceptionIfUndone(exception);
							}
							
							@Override
							public void resultAvailable(Void result){}
						});
					}
				}
			});
		}
		
		try
		{
			reached.get();	// Block agent/plan until location is reached.
		}
		catch(Throwable t)
		{
			// Move interrupted -> set exception to abort move steps.
			reached.setExceptionIfUndone(t instanceof Exception ? (Exception)t : new ErrorException((Error)t));
			SUtil.throwUnchecked(t);
		}
		finally
		{
			// After move finished/failed always reset state.
			target	= null;
			pheromone	= null;
		}
	}
	
	
	
	/**
	 *  Recharge a cleaner at a charging station to a desired charging level.
	 *  The cleaner needs to be at the location of the charging station
	 *  Note, the charging rate gets slower over 70% charge state.
	 *  @param chargingstation The charging station to recharge at.
	 *  @param level	The desired charging level between 0 and 1.
	 */
	public synchronized void	recharge(IChargingstation chargingstation, double level)
	{
		if(!agent.getFeature(IExecutionFeature.class).isComponentThread())
		{
			throw new IllegalStateException("Error: Must be called on agent thread.");
		}
		
		// Signal variable to check when level is reached.
		final Future<Void>	reached	= new Future<>();
		
		// Wait for clock ticks and recharge the cleaner. (asynchronous!)
		final IClockService	clock	= agent.getFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>(IClockService.class));
		clock.createTickTimer(new ITimedObject()
		{
			long lasttime	= clock.getTime();
			ITimedObject	timer	= this;
			
			@Override
			public void timeEventOccurred(long currenttime)
			{
				if(!reached.isDone())	// no new timer when future is terminated from outside (e.g. agent killed)
				{
					// Run update on agent thread to avoid synchronization issues.
					agent.getFeature(IExecutionFeature.class).scheduleStep(new IComponentStep<Void>()
					{
						@Override
						public IFuture<Void> execute(IInternalAccess ia)
						{
							// Check the location.
							if(!self.getLocation().isNear(chargingstation.getLocation()))
							{
								throw new IllegalStateException("Cannot not recharge. Charging station out of reach!");
							}
							
							// Calculate time passed as fraction of a second.
							double	delta	= (currenttime-lasttime)/1000.0;
							
							// Set new charge state
							double	inc	= delta/10; 	// increase ~ 10%/sec while recharging.
							if(self.getChargestate()>0.7)	// when >70% linearily decrease charging rate to 0 at 100%.
							{
								inc	= inc * 10/3.0 * (1-self.getChargestate());
							}
							self.setChargestate(self.getChargestate()+inc);
							
							// Post new own state to environment
							Environment.getInstance().updateCleaner(self);
							
							// Finish or repeat?
							if(self.getChargestate()>=level)
							{
								// Release block.
								reached.setResultIfUndone(null);
							}
							else
							{
								// Wait for next tick.
								lasttime	= currenttime;
								clock.createTickTimer(timer);
							}
							return null;
						}
					}).addResultListener(new IResultListener<Void>()
					{
						@Override
						public void exceptionOccurred(Exception exception)
						{
							reached.setExceptionIfUndone(exception);
						}
						
						@Override
						public void resultAvailable(Void result){}
					});
				}
			}
		});
		
		try
		{
			reached.get();	// Block agent/plan until level is reached.
		}
		catch(Throwable t)
		{
			// Move interrupted -> set exception to abort recharge steps.
			reached.setExceptionIfUndone(t instanceof Exception ? (Exception)t : new ErrorException((Error)t));
			SUtil.throwUnchecked(t);
		}
	}

	/**
	 *  Get the current movement target, if any.
	 *  @return	The target or null when no current target.
	 */
	public ILocation getTarget()
	{
		return target;
	}
	
	/**
	 *  Try to pick up some piece of waste.
	 *  @param waste The waste.
	 */
	public void	pickUpWaste(IWaste waste)
	{
		// Try action in environment
		Environment.getInstance().pickupWaste(self, (Waste)waste);
		
		// Update local knowledge
		wastes.remove(waste);
		self.setCarriedWaste((Waste)waste);
		((Waste)waste).setLocation(null);
	}

	/**
	 *  Drop a piece of waste.
	 */
	public void dropWasteInWastebin(IWaste waste, IWastebin wastebin)
	{
		// Try action in environment
		Environment.getInstance().dropWasteInWastebin(self, (Waste)waste, (Wastebin)wastebin);
		
		// Update local knowledge
		self.setCarriedWaste(null);
		((Wastebin)wastebin).addWaste(waste);
	}
	
	/**
	 *  Disperse pheromones when moving.
	 *  The dispersion happens during the current/next movoTo() operation and stops automatically afterwards.
	 *  @param type	The pheromone type (can be an arbitrary string).
	 */
	public void	dispersePheromones(String type)
	{
		this.pheromone	= type;
	}
	
	//-------- internal methods --------
	
	/**
	 *  Update the sensor based on current vision.
	 *  Called from activities like moveTo().
	 */
	void	update()
	{
		updateObjects(cleaners, Environment.getInstance().getCleaners());
		updateObjects(wastes, Environment.getInstance().getWastes());
		updateObjects(chargingstations, Environment.getInstance().getChargingstations());
		updateObjects(wastebins, Environment.getInstance().getWastebins());
	}
	
	/**
	 *  Update a set of location objects with the current situation.
	 *  @param oldset	The old set of objects, i.e. the previous knowledge.
	 *  @param newset	The new set of objects, i.e. the current perception.
	 */
	<T extends ILocationObject> void updateObjects(Set<T> oldset, T[] newset)
	{
		Map<T, T>	newmap	= new LinkedHashMap<>();
		for(T o: newset)
		{
			// Special treatment for knowledge about myself.
			if(o.equals(self))
			{
				self.update((Cleaner)o);
//				System.out.println("updated: "+self);
			}
			
			// Store new object in map -> used to apply changes to existing knowledge below
			else
			{
				newmap.put(o, o);
			}
		}
		
		// Apply new perception to previous knowledge.
		for(LocationObject oldobj: oldset.toArray(new LocationObject[oldset.size()]))
		{
			LocationObject	newobj	= (LocationObject)newmap.remove(oldobj);			
			
			// When previous object location in vision range, but current object location (if any) not in range -> remove. 
			if(oldobj.getLocation().getDistance(self.getLocation())<=self.getVisionRange()
				&& (newobj==null || newobj.getLocation().getDistance(self.getLocation())>self.getVisionRange()))
			{
				oldset.remove(oldobj);
//				System.out.println("removed: "+oldobj);
			}
			
			// When new object in vision range -> update knowledge about object.
			if(newobj!=null && newobj.getLocation().getDistance(self.getLocation())<=self.getVisionRange())
			{
				oldobj.update(newobj);
//				System.out.println("updated: "+oldobj);
			}
		}
		
		// Add remaining new objects, when in vision range.
		for(T newobj: newmap.values())
		{
			if(newobj.getLocation().getDistance(self.getLocation())<=self.getVisionRange())
			{
				oldset.add(newobj);
//				System.out.println("added: "+newobj);
			}
		}
	}
}
