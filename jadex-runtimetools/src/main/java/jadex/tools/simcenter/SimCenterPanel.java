package jadex.tools.simcenter;

import jadex.adapter.base.ISimulationService;
import jadex.bridge.IClockService;
import jadex.bridge.IPlatform;
import jadex.bridge.ITimedObject;
import jadex.commons.TimeFormat;

import java.awt.BorderLayout;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.UIDefaults;

/**
 *  The simulation center panel.
 */
public class SimCenterPanel extends JPanel
{
	//-------- static part --------

	/** The image icons. */
	protected static UIDefaults	icons	= new UIDefaults(new Object[]
	{
		//"Browse", SGUI.makeIcon(SimCenterPanel.class,	"/jadex/tools/common/images/dots_small.png"),
	});

	//-------- attributes --------

	/** The sim center plugin. */
	protected SimCenterPlugin simcenter;

	/** The clock panel. */
	protected ClockPanel clockp;
	
	/** The context panel. */
	protected ContextPanel contextp;
	
	/** The timer panel. */
	protected TimerPanel timerp;
	
	/** Format. */
	protected DateFormat dateformat;
	
	/** The time mode. */
	protected int timemode;
	
	//-------- constructors --------

	/**
	 *  The sim center gui.
	 *  @param simcenter The sim center.
	 */
	public SimCenterPanel(final SimCenterPlugin simcenter)
	{
		super(new BorderLayout());
		this.simcenter = simcenter;
		
		ISimulationService simservice = (ISimulationService)simcenter.getJCC().getAgent().getPlatform().getService(ISimulationService.class);
		if(simservice==null)
			throw new RuntimeException("Could not find simulation service: "+simservice);
		
		this.dateformat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss S");
		
		//this.clockp	= new ClockPanel(getClock());
		this.clockp	= new ClockPanel(this);
		this.contextp	= new ContextPanel(this);
		JPanel left	= new JPanel(new BorderLayout());
		left.add(clockp, BorderLayout.NORTH);
		left.add(contextp, BorderLayout.SOUTH);
		
		this.timerp	= new TimerPanel(this);
		
		JSplitPane sp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		sp.setOneTouchExpandable(true);
		sp.add(left);
		sp.add(timerp);
		
		this.add(sp, "Center");
		
		/*Timer t = new Timer(100, new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				updateView();
			}
		});
		t.start();*/
	}
	
	/**
	 *  Get the platform.
	 *  @return The platform.
	 */
	public IPlatform getPlatform()
	{
		return (IPlatform)simcenter.getJCC().getAgent().getPlatform();
	}
	
	/**
	 *  Get the execution context panel.
	 *  @return The execution context panel.
	 */
	public ContextPanel getContextPanel()
	{
		return contextp;
	}
	
	/**
	 *  Format a time.
	 *  @return The formatted time string.
	 */
	public String formatTime(long time)
	{
		String ret;
		
		if(timemode==0)
			ret = ""+time;
		else if(timemode==1)
			ret = TimeFormat.format(time);
		else
			ret = dateformat.format(new Date(time));
		
		return ret;
	}
	
	
	
	/**
	 *  Get the time mode.
	 *  @return The time mode.
	 */
	public int getTimeMode()
	{
		return timemode;
	}

	/**
	 *  Set the time mode.
	 *  @param timemode The timemode to set.
	 */
	public void setTimemode(int timemode)
	{
		this.timemode = timemode;
	}

	/**
	 *  Update the view.
	 */
	public void updateView()
	{
		clockp.updateView();
		contextp.updateView();
		timerp.updateView();
	}
	
	/**
	 *  Main for testing. 
	 */
	public static void main(String[] args)
	{
		final SimCenterPanel p = new SimCenterPanel(null);
		final IClockService clock = (IClockService)p.getPlatform().getService(IClockService.class);
		for(int i=0; i<30; i++)
		{
			clock.createTimer(2000*i, new ITimedObject()
			{
				public void timeEventOccurred()
				{
					System.out.println("Event: "+clock.getTime());
				}
			});
		}
		
		JFrame f = new JFrame();
		f.add(p, "Center");
		f.pack();
		f.setVisible(true);
		
		/*Timer t = new Timer(50, new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				p.updateView();
			}
		});
		t.start();*/
		clock.start();
	}
}
