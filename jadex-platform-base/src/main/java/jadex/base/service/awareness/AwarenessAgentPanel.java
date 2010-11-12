package jadex.base.service.awareness;

import jadex.base.gui.componentviewer.IComponentViewerPanel;
import jadex.base.gui.jtable.ComponentIdentifierRenderer;
import jadex.base.gui.plugin.IControlCenter;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.commons.Future;
import jadex.commons.ICommand;
import jadex.commons.IFuture;
import jadex.commons.IResultCommand;
import jadex.commons.Properties;
import jadex.commons.concurrent.SwingDefaultResultListener;
import jadex.commons.jtable.DateTimeRenderer;
import jadex.micro.IMicroExternalAccess;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.Timer;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.AbstractTableModel;

/**
 *  Panel for the awareness infos.
 */
public class AwarenessAgentPanel implements IComponentViewerPanel
{
	//-------- attributes --------
	
	/** The jcc. */
	protected IControlCenter jcc;
	
	/** The component. */
	protected IMicroExternalAccess component;
	
	/** The update timer. */
	protected Timer timer;
	
	/** The timer delay. */
	protected int timerdelay;
	
	
	/** The inet address. */
	protected InetAddress address;
	
	/** The port. */
	protected int port;
	
	/** The delay. */
	protected long delay;
	
	/** The proxydelay. */
	protected long proxydelay;
	
	/** The autocreate flag. */
	protected boolean autocreate;
	
	/** The autocreate flag. */
	protected boolean autodelete;

	
	protected JTextField	tfipaddress;

	protected JTextField	tfport;

	protected JSpinner	spdelay;

	protected JCheckBox	cbautocreate;

	protected JCheckBox	cbautodelete;

	protected JSpinner	sprefresh;
	
	protected JSpinner	spprorefresh;

	protected JPanel panel;
	
	
	//-------- methods --------
	
