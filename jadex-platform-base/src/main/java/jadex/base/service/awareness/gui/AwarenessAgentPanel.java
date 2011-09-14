package jadex.base.service.awareness.gui;

import jadex.base.gui.SwingDefaultResultListener;
import jadex.base.gui.SwingDelegationResultListener;
import jadex.base.gui.componentviewer.IComponentViewerPanel;
import jadex.base.gui.jtable.ComponentIdentifierRenderer;
import jadex.base.gui.plugin.IControlCenter;
import jadex.base.service.awareness.management.AwarenessManagementAgent;
import jadex.base.service.awareness.management.DiscoveryInfo;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.commons.Properties;
import jadex.commons.Property;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.gui.EditableList;
import jadex.commons.gui.jtable.DateTimeRenderer;
import jadex.xml.annotation.XMLClassname;
import jadex.xml.annotation.XMLIncludeFields;

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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.Timer;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;

/**
 *  Panel for the awareness infos.
 */
public class AwarenessAgentPanel implements IComponentViewerPanel
{
	//-------- constants --------
	
	/** The property key for the settings object. */
	public static final String	PROPERTY_GUI_REFRESH	= "gui-refresh";
	
	//-------- attributes --------
	
	/** The jcc. */
	protected IControlCenter jcc;
	
	/** The component. */
	protected IExternalAccess component;
	
	/** The update timer. */
	protected Timer timer;
	
	/** The timer delay. */
	protected int timerdelay;
	
	/** The latest settings of the agent. */
	protected AwarenessSettings	settings;
	
//	/** The IP address text field. */
//	protected JTextField	tfipaddress;
//
//	/** The port text field. */
//	protected JTextField	tfport;

	/** The delay spinner. */
	protected JSpinner	spdelay;

	/** The fast awareness check box. */
	protected JCheckBox	cbfast;

	/** The auto create check box. */
	protected JCheckBox	cbautocreate;

	/** The auto delete check box. */
	protected JCheckBox	cbautodelete;

	/** The refresh delay spinner. */
	protected JSpinner	sprefresh;
	
	/** The includes list. */
	protected EditableList	includes;
	
	/** The excludes list. */
	protected EditableList	excludes;
	
	/** The complete awareness panel. */
	protected JPanel panel;
	
	/** The apply button. */
	protected JButton	buapply;
		
	/** The refresh button. */
	protected JButton	burefresh;
		
	/** The cancel button. */
	protected JButton	bucancel;
		
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
		this.component = component;
		
		this.panel = new JPanel(new GridBagLayout());
		
		this.timerdelay = 5000;
				
//		tfipaddress = new JTextField(9);
//		tfipaddress.setPreferredSize(tfipaddress.getPreferredSize());
//		tfipaddress.setMinimumSize(tfipaddress.getPreferredSize());
//		tfipaddress.setHorizontalAlignment(JTextField.RIGHT);
//		tfport = new JTextField(4);
//		tfport.setPreferredSize(tfport.getPreferredSize());
//		tfport.setMinimumSize(tfport.getPreferredSize());
//		tfport.setHorizontalAlignment(JTextField.RIGHT);
		SpinnerNumberModel spmdelay = new SpinnerNumberModel(0, 0, 100000, 1);
		spdelay = new JSpinner(spmdelay);
		cbfast = new JCheckBox();
		
		cbautocreate = new JCheckBox();
		cbautodelete = new JCheckBox();
		includes	= new EditableList("Includes");
		excludes	= new EditableList("Excludes");
		JScrollPane	pincludes	= new JScrollPane(includes);
		JScrollPane	pexcludes	= new JScrollPane(excludes);
		pincludes.setMinimumSize(new Dimension(0, 0));
		pincludes.setPreferredSize(new Dimension(0, 0));
		pexcludes.setMinimumSize(new Dimension(0, 0));
		pexcludes.setPreferredSize(new Dimension(0, 0));
		
