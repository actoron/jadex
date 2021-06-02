package jadex.tools.simcenter;

import java.awt.BorderLayout;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.UIDefaults;

import jadex.base.gui.plugin.IControlCenter;
import jadex.bridge.service.types.simulation.ISimulationService;
import jadex.commons.Properties;
import jadex.commons.Property;
import jadex.commons.TimeFormat;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

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

	/** The jcc. */
	protected IControlCenter	jcc;
	
	/** The simulation service. */
	protected ISimulationService	simservice;
	
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
	public SimCenterPanel(IControlCenter jcc, ISimulationService simservice)
	{
		super(new BorderLayout());
		this.simservice	= simservice;
		this.jcc	= jcc;
		this.timemode = 2;
		
		dateformat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss S");
		
		//this.clockp	= new ClockPanel(getClock());
		clockp	= new ClockPanel(SimCenterPanel.this);
		contextp	= new ContextPanel(SimCenterPanel.this);
		JPanel left	= new JPanel(new BorderLayout());
		left.add(clockp, BorderLayout.NORTH);
		left.add(contextp, BorderLayout.SOUTH);
		
		timerp	= new TimerPanel(SimCenterPanel.this);
		
		JSplitPane sp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		sp.setOneTouchExpandable(true);
		sp.add(left);
		sp.add(timerp);
		
		add(sp, "Center");
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
	 *  Get the simulation service
	 *  @return The simulation service.
	 */
	public ISimulationService	getSimulationService()
	{
		return simservice;
	}
	
	/**
	 *  Get the JCC.
	 */
	public IControlCenter	getJCC()
	{
		return jcc;
	}
	
//	/**
//	 *  Get the host component of a service. 
//	 */
//	public IFuture<IExternalAccess> getComponentForService()
//	{
//		final Future<IExternalAccess> ret = new Future<IExternalAccess>();
//		
//		jcc.getJCCAccess().getServiceProvider().searchService( new ServiceQuery<>( IComponentManagementService.class, ServiceScope.PLATFORM))
//			.addResultListener(new ExceptionDelegationResultListener<IComponentManagementService, IExternalAccess>(ret)
//		{
//			public void customResultAvailable(IComponentManagementService cms)
//			{
////				IComponentManagementService	cms	= (IComponentManagementService)result;
//				cms.getExternalAccess((IComponentIdentifier)((IService)simservice).getId().getProviderId())
//					.addResultListener(new DelegationResultListener<IExternalAccess>(ret));
//			}
//		});
//		
//		return ret;
//	}

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
		timerp.updateView();
	}
	
	/**
	 *  Set properties loaded from project.
	 */
	public IFuture<Void> setProperties(Properties ps)
	{
		int	timemode	= ps.getProperty("timemode")!=null
			? ps.getIntProperty("timemode") : 2;
		setTimemode(timemode);
		updateView();
		return IFuture.DONE;
	}

	/**
	 *  Return properties to be saved in project.
	 */
	public IFuture<Properties> getProperties()
	{
		Properties	props	= new Properties();
		props.addProperty(new Property("timemode", Integer.toString(getTimeMode())));
		return new Future<Properties>(props);
	}

	/**
	 *  Informs the panel that it should stop all its computation
	 */
	public IFuture<Void> shutdown()
	{
		clockp.setActive(false);
		timerp.setActive(false);
		return IFuture.DONE;
	}
}
