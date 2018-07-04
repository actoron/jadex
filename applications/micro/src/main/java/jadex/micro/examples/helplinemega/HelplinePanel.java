package jadex.micro.examples.helplinemega;

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
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.swing.JButton;
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

import jadex.bridge.BasicComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IMonitoringComponentFeature;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceNotFoundException;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.cms.IComponentManagementService.CMSCreatedEvent;
import jadex.bridge.service.types.cms.IComponentManagementService.CMSStatusEvent;
import jadex.bridge.service.types.monitoring.IMonitoringEvent;
import jadex.bridge.service.types.monitoring.IMonitoringService.PublishEventLevel;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.IntermediateDefaultResultListener;
import jadex.commons.gui.SGUI;
import jadex.commons.gui.future.SwingDefaultResultListener;
import jadex.commons.gui.future.SwingIntermediateDefaultResultListener;
import jadex.commons.gui.future.SwingIntermediateResultListener;
import jadex.commons.gui.jtable.DateTimeRenderer;

/**
 *  Helpline gui that allows searching for person info and adding new info.
 */
public class HelplinePanel extends JPanel
{
	// to make eclipse happy...
	private static final long serialVersionUID = 3159064974941120503L;
	
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
		final JButton bshowinfo = new JButton("Show local info");
		final JButton bsearchinfo = new JButton("Search remote info");
		pget.add(new JLabel("Person's name"), new GridBagConstraints(0, 0, 1, 1, 0, 0, 
			GridBagConstraints.EAST, GridBagConstraints.VERTICAL, new Insets(1,2,1,2), 0, 0));
		pget.add(tfname, new GridBagConstraints(1, 0, 1, 1, 1, 0, 
			GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(1,2,1,2), 0, 0));
//		pget.add(new JLabel("Remote"), new GridBagConstraints(2, 0, 1, 1, 0, 0, 
//			GridBagConstraints.EAST, GridBagConstraints.VERTICAL, new Insets(1,2,1,2), 0, 0));
		pget.add(bshowinfo, new GridBagConstraints(2, 0, 1, 1, 0, 0, 
			GridBagConstraints.EAST, GridBagConstraints.VERTICAL, new Insets(1,2,1,2), 0, 0));
		pget.add(bsearchinfo, new GridBagConstraints(3, 0, 1, 1, 0, 0, 
			GridBagConstraints.EAST, GridBagConstraints.VERTICAL, new Insets(1,2,1,2), 0, 0));
		
		final List<InformationEntry> infolist = new ArrayList<InformationEntry>();
		final JTable infotable;
		final AbstractTableModel infomodel = new InfoTableModel(infolist);
		
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
	
		padd.add(pss, BorderLayout.NORTH);
		
		final JPanel pinfoentry = new JPanel(new GridBagLayout());
		JLabel lname = new JLabel("Name");
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
		pinfoentry.add(badd, new GridBagConstraints(4, 0, 1, 1, 0, 0, 
			GridBagConstraints.EAST, GridBagConstraints.VERTICAL, new Insets(1,2,1,2), 0, 0));
		
		// Show local info.
		bshowinfo.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				infolist.clear();
				badd.setEnabled(false);
				bshowinfo.setEnabled(false);
				bsearchinfo.setEnabled(false);
				