	/**
	 *  Called once to initialize the panel.
	 *  Called on the swing thread.
	 *  @param jcc	The jcc.
	 * 	@param component The component.
	 */
	public IFuture init(final IControlCenter jcc, IExternalAccess component)
	{
		this.jcc = jcc;
		this.component = (IMicroExternalAccess)component;
		
		this.panel = new JPanel(new GridBagLayout());
		
		this.timerdelay = 5000;
				
		tfipaddress = new JTextField(0);
		tfport = new JTextField(0);
		updateAddress();
		SpinnerNumberModel spmdelay = new SpinnerNumberModel(0, 0, 100000, 1);
		spdelay = new JSpinner(spmdelay);
		updateDelay();
		SpinnerNumberModel spmrefresh = new SpinnerNumberModel(5, 0, 100000, 1);
		sprefresh = new JSpinner(spmrefresh);
		
		cbautocreate = new JCheckBox();
		updateAutoCreate(cbautocreate);
		cbautodelete = new JCheckBox();
		updateAutoDelete(cbautodelete);
		SpinnerNumberModel spmprorefresh = new SpinnerNumberModel(5, 0, 100000, 1);
		spprorefresh = new JSpinner(spmprorefresh);
		updateProxyDelay();

		
		final JPanel pdissettings = new JPanel(new GridBagLayout());
		pdissettings.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), " Discovery Settings "));
		int y=0;
		pdissettings.add(new JLabel("Multicast address [ip:port]", JLabel.LEFT), 
			new GridBagConstraints(0, y, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, 
			GridBagConstraints.NONE, new Insets(1,1,1,1), 0, 0));
		pdissettings.add(tfipaddress, new GridBagConstraints(1, y, 1, 1, 1, 0, GridBagConstraints.NORTHWEST, 
			GridBagConstraints.HORIZONTAL, new Insets(1,1,1,1), 0, 0));
		pdissettings.add(tfport, new GridBagConstraints(2, y, 1, 1, 1, 0, GridBagConstraints.NORTHWEST, 
			GridBagConstraints.HORIZONTAL, new Insets(1,1,1,1), 0, 0));
		y++;
		pdissettings.add(new JLabel("Info send delay (0=off) [s]", JLabel.LEFT), 
			new GridBagConstraints(0, y, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, 
			GridBagConstraints.NONE, new Insets(1,1,1,1), 0, 1));
		pdissettings.add(spdelay, new GridBagConstraints(1, y, 2, 1, 1, 0, GridBagConstraints.NORTHWEST, 
			GridBagConstraints.HORIZONTAL, new Insets(1,1,1,1), 0, 0));
		y++;
		pdissettings.add(new JLabel("Gui refresh delay (0=off) [s]", JLabel.LEFT), 
			new GridBagConstraints(0, y, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, 
			GridBagConstraints.NONE, new Insets(1,1,1,1), 0, 0));
		pdissettings.add(sprefresh, new GridBagConstraints(1, y, 2, 1, 1, 0, GridBagConstraints.NORTHWEST, 
			GridBagConstraints.HORIZONTAL, new Insets(1,1,1,1), 0, 0));
		y++;
		
		final JPanel pprosettings = new JPanel(new GridBagLayout());
		pprosettings.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), " Proxy Settings "));
		y=0;
		pprosettings.add(new JLabel("Create on discovery", JLabel.LEFT), 
			new GridBagConstraints(0, y, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, 
			GridBagConstraints.NONE, new Insets(1,1,1,1), 0, 0));
		pprosettings.add(cbautocreate, new GridBagConstraints(1, y, 2, 1, 1, 0, GridBagConstraints.NORTHWEST, 
			GridBagConstraints.NONE, new Insets(1,1,1,1), 0, 0));
		y++;
		pprosettings.add(new JLabel("Delete on disappearance", JLabel.LEFT), 
			new GridBagConstraints(0, y, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, 
			GridBagConstraints.NONE, new Insets(1,1,1,1), 0, 0));
		pprosettings.add(cbautodelete, new GridBagConstraints(1, y, 2, 1, 1, 0, GridBagConstraints.NORTHWEST, 
			GridBagConstraints.NONE, new Insets(1,1,1,1), 0, 0));
		y++;
		pprosettings.add(new JLabel("Refresh delay (0=off) [s]", JLabel.LEFT), 
			new GridBagConstraints(0, y, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, 
			GridBagConstraints.NONE, new Insets(1,1,1,1), 0, 0));
		pprosettings.add(spprorefresh, new GridBagConstraints(1, y, 2, 1, 1, 0, GridBagConstraints.NORTHWEST, 
			GridBagConstraints.HORIZONTAL, new Insets(1,1,1,1), 0, 0));
		y++;
		
		JButton buapply = new JButton("Apply");
//		buapply.setMargin(new Insets(0,0,0,0));
		buapply.setToolTipText("Apply setting changes.");
