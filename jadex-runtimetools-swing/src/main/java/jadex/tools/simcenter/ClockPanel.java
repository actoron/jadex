package jadex.tools.simcenter;

import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.RemoteChangeListenerHandler;
import jadex.bridge.service.types.clock.IClock;
import jadex.bridge.service.types.clock.IClockService;
import jadex.bridge.service.types.simulation.ISimulationService;
import jadex.commons.ChangeEvent;
import jadex.commons.IChangeListener;
import jadex.commons.IRemoteChangeListener;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.gui.future.SwingDefaultResultListener;
import jadex.commons.transformation.annotations.Classname;
import jadex.platform.service.simulation.ClockState;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.AbstractSpinnerModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeListener;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;

/**
 *	The clock panel shows the settings for a clock object.
 */
public class ClockPanel	extends JPanel
{
	//-------- attributes --------
	
	/** The sim center panel. */
	protected SimCenterPanel simp;
	
	/** The update flag. */
	protected JCheckBox update;
	
	/** The simulation mode. */
	protected JComboBox emode;

	/** The start time. */
	protected JTextField starttime;
	
	/** The tick size. */
	protected JTextField ticksize;
	protected JTextField curticksize;
	
	/** The dilation. */
	protected JSpinner dilation;
	protected JTextField curdilation;
	
	/** The current time. */
	protected JLabel currenttime;
	
	/** The tick count. */
	protected JLabel tickcount;

	/** The current system time. */
	protected JLabel systemtime;
			
	/** The last clocktype (used to check if update of gui items is necessary). */
	protected String	lastclocktype;
	
	/** Format. */
	protected DecimalFormat numberformat;

	/** The last known clock state. */
	protected ClockState	laststate;

	//-------- constructors --------

