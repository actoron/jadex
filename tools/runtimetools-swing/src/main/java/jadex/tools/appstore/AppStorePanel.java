package jadex.tools.appstore;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import jadex.base.gui.componentviewer.IAbstractViewerPanel;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.IService;
import jadex.bridge.service.ServiceScope;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.appstore.AppMetaInfo;
import jadex.bridge.service.types.appstore.IAppGui;
import jadex.bridge.service.types.appstore.IAppProviderService;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.gui.SGUI;
import jadex.commons.gui.future.SwingDefaultResultListener;
import jadex.commons.gui.future.SwingIntermediateExceptionDelegationResultListener;
import jadex.commons.gui.future.SwingResultListener;

/**
 * 
 */
public class AppStorePanel extends JPanel
{
	/** The external access. */
	protected IExternalAccess access;
	
	/** The app table. */
	protected JTable apptable;
	
	/** The current apps. */
	protected Map<IAppProviderService<?>, AppMetaInfo> apps;
	
	/**
	 *  Create a new appstore panel.
	 */
	public AppStorePanel(IExternalAccess access)
	{
		this.access = access;
		this.apps = new HashMap<IAppProviderService<?>, AppMetaInfo>();
		this.setLayout(new BorderLayout());
		
		apptable	= new JTable(new AppTableModel());
//		apptable.setTableHeader(new ResizeableTableHeader(apptable.getColumnModel()));
		apptable.setRowHeight(40);
		apptable.addMouseListener(new AppMouseAdapter(apptable));
		
		JScrollPane apppan = new JScrollPane(apptable);
		
		DefaultTableCellRenderer apprend = new DefaultTableCellRenderer()
		{
			public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean focus, int row, int column)
			{
				Component ret = this;
				
				super.getTableCellRendererComponent(table, value, selected, focus, row, column);
				Object[] val = (Object[])value;
				AppMetaInfo app = (AppMetaInfo)val[1];
				if(column==0)
				{
					this.setText("");
					this.setToolTipText("");
					this.setIcon(app.getImage()==null? null: new ImageIcon(app.getImage()));
				}
				else if(column==1)
				{
					this.setText(app.getName());
					this.setToolTipText(app.getDescription());
					this.setIcon(null);
				}
				else if(column==2)
				{
					this.setText(app.getVersion());
					this.setToolTipText("");
					this.setIcon(null);
				}
				else if(column==3)
				{
					this.setText(app.getProvider());
					this.setToolTipText("");
					this.setIcon(null);
				}
				
				return ret;
			}
		};
		
		apptable.getColumnModel().getColumn(0).setCellRenderer(apprend);
		apptable.getColumnModel().getColumn(1).setCellRenderer(apprend);
		apptable.getColumnModel().getColumn(2).setCellRenderer(apprend);
		apptable.getColumnModel().getColumn(3).setCellRenderer(apprend);
		
