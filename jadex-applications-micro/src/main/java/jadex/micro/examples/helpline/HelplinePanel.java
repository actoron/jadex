package jadex.micro.examples.helpline;

import jadex.commons.concurrent.SwingDefaultResultListener;
import jadex.commons.service.SServiceProvider;
import jadex.micro.IMicroExternalAccess;

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
public class HelplinePanel extends JPanel
{
	//-------- attributes --------
	
	protected IMicroExternalAccess agent;
	protected List shoplist = new ArrayList();
	protected JCheckBox remote;
	protected JTable infotable;
	protected AbstractTableModel infomodel = new ItemTableModel(shoplist);
	
	protected List invlist = new ArrayList();
	protected AbstractTableModel invmodel = new ItemTableModel(invlist);
	protected JTable invtable;
	
	//-------- constructors --------
	
	/**
	 *  Create a new gui.
	 */
	public HelplinePanel(final IMicroExternalAccess agent)
	{
		this.agent = agent;
		
		final JComboBox helplinescombo = new JComboBox();
		helplinescombo.addItem("none");
		helplinescombo.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent e)
			{
				if(helplinescombo.getSelectedItem() instanceof IHelpline)
					refresh((IHelpline)helplinescombo.getSelectedItem());
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
		    	SServiceProvider.getServices(agent.getServiceProvider(), IHelpline.class, remote.isSelected(), true)
					.addResultListener(new SwingDefaultResultListener(HelplinePanel.this)
				{
					public void customResultAvailable(Object source, Object result)
					{
				    	searchbut.setEnabled(true);
						System.out.println("Helpline search result: "+result);
						Collection coll = (Collection)result;
						((DefaultComboBoxModel)helplinescombo.getModel()).removeAllElements();
						if(coll!=null && coll.size()>0)
						{
							for(Iterator it=coll.iterator(); it.hasNext(); )
							{
								IHelpline hl = (IHelpline)it.next();
								((DefaultComboBoxModel)helplinescombo.getModel()).addElement(hl);
							}
						}
						else
						{
							((DefaultComboBoxModel)helplinescombo.getModel()).addElement("none");
						}					
					}
					
					public void customExceptionOccurred(Object source, Exception exception)
					{
				    	searchbut.setEnabled(true);
						super.customExceptionOccurred(source, exception);
					}
				});
		    }
		});
		
		JPanel selpanel = new JPanel(new GridBagLayout());
		selpanel.setBorder(new TitledBorder(new EtchedBorder(), "Properties"));
		int x=0;
		int y=0;
		x++;
		selpanel.add(new JLabel("Available helplines: "), new GridBagConstraints(
			x,y,1,1,0,0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
		x++;
		selpanel.add(helplinescombo, new GridBagConstraints(
			x,y,1,1,0,0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(2,2,2,2),0,0));
		x++;
		selpanel.add(searchbut, new GridBagConstraints(
			x,y,1,1,0,0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
		x++;
		selpanel.add(remote, new GridBagConstraints(
			x,y,1,1,0,0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
		
		JPanel shoppanel = new JPanel(new BorderLayout());
		shoppanel.setBorder(new TitledBorder(new EtchedBorder(), "Shop Catalog"));
		infotable = new JTable(infomodel);
		infotable.setPreferredScrollableViewportSize(new Dimension(600, 120));
		infotable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		shoppanel.add(BorderLayout.CENTER, new JScrollPane(infotable));

		JPanel invpanel = new JPanel(new BorderLayout());
		invpanel.setBorder(new TitledBorder(new EtchedBorder(), "Customer Inventory"));
		invtable = new JTable(invmodel);
		invtable.setPreferredScrollableViewportSize(new Dimension(600, 120));
		invpanel.add(BorderLayout.CENTER, new JScrollPane(invtable));

		JPanel butpanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
//		butpanel.setBorder(new TitledBorder(new EtchedBorder(), "Actions"));
		JButton add = new JButton("Add");
		final JTextField item = new JTextField(8);
		item.setEditable(false);
		butpanel.add(new JLabel("Selected item:"));
		butpanel.add(item);
		butpanel.add(add);
		add.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
			}
		});
		
		infotable.getSelectionModel().addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent e)
			{
				int sel = infotable.getSelectedRow();
				if(sel!=-1)
				{
					item.setText(""+infomodel.getValueAt(sel, 0));
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
	public static void createCustomerGui(final IBDIExternalAccess agent)
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
	public void refresh(IHelpline hl)
	{
		if(hl!=null)
		{
//			hl.get.addResultListener(new SwingDefaultResultListener(HelplinePanel.this)
//			{
//				public void customResultAvailable(Object source, Object result)
//				{
//					int sel = infotable.getSelectedRow();
//					ItemInfo[] aitems = (ItemInfo[])result;
//					shoplist.clear();
//					for(int i = 0; i < aitems.length; i++)
//					{
//						if(!shoplist.contains(aitems[i]))
//						{
////							System.out.println("added: "+aitems[i]);
//							shoplist.add(aitems[i]);
//						}
//					}
//					infomodel.fireTableDataChanged();
//					if(sel!=-1 && sel<aitems.length)
//						((DefaultListSelectionModel)infotable.getSelectionModel()).setSelectionInterval(sel, sel);
//				}
//			});
		}
		else
		{
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					shoplist.clear();
					infomodel.fireTableDataChanged();
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
		return 2;
	}

	public String getColumnName(int column)
	{
		switch(column)
		{
			case 0:
				return "Name";
			case 1:
				return "Information";
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
		InformationEntry ie = (InformationEntry)list.get(row);
		if(column == 0)
		{
			value = ie.getName();
		}
		else if(column == 1)
		{
			value = ie.getInformation();
		}
		return value;
	}
};
