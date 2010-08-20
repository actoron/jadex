package jadex.tools.serviceviewer.awareness;

import jadex.base.service.awareness.AwarenessAgent;
import jadex.base.service.awareness.DiscoveryInfo;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.commons.ICommand;
import jadex.commons.IResultCommand;
import jadex.commons.Properties;
import jadex.commons.concurrent.SwingDefaultResultListener;
import jadex.micro.IMicroExternalAccess;
import jadex.tools.common.jtable.ComponentIdentifierRenderer;
import jadex.tools.common.jtable.DateTimeRenderer;
import jadex.tools.common.plugin.IControlCenter;
import jadex.tools.serviceviewer.IComponentViewerPanel;

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
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.Timer;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.AbstractTableModel;

/**
 * 
 */
public class AwarenessAgentPanel implements IComponentViewerPanel
{
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
	
	/** The autocreate flag. */
	protected boolean autocreate;
	
	/**
	 *  Called once to initialize the panel.
	 *  Called on the swing thread.
	 *  @param jcc	The jcc.
	 * 	@param component The component.
	 */
	public void init(IControlCenter jcc, IExternalAccess component)
	{
		this.jcc = jcc;
		this.component = (IMicroExternalAccess)component;
	}
	
	/**
	 *  Informs the panel that it should stop all its computation
	 */
	public void shutdown()
	{
		timer.stop();
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
		this.timerdelay = 5000;
		
		final JPanel psettings = new JPanel(new GridBagLayout());
		psettings.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), " Settings "));
		
		final JTextField tfipaddress = new JTextField(8);
		final JTextField tfport = new JTextField(5);
		updateAddress(tfipaddress, tfport);
		
		final JButton buapply = new JButton("Apply");
//		buapply.setMargin(new Insets(0,0,0,0));
		buapply.setToolTipText("Apply setting changes.");
