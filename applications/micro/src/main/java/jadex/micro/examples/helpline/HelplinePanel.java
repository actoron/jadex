package jadex.micro.examples.helpline;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.AbstractTableModel;

import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IMonitoringComponentFeature;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.types.monitoring.IMonitoringEvent;
import jadex.bridge.service.types.monitoring.IMonitoringService.PublishEventLevel;
import jadex.commons.future.CollectionResultListener;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.IntermediateDefaultResultListener;
import jadex.commons.future.IntermediateFuture;
import jadex.commons.gui.SGUI;
import jadex.commons.gui.future.SwingDefaultResultListener;
import jadex.commons.gui.future.SwingIntermediateResultListener;
import jadex.commons.gui.jtable.DateTimeRenderer;

/**
 *  Helpline gui that allows searching for person info and adding new info.
 */
public class HelplinePanel extends JPanel
{
	//-------- attributes --------
	
	/** The external access of the agent. */
	protected IExternalAccess agent;
	
	//-------- constructors --------
	
	/**
	 *  Create a new gui.
	 */
	public HelplinePanel(final IExternalAccess agent)
	{
		this.agent = agent;
		this.setLayout(new BorderLayout());
		
		JPanel phelp = new JPanel(new BorderLayout());
		
		JPanel pget = new JPanel(new GridBagLayout());
		pget.setBorder(new TitledBorder(new EtchedBorder(), "Search Options"));
		final JTextField tfname = new JTextField("Lennie Lost");
		final JButton bsearchinfo = new JButton("Search");
		final JCheckBox cbremoteinfo = new JCheckBox("Remote");
		pget.add(new JLabel("Person's name"), new GridBagConstraints(0, 0, 1, 1, 0, 0, 
			GridBagConstraints.EAST, GridBagConstraints.VERTICAL, new Insets(1,2,1,2), 0, 0));
		pget.add(tfname, new GridBagConstraints(1, 0, 1, 1, 1, 0, 
			GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(1,2,1,2), 0, 0));
//		pget.add(new JLabel("Remote"), new GridBagConstraints(2, 0, 1, 1, 0, 0, 
//			GridBagConstraints.EAST, GridBagConstraints.VERTICAL, new Insets(1,2,1,2), 0, 0));
		pget.add(cbremoteinfo, new GridBagConstraints(2, 0, 1, 1, 0, 0, 
			GridBagConstraints.EAST, GridBagConstraints.VERTICAL, new Insets(1,2,1,2), 0, 0));
		pget.add(bsearchinfo, new GridBagConstraints(3, 0, 1, 1, 0, 0, 
			GridBagConstraints.EAST, GridBagConstraints.VERTICAL, new Insets(1,2,1,2), 0, 0));
		
		final List infolist = new ArrayList();
		final JTable infotable;
		final AbstractTableModel infomodel = new InfoTableModel(infolist);
		
		bsearchinfo.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				bsearchinfo.setEnabled(false);
				getInformation(tfname.getText(), cbremoteinfo.isSelected()).addResultListener(new SwingDefaultResultListener()
				{
					public void customResultAvailable(Object result)
					{
						infolist.clear();
						if(result!=null)
							infolist.addAll((Collection)result);
						infomodel.fireTableDataChanged();
						bsearchinfo.setEnabled(true);
					}
					
					public void customExceptionOccurred(Exception exception)
					{
						super.customExceptionOccurred(exception);
						bsearchinfo.setEnabled(true);
					}
				});
			}
		});
		
		phelp.add(pget, BorderLayout.NORTH);
		
		JPanel infopanel = new JPanel(new BorderLayout());
		infopanel.setBorder(new TitledBorder(new EtchedBorder(), "Person Information"));
		infotable = new JTable(infomodel);
		infotable.setPreferredScrollableViewportSize(new Dimension(600, 120));
		infotable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		infotable.setDefaultRenderer(Date.class, new DateTimeRenderer());
		infopanel.add(BorderLayout.CENTER, new JScrollPane(infotable));
		
		phelp.add(infopanel, BorderLayout.CENTER);
		
		JPanel padd = new JPanel(new BorderLayout());
		padd.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), "Add Information Entry "));
		JPanel pss = new JPanel(new GridBagLayout());
	
		final JComboBox cbselser = new JComboBox(); 
		final JCheckBox cbremoteser = new JCheckBox("Remote");
		final JButton bsearchser = new JButton("Search");
		JLabel selsl = new JLabel("Select service");
		pss.add(selsl, new GridBagConstraints(0, 0, 1, 1, 0, 0, 
			GridBagConstraints.EAST, GridBagConstraints.VERTICAL, new Insets(1,2,1,2), 0, 0));
		pss.add(cbselser,  new GridBagConstraints(1, 0, 1, 1, 1, 0, 
			GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(1,2,1,2), 0, 0));
		pss.add(cbremoteser, new GridBagConstraints(2, 0, 1, 1, 0, 0, 
			GridBagConstraints.EAST, GridBagConstraints.VERTICAL, new Insets(1,2,1,2), 0, 0));
		pss.add(bsearchser, new GridBagConstraints(3, 0, 1, 1, 0, 0, 
			GridBagConstraints.EAST, GridBagConstraints.VERTICAL, new Insets(1,2,1,2), 0, 0));;
		
		bsearchser.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				refreshServicesCombo(cbselser, cbremoteser.isSelected());
			}
		});
		
		padd.add(pss, BorderLayout.NORTH);
		
		final JPanel pinfoentry = new JPanel(new GridBagLayout());
		JLabel lname = new JLabel("Name");
		lname.setPreferredSize(selsl.getPreferredSize());
		pinfoentry.add(lname, new GridBagConstraints(0, 0, 1, 1, 0, 0, 
			GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(1,2,1,2), 0, 0));
		final JTextField tfpname = new JTextField("Lennie Lost");
		pinfoentry.add(tfpname, new GridBagConstraints(1, 0, 1, 1, 1, 0, 
			GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(1,2,1,2), 0, 0));
		pinfoentry.add(new JLabel("Information"), new GridBagConstraints(2, 0, 1, 1, 0, 0, 
			GridBagConstraints.EAST, GridBagConstraints.VERTICAL, new Insets(1,12,1,2), 0, 0));
		final JTextField tfpinfo = new JTextField(8);
		pinfoentry.add(tfpinfo, new GridBagConstraints(3, 0, 1, 1, 3, 0, 
			GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(1,2,1,2), 0, 0));
		final JButton badd = new JButton("Add");
		badd.setPreferredSize(bsearchser.getPreferredSize());
		badd.setEnabled(false);
		pinfoentry.add(badd, new GridBagConstraints(4, 0, 1, 1, 0, 0, 
			GridBagConstraints.EAST, GridBagConstraints.VERTICAL, new Insets(1,2,1,2), 0, 0));
		
		badd.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				IHelpline hl = (IHelpline)cbselser.getSelectedItem();
				if(hl!=null)
				{
					hl.addInformation(tfpname.getText(), tfpinfo.getText());
				}
			}
		});
		
		cbselser.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				IHelpline hl = (IHelpline)cbselser.getSelectedItem();
				if(hl!=null)
				{
					badd.setEnabled(true);
				}
				else
				{
					badd.setEnabled(false);
				}
			}
		});
		
		padd.add(pinfoentry, BorderLayout.CENTER);
		
		phelp.add(padd, BorderLayout.SOUTH);
		
		this.add(phelp, BorderLayout.CENTER);
		
		refreshServicesCombo(cbselser, cbremoteser.isSelected());
	}
	
	/**
	 *  Refresh the service combo box.
	 */
	protected void refreshServicesCombo(final JComboBox selcb, final boolean remote)
	{
//		SServiceProvider.getServices(agent.getServiceProvider(), IHelpline.class, remote, true)
//			.addResultListener(new SwingDefaultResultListener(HelplinePanel.this)
//			(agent.getServiceProvider(), IHelpline.class, remote, true)
		agent.scheduleStep(new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				ia.getFeature(IRequiredServicesFeature.class).getServices(
					remote ? "remotehelplineservices" : "localhelplineservices").addResultListener(new SwingDefaultResultListener(HelplinePanel.this) 
				{
					public void customResultAvailable(Object result) 
					{
						Collection newservices = (Collection)result;
						
						selcb.removeAllItems();
						if(newservices!=null)
						{
							for(Iterator it=newservices.iterator(); it.hasNext(); )
							{
								selcb.addItem(it.next());
							}
						}
					}
				});
				return IFuture.DONE;
			}
		});
	}
	
	/**
	 *  Get all information about a person.
	 *  @param name The person's name.
	 *  @return Future that contains the information.
	 */
	public IIntermediateFuture<InformationEntry> getInformation(final String name, final boolean remote)
	{
//		SServiceProvider.getServices(agent.getServiceProvider(), IHelpline.class, remote, true)
		final IntermediateFuture<InformationEntry> ret = new IntermediateFuture<InformationEntry>();
		
		IIntermediateFuture<IHelpline> fut = (IIntermediateFuture<IHelpline>)agent.scheduleStep(new IComponentStep<Collection<IHelpline>>()
		{
			public IIntermediateFuture<IHelpline> execute(IInternalAccess ia)
			{
				IIntermediateFuture<IHelpline> ret;
				if(remote)
				{
					ret	= ia.getFeature(IRequiredServicesFeature.class).getServices("remotehelplineservices");
				}
				else
				{
					ret	= ia.getFeature(IRequiredServicesFeature.class).getServices("localhelplineservices");
				}
				return ret;
			}
		});
		
		fut.addResultListener(new IResultListener()
		{
			public void resultAvailable(Object result)
			{
				if(result!=null)
				{
					Collection coll = (Collection)result;
					CollectionResultListener crl = new CollectionResultListener(
						coll.size(), true, new DefaultResultListener()
					{
						public void resultAvailable(Object result)
						{
							if(result!=null)
							{
								Collection tmp = (Collection)result;
								Iterator it = tmp.iterator();
								List all = new ArrayList();
								for(; it.hasNext(); )
								{
									Collection part = (Collection)it.next();
									for(Iterator it2=part.iterator(); it2.hasNext(); )
									{
										Object next = it2.next();
										if(next instanceof InformationEntry && !all.contains(next))
											all.add(next);
									}
								}
								// Sorts the list by date.
								Collections.sort(all);
								
								ret.setResult(all);
							}
							else
							{
								ret.setResult(null);
							}
						}
					});
					for(Iterator it=coll.iterator(); it.hasNext(); )
					{
						IHelpline hl = (IHelpline)it.next();
						IFuture res = hl.getInformation(name);
//						res.addResultListener(new DefaultResultListener()
//						{
//							public void resultAvailable(Object result)
//							{
//								System.out.println("result"+result);
//							}
//						});
						res.addResultListener(crl);
					}
				}
			}
			
			public void exceptionOccurred(Exception exception)
			{
				ret.setException(exception);
			}
		});
			
		return ret;
	}
	
	/**
	 *  Create a customer gui frame.
	 */
	public static void createHelplineGui(final IExternalAccess agent)
	{
		final JFrame f = new JFrame();
		f.add(new HelplinePanel(agent));
		f.pack();
		f.setLocation(SGUI.calculateMiddlePosition(f));
		f.setVisible(true);
		f.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				agent.killComponent();
			}
		});
		
		// Dispose frame on exception.
		IResultListener<Void>	dislis	= new IResultListener<Void>()
		{
			public void exceptionOccurred(Exception exception)
			{
				f.dispose();
			}
			public void resultAvailable(Void result)
			{
			}
		};
		agent.scheduleStep(new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
//				ia.addComponentListener(new TerminationAdapter()
//				{
//					public void componentTerminated()
//					{
//						SwingUtilities.invokeLater(new Runnable()
//						{
//							public void run()
//							{
//								f.dispose();	
//							}
//						});
//					}
//				});
				
				ia.getFeature(IMonitoringComponentFeature.class).subscribeToEvents(IMonitoringEvent.TERMINATION_FILTER, false, PublishEventLevel.COARSE)
					.addResultListener(new SwingIntermediateResultListener<IMonitoringEvent>(new IntermediateDefaultResultListener<IMonitoringEvent>()
				{
					public void intermediateResultAvailable(IMonitoringEvent result)
					{
						f.dispose();
					}
				}));
				
				return IFuture.DONE;
			}
		}).addResultListener(dislis);
	}
}

class InfoTableModel extends AbstractTableModel
{
	protected List list;
	
	public InfoTableModel(List list)
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
				return "Date and Time";
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
			value = new Date(ie.getDate());
		}
		else if(column == 1)
		{
			value = ie.getInformation();
		}
		
		return value;
	}
	
	public Class getColumnClass(int column)
	{
		Class ret = Object.class;
		if(column == 0)
		{
			ret = Date.class;
		}
		else if(column == 1)
		{
			ret = String.class;
		}
		return ret;
	}
};
