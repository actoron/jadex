package jadex.tools.simcenter;

import jadex.base.service.simulation.ISimulationService;
import jadex.commons.concurrent.SwingDefaultResultListener;
import jadex.commons.service.SServiceProvider;
import jadex.commons.service.clock.IClock;
import jadex.commons.service.clock.IClockService;
import jadex.commons.service.threadpool.IThreadPoolService;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.DecimalFormat;
import java.text.ParseException;

import javax.swing.AbstractSpinnerModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;

/**
 *	The clock panel shows the settings for a clock object.
 */
public class ClockPanel extends AbstractTimePanel
{
	//-------- attributes --------
	
	/** The sim center panel. */
	protected SimCenterPanel simp;
	
	/** The simulation mode. */
	protected JComboBox emode;

	/** The clock name. */
	//protected JLabel name;
	
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
	
	/** The relative time flag. */
	//protected JCheckBox relative;
	
	/** The tick count. */
	protected JLabel tickcount;

	/** The current system time. */
	protected JLabel systemtime;
			
	/** The last clocktype (used to check if update of gui items is necessary). */
	protected String	lastclocktype;
	
	/** Format. */
	protected DecimalFormat numberformat;

	//-------- constructors --------

	/**
	 *  Create a clock panel.
	 *  @param clock The clock.
	 */
	public ClockPanel(final SimCenterPanel simp)
	{
		super(simp.getServiceContainer());
		this.setLayout(new GridBagLayout());
		//this.clock = clock;
		//this.context = context;
		this.simp = simp;
		this.numberformat = new DecimalFormat("#######0.####");
		
		this.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), "Clock Settings "));
		
		int x=0;
		int y=0;
	
		final JCheckBox update = new JCheckBox("Update clock", true);
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

		/*this.name = new JLabel();
		this.add(new JLabel("Clock name"), new GridBagConstraints(x=0,++y,1,1,0,0,
			GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets(4,2,2,4),0,0));
		this.add(name, new GridBagConstraints(++x,y,1,1,1,0,
			GridBagConstraints.EAST,GridBagConstraints.HORIZONTAL,new Insets(4,2,2,4),0,0));*/
		
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

		// Only for realtime
		dilation = new JSpinner(new ExponentialSpinnerModel(1, 2))
		{
			// Override to avoid button writing textfield value into model
		    public void commitEdit() throws ParseException 
		    {
		    }
		};
		JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor)dilation.getEditor();
		JFormattedTextField tf = editor.getTextField();
		//DecimalFormat df = new DecimalFormat("######0.#####");
		tf.setFormatterFactory(new DefaultFormatterFactory(new NumberFormatter(numberformat)));
		tf.setEditable(true);
		
		//if(context.getClock() instanceof IContinuousClock)
		//	dilation.setText(""+((IContinuousClock)context.getClock()).getDilation());
		curdilation = new JTextField(3);
		curdilation.setEditable(false);
		this.add(new JLabel("Dilation"), new GridBagConstraints(x=0,++y,1,1,0,0,
			GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets(4,2,2,4),0,0));
		this.add(curdilation, new GridBagConstraints(++x,y,1,1,0,0,
			GridBagConstraints.EAST,GridBagConstraints.HORIZONTAL,new Insets(4,2,2,4),0,0));
		this.add(dilation, new GridBagConstraints(++x,y,1,1,1,0,
			GridBagConstraints.EAST,GridBagConstraints.HORIZONTAL,new Insets(4,2,2,4),0,0));

		currenttime = new JLabel();
		/*relative = new JCheckBox("Relative", true);
		JPanel tp = new JPanel(new BorderLayout());
		tp.add(currenttime, "Center");
		tp.add(relative, "East");*/
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
		
		JButton refresh = new JButton("Refresh");
		JButton apply = new JButton("Apply");
		
		JPanel buts = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		buts.add(refresh);
		buts.add(apply);
		
		//this.add(buts, new GridBagConstraints(x=0,++y,2,1,1,0,
		//	GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets(4,2,2,4),0,0));
	