		// Enable apply/cancel buttons on settings changes.
		spdelay.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
				buapply.setEnabled(true);
				bucancel.setEnabled(true);
			}
		});
		ActionListener	al	= new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				buapply.setEnabled(true);
				bucancel.setEnabled(true);
			}
		};
		TableModelListener	tml	= new TableModelListener()
		{
			public void tableChanged(TableModelEvent e)
			{
				buapply.setEnabled(true);
				bucancel.setEnabled(true);
			}
		};
		cbfast.addActionListener(al);
		cbautocreate.addActionListener(al);
		cbautodelete.addActionListener(al);
		includes.getModel().addTableModelListener(tml);
		excludes.getModel().addTableModelListener(tml);
		
		final JPanel pdissettings = new JPanel(new GridBagLayout());
		pdissettings.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), " Discovery Settings "));
		int y=0;
//		pdissettings.add(new JLabel("Multicast address [ip:port]", JLabel.LEFT), 
//			new GridBagConstraints(0, y, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, 
//			GridBagConstraints.NONE, new Insets(1,1,1,1), 0, 0));
//		pdissettings.add(tfipaddress, new GridBagConstraints(1, y, 1, 1, 1, 0, GridBagConstraints.NORTHWEST, 
//			GridBagConstraints.HORIZONTAL, new Insets(1,1,1,1), 0, 0));
//		pdissettings.add(tfport, new GridBagConstraints(2, y, 1, 1, 1, 0, GridBagConstraints.NORTHWEST, 
//			GridBagConstraints.HORIZONTAL, new Insets(1,1,1,1), 0, 0));
		y++;
		pdissettings.add(new JLabel("Info send delay (0=off) [s]", JLabel.LEFT), 
				new GridBagConstraints(0, y, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, 
				GridBagConstraints.NONE, new Insets(1,1,1,1), 0, 1));
		pdissettings.add(spdelay, new GridBagConstraints(1, y, 2, 1, 1, 0, GridBagConstraints.NORTHWEST, 
			GridBagConstraints.HORIZONTAL, new Insets(1,1,1,1), 0, 0));
		y++;
		pdissettings.add(new JLabel("Fast startup awareness", JLabel.LEFT), 
				new GridBagConstraints(0, y, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, 
				GridBagConstraints.NONE, new Insets(1,1,1,1), 0, 1));
		pdissettings.add(cbfast, new GridBagConstraints(1, y, 2, 1, 1, 0, GridBagConstraints.NORTHWEST, 
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
		
		buapply = new JButton("Apply");
		buapply.setToolTipText("Apply setting changes to component.");
		buapply.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ae)
			{
				applySettings();
			}
		});
		bucancel = new JButton("Cancel");
		bucancel.setToolTipText("Cancel changes and reset to previous values.");
		bucancel.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				// Apply previous settings.
				updateSettings(settings);
			}
		});
		burefresh = new JButton("Refresh");
		burefresh.setToolTipText("Refresh settings from underlying component.");
		burefresh.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				refreshSettings();
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
				updateDiscoveryInfos(jtdis);
			}
		});
		
		SpinnerNumberModel spmrefresh = new SpinnerNumberModel(5, 0, 100000, 1);
		sprefresh = new JSpinner(spmrefresh);
		
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
					if(dif.getProxy()!=null)
					{
						jcc.displayError("Creation Error", "Component already has proxy.", null);
					}
					else
					{
						final IComponentIdentifier	cid	= dif.getComponentIdentifier();
						AwarenessAgentPanel.this.component.scheduleStep(new IComponentStep()
						{
							@XMLClassname("createProxy")
							public Object execute(IInternalAccess ia)
							{
								AwarenessManagementAgent agent = (AwarenessManagementAgent)ia;
								return agent.createProxy(cid);
							}
						}).addResultListener(new SwingDefaultResultListener(panel)
						{
							public void customResultAvailable(Object result)
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
					final DiscoveryInfo dif = (DiscoveryInfo)dismodel.getList().get(sel);
					if(dif.getProxy()==null)
					{
						jcc.displayError("Deletion Error", "Component has no proxy.", null);
					}
					else
					{
						AwarenessAgentPanel.this.component.scheduleStep(new IComponentStep()
						{
							@XMLClassname("deleteProxy")
							public Object execute(IInternalAccess ia)
							{
								AwarenessManagementAgent agent = (AwarenessManagementAgent)ia;
								return agent.deleteProxy(dif);
							}
						}).addResultListener(new SwingDefaultResultListener(panel)
						{
							public void customResultAvailable(Object result)
							{
								updateDiscoveryInfos(jtdis);
							}
						});
					}
				}
			}
		});
				
		bucreate.setPreferredSize(burefreshdis.getPreferredSize());
		bucreate.setMinimumSize(burefreshdis.getPreferredSize());
		budelete.setPreferredSize(burefreshdis.getPreferredSize());
		budelete.setMinimumSize(burefreshdis.getMinimumSize());
		
		JPanel pbobuts = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		pbobuts.add(burefreshdis);
		pbobuts.add(bucreate);
		pbobuts.add(budelete);
		
		timer.start();
		
		
		GridBagConstraints	gbc	= new GridBagConstraints();
		gbc.fill	= GridBagConstraints.BOTH;
		
		gbc.gridy	= 0;
		gbc.gridwidth	= 2;
		panel.add(pprosettings, gbc);
		gbc.gridwidth	= 1;

		gbc.weightx	= 1;
		gbc.gridwidth	= 1;
		gbc.gridheight	= 2;
		gbc.insets	= new Insets(2,2,2,2);
		panel.add(pincludes, gbc);
		panel.add(pexcludes, gbc);
		gbc.insets	= new Insets(0,0,0,0);
		gbc.gridheight	= 1;
		
		gbc.gridy++;
		gbc.gridx	= 0;
		gbc.gridwidth	= 2;
		gbc.weightx	= 0;
		panel.add(pdissettings, gbc);
		gbc.gridx	= GridBagConstraints.RELATIVE;
		
		gbc.gridy++;
		gbc.gridwidth	= GridBagConstraints.REMAINDER;
		panel.add(pbuts, gbc);
		
		gbc.gridy++;
		gbc.weighty	= 1;
		panel.add(pdisinfos, gbc);
		
		gbc.gridy++;
		gbc.weightx	= 0;
		gbc.weighty	= 0;
		gbc.gridx	= GridBagConstraints.RELATIVE;
		gbc.gridwidth	= 1;
		gbc.fill	= GridBagConstraints.NONE;
		gbc.insets	= new Insets(0, 6, 0, 0);
		panel.add(new JLabel("Gui refresh delay (0=off) [s]", JLabel.LEFT), gbc);
		gbc.insets	= new Insets(0, 0, 0, 0);
		panel.add(sprefresh, gbc);
		gbc.fill	= GridBagConstraints.BOTH;

		gbc.weightx	= 1;
		gbc.gridwidth	= GridBagConstraints.REMAINDER;
		panel.add(pbobuts, gbc);
		
		return refreshSettings();
	}
	
	/**
	 *  Informs the panel that it should stop all its computation
	 */
	public IFuture shutdown()
	{
		if(timer.isRunning())
			timer.stop();
		return IFuture.DONE;
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
	public IFuture<Void> setProperties(Properties ps)
	{
		Property	settings	= ps.getProperty(PROPERTY_GUI_REFRESH);
		if(settings!=null)
		{
			sprefresh.setValue(new Integer(settings.getValue()));
		}
		
		return IFuture.DONE;
	}

	/**
	 *  Advices the panel provide its setting as properties (if any).
	 *  This is done on project close or save.
	 */
	public IFuture<Properties> getProperties()
	{
		Properties	props	= new Properties();
		props.addProperty(new Property(PROPERTY_GUI_REFRESH, sprefresh.getValue().toString()));
		Future<Properties>	ret	= new Future<Properties>(props);
		return ret;
	}
	
	/**
	 *  Get the current settings from the agent and update the GUI.
	 */
	protected IFuture	refreshSettings()
	{
		final Future	ret	= new Future();
		component.scheduleStep(new IComponentStep()
		{
			@XMLClassname("refreshSettings")
			public Object execute(IInternalAccess ia)
			{
				AwarenessManagementAgent agent = (AwarenessManagementAgent)ia;
				AwarenessSettings	ret	= new AwarenessSettings();
//				Object[]	ai	= agent.getAddressInfo();
//				ret.address	= (InetAddress)ai[0];
//				ret.port	= (Integer)ai[1];
				ret.delay	= agent.getDelay();
				ret.fast	= agent.isFastAwareness();
				ret.autocreate	= agent.isAutoCreateProxy();
				ret.autodelete	= agent.isAutoDeleteProxy();
				ret.includes	= agent.getIncludes();
				ret.excludes	= agent.getExcludes();
				return ret;
			}
		}).addResultListener(new SwingDelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				updateSettings((AwarenessSettings)result);
				ret.setResult(null);
			}
		});
		return ret;
	}
	
	/**
	 *  Update the discovery infos.
	 */
	protected void updateDiscoveryInfos(final JTable jtdis)
	{
		component.scheduleStep(new IComponentStep()
		{
			@XMLClassname("getDiscoveryInfos")
			public Object execute(IInternalAccess ia)
			{
				AwarenessManagementAgent agent = (AwarenessManagementAgent)ia;
				return agent.getDiscoveryInfos();
			}
		}).addResultListener(new SwingDefaultResultListener(jtdis)
		{
			public void customResultAvailable(Object result)
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
			
			public void customExceptionOccurred(Exception exception)
			{
				sprefresh.setValue(new Integer(0));
			}
		});
	}
	
	/**
	 *  Apply current settings to agent
	 */
	protected void applySettings()
	{
		// Send remote settings.
		try
		{
			final AwarenessSettings	settings	= new AwarenessSettings();	// local variable for XML transfer
//			settings.address	= InetAddress.getByName(tfipaddress.getText());
//			settings.port = Integer.parseInt(tfport.getText());
			settings.delay = ((Number)spdelay.getValue()).longValue()*1000;
			settings.fast = cbfast.isSelected();
			settings.autocreate = cbautocreate.isSelected();
			settings.autodelete = cbautodelete.isSelected();
			settings.includes	= includes.getEntries();
			settings.excludes	= excludes.getEntries();
			this.settings	= settings;	// todo: wait for step before setting?
			component.scheduleStep(new IComponentStep()
			{
				@XMLClassname("applySettings")
				public Object execute(IInternalAccess ia)
				{
					AwarenessManagementAgent agent	= (AwarenessManagementAgent)ia;
//					agent.setAddressInfo(settings.address, settings.port);
					agent.setDelay(settings.delay);
					agent.setFastAwareness(settings.fast);
					agent.setAutoCreateProxy(settings.autocreate);
					agent.setAutoDeleteProxy(settings.autodelete);
					agent.setIncludes(settings.includes);
					agent.setExcludes(settings.excludes);
					return null;
				}
			}).addResultListener(new SwingDefaultResultListener(panel)
			{
				public void customResultAvailable(Object result)
				{
					buapply.setEnabled(false);
					bucancel.setEnabled(false);
				}
			});
		}
		catch(Exception e)
		{
			JOptionPane.showMessageDialog(panel, "Could not parse value: "+e, "Settings not applied", JOptionPane.ERROR_MESSAGE);
		}
		
		// Set discovery infos refresh delay (local gui settings).
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
//				System.out.println("timer delay: "+timerdelay);
				timer.setDelay(timerdelay);
				if(!timer.isRunning())
					timer.start();
			}
		}
	}
	
	/**
	 *  Apply settings to GUI.
	 */
	protected void updateSettings(AwarenessSettings settings)
	{
		this.settings	= settings;
//		tfipaddress.setText(settings.address.getHostAddress());
//		tfport.setText(""+settings.port);
		spdelay.setValue(new Long(settings.delay/1000));
		cbfast.setSelected(settings.fast);
		cbautocreate.setSelected(settings.autocreate);
		cbautodelete.setSelected(settings.autodelete);
		includes.setEntries(settings.includes);
		excludes.setEntries(settings.excludes);
		
		buapply.setEnabled(false);
		bucancel.setEnabled(false);
	}

	/**
	 *  The awareness settings transferred between GUI and agent.
	 */
	@XMLIncludeFields
	public static class AwarenessSettings
	{
		/** The inet address. */
		public InetAddress address;
		
		/** The port. */
		public int port;
		
		/** The delay. */
		public long delay;
		
		/** The fast awareness flag. */
		public boolean fast;
		
		/** The autocreate flag. */
		public boolean autocreate;
		
		/** The autocreate flag. */
		public boolean autodelete;
		
		/** The includes list. */
		public String[]	includes;
		
		/** The excludes list. */
		public String[]	excludes;
	}
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
				return "Time of Last Info";
			case 3:
				return "Has a Proxy";
			case 4:
				return "Remote Excluded";
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
			value = dif.getProxy()!=null ? Boolean.TRUE : Boolean.FALSE;
		}
		else if(column == 4)
		{
			value = dif.isRemoteExcluded() ? Boolean.TRUE : Boolean.FALSE;
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

