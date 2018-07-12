package jadex.tools.awareness;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
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

import jadex.base.gui.componentviewer.IComponentViewerPanel;
import jadex.base.gui.componentviewer.IServiceViewerPanel;
import jadex.base.gui.jtable.ComponentIdentifierRenderer;
import jadex.base.gui.plugin.IControlCenter;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.modelinfo.SubcomponentTypeInfo;
import jadex.bridge.service.IService;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.awareness.DiscoveryInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.Properties;
import jadex.commons.Property;
import jadex.commons.SUtil;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.gui.EditableList;
import jadex.commons.gui.future.SwingDefaultResultListener;
import jadex.commons.gui.future.SwingExceptionDelegationResultListener;
import jadex.commons.gui.jtable.DateTimeRenderer;
import jadex.platform.service.awareness.management.AwarenessManagementAgent;
import jadex.platform.service.awareness.management.AwarenessManagementAgentHelper;
import jadex.platform.service.awareness.management.AwarenessSettingsData;

/**
 *  Panel for the awareness infos.
 */
public class AwarenessAgentPanel implements IComponentViewerPanel, IServiceViewerPanel
{	
	//-------- constants --------
	
	/** The property key for the settings object. */
	public static final String	PROPERTY_GUI_REFRESH	= "gui-refresh";
	
	//-------- attributes --------
	
	/** The jcc. */
	protected IControlCenter jcc;
	
	/** The component. */
//	protected IExternalAccess component;
	
	/** The update timer. */
	protected Timer timer;
	
	/** The timer delay. */
	protected int timerdelay;
	
	/** The latest settings of the agent. */
	protected AwarenessSettingsData	settings;
	
//	/** The IP address text field. */
//	protected JTextField	tfipaddress;
//
//	/** The port text field. */
//	protected JTextField	tfport;
	
	/** The discovery info table. */
	protected JTable	jtdis; 
	
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
	protected JComponent panel;
	
	/** The apply button. */
	protected JButton	buapply;
	
	/** The discovery mechanisms. */
	protected JCheckBox[] cbmechanisms;

	/** The Helper for GUI-independent tasks. **/
	protected AwarenessManagementAgentHelper helper;

		
	//-------- methods --------
	
	/**
	 *  Called once to initialize the panel.
	 *  Called on the swing thread.
	 *  @param jcc	The jcc.
	 * 	@param service	The service.
	 */
	public IFuture<Void> init(final IControlCenter jcc, final IService service)
	{
		final Future<Void> ret = new Future<Void>();
		jcc.getPlatformAccess().searchService( new ServiceQuery<>( IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM))
			.addResultListener(new ExceptionDelegationResultListener<IComponentManagementService, Void>(ret)
		{
			public void customResultAvailable(IComponentManagementService cms)
			{
				cms.getExternalAccess(service.getId().getProviderId())
					.addResultListener(new ExceptionDelegationResultListener<IExternalAccess, Void>(ret)
				{
					public void customResultAvailable(IExternalAccess result)
					{
						init(jcc, result).addResultListener(new DelegationResultListener<Void>(ret));
					}
				});
			}
		});
		return ret;
	}
	