//		final IThreadPool tp = (IThreadPool)getPlatform().getService(ThreadPoolService.class);
		emode.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent e)
			{
				if("System".equals(emode.getSelectedItem()))
				{
					SServiceProvider.getService(getServiceProvider(),
						IThreadPoolService.class).addResultListener(new SwingDefaultResultListener(ClockPanel.this)
					{
						public void customResultAvailable(Object source, Object result)
						{
							final IThreadPoolService tp = (IThreadPoolService)result;
							SServiceProvider.getService(getServiceProvider(),
								ISimulationService.class).addResultListener(new SwingDefaultResultListener(ClockPanel.this)
							{
								public void customResultAvailable(Object source, Object result)
								{
									ISimulationService sims = (ISimulationService)result;
									if(!IClock.TYPE_SYSTEM.equals(lastclocktype))
									{
										sims.setClockType(IClock.TYPE_SYSTEM, tp);
		//								updateView();
										simp.updateView();
									}
								}
							});
						}
					});
				}
				else if("Continuous".equals(emode.getSelectedItem()))
				{
					SServiceProvider.getService(getServiceProvider(),
						IThreadPoolService.class).addResultListener(new SwingDefaultResultListener(ClockPanel.this)
					{
						public void customResultAvailable(Object source, Object result)
						{
							final IThreadPoolService tp = (IThreadPoolService)result;
							SServiceProvider.getService(getServiceProvider(),
								ISimulationService.class).addResultListener(new SwingDefaultResultListener(ClockPanel.this)
							{
								public void customResultAvailable(Object source, Object result)
								{
									ISimulationService sims = (ISimulationService)result;
									if(!IClock.TYPE_CONTINUOUS.equals(lastclocktype))
									{
										sims.setClockType(IClock.TYPE_CONTINUOUS, tp);
				//						updateView();
										simp.updateView();
									}
								}
							});
						}
					});
				}
				else if("Time Stepped".equals(emode.getSelectedItem()))
				{
					SServiceProvider.getService(getServiceProvider(),
						IThreadPoolService.class).addResultListener(new SwingDefaultResultListener(ClockPanel.this)
					{
						public void customResultAvailable(Object source, Object result)
						{
							final IThreadPoolService tp = (IThreadPoolService)result;
							SServiceProvider.getService(getServiceProvider(),
								ISimulationService.class).addResultListener(new SwingDefaultResultListener(ClockPanel.this)
							{
								public void customResultAvailable(Object source, Object result)
								{
									ISimulationService sims = (ISimulationService)result;
									if(!IClock.TYPE_TIME_DRIVEN.equals(lastclocktype))
									{
										sims.setClockType(IClock.TYPE_TIME_DRIVEN, tp);
				//						updateView();
										simp.updateView();
									}
								}
							});
						}
					});
				}
				else if("Event Driven".equals(emode.getSelectedItem()))
				{
					SServiceProvider.getService(getServiceProvider(),
						IThreadPoolService.class).addResultListener(new SwingDefaultResultListener(ClockPanel.this)
					{
						public void customResultAvailable(Object source, Object result)
						{
							final IThreadPoolService tp = (IThreadPoolService)result;
							SServiceProvider.getService(getServiceProvider(),
								ISimulationService.class).addResultListener(new SwingDefaultResultListener(ClockPanel.this)
							{
								public void customResultAvailable(Object source, Object result)
								{
									ISimulationService sims = (ISimulationService)result;
									if(!IClock.TYPE_EVENT_DRIVEN.equals(lastclocktype))
									{
										sims.setClockType(IClock.TYPE_EVENT_DRIVEN, tp);
				//						updateView();
										simp.updateView();
									}
								}
							});
						}
					});
				}
				else
				{
					throw new RuntimeException("Unsupported clock type: "+emode.getSelectedItem());
				}
			}
		});
		
		dilation.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
				SServiceProvider.getService(getServiceProvider(),
					IClockService.class).addResultListener(new SwingDefaultResultListener(ClockPanel.this)
				{
					public void customResultAvailable(Object source, Object result)
					{
						IClockService cs = (IClockService)result;
						if(!IClock.TYPE_CONTINUOUS.equals(cs.getClockType()))
							return;
				
						try
						{
							double dil = ((Double)dilation.getValue()).doubleValue();
							cs.setDilation(dil);
						}
						catch(NumberFormatException ex)
						{
						}
					}
				});
			}
		});
		
		ticksize.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				SServiceProvider.getService(getServiceProvider(),
					IClockService.class).addResultListener(new SwingDefaultResultListener(ClockPanel.this)
				{
					public void customResultAvailable(Object source, Object result)
					{
						IClockService cs = (IClockService)result;
						try
						{
							long tick = Long.parseLong(ticksize.getText());
							cs.setDelta(tick);
							//System.out.println("Setting tick size to: "+tick);
						}
						catch(NumberFormatException ex)
						{
							ex.printStackTrace();
						}
					}
				});
			}
		});
		
		refresh.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				updateView();
			}
		});

		/*relative.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if(relative.isSelected())
					currenttime.setText(""+timeformat.format(getContext().getClock().getTime()));
				else
					currenttime.setText(""+dateformat.format(new Date(getContext().getClock().getTime())));
			}
		});*/

		setActive(true);
	}
	