		JButton buref = new JButton("Refresh");
		buref.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				refreshGui();
			}
		});
		
		JPanel psou = new JPanel(new FlowLayout());
		psou.add(buref);
		
		this.add(apppan, BorderLayout.CENTER);
		this.add(psou, BorderLayout.SOUTH);
	}
	
	/**
	 * 
	 */
	protected IFuture<Void> searchAppProviders()
	{
		final Future<Void> ret = new Future<Void>();
		
		apps.clear();
		IIntermediateFuture<IAppProviderService> fut = access.searchServices( new ServiceQuery<>(IAppProviderService.class, ServiceScope.GLOBAL));
		fut.addResultListener(new SwingIntermediateExceptionDelegationResultListener<IAppProviderService, Void>(ret)
		{
			protected boolean fin = false;
			protected int cnt = 0;
			
			public void customIntermediateResultAvailable(final IAppProviderService ser)
			{
				cnt++;
				ser.getAppMetaInfo().addResultListener(new SwingResultListener<AppMetaInfo>(new IResultListener<AppMetaInfo>()
				{
					public void resultAvailable(AppMetaInfo ami)
					{
//						System.out.println("found: "+ami);
						if(!apps.containsKey(ser))
						{
							// todo: save according to id
							apps.put(ser, ami);
							refreshAppTable();
						}
						
						if(--cnt==0 && fin)
							ret.setResult(null);
					}
					
					public void exceptionOccurred(Exception exception)
					{
						if(--cnt==0 && fin)
							ret.setResult(null);
					}
				}));
			}
			
			public void customFinished()
			{
				if(cnt==0)
				{
					ret.setResult(null);
				}
				else
				{
					fin = true;
				}
			}
			
			public void customResultAvailable(Collection<IAppProviderService> results)
			{
				if(results!=null)
				{
					for(IAppProviderService aps: results)
					{
						intermediateResultAvailable(aps);
					}
				}
				finished();
			}
		});
		
		return ret;
	}
	
	/**
	 * 
	 */
	public void refreshGui()
	{
		searchAppProviders().addResultListener(new SwingDefaultResultListener<Void>()
		{
			public void customResultAvailable(Void result)
			{
				refreshAppTable();
			}
		});
	}
	
	/**
	 * 
	 */
	protected void refreshAppTable()
	{
		// System.out.println("ref table");
		((DefaultTableModel)apptable.getModel()).fireTableDataChanged();
		apptable.getParent().invalidate();
		apptable.getParent().doLayout();
		apptable.repaint();
	}
	
	/**
	 *  Table model for list of users.
	 */
	public class AppTableModel	extends DefaultTableModel
	{
		protected String[]	columns	= new String[]{"Image", "Name", "Version", "Provider"};
		
		public int getColumnCount()
		{
			return columns.length;
		}
		
		public String getColumnName(int i)
		{
			return columns[i];
		}
		
		public Class<?> getColumnClass(int i)
		{
			return String.class;
		}
		
		public int getRowCount()
		{
//			System.out.println(apps.size());
			return apps.size();
		}
		
		public Object getValueAt(int row, int column)
		{
			Object key = apps.keySet().toArray()[row];
			return new Object[]{key, apps.get(key)};
		}
		
		public boolean isCellEditable(int row, int column)
		{
			return false;
		}
		
		public void setValueAt(Object val, int row, int column)
		{
		}
		
		public void addTableModelListener(TableModelListener l)
		{
		}
		public void removeTableModelListener(TableModelListener l)
		{
		}
	}
	
	/**
	 *  File transfer mouse adapter.
	 */
	public class AppMouseAdapter extends MouseAdapter
	{
		protected JTable table;
		
		public AppMouseAdapter(JTable table)
		{
			this.table = table;
		}
		
		public void mousePressed(MouseEvent e) 
		{
			trigger(e);
		}

		public void mouseReleased(MouseEvent e) 
		{
			trigger(e);
		}
		
		protected void trigger(MouseEvent e)
		{
			if(e.isPopupTrigger()) 
			{
				int row = table.rowAtPoint(e.getPoint());
				if(row!=-1)
				{
					Object[] vals = (Object[])((AppTableModel)table.getModel()).getValueAt(row, 0);
					createMenu((IAppProviderService<?>)vals[0]).show(e.getComponent(), e.getX(), e.getY());
				}
			}
		}
		
		protected JPopupMenu createMenu(final IAppProviderService<?> aps)
		{
			final JPopupMenu menu = new JPopupMenu();
			
//			menu.add(new JMenuItem("test"));
			
			IFuture<Object> fut = (IFuture<Object>)aps.getApplication();
			fut.addResultListener(new SwingDefaultResultListener<Object>()
			{
				public void customResultAvailable(final Object result)
				{
					if(result!=null)
					{
						JMenuItem mi = new JMenuItem("Start");
						mi.addActionListener(new ActionListener()
						{
							public void actionPerformed(ActionEvent e)
							{
								if(result instanceof IService)
								{
									final IService appser = (IService)result;
									Map<String, Object> props = appser.getPropertyMap();
									Class<?> guiclass = (Class<?>)props.get(IAbstractViewerPanel.PROPERTY_VIEWERCLASS);
									JFrame fr;
									try
									{
										final IAppGui gui = (IAppGui)guiclass.newInstance();
										if(gui instanceof JFrame)
										{
											fr = (JFrame)gui;
											fr.setLocation(SGUI.calculateMiddlePosition(fr));
											fr.pack();
											fr.setVisible(true);
										}
										else if(gui instanceof JComponent)
										{	
											fr = new JFrame();
											fr.setLayout(new BorderLayout());
											fr.add((JComponent)gui, BorderLayout.CENTER);
//											fr.add(new JButton("a"), BorderLayout.SOUTH);
											fr.setLocation(SGUI.calculateMiddlePosition(fr));
											fr.pack();
//											fr.setSize(400, 200);
											fr.setVisible(true);
										}
										else
										{
											System.out.println("Unknown gui type: "+gui);
										}
										gui.init(access, appser).addResultListener(new SwingDefaultResultListener<Void>()
										{
											public void customResultAvailable(Void result)
											{
												
											}
										});
									}
									catch(Exception ex)
									{
										ex.printStackTrace();
									}
								}
							}
						});
						menu.add(mi);
						menu.invalidate();
						menu.doLayout();
						menu.pack();
						menu.repaint();
					}
				}
			});
			
			return menu;
		}
	};
}
