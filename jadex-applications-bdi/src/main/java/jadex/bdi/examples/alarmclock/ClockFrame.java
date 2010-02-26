package jadex.bdi.examples.alarmclock;

import jadex.bdi.runtime.AgentEvent;
import jadex.bdi.runtime.IAgentListener;
import jadex.bdi.runtime.IBDIExternalAccess;
import jadex.commons.SGUI;

import java.awt.AWTException;
import java.awt.MenuItem;
import java.awt.Point;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIDefaults;
import javax.swing.border.EtchedBorder;

/**
 *  The clock frame.
 */
public class ClockFrame extends JFrame
{
	//-------- static part --------

	/** The image icons. */
	protected static UIDefaults	icons	= new UIDefaults(new Object[]
	{
		"Clock", SGUI.makeIcon(ClockFrame.class,	"/jadex/bdi/examples/alarmclock/images/clock.png")
	});

	//-------- attributes --------

	/** The time label. */
	protected JLabel	time;

	/** The external access. */
	protected IBDIExternalAccess agent;

	/** The timer. */
	protected Timer timer;

	/** The formatter. */
	protected SimpleDateFormat	format;

	/** The ampm format. */
	protected boolean last_ampm;

	/** The last font size. */
	protected int last_fontsize;

	/** The alarms gui. */
	protected AlarmsGui alarms_gui;

	/** The system tray. */
	// Needs Java 1.6
	protected SystemTray	tray;
	
	/** The tray icon. */
	// Needs Java 1.6
	protected TrayIcon ti;
	
