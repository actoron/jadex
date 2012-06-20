package jadex.gpmn.editor.gui.controllers;

import jadex.gpmn.editor.gui.IModelContainer;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import com.mxgraph.view.mxStylesheet;

/**
 * 
 * Class for providing controllers for the menu items.
 *
 */
public class SMenuControllerFactory
{
	/**
	 *  Creates a controller for setting style sheets.
	 * 
	 *  @param container The model container.
	 *  @param sheet The style sheet.
	 *  @return The controller.
	 */
	public static Action createStyleController(final IModelContainer container, final mxStylesheet sheet)
	{
		return new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				container.getGraph().setStylesheet(sheet);
				container.getGraph().refresh();
			}
		};
	}
}
