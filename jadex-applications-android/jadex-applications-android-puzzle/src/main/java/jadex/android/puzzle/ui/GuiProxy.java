package jadex.android.puzzle.ui;

import jadex.android.puzzle.SokratesService.SokratesListener;
import jadex.bdi.examples.puzzle.Board;
import jadex.bdi.runtime.IBDIInternalAccess;
import jadex.bdi.runtime.IBelief;
import jadex.bdi.runtime.IBeliefbase;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.commons.beans.PropertyChangeEvent;
import jadex.commons.beans.PropertyChangeListener;
import jadex.commons.future.IFuture;

public class GuiProxy
{
	private SokratesListener sokratesListener;

	public GuiProxy(final Board board, final SokratesListener sokratesListener, jadex.bdi.runtime.IBDIExternalAccess access)
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
