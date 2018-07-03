package jadex.base;

import java.util.Collection;
import java.util.Iterator;

import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.RemoteChangeListenerHandler;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.clock.IClock;
import jadex.bridge.service.types.clock.IClockService;
import jadex.bridge.service.types.clock.ITimer;
import jadex.bridge.service.types.simulation.ISimulationService;
import jadex.commons.ChangeEvent;
import jadex.commons.IChangeListener;
import jadex.commons.IRemoteChangeListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.IntermediateFuture;
import jadex.commons.transformation.annotations.Classname;
import jadex.commons.transformation.annotations.IncludeFields;

/**
 *  Helper class for remote access to clock service.
 */
public class SRemoteClock
{
	/**
	 *  Set the dilation.
	 */
	public static IFuture<Void>	setDilation(final double dilation, final IExternalAccess exta)
	{
		final Future<Void>	ret	= new Future<Void>();
		ServiceQuery<IClockService> query = new ServiceQuery<>(IClockService.class);
		query.setScope(RequiredServiceInfo.SCOPE_PLATFORM);
		SServiceProvider.searchService(exta, query)
			.addResultListener(new ExceptionDelegationResultListener<IClockService, Void>(ret)
		{
			public void customResultAvailable(final IClockService cs)
			{
				cs.setDilation(dilation);
				ret.setResult(null);
				
//				SServiceProvider.getService(exta.getServiceProvider(), IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
//					.addResultListener(new ExceptionDelegationResultListener<IComponentManagementService, Void>(ret)
//				{
//					public void customResultAvailable(IComponentManagementService cms)
//					{
//						cms.getExternalAccess(((IService)cs).getServiceIdentifier().getProviderId())
//							.addResultListener(new ExceptionDelegationResultListener<IExternalAccess, Void>(ret)
//						{
//							public void customResultAvailable(IExternalAccess csexta)
//							{
//								// schedule on clock service component, because it is not decoupled.
//								csexta.scheduleStep(new IComponentStep<Void>()
//								{
//									@Classname("setDilation")
//									public IFuture<Void> execute(IInternalAccess ia)
//									{
//										cs.setDilation(dilation);
//										return IFuture.DONE;
//									}
//								}).addResultListener(new DelegationResultListener<Void>(ret));
//							}
//						});
//					}
//				});
			}
		});
		
		return ret;
	}
	
	/**
	 *  Set the delta.
	 */
	public static IFuture<Void>	setDelta(final long delta, final IExternalAccess exta)
	{
		final Future<Void>	ret	= new Future<Void>();
		ServiceQuery<IClockService> query = new ServiceQuery<>(IClockService.class);
		query.setScope(RequiredServiceInfo.SCOPE_PLATFORM);
		SServiceProvider.searchService(exta, query)
			.addResultListener(new ExceptionDelegationResultListener<IClockService, Void>(ret)
		{
			public void customResultAvailable(final IClockService cs)
			{
				cs.setDelta(delta);
				ret.setResult(null);
				
//				SServiceProvider.getService(exta.getServiceProvider(), IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
//					.addResultListener(new ExceptionDelegationResultListener<IComponentManagementService, Void>(ret)
//				{
//					public void customResultAvailable(IComponentManagementService cms)
//					{
//						cms.getExternalAccess(((IService)cs).getServiceIdentifier().getProviderId())
//							.addResultListener(new ExceptionDelegationResultListener<IExternalAccess, Void>(ret)
//						{
//							public void customResultAvailable(IExternalAccess csexta)
//							{
//								// schedule on clock service component, because it is not decoupled.
//								csexta.scheduleStep(new IComponentStep<Void>()
//								{
//									@Classname("setDelta")
//									public IFuture<Void> execute(IInternalAccess ia)
//									{
//										cs.setDelta(delta);
//										return IFuture.DONE;
//									}
//								}).addResultListener(new DelegationResultListener<Void>(ret));
//							}
//						});
//					}
//				});
			}
		});
		
		return ret;
	}
	