//		busetaddr.setBorder(null);
		buapply.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ae)
			{
				applySettings(panel);
			}
		});
		JButton bucancel = new JButton("Cancel");
		buapply.setToolTipText("Cancel changes and reset original values.");
		bucancel.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				tfipaddress.setText(address.getHostAddress());
				tfport.setText(""+port);
				spdelay.setValue(new Long(delay/1000));
				cbautocreate.setSelected(autocreate);
				cbautodelete.setSelected(autodelete);
				spprorefresh.setValue(new Long(proxydelay/1000));
			}
		});
		JButton burefresh = new JButton("Refresh");
		burefresh.setToolTipText("Refresh settings from underlying component.");
		burefresh.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				updateAddress();
				updateDelay();
				updateAutoCreate(cbautocreate);
				updateAutoDelete(cbautodelete);
				updateProxyDelay();
			}
		});
		buapply.setPreferredSize(burefresh.getPreferredSize());
		buapply.setMinimumSize(burefresh.getMinimumSize());
		bucancel.setPreferredSize(burefresh.getPreferredSize());
		bucancel.setMinimumSize(burefresh.getMinimumSize());
		JPanel pbuts = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		pbuts.add(burefresh);
		pbuts.add(buapply);
		pbuts.add(bucancel);
		
		
		
		JPanel pdisinfos = new JPanel(new BorderLayout());
		pdisinfos.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), " Discovery Infos "));
		final DiscoveryTableModel dismodel = new DiscoveryTableModel();
		final JTable jtdis = new JTable(dismodel);
		jtdis.setPreferredScrollableViewportSize(new Dimension(600, 120));
		jtdis.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		pdisinfos.add(BorderLayout.CENTER, new JScrollPane(jtdis));
		jtdis.setDefaultRenderer(Date.class, new DateTimeRenderer());
		jtdis.setDefaultRenderer(IComponentIdentifier.class, new ComponentIdentifierRenderer());
		updateDiscoveryInfos(jtdis);
		
		timer = new Timer(timerdelay, new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
//				System.out.println("update: "+this+" "+System.currentTimeMillis());
				updateDiscoveryInfos(jtdis);
			}
		});
				
		JButton burefreshdis = new JButton("Refresh");
		burefreshdis.setToolTipText("Refresh discovery infos.");
		burefreshdis.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				updateDiscoveryInfos(jtdis);
			}
		});
		
		JButton bucreate = new JButton("Create");
		bucreate.setToolTipText("Create a proxy for the selected component.");
		bucreate.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				int sel = jtdis.getSelectedRow();
				if(sel==-1)
				{
					jcc.displayError("Creation Error", "No discovered component selected.", null);
				}
				else
				{
					// todo: hack, could be wrong due to sorting (visual!=data order)
					DiscoveryInfo dif = (DiscoveryInfo)dismodel.getList().get(sel);
					if(dif.isProxy())
					{
						jcc.displayError("Creation Error", "Component already has proxy.", null);
					}
					else
					{
						AwarenessAgentPanel.this.component.scheduleStep(new CreateProxyCommand(dif.getComponentIdentifier()))
							.addResultListener(new SwingDefaultResultListener(panel)
						{
							public void customResultAvailable(Object source, Object result)
							{
								updateDiscoveryInfos(jtdis);
							}
						});
					}
				}
			}
		});
		
		JButton budelete = new JButton("Delete");
		budelete.setToolTipText("Delete proxy for the selected component.");
		budelete.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				int sel = jtdis.getSelectedRow();
				if(sel==-1)
				{
					jcc.displayError("Deletion Error", "No discovered component selected.", null);
				}
				else
				{
					// todo: hack, could be wrong due to sorting (visual!=data order)
					DiscoveryInfo dif = (DiscoveryInfo)dismodel.getList().get(sel);
					if(!dif.isProxy())
					{
						jcc.displayError("Deletion Error", "Component has no proxy.", null);
					}
					else
					{
						AwarenessAgentPanel.this.component.scheduleStep(new DeleteProxyCommand(dif.getComponentIdentifier()))
							.addResultListener(new SwingDefaultResultListener(panel)
						{
							public void customResultAvailable(Object source, Object result)
							{
								updateDiscoveryInfos(jtdis);
							}
						});
					}
				}
			}
		});
		
		JButton buexclude = new JButton("Exclude");
		buexclude.setToolTipText("Exclude/include from automatic proxy generation.");
		buexclude.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				int sel = jtdis.getSelectedRow();
				if(sel==-1)
				{
					jcc.displayError("Exclusion Error", "No discovered component selected.", null);
				}
				else
				{
					// todo: hack, could be wrong due to sorting (visual!=data order)
					DiscoveryInfo dif = (DiscoveryInfo)dismodel.getList().get(sel);
					AwarenessAgentPanel.this.component.scheduleStep(new SetExcludedCommand(dif.getComponentIdentifier(), !dif.isExcluded()))
						.addResultListener(new SwingDefaultResultListener(panel)
					{
						public void customResultAvailable(Object source, Object result)
						{
							updateDiscoveryInfos(jtdis);
						}
					});
				}
			}
		});
		
		bucreate.setPreferredSize(buexclude.getPreferredSize());
		budelete.setMinimumSize(buexclude.getMinimumSize());
		burefreshdis.setPreferredSize(buexclude.getPreferredSize());
		
		JPanel pbobuts = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		pbobuts.add(burefreshdis);
		pbobuts.add(bucreate);
		pbobuts.add(budelete);
		pbobuts.add(buexclude);
		
		timer.start();
		
		
		GridBagConstraints	gbc	= new GridBagConstraints();
		gbc.gridy	= 0;
		gbc.weightx	= 1;
		gbc.weighty	= 0;
		gbc.fill	= GridBagConstraints.BOTH;
		panel.add(pdissettings, gbc);
		panel.add(pprosettings, gbc);
		gbc.gridy	= 1;
		gbc.gridwidth	= GridBagConstraints.REMAINDER;
		panel.add(pbuts, gbc);
		gbc.gridy	= 2;
		gbc.weighty	= 1;
		panel.add(pdisinfos, gbc);
		gbc.gridy	= 3;
		gbc.weighty	= 0;
		panel.add(pbobuts, gbc);
		
		return new Future(null);
	}
	
	/**
	 *  Informs the panel that it should stop all its computation
	 */
	public IFuture shutdown()
	{
		if(timer.isRunning())
			timer.stop();
		return new Future(null);
	}

	/**
	 *  The id used for mapping properties.
	 */
	public String getId()
	{
		return "awarenessviewer";
	}

	/**
	 *  The component to be shown in the gui.
	 *  @return	The component to be displayed.
	 */
	public JComponent getComponent()
	{
		return panel;
	}

	/**
	 *  Advices the the panel to restore its properties from the argument
	 */
	public void setProperties(Properties ps)
	{
		// todo: proerties?
	}

	/**
	 *  Advices the panel provide its setting as properties (if any).
	 *  This is done on project close or save.
	 */
	public Properties	getProperties()
	{
		// todo: proerties?
		
		return null;
	}
	
	/**
	 *  Update address.
	 */
	protected void updateAddress()
	{
		component.scheduleStep(new GetAddressCommand())
			.addResultListener(new SwingDefaultResultListener(tfipaddress)
		{
			public void customResultAvailable(Object source, Object result)
			{
				Object[] ai = (Object[])result;
				address = (InetAddress)ai[0];
				port = ((Number)ai[1]).intValue();
				tfipaddress.setText(address.getHostAddress());
				tfport.setText(""+port);
			}
		});
	}
	
	/**
	 *  Update delay.
	 */
	protected void updateDelay()
	{
		component.scheduleStep(new GetDelayCommand())
			.addResultListener(new SwingDefaultResultListener(spdelay)
		{
			public void customResultAvailable(Object source, Object result)
			{
				delay = ((Number)result).longValue();
	//			System.out.println("delay is: "+delay);
				spdelay.setValue(new Long(delay/1000));
			}
		});
	}
	
	/**
	 *  Update proxydelay.
	 */
	protected void updateProxyDelay()
	{
		component.scheduleStep(new GetProxyDelayCommand())
			.addResultListener(new SwingDefaultResultListener(spprorefresh)
		{
			public void customResultAvailable(Object source, Object result)
			{
				proxydelay = ((Number)result).longValue();
	//			System.out.println("delay is: "+delay);
				spprorefresh.setValue(new Long(proxydelay/1000));
			}
		});
	}
	
	/**
	 *  Update autocreate.
	 */
	protected void updateAutoCreate(final JCheckBox cbautocreate)
	{
		component.scheduleStep(new GetAutoCreateProxyCommand())
			.addResultListener(new SwingDefaultResultListener(cbautocreate)
		{
			public void customResultAvailable(Object source, Object result)
			{
				autocreate = ((Boolean)result).booleanValue();
				cbautocreate.setSelected(autocreate);
			}
		});
	}
	
	/**
	 *  Update autodelete.
	 */
	protected void updateAutoDelete(final JCheckBox cbautodelete)
	{
		component.scheduleStep(new GetAutoDeleteProxyCommand())
			.addResultListener(new SwingDefaultResultListener(cbautodelete)
		{
			public void customResultAvailable(Object source, Object result)
			{
				autodelete = ((Boolean)result).booleanValue();
				cbautodelete.setSelected(autodelete);
			}
		});
	}
	
	/**
	 *  Update the discovery infos.
	 */
	protected void updateDiscoveryInfos(final JTable jtdis)
	{
		component.scheduleStep(new GetDiscoveryInfosCommand())
			.addResultListener(new SwingDefaultResultListener(jtdis)
		{
			public void customResultAvailable(Object source, Object result)
			{
				DiscoveryInfo[] ds = (DiscoveryInfo[])result;
				
				int sel = jtdis.getSelectedRow();
				DiscoveryTableModel dtm = (DiscoveryTableModel)jtdis.getModel();
				List disinfos = dtm.getList();
				disinfos.clear();
				for(int i = 0; i < ds.length; i++)
				{
//					if(!disinfos.contains(ds[i]))
//					{
//						System.out.println("added: "+aitems[i]);
						disinfos.add(ds[i]);
//					}
				}
				
				dtm.fireTableDataChanged();
				if(sel!=-1 && sel<ds.length)
					((DefaultListSelectionModel)jtdis.getSelectionModel()).setSelectionInterval(sel, sel);
			}
			
			public void customExceptionOccurred(Object source, Exception exception)
			{
				sprefresh.setValue(new Integer(0));
			}
		});
	}
	
	/**
	 *  Apply current settings.
	 */
	protected void applySettings(final JComponent ui)
	{
		// Set address.
		try
		{
			final InetAddress address = InetAddress.getByName(tfipaddress.getText());
			final int port = Integer.parseInt(tfport.getText());
			
			if(!address.equals(AwarenessAgentPanel.this.address))
			{
				component.scheduleStep(new SetAddressCommand(address, port))
					.addResultListener(new SwingDefaultResultListener(ui)
				{
					public void customResultAvailable(Object source, Object result)
					{
						AwarenessAgentPanel.this.address = address;
						AwarenessAgentPanel.this.port = port;
					}
				});
			}
		}
		catch(Exception e)
		{
			jcc.displayError("Parsing Error", "Could not create address.", e);
		}
		
		// Set delay.
		final long delay = ((Number)spdelay.getValue()).longValue()*1000;
////	System.out.println("cur val: "+delay);
		if(delay!=AwarenessAgentPanel.this.delay)
		{
			component.scheduleStep(new SetDelayCommand(delay))
				.addResultListener(new SwingDefaultResultListener(ui)
			{
				public void customResultAvailable(Object source, Object result)
				{
					AwarenessAgentPanel.this.delay = delay;
				}
			});
		}
		
		// Set autocreate.
		final boolean autocreate = cbautocreate.isSelected();
		if(autocreate!=AwarenessAgentPanel.this.autocreate)
		{
			component.scheduleStep(new SetAutoCreateProxyCommand())
				.addResultListener(new SwingDefaultResultListener(ui)
			{
				public void customResultAvailable(Object source, Object result)
				{
					AwarenessAgentPanel.this.autocreate = autocreate;
				}
			});
		}
		
		// Set autodelete.
		final boolean autodelete = cbautodelete.isSelected();
		if(autodelete!=AwarenessAgentPanel.this.autodelete)
		{
			component.scheduleStep(new SetAutoDeleteProxyCommand())
				.addResultListener(new SwingDefaultResultListener(ui)
			{
				public void customResultAvailable(Object source, Object result)
				{
					AwarenessAgentPanel.this.autodelete = autodelete;
				}
			});
		}
		
		// Set proxy delay.
		final long proxydelay = ((Number)spprorefresh.getValue()).longValue()*1000;
////	System.out.println("cur val: "+delay);
		if(proxydelay!=AwarenessAgentPanel.this.proxydelay)
		{
			component.scheduleStep(new SetProxyDelayCommand(proxydelay))
				.addResultListener(new SwingDefaultResultListener(ui)
			{
				public void customResultAvailable(Object source, Object result)
				{
					AwarenessAgentPanel.this.proxydelay = proxydelay;
				}
			});
		}
		
		// Set discovery infos refresh delay.
		final int timerdelay = ((Number)sprefresh.getValue()).intValue()*1000;
		if(timerdelay!=AwarenessAgentPanel.this.timerdelay)
		{
			AwarenessAgentPanel.this.timerdelay = timerdelay;
			if(timerdelay==0)
			{
				timer.stop();
			}
			else
			{
//						System.out.println("timer delay: "+timerdelay);
				timer.setDelay(timerdelay);
				if(!timer.isRunning())
					timer.start();
			}
		}
	}

	/**
	 *  Get delay command.
	 */
	public static class GetDelayCommand implements IComponentStep
	{
		public Object execute(IInternalAccess ia)
		{
			AwarenessAgent agent = (AwarenessAgent)ia;
			final long delay = agent.getDelay();
			return new Long(delay);
		}
	}
	
	/**
	 *  Get proxy delay command.
	 */
	public static class GetProxyDelayCommand implements IComponentStep
	{
		public Object execute(IInternalAccess ia)
		{
			AwarenessAgent agent = (AwarenessAgent)ia;
			final long delay = agent.getProxyDelay();
			return new Long(delay);
		}
	}
	
	/**
	 *  Set delay command.
	 */
	public static class SetDelayCommand implements IComponentStep
	{
		public static boolean XML_INCLUDE_FIELDS = true;
		public long delay;
		
		public SetDelayCommand()
		{
		}

		public SetDelayCommand(long delay)
		{
			this.delay = delay;
		}
		
		public Object execute(IInternalAccess ia)
		{
			AwarenessAgent agent = (AwarenessAgent)ia;
			agent.setDelay(delay);
			return null;
		}
	};
	
	/**
	 *  Set proxy delay command.
	 */
	public static class SetProxyDelayCommand implements IComponentStep
	{
		public static boolean XML_INCLUDE_FIELDS = true;
		public long delay;
		
		public SetProxyDelayCommand()
		{
		}

		public SetProxyDelayCommand(long delay)
		{
			this.delay = delay;
		}
		
		public Object execute(IInternalAccess ia)
		{
			AwarenessAgent agent = (AwarenessAgent)ia;
			agent.setProxyDelay(delay);
			return null;
		}
	};
	
	/**
	 *  Get address command.
	 */
	public static class GetAddressCommand implements IComponentStep
	{
		public Object execute(IInternalAccess ia)
		{
			AwarenessAgent agent = (AwarenessAgent)ia;
			return agent.getAddressInfo();
		}
	};

	/**
	 *  Set address command.
	 */
	public static class SetAddressCommand implements IComponentStep
	{
		public static boolean XML_INCLUDE_FIELDS = true;
		public InetAddress address;
		public int port;
		
		public SetAddressCommand()
		{
		}

		public SetAddressCommand(InetAddress address, int port)
		{
			this.address = address;
			this.port = port;
		}
		
		public Object execute(IInternalAccess ia)
		{
			AwarenessAgent agent = (AwarenessAgent)ia;
			agent.setAddressInfo(address, port);
			return null;
		}
	};
	
	/**
	 *  Get auto create command.
	 */
	public static class GetAutoCreateProxyCommand implements IComponentStep
	{
		public Object execute(IInternalAccess ia)
		{
			AwarenessAgent agent = (AwarenessAgent)ia;
			boolean auto = agent.isAutoCreateProxy();
			return auto? Boolean.TRUE: Boolean.FALSE;
		}
	}
	
	/**
	 *  Set auto create command.
	 */
	public static class SetAutoCreateProxyCommand implements IComponentStep
	{
		public static boolean XML_INCLUDE_FIELDS = true;
		public boolean autocreate;
		
		public SetAutoCreateProxyCommand()
		{
		}

		public SetAutoCreateProxyCommand(boolean autocreate)
		{
			this.autocreate = autocreate;
		}
		
		public Object execute(IInternalAccess ia)
		{
			AwarenessAgent agent = (AwarenessAgent)ia;
			agent.setAutoCreateProxy(autocreate);
			return null;
		}
	};
	
	/**
	 *  Get auto delete command.
	 */
	public static class GetAutoDeleteProxyCommand implements IComponentStep
	{
		public Object execute(IInternalAccess ia)
		{
			AwarenessAgent agent = (AwarenessAgent)ia;
			boolean auto = agent.isAutoDeleteProxy();
			return auto? Boolean.TRUE: Boolean.FALSE;
		}
	}
	
	/**
	 *  Set auto delete command.
	 */
	public static class SetAutoDeleteProxyCommand implements IComponentStep
	{
		public static boolean XML_INCLUDE_FIELDS = true;
		public boolean autodelete;
		
		public SetAutoDeleteProxyCommand()
		{
		}

		public SetAutoDeleteProxyCommand(boolean autodelete)
		{
			this.autodelete = autodelete;
		}
		
		public Object execute(IInternalAccess ia)
		{
			AwarenessAgent agent = (AwarenessAgent)ia;
			agent.setAutoDeleteProxy(autodelete);
			return null;
		}
	};
	
	/**
	 *  Get discovery info command.
	 */
	public static class GetDiscoveryInfosCommand implements IComponentStep
	{
		public Object execute(IInternalAccess ia)
		{
			AwarenessAgent agent = (AwarenessAgent)ia;
			return agent.getDiscoveryInfos();
		}
	}

	/**
	 *  Create proxy command.
	 */
	public static class CreateProxyCommand implements IComponentStep
	{
		public static boolean XML_INCLUDE_FIELDS = true;
		public IComponentIdentifier cid;
		
		public CreateProxyCommand()
		{
		}

		public CreateProxyCommand(IComponentIdentifier cid)
		{
			this.cid = cid;
		}
		
		public Object execute(IInternalAccess ia)
		{
			AwarenessAgent agent = (AwarenessAgent)ia;
			return agent.createProxy(cid);
		}
	};
	
	/**
	 *  Delete proxy command.
	 */
	public static class DeleteProxyCommand implements IComponentStep
	{
		public static boolean XML_INCLUDE_FIELDS = true;
		public IComponentIdentifier cid;
		
		public DeleteProxyCommand()
		{
		}

		public DeleteProxyCommand(IComponentIdentifier cid)
		{
			this.cid = cid;
		}
		
		public Object execute(IInternalAccess ia)
		{
			AwarenessAgent agent = (AwarenessAgent)ia;
			return agent.deleteProxy(cid);
		}
	};
	
	/**
	 *  Set exclude command.
	 */
	public static class SetExcludedCommand implements IComponentStep
	{
		public static boolean XML_INCLUDE_FIELDS = true;
		public IComponentIdentifier cid;
		public boolean excluded;
		
		public SetExcludedCommand()
		{
		}

		public SetExcludedCommand(IComponentIdentifier cid, boolean excluded)
		{
			this.cid = cid;
			this.excluded = excluded;
		}
		
		public Object execute(IInternalAccess ia)
		{
			AwarenessAgent agent = (AwarenessAgent)ia;
			agent.setExcluded(cid, excluded);
			return null;
		}
	};
}

