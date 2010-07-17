package jadex.bdi.examples.shop;

import jadex.bdi.runtime.AgentEvent;
import jadex.bdi.runtime.IBDIExternalAccess;
import jadex.bdi.runtime.IBeliefSetListener;
import jadex.commons.SGUI;
import jadex.commons.Tuple;
import jadex.commons.concurrent.SwingDefaultResultListener;
import jadex.service.SServiceProvider;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
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
 * 
 */
public class CustomerGui extends JFrame
{
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
		shopscombo.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent e)
			{
				refresh((IShop)shopscombo.getSelectedItem());
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
						if(shops!=null)
						{
							((DefaultComboBoxModel)shopscombo.getModel()).removeAllElements();
							for(Iterator it=shops.iterator(); it.hasNext(); )
							{
								((DefaultComboBoxModel)shopscombo.getModel()).addElement(it.next());
							}
						}
						
					}
				});
		    }
		});
		
		JPanel selpanel = new JPanel(new GridBagLayout());
		int x=0;
		int y=0;
		selpanel.add(new JLabel("Search for shops: "), new GridBagConstraints(
			x,y,1,1,1,0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
		x++;
		selpanel.add(searchbut, new GridBagConstraints(
			x,y,1,1,0,0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
		x=0; y++;
		selpanel.add(new JLabel("Available shops: "), new GridBagConstraints(
			x,y,1,1,1,0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
		x++;
		selpanel.add(shopscombo, new GridBagConstraints(
			x,y,1,1,0,0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
		
		JPanel shoppanel = new JPanel(new BorderLayout());
		shoppanel.setBorder(new TitledBorder(new EtchedBorder(), "Shop Catalog"));
		shoptable = new JTable(shopmodel);
		shoptable.setPreferredScrollableViewportSize(new Dimension(600, 120));
		shoptable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		shoppanel.add(BorderLayout.CENTER, shoptable);

		JPanel invpanel = new JPanel(new BorderLayout());
		invpanel.setBorder(new TitledBorder(new EtchedBorder(), "Customer Inventory"));
		invtable = new JTable(invmodel);
		invtable.setPreferredScrollableViewportSize(new Dimension(600, 120));
		invpanel.add(BorderLayout.CENTER, invtable);
		
		agent.getBeliefbase().addBeliefSetListener("inventory", new IBeliefSetListener()
		{
			public void factRemoved(final AgentEvent ae)
			{
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						invlist.remove(ae.getValue());
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
					}
				});
			}
		});
		
		JPanel butpanel = new JPanel();
		JButton buy = new JButton("Buy");
		final JTextField item = new JTextField(15);
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
					String name = (String)shopmodel.getValueAt(sel, 0);
					double price = ((Double)shopmodel.getValueAt(sel, 1)).doubleValue();
					IShop shop = (IShop)shopscombo.getSelectedItem();
					System.out.println("buying: "+name+" at: "+shop.getName());
					shop.buyItem(name, price).addResultListener(new SwingDefaultResultListener()
					{
						public void customResultAvailable(Object source, Object result)
						{
							ItemInfo bought = (ItemInfo)result;
							System.out.println("bought: "+bought);
							invlist.add(bought);
							invmodel.fireTableDataChanged();
						}
					});
				}
			}
		});
		invpanel.add(BorderLayout.SOUTH, butpanel);
		
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
		
		getContentPane().add(BorderLayout.NORTH, selpanel);
		getContentPane().add(BorderLayout.CENTER, shoppanel);
		getContentPane().add(BorderLayout.SOUTH, invpanel);
		
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
