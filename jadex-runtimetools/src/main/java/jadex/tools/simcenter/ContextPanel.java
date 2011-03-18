package jadex.tools.simcenter;

import jadex.base.service.simulation.ISimulationService;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.SServiceProvider;
import jadex.bridge.service.clock.IClock;
import jadex.bridge.service.clock.IClockService;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.SwingDefaultResultListener;
import jadex.commons.future.SwingDelegationResultListener;
import jadex.commons.gui.SGUI;
import jadex.commons.gui.ToolTipAction;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.JToolBar;
import javax.swing.UIDefaults;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

/**
 *	The context panel shows the settings for an execution context.
 */
public class ContextPanel extends AbstractTimePanel
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
		super(simp.getServiceContainer());
		this.setLayout(new FlowLayout());
		this.simp = simp;
		
		this.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), " Execution Control "));
		
//		this.run = new JButton(START);
//		this.estep = new JButton(STEP_EVENT);
//		this.tstep = new JButton(STEP_TIME);
//		this.pause= new JButton(PAUSE);
//		run.setMargin(new Insets(0,0,0,0));
//		estep.setMargin(new Insets(0,0,0,0));
//		tstep.setMargin(new Insets(0,0,0,0));
//		pause.setMargin(new Insets(0,0,0,0));
		
		JToolBar	toolbar	= new JToolBar("Simulation Control");
		toolbar.add(START);
		toolbar.add(STEP_EVENT);
		toolbar.add(STEP_TIME);
		toolbar.add(PAUSE);
		this.add(toolbar);
		