	/**
	 *  Create a clock panel.
	 *  @param clock The clock.
	 */
	public ClockPanel(final SimCenterPanel simp)
	{
		this.setLayout(new GridBagLayout());
		this.simp = simp;
		this.numberformat = new DecimalFormat("#######0.####");
		
		this.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), "Clock Settings "));
		
		int x=0;
		int y=0;
	
		update	= new JCheckBox("Update clock", true);
		update.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				setActive(update.isSelected());
			}
		});

		this.add(update, new GridBagConstraints(x,y,2,1,0,0,
			GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(4,2,2,4),0,0));

		emode = new JComboBox(new String[]{"System", "Continuous", "Time Stepped", "Event Driven"});
		emode.setEditable(false);
		this.add(new JLabel("Execution mode"), new GridBagConstraints(x,++y,1,1,0,0,
			GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets(4,2,2,4),0,0));
		this.add(emode, new GridBagConstraints(++x,y,2,1,1,0,
			GridBagConstraints.EAST,GridBagConstraints.HORIZONTAL,new Insets(4,2,2,4),0,0));

		starttime = new JTextField(8);
		starttime.setEditable(false);
		this.add(new JLabel("Start time"), new GridBagConstraints(x=0,++y,1,1,0,0,
			GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets(4,2,2,4),0,0));
		this.add(starttime, new GridBagConstraints(++x,y,2,1,1,0,
			GridBagConstraints.EAST,GridBagConstraints.HORIZONTAL,new Insets(4,2,2,4),0,0));

		ticksize = new JTextField(8);
		curticksize = new JTextField(4);
		curticksize.setEditable(false);
		this.add(new JLabel("Tick size"), new GridBagConstraints(x=0,++y,1,1,0,0,
			GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets(4,2,2,4),0,0));
		this.add(curticksize, new GridBagConstraints(++x,y,1,1,1,0,
				GridBagConstraints.EAST,GridBagConstraints.HORIZONTAL,new Insets(4,2,2,4),0,0));
		this.add(ticksize, new GridBagConstraints(++x,y,1,1,1,0,
			GridBagConstraints.EAST,GridBagConstraints.HORIZONTAL,new Insets(4,2,2,4),0,0));

		// Only for continuous time.
		dilation = new JSpinner(new ExponentialSpinnerModel(1, 2))
		{
			// Override to avoid button writing textfield value into model
		    public void commitEdit() throws ParseException 
		    {
		    }
		};
		JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor)dilation.getEditor();
		JFormattedTextField tf = editor.getTextField();
		tf.setFormatterFactory(new DefaultFormatterFactory(new NumberFormatter(numberformat)));
		tf.setEditable(true);
		
		curdilation = new JTextField(3);
		curdilation.setEditable(false);
		this.add(new JLabel("Dilation"), new GridBagConstraints(x=0,++y,1,1,0,0,
			GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets(4,2,2,4),0,0));
		this.add(curdilation, new GridBagConstraints(++x,y,1,1,0,0,
			GridBagConstraints.EAST,GridBagConstraints.HORIZONTAL,new Insets(4,2,2,4),0,0));
		this.add(dilation, new GridBagConstraints(++x,y,1,1,1,0,
			GridBagConstraints.EAST,GridBagConstraints.HORIZONTAL,new Insets(4,2,2,4),0,0));

		currenttime = new JLabel();
		this.add(new JLabel("Model time"), new GridBagConstraints(x=0,++y,1,1,0,0,
			GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets(4,2,2,4),0,0));
		this.add(currenttime, new GridBagConstraints(++x,y,2,1,1,0,
			GridBagConstraints.EAST,GridBagConstraints.HORIZONTAL,new Insets(4,2,2,4),0,0));
		
		tickcount = new JLabel();
		this.add(new JLabel("Tick count"), new GridBagConstraints(x=0,++y,1,1,0,0,
			GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets(4,2,2,4),0,0));
		this.add(tickcount, new GridBagConstraints(++x,y,2,1,1,0,
			GridBagConstraints.EAST,GridBagConstraints.HORIZONTAL,new Insets(4,2,2,4),0,0));
		
		systemtime = new JLabel();
		this.add(new JLabel("System time"), new GridBagConstraints(x=0,++y,1,1,0,0,
			GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets(4,2,2,4),0,0));
		this.add(systemtime, new GridBagConstraints(++x,y,2,1,1,0,
			GridBagConstraints.EAST,GridBagConstraints.HORIZONTAL,new Insets(4,2,2,4),0,0));
		
		emode.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent e)
			{
				if("System".equals(emode.getSelectedItem()))
				{
					if(!IClock.TYPE_SYSTEM.equals(lastclocktype))
					{
						simp.getSimulationService().setClockType(IClock.TYPE_SYSTEM);
						simp.updateView();
					}
				}
				else if("Continuous".equals(emode.getSelectedItem()))
				{
					if(!IClock.TYPE_CONTINUOUS.equals(lastclocktype))
					{
						simp.getSimulationService().setClockType(IClock.TYPE_CONTINUOUS);
						simp.updateView();
					}
				}
				else if("Time Stepped".equals(emode.getSelectedItem()))
				{
					if(!IClock.TYPE_TIME_DRIVEN.equals(lastclocktype))
					{
						simp.getSimulationService().setClockType(IClock.TYPE_TIME_DRIVEN);
						simp.updateView();
					}
				}
				else if("Event Driven".equals(emode.getSelectedItem()))
				{
					if(!IClock.TYPE_EVENT_DRIVEN.equals(lastclocktype))
					{
						simp.getSimulationService().setClockType(IClock.TYPE_EVENT_DRIVEN);
						simp.updateView();
					}
				}
			}
		});
		
		dilation.addChangeListener(new ChangeListener()
		{
			public void stateChanged(javax.swing.event.ChangeEvent e)
			{
				if(!IClock.TYPE_CONTINUOUS.equals(lastclocktype))
					return;
		
				try
				{
					final double dil = ((Double)dilation.getValue()).doubleValue();
					final IClockService	cs	= simp.getSimulationService().getClockService();
					simp.getComponentForService().addResultListener(new SwingDefaultResultListener(ClockPanel.this)
					{
						public void customResultAvailable(Object result)
						{
							IExternalAccess	access	= (IExternalAccess)result;
							access.scheduleStep(new IComponentStep<Void>()
							{
								@Classname("setDilation")
								public IFuture<Void> execute(IInternalAccess ia)
								{
									cs.setDilation(dil);
									return IFuture.DONE;
								}
							});
						}
					});
				}
				catch(NumberFormatException ex)
				{
				}
			}
		});
		
		ticksize.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					final long tick = Long.parseLong(ticksize.getText());
					final IClockService	cs	= simp.getSimulationService().getClockService();
					simp.getComponentForService().addResultListener(new SwingDefaultResultListener(ClockPanel.this)
					{
						public void customResultAvailable(Object result)
						{
							IExternalAccess	access	= (IExternalAccess)result;
							access.scheduleStep(new IComponentStep<Void>()
							{
								@Classname("setDilation")
								public IFuture<Void> execute(IInternalAccess ia)
								{
									cs.setDelta(tick);
									return IFuture.DONE;
								}
							});
						}
					});
				}
				catch(NumberFormatException ex)
				{
				}
			}
		});
		
		setActive(true);
	}
	
	/**
	 *  Update the view.
	 */
	public void	updateView()
	{
		if(laststate!=null)
		{
			updateView(laststate);
		}
	}
	
	/**
	 *  Update the view.
	 */
	public void	updateView(ClockState state)
	{
		this.laststate	= state;

		emode.setEnabled(state.changeallowed);
		
		String	tsstring	= curticksize.getText();
		String	tsstring_new	= ""+numberformat.format(state.delta);
		
		if(!tsstring.equals(tsstring_new))
			curticksize.setText(tsstring_new);
	
		String	ststring	= starttime.getText();
		String	ststring_new	= simp.formatTime(state.starttime);
		if(!ststring.equals(ststring_new))
			starttime.setText(ststring_new);
		
		currenttime.setText(simp.formatTime(state.time));
		
		tickcount.setText(""+state.tick);
		systemtime.setText(simp.formatTime(System.currentTimeMillis()));
			
		if(state.type.equals(IClock.TYPE_CONTINUOUS))
		{
			String	dstring	= curdilation.getText();
			String	dstring_new	= ""+state.dilation;
			if(!dstring.equals(dstring_new))
			{
				curdilation.setText(dstring_new);
			}
		}
		
		// Clock change actions
		if(lastclocktype==null || !lastclocktype.equals(state.type))
		{
			lastclocktype	= state.type;
			if(lastclocktype.equals(IClock.TYPE_SYSTEM))
			{
				emode.setSelectedItem("System");
			}
			else if(lastclocktype.equals(IClock.TYPE_CONTINUOUS))
			{
				emode.setSelectedItem("Continuous");
			}
			else if(lastclocktype.equals(IClock.TYPE_TIME_DRIVEN))
			{
				emode.setSelectedItem("Time Stepped");
			}
			else if(lastclocktype.equals(IClock.TYPE_EVENT_DRIVEN))
			{
				emode.setSelectedItem("Event Driven");
			}

			if(lastclocktype.equals(IClock.TYPE_CONTINUOUS))
			{
				dilation.setEnabled(true);
				((JSpinner.DefaultEditor)dilation.getEditor()).getTextField().setEditable(true);
				dilation.setValue(new Double(state.dilation));
			}
			else
			{
				dilation.setEnabled(false);
				((JSpinner.DefaultEditor)dilation.getEditor()).getTextField().setEditable(false);
				dilation.setValue(new Double(0));
				curdilation.setText("");
			}
		}
	}
	
	/**
	 *  Activate / deactivate updates.
	 */
	public void	setActive(final boolean active)
	{
		// Called from external -> update check box
		if(update.isSelected()!=active)
		{
			update.setSelected(active);
		}
		
		// Called from check box -> change state.
		else
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
						updateView((ClockState)event.getValue());
					}
				}
			};
			
			simp.getComponentForService().addResultListener(new SwingDefaultResultListener(ClockPanel.this)
			{
				public void customResultAvailable(Object result)
				{
					IExternalAccess	access	= (IExternalAccess)result;
					final String	id	= "ClockPanel"+ClockPanel.this.hashCode()+"@"+simp.jcc.getJCCAccess().getComponentIdentifier();
					final ISimulationService	simservice	= simp.getSimulationService();
					access.scheduleStep(new IComponentStep<Void>()
					{
						@Classname("addListener")
						public IFuture<Void> execute(IInternalAccess ia)
						{
							RemoteClockChangeListener	rccl	= new RemoteClockChangeListener(id, ia, rcl, simservice);
							if(active)
							{
//								System.out.println("register listener: "+id);
								simservice.addChangeListener(rccl);
								simservice.getClockService().addChangeListener(rccl);
								
								// Initial event.
								rccl.changeOccurred(null);
							}
							else
							{
								simservice.removeChangeListener(rccl);
								simservice.getClockService().removeChangeListener(rccl);
//								System.out.println("deregister listener: "+id);
							}
							return IFuture.DONE;
						}
					});
				}
			});
		}
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
		public RemoteClockChangeListener(String id, IInternalAccess instance, IRemoteChangeListener rcl, ISimulationService simservice)
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
//						System.out.println("elementChanged");
						boolean	executing	= ((Boolean)result).booleanValue();
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

