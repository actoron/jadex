package jadex.bdibpmn.examples.puzzle;

import jadex.bdi.runtime.IBDIExternalAccess;
import jadex.bdi.runtime.IBDIInternalAccess;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.TerminationAdapter;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.gui.SGUI;
import jadex.commons.transformation.annotations.Classname;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

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
	public BoardGui(IBDIExternalAccess agent, final IBoard board)
	{
		this(agent, board, false);
	}

	/**
	 *  Create a new board gui.
	 */
	public BoardGui(final IBDIExternalAccess agent, final IBoard board, boolean controls)
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
				BoardGui.this.dispose();
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
				IBDIInternalAccess bia = (IBDIInternalAccess)ia;
				
				bia.addComponentListener(new TerminationAdapter()
				{
					public void componentTerminated()
					{
						SwingUtilities.invokeLater(new Runnable()
						{
							public void run()
							{
								BoardGui.this.dispose();
							}
						});
					}
				});
				return IFuture.DONE;
			}
		}).addResultListener(dislis);
	}
}