	/**
	 *  Add a clock listener.
	 *  @param	id	The listener id for later removal. 
	 */
	public static IIntermediateFuture<ClockState>	addClockListener(final String id, final ISimulationService simservice, IExternalAccess exta)
	{
		final IntermediateFuture<ClockState>	ret	= new IntermediateFuture<ClockState>();
		
		// Local listener object that is passed as reference and invoked remotely.
		final IRemoteChangeListener<?>	rcl	= new IRemoteChangeListener()
		{
			public IFuture<Void> changeOccurred(ChangeEvent event)
			{
				handleEvent(event);
				return IFuture.DONE;
			}
			
			public void	handleEvent(ChangeEvent<?> event)
			{
				if(RemoteChangeListenerHandler.EVENT_BULK.equals(event.getType()))
				{
					Collection<?>	events	= (Collection<?>)event.getValue();
					for(Iterator<?> it=events.iterator(); it.hasNext(); )
					{
						handleEvent((ChangeEvent<?>)it.next());
					}
				}
				else
				{
					ret.addIntermediateResult((ClockState)event.getValue());
				}
			}
		};
		
		// Add remote listener that calls local listener via RMI.
		// Schedule as remote step, because methods are remote excluded
		exta.scheduleStep(new IComponentStep<Void>()
		{
			@Classname("addListener")
			public IFuture<Void> execute(IInternalAccess ia)
			{
				RemoteClockChangeListener	rccl	= new RemoteClockChangeListener(id, ia, rcl, simservice);
				simservice.addChangeListener(rccl);
				simservice.getClockService().addChangeListener(rccl);
			
				// Initial event.
				rccl.changeOccurred(null);
				
				return IFuture.DONE;
			}
		}).addResultListener(new ExceptionDelegationResultListener<Void, Collection<ClockState>>(ret)
		{
			public void customResultAvailable(Void result)
			{
				// success -> keep future alive until deregistered.
			}
		});
		
		return ret;
	}
		
	/**
	 *  Remove a clock listener.
	 *  @param	id	The listener id used for adding. 
	 */
	public static IFuture<Void>	removeClockListener(final String id, final ISimulationService simservice, IExternalAccess exta)
	{
		final Future<Void>	ret	= new Future<Void>();
		
		// Schedule as remote step, because methods are remote excluded
		exta.scheduleStep(new IComponentStep<Void>()
		{
			@Classname("removeListener")
			public IFuture<Void> execute(IInternalAccess ia)
			{
				RemoteClockChangeListener	rccl	= new RemoteClockChangeListener(id, ia, null, simservice);
				simservice.removeChangeListener(rccl);
				simservice.getClockService().removeChangeListener(rccl);
			
				return IFuture.DONE;
			}
		}).addResultListener(new DelegationResultListener<Void>(ret));
		
		return ret;
	}