	/**
	 *  Create a new clock frame.
	 */
	public ClockFrame(IBDIExternalAccess agent)
	{
		this.agent = agent;
		format = new SimpleDateFormat();

		time = new JLabel("", JLabel.CENTER);

		JPanel cp = new JPanel();
		cp.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
		cp.add("Center", time);

		setUndecorated(true);
		getContentPane().add(cp, "Center");

		// Add dragging support
		final Point origin = new Point();
		cp.addMouseListener(new MouseAdapter()
		{
			public void mousePressed(MouseEvent e)
			{
				origin.x = e.getX();
				origin.y = e.getY();
			}
		});
		cp.addMouseMotionListener(new MouseMotionAdapter()
		{
			public void mouseDragged(MouseEvent e)
			{
				Point p = getLocation();
				setLocation(p.x + e.getX() - origin.x, p.y + e.getY() - origin.y);
			}
		});

		// Create the popup menu
		final JPopupMenu jmenu = new JPopupMenu();
//		add(menu);
		JMenuItem alarms = new JMenuItem("Alarms");
		JMenuItem options = new JMenuItem("Options");
		JMenuItem exit = new JMenuItem("Exit");
		jmenu.add(alarms);
		jmenu.add(options);
		jmenu.addSeparator();
		jmenu.add(exit);
		cp.addMouseListener(new MouseAdapter()
		{
			public void mousePressed(MouseEvent evt)
			{
				if(evt.isPopupTrigger())
					jmenu.show(evt.getComponent(), evt.getX(), evt.getY());
			}

			public void mouseReleased(MouseEvent evt)
			{
				if(evt.isPopupTrigger())
					jmenu.show(evt.getComponent(), evt.getX(), evt.getY());
			}
		});

		ActionListener lalarms = new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if(alarms_gui==null || !alarms_gui.isVisible())
				{
					alarms_gui = new AlarmsGui(ClockFrame.this.agent);
					alarms_gui.pack();
					alarms_gui.setLocation(SGUI.calculateMiddlePosition(alarms_gui));
					alarms_gui.setVisible(true);
				}
			}
		};
		ActionListener loptions = new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				OptionDialog odia = new OptionDialog(ClockFrame.this, ClockFrame.this.agent);
				odia.pack();
				odia.setLocation(SGUI.calculateMiddlePosition(odia));
				odia.setVisible(true);
			}
		};
		ActionListener lexit = new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if(alarms_gui!=null)
					alarms_gui.dispose();
				dispose();
				Settings sets = (Settings)ClockFrame.this.agent.getBeliefbase().getBelief("settings").getFact();
				if(sets.isAutosave())
				{
					try
					{
						sets.save();
					}
					catch(Exception ex)
					{
						JOptionPane.showMessageDialog(ClockFrame.this, "Cannot save settings. The file: \n"
							+sets.getFilename()+"\n could not be written", "Settings error",
							JOptionPane.ERROR_MESSAGE);
					}
				}
				ClockFrame.this.agent.killAgent(); // Use -autoshutdown to kill standalone platform as well
				//IGoal kp = agent.createGoal("cms_shutdown_platform");
				//agent.dispatchTopLevelGoal(kp);
			}
		};
		
		alarms.addActionListener(lalarms);
		options.addActionListener(loptions);
		exit.addActionListener(lexit);

		if(SystemTray.isSupported())
		{
			final PopupMenu menu = new PopupMenu();
			MenuItem a = new MenuItem("Alarms");
			MenuItem o = new MenuItem("Options");
			MenuItem e = new MenuItem("Exit");
			menu.add(a);
			menu.add(o);
			menu.addSeparator();
			menu.add(e);
			a.addActionListener(lalarms);
			o.addActionListener(loptions);
			e.addActionListener(lexit);
			
			tray = SystemTray.getSystemTray();
		    ti = new TrayIcon(((ImageIcon)icons.getIcon("Clock")).getImage(), "Jadex - Alarm Clock", menu);
			
			try
			{
				tray.add(ti);
		    
				final MenuItem stt = new MenuItem("Send to tray");
				final MenuItem rft = new MenuItem("Restore");
				menu.add(stt);
				
				ActionListener lstt = new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						try
						{
							setVisible(false);
							menu.remove(stt);
							menu.add(rft);
	//						tray.add(ti);
						}
						catch(Exception e2)
						{
							e2.printStackTrace();
						}
					}
				};
				
				stt.addActionListener(lstt);
				
				rft.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
	//					tray.remove(ti);
						menu.remove(rft);
						menu.add(stt);
						setVisible(true);
					}
				});
				/*ti.addActionListener(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						tray.removeTrayIcon(ti);
						menu.remove(rft);
						menu.add(stt);
						ClockGui.this.setVisible(true);
					}
				});*/
				
				JMenuItem send = new JMenuItem("Send to tray");
				jmenu.add(send);
				send.addActionListener(lstt);
				
			}
			catch(AWTException ex)
			{
				ex.printStackTrace();
			}
		}
		

		pack();
		setLocation(SGUI.calculateMiddlePosition(ClockFrame.this));
		setVisible(true);

		timer	= new Timer(1000, new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				refresh();
			}
		});
		timer.setRepeats(true);
		timer.start();

		agent.addAgentListener(new IAgentListener()
		{
			public void agentTerminating(AgentEvent ae)
			{
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						if(tray!=null)
							tray.remove(ti);
						timer.stop();
						dispose();
					}
				});
			}
			
			public void agentTerminated(AgentEvent ae)
			{
			}
		});
	}
	
	//-------- methods --------
	
	/**
	 *  Refresh the clock.
	 */
	public void refresh()
	{
		final boolean[] firsttime = new boolean[]{true};
		try
		{
			Date	current	= new Date(agent.getTime());
			Settings sets = (Settings)agent.getBeliefbase().getBelief("settings").getFact();
			if(sets.isAMPM()!=last_ampm || sets.getFontsize()!=last_fontsize || firsttime[0] )
			{
				if(sets.isAMPM())
					format.applyPattern("hh:mm:ss a");
				else
					format.applyPattern("HH:mm:ss");
				firsttime[0] = false;
				time.setFont(time.getFont().deriveFont((float)sets.getFontsize()));
				time.setText(format.format(current));
				pack();
				last_ampm = sets.isAMPM();
				last_fontsize = sets.getFontsize();
			}
			else
			{
				time.setText(format.format(current));
			}
			if(ti!=null)
				ti.setToolTip(format.format(current));
		}
		catch(Exception e)
		{
			//e.printStackTrace();
			// Nop. Agent may died, this is executed on swing thread
			// hence exception should be catched.
		}
	}
}
