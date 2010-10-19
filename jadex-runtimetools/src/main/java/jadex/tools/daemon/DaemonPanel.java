package jadex.tools.daemon;

import jadex.base.gui.componenttree.PropertiesPanel;
import jadex.bridge.IComponentIdentifier;
import jadex.commons.ChangeEvent;
import jadex.commons.IChangeListener;
import jadex.commons.SGUI;
import jadex.commons.concurrent.SwingDefaultResultListener;
import jadex.commons.service.SServiceProvider;
import jadex.micro.IMicroExternalAccess;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;

/**
 */
public class DaemonPanel extends JPanel
{
	//-------- attributes --------
	
	/** The external access of the agent. */
	protected IMicroExternalAccess agent;
	
	//-------- constructors --------
	
	/**
	 *  Create a new gui.
	 */
	public DaemonPanel(final IMicroExternalAccess agent)
	{
		this.agent = agent;
		this.setLayout(new BorderLayout());
		
		JPanel p = new JPanel(new BorderLayout());
		
		final PropertiesPanel sop = new PropertiesPanel("Start Options");
		sop.createTextField("Java command", "java", true);
		sop.createTextField("VM arguments", null, true);
		sop.createTextField("Classpath", null, true);
		sop.createTextField("Main class", "jadex.base.Starter", true);
		sop.createTextField("Program arguments", null, true);
		sop.createTextField("Start directory", ".", true);
		
		JButton[] buts = sop.createButtons(new String[]{"Start", "Reset"});
		buts[0].setToolTipText("Start a new platform.");
		buts[0].addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				SServiceProvider.getService(agent.getServiceProvider(), IDaemonService.class)
					.addResultListener(new SwingDefaultResultListener()
				{
					public void customResultAvailable(Object source, Object result)
					{
						IDaemonService ds = (IDaemonService)result;
						StartOptions so = new StartOptions();
						so.setJavaCommand(sop.getTextField("Java command").getText());
						so.setVMArguments(sop.getTextField("VM arguments").getText());
						so.setClassPath(sop.getTextField("Classpath").getText());
						so.setMain(sop.getTextField("Main class").getText());
						so.setProgramArguments(sop.getTextField("Program arguments").getText());
						so.setStartDirectory(sop.getTextField("Start directory").getText());
						ds.startPlatform(so);
					}
				});
			}
		});
		buts[1].setToolTipText("Reset the start settings.");
		buts[1].addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				sop.getTextField("Java command").setText("java");
				sop.getTextField("VM arguments").setText("");
				sop.getTextField("Classpath").setText("");
				sop.getTextField("Main class").setText("jadex.starter.Main");
				sop.getTextField("Program arguments").setText("");
				sop.getTextField("Start directory").setText("");
			}
		});
		
		final JList platforml = new JList(new DefaultListModel());
		
		JButton shutdownb = new JButton("Shudown platform");
		shutdownb.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				SServiceProvider.getService(agent.getServiceProvider(), IDaemonService.class)
					.addResultListener(new SwingDefaultResultListener()
				{
					public void customResultAvailable(Object source, Object result)
					{
						IDaemonService ds = (IDaemonService)result;
						Object[] cids = (Object[])platforml.getSelectedValues();
						for(int i=0; i<cids.length; i++)
						{
							ds.shutdownPlatform(((IComponentIdentifier)cids[i]));
						}
							
					}
				});
			}
		});
		
		SServiceProvider.getService(agent.getServiceProvider(), IDaemonService.class)
			.addResultListener(new SwingDefaultResultListener()
		{
			public void customResultAvailable(Object source, Object result)
			{
				IDaemonService ds = (IDaemonService)result;
				ds.addChangeListener(new IChangeListener()
				{
					public void changeOccurred(ChangeEvent event)
					{
						if(IDaemonService.ADDED.equals(event.getType()))
						{
							((DefaultListModel)platforml.getModel()).addElement(event.getValue());
						}
						else if(IDaemonService.REMOVED.equals(event.getType()))
						{
							((DefaultListModel)platforml.getModel()).removeElement(event.getValue());
						}
					}
				});
			}
		});
		
		p.add(sop, BorderLayout.NORTH);
		p.add(platforml, BorderLayout.EAST);
		p.add(shutdownb, BorderLayout.SOUTH);
		
		this.add(p, BorderLayout.CENTER);
	}
	
	/**
	 *  Create a gui frame.
	 */
	public static void createGui(final IMicroExternalAccess agent)
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