//		busetaddr.setBorder(null);
		
		SpinnerNumberModel spmdelay = new SpinnerNumberModel(0, 0, 100000, 1);
		final JSpinner spdelay = new JSpinner(spmdelay);
		updateDelay(spdelay);
		
		final JCheckBox cbauto = new JCheckBox();
		updateAutocreate(cbauto);
		
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
		
		final Timer timer = new Timer(timerdelay, new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
//				System.out.println("update: "+this+" "+System.currentTimeMillis());
				updateDiscoveryInfos(jtdis);
			}
		});
		
		SpinnerNumberModel spmrefresh = new SpinnerNumberModel(5, 0, 100000, 1);
		final JSpinner sprefresh = new JSpinner(spmrefresh);
		
		buapply.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ae)
			{
				// Set address.
				try
				{
					final InetAddress address = InetAddress.getByName(tfipaddress.getText());
					final int port = Integer.parseInt(tfport.getText());
					
					if(!address.equals(AwarenessAgentPanel.this.address))
					{
						component.scheduleStep(new SetAddressCommand(address, port))
							.addResultListener(new SwingDefaultResultListener(psettings)
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
////			System.out.println("cur val: "+delay);
				if(delay!=AwarenessAgentPanel.this.delay)
				{
					component.scheduleStep(new SetDelayCommand(delay))
						.addResultListener(new SwingDefaultResultListener(psettings)
					{
						public void customResultAvailable(Object source, Object result)
						{
							AwarenessAgentPanel.this.delay = delay;
						}
					});
				}
				
				// Set autocreate.
				final boolean autocreate = cbauto.isSelected();
				if(autocreate!=AwarenessAgentPanel.this.autocreate)
				{
					component.scheduleStep(new SetAutoCreateProxyCommand())
						.addResultListener(new SwingDefaultResultListener(psettings)
					{
						public void customResultAvailable(Object source, Object result)
						{
							AwarenessAgentPanel.this.autocreate = autocreate;
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
						System.out.println("timer delay: "+timerdelay);
						timer.setDelay(timerdelay);
						if(!timer.isRunning())
							timer.start();
					}
				}
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
				spdelay.setValue(delay/1000);
				cbauto.setSelected(autocreate);
			}
		});
		JButton burefresh = new JButton("Refresh");
		burefresh.setToolTipText("Refresh settings from underlying component.");
		burefresh.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				updateAddress(tfipaddress, tfport);
				updateDelay(spdelay);
				updateAutocreate(cbauto);
			}
		});
		buapply.setPreferredSize(burefresh.getPreferredSize());
		buapply.setMinimumSize(burefresh.getMinimumSize());
		bucancel.setPreferredSize(burefresh.getPreferredSize());
		bucancel.setMinimumSize(burefresh.getMinimumSize());
		
		psettings.add(new JLabel("IP-multicast address [ip:port]", JLabel.LEFT), 
			new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, 
			GridBagConstraints.NONE, new Insets(1,1,1,1), 0, 0));
		psettings.add(tfipaddress, new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, 
			GridBagConstraints.HORIZONTAL, new Insets(1,1,1,1), 0, 0));
		psettings.add(tfport, new GridBagConstraints(2, 0, 1, 1, 1, 0, GridBagConstraints.NORTHWEST, 
			GridBagConstraints.HORIZONTAL, new Insets(1,1,1,1), 0, 0));
		
		psettings.add(new JLabel("Delay (0=off) between sending infos [s]", JLabel.LEFT), 
			new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, 
			GridBagConstraints.NONE, new Insets(1,1,1,1), 0, 1));
		psettings.add(spdelay, new GridBagConstraints(1, 1, 2, 1, 1, 0, GridBagConstraints.NORTHWEST, 
			GridBagConstraints.HORIZONTAL, new Insets(1,1,1,1), 0, 0));
		
		psettings.add(new JLabel("Autocreate proxy on discovery", JLabel.LEFT), 
			new GridBagConstraints(0, 2, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, 
			GridBagConstraints.NONE, new Insets(1,1,1,1), 0, 0));
		psettings.add(cbauto, new GridBagConstraints(1, 2, 2, 1, 1, 0, GridBagConstraints.NORTHWEST, 
			GridBagConstraints.NONE, new Insets(1,1,1,1), 0, 0));
		
		psettings.add(new JLabel("Autorefresh delay (0=off) for discovery infos [s]", JLabel.LEFT), 
			new GridBagConstraints(0, 3, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, 
			GridBagConstraints.NONE, new Insets(1,1,1,1), 0, 0));
		psettings.add(sprefresh, new GridBagConstraints(1, 3, 2, 1, 1, 0, GridBagConstraints.NORTHWEST, 
			GridBagConstraints.HORIZONTAL, new Insets(1,1,1,1), 0, 0));
		
		JPanel pbuts = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		pbuts.add(burefresh);
		pbuts.add(buapply);
		pbuts.add(bucancel);
		
		psettings.add(pbuts, new GridBagConstraints(0, 4, 3, 1, 0, 1, GridBagConstraints.NORTHEAST, 
			GridBagConstraints.NONE, new Insets(1,1,1,1), 0, 1));

		
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
					if(dif.hasProxy())
					{
						jcc.displayError("Creation Error", "Component already has proxy.", null);
					}
					else
					{
						component.scheduleStep(new CreateProxyCommand(dif.getComponentIdentifier()))
							.addResultListener(new SwingDefaultResultListener(psettings)
						{
							public void customResultAvailable(Object source, Object result)
							{
								System.out.println("created: "+result);
								updateDiscoveryInfos(jtdis);
							}
							public void customExceptionOccurred(Object source,
									Exception exception)
							{
								System.out.println("not");
								super.customExceptionOccurred(source, exception);
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
					if(!dif.hasProxy())
					{
						jcc.displayError("Deletion Error", "Component has no proxy.", null);
					}
					else
					{
						component.scheduleStep(new DeleteProxyCommand(dif.getComponentIdentifier()))
							.addResultListener(new SwingDefaultResultListener(psettings)
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
		
		JPanel pbobuts = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		pbobuts.add(burefreshdis);
		pbobuts.add(bucreate);
		pbobuts.add(budelete);
		
		pdisinfos.add(pbobuts, BorderLayout.SOUTH);
		
		timer.start();
		
		JPanel pall = new JPanel(new BorderLayout());
		
		JSplitPane pn = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		pn.setOneTouchExpandable(true);
		pn.add(psettings);
		pn.add(pdisinfos);
		
		pall.add(pn, BorderLayout.CENTER);
		
		return pall;
	}

	/**
	 *  Advices the the panel to restore its properties from the argument
	 */
	public void setProperties(Properties ps)
	{
	}

	/**
	 *  Advices the panel provide its setting as properties (if any).
	 *  This is done on project close or save.
	 */
	public Properties	getProperties()
	{
		return null;
	}
	
	/**
	 *  Update address.
	 */
	protected void updateAddress(final JTextField tfipaddress, final JTextField tfport)
	{
		component.scheduleResultStep(new GetAddressCommand())
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
	protected void updateDelay(final JSpinner spdelay)
	{
		component.scheduleResultStep(new GetDelayCommand())
			.addResultListener(new SwingDefaultResultListener(spdelay)
		{
			public void customResultAvailable(Object source, Object result)
			{
				delay = ((Number)result).longValue();
	//			System.out.println("delay is: "+delay);
				spdelay.setValue(delay/1000);
			}
		});
	}
	
	/**
	 *  Update autocreate.
	 */
	protected void updateAutocreate(final JCheckBox cbauto)
	{
		component.scheduleResultStep(new GetAutoCreateProxyCommand())
			.addResultListener(new SwingDefaultResultListener(cbauto)
		{
			public void customResultAvailable(Object source, Object result)
			{
				autocreate = ((Boolean)result).booleanValue();
				cbauto.setSelected(autocreate);
			}
		});
	}
	
	/**
	 *  Update the discovery infos.
	 */
	protected void updateDiscoveryInfos(final JTable jtdis)
	{
		component.scheduleResultStep(new GetDiscoveryInfosCommand())
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
			
//			public void customExceptionOccurred(Object source, Exception exception)
//			{
//				super.customExceptionOccurred(source, exception);
//				timer.stop();
//			}
		});
	}
	
	/**
	 *  Get delay command.
	 */
	public static class GetDelayCommand implements IResultCommand
	{
		public Object execute(Object args)
		{
			AwarenessAgent agent = (AwarenessAgent)args;
			final long delay = agent.getDelay();
			return new Long(delay);
		}
	}
	
	/**
	 *  Set delay command.
	 */
	public static class SetDelayCommand implements ICommand
	{
		public long delay;
		
		public SetDelayCommand()
		{
		}

		public SetDelayCommand(long delay)
		{
			this.delay = delay;
		}
		
		public void execute(Object args)
		{
			AwarenessAgent agent = (AwarenessAgent)args;
			agent.setDelay(delay);
		}

		public long getDelay()
		{
			return delay;
		}

		public void setDelay(long delay)
		{
			this.delay = delay;
		}
	};
	
	/**
	 *  Get address command.
	 */
	public static class GetAddressCommand implements IResultCommand
	{
		public Object execute(Object args)
		{
			AwarenessAgent agent = (AwarenessAgent)args;
			return agent.getAddressInfo();
		}
	};

	/**
	 *  Set address command.
	 */
	public static class SetAddressCommand implements ICommand
	{
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
		
		public void execute(Object args)
		{
			AwarenessAgent agent = (AwarenessAgent)args;
			agent.setAddressInfo(address, port);
		}

		public InetAddress getAddress()
		{
			return address;
		}

		public void setAddress(InetAddress address)
		{
			this.address = address;
		}

		public int getPort()
		{
			return port;
		}

		public void setPort(int port)
		{
			this.port = port;
		}
	};
	
	/**
	 *  Get auto create command.
	 */
	public static class GetAutoCreateProxyCommand implements IResultCommand
	{
		public Object execute(Object args)
		{
			AwarenessAgent agent = (AwarenessAgent)args;
			boolean auto = agent.isAutoCreateProxy();
			return auto? Boolean.TRUE: Boolean.FALSE;
		}
	}
	
	/**
	 *  Set auto create command.
	 */
	public static class SetAutoCreateProxyCommand implements ICommand
	{
		public boolean autocreate;
		
		public SetAutoCreateProxyCommand()
		{
		}

		public SetAutoCreateProxyCommand(boolean autocreate)
		{
			this.autocreate = autocreate;
		}
		
		public void execute(Object args)
		{
			AwarenessAgent agent = (AwarenessAgent)args;
			agent.setAutoCreateProxy(autocreate);
		}

		public boolean isAutocreate()
		{
			return autocreate;
		}

		public void setAutocreate(boolean autocreate)
		{
			this.autocreate = autocreate;
		}
	};
	
	/**
	 *  Get discovery info command.
	 */
	public static class GetDiscoveryInfosCommand implements IResultCommand
	{
		public Object execute(Object args)
		{
			AwarenessAgent agent = (AwarenessAgent)args;
			return agent.getDiscoveryInfos();
		}
	}
	

	/**
	 *  Create proxy command.
	 */
	public static class CreateProxyCommand implements ICommand
	{
		public IComponentIdentifier cid;
		
		public CreateProxyCommand()
		{
		}

		public CreateProxyCommand(IComponentIdentifier cid)
		{
			this.cid = cid;
		}
		
		public void execute(Object args)
		{
			AwarenessAgent agent = (AwarenessAgent)args;
			agent.createProxy(cid);
		}

		public IComponentIdentifier getComponentIdentifier()
		{
			return cid;
		}

		public void setComponentIdentifier(IComponentIdentifier cid)
		{
			this.cid = cid;
		}
	};
	
	/**
	 *  Delete proxy command.
	 */
	public static class DeleteProxyCommand implements ICommand
	{
		public IComponentIdentifier cid;
		
		public DeleteProxyCommand()
		{
		}

		public DeleteProxyCommand(IComponentIdentifier cid)
		{
			this.cid = cid;
		}
		
		public void execute(Object args)
		{
			AwarenessAgent agent = (AwarenessAgent)args;
			agent.deleteProxy(cid);
		}

		public IComponentIdentifier getComponentIdentifier()
		{
			return cid;
		}

		public void setComponentIdentifier(IComponentIdentifier cid)
		{
			this.cid = cid;
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
		return 4;
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
				return "Last Received Info";
			case 3:
				return "Has a Proxy";
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
			value = dif.hasProxy()? Boolean.TRUE: Boolean.FALSE;
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
		return ret;
	}
};

