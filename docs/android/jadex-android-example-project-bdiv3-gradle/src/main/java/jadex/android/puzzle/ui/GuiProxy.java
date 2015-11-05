package jadex.android.puzzle.ui;

import jadex.android.puzzle.SokratesService.SokratesListener;
import jadex.bdi.examples.puzzle.Board;
import jadex.bdiv3.examples.puzzle.IBoard;
import jadex.commons.beans.PropertyChangeEvent;
import jadex.commons.beans.PropertyChangeListener;

public class GuiProxy
{
	private SokratesListener sokratesListener;

	public GuiProxy(final IBoard board, final SokratesListener sokratesListener)
	{
		this.sokratesListener = sokratesListener;
		sokratesListener.setBoard(board);
		board.addPropertyChangeListener(new PropertyChangeListener()
		{
			
			@Override
			public void propertyChange(PropertyChangeEvent event)
			{
				sokratesListener.handleEvent(event);
			}
		});
	}
	
	public void showMessage(String text) {
		sokratesListener.showMessage(text);
	}
}
