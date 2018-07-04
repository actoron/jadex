package jadex.tools.daemon;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.daemon.IDaemonService;
import jadex.bridge.service.types.daemon.StartOptions;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.gui.PropertiesPanel;
import jadex.commons.gui.SGUI;
import jadex.commons.gui.future.SwingDefaultResultListener;
import jadex.commons.gui.jtable.ObjectTableModel;

/**
 *  Panel for daemon configuration.
 */
public class DaemonPanel extends JPanel
{
	//-------- attributes --------
	
	/** The external access of the agent. */
	protected IExternalAccess agent;
	
	/** The change listener. */
	protected DaemonChangeListener listener;
	
	//-------- constructors --------
	
	/**
	 *  Create a new gui.
	 */
	public DaemonPanel(final IExternalAccess agent)
	{
		this.agent = agent;
		
		this.setLayout(new BorderLayout());
		
		JPanel p = new JPanel(new BorderLayout());
		
		final PropertiesPanel stop = new PropertiesPanel("Start Options");
		stop.createTextField("Java command", "java", true, 0);
		stop.createTextField("VM arguments", null, true, 0);
		stop.createTextField("Classpath", null, true, 0);
		stop.createTextField("Main class", "jadex.base.Starter", true, 0);
		stop.createTextField("Program arguments", null, true, 0);
		stop.createTextField("Start directory", ".", true, 0);
		
		JButton[] stobuts = stop.createButtons("stobuts", new String[]{"Start", "Reset"}, 1);
		stobuts[0].setToolTipText("Start a new platform.");
		stobuts[0].addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				SServiceProvider.searchService(agent, new ServiceQuery<>( IDaemonService.class, RequiredServiceInfo.SCOPE_PLATFORM))
					.addResultListener(new SwingDefaultResultListener<IDaemonService>()
				{
					public void customResultAvailable(IDaemonService result)
					{
						IDaemonService ds = (IDaemonService)result;
						StartOptions so = new StartOptions();
						so.setJavaCommand(stop.getTextField("Java command").getText());
						so.setVMArguments(stop.getTextField("VM arguments").getText());
						so.setClassPath(stop.getTextField("Classpath").getText());
						so.setMain(stop.getTextField("Main class").getText());
						so.setProgramArguments(stop.getTextField("Program arguments").getText());
						so.setStartDirectory(stop.getTextField("Start directory").getText());
						ds.startPlatform(so);
					}
				});
			}
		});
		stobuts[1].setToolTipText("Reset the start settings.");
		stobuts[1].addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				stop.getTextField("Java command").setText("java");
				stop.getTextField("VM arguments").setText("");
				stop.getTextField("Classpath").setText("");
				stop.getTextField("Main class").setText("jadex.base.Starter");
				stop.getTextField("Program arguments").setText("");
				stop.getTextField("Start directory").setText("");
			}
		});
		
		final ObjectTableModel ptm = new ObjectTableModel(new String[]{"Platform Name"});
		final JTable platformt = new JTable(ptm);
		platformt.setPreferredScrollableViewportSize(new Dimension(600, 120));
//		jtdis.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//		platformt.setDefaultRenderer(IComponentIdentifier.class, new ComponentIdentifierRenderer());
		
		this.listener = new DaemonChangeListener(platformt);
		
		SServiceProvider.searchService(agent, new ServiceQuery<>( IDaemonService.class, RequiredServiceInfo.SCOPE_PLATFORM))
			.addResultListener(new SwingDefaultResultListener<IDaemonService>()
		{
			public void customResultAvailable(IDaemonService result)
			{
				IDaemonService ds = (IDaemonService)result;
				ds.addChangeListener(listener);
			}
		});
		
		final PropertiesPanel suop = new PropertiesPanel("Shutdown Options");
		suop.addFullLineComponent("platformtable", new JScrollPane(platformt), 1);
		JButton[] suobuts = suop.createButtons("suobuts", new String[]{"Shutdown"}, 0);
		suobuts[0].setToolTipText("Start a new platform.");
		suobuts[0].addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				SServiceProvider.searchService(agent, new ServiceQuery<>( IDaemonService.class, RequiredServiceInfo.SCOPE_PLATFORM))
					.addResultListener(new SwingDefaultResultListener<IDaemonService>()
				{
					public void customResultAvailable(IDaemonService result)
					{
						IDaemonService ds = (IDaemonService)result;
						int[] rows = (int[])platformt.getSelectedRows();
						for(int i=0; i<rows.length; i++)
						{
							IComponentIdentifier cid = (IComponentIdentifier)ptm.getObjectForRow(rows[i]);
							ds.shutdownPlatform(cid);
						}
					}
				});
			}
		});
		
		p.add(stop, BorderLayout.NORTH);
		p.add(suop, BorderLayout.CENTER);
		
		this.add(p, BorderLayout.CENTER);
	}
	
	/**
	 *  Informs the panel that it should stop all its computation
	 */
	public IFuture<Void> shutdown()
	{
		final Future<Void> ret = new Future<Void>();
		SServiceProvider.searchService(agent, new ServiceQuery<>( IDaemonService.class, RequiredServiceInfo.SCOPE_PLATFORM))
			.addResultListener(new ExceptionDelegationResultListener<IDaemonService, Void>(ret)
		{
			public void customResultAvailable(IDaemonService ds)
			{
//				IDaemonService ds = (IDaemonService)result;
				ds.removeChangeListener(listener);
				ret.setResult(null);
			}
			
			public void exceptionOccurred(Exception exception)
			{
				ret.setException(exception);
			}
		});
		return ret;
	}
	
	/**
	 *  Finalize.
	 */
	protected void finalize() throws Throwable
	{
		shutdown();
		super.finalize();
	}
	
	/**
	 *  Create a gui frame.
	 */
	public static void createGui(final IExternalAccess agent)
	{
		final JFrame f = new JFrame();
		f.add(new DaemonPanel(agent));
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
		
		// todo: micro listener
	}
}