	/**
	 *  Add a sim listener.
	 *  @param	id	The listener id for later removal. 
	 */
	public static IIntermediateFuture<SimulationState>	addSimulationListener(final String id, final ISimulationService simservice, IExternalAccess exta)
	{
		final IntermediateFuture<SimulationState>	ret	= new IntermediateFuture<SimulationState>();
		
		// Local listener object that is passed as reference and invoked remotely.
		final IRemoteChangeListener<?>	rcl	= new IRemoteChangeListener()
		{
			public IFuture<Void> changeOccurred(ChangeEvent event)
			{
				handleEvent(event);
				return IFuture.DONE;
			}
			
			public void	handleEvent(ChangeEvent<?> event)
			{
				if(RemoteChangeListenerHandler.EVENT_BULK.equals(event.getType()))
				{
					Collection<?>	events	= (Collection<?>)event.getValue();
					for(Iterator<?> it=events.iterator(); it.hasNext(); )
					{
						handleEvent((ChangeEvent<?>)it.next());
					}
				}
				else
				{
					ret.addIntermediateResult((SimulationState)event.getValue());
				}
			}
		};
		
		// Add remote listener that calls local listener via RMI.
		// Schedule as remote step, because methods are remote excluded
		exta.scheduleStep(new IComponentStep<Void>()
		{
			@Classname("addListener")
			public IFuture<Void> execute(IInternalAccess ia)
			{
				RemoteSimServiceChangeListener	rccl	= new RemoteSimServiceChangeListener(id, ia, rcl, simservice);
				simservice.addChangeListener(rccl);
				simservice.getClockService().addChangeListener(rccl);
			
				// Initial event.
				rccl.changeOccurred(null);
				
				return IFuture.DONE;
			}
		}).addResultListener(new ExceptionDelegationResultListener<Void, Collection<SimulationState>>(ret)
		{
			public void customResultAvailable(Void result)
			{
				// success -> keep future alive until deregistered.
			}
		});
		
		return ret;
	}
		
	/**
	 *  Remove a sim listener.
	 *  @param	id	The listener id used for adding. 
	 */
	public static IFuture<Void>	removeSimulationListener(final String id, final ISimulationService simservice, IExternalAccess exta)
	{
		final Future<Void>	ret	= new Future<Void>();
		
		// Schedule as remote step, because methods are remote excluded
		exta.scheduleStep(new IComponentStep<Void>()
		{
			@Classname("removeListener")
			public IFuture<Void> execute(IInternalAccess ia)
			{
				RemoteSimServiceChangeListener	rccl	= new RemoteSimServiceChangeListener(id, ia, null, simservice);
				simservice.removeChangeListener(rccl);
				simservice.getClockService().removeChangeListener(rccl);
			
				return IFuture.DONE;
			}
		}).addResultListener(new DelegationResultListener<Void>(ret));
		
		return ret;
	}


	/**
	 *  Add a timer listener.
	 *  @param	id	The listener id for later removal. 
	 */
	public static IIntermediateFuture<TimerEntries>	addTimerListener(final String id, final ISimulationService simservice, IExternalAccess exta)
	{
		final IntermediateFuture<TimerEntries>	ret	= new IntermediateFuture<TimerEntries>();
		
		// Local listener object that is passed as reference and invoked remotely.
		final IRemoteChangeListener<?>	rcl	= new IRemoteChangeListener()
		{
			public IFuture<Void> changeOccurred(ChangeEvent event)
			{
				handleEvent(event);
				return IFuture.DONE;
			}
			
			public void	handleEvent(ChangeEvent<?> event)
			{
				if(RemoteChangeListenerHandler.EVENT_BULK.equals(event.getType()))
				{
					Collection<?>	events	= (Collection<?>)event.getValue();
					for(Iterator<?> it=events.iterator(); it.hasNext(); )
					{
						handleEvent((ChangeEvent<?>)it.next());
					}
				}
				else
				{
					ret.addIntermediateResult((TimerEntries)event.getValue());
				}
			}
		};
		
		// Add remote listener that calls local listener via RMI.
		// Schedule as remote step, because methods are remote excluded
		exta.scheduleStep(new IComponentStep<Void>()
		{
			@Classname("addListener")
			public IFuture<Void> execute(IInternalAccess ia)
			{
				RemoteTimerChangeListener	rccl	= new RemoteTimerChangeListener(id, ia, rcl, simservice.getClockService());
				simservice.addChangeListener(rccl);
				simservice.getClockService().addChangeListener(rccl);
			
				// Initial event.
				rccl.elementChanged("timers", TimerEntries.getTimerEntries(simservice.getClockService()));
				
				return IFuture.DONE;
			}
		}).addResultListener(new ExceptionDelegationResultListener<Void, Collection<TimerEntries>>(ret)
		{
			public void customResultAvailable(Void result)
			{
				// success -> keep future alive until deregistered.
			}
		});
		
		return ret;
	}
		