class DiscoveryTableModel extends AbstractTableModel
{
	protected List list;
	
	public DiscoveryTableModel()
	{
		this(new ArrayList());
	}
	
	public DiscoveryTableModel(List list)
	{
		this.list = list;
	}
	
	public List getList()
	{
		return list;
	}

	public int getRowCount()
	{
		return list.size();
	}

	public int getColumnCount()
	{
		return 5;
	}

	public String getColumnName(int column)
	{
		switch(column)
		{
			case 0:
				return "Component Identifier";
			case 1:
				return "Delay";
			case 2:
				return "Time of Last Received Info";
			case 3:
				return "Has a Proxy";
			case 4:
				return "Excluded from Proxy Creation";
			default:
				return "";
		}
	}

	public boolean isCellEditable(int row, int column)
	{
		return false;
	}

	public Object getValueAt(int row, int column)
	{
		Object value = null;
		DiscoveryInfo dif = (DiscoveryInfo)list.get(row);
		if(column == 0)
		{
			value = dif.getComponentIdentifier();
		}
		else if(column == 1)
		{
			value = new Long(dif.getDelay());
		}
		else if(column == 2)
		{
			value = new Date(dif.getTime());
		}
		else if(column == 3)
		{
			value = dif.isProxy()? Boolean.TRUE: Boolean.FALSE;
		}
		else if(column == 4)
		{
			value = dif.isExcluded()? Boolean.TRUE: Boolean.FALSE;
		}
		return value;
	}
	
	public Class getColumnClass(int column)
	{
		Class ret = Object.class;
		if(column == 0)
		{
			ret = IComponentIdentifier.class;
		}
		else if(column == 1)
		{
			ret = Long.class;
		}
		else if(column == 2)
		{
			ret = Date.class;
		}
		else if(column == 3)
		{
			ret = Boolean.class;
		}
		else if(column == 4)
		{
			ret = Boolean.class;
		}
		return ret;
	}
};

