package jadex.gpmn.editor.gui;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Adapter class that merges all document updates.
 *
 */
public abstract class DocumentAdapter implements DocumentListener
{
	public void insertUpdate(DocumentEvent e)
	{
		update(e);
	}
	
	public void removeUpdate(DocumentEvent e)
	{
		update(e);
	}
	
	public void changedUpdate(DocumentEvent e)
	{
		update(e);
	}
	
	/**
	 *  Merged update call.
	 *  @param e The event.
	 */
	public abstract void update(DocumentEvent e);
}