/**
 *  The exponential spinner model. 
 */
class ExponentialSpinnerModel extends AbstractSpinnerModel
{
	//-------- attributes --------
	
	/** The current value. */
	protected double value;
	
	/** The growth rate. */
	protected double rate;
	
	//-------- constructors --------
	
	/**
	 *  Create a new model.
	 */
	public ExponentialSpinnerModel(double value, double rate)
	{
		this.value = value;
		this.rate = rate;
	}
	
	//-------- methods --------

	/**
	 *  Get the next value.
	 *  @return The next value.
	 */
	public Object getNextValue()
	{
		//System.out.println("nv: "+value*rate);
		return new Double(value*rate);
	}

	/**
	 *  Get the previous value.
	 *  @return The previous value.
	 */
	public Object getPreviousValue()
	{
		//System.out.println("pv: "+value/rate);
		return new Double(value/rate);
	}

	/**
	 *  Get the current value.
	 *  @return The current value.
	 */
	public Object getValue()
	{
		//System.out.println("gV: "+value);
		return new Double(value);
	}

	/**
	 *  Set the current value.
	 *  @param value The current value.
	 */
	public void setValue(Object value)
	{
		//System.out.println("YYY: "+value);
		//Thread.dumpStack();
		
		if((value == null) || !(value instanceof Number)) 
		    throw new IllegalArgumentException("Illegal value: "+value);
		
		if(!value.equals(new Double(this.value))) 
		{
			this.value = ((Number)value).doubleValue();
		    fireStateChanged();
		}
	}
}