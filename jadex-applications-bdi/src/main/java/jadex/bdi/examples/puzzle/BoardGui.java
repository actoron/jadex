package jadex.bdi.examples.puzzle;

import jadex.bdi.runtime.IBDIExternalAccess;
import jadex.bdi.runtime.IBDIInternalAccess;
import jadex.bridge.ComponentAdapter;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.commons.ChangeEvent;
import jadex.commons.future.IFuture;
import jadex.commons.gui.SGUI;
import jadex.xml.annotation.XMLClassname;

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
		
		agent.scheduleStep(new IComponentStep()
		{
			@XMLClassname("dispose")
			public Object execute(IInternalAccess ia)
			{
				IBDIInternalAccess bia = (IBDIInternalAccess)ia;
				bia.addComponentListener(new ComponentAdapter()
				{
					public IFuture componentTerminating(ChangeEvent ae)
					{
						SwingUtilities.invokeLater(new Runnable()
						{
							public void run()
							{
								BoardGui.this.dispose();
							}
						});
						return IFuture.DONE;
					}
				});
				return null;
			}
		});
		
//		agent.addAgentListener(new IAgentListener()
//		{
//			public void agentTerminating(AgentEvent ae)
//			{
//				SwingUtilities.invokeLater(new Runnable()
//				{
//					public void run()
//					{
//						BoardGui.this.dispose();
//					}
//				});
//			}
//			
//			public void agentTerminated(AgentEvent ae)
//			{
//			}
//		});
	}
}
