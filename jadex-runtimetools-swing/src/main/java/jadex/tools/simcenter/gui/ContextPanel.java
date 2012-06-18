package jadex.tools.simcenter.gui;

import jadex.base.service.simulation.ClockState;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.RemoteChangeListenerHandler;
import jadex.bridge.service.types.clock.IClock;
import jadex.bridge.service.types.simulation.ISimulationService;
import jadex.commons.ChangeEvent;
import jadex.commons.IChangeListener;
import jadex.commons.IRemoteChangeListener;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.gui.SGUI;
import jadex.commons.gui.ToolTipAction;
import jadex.commons.gui.future.SwingDefaultResultListener;
import jadex.commons.transformation.annotations.Classname;
import jadex.commons.transformation.annotations.IncludeFields;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.UIDefaults;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

/**
 *	The context panel shows the settings for an execution context.
 */
public class ContextPanel extends JPanel
{
	//-------- static part --------

	/** The image icons. */
	protected static UIDefaults	icons	= new UIDefaults(new Object[]
	{
		// todo: rename icon?
		"start",	SGUI.makeIcon(ContextPanel.class, "/jadex/tools/common/images/start.png"),
		"step_event",	SGUI.makeIcon(ContextPanel.class, "/jadex/tools/common/images/single_step_event.png"),
		"step_time",	SGUI.makeIcon(ContextPanel.class, "/jadex/tools/common/images/single_step_time.png"),
		"pause",	SGUI.makeIcon(ContextPanel.class, "/jadex/tools/common/images/pause.png")
	});
	
	//-------- attributes --------

	/** The sim center panel. */
	protected SimCenterPanel simp;
	
	//-------- constructors --------

	/**
	 *  Create a context panel.
	 *  @param context The execution context.
	 */
	public ContextPanel(SimCenterPanel simp)
	{
		this.setLayout(new FlowLayout());
		this.simp = simp;
		
		this.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), " Execution Control "));
		
		JToolBar	toolbar	= new JToolBar("Simulation Control");
		toolbar.add(START);
		toolbar.add(STEP_EVENT);
		toolbar.add(STEP_TIME);
		toolbar.add(PAUSE);
		this.add(toolbar);
		
		setActive(true);
	}
	
	/**
	 *  Update the view.
	 */
	public void	updateView(SimulationState state)
	{
		boolean	startenabled	= !state.executing;
		boolean	pauseenabled	= state.executing;
		boolean	stepenabled	= !state.executing && state.clockok
			&& !IClock.TYPE_CONTINUOUS.equals(state.clocktype)
			&& !IClock.TYPE_SYSTEM.equals(state.clocktype);
		
		START.setEnabled(startenabled);
		STEP_EVENT.setEnabled(stepenabled);
		STEP_TIME.setEnabled(stepenabled);
		PAUSE.setEnabled(pauseenabled);
	}
	
	/**
	 *  Start action.
	 */
	public final Action START = new ToolTipAction(null, icons.getIcon("start"),
		"Start the execution of the application")
	{
		public void actionPerformed(ActionEvent e)
		{
			simp.getSimulationService().start();
		}
	};
	
	/**
	 *  Step action.
	 */
	public final Action STEP_EVENT = new ToolTipAction(null, icons.getIcon("step_event"),
		"Execute one timer entry.")
	{
		public void actionPerformed(ActionEvent e)
		{
			simp.getSimulationService().stepEvent();
		}
	};
	
	/**
	 *  Time step action.
	 */
	public final Action STEP_TIME = new ToolTipAction(null, icons.getIcon("step_time"),
		"Execute all timer entries belonging to the current time point.")
	{
		public void actionPerformed(ActionEvent e)
		{
			simp.getSimulationService().stepTime();
		}
	};
	
	/**
	 *  Pause the current execution.
	 */
	public final Action PAUSE = new ToolTipAction(null , icons.getIcon("pause"),
		"Pause the current execution.")
	{
		public void actionPerformed(ActionEvent e)
		{
			simp.getSimulationService().pause();
		}
	};

	/**
	 *  Activate / deactivate updates.
	 */
	public void	setActive(final boolean active)
	{
		final IRemoteChangeListener	rcl	= new IRemoteChangeListener()
		{
			public IFuture changeOccurred(ChangeEvent event)
			{
				handleEvent(event);
				return IFuture.DONE;
			}
			
			public void	handleEvent(ChangeEvent event)
			{
				if(RemoteChangeListenerHandler.EVENT_BULK.equals(event.getType()))
				{
					Collection	events	= (Collection)event.getValue();
					for(Iterator it=events.iterator(); it.hasNext(); )
					{
						handleEvent((ChangeEvent)it.next());
					}
				}
				else
				{
					updateView((SimulationState)event.getValue());
				}
			}
		};
		
		simp.getComponentForService().addResultListener(new SwingDefaultResultListener(ContextPanel.this)
		{
			public void customResultAvailable(Object result)
			{
				IExternalAccess	access	= (IExternalAccess)result;
				final String	id	= "ContextPanel"+ContextPanel.this.hashCode()+"@"+simp.jcc.getJCCAccess().getComponentIdentifier();
				final ISimulationService	simservice	= simp.getSimulationService();
				access.scheduleStep(new IComponentStep<Void>()
				{
					@Classname("addListener")
					public IFuture<Void> execute(IInternalAccess ia)
					{
						RemoteSimServiceChangeListener	rccl	= new RemoteSimServiceChangeListener(id, ia, rcl, simservice);
						if(active)
						{
//							System.out.println("register listener: "+id);
							simservice.addChangeListener(rccl);
							simservice.getClockService().addChangeListener(rccl);
							
							// Initial event.
							rccl.changeOccurred(null);
						}
						else
						{
							simservice.addChangeListener(rccl);
							simservice.getClockService().addChangeListener(rccl);
//							System.out.println("deregister listener: "+id);
						}
						return IFuture.DONE;
					}
				});
			}
		});
	}
	
	//--------- helper classes --------
	
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
			return obj instanceof ClockState;
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
			// Code in component result listener as clock runs on its own thread. 
			simservice.isExecuting().addResultListener(instance.createResultListener(new IResultListener()
			{
				public void resultAvailable(Object result)
				{
					try
					{
						boolean	executing	= ((Boolean)result).booleanValue();
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
			}));
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
}