//		int x=0;
//		int y=0;
//		this.add(run, new GridBagConstraints(x++,y,1,1,1,0,
//			GridBagConstraints.NORTHEAST,GridBagConstraints.NONE,new Insets(4,2,2,4),0,0));
//		this.add(estep, new GridBagConstraints(x++,y,1,1,0,0,
//			GridBagConstraints.NORTH,GridBagConstraints.NONE,new Insets(4,2,2,4),0,0));
//		this.add(tstep, new GridBagConstraints(x++,y,1,1,0,0,
//			GridBagConstraints.NORTH,GridBagConstraints.NONE,new Insets(4,2,2,4),0,0));
//		this.add(pause, new GridBagConstraints(x++,y,1,1,1,0,
//			GridBagConstraints.NORTHWEST,GridBagConstraints.NONE,new Insets(4,2,2,4),0,0));

		setActive(true);
	}
	
	/**
	 *  Update the view.
	 */
	public IFuture	updateView()
	{
		final Future ret = new Future();
		SServiceProvider.getService(getServiceProvider(), IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM)
		.addResultListener(new SwingDelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				final IClockService cs = (IClockService)result;
				SServiceProvider.getService(getServiceProvider(), ISimulationService.class, RequiredServiceInfo.SCOPE_PLATFORM)
					.addResultListener(new SwingDelegationResultListener(ret)
				{
					public void customResultAvailable(Object result)
					{
						ISimulationService sims = (ISimulationService)result;
						sims.isExecuting().addResultListener(new SwingDelegationResultListener(ret)
						{
							public void customResultAvailable(Object result)
							{
								boolean executing = ((Boolean)result).booleanValue();
								boolean clockok = cs.getNextTimer()!=null;
								String	type	= cs.getClockType();
								
								boolean	startenabled	= !executing;
								boolean	pauseenabled	= executing;
								boolean	stepenabled	= !executing && clockok
									&& !IClock.TYPE_CONTINUOUS.equals(type)
									&& !IClock.TYPE_SYSTEM.equals(type);
								
								START.setEnabled(startenabled);
								STEP_EVENT.setEnabled(stepenabled);
								STEP_TIME.setEnabled(stepenabled);
								PAUSE.setEnabled(pauseenabled);
								
								ret.setResult(null);
							}
						});
					}
				});
			}
		});
		return ret;
	}
	
	/**
	 *  Start action.
	 */
	public final Action START = new ToolTipAction(null, icons.getIcon("start"),
		"Start the execution of the application")
	{
		public void actionPerformed(ActionEvent e)
		{
			SServiceProvider.getService(getServiceProvider(),
				ISimulationService.class, RequiredServiceInfo.SCOPE_PLATFORM).addResultListener(new SwingDefaultResultListener(ContextPanel.this)
			{
				public void customResultAvailable(Object result)
				{
					try
					{
						((ISimulationService)result).start();
					}
					catch(Exception ex)
					{
						ex.printStackTrace();
					}
				}
			});
		}

//		public boolean isEnabled()
//		{
//			testEnabled().addResultListener(new SwingDefaultResultListener()
//			{
//				public void customResultAvailable(Object result)
//				{
//					boolean	oldena	= ena;
//					ena = ((Boolean)result).booleanValue();
//					if(oldena!=ena)
//					{
//						updateView();
//					}
//				}
//			});
//			// todo: hack!
//			// problem: service must be fetched fresh!
////			IClockService cs = (IClockService)SServiceProvider.getService(getServiceProvider(), IClockService.class).get(new ThreadSuspendable());
////			ISimulationService sims = (ISimulationService)SServiceProvider.getService(getServiceProvider(), ISimulationService.class, RequiredServiceInfo.SCOPE_PLATFORM).get(new ThreadSuspendable());
////			boolean clockok = cs.getNextTimer()!=null 
////				|| cs.getClockType().equals(IClock.TYPE_CONTINUOUS)
////				|| cs.getClockType().equals(IClock.TYPE_SYSTEM);
////			return !sims.isExecuting();// && clockok;
//		
//			return ena;
//		}
//		
//		/**
//		 *  Test asynchronously if enabled.
//		 */
//		public IFuture testEnabled()
//		{
//			final Future ret = new Future();
//			SServiceProvider.getService(getServiceProvider(), ISimulationService.class, RequiredServiceInfo.SCOPE_PLATFORM)
//				.addResultListener(new SwingDelegationResultListener(ret)
//			{
//				public void customResultAvailable(Object result)
//				{
//					ISimulationService sims = (ISimulationService)result;
//					sims.isExecuting().addResultListener(new SwingDelegationResultListener(ret)
//					{
//						public void customResultAvailable(Object result)
//						{
//							boolean exe = ((Boolean)result).booleanValue();
//							ret.setResult(!exe);
//						}
//					});
//				}
//			});
//			return ret;
//		}
	};
	
	/**
	 *  Step action.
	 */
	public final Action STEP_EVENT = new ToolTipAction(null, icons.getIcon("step_event"),
		"Execute one timer entry.")
	{
//		boolean ena;
		public void actionPerformed(ActionEvent e)
		{
			SServiceProvider.getService(getServiceProvider(),
				ISimulationService.class, RequiredServiceInfo.SCOPE_PLATFORM).addResultListener(new SwingDefaultResultListener(ContextPanel.this)
			{
				public void customResultAvailable(Object result)
				{
					try
					{
						((ISimulationService)result).stepEvent();
					}
					catch(Exception ex)
					{
						ex.printStackTrace();
					}
				}
			});
		}
		
//		public boolean isEnabled()
//		{
//			testEnabled().addResultListener(new SwingDefaultResultListener()
//			{
//				public void customResultAvailable(Object result)
//				{
//					boolean	oldena	= ena;
//					ena = ((Boolean)result).booleanValue();
//					if(oldena!=ena)
//					{
//						updateView();
//					}
//				}
//			});
//			return ena;
//			
////			// todo: hack!
////			// problem: clock service must be fetched fresh!
////			IClockService cs = (IClockService)SServiceProvider.getService(getServiceProvider(), IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM).get(new ThreadSuspendable());
////			ISimulationService sims = (ISimulationService)SServiceProvider.getService(getServiceProvider(), ISimulationService.class, RequiredServiceInfo.SCOPE_PLATFORM).get(new ThreadSuspendable());
////			boolean clockok = cs.getNextTimer()!=null; 
////			return !cs.getClockType().equals(IClock.TYPE_CONTINUOUS) 
////				&& !cs.getClockType().equals(IClock.TYPE_SYSTEM) 
////				&& !sims.isExecuting() && clockok;
//		}
//		
//		/**
//		 *  Test asynchronously if enabled.
//		 */
//		public IFuture testEnabled()
//		{
//			final Future ret = new Future();
//			SServiceProvider.getService(getServiceProvider(), IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM)
//				.addResultListener(new SwingDelegationResultListener(ret)
//			{
//				public void customResultAvailable(Object result)
//				{
//					final IClockService cs = (IClockService)result;
//					SServiceProvider.getService(getServiceProvider(), ISimulationService.class, RequiredServiceInfo.SCOPE_PLATFORM)
//						.addResultListener(new SwingDelegationResultListener(ret)
//					{
//						public void customResultAvailable(Object result)
//						{
//							ISimulationService sims = (ISimulationService)result;
//							sims.isExecuting().addResultListener(new SwingDelegationResultListener(ret)
//							{
//								public void customResultAvailable(Object result)
//								{
//									boolean exe = ((Boolean)result).booleanValue();
//									boolean clockok = cs.getNextTimer()!=null;
//									ret.setResult(new Boolean(!cs.getClockType().equals(IClock.TYPE_CONTINUOUS)
//										&& !cs.getClockType().equals(IClock.TYPE_SYSTEM)
//										&& !exe && clockok));
//								}
//							});
//						}
//					});
//				}
//			});
//			return ret;
//		}
	};
	
	/**
	 *  Time step action.
	 */
	public final Action STEP_TIME = new ToolTipAction(null, icons.getIcon("step_time"),
		"Execute all timer entries belonging to the current time point.")
	{
//		boolean ena;
//		{
//			testEnabled().addResultListener(new SwingDefaultResultListener()
//			{
//				public void customResultAvailable(Object result)
//				{
//					enabled = ((Boolean)result).booleanValue();
//				}
//			});
//		}
		
		public void actionPerformed(ActionEvent e)
		{
			SServiceProvider.getService(getServiceProvider(),
				ISimulationService.class, RequiredServiceInfo.SCOPE_PLATFORM).addResultListener(new SwingDefaultResultListener(ContextPanel.this)
			{
				public void customResultAvailable(Object result)
				{
					try
					{
						((ISimulationService)result).stepTime();
//							.addResultListener(new SwingDefaultResultListener()
//						{
//							public void customResultAvailable(Object result)
//							{
//								ena = true;
//							}
//						});
					}
					catch(Exception ex)
					{
						ex.printStackTrace();
					}
				}
			});
		}
		
//		public boolean isEnabled()
//		{
//			testEnabled().addResultListener(new SwingDefaultResultListener()
//			{
//				public void customResultAvailable(Object result)
//				{
//					boolean	oldena	= ena;
//					ena = ((Boolean)result).booleanValue();
//					if(oldena!=ena)
//					{
//						updateView();
//					}
//				}
//			});
//			return ena;
//		}
//		
//		/**
//		 *  Test asynchronously if enabled.
//		 */
//		public IFuture testEnabled()
//		{
//			final Future ret = new Future();
//			SServiceProvider.getService(getServiceProvider(), IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM)
//				.addResultListener(new SwingDelegationResultListener(ret)
//			{
//				public void customResultAvailable(Object result)
//				{
//					final IClockService cs = (IClockService)result;
//					SServiceProvider.getService(getServiceProvider(), ISimulationService.class, RequiredServiceInfo.SCOPE_PLATFORM)
//						.addResultListener(new SwingDelegationResultListener(ret)
//					{
//						public void customResultAvailable(Object result)
//						{
//							ISimulationService sims = (ISimulationService)result;
//							sims.isExecuting().addResultListener(new SwingDelegationResultListener(ret)
//							{
//								public void customResultAvailable(Object result)
//								{
//									boolean exe = ((Boolean)result).booleanValue();
//									boolean clockok = cs.getNextTimer()!=null;
//									ret.setResult(new Boolean(!cs.getClockType().equals(IClock.TYPE_CONTINUOUS)
//										&& !cs.getClockType().equals(IClock.TYPE_SYSTEM)
//										&& !exe && clockok));
//								}
//							});
//						}
//					});
//				}
//			});
//			return ret;
//		}
	};
	
	/**
	 *  Pause the current execution.
	 */
	public final Action PAUSE = new ToolTipAction(null , icons.getIcon("pause"),
		"Pause the current execution.")
	{
//		boolean ena;
//		{
//			testEnabled().addResultListener(new SwingDefaultResultListener()
//			{
//				public void customResultAvailable(Object result)
//				{
//					enabled = ((Boolean)result).booleanValue();
//				}
//			});
//		}
		
		public void actionPerformed(ActionEvent e)
		{
			SServiceProvider.getService(getServiceProvider(),
				ISimulationService.class, RequiredServiceInfo.SCOPE_PLATFORM).addResultListener(new SwingDefaultResultListener(ContextPanel.this)
			{
				public void customResultAvailable(Object result)
				{
					try
					{
						((ISimulationService)result).pause();
					}
					catch(Exception ex)
					{
						ex.printStackTrace();
					}
				}
			});
		}
		
//		public boolean isEnabled()
//		{
//			testEnabled().addResultListener(new SwingDefaultResultListener()
//			{
//				public void customResultAvailable(Object result)
//				{
//					boolean	oldena	= ena;
//					ena = ((Boolean)result).booleanValue();
//					if(oldena!=ena)
//					{
//						updateView();
//					}
//				}
//			});
//			return ena;
////			// todo: hack!
////			ISimulationService sims = (ISimulationService)SServiceProvider.getService(getServiceProvider(), ISimulationService.class, RequiredServiceInfo.SCOPE_PLATFORM).get(new ThreadSuspendable());
////			return sims.isExecuting() && sims.getMode()
////				.equals(ISimulationService.MODE_NORMAL);
//		}
//		
//		/**
//		 *  Test asynchronously if enabled.
//		 */
//		public IFuture testEnabled()
//		{
//			final Future ret = new Future();
//			SServiceProvider.getService(getServiceProvider(), ISimulationService.class, RequiredServiceInfo.SCOPE_PLATFORM)
//				.addResultListener(new SwingDelegationResultListener(ret)
//			{
//				public void customResultAvailable(Object result)
//				{
//					final ISimulationService sims = (ISimulationService)result;
//					sims.isExecuting().addResultListener(new SwingDelegationResultListener(ret)
//					{
//						public void customResultAvailable(Object result)
//						{
//							final boolean exe = ((Boolean)result).booleanValue();
//							sims.getMode().addResultListener(new SwingDelegationResultListener(ret)
//							{
//								public void customResultAvailable(Object result)
//								{
//									String mode = (String)result;
//									ret.setResult(new Boolean(exe && mode.equals(ISimulationService.MODE_NORMAL)));
//								}
//							});
//						}
//					});
//				}
//			});
//			return ret;
//		}
	};
}