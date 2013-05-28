package jadex.bpmn.editor.gui;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

public class CellContextMenu extends JPopupMenu
{
	public CellContextMenu(final BpmnGraph graph, final Object[] cells)
	{
		JMenuItem mitem = new JMenuItem(new AbstractAction("Bring to Front")
		{
			public void actionPerformed(ActionEvent e)
			{
				graph.orderCells(false, cells);
			}
		});
		add(mitem);
		
		mitem = new JMenuItem(new AbstractAction("Send to Back")
		{
			public void actionPerformed(ActionEvent e)
			{
				graph.orderCells(true, cells);
			}
		});
		add(mitem);
	}
}