//	long time;
//	int cnt;
	
	/**
	 *  Update the view.
	 */
	public synchronized void updateView()
	{
//		cnt++;
//		if(System.currentTimeMillis()-time>1000)
//		{
//			System.out.println("ClockPanel.updateView called "+cnt+" times.");
//			cnt	= 0;
//			time	= System.currentTimeMillis();
//		}
		
		SServiceProvider.getService(getServiceProvider(),
			IClockService.class).addResultListener(new SwingDefaultResultListener(ClockPanel.this)
		{
			public void customResultAvailable(Object source, Object result)
			{
				final IClockService cs = (IClockService)result;
				SServiceProvider.getService(getServiceProvider(),
					ISimulationService.class).addResultListener(new SwingDefaultResultListener(ClockPanel.this)
				{
					public void customResultAvailable(Object source, Object result)
					{
						ISimulationService sims = (ISimulationService)result;
		
						String	tsstring	= curticksize.getText();
						String	tsstring_new	= ""+numberformat.format(cs.getDelta());
						
						if(!tsstring.equals(tsstring_new))
							curticksize.setText(tsstring_new);
				
						String	ststring	= starttime.getText();
						String	ststring_new	= ""+cs.getStarttime();
						if(!ststring.equals(ststring_new))
							starttime.setText(ststring_new);
						
						//name.setText(""+getContext().getClock());
						/*if(relative.isSelected())
							currenttime.setText(""+timeformat.format(getContext().getClock().getTime()));
						else
							currenttime.setText(""+dateformat.format(new Date(getContext().getClock().getTime())));
						*/
						currenttime.setText(simp.formatTime(cs.getTime()));
						
						tickcount.setText(""+cs.getTick());
						systemtime.setText(simp.formatTime(System.currentTimeMillis()));
							
						if(cs.getClockType().equals(IClock.TYPE_CONTINUOUS))
						{
							String	dstring	= curdilation.getText();
							String	dstring_new	= ""+cs.getDilation();
							if(!dstring.equals(dstring_new))
							{
								curdilation.setText(dstring_new);
							}
						}
						
						// Clock change actions
						if(lastclocktype==null || !lastclocktype.equals(cs.getClockType()))
						{
							lastclocktype	= cs.getClockType();
							curticksize.setText(""+cs.getDelta());
							ticksize.setText(""+cs.getDelta());
							if(lastclocktype.equals(IClock.TYPE_SYSTEM))
							{
								emode.setSelectedItem("System");
								dilation.setValue(new Double(0));
								curdilation.setText("");
							}
							else if(lastclocktype.equals(IClock.TYPE_CONTINUOUS))
							{
								emode.setSelectedItem("Continuous");
								dilation.setValue(new Double(cs.getDilation()));
								curdilation.setText(""+cs.getDilation());
							}
							else if(lastclocktype.equals(IClock.TYPE_TIME_DRIVEN))
							{
								emode.setSelectedItem("Time Stepped");
								dilation.setValue(new Double(0));
								curdilation.setText("");
							}
							else if(lastclocktype.equals(IClock.TYPE_EVENT_DRIVEN))
							{
								emode.setSelectedItem("Event Driven");
								dilation.setValue(new Double(0));
								curdilation.setText("");
							}
						}
						
						if(lastclocktype.equals(IClock.TYPE_CONTINUOUS))// && !getContext().isRunning())
						{
							if(!dilation.isEnabled())
							{
								dilation.setEnabled(true);
								((JSpinner.DefaultEditor)dilation.getEditor()).getTextField().setEditable(true);
							}
						}
						else
						{
							if(dilation.isEnabled())
							{
								dilation.setEnabled(false);
								((JSpinner.DefaultEditor)dilation.getEditor()).getTextField().setEditable(false);
							}
						}
						emode.setEnabled(!sims.isExecuting());
					}
				});
			}
		});
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