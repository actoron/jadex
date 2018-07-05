package jadex.tools.registry;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.Timer;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import jadex.base.gui.componentviewer.AbstractComponentViewerPanel;
import jadex.base.gui.jtable.ComponentIdentifierRenderer;
import jadex.base.gui.jtable.ServiceIdentifierRenderer;
import jadex.bridge.ClassInfo;
import jadex.bridge.ComponentIdentifier;
import jadex.bridge.ComponentNotFoundException;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.IServiceRegistry;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.search.ServiceNotFoundException;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.search.ServiceQueryInfo;
import jadex.bridge.service.search.ServiceRegistry;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.registry.IPeerRegistrySynchronizationService;
import jadex.bridge.service.types.registry.ISuperpeerRegistrySynchronizationService;
import jadex.commons.IResultCommand;
import jadex.commons.SUtil;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.gui.future.SwingResultListener;
import jadex.commons.gui.jtable.ClassInfoRenderer;
import jadex.commons.gui.jtable.DateTimeRenderer;
import jadex.commons.transformation.annotations.Classname;
import jadex.platform.service.registry.PeerRegistrySynchronizationAgent;
import jadex.platform.service.registry.SuperpeerRegistrySynchronizationAgent;

/**
 *  Panel to view the registry.
 */
public class RegistryPanel extends AbstractComponentViewerPanel
{
	/** The table of registry entries. */
	protected JTable jtservices;
	
	/** The table model. */
	protected ServiceTableModel sermodel;
	
	/** The table of queries. */
	protected JTable jtqueries;
	
	/** The table model. */
	protected QueryTableModel querymodel;
	
	/** The table of partner platforms. */
	protected JTable jtpartners;
	
	/** The table model. */
	protected CidTableModel partnermodel;
	
	/** The table of client platforms. */
	protected JTable jtclients;
	
	/** The table model. */
	protected CidTableModel clientmodel;
	
	/** The timer. */
	protected Timer timer;
	
	/** The timer delay. */
	protected int timerdelay;
	
	/** The textfield with superpeer. */
	protected JTextField tfsuperpeer;
	
	/** The make superpeer/peer button. */
	protected JButton buswitchpeer;
	
	/** fetch superpeer button. */
	protected JButton bufetchpeer;
	
	/** The tabbed pane. */
	protected JTabbedPane tpane;
	
	/**
	 *  Get the component.
	 */
	public JComponent getComponent()
	{
		timerdelay = 5000;
		tpane = new JTabbedPane();
		
		JPanel panel = new JPanel(new BorderLayout());//new GridBagLayout());
		panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Registry Information"));
		
		JLabel lsp = new JLabel("Superpeer: ");
		tfsuperpeer = new JTextField();
		tfsuperpeer.setEditable(false);
		
		buswitchpeer = new JButton("Switch");
		buswitchpeer.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				buswitchpeer.setEnabled(false);
				
				getActiveComponent().searchService( new ServiceQuery<>( IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM))
					.addResultListener(new IResultListener<IComponentManagementService>()
				{
					public void resultAvailable(final IComponentManagementService cms)
					{
						getActiveComponent().searchService( new ServiceQuery<>( ISuperpeerRegistrySynchronizationService.class, RequiredServiceInfo.SCOPE_PLATFORM))
							.addResultListener(new IResultListener<ISuperpeerRegistrySynchronizationService>()
						{
							public void resultAvailable(ISuperpeerRegistrySynchronizationService sps)
							{
								cms.destroyComponent(((IService)sps).getServiceIdentifier().getProviderId());
								cms.createComponent("registrypeer", PeerRegistrySynchronizationAgent.class.getName()+".class", null);
							}
							
							public void exceptionOccurred(Exception exception)
							{
								getActiveComponent().searchService( new ServiceQuery<>( IPeerRegistrySynchronizationService.class, RequiredServiceInfo.SCOPE_PLATFORM))
									.addResultListener(new IResultListener<IPeerRegistrySynchronizationService>()
								{
									public void resultAvailable(IPeerRegistrySynchronizationService ps)
									{
										cms.destroyComponent(((IService)ps).getServiceIdentifier().getProviderId());
										cms.createComponent("registrysuperpeer", SuperpeerRegistrySynchronizationAgent.class.getName()+".class", null);
									}
									
									public void exceptionOccurred(Exception exception)
									{
									}
								});
							}
						});
					}
					
					public void exceptionOccurred(Exception exception)
					{
					}
				});
			}
		});
		
		bufetchpeer = new JButton("Refresh");
		bufetchpeer.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				fetchSuperpeer();
			}
		});
		
		JPanel psp = new JPanel(new GridBagLayout());
		psp.add(lsp, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2,2,2,2), 0, 0));
		psp.add(tfsuperpeer, new GridBagConstraints(1, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(2,2,2,2), 0, 0));
		psp.add(buswitchpeer, new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(2,2,2,2), 0, 0));
		psp.add(bufetchpeer, new GridBagConstraints(3, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(2,2,2,2), 0, 0));

		// create panel with service table
		JPanel pserinfos = new JPanel(new BorderLayout());
		pserinfos.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), " Registry Services "));
		sermodel = new ServiceTableModel();
		jtservices = new JTable(sermodel)
		{
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column) 
			{    
                int modelidx = convertRowIndexToModel(row);
                Component c = super.prepareRenderer(renderer, row, column);
                c.setBackground(sermodel.getRowColor(modelidx));
                return c;
			}
		};
		jtservices.setAutoCreateRowSorter(true);
		jtservices.setRowSelectionAllowed(true);
