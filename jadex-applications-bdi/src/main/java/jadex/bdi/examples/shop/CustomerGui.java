package jadex.bdi.examples.shop;

import jadex.bdi.runtime.AgentEvent;
import jadex.bdi.runtime.IAgentListener;
import jadex.bdi.runtime.IBDIExternalAccess;
import jadex.bdi.runtime.IBeliefListener;
import jadex.bdi.runtime.IBeliefSetListener;
import jadex.bdi.runtime.IEAGoal;
import jadex.commons.SGUI;
import jadex.commons.concurrent.SwingDefaultResultListener;
import jadex.service.SServiceProvider;

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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
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
public class CustomerGui extends JFrame
{
	//-------- attributes --------
	
	protected IBDIExternalAccess agent;
	protected List shoplist = new ArrayList();
	protected JTable shoptable;
	protected AbstractTableModel shopmodel = new ItemTableModel(shoplist);
	
	protected List invlist = new ArrayList();
	protected AbstractTableModel invmodel = new ItemTableModel(invlist);
	protected JTable invtable;
	
	//-------- constructors --------
	
	/**
	 *  Create a new gui.
	 */
	public CustomerGui(final IBDIExternalAccess agent)
	{
		super(agent.getComponentName());
		this.agent = agent;
		
		final JComboBox shopscombo = new JComboBox();
		shopscombo.addItem("none");
		shopscombo.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent e)
			{
				if(shopscombo.getSelectedItem() instanceof IShop)
				{
					refresh((IShop)shopscombo.getSelectedItem());
				}
			}
		});
		
		JButton searchbut = new JButton("Search");
		searchbut.addActionListener(new ActionListener()
		{
		    public void actionPerformed(ActionEvent e)
		    {
		    	SServiceProvider.getServices(agent.getServiceProvider(), IShop.class)
					.addResultListener(new SwingDefaultResultListener()
				{
					public void customResultAvailable(Object source, Object result)
					{
						Collection shops = (Collection)result;
						((DefaultComboBoxModel)shopscombo.getModel()).removeAllElements();
						if(shops!=null && shops.size()>0)
						{
							for(Iterator it=shops.iterator(); it.hasNext(); )
							{
								((DefaultComboBoxModel)shopscombo.getModel()).addElement(it.next());
							}
						}
						else
						{
							((DefaultComboBoxModel)shopscombo.getModel()).addElement("none");
						}						
					}
				});
		    }
		});

		final NumberFormat df = NumberFormat.getInstance();
		df.setMaximumFractionDigits(2);
		df.setMinimumFractionDigits(2);

		final JTextField money = new JTextField(5);
		agent.getBeliefbase().getBeliefFact("money")
			.addResultListener(new SwingDefaultResultListener()
		{
			public void customResultAvailable(Object source, Object result)
			{
				money.setText(df.format(result));
			}
		});
		money.setEditable(false);
		agent.getBeliefbase().addBeliefListener("money", new IBeliefListener()
		{
			public void beliefChanged(final AgentEvent ae)
			{
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						money.setText(df.format(ae.getValue()));
					}
				});
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

		agent.getBeliefbase().addBeliefSetListener("inventory", new IBeliefSetListener()
		{
			public void factRemoved(final AgentEvent ae)
			{
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						invlist.remove(ae.getValue());
						invmodel.fireTableDataChanged();
					}
				});
			}
			
			public void factChanged(final AgentEvent ae)
			{
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						invlist.remove(ae.getValue());
						invlist.add(ae.getValue());
						invmodel.fireTableDataChanged();
					}
				});
			}
			
			public void factAdded(final AgentEvent ae)
			{
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						invlist.add(ae.getValue());
						invmodel.fireTableDataChanged();
					}
				});
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
					final IShop shop = (IShop)shopscombo.getSelectedItem();
//					System.out.println("buying: "+name+" at: "+shop.getName());
					agent.createGoal("buy").addResultListener(new SwingDefaultResultListener()
					{
						public void customResultAvailable(Object source, Object result)
						{
							IEAGoal buy = (IEAGoal)result;
							buy.setParameterValue("name", name);
							buy.setParameterValue("shop", shop);
							buy.setParameterValue("price", price);
							agent.dispatchTopLevelGoalAndWait(buy).addResultListener(new SwingDefaultResultListener(CustomerGui.this)
							{
								public void customResultAvailable(Object source, Object result)
								{
									// Update number of available items
									refresh(shop);
								}
							});
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
		
		getContentPane().setLayout(new GridBagLayout());
		x=0;
		y=0;
		getContentPane().add(selpanel, new GridBagConstraints(
			x,y++,1,1,0,0,GridBagConstraints.WEST,GridBagConstraints.BOTH,new Insets(2,2,2,2),0,0));
		getContentPane().add(shoppanel, new GridBagConstraints(
			x,y++,1,1,1,1,GridBagConstraints.WEST,GridBagConstraints.BOTH,new Insets(2,2,2,2),0,0));
		getContentPane().add(invpanel, new GridBagConstraints(
			x,y++,1,1,1,1,GridBagConstraints.WEST,GridBagConstraints.BOTH,new Insets(2,2,2,2),0,0));
		getContentPane().add(butpanel, new GridBagConstraints(
			x,y++,1,1,0,0,GridBagConstraints.WEST,GridBagConstraints.BOTH,new Insets(2,2,2,2),0,0));
		
//		refresh();
		pack();
		setLocation(SGUI.calculateMiddlePosition(this));
		setVisible(true);
		addWindowListener(new WindowAdapter()
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
				setVisible(false);
				dispose();
			}
			
			public void agentTerminated(AgentEvent ae) 
			{
			}
		});
	}
	
	/**
	 * Method to be called when goals may have changed.
	 */
	public void refresh(IShop shop)
	{
		if(shop!=null)
		{
			shop.getCatalog().addResultListener(new SwingDefaultResultListener()
			{
				public void customResultAvailable(Object source, Object result)
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
			value = new Double(ii.getPrice());
		}
		else if(column == 2)
		{
			value = new Integer(ii.getQuantity());
		}
		return value;
	}
};
