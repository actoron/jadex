package jadex.bdi.examples.shop;

import jadex.bdi.runtime.IBDIExternalAccess;
import jadex.commons.SGUI;
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

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.AbstractTableModel;

/**
 * 
 */
public class CustomerGui extends JFrame
{
	protected IBDIExternalAccess agent;
	protected List items = new ArrayList();
	protected JTable table;
	
	private AbstractTableModel lim = new AbstractTableModel()
	{
		public int getRowCount()
		{
			return items.size();
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
					return "Name";
				case 1:
					return "Price";
//				case 2:
//					return "Bought";
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
			ItemInfo ii = (ItemInfo)items.get(row);
			if(column == 0)
			{
				value = ii.getName();
			}
			else if(column == 1)
			{
				value = new Double(ii.getPrice());
			}
			return value;
		}
	};
	
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
			x,y,1,1,1,0,GridBagConstraints.NORTHEAST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
		x++;
		selpanel.add(searchbut, new GridBagConstraints(
			x,y,1,1,0,0,GridBagConstraints.NORTHWEST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
		x=0; y++;
		selpanel.add(new JLabel("Available shops: "), new GridBagConstraints(
			x,y,1,1,1,0,GridBagConstraints.NORTHEAST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
		x++;
		selpanel.add(shopscombo, new GridBagConstraints(
			x,y,1,1,0,0,GridBagConstraints.NORTHWEST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
		
		JPanel itempanel = new JPanel(new BorderLayout());
		itempanel.setBorder(new TitledBorder(new EtchedBorder(), "Shop Catalog"));

		table = new JTable(lim);
		table.setPreferredScrollableViewportSize(new Dimension(600, 120));
		itempanel.add(BorderLayout.CENTER, table);

		getContentPane().add(BorderLayout.NORTH, selpanel);
		getContentPane().add(BorderLayout.CENTER, itempanel);
		
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
					for(int i = 0; i < aitems.length; i++)
					{
						if(!items.contains(aitems[i]))
						{
							items.add(aitems[i]);
						}
					}
					lim.fireTableDataChanged();
				}
			});
		}
		else
		{
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					items.clear();
					lim.fireTableDataChanged();
				}
			});
		}
	}
}