//		jtdis.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        jtservices.setPreferredScrollableViewportSize(new Dimension(600, 120));
		jtservices.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		pserinfos.add(BorderLayout.CENTER, new JScrollPane(jtservices));
		jtservices.setDefaultRenderer(Date.class, new DateTimeRenderer());
		jtservices.setDefaultRenderer(ComponentIdentifier.class, new ComponentIdentifierRenderer(null));
		jtservices.setDefaultRenderer(IComponentIdentifier.class, new ComponentIdentifierRenderer(getActiveComponent().getIdentifier().getRoot()));
		jtservices.setDefaultRenderer(ClassInfo.class, new ClassInfoRenderer());
		jtservices.setDefaultRenderer(IServiceIdentifier.class, new ServiceIdentifierRenderer());
		jtservices.setDefaultRenderer(Set.class, new DefaultTableCellRenderer());
		
		// create panel with query table
		JPanel pqueryinfos = new JPanel(new BorderLayout());
		pqueryinfos.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), " Registry Queries "));
		querymodel = new QueryTableModel();
		jtqueries = new JTable(querymodel)
		{
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column) 
			{    
                int modelidx = convertRowIndexToModel(row);
                Component c = super.prepareRenderer(renderer, row, column);
                c.setBackground(querymodel.getRowColor(modelidx));
                return c;
			}
		};
		jtqueries.setAutoCreateRowSorter(true);
		jtqueries.setRowSelectionAllowed(true);
