package jadex.bdibpmn.examples.puzzle;

import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IMonitoringComponentFeature;
import jadex.bridge.service.types.monitoring.IMonitoringEvent;
import jadex.bridge.service.types.monitoring.IMonitoringService.PublishEventLevel;
import jadex.commons.beans.PropertyChangeEvent;
import jadex.commons.beans.PropertyChangeListener;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.IntermediateDefaultResultListener;
import jadex.commons.gui.SGUI;
import jadex.commons.gui.future.SwingIntermediateResultListener;
import jadex.commons.transformation.annotations.Classname;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 *  The board gui.
 */
public class BoardGui extends JFrame
{
	//-------- attributes --------

	/** The board to visualize. */
	protected IBoard board;

	//-------- constructors --------

	/**
	 *  Create a new board gui.
	 */
	public BoardGui(IExternalAccess agent, final IBoard board)
	{
		this(agent, board, false);
	}

	/**
	 *  Create a new board gui.
	 */
	public BoardGui(final IExternalAccess agent, final IBoard board, boolean controls)
	{
		this.board = board;
		final BoardPanel bp = new BoardPanel(board);
		this.board.addPropertyChangeListener(new PropertyChangeListener()
		{
			public void propertyChange(PropertyChangeEvent evt)
			{
				bp.update(evt);
			}
		});

		this.getContentPane().add("Center", bp);
		if(controls)
		{
			final BoardControlPanel bcp = new BoardControlPanel(board, bp);
			this.getContentPane().add("South", bcp);
		}
		this.setTitle("Puzzle Board");
		this.setSize(400, 400);
		this.setLocation(SGUI.calculateMiddlePosition(this));
		this.setVisible(true);

		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				agent.killComponent();
			}
		});
		
		IResultListener<Void>	dislis	= new IResultListener<Void>()
		{
			public void exceptionOccurred(Exception exception)
			{
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
//						System.out.println("boardgui dispose2: "+Thread.currentThread());
						BoardGui.this.dispose();
					}
				});
			}
			public void resultAvailable(Void result)
			{
			}
		};
		
		agent.scheduleStep(new IComponentStep<Void>()
		{
			@Classname("dispose")
			public IFuture<Void> execute(IInternalAccess ia)
			{
//				IBDIInternalAccess bia = (IBDIInternalAccess)ia;
//				
//				bia.addComponentListener(new TerminationAdapter()
//				{
//					public void componentTerminated()
//					{
//						SwingUtilities.invokeLater(new Runnable()
//						{
//							public void run()
//							{
//								BoardGui.this.dispose();
//							}
//						});
//					}
//				});
				
				ia.getComponentFeature(IMonitoringComponentFeature.class).subscribeToEvents(IMonitoringEvent.TERMINATION_FILTER, false, PublishEventLevel.COARSE)
					.addResultListener(new SwingIntermediateResultListener<IMonitoringEvent>(new IntermediateDefaultResultListener<IMonitoringEvent>()
				{
					public void intermediateResultAvailable(IMonitoringEvent result)
					{
//						System.out.println("boardgui dispose");
						BoardGui.this.dispose();
					}
				}));
				
				return IFuture.DONE;
			}
		}).addResultListener(dislis);
	}
}