	/**
	 *  Remove a timer listener.
	 *  @param	id	The listener id used for adding. 
	 */
	public static IFuture<Void>	removeTimerListener(final String id, final ISimulationService simservice, IExternalAccess exta)
	{
		final Future<Void>	ret	= new Future<Void>();
		
		// Schedule as remote step, because methods are remote excluded
		exta.scheduleStep(new IComponentStep<Void>()
		{
			@Classname("removeListener")
			public IFuture<Void> execute(IInternalAccess ia)
			{
				RemoteTimerChangeListener	rccl	= new RemoteTimerChangeListener(id, ia, null, simservice.getClockService());
				simservice.removeChangeListener(rccl);
				simservice.getClockService().removeChangeListener(rccl);
			
				return IFuture.DONE;
			}
		}).addResultListener(new DelegationResultListener<Void>(ret));
		
		return ret;
	}

	//--------- helper classes --------
	
	/**
	 *  The remote clock change listener.
	 */
	public static class RemoteClockChangeListener	extends RemoteChangeListenerHandler	implements IChangeListener
	{
		//-------- attributes --------
		
		/** The simulation service. */
		protected ISimulationService	simservice;
		
		//-------- constructors --------
		
		/**
		 *  Create a BPMN listener.
		 */
		public RemoteClockChangeListener(String id, IInternalAccess instance, IRemoteChangeListener<?> rcl, ISimulationService simservice)
		{
			super(id, instance, rcl);
			this.simservice	= simservice;
		}
		
		//-------- IChangeListener interface --------
		
		/**
		 *  Called when the process executes.
		 */
		public void changeOccurred(ChangeEvent event)
		{
			// Code in component thread as clock runs on its own thread.
			instance.getComponentFeature(IExecutionFeature.class).scheduleStep(new IComponentStep<Void>()
			{
				public IFuture<Void> execute(IInternalAccess ia)
				{
					simservice.isExecuting().addResultListener(new IResultListener<Boolean>()
					{
						public void resultAvailable(Boolean result)
						{
							try
							{
//								System.out.println("elementChanged");
								boolean	executing	= result.booleanValue();
								IClockService	cs	= simservice.getClockService();
								elementChanged("clock", new ClockState(cs.getClockType(), cs.getTime(), cs.getTick(), cs.getStarttime(),
									cs.getDelta(), IClock.TYPE_CONTINUOUS.equals(cs.getClockType()) ? cs.getDilation() : 0, !executing));
							}
							catch(Exception e)
							{
								exceptionOccurred(e);
							}
						}
						
						public void exceptionOccurred(Exception exception)
						{
							dispose();
						}
					});
					
					return IFuture.DONE;
				}
			});
		}

		/**
		 *  Remove local listeners.
		 */
		protected void dispose()
		{
			super.dispose();
			try
			{
				simservice.removeChangeListener(this);
				simservice.getClockService().removeChangeListener(this);
			}
			catch(Exception e)
			{
				
			}
//			System.out.println("dispose: "+id);
		}
	}
	
	/**
	 *  The remote clock change listener.
	 */
	public static class RemoteSimServiceChangeListener	extends RemoteChangeListenerHandler	implements IChangeListener
	{
		//-------- attributes --------
		
		/** The simulation service. */
		protected ISimulationService	simservice;
		
		/** The last state. */
		protected SimulationState	laststate;
		
		//-------- constructors --------
		
		/**
		 *  Create a BPMN listener.
		 */
		public RemoteSimServiceChangeListener(String id, IInternalAccess instance, IRemoteChangeListener rcl, ISimulationService simservice)
		{
			super(id, instance, rcl);
			this.simservice	= simservice;
		}
		
		//-------- IChangeListener interface --------
		