//		jtdis.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		jtqueries.setPreferredScrollableViewportSize(new Dimension(600, 120));
		jtqueries.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		pqueryinfos.add(BorderLayout.CENTER, new JScrollPane(jtqueries));
		jtqueries.setDefaultRenderer(Date.class, new DateTimeRenderer());
		jtqueries.setDefaultRenderer(ComponentIdentifier.class, new ComponentIdentifierRenderer(null));
		jtqueries.setDefaultRenderer(IComponentIdentifier.class, new ComponentIdentifierRenderer(getActiveComponent().getIdentifier().getRoot()));
		jtqueries.setDefaultRenderer(ClassInfo.class, new ClassInfoRenderer());
		jtqueries.setDefaultRenderer(IServiceIdentifier.class, new ServiceIdentifierRenderer());
		jtqueries.setDefaultRenderer(String[].class, new DefaultTableCellRenderer()
		{
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
			{
				super.getTableCellRendererComponent(table, null, isSelected, hasFocus, row, column);
				String[] sa = (String[])value;
				if(sa!=null)
				{
					setText(Arrays.toString(sa));
				}
				return this;
			}
		});
		
		
		// create panel with partner table
		JPanel ppartners = new JPanel(new BorderLayout());
		ppartners.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), " Partner Platforms "));
		partnermodel = new CidTableModel();
		jtpartners = new JTable(partnermodel);
		jtpartners.setAutoCreateRowSorter(true);
		jtpartners.setRowSelectionAllowed(true);
		jtpartners.setPreferredScrollableViewportSize(new Dimension(600, 120));
		jtpartners.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		ppartners.add(BorderLayout.CENTER, new JScrollPane(jtpartners));
		jtpartners.setDefaultRenderer(ComponentIdentifier.class, new ComponentIdentifierRenderer(null));
		jtpartners.setDefaultRenderer(IComponentIdentifier.class, new ComponentIdentifierRenderer(getActiveComponent().getIdentifier().getRoot()));
		
		// create panel with partner table
		JPanel pclients = new JPanel(new BorderLayout());
		pclients.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), " Client Platforms "));
		clientmodel = new CidTableModel();
		jtclients = new JTable(clientmodel);
		jtclients.setAutoCreateRowSorter(true);
		jtclients.setRowSelectionAllowed(true);
		jtclients.setPreferredScrollableViewportSize(new Dimension(600, 120));
		jtclients.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		pclients.add(BorderLayout.CENTER, new JScrollPane(jtclients));
		jtclients.setDefaultRenderer(ComponentIdentifier.class, new ComponentIdentifierRenderer(null));
		jtclients.setDefaultRenderer(IComponentIdentifier.class, new ComponentIdentifierRenderer(getActiveComponent().getIdentifier().getRoot()));
		
		tpane.addTab("Services", pserinfos);
		tpane.addTab("Queries", pqueryinfos);
		tpane.addTab("Partners", ppartners);
		tpane.addTab("Clients", pclients);
		
		updateAll();
		
		final Runnable updatesizes = new Runnable()
		{
			public void run()
			{
				resizeColumns(jtservices, new float[]{10f, 10f, 10f, 15f, 15f, 15f, 15f, 10f});
				resizeColumns(jtqueries, new float[]{10f, 10f, 10f, 10f, 10f, 10f, 15f, 15f});
				resizeColumns(jtpartners, new float[]{10f, 90f});
				resizeColumns(jtclients, new float[]{10f, 90f});
			}
		};
		
		panel.addComponentListener(new ComponentAdapter()
		{
			public void componentResized(ComponentEvent e) 
			{
				updatesizes.run();
		    }
		});
		updatesizes.run();
		
		timer = new Timer(timerdelay, new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				updateAll();
			}
		});
		timer.start();
		
		panel.add(psp, BorderLayout.NORTH);
		panel.add(tpane, BorderLayout.CENTER);
		
		return panel;
	}

	/** 
	 *  Update all parts.
	 */
	protected void updateAll()
	{
		updateServiceInfos();
		updateQueryInfos();
		updateSuperpeerInfo();
		updatePartners();
		updateClients();
	}
	
	/**
	 *  Resize the table columns.
	 */
	protected void resizeColumns(JTable table, float[] perc) 
	{
	    int width = table.getWidth();
	    TableColumnModel cm = table.getColumnModel();
	    for(int i=0; i<cm.getColumnCount(); i++) 
	    {
	        cm.getColumn(i).setPreferredWidth(Math.round(perc[i] * width));
	    }
	}
	
	/**
	 *  Update the registry infos.
	 */
	protected void updateServiceInfos()
	{
//		Set<IService> alls = getRegistry().getAllServices();
		executeRegistryCommand(new IResultCommand<Object, IServiceRegistry>()
		{
			public Object execute(IServiceRegistry reg)
			{
				return reg.getAllServices();
			}
		}).addResultListener(new SwingResultListener<Object>(new IResultListener<Object>()
		{
			public void resultAvailable(Object result)
			{
				Set<IService> alls = (Set<IService>)result;
//				System.out.println("refresh: "+alls.size());
				
				int sel = jtservices.getSelectedRow();
				List<IService> reginfos = sermodel.getList();
				reginfos.clear();
				for(Iterator<IService> it=alls.iterator(); it.hasNext(); )
				{
					reginfos.add(it.next());
				}
				
				sermodel.fireTableDataChanged();
				if(sel!=-1 && sel<alls.size())
					((DefaultListSelectionModel)jtservices.getSelectionModel()).setSelectionInterval(sel, sel);
			}
			
			public void exceptionOccurred(Exception exception)
			{
				exception.printStackTrace();
			}
		}));
	}
	
	/**
	 *  Update the registry infos.
	 */
	protected void updateQueryInfos()
	{
//		Set<IService> alls = getRegistry().getAllServices();
		executeRegistryCommand(new IResultCommand<Object, IServiceRegistry>()
		{
			@Classname("executeRegistryCommandListener")
			public Object execute(IServiceRegistry reg)
			{
				return reg.getAllQueries();
			}
		}).addResultListener(new SwingResultListener<Object>(new IResultListener<Object>()
		{
			public void resultAvailable(Object result)
			{
				Set<ServiceQueryInfo<IService>> alls = (Set<ServiceQueryInfo<IService>>)result;
//				System.out.println("refresh: "+alls.size());
				
				int sel = jtqueries.getSelectedRow();
				List<ServiceQueryInfo<IService>> reginfos = querymodel.getList();
				reginfos.clear();
				for(Iterator<ServiceQueryInfo<IService>> it=alls.iterator(); it.hasNext(); )
				{
					reginfos.add(it.next());
				}
				
				sermodel.fireTableDataChanged();
				if(sel!=-1 && sel<alls.size())
					((DefaultListSelectionModel)jtqueries.getSelectionModel()).setSelectionInterval(sel, sel);
			}
			
			public void exceptionOccurred(Exception exception)
			{
				exception.printStackTrace();
			}
		}));
	}
	
	/**
	 *  Update the superpeer field.
	 */
	protected void updateSuperpeerInfo()
	{
//		ISuperpeerRegistrySynchronizationService sps = getRegistry().searchServiceSync(new ServiceQuery<ISuperpeerRegistrySynchronizationService>(ISuperpeerRegistrySynchronizationService.class, null, null, null, null));
		
//		final IComponentIdentifier fplat = getActiveComponent().getComponentIdentifier().getRoot();
//		getActiveComponent().searchService( new ServiceQuery<>( ISuperpeerRegistrySynchronizationService.class, RequiredServiceInfo.SCOPE_PLATFORM))
//			.addResultListener(new IResultListener<ISuperpeerRegistrySynchronizationService>()
//		{
//			public void resultAvailable(ISuperpeerRegistrySynchronizationService sps)
//			{
//				tfsuperpeer.setText("self");
//				buswitchpeer.setEnabled(true);
//			}
//			
//			public void exceptionOccurred(Exception exception)
//			{
//				tfsuperpeer.setText("fetching ...");
//				
//				executeRegistryCommand(new IResultCommand<Object, IServiceRegistry>()
//				{
//					public Object execute(IServiceRegistry reg)
//					{
//						return reg.getSuperpeer();
//					}
//				}).addResultListener(new SwingResultListener<Object>(new IResultListener<Object>()
//				{
//					public void resultAvailable(Object result)
//					{
//						IComponentIdentifier cid = (IComponentIdentifier)result;
//						tfsuperpeer.setText(cid==null? "n/a": cid.getName());
//						buswitchpeer.setEnabled(true);
//					}
//					
//					public void exceptionOccurred(Exception exception)
//					{
//						if(exception instanceof ComponentNotFoundException || exception instanceof ServiceNotFoundException)
//						{
//							tfsuperpeer.setText("None");
//						}
//						else
//						{
//							tfsuperpeer.setText("Error: "+exception.getMessage());
//						}
//						buswitchpeer.setEnabled(true);
//					}
//				}));
//			}
//		});
		
		fetchSuperpeer();
	}
	
	/**
	 *  Fetch superpeer info.
	 */
	protected void fetchSuperpeer()
	{
		getActiveComponent().searchService( new ServiceQuery<>( IPeerRegistrySynchronizationService.class, RequiredServiceInfo.SCOPE_PLATFORM))
			.addResultListener(new IResultListener<IPeerRegistrySynchronizationService>()
		{
			public void exceptionOccurred(Exception exception)
			{
				tfsuperpeer.setText("self");
				bufetchpeer.setEnabled(true);
			}
			
			public void resultAvailable(IPeerRegistrySynchronizationService sps)
			{
				tfsuperpeer.setText("fetching ...");
				
				sps.getSuperpeer(false).addResultListener(new SwingResultListener<IComponentIdentifier>(new IResultListener<IComponentIdentifier>()
				{
					public void resultAvailable(IComponentIdentifier result)
					{
						IComponentIdentifier cid = (IComponentIdentifier)result;
						tfsuperpeer.setText(cid==null? "n/a": cid.getName());
						bufetchpeer.setEnabled(true);
					}
					
					public void exceptionOccurred(Exception exception)
					{
						if(exception instanceof ComponentNotFoundException || exception instanceof ServiceNotFoundException)
						{
							tfsuperpeer.setText("None");
						}
						else
						{
							tfsuperpeer.setText("Error: "+exception.getMessage());
						}
						bufetchpeer.setEnabled(true);
					}
				}));
			}
		});
	}
	
	/**
	 *  Update the superpeer partners.
	 */
	protected void updatePartners()
	{
//		ISuperpeerRegistrySynchronizationService sps = getRegistry().searchServiceSync(new ServiceQuery<ISuperpeerRegistrySynchronizationService>(ISuperpeerRegistrySynchronizationService.class, null, null, null, null));
		
//		final IComponentIdentifier fplat = getActiveComponent().getComponentIdentifier().getRoot();
		getActiveComponent().searchService( new ServiceQuery<>( ISuperpeerRegistrySynchronizationService.class, RequiredServiceInfo.SCOPE_PLATFORM))
			.addResultListener(new IResultListener<ISuperpeerRegistrySynchronizationService>()
		{
			public void resultAvailable(ISuperpeerRegistrySynchronizationService sps)
			{
				sps.getPartnerSuperpeers().addResultListener(new SwingResultListener<Collection<IComponentIdentifier>>(new IResultListener<Collection<IComponentIdentifier>>()
				{
					public void resultAvailable(Collection<IComponentIdentifier> result) 
					{
						int sel = jtpartners.getSelectedRow();
						List<IComponentIdentifier> vals = partnermodel.getList();
						vals.clear();
						for(Iterator<IComponentIdentifier> it=result.iterator(); it.hasNext(); )
						{
							vals.add(it.next());
						}
						
						partnermodel.fireTableDataChanged();
						if(sel!=-1 && sel<result.size())
							((DefaultListSelectionModel)jtpartners.getSelectionModel()).setSelectionInterval(sel, sel);
					}
					
					public void exceptionOccurred(Exception exception)
					{
					}
				}));
			}
			
			public void exceptionOccurred(Exception exception)
			{
			}
		});
	}
	
	/**
	 *  Update the superpeer clients.
	 */
	protected void updateClients()
	{
//		ISuperpeerRegistrySynchronizationService sps = getRegistry().searchServiceSync(new ServiceQuery<ISuperpeerRegistrySynchronizationService>(ISuperpeerRegistrySynchronizationService.class, null, null, null, null));
		
//		final IComponentIdentifier fplat = getActiveComponent().getComponentIdentifier().getRoot();
		getActiveComponent().searchService( new ServiceQuery<>( ISuperpeerRegistrySynchronizationService.class, RequiredServiceInfo.SCOPE_PLATFORM))
			.addResultListener(new IResultListener<ISuperpeerRegistrySynchronizationService>()
		{
			public void resultAvailable(ISuperpeerRegistrySynchronizationService sps)
			{
				sps.getClients().addResultListener(new SwingResultListener<Collection<IComponentIdentifier>>(new IResultListener<Collection<IComponentIdentifier>>()
				{
					public void resultAvailable(Collection<IComponentIdentifier> result) 
					{
						int sel = jtclients.getSelectedRow();
						List<IComponentIdentifier> vals = clientmodel.getList();
						vals.clear();
						for(Iterator<IComponentIdentifier> it=result.iterator(); it.hasNext(); )
						{
							vals.add(it.next());
						}
						
						clientmodel.fireTableDataChanged();
						if(sel!=-1 && sel<result.size())
							((DefaultListSelectionModel)jtclients.getSelectionModel()).setSelectionInterval(sel, sel);
					}
					
					public void exceptionOccurred(Exception exception)
					{
					}
				}));
			}
			
			public void exceptionOccurred(Exception exception)
			{
			}
		});
	}
	
	/**
	 *  Execute a registry command.
	 */
	public IFuture<Object> executeRegistryCommand(final IResultCommand<Object, IServiceRegistry> cmd)
	{
		return getActiveComponent().scheduleStep(new IComponentStep<Object>()
		{
			@Classname("executeRegistryCommand")
			public IFuture<Object> execute(IInternalAccess ia)
			{
				IFuture<Object> ret = null;
				try
				{
					IServiceRegistry reg = ServiceRegistry.getRegistry(ia.getIdentifier());
					Object res = cmd.execute(reg);
					if(res instanceof IFuture)
						ret = (IFuture<Object>)res;
					else
						ret = new Future<Object>(res);
				}
				catch(Exception e)
				{
					((Future<Object>)ret).setException(e);
				}
				return ret;
			}
		});
	}
	
	class ServiceTableModel extends AbstractTableModel
	{
		protected List<IService> list;
		
		public ServiceTableModel()
		{
			this(new ArrayList<IService>());
		}
		
		public ServiceTableModel(List<IService> list)
		{
			this.list = list;
		}
		
		public List<IService> getList()
		{
			return list;
		}

		public int getRowCount()
		{
			return list.size();
		}

		public int getColumnCount()
		{
			return 8;
		}

		public String getColumnName(int column)
		{
			switch(column)
			{
				case 0:
					return "No";
				case 1:
					return "Type";
				case 2:
					return "Owner";
				case 3:
					return "Platform";
				case 4:
					return "Service Id";
				case 5:
					return "Tags";
				case 6:
					return "Networks";
				case 7:
					return "Unrestricted";
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
			IService ser = list.get(row);
			
			if(ser==null)
				return null;
			
			if(column == 0)
			{
				value = row;
			}
			else if(column == 1)
			{
				value = ser.getServiceIdentifier().getServiceType();
			}
			else if(column == 2)
			{
				value = ser.getServiceIdentifier().getProviderId();
			}
			else if(column == 3)
			{
				value = ser.getServiceIdentifier().getProviderId().getRoot();
			}
			else if(column == 4)
			{
				value = ser.getServiceIdentifier();
			}
			else if(column == 5)
			{
//				Map<String, Object> sprops = ser.getPropertyMap();
//				if(sprops != null)
//					value = (Set<String>)sprops.get(TagProperty.SERVICE_PROPERTY_NAME);
				value = ser.getServiceIdentifier().getTags();
			}
			else if(column == 6)
			{
				value = ser.getServiceIdentifier().getNetworkNames();
			}
			else if(column == 7)
			{
				value = ser.getServiceIdentifier().isUnrestricted();
			}
			return value;
		}
		
		public void setValueAt(Object val, int row, int column)
		{
		}
		
		public Class<?> getColumnClass(int column)
		{
			Class<?> ret = Object.class;
			if(column == 0)
			{
				ret = Integer.class;
			}
			else if(column == 1)
			{
				ret = ClassInfo.class;
			}
			else if(column == 2)
			{
				ret = ComponentIdentifier.class;
			}
			else if(column == 3)
			{
				ret = IComponentIdentifier.class;
			}
			else if(column == 4)
			{
				ret = IServiceIdentifier.class;
			}
			else if(column == 5)
			{
				ret = Set.class;
			}
			else if(column == 6)
			{
				ret = Set.class;
			}
			else if(column == 7)
			{
				ret = Boolean.class;
			}
			return ret;
		}	
		
		public Color getRowColor(int row) 
		{
			Color ret = Color.WHITE;
			try
			{
				IComponentIdentifier cid = (IComponentIdentifier)getValueAt(row, 3);
				
				if(!cid.getRoot().equals(getActiveComponent().getIdentifier()))
				{
					int cc = SUtil.diffuseStringHash(cid.toString());
					float cc2 = ((float)cc)/Integer.MAX_VALUE;
//					float cv = (float)(cc2/2+0.5);
					
//					System.out.println(cid+" "+cc+" "+cc2+" "+cv);
					
//					ret = new Color((int)(cc2 * 0x1000000));
//					ret = new Color(cv,cv,cv);
					ret = Color.getHSBColor(cc2, 0.2f, 0.9f);
					
//					ret = new Color(dig[0] & 0xFF, dig[1] & 0xFF, dig[2] & 0xFF, dig[3] & 0xFF);
					
//					MessageDigest md = MessageDigest.getInstance("MD5");
//					byte[] dig = md.digest(cid.getName().getBytes());
//					ret = new Color(dig[0] & 0xFF, dig[1] & 0xFF, dig[2] & 0xFF, dig[3] & 0xFF);
				}
			}
			catch(Exception e)
			{
//				throw new RuntimeException(e);
			}
			return ret;
		}
	};
	
	class QueryTableModel extends AbstractTableModel
	{
		protected List<ServiceQueryInfo<IService>> list;
		
		public QueryTableModel()
		{
			this(new ArrayList<ServiceQueryInfo<IService>>());
		}
		
		public QueryTableModel(List<ServiceQueryInfo<IService>> list)
		{
			this.list = list;
		}
		
		public List<ServiceQueryInfo<IService>> getList()
		{
			return list;
		}

		public int getRowCount()
		{
			return list.size();
		}

		public int getColumnCount()
		{
			return 8;
		}

		public String getColumnName(int column)
		{
			switch(column)
			{
				case 0:
					return "Id";
				case 1:
					return "Interface";
				case 2:
					return "Owner";
				case 3:
					return "Provider";
				case 4:
					return "Platform";
				case 5:
					return "Scope";
				case 6:
					return "Tags";
				case 7:
					return "Networks";
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
			ServiceQueryInfo<?> query = list.get(row);
			if(column == 0)
			{
				value = query.getQuery().getId();
			}
			else if(column == 1)
			{
				value = query.getQuery().getServiceType();
			}
			else if(column == 2)
			{
				value = query.getQuery().getOwner();
			}
			else if(column == 3)
			{
				value = query.getQuery().getProvider();
			}
			else if(column == 4)
			{
				value = query.getQuery().getPlatform();
			}
			else if(column == 5)
			{
				value = query.getQuery().getScope();
			}
			else if(column == 6)
			{
				value = query.getQuery().getServiceTags();
			}
			else if(column == 7)
			{
				value = query.getQuery().getNetworkNames();
			}
			return value;
		}
		
		public void setValueAt(Object val, int row, int column)
		{
		}
		
		public Class<?> getColumnClass(int column)
		{
			Class<?> ret = Object.class;
			if(column == 0)
			{
				ret = String.class;
			}
			else if(column == 1)
			{
				ret = ClassInfo.class;
			}
			else if(column == 2)
			{
				ret = ComponentIdentifier.class;
			}
			else if(column == 3)
			{
				ret = IComponentIdentifier.class;
			}
			else if(column == 4)
			{
				ret = IComponentIdentifier.class;
			}
			else if(column == 5)
			{
				ret = String.class;
			}
			else if(column == 6)
			{
				ret = String[].class;
			}
			else if(column == 7)
			{
				ret = String[].class;
			}
			return ret;
		}	
		
		public Color getRowColor(int row) 
		{
			Color ret = Color.WHITE;
			try
			{
				IComponentIdentifier cid = (IComponentIdentifier)getValueAt(row, 2);
				
				if(cid!=null && !cid.getRoot().equals(getActiveComponent().getIdentifier()))
				{
					int cc = SUtil.diffuseStringHash(cid.toString());
					float cc2 = ((float)cc)/Integer.MAX_VALUE;
//					float cv = (float)(cc2/2+0.5);
					
//					System.out.println(cid+" "+cc+" "+cc2+" "+cv);
					
//					ret = new Color((int)(cc2 * 0x1000000));
//					ret = new Color(cv,cv,cv);
					ret = Color.getHSBColor(cc2, 0.2f, 0.9f);
					
//					ret = new Color(dig[0] & 0xFF, dig[1] & 0xFF, dig[2] & 0xFF, dig[3] & 0xFF);
					
//					MessageDigest md = MessageDigest.getInstance("MD5");
//					byte[] dig = md.digest(cid.getName().getBytes());
//					ret = new Color(dig[0] & 0xFF, dig[1] & 0xFF, dig[2] & 0xFF, dig[3] & 0xFF);
				}
			}
			catch(Exception e)
			{
				//throw new RuntimeException(e);
			}
			return ret;
		}
	};
	
	class CidTableModel extends AbstractTableModel
	{
		protected List<IComponentIdentifier> list;
		
		public CidTableModel()
		{
			this(new ArrayList<IComponentIdentifier>());
		}
		
		public CidTableModel(List<IComponentIdentifier> list)
		{
			this.list = list;
		}
		
		public List<IComponentIdentifier> getList()
		{
			return list;
		}

		public int getRowCount()
		{
			return list.size();
		}

		public int getColumnCount()
		{
			return 2;
		}

		public String getColumnName(int column)
		{
			switch(column)
			{
				case 0:
					return "No";
				case 1:
					return "Component ID";
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
			IComponentIdentifier cid = list.get(row);
			
			if(cid==null)
				return null;
			
			if(column == 0)
			{
				value = row;
			}
			else if(column == 1)
			{
				value = cid;
			}
			return value;
		}
		
		public void setValueAt(Object val, int row, int column)
		{
		}
		
		public Class<?> getColumnClass(int column)
		{
			Class<?> ret = Object.class;
			if(column == 0)
			{
				ret = Integer.class;
			}
			else if(column == 1)
			{
				ret = ComponentIdentifier.class;
			}
			return ret;
		}	
	};
	
	@Override
	public IFuture<Void> shutdown()
	{
		timer.stop();
		return super.shutdown();
	}
}
