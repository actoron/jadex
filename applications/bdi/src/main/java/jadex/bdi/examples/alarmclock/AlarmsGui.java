package jadex.bdi.examples.alarmclock;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.TableCellRenderer;

import jadex.bdiv3x.features.IBDIXAgentFeature;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.types.clock.IClockService;
import jadex.commons.beans.PropertyChangeEvent;
import jadex.commons.beans.PropertyChangeListener;
import jadex.commons.future.IFuture;
import jadex.commons.gui.future.SwingDefaultResultListener;
import jadex.commons.gui.jtable.ObjectTableModel;
import jadex.commons.transformation.annotations.Classname;

/**
 *  The alarm dialog presenting a list of alarms..
 */
public class AlarmsGui extends JFrame
{
	//-------- attributes --------

	/** The alarms table. */
	protected JTable alarms;

	/** The agent. */
	protected IExternalAccess agent;

	/** The property change listener. */
	protected PropertyChangeListener plis;

	//-------- constructors --------

	/**
	 *  Create a new test center panel.
	 */
	public AlarmsGui(final IExternalAccess agent)
	{
		super("Alarms");
		this.agent = agent;
		this.plis = new PropertyChangeListener()
		{
			public void propertyChange(PropertyChangeEvent evt)
			{
				//System.out.println("-----:"+evt);
				if(evt.getPropertyName().equals("alarmtime"))
					alarms.repaint();
			}
		};

		final ObjectTableModel tadata = new ObjectTableModel(new String[]{"Message", "Mode", "Active"})
		{
			public void setValueAt(Object value, int row, int col)
			{
				Alarm alarm = (Alarm)getObjectForRow(row);
				//System.out.println("alarm is: "+alarm);
				alarm.setActive(((Boolean)value).booleanValue());
				modifyData(value, row, col);
				fireTableCellUpdated(row, col);
			}
		};
		tadata.setColumnClass(Boolean.class, 2);
		tadata.setColumnEditable(true, 2);
		
		agent.scheduleStep(new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				IFuture<IClockService>	fut	= ia.getComponentFeature(IRequiredServicesFeature.class).getService("clockservice");
				fut.addResultListener(new SwingDefaultResultListener<IClockService>(AlarmsGui.this)
				{
					public void customResultAvailable(final IClockService cs)
					{
						alarms = new JTable(tadata)
						{
							public Component prepareRenderer(
								TableCellRenderer renderer, int row, int column)
							{
								Component c = super.prepareRenderer(renderer, row, column);
	
								if(!isRowSelected(row))
								{
									Alarm alarm = (Alarm)tadata.getObjectForRow(row);
	//								IClockService cs = (IClockService)agent.getComponentFeature(IRequiredServicesFeature.class).getService(IClockService.class);
									if(alarm.getAlarmtime(cs.getTime())<cs.getTime())
									{
										c.setBackground(new Color(255, 211, 156));
									}
									else
									{
										c.setBackground(AlarmsGui.this.alarms.getBackground());
									}
								}
	
								//if(isRowSelected(row) && isColumnSelected(column))
								//	((JComponent)c).setBorder(new LineBorder(Color.red));
	
								return c;
							}
	
						};
						alarms.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
						JScrollPane	scroll	= new JScrollPane(alarms);
						alarms.setPreferredScrollableViewportSize(new Dimension(400, 200)); // todo: hack
	
						JButton add = new JButton("Add...");
						JButton edit = new JButton("Edit...");
						JButton remove = new JButton("Remove");
						Dimension md = remove.getMinimumSize();
						Dimension pd = remove.getPreferredSize();
						add.setMinimumSize(md);
						add.setPreferredSize(pd);
						edit.setMinimumSize(md);
						edit.setPreferredSize(pd);
	
						JPanel pan = new JPanel(new GridBagLayout());
						pan.add(scroll, new GridBagConstraints(0,0,3,1,1,1,GridBagConstraints.NORTHEAST,
							GridBagConstraints.BOTH, new Insets(4,2,2,4),0,0));
						pan.add(add, new GridBagConstraints(0,1,1,1,1,0,GridBagConstraints.NORTHEAST,
							GridBagConstraints.NONE, new Insets(4,2,2,4),0,0));
						pan.add(edit, new GridBagConstraints(1,1,1,1,0,0,GridBagConstraints.NORTHEAST,
							GridBagConstraints.NONE, new Insets(4,2,2,4),0,0));
						pan.add(remove, new GridBagConstraints(2,1,1,1,0,0,GridBagConstraints.NORTHEAST,
							GridBagConstraints.NONE, new Insets(4,2,2,4),0,0));
	
						alarms.addMouseListener(new MouseAdapter()
						{
							public void mouseClicked(MouseEvent e)
							{
								if(e.getClickCount()==2)
									handleEdit(alarms, alarms.rowAtPoint(e.getPoint()), agent);
							}
						});
						add.addActionListener(new ActionListener()
						{
							public void actionPerformed(ActionEvent e)
							{
								Alarm alarm = AlarmSettingsDialog.showDialog(agent, AlarmsGui.this, null);
								if(alarm!=null && agent!=null)
									addRow(alarm, tadata.getRowCount());
							}
						});
						edit.addActionListener(new ActionListener()
						{
							public void actionPerformed(ActionEvent e)
							{
								handleEdit(alarms, alarms.getSelectedRow(), agent);
							}
						});
						remove.addActionListener(new ActionListener()
						{
							public void actionPerformed(ActionEvent e)
							{
								int sel = alarms.getSelectedRow();
								if(sel!=-1)
								{
									Alarm alarm = (Alarm)tadata.getObjectForRow(sel);
									removeRow(alarm);
								}
							}
						});
	
	//					SwingUtilities.invokeLater(new Runnable()
	//					{
	//						public void run()
	//						{
								agent.scheduleStep(new IComponentStep<Void>()
								{
									@Classname("alarms")
									public IFuture<Void> execute(IInternalAccess ia)
									{
										IBDIXAgentFeature bia = ia.getComponentFeature(IBDIXAgentFeature.class);
										final Alarm[] alarms = (Alarm[])bia.getBeliefbase().getBeliefSet("alarms").getFacts();
										SwingUtilities.invokeLater(new Runnable()
										{
											public void run()
											{
												for(int i=0; i<alarms.length; i++)
												{
													// Cannot use add row as beliefset already contains alarm.
													tadata.addRow(new Object[]{alarms[i].getMessage(), alarms[i].getMode(),
														Boolean.valueOf(alarms[i].isActive())}, alarms[i]);
													alarms[i].addPropertyChangeListener(plis);
												}
											}
										});
										return IFuture.DONE;
									}
								});
	//							agent.getBeliefbase().getBeliefSetFacts("alarms").addResultListener(new SwingDefaultResultListener(AlarmsGui.this)
	//							{
	//								public void customResultAvailable(Object source, Object result)
	//								{
	//									Alarm[] alarms = (Alarm[])result;
	//									for(int i=0; i<alarms.length; i++)
	//									{
	//										// Cannot use add row as beliefset already contains alarm.
	//										tadata.addRow(new Object[]{alarms[i].getMessage(), alarms[i].getMode(),
	//											new Boolean(alarms[i].isActive())}, alarms[i]);
	//										alarms[i].addPropertyChangeListener(plis);
	//									}
	//								}
	//							});
	//						}
	//					});
						getContentPane().add("Center", pan);
						pack();
					}
				});
				return IFuture.DONE;
			}
		});
		
		
	}

	//-------- methods --------

	/**
	 *  Handle an edit request in the table.
	 *  @param alarms The table.
	 *  @param sel The selected row.
	 *  @param agent The agent.
	 */
	protected void handleEdit(JTable alarms, int sel, IExternalAccess agent)
	{
		if(sel==-1)
			return;

		ObjectTableModel tadata = (ObjectTableModel)alarms.getModel();
		Alarm oldalarm = (Alarm)tadata.getObjectForRow(sel);
		Alarm alarm = AlarmSettingsDialog.showDialog(agent, AlarmsGui.this, oldalarm);

		if(alarm!=null)// && !oldalarm.equals(alarm))
		{
			removeRow(oldalarm);
			addRow(alarm, sel);
			alarms.getSelectionModel().setSelectionInterval(sel, sel);
		}
	}

	/**
	 *  Add a row to the table.
	 *  Updates belief set "alarms".
	 *  Adds new row to the jtable.
	 *  Adds a property change listener to the alarm.
	 *  @param alarm The alarm.
	 */
	public void addRow(final Alarm alarm, int rowcnt)
	{
//		System.out.println("Adding:"+alarm);
		alarm.addPropertyChangeListener(plis);
		agent.scheduleStep(new IComponentStep<Void>()
		{
			@Classname("addAlarm")
			public IFuture<Void> execute(IInternalAccess ia)
			{
				IBDIXAgentFeature bia = ia.getComponentFeature(IBDIXAgentFeature.class);
				bia.getBeliefbase().getBeliefSet("alarms").addFact(alarm);
				return IFuture.DONE;
			}
		});
		
//		agent.getBeliefbase().addBeliefSetFact("alarms", alarm);
		ObjectTableModel tadata = (ObjectTableModel)alarms.getModel();
		tadata.insertRow(rowcnt, new Object[]{alarm.getMessage(),
			alarm.getMode(), Boolean.valueOf(alarm.isActive())}, alarm);
	}

	/**
	 *  remove a row from the table.
	 *  Updates belief set "alarms".
	 *  Removes row from the jtable.
	 *  Removes a property change listener from the alarm.
	 *  @param alarm The alarm.
	 */
	public void removeRow(final Alarm alarm)
	{
//		System.out.println("Removing:"+alarm);
		agent.scheduleStep(new IComponentStep<Void>()
		{
			@Classname("removeAlarm")
			public IFuture<Void> execute(IInternalAccess ia)
			{
				IBDIXAgentFeature bia = ia.getComponentFeature(IBDIXAgentFeature.class);
				bia.getBeliefbase().getBeliefSet("alarms").removeFact(alarm);
				return IFuture.DONE;
			}
		});
//		agent.getBeliefbase().removeBeliefSetFact("alarms", alarm);
		ObjectTableModel tadata = (ObjectTableModel)alarms.getModel();
		tadata.removeRow(alarm);
		alarm.removePropertyChangeListener(plis);
	}

	/**
	 *  Main for starting.
	 */
	public static void main(String[] args)
	{
		/*AlarmsGui ad = new AlarmsGui(null);
		ad.pack();
		ad.setVisible(true);*/
	}

}