		/**
		 *  Called when the process executes.
		 */
		public void changeOccurred(ChangeEvent event)
		{
			// Code in component thread as clock runs on its own thread.
			instance.getComponentFeature(IExecutionFeature.class).scheduleStep(new IComponentStep<Void>()
			{
				public IFuture<Void> execute(IInternalAccess ia)
				{
					simservice.isExecuting().addResultListener(new IResultListener<Boolean>()
					{
						public void resultAvailable(Boolean result)
						{
							try
							{
								boolean	executing	= result.booleanValue();
								String	clocktype	= simservice.getClockService().getClockType();
								boolean	clockok	= simservice.getClockService().getNextTimer()!=null;
								
								if(laststate==null || executing!=laststate.executing || clockok!=laststate.clockok
									|| !clocktype.equals(laststate.clocktype))
								{
									laststate	= new SimulationState(executing, clocktype, clockok);
									elementChanged("simulation", laststate);
								}
							}
							catch(Exception e)
							{
								exceptionOccurred(e);
							}
						}
						
						public void exceptionOccurred(Exception exception)
						{
							dispose();
						}
					});
					
					return IFuture.DONE;
				}
			});
		}

		/**
		 *  Remove local listeners.
		 */
		protected void dispose()
		{
			super.dispose();
			try
			{
				simservice.removeChangeListener(this);
				simservice.getClockService().removeChangeListener(this);
			}
			catch(Exception e)
			{
				
			}
//			System.out.println("dispose: "+id);
		}
	}

	
	/**
	 *  Information about the simulation to be transferred.
	 */
	@IncludeFields
	public static class SimulationState
	{
		//-------- attributes --------
		
		/** The execution state. */
		public boolean	executing;
		
		/** The clock type. */
		public String	clocktype;
		
		/** The clock ok flag. */
		public boolean	clockok;
		
		//-------- constructors --------
		
		/**
		 *  Bean constructor.
		 */
		public SimulationState()
		{
		}
		
		/**
		 *  Create a clock state object.
		 */
		public SimulationState(boolean executing, String clocktype, boolean clockok)
		{
			this.executing	= executing;
			this.clocktype	= clocktype;
			this.clockok	= clockok;
		}
		
		//-------- methods --------
		
		/**
		 *  The hash code.
		 *  Overridden to have only one clock state per update.
		 */
		public int hashCode()
		{
			return 123;
		}
		
		/**
		 *  Test if two objects are equal.
		 *  Overridden to have only one clock state per update.
		 */
		public boolean equals(Object obj)
		{
			return obj instanceof SimulationState;
		}

		public String toString()
		{
			return "SimulationState [executing=" + executing + ", clocktype="
					+ clocktype + ", clockok=" + clockok + "]";
		}
	}
	
	/**
	 *  Information about the clock to be transferred.
	 */
	@IncludeFields
	public static class ClockState
	{
		//-------- attributes --------
		
		/** The clock type. */
		public String	type;
		
		/** The current time. */
		public long	time;
		
		/** The current tick. */
		public double	tick;
		
		/** The start time. */
		public long	starttime;
		
		/** The clock delta. */
		public long	delta;
		
		/** The clock dilation. */
		public double	dilation;
		
		/** Changing clock type allowed? */
		public boolean	changeallowed;
		
		//-------- constructors --------
		
		/**
		 *  Bean constructor.
		 */
		public ClockState()
		{
		}
		
		/**
		 *  Create a clock state object.
		 */
		public ClockState(String type, long time, double tick, long starttime, long delta, double dilation, boolean changeallowed)
		{
			this.type	= type;
			this.time	= time;
			this.tick	= tick;
			this.starttime	= starttime;
			this.delta	= delta;
			this.dilation	= dilation;
			this.changeallowed	= changeallowed;
		}
		
		//-------- methods --------
		
		/**
		 *  The hash code.
		 *  Overridden to have only one clock state per update.
		 */
		public int hashCode()
		{
			return 123;
		}
		
