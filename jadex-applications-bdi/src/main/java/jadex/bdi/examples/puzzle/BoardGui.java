package jadex.bdi.examples.puzzle;

import jadex.bdi.runtime.AgentEvent;
import jadex.bdi.runtime.IAgentListener;
import jadex.bdi.runtime.IExternalAccess;
import jadex.commons.SGUI;

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
				agent.killAgent();
			}
		});
		
		agent.addAgentListener(new IAgentListener()
		{
			public void agentTerminating(AgentEvent ae)
			{
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						BoardGui.this.dispose();
					}
				});
			}
			
			public void agentTerminated(AgentEvent ae)
			{
			}
		});
	}
}
