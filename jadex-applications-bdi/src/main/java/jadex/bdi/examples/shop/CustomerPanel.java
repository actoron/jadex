package jadex.bdi.examples.shop;

import jadex.bdiv3.runtime.IGoal;
import jadex.bdiv3.runtime.impl.BeliefAdapter;
import jadex.bdiv3x.features.IBDIXAgentFeature;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.commons.SUtil;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.gui.SGUI;
import jadex.commons.gui.future.SwingDefaultResultListener;
import jadex.commons.transformation.annotations.Classname;
import jadex.rules.eca.ChangeInfo;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

/**
 *  Customer gui that allows buying items at different shops.
 */
public class CustomerPanel extends JPanel
{
	//-------- attributes --------
	
	protected IExternalAccess agent;
	protected List shoplist = new ArrayList();
	protected JCheckBox remote;
	protected JTable shoptable;
	protected AbstractTableModel shopmodel = new ItemTableModel(shoplist);
	
	protected List invlist = new ArrayList();
	protected AbstractTableModel invmodel = new ItemTableModel(invlist);
	protected JTable invtable;
	protected Map	shops;
	
	//-------- constructors --------
	
	/**
	 *  Create a new gui.
	 */
	public CustomerPanel(final IExternalAccess agent)
	{
		this.agent = agent;
		this.shops	= new HashMap();
		
		final JComboBox shopscombo = new JComboBox();
		shopscombo.addItem("none");
		shopscombo.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent e)
			{
				if(shops.get(shopscombo.getSelectedItem()) instanceof IShopService)
				{
					refresh((IShopService)shops.get(shopscombo.getSelectedItem()));
				}
			}
		});
		
		remote = new JCheckBox("Remote");
		remote.setToolTipText("Also search remote platforms for shops.");
		final JButton searchbut = new JButton("Search");
		searchbut.addActionListener(new ActionListener()
		{
		    public void actionPerformed(ActionEvent e)
		    {
		    	searchbut.setEnabled(false);
		    	
//		    	SServiceProvider.getServices(agent.getServiceProvider(), IShop.class, remote.isSelected(), true)
				IFuture<Collection<IShopService>> ret = agent.scheduleStep(new IComponentStep<Collection<IShopService>>()
				{
					public IFuture<Collection<IShopService>> execute(IInternalAccess ia)
					{
						Future<Collection<IShopService>> ret = new Future<Collection<IShopService>>();
						if(remote.isSelected())
						{
							IFuture<Collection<IShopService>> fut = ia.getComponentFeature(IRequiredServicesFeature.class).getRequiredServices("remoteshopservices");
							fut.addResultListener(new DelegationResultListener<Collection<IShopService>>(ret));
						}
						else
						{
							IFuture<Collection<IShopService>> fut = ia.getComponentFeature(IRequiredServicesFeature.class).getRequiredServices("localshopservices");
							fut.addResultListener(new DelegationResultListener<Collection<IShopService>>(ret));
						}
						return ret;
					}
				});
				
				ret.addResultListener(new SwingDefaultResultListener<Collection<IShopService>>(CustomerPanel.this)
				{
					public void customResultAvailable(Collection<IShopService> coll)
					{
				    	searchbut.setEnabled(true);
//						System.out.println("Customer search result: "+result);
						((DefaultComboBoxModel)shopscombo.getModel()).removeAllElements();
						shops.clear();
						if(coll!=null && coll.size()>0)
						{
							for(Iterator<IShopService> it=coll.iterator(); it.hasNext(); )
							{
								IShopService	shop	= it.next();
								shops.put(shop.getName(), shop);
								((DefaultComboBoxModel)shopscombo.getModel()).addElement(shop.getName());
							}
						}
						else
						{
							((DefaultComboBoxModel)shopscombo.getModel()).addElement("none");
						}					
					}
					
					public void customExceptionOccurred(Exception exception)
					{
				    	searchbut.setEnabled(true);
						super.customExceptionOccurred(exception);
					}
				});
		    }
		});

		final NumberFormat df = NumberFormat.getInstance();
		df.setMaximumFractionDigits(2);
		df.setMinimumFractionDigits(2);

		final JTextField money = new JTextField(5);
		
		agent.scheduleStep(new IComponentStep<Void>()
		{
			@Classname("initialMoney")
			public IFuture<Void> execute(IInternalAccess ia)
			{
				IBDIXAgentFeature capa = ia.getComponentFeature(IBDIXAgentFeature.class);
				final Object mon = capa.getBeliefbase().getBelief("money").getFact();
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						money.setText(df.format(mon));
					}
				});
				return IFuture.DONE;
			}
		});
		money.setEditable(false);
		
		agent.scheduleStep(new IComponentStep<Void>()
		{
			@Classname("money")
			public IFuture<Void> execute(IInternalAccess ia)
			{
				IBDIXAgentFeature capa = ia.getComponentFeature(IBDIXAgentFeature.class);
				capa.getBeliefbase().getBelief("money").addBeliefListener(new BeliefAdapter<Object>()
				{
					public void beliefChanged(final jadex.rules.eca.ChangeInfo<Object> info) 
					{
						SwingUtilities.invokeLater(new Runnable()
						{
							public void run()
							{
								money.setText(df.format(info.getValue()));
							}
						});
					}
				});
				return IFuture.DONE;
			}
		});
		
		JPanel selpanel = new JPanel(new GridBagLayout());
		selpanel.setBorder(new TitledBorder(new EtchedBorder(), "Properties"));
		int x=0;
		int y=0;
		selpanel.add(new JLabel("Money: "), new GridBagConstraints(
			x,y,1,1,0,0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
		x++;
		selpanel.add(money, new GridBagConstraints(
			x,y,1,1,1,0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
		x++;
		selpanel.add(new JLabel("Available shops: "), new GridBagConstraints(
			x,y,1,1,0,0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
		x++;
		selpanel.add(shopscombo, new GridBagConstraints(
			x,y,1,1,0,0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(2,2,2,2),0,0));
		x++;
		selpanel.add(searchbut, new GridBagConstraints(
			x,y,1,1,0,0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
		x++;
		selpanel.add(remote, new GridBagConstraints(
			x,y,1,1,0,0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
		
		JPanel shoppanel = new JPanel(new BorderLayout());
		shoppanel.setBorder(new TitledBorder(new EtchedBorder(), "Shop Catalog"));
		shoptable = new JTable(shopmodel);
		shoptable.setPreferredScrollableViewportSize(new Dimension(600, 120));
		shoptable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		shoppanel.add(BorderLayout.CENTER, new JScrollPane(shoptable));

		JPanel invpanel = new JPanel(new BorderLayout());
		invpanel.setBorder(new TitledBorder(new EtchedBorder(), "Customer Inventory"));
		invtable = new JTable(invmodel);
		invtable.setPreferredScrollableViewportSize(new Dimension(600, 120));
		invpanel.add(BorderLayout.CENTER, new JScrollPane(invtable));

		agent.scheduleStep(new IComponentStep<Void>()
		{
			@Classname("inventory")
			public IFuture<Void> execute(IInternalAccess ia)
			{
				IBDIXAgentFeature capa = ia.getComponentFeature(IBDIXAgentFeature.class);
				try
				{
					// Hack, as long as we do not have a specific XML feature interface
					capa.getBeliefbase().getBeliefSet("inventory").addBeliefSetListener(new BeliefAdapter<Object>()
					{
						public void factRemoved(final ChangeInfo<Object> info)
						{
							SwingUtilities.invokeLater(new Runnable()
							{
								public void run()
								{
									invlist.remove(info.getValue());
									invmodel.fireTableDataChanged();
								}
							});
						}
						
						public void factChanged(final ChangeInfo<Object> info)
						{
							SwingUtilities.invokeLater(new Runnable()
							{
								public void run()
								{
									invlist.remove(info.getValue());
									invlist.add(info.getValue());
									invmodel.fireTableDataChanged();
								}
							});
						}
						
						public void factAdded(final ChangeInfo<Object> info) 
						{
							SwingUtilities.invokeLater(new Runnable()
							{
								public void run()
								{
									invlist.add(info.getValue());
									invmodel.fireTableDataChanged();
								}
							});
						}
					});
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
				return IFuture.DONE;
			}
		});
		
		JPanel butpanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
//		butpanel.setBorder(new TitledBorder(new EtchedBorder(), "Actions"));
		JButton buy = new JButton("Buy");
		final JTextField item = new JTextField(8);
		item.setEditable(false);
		butpanel.add(new JLabel("Selected item:"));
		butpanel.add(item);
		butpanel.add(buy);
		buy.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				int sel = shoptable.getSelectedRow();
				if(sel!=-1)
				{
					final String name = (String)shopmodel.getValueAt(sel, 0);
					final Double price = (Double)shopmodel.getValueAt(sel, 1);
					final IShopService shop = (IShopService)shops.get(shopscombo.getSelectedItem());
					agent.scheduleStep(new IComponentStep<Void>()
					{
						@Classname("buy")
						public IFuture<Void> execute(IInternalAccess ia)
						{
							IBDIXAgentFeature capa = ia.getComponentFeature(IBDIXAgentFeature.class);
							final IGoal buy = capa.getGoalbase().createGoal("buy");
							buy.getParameter("name").setValue(name);
							buy.getParameter("shop").setValue(shop);
							buy.getParameter("price").setValue(price);
//							buy.addGoalListener(new IGoalListener()
//							{
//								public void goalFinished(AgentEvent ae)
//								{
//									// Update number of available items
//									refresh(shop);
//									if(!buy.isSucceeded())
//									{
//										final String text = SUtil.wrapText("Item could not be bought. "+buy.getException().getMessage());
//										SwingUtilities.invokeLater(new Runnable()
//										{
//											public void run()
//											{
//												JOptionPane.showMessageDialog(SGUI.getWindowParent(CustomerPanel.this), text, "Buy problem", JOptionPane.INFORMATION_MESSAGE);
//											}
//										});
//									}
//								}
//								
//								public void goalAdded(AgentEvent ae)
//								{
//								}
//							});
							capa.getGoalbase().dispatchTopLevelGoal(buy).addResultListener(new IResultListener<Object>()
							{
								public void exceptionOccurred(Exception exception)
								{
									refresh(shop);
									printErr();
								}
								
								public void resultAvailable(Object result)
								{
									// Update number of available items
									refresh(shop);
									if(!buy.isSucceeded())
										printErr();
								}
								
								protected void printErr()
								{
									final String text = SUtil.wrapText("Item could not be bought. "+buy.getException().getMessage());
									SwingUtilities.invokeLater(new Runnable()
									{
										public void run()
										{
											JOptionPane.showMessageDialog(SGUI.getWindowParent(CustomerPanel.this), text, "Buy problem", JOptionPane.INFORMATION_MESSAGE);
										}
									});
								}
							});
							return IFuture.DONE;
						}
					});
				}
			}
		});
		
		shoptable.getSelectionModel().addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent e)
			{
				int sel = shoptable.getSelectedRow();
				if(sel!=-1)
				{
					item.setText(""+shopmodel.getValueAt(sel, 0));
				}
			}
		});
		
		setLayout(new GridBagLayout());
		x=0;
		y=0;
		add(selpanel, new GridBagConstraints(
			x,y++,1,1,0,0,GridBagConstraints.WEST,GridBagConstraints.BOTH,new Insets(2,2,2,2),0,0));
		add(shoppanel, new GridBagConstraints(
			x,y++,1,1,1,1,GridBagConstraints.WEST,GridBagConstraints.BOTH,new Insets(2,2,2,2),0,0));
		add(invpanel, new GridBagConstraints(
			x,y++,1,1,1,1,GridBagConstraints.WEST,GridBagConstraints.BOTH,new Insets(2,2,2,2),0,0));
		add(butpanel, new GridBagConstraints(
			x,y++,1,1,0,0,GridBagConstraints.WEST,GridBagConstraints.BOTH,new Insets(2,2,2,2),0,0));
		
//		refresh();
	}
	
	/**
	 *  Create a customer gui frame.
	 * /
	public static void createCustomerGui(final IExternalAccess agent)
	{
		final JFrame f = new JFrame();
		f.add(new CustomerPanel(agent));
		f.pack();
		f.setLocation(SGUI.calculateMiddlePosition(f));
		f.setVisible(true);
		f.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				agent.killAgent();
			}
		});
		agent.addAgentListener(new IAgentListener() 
		{
			public void agentTerminating(AgentEvent ae) 
			{
				f.setVisible(false);
				f.dispose();
			}
			
			public void agentTerminated(AgentEvent ae) 
			{
			}
		});
	}*/
	
	/**
	 * Method to be called when goals may have changed.
	 */
	public void refresh(IShopService shop)
	{
		if(shop!=null)
		{
			shop.getCatalog().addResultListener(new SwingDefaultResultListener(CustomerPanel.this)
			{
				public void customResultAvailable(Object result)
				{
					int sel = shoptable.getSelectedRow();
					ItemInfo[] aitems = (ItemInfo[])result;
					shoplist.clear();
					for(int i = 0; i < aitems.length; i++)
					{
						if(!shoplist.contains(aitems[i]))
						{
//							System.out.println("added: "+aitems[i]);
							shoplist.add(aitems[i]);
						}
					}
					shopmodel.fireTableDataChanged();
					if(sel!=-1 && sel<aitems.length)
						((DefaultListSelectionModel)shoptable.getSelectionModel()).setSelectionInterval(sel, sel);
				}
			});
		}
		else
		{
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					shoplist.clear();
					shopmodel.fireTableDataChanged();
				}
			});
		}
	}
		
}

class ItemTableModel extends AbstractTableModel
{
	protected List list;
	
	public ItemTableModel(List list)
	{
		this.list = list;
	}
	
	public int getRowCount()
	{
		return list.size();
	}

	public int getColumnCount()
	{
		return 3;
	}

	public String getColumnName(int column)
	{
		switch(column)
		{
			case 0:
				return "Name";
			case 1:
				return "Price";
			case 2:
				return "Quantity";
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
		ItemInfo ii = (ItemInfo)list.get(row);
		if(column == 0)
		{
			value = ii.getName();
		}
		else if(column == 1)
		{
			value = Double.valueOf(ii.getPrice());
		}
		else if(column == 2)
		{
			value = Integer.valueOf(ii.getQuantity());
		}
		return value;
	}
};
