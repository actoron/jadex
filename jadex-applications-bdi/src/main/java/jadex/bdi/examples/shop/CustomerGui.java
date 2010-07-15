package jadex.bdi.examples.shop;

import jadex.bdi.examples.booktrading.common.Gui;
import jadex.bdi.examples.booktrading.common.Order;
import jadex.bdi.runtime.AgentEvent;
import jadex.bdi.runtime.IAgentListener;
import jadex.bdi.runtime.IBDIExternalAccess;
import jadex.bdi.runtime.IBeliefSetListener;
import jadex.bdi.runtime.IEAGoal;
import jadex.commons.SGUI;
import jadex.commons.concurrent.DefaultResultListener;
import jadex.commons.concurrent.SwingDefaultResultListener;
import jadex.service.SServiceProvider;
import jadex.service.clock.IClockService;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

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
	public CustomerGui(final IBDIExternalAccess agent, IShop shop)
	{
		super(agent.getComponentName());
		this.agent = agent;
		
		JPanel itempanel = new JPanel(new BorderLayout());
		itempanel.setBorder(new TitledBorder(new EtchedBorder(), "Items"));

		table = new JTable(lim);
		table.setPreferredScrollableViewportSize(new Dimension(600, 120));

		agent.getBeliefbase().addBeliefSetListener("catalog", new IBeliefSetListener()
		{
			public void factChanged(AgentEvent ae)
			{
				refresh();
			}
			
			public void factAdded(AgentEvent ae)
			{
//						System.out.println("Added: "+ae);
				refresh();
			}
			
			public void factRemoved(AgentEvent ae)
			{
//						System.out.println("Removed: "+ae);
				refresh();
			}
		});
	}
	
	/**
	 * Method to be called when goals may have changed.
	 */
	public void refresh()
	{
		agent.getBeliefbase().getBeliefSetFacts("catalog").addResultListener(new SwingDefaultResultListener()
		{
			public void customResultAvailable(Object source, final Object result)
			{
				Order[]	aitems = (Order[])result;
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
	
	

	/**
	 *  Shows the gui, and updates it when beliefs change.
	 * /
	public Gui(final IBDIExternalAccess agent, final boolean buy)
	{
		super((buy? "Buyer: ": "Seller: ")+agent.getComponentName());
		this.agent = agent;
		
		if(buy)
		{
			itemlabel = " Books to buy ";
			goalname = "purchase_book";
			addorderlabel = "Add new purchase order";
		}
		else
		{
			itemlabel = " Books to sell ";
			goalname = "sell_book";
			addorderlabel = "Add new sell order";
		}

		
		JScrollPane scroll = new JScrollPane(table);
		itempanel.add(BorderLayout.CENTER, scroll);
		
		detailsdm = new DefaultTableModel(new String[]{"Negotiation Details"}, 0);
		JTable details = new JTable(detailsdm);
		details.setPreferredScrollableViewportSize(new Dimension(600, 120));
		
		JPanel dep = new JPanel(new BorderLayout());
		dep.add(BorderLayout.CENTER, new JScrollPane(details));
	
		JPanel south = new JPanel();
		// south.setBorder(new TitledBorder(new EtchedBorder(), " Control "));
		JButton add = new JButton("Add");
		final JButton remove = new JButton("Remove");
		final JButton edit = new JButton("Edit");
		add.setMinimumSize(remove.getMinimumSize());
		add.setPreferredSize(remove.getPreferredSize());
		edit.setMinimumSize(remove.getMinimumSize());
		edit.setPreferredSize(remove.getPreferredSize());
		south.add(add);
		south.add(remove);
		south.add(edit);
		remove.setEnabled(false);
		edit.setEnabled(false);
		
		JSplitPane splitter = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitter.add(itempanel);
		splitter.add(dep);
		splitter.setOneTouchExpandable(true);
		//splitter.setDividerLocation();
		
		getContentPane().add(BorderLayout.CENTER, splitter);
		getContentPane().add(BorderLayout.SOUTH, south);

		agent.getBeliefbase().addBeliefSetListener("orders", new IBeliefSetListener()
		{
			public void factChanged(AgentEvent ae)
			{
				refresh();
			}
			
			public void factAdded(AgentEvent ae)
			{
//						System.out.println("Added: "+ae);
				refresh();
			}
			
			public void factRemoved(AgentEvent ae)
			{
//						System.out.println("Removed: "+ae);
				refresh();
			}
		});
		
		agent.getBeliefbase().addBeliefSetListener("negotiation_reports", new IBeliefSetListener()
		{
			public void factAdded(AgentEvent ae)
			{
//						System.out.println("a fact was added");
				refreshDetails();
			}

			public void factRemoved(AgentEvent ae)
			{
//						System.out.println("a fact was removed");
				refreshDetails();
			}

			public void factChanged(AgentEvent ae)
			{
				//System.out.println("belset changed");
			}
		});
		
		agent.addAgentListener(new IAgentListener()
		{
			public void agentTerminating(AgentEvent ae)
			{
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						dispose();
					}
				});
			}
			
			public void agentTerminated(AgentEvent ae)
			{
			}
		});
		
		table.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent e)
			{
				refreshDetails();
			}
		} );
		
		final InputDialog dia = new InputDialog(buy);
		add.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				while(dia.requestInput(((IClockService)SServiceProvider.getService(agent.getServiceProvider(), IClockService.class)).getTime()))
				{
					try
					{
						String title = dia.title.getText();
						int limit = Integer.parseInt(dia.limit.getText());
						int start = Integer.parseInt(dia.start.getText());
						Date deadline = dformat.parse(dia.deadline.getText());
						final Order order = new Order(title, deadline, start, limit, buy, 
							(IClockService)SServiceProvider.getService(agent.getServiceProvider(), IClockService.class));
						
						agent.createGoal(goalname).addResultListener(new DefaultResultListener()
						{
							public void resultAvailable(Object source, Object result)
							{
								IEAGoal purchase = (IEAGoal)result;
								purchase.setParameterValue("order", order);
								agent.dispatchTopLevelGoal(purchase);
							}
						});
						orders.add(order);
						items.fireTableDataChanged();
						break;
					}
					catch(NumberFormatException e1)
					{
						JOptionPane.showMessageDialog(Gui.this, "Price limit must be integer.", "Input error", JOptionPane.ERROR_MESSAGE);
					}
					catch(ParseException e1)
					{
						JOptionPane.showMessageDialog(Gui.this, "Wrong date format, use YYYY/MM/DD hh:mm.", "Input error", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});

		table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener()
		{

			public void valueChanged(ListSelectionEvent e)
			{
				boolean selected = table.getSelectedRow() >= 0;
				remove.setEnabled(selected);
				edit.setEnabled(selected);
			}
		});

		remove.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				int row = table.getSelectedRow();
				if(row >= 0 && row < orders.size())
				{
					final Order order = (Order)orders.remove(row);
					items.fireTableRowsDeleted(row, row);
					agent.getGoalbase().getGoals(goalname).addResultListener(new DefaultResultListener()
					{
						public void resultAvailable(Object source, Object result)
						{
							IEAGoal[] goals = (IEAGoal[])result;
							dropGoal(goals, 0, order);
						}
					});
				}
			}
		});

		final InputDialog edit_dialog = new InputDialog(buy);
		edit.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				int row = table.getSelectedRow();
				if(row >= 0 && row < orders.size())
				{
					final Order order = (Order)orders.get(row);
					edit_dialog.title.setText(order.getTitle());
					edit_dialog.limit.setText(Integer.toString(order.getLimit()));
					edit_dialog.start.setText(Integer.toString(order.getStartPrice()));
					edit_dialog.deadline.setText(dformat.format(order.getDeadline()));

					while(edit_dialog.requestInput(((IClockService)SServiceProvider.getService(agent.getServiceProvider(), IClockService.class)).getTime()))
					{
						try
						{
							String title = edit_dialog.title.getText();
							int limit = Integer.parseInt(edit_dialog.limit.getText());
							int start = Integer.parseInt(edit_dialog.start.getText());
							Date deadline = dformat.parse(edit_dialog.deadline.getText());
							order.setTitle(title);
							order.setLimit(limit);
							order.setStartPrice(start);
							order.setDeadline(deadline);
							items.fireTableDataChanged();
							
							agent.getGoalbase().getGoals(goalname).addResultListener(new DefaultResultListener()
							{
								public void resultAvailable(Object source, Object result)
								{
									IEAGoal[] goals = (IEAGoal[])result;
									dropGoal(goals, 0, order);
								}
							});
							agent.createGoal(goalname).addResultListener(new DefaultResultListener()
							{
								public void resultAvailable(Object source, Object result)
								{
									IEAGoal goal = (IEAGoal)result;
									goal.setParameterValue("order", order);
									agent.dispatchTopLevelGoal(goal);
								}
							});
							break;
						}
						catch(NumberFormatException e1)
						{
							JOptionPane.showMessageDialog(Gui.this, "Price limit must be integer.", "Input error", JOptionPane.ERROR_MESSAGE);
						}
						catch(ParseException e1)
						{
							JOptionPane.showMessageDialog(Gui.this, "Wrong date format, use YYYY/MM/DD hh:mm.", "Input error", JOptionPane.ERROR_MESSAGE);
						}
					}
				}
			}
		});

		refresh();
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
	}*/
}