				getLocalService(tfname.getText()).addResultListener(new SwingDefaultResultListener<IHelpline>()
				{
					@Override
					public void customResultAvailable(IHelpline helpline)
					{
						helpline.getInformation()
							.addResultListener(new SwingDefaultResultListener<Set<InformationEntry>>()
						{
							@Override
							public void customResultAvailable(Set<InformationEntry> entries)
							{
								infolist.addAll(entries);
								infomodel.fireTableDataChanged();
								
								badd.setEnabled(true);
								bshowinfo.setEnabled(true);
								bsearchinfo.setEnabled(true);
							}
							
							public void customExceptionOccurred(Exception exception)
							{
								badd.setEnabled(true);
								bshowinfo.setEnabled(true);
								bsearchinfo.setEnabled(true);
							}
						});
					}
					
					@Override
					public void customExceptionOccurred(Exception exception)
					{
						badd.setEnabled(true);
						bshowinfo.setEnabled(true);
						bsearchinfo.setEnabled(true);
					}
				});
			}
		});
		
		// Search for remote info.
		bsearchinfo.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				infolist.clear();
				badd.setEnabled(false);
				bshowinfo.setEnabled(false);
				bsearchinfo.setEnabled(false);
				
				getLocalService(tfname.getText()).addResultListener(new SwingDefaultResultListener<IHelpline>()
				{
					@Override
					public void customResultAvailable(IHelpline helpline)
					{
						helpline.searchInformation()
							.addResultListener(new SwingIntermediateDefaultResultListener<InformationEntry>()
						{
							@Override
							public void customIntermediateResultAvailable(InformationEntry entry)
							{
								infolist.add(entry);
								infomodel.fireTableDataChanged();
							}
							
							public void customFinished()
							{
								badd.setEnabled(true);
								bshowinfo.setEnabled(true);
								bsearchinfo.setEnabled(true);
							}
							
							public void customExceptionOccurred(Exception exception)
							{
								badd.setEnabled(true);
								bshowinfo.setEnabled(true);
								bsearchinfo.setEnabled(true);
							}
						});
					}
					
					@Override
					public void customExceptionOccurred(Exception exception)
					{
						badd.setEnabled(true);
						bshowinfo.setEnabled(true);
						bsearchinfo.setEnabled(true);
					}
				});
			}
		});
		
		// Add new info.
		badd.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				badd.setEnabled(false);
				bshowinfo.setEnabled(false);
				bsearchinfo.setEnabled(false);
				
				getLocalService(tfpname.getText()).addResultListener(new SwingDefaultResultListener<IHelpline>()
				{
					@Override
					public void customResultAvailable(IHelpline helpline)
					{
						helpline.addInformation(tfpinfo.getText());
						
						badd.setEnabled(true);
						bshowinfo.setEnabled(true);
						bsearchinfo.setEnabled(true);
					}
					
					@Override
					public void customExceptionOccurred(Exception exception)
					{
						badd.setEnabled(true);
						bshowinfo.setEnabled(true);
						bsearchinfo.setEnabled(true);
					}
				});
			}
		});
		
		padd.add(pinfoentry, BorderLayout.CENTER);
		
		phelp.add(padd, BorderLayout.SOUTH);
		
		this.add(phelp, BorderLayout.CENTER);
	}
	
	/**
	 *  Get or create the local helpline service for a specific person.
	 */
	protected IFuture<IHelpline>	getLocalService(final String person)
	{
		return agent.scheduleStep(new IComponentStep<IHelpline>()
		{
			@Override
			public IFuture<IHelpline> execute(final IInternalAccess ia)
			{
				final Future<IHelpline>	ret	= new Future<IHelpline>();
				try
				{
					IHelpline	helpline	= ia.getComponentFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>( IHelpline.class, new BasicComponentIdentifier(person, ia.getComponentIdentifier())));
					ret.setResult(helpline);
				}
				catch(ServiceNotFoundException snfe)
				{
					CreationInfo	ci	= new CreationInfo(Collections.singletonMap("person", (Object)person), ia.getComponentIdentifier());
					ia.getComponentFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>( IComponentManagementService.class)).createComponent(ci, person, HelplineAgent.class.getName()+".class")
						.addResultListener(new IntermediateDefaultResultListener<CMSStatusEvent>()
					{
						@Override
						public void intermediateResultAvailable(CMSStatusEvent event)
						{
							if(event instanceof CMSCreatedEvent)
							{
								IHelpline	helpline	= ia.getComponentFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>( IHelpline.class, event.getComponentIdentifier()));
								if(helpline==null)
								{
									exceptionOccurred(new RuntimeException("No service after creation for "+person));
								}
								ret.setResult(helpline);
							}
						}
						
						@Override
						public void exceptionOccurred(Exception exception)
						{
							ret.setException(exception);
						}
					});
				}
				return ret;
			}
		});
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
				
				ia.getComponentFeature(IMonitoringComponentFeature.class).subscribeToEvents(IMonitoringEvent.TERMINATION_FILTER, false, PublishEventLevel.COARSE)
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
	// make eclipse happy
	private static final long serialVersionUID = -6559445738093437784L;
	
	protected List<InformationEntry> list;
	
	public InfoTableModel(List<InformationEntry> list)
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
		InformationEntry ie = list.get(row);
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