		/**
		 *  Test if two objects are equal.
		 *  Overridden to have only one clock state per update.
		 */
		public boolean equals(Object obj)
		{
			return obj instanceof ClockState;
		}
	}

	/**
	 *  Information about the timers to be transferred.
	 */
	@IncludeFields
	public static class TimerEntries
	{
		//-------- attributes --------
		
		/** The times. */
		public long[]	times;
		
		/** The objects. */
		public String[]	objects;
		
		//-------- constructors --------
		
		/**
		 *  Bean constructor.
		 */
		public TimerEntries()
		{
		}
		
		/**
		 *  Create timer entries
		 */
		public TimerEntries(long[] times, String[] objects)
		{
			this.times	= times;
			this.objects	= objects;
		}
		
		//-------- methods --------
		
		/**
		 *  The hash code.
		 *  Overridden to have only one clock state per update.
		 */
		public int hashCode()
		{
			return 123;
		}
		
		/**
		 *  Test if two objects are equal.
		 *  Overridden to have only one clock state per update.
		 */
		public boolean equals(Object obj)
		{
			return obj instanceof TimerEntries;
		}
		
		//-------- helper method --------
		
		/**
		 *  Get the current timer entries.
		 *  Only to be called with local clock service!
		 */
		public static TimerEntries	getTimerEntries(IClockService cs)
		{
			ITimer	next	= cs.getNextTimer();
			ITimer[]	t	= cs.getTimers();
			long[]	times;
			String[]	objects;
			// If next timer is tick timer add to list.
			// Todo: tick timer should be in list?
			if(next!=null && (t==null || t.length==0))
			{
				times	= new long[]{next.getNotificationTime()};
				objects	= new String[]{next.getTimedObject().toString()};
			}
			else if(next!=null && !next.equals(t[0]))
			{
				times	= new long[t.length+1];
				objects	= new String[t.length+1];
				times[0]	= next.getNotificationTime();
				objects[0]	= next.getTimedObject().toString();
				for(int i=0; i<t.length; i++)
				{
					times[i+1]	= t[i].getNotificationTime();
					objects[i+1]	= t[i].getTimedObject().toString();
				}
			}
			else
			{
				times	= new long[t.length];
				objects	= new String[t.length];
				for(int i=0; i<t.length; i++)
				{
					times[i]	= t[i].getNotificationTime();
					objects[i]	= t[i].getTimedObject().toString();
				}
			}
			return new TimerEntries(times, objects);
		}
	}
	
	/**
	 *  The remote clock change listener.
	 */
	public static class RemoteTimerChangeListener	extends RemoteChangeListenerHandler	implements IChangeListener
	{
		//-------- attributes --------
		
		/** The clock service. */
		protected IClockService	cs;
		
		//-------- constructors --------
		
		/**
		 *  Create a BPMN listener.
		 */
		public RemoteTimerChangeListener(String id, IInternalAccess instance, IRemoteChangeListener rcl, IClockService cs)
		{
			super(id, instance, rcl);
			this.cs	= cs;
		}
		
		//-------- IChangeListener interface --------
		
		/**
		 *  Called when the process executes.
		 */
		public void changeOccurred(ChangeEvent event)
		{
			// Use schedule step as clock runs on its own thread. 
			instance.getExternalAccess().scheduleStep(new IComponentStep<Void>()
			{
				public IFuture<Void> execute(IInternalAccess ia)
				{
					elementChanged("timers", TimerEntries.getTimerEntries(cs));
					return IFuture.DONE;
				}
			}).addResultListener(new IResultListener<Void>()
			{
				public void resultAvailable(Void result)
				{
				}
				public void exceptionOccurred(Exception exception)
				{
					dispose();
				}
			});
		}

		/**
		 *  Remove local listeners.
		 */
		protected void dispose()
		{
			super.dispose();
			
			cs.removeChangeListener(this);
//			System.out.println("dispose: "+id);
		}
	}
}
