package jadex.bdiv3.examples.puzzle;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;

/**
 *  The control part of
 */
public class BoardControlPanel extends JPanel
{
	//-------- constructors --------

	/**
	 *  Create a new board control panel.
	 */
	public BoardControlPanel(final IBoard board, BoardPanel bp)
	{
		bp.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				//System.out.println("Action event: "+e);
				Position p = (Position)e.getSource();

				List<Move> pmoves = board.getPossibleMoves();
				boolean moved = false;
				for(int i=0; i<pmoves.size() && !moved; i++)
				{
					Move m = pmoves.get(i);
					if(m.getStart().equals(p))
					{
						board.move(m);
						moved = true;
					}
				}
				if(!moved)
					System.out.println("Cannot make move with piece on: "+p);
			}
		});

		JButton back = new JButton("back");
		back.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				board.takeback();
			}
		});
		this.add("Center", back);
	}

}
