package jadex.bpmn.editor.gui.propertypanels;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

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
	
	/**
	 *  Text extraction from the Java "Document" class is braindead,
	 *  throws non-RuntimeException: Bloat, bloat, bloat.
	 *  
	 *  @param doc The document.
	 *  @return The extracted String.
	 */
	public static final String getText(Document doc)
	{
		String ret = null;
        
		try
        {
            ret = doc.getText(0, doc.getLength());
            if (ret.length() == 0)
    		{
    			ret = null;
    		}
        }
        catch (BadLocationException e)
        {
        }
        
        return ret;
	}
}