	/**
	 *  Called once to initialize the panel.
	 *  Called on the swing thread.
	 *  @param jcc	The jcc.
	 * 	@param component The component.
	 */
	public IFuture<Void> init(final IControlCenter jcc, final IExternalAccess component)
	{
		this.jcc = jcc;
//		this.component = component;
		this.helper = new AwarenessManagementAgentHelper(component);
		
		JPanel	mainpanel = new JPanel(new GridBagLayout());
//		final JLabel	map	= new JLabel("No map available.", JLabel.CENTER);
		this.panel	= new JTabbedPane();
		panel.add(mainpanel, "Main");
		
		// Commented out as long as it is broken
//		panel.add(map, "Map");
		
//		map.addComponentListener(new ComponentAdapter()
//		{
//			public void componentResized(ComponentEvent e)
//			{
//				loadMap();
//			}
//			
//			public void componentShown(ComponentEvent e)
//			{
//				loadMap();
//			}	
//		
//			protected void loadMap()
//			{
//				map.setIcon(null);
//				map.setText("Loading map. Please wait...");
//				map.repaint();
//				panel.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
//
//				component.getComponentFeature(IRequiredServicesFeature.class).searchService(new ServiceQuery<>( IMessageService.class, RequiredServiceInfo.SCOPE_PLATFORM))
//					.addResultListener(new SwingResultListener<IMessageService>(new IResultListener<IMessageService>()
//				{
//					public void resultAvailable(IMessageService ms)
//					{
//						ms.getAddresses().addResultListener(new SwingResultListener<String[]>(new IResultListener<String[]>()
//						{
//							public void resultAvailable(String[] addresses)
//							{
//								boolean	done	= false;
//								for(String adr: addresses)
//								{
//									if(adr.startsWith("relay-http"))
//									{
//										adr	= RelayConnectionManager.httpAddress(adr) + "map?width="+map.getWidth()+"&height="+map.getHeight();
//		//								System.out.println("adr: "+adr);
//										try
//										{
//											map.setIcon(new ImageIcon(new URL(adr)));
//											map.setText(null);
//											map.repaint();
//											done	= true;
//											break;
//										}
//										catch(Exception ex)
//										{
//		//									ex.printStackTrace();
//											map.setIcon(null);
//											map.setText("Error while loading map: "+ex.toString());
//											map.repaint();
//											done	= true;
//											break;
//										}
//									}
//								}
//								
//								if(!done)
//								{
//									map.setText("Could not load map.");
//									map.repaint();					
//								}
//		
//								panel.setCursor(Cursor.getDefaultCursor());
//							}
//							
//							public void exceptionOccurred(Exception exception)
//							{
//								map.setIcon(null);
//								map.setText("Error while loading map: "+exception.toString());
//								map.repaint();
//								
//								panel.setCursor(Cursor.getDefaultCursor());
//							}
//						}));
//					}
//					
//					public void exceptionOccurred(Exception exception)
//					{
//						map.setIcon(null);
//						map.setText("Error while loading map: "+exception.toString());
//						map.repaint();
//						
//						panel.setCursor(Cursor.getDefaultCursor());
//					}
//				}));
//			}
//		});
		
		this.timerdelay = 5000;
				
//		tfipaddress = new JTextField(9);
//		tfipaddress.setPreferredSize(tfipaddress.getPreferredSize());
//		tfipaddress.setMinimumSize(tfipaddress.getPreferredSize());
//		tfipaddress.setHorizontalAlignment(JTextField.RIGHT);
//		tfport = new JTextField(4);
//		tfport.setPreferredSize(tfport.getPreferredSize());
//		tfport.setMinimumSize(tfport.getPreferredSize());
//		tfport.setHorizontalAlignment(JTextField.RIGHT);
		SpinnerNumberModel spmdelay = new SpinnerNumberModel(0, 0, 100, 1);
		spmdelay.setMaximum(null); // unbounded
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
			}
		});
		ActionListener	al	= new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				applySettings();
			}
		};
		TableModelListener	tml	= new TableModelListener()
		{
			public void tableChanged(TableModelEvent e)
			{
				applySettings();
			}
		};
		cbfast.addActionListener(al);
		cbautocreate.addActionListener(al);
		cbautodelete.addActionListener(al);
		includes.getModel().addTableModelListener(tml);
		excludes.getModel().addTableModelListener(tml);
		
		buapply = new JButton("Apply");
		buapply.setMargin(new Insets(0, 0, 0, 0));
		buapply.setToolTipText("Apply setting changes to component.");
		buapply.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent ae)
			{
				applySettings();
			}
		});
		buapply.setEnabled(false);
		Dimension	dim	= new Dimension(buapply.getMinimumSize().width, spdelay.getPreferredSize().height);
		buapply.setMinimumSize(dim);
		buapply.setPreferredSize(dim);
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
		pdissettings.add(spdelay, new GridBagConstraints(1, y, 1, 1, 1, 0, GridBagConstraints.NORTHWEST, 
			GridBagConstraints.HORIZONTAL, new Insets(1,1,1,1), 0, 0));
		pdissettings.add(buapply, new GridBagConstraints(2, y, GridBagConstraints.REMAINDER, 1, 0, 0, GridBagConstraints.NORTHWEST, 
			GridBagConstraints.HORIZONTAL, new Insets(1,1,1,1), 0, 0));
		y++;
		pdissettings.add(new JLabel("Fast startup awareness", JLabel.LEFT), 
			new GridBagConstraints(0, y, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, 
			GridBagConstraints.NONE, new Insets(1,1,1,1), 0, 1));
		pdissettings.add(cbfast, new GridBagConstraints(1, y, GridBagConstraints.REMAINDER, 1, 1, 0, GridBagConstraints.NORTHWEST, 
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
		
		JPanel pdisinfos = new JPanel(new BorderLayout());
		pdisinfos.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), " Discovery Infos "));
		final DiscoveryTableModel dismodel = new DiscoveryTableModel();
		jtdis = new JTable(dismodel);
		jtdis.setPreferredScrollableViewportSize(new Dimension(600, 120));
		jtdis.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		pdisinfos.add(BorderLayout.CENTER, new JScrollPane(jtdis));
		jtdis.setDefaultRenderer(Date.class, new DateTimeRenderer());
		jtdis.setDefaultRenderer(IComponentIdentifier.class, new ComponentIdentifierRenderer(jcc.getJCCAccess().getId().getRoot()));
		updateDiscoveryInfos(jtdis);
		jtdis.addMouseListener(new MouseAdapter()
		{
			public void mousePressed(MouseEvent e)
			{
				popup(e);
			}
			public void mouseReleased(MouseEvent e)
			{
				popup(e);
			}
			protected void	popup(MouseEvent e)
			{
				if(e.isPopupTrigger())
				{
					int	row	= jtdis.rowAtPoint(e.getPoint());
					if(row!=-1)
					{
						DiscoveryInfo	info	= dismodel.getList().get(row);
						String	name	= info.getComponentIdentifier().getPlatformName();
						String	template	= null; 
						if(name.length()>4 && name.charAt(name.length()-4)=='_')
							template	= name.substring(0, name.length()-4);
						
						JPopupMenu	menu	= new JPopupMenu("Adjust includes/excludes");
						
						String[]	entries	= includes.getEntries();
						boolean	found	= false;
						boolean	foundt	= false;
						for(int i=0; i<entries.length; i++)
						{
							if(name.startsWith(entries[i]))
							{
								menu.add(new AddRemoveAction(false, true, entries[i]));
							}
							found	= found || entries[i].equals(name);
							foundt	= foundt || (template!=null && entries[i].equals(template));
						}
						if(!found)
						{
							menu.add(new AddRemoveAction(true, true, name));	
						}
						if(template!=null && !foundt)
						{
							menu.add(new AddRemoveAction(true, true, template));							
						}

						menu.addSeparator();
						
						entries	= excludes.getEntries();
						found	= false;
						foundt	= false;
						for(int i=0; i<entries.length; i++)
						{
							if(name.startsWith(entries[i]))
							{
								menu.add(new AddRemoveAction(false, false, entries[i]));
							}
							found	= found || entries[i].equals(name);
							foundt	= foundt || (template!=null && entries[i].equals(template));
						}
						if(!found)
						{
							menu.add(new AddRemoveAction(true, false, name));	
						}
						if(template!=null && !foundt)
						{
							menu.add(new AddRemoveAction(true, false, template));							
						}
						
						menu.show(jtdis, e.getX(), e.getY());
					}
				}
			}
		});
		
		timer = new Timer(timerdelay, new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				updateDiscoveryInfos(jtdis);
				updateDiscoveryMechanisms();
			}
		});
		
		SpinnerNumberModel spmrefresh = new SpinnerNumberModel(5, 0, 100, 1);
		spmrefresh.setMaximum(null);	// unbounded.
		sprefresh = new JSpinner(spmrefresh);
		
		JButton burefresh = new JButton("Refresh");
		burefresh.setToolTipText("Refresh settings and discovery infos.");
		burefresh.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				refreshSettings();
				updateDiscoveryInfos(jtdis);
				updateDiscoveryMechanisms();
			}
		});
		
		JPanel	prefresh	= new JPanel();
		prefresh.add(sprefresh);
		prefresh.add(new JLabel("Gui refresh delay (0=off) [s]", JLabel.LEFT));
		prefresh.add(burefresh);
		
		SubcomponentTypeInfo[] dis = component.getModel().getSubcomponentTypes();
		cbmechanisms = new JCheckBox[dis.length];
		for(int i=0; i<dis.length; i++)
		{
			cbmechanisms[i] = new JCheckBox(dis[i].getName());
			final JCheckBox cb = cbmechanisms[i];
			final String localtype = dis[i].getName();
			cbmechanisms[i].addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					final boolean on = cb.isSelected();
					helper.setDiscoveryMechanismState(localtype, on);
				}
			});
		}
		
		final JPanel pdismechs = new JPanel(new GridBagLayout());
		pdismechs.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), " Discovery Mechanisms "));
		for(int i=0; i<cbmechanisms.length; i++)
		{
			pdismechs.add(cbmechanisms[i], new GridBagConstraints(i, 0, 1, 1, i==cbmechanisms.length-1? 1: 0, 0, GridBagConstraints.WEST, 
				GridBagConstraints.HORIZONTAL, new Insets(1,1,1,1), 0, 0));
		}
		
		
		// Layout of main panel starts here
		GridBagConstraints	gbc	= new GridBagConstraints();
		gbc.fill	= GridBagConstraints.BOTH;
		
		gbc.gridy	= 0;
		gbc.gridwidth	= 2;
		mainpanel.add(pprosettings, gbc);
		gbc.gridwidth	= 1;

		gbc.weightx	= 1;
		gbc.gridwidth	= 1;
		gbc.gridheight	= 2;
		gbc.insets	= new Insets(2,2,2,2);
		mainpanel.add(pincludes, gbc);
		mainpanel.add(pexcludes, gbc);
		gbc.insets	= new Insets(0,0,0,0);
		gbc.gridheight	= 1;
		
		gbc.gridy++;
		gbc.gridx	= 0;
		gbc.gridwidth	= 2;
		gbc.weightx	= 0;
		mainpanel.add(pdissettings, gbc);
		gbc.gridx	= GridBagConstraints.RELATIVE;
		gbc.gridwidth	= GridBagConstraints.REMAINDER;
				
		gbc.gridy++;
		mainpanel.add(pdismechs, gbc);
		
		gbc.gridy++;
		gbc.weighty	= 1;
		mainpanel.add(pdisinfos, gbc);
		
		gbc.gridy++;
		gbc.weightx	= 0;
		gbc.weighty	= 0;
		gbc.gridx	= GridBagConstraints.RELATIVE;
		gbc.fill	= GridBagConstraints.NONE;
		gbc.anchor	= GridBagConstraints.EAST;
		gbc.gridwidth	= GridBagConstraints.REMAINDER;
		mainpanel.add(prefresh, gbc);
				
		
		updateDiscoveryMechanisms();
		
		timer.start();
		
		return refreshSettings();
	}
	
	/**
	 *  Informs the panel that it should stop all its computation
	 */
	public IFuture<Void> shutdown()
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
			sprefresh.setValue(Integer.valueOf(settings.getValue()));
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
	protected IFuture<Void>	refreshSettings()
	{
		final Future<Void>	ret	= new Future<Void>();
		helper.getSettings().addResultListener(new SwingExceptionDelegationResultListener<AwarenessSettingsData, Void>(ret)
		{
			public void customResultAvailable(AwarenessSettingsData result)
			{
				updateSettings(result);
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
		helper.getDiscoveryInfos().addResultListener(new SwingDefaultResultListener<DiscoveryInfo[]>(jtdis)
		{
			public void customResultAvailable(DiscoveryInfo[] ds)
			{
				int sel = jtdis.getSelectedRow();
				DiscoveryTableModel dtm = (DiscoveryTableModel)jtdis.getModel();
				List<DiscoveryInfo> disinfos = dtm.getList();
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
				exception.printStackTrace();
				sprefresh.setValue(Integer.valueOf(0));
			}
		});
	}
	
	/**
	 *  Update the discovery mechanisms.
	 */
	protected void updateDiscoveryMechanisms()
	{
		helper.getActiveDiscoveryMechanisms().addResultListener(new SwingDefaultResultListener<Set<String>>()
		{
			public void customResultAvailable(Set<String> localtypes)
			{
				for(int i=0; i<cbmechanisms.length; i++)
				{
//					System.out.println("test: "+cbmechanisms[i].getText()+" "+localtypes);
					cbmechanisms[i].setSelected(localtypes.contains(cbmechanisms[i].getText()));
				}
			}
			
			public void customExceptionOccurred(Exception exception)
			{
				sprefresh.setValue(Integer.valueOf(0));
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
			final AwarenessSettingsData	settings	= new AwarenessSettingsData();	// local variable for XML transfer
//			settings.address	= InetAddress.getByName(tfipaddress.getText());
//			settings.port = Integer.parseInt(tfport.getText());
			settings.delay = ((Number)spdelay.getValue()).longValue()*1000;
			settings.fast = cbfast.isSelected();
			settings.autocreate = cbautocreate.isSelected();
			settings.autodelete = cbautodelete.isSelected();
			settings.includes	= includes.getEntries();
			settings.excludes	= excludes.getEntries();
			this.settings	= settings;	// todo: wait for step before setting?
			helper.setSettings(settings).addResultListener(new SwingDefaultResultListener<Void>(panel)
			{
				public void customResultAvailable(Void result)
				{
					buapply.setEnabled(false);
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
	protected void updateSettings(AwarenessSettingsData settings)
	{
		this.settings	= settings;
//		tfipaddress.setText(settings.address.getHostAddress());
//		tfport.setText(""+settings.port);
		spdelay.setValue(Long.valueOf(settings.delay/1000));
		cbfast.setSelected(settings.fast);
		cbautocreate.setSelected(settings.autocreate);
		cbautodelete.setSelected(settings.autodelete);
		includes.setEntries(settings.includes);
		excludes.setEntries(settings.excludes);		
	}

	/**
	 *  Action to add or remote an entry to/from the includes/excludes list.
	 */
	public class AddRemoveAction	extends AbstractAction
	{
		//-------- attributes --------
		
		/** Add  (true) or remove (false). */
		protected boolean	add;
		
		/** Add/remove to/from includes (true) or excludes (false). */
		protected boolean	includes;
		
		/** The entry. */
		protected String entry;
		
		//-------- constructors --------
		
		/**
		 *  Create a new add or remove action.
		 */
		public AddRemoveAction(boolean add, boolean includes, String entry)
		{
			super(add ? "Add '"+entry+"' to "+(includes?"includes":"excludes")
				: "Remove '"+entry+"' from "+(includes?"includes":"excludes"));
			this.add	= add;
			this.includes	= includes;
			this.entry	= entry;
		}
		
		//-------- Action interface --------
		
		/**
		 *  Perform the action.
		 */
		public void actionPerformed(ActionEvent e)
		{
			if(includes && add)
			{
				AwarenessAgentPanel.this.includes.addEntry(entry);
			}
			else if(includes && !add)
			{
				AwarenessAgentPanel.this.includes.removeEntry(entry);
			}
			else if(!includes && add)
			{
				AwarenessAgentPanel.this.excludes.addEntry(entry);
			}
			else if(!includes && !add)
			{
				AwarenessAgentPanel.this.excludes.removeEntry(entry);
			}
			
			applySettings();
		}
	}

	class DiscoveryTableModel extends AbstractTableModel
	{
		protected List<DiscoveryInfo> list;
		
		public DiscoveryTableModel()
		{
			this(new ArrayList<DiscoveryInfo>());
		}
		
		public DiscoveryTableModel(List<DiscoveryInfo> list)
		{
			this.list = list;
		}
		
		public List<DiscoveryInfo> getList()
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
					return "Properties";
				case 2:
					return "Time/Delay per Mechanism";
//				case 3:
//					return "Time of Last Info";
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
			return column==4;	// only proxy is editable
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
				value = ""+dif.getProperties();
			}
			else if(column == 2)
			{
//				value = new Long(dif.getDelay());
//				value = dif.getTimeDelays().toString();
				StringBuffer buf = new StringBuffer();
				Map<String, long[]> tds = dif.getTimeDelays();
				for(String key: tds.keySet())
				{
					long[] vals = tds.get(key);
					buf.append(key).append("=[").append(SUtil.SDF3.get().format(vals[0])).append(" ").append(vals[1]).append("], ");
				}
				value = buf.toString();
				
			}
//			else if(column == 3)
//			{
//				value = new Date(dif.getTime());
//			}
			else if(column == 3)
			{
				value = dif.getProxy()!=null && dif.getProxy().isDone() && dif.getProxy().getException()==null ? Boolean.TRUE : Boolean.FALSE;
			}
			else if(column == 4)
			{
				value = dif.isRemoteExcluded() ? Boolean.TRUE : Boolean.FALSE;
			}
			return value;
		}
		
		public void setValueAt(Object val, int row, int column)
		{
			DiscoveryInfo dif = (DiscoveryInfo)list.get(row);
			final boolean	create	= ((Boolean)val).booleanValue();
			final IComponentIdentifier	cid	= dif.getComponentIdentifier();
			final IComponentIdentifier	proxy	=  dif.getProxy()!=null && dif.getProxy().isDone() && dif.getProxy().getException()==null ? dif.getProxy().get() : null;
			if(create && dif.getProxy()==null || !create && proxy!=null)
			{
				// Ask user if platform should be added to excludes list.
				boolean	proceed	= true;
				if(!create && AwarenessManagementAgent.isIncluded(cid,
					AwarenessAgentPanel.this.includes.getEntries(),
					AwarenessAgentPanel.this.excludes.getEntries()))
				{
					String	name	= cid.getPlatformName();
					String	template	= null; 
					if(name.length()>4 && name.charAt(name.length()-4)=='_')
						template	= name.substring(0, name.length()-4);
					
					
					JPanel	pmsg	= new JPanel(new GridBagLayout());
					GridBagConstraints	gbc	= new GridBagConstraints();
					gbc.gridy	= 0;
					gbc.anchor	= GridBagConstraints.WEST;
					
					JTextArea	msg	= new JTextArea("Add entry to the excludes list?");
					msg.setEditable(false);  
					msg.setCursor(null);  
					msg.setOpaque(false);
					
					pmsg.add(msg, gbc);
					gbc.gridy++;
					gbc.insets	= new Insets(1,10,1,1);
					
					JRadioButton	rbname	= new JRadioButton(name);
					JRadioButton	rbtmp	= new JRadioButton(template);
					ButtonGroup	bg	= new ButtonGroup();
					bg.add(rbname);
					bg.add(rbtmp);
					rbtmp.setSelected(true);
					
					if(template==null)
					{
						pmsg.add(new JLabel(name), gbc);
					}
					else
					{
						pmsg.add(rbname, gbc);
						gbc.gridy++;
						pmsg.add(rbtmp, gbc);
					}
					
					int	res	= JOptionPane.showConfirmDialog(AwarenessAgentPanel.this.panel, pmsg, "Delete Proxy", JOptionPane.YES_NO_CANCEL_OPTION);
					if(res==JOptionPane.CANCEL_OPTION)
					{
						proceed	= false;
					}
					else if(res==JOptionPane.YES_OPTION)
					{
						String	entry	= template!=null && rbtmp.isSelected() ? template : name;
						excludes.addEntry(entry);
						applySettings();
					}
				}
				
				if(proceed)
				{
					AwarenessAgentPanel.this.panel.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					helper.createOrDeleteProxy(cid, create).addResultListener(new SwingDefaultResultListener<Void>(panel)
					{
						public void customResultAvailable(Void result)
						{
							AwarenessAgentPanel.this.panel.setCursor(Cursor.getDefaultCursor());
							updateDiscoveryInfos(jtdis);
						}
						public void customExceptionOccurred(Exception exception)
						{
							AwarenessAgentPanel.this.panel.setCursor(Cursor.getDefaultCursor());
							super.customExceptionOccurred(exception);
						}
					});
				}
			}
			
		}
		
		public Class<?> getColumnClass(int column)
		{
			Class<?> ret = Object.class;
			if(column == 0)
			{
				ret = IComponentIdentifier.class;
			}
			else if(column == 1)
			{
				ret = String.class;
			}
			else if(column == 2)
			{
				ret = String.class;
			}
//			else if(column == 3)
//			{
//				ret = Date.class;
//			}
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
}
