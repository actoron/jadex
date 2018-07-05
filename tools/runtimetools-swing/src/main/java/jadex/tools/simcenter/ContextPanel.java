package jadex.tools.simcenter;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.UIDefaults;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import jadex.base.SRemoteClock;
import jadex.base.SRemoteClock.SimulationState;
import jadex.bridge.service.types.clock.IClock;
import jadex.bridge.service.types.simulation.ISimulationService;
import jadex.commons.gui.SGUI;
import jadex.commons.gui.ToolTipAction;
import jadex.commons.gui.future.SwingIntermediateDefaultResultListener;

/**
 *	The context panel shows the settings for an execution context.
 */
public class ContextPanel extends JPanel
{
	//-------- static part --------

	/** The image icons. */
	protected static final UIDefaults	icons	= new UIDefaults(new Object[]
	{
		// todo: rename icon?
		"start",	SGUI.makeIcon(ContextPanel.class, "/jadex/tools/common/images/start.png"),
		"step_event",	SGUI.makeIcon(ContextPanel.class, "/jadex/tools/common/images/single_step_event.png"),
		"step_time",	SGUI.makeIcon(ContextPanel.class, "/jadex/tools/common/images/single_step_time.png"),
		"pause",	SGUI.makeIcon(ContextPanel.class, "/jadex/tools/common/images/pause.png")
	});
	
	//-------- attributes --------

	/** The sim center panel. */
	protected SimCenterPanel simp;
	
	//-------- constructors --------

	/**
	 *  Create a context panel.
	 *  @param context The execution context.
	 */
	public ContextPanel(SimCenterPanel simp)
	{
		this.setLayout(new FlowLayout());
		this.simp = simp;
		
		this.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), " Execution Control "));
		
		JToolBar	toolbar	= new JToolBar("Simulation Control");
		toolbar.add(START);
		toolbar.add(STEP_EVENT);
		toolbar.add(STEP_TIME);
		toolbar.add(PAUSE);
		this.add(toolbar);
		
		setActive(true);
	}
	
	/**
	 *  Update the view.
	 */
	public void	updateView(SimulationState state)
	{
//		System.out.println("update view: "+state);
		
		boolean	startenabled	= !state.executing;
		boolean	pauseenabled	= state.executing;
		boolean	stepenabled	= !state.executing && state.clockok
			&& !IClock.TYPE_CONTINUOUS.equals(state.clocktype)
			&& !IClock.TYPE_SYSTEM.equals(state.clocktype);
		
		START.setEnabled(startenabled);
		STEP_EVENT.setEnabled(stepenabled);
		STEP_TIME.setEnabled(stepenabled);
		PAUSE.setEnabled(pauseenabled);
	}
	
	/**
	 *  Start action.
	 */
	public final Action START = new ToolTipAction(null, icons.getIcon("start"),
		"Start the execution of the application")
	{
		public void actionPerformed(ActionEvent e)
		{
			simp.getSimulationService().start();
		}
	};
	
	/**
	 *  Step action.
	 */
	public final Action STEP_EVENT = new ToolTipAction(null, icons.getIcon("step_event"),
		"Execute one timer entry.")
	{
		public void actionPerformed(ActionEvent e)
		{
			simp.getSimulationService().stepEvent();
		}
	};
	
	/**
	 *  Time step action.
	 */
	public final Action STEP_TIME = new ToolTipAction(null, icons.getIcon("step_time"),
		"Execute all timer entries belonging to the current time point.")
	{
		public void actionPerformed(ActionEvent e)
		{
			simp.getSimulationService().stepTime();
		}
	};
	
	/**
	 *  Pause the current execution.
	 */
	public final Action PAUSE = new ToolTipAction(null , icons.getIcon("pause"),
		"Pause the current execution.")
	{
		public void actionPerformed(ActionEvent e)
		{
			simp.getSimulationService().pause();
		}
	};

	/**
	 *  Activate / deactivate updates.
	 */
	public void	setActive(final boolean active)
	{
		final String	id	= "ContextPanel"+ContextPanel.this.hashCode()+"@"+simp.jcc.getJCCAccess().getIdentifier();
		final ISimulationService	simservice	= simp.getSimulationService();
		if(active)
		{
			SRemoteClock.addSimulationListener(id, simservice, simp.getJCC().getPlatformAccess())
				.addResultListener(new SwingIntermediateDefaultResultListener<SimulationState>(ContextPanel.this)
			{
				public void customIntermediateResultAvailable(SimulationState result)
				{
					updateView(result);
				}
			});
		}
		else
		{
			SRemoteClock.removeSimulationListener(id, simservice, simp.getJCC().getPlatformAccess());
		}
	}
}
