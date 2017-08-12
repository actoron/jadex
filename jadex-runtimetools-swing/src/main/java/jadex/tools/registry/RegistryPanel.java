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
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.Timer;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.AbstractTableModel;
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

/**
 *  Panel to view the registry.
 */
public class RegistryPanel extends AbstractComponentViewerPanel
{
	/** The table of registry entries. */
	protected JTable jtreg;
	
	/** The timer. */
	protected Timer timer;
	
	/** The timer delay. */
	protected int timerdelay;
	
	/** The table model. */
	protected RegistryTableModel regmodel;
	
	/** The textfield with superpeer. */
	protected JTextField tfsuperpeer;
	
	/** The make superpeer/peer button. */
	protected JButton buswitchpeer;
	
	/**
	 *  Get the component.
	 */
	public JComponent getComponent()
	{
		timerdelay = 5000;
		
		JPanel	panel	= new JPanel(new BorderLayout());//new GridBagLayout());
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
				
				SServiceProvider.getService(getActiveComponent(), IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
					.addResultListener(new IResultListener<IComponentManagementService>()
				{
					public void resultAvailable(final IComponentManagementService cms)
					{
						SServiceProvider.getService(getActiveComponent(), ISuperpeerRegistrySynchronizationService.class, RequiredServiceInfo.SCOPE_PLATFORM)
							.addResultListener(new IResultListener<ISuperpeerRegistrySynchronizationService>()
						{
							public void resultAvailable(ISuperpeerRegistrySynchronizationService sps)
							{
								cms.destroyComponent(((IService)sps).getServiceIdentifier().getProviderId());
							}
							
							public void exceptionOccurred(Exception exception)
							{
								SServiceProvider.getService(getActiveComponent(), IPeerRegistrySynchronizationService.class, RequiredServiceInfo.SCOPE_PLATFORM)
									.addResultListener(new IResultListener<IPeerRegistrySynchronizationService>()
								{
									public void resultAvailable(IPeerRegistrySynchronizationService ps)
									{
										cms.destroyComponent(((IService)ps).getServiceIdentifier().getProviderId());
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
		
		JPanel psp = new JPanel(new GridBagLayout());
		psp.add(lsp, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2,2,2,2), 0, 0));
		psp.add(tfsuperpeer, new GridBagConstraints(1, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(2,2,2,2), 0, 0));
		psp.add(buswitchpeer, new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(2,2,2,2), 0, 0));
		
		JPanel preginfos = new JPanel(new BorderLayout());
		preginfos.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), " Registry Services "));
		regmodel = new RegistryTableModel();
		jtreg = new JTable(regmodel)
		{
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column) 
			{    
                int modelidx = convertRowIndexToModel(row);
                Component c = super.prepareRenderer(renderer, row, column);
                c.setBackground(regmodel.getRowColor(modelidx));
                return c;
			}
		};
		jtreg.setAutoCreateRowSorter(true);
		
//		jtdis.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        jtreg.setPreferredScrollableViewportSize(new Dimension(600, 120));
		jtreg.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		preginfos.add(BorderLayout.CENTER, new JScrollPane(jtreg));
		jtreg.setDefaultRenderer(Date.class, new DateTimeRenderer());
		jtreg.setDefaultRenderer(ComponentIdentifier.class, new ComponentIdentifierRenderer());
		jtreg.setDefaultRenderer(IComponentIdentifier.class, new ComponentIdentifierRenderer(getActiveComponent().getComponentIdentifier().getRoot()));
		jtreg.setDefaultRenderer(ClassInfo.class, new ClassInfoRenderer());
		jtreg.setDefaultRenderer(IServiceIdentifier.class, new ServiceIdentifierRenderer());
		updateRegistryInfos();
		
		timer = new Timer(timerdelay, new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				updateRegistryInfos();
				updateSuperpeerInfo();
			}
		});
		timer.start();
		
		panel.add(psp, BorderLayout.NORTH);
		panel.add(preginfos, BorderLayout.CENTER);
		
		final float[] perc = new float[]{6.0f, 23.5f, 23.5f, 23.5f, 23.5f};
		
		panel.addComponentListener(new ComponentAdapter()
		{
			public void componentResized(ComponentEvent e) 
			{
				resizeColumns(perc);
		    }
		});
		
		resizeColumns(perc);
		
		return panel;
	}

	/**
	 *  Resize the table columns.
	 */
	protected void resizeColumns(float[] perc) 
	{
	    int width = jtreg.getWidth();
	    TableColumnModel cm = jtreg.getColumnModel();
	    for(int i=0; i<cm.getColumnCount(); i++) 
	    {
	        cm.getColumn(i).setPreferredWidth(Math.round(perc[i] * width));
	    }
	}
	
	/**
	 *  Update the registry infos.
	 */
	protected void updateRegistryInfos()
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
				
				int sel = jtreg.getSelectedRow();
				List<IService> reginfos = regmodel.getList();
				reginfos.clear();
				for(Iterator<IService> it=alls.iterator(); it.hasNext(); )
				{
					reginfos.add(it.next());
				}
				
				regmodel.fireTableDataChanged();
				if(sel!=-1 && sel<alls.size())
					((DefaultListSelectionModel)jtreg.getSelectionModel()).setSelectionInterval(sel, sel);
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
		SServiceProvider.getService(getActiveComponent(), ISuperpeerRegistrySynchronizationService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(new IResultListener<ISuperpeerRegistrySynchronizationService>()
		{
			public void resultAvailable(ISuperpeerRegistrySynchronizationService sps)
			{
				tfsuperpeer.setText("self");
				buswitchpeer.setEnabled(true);
			}
			
			public void exceptionOccurred(Exception exception)
			{
				tfsuperpeer.setText("fetching ...");
				
				executeRegistryCommand(new IResultCommand<Object, IServiceRegistry>()
				{
					public Object execute(IServiceRegistry reg)
					{
						return reg.getSuperpeer(false);
					}
				}).addResultListener(new SwingResultListener<Object>(new IResultListener<Object>()
				{
					public void resultAvailable(Object result)
					{
						IComponentIdentifier cid = (IComponentIdentifier)result;
						tfsuperpeer.setText(cid.getName());
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
						buswitchpeer.setEnabled(true);
					}
				}));
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
			public IFuture<Object> execute(IInternalAccess ia)
			{
				IFuture<Object> ret = null;
				try
				{
					IServiceRegistry reg = ServiceRegistry.getRegistry(ia.getComponentIdentifier());
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
	
	class RegistryTableModel extends AbstractTableModel
	{
		protected List<IService> list;
		
		public RegistryTableModel()
		{
			this(new ArrayList<IService>());
		}
		
		public RegistryTableModel(List<IService> list)
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
			return 5;
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
			return ret;
		}	
		
		public Color getRowColor(int row) 
		{
			Color ret = Color.WHITE;
			try
			{
				IComponentIdentifier cid = (IComponentIdentifier)getValueAt(row, 3);
				
				if(!cid.getRoot().equals(getActiveComponent().getComponentIdentifier()))
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
				throw new RuntimeException(e);
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
