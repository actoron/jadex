package jadex.tools.daemon;

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
		
		JPanel p = new JPanel(new FlowLayout());
		
		JButton startb = new JButton("Start platform");
		startb.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				SServiceProvider.getService(agent.getServiceProvider(), IDaemonService.class)
					.addResultListener(new SwingDefaultResultListener()
				{
					public void customResultAvailable(Object source, Object result)
					{
						IDaemonService ds = (IDaemonService)result;
						ds.startPlatform(null);
					}
				});
			}
		});
		
//		JList platforml = new JList();
//		DefaultListModel 
		
//		JButton shutdownb = new JButton("Shudown platform");
//		shutdownb.addActionListener(new ActionListener()
//		{
//			public void actionPerformed(ActionEvent e)
//			{
//				SServiceProvider.getService(agent.getServiceProvider(), IDaemonService.class)
//					.addResultListener(new SwingDefaultResultListener()
//				{
//					public void customResultAvailable(Object source, Object result)
//					{
//						IDaemonService ds = (IDaemonService)result;
//						ds.shutdownPlatform();
//					}
//				});
//			}
//		});
		
		p.add(startb);
		
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
