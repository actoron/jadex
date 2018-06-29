package jadex.gpmn.editor.gui;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 *  An improved JFileChooser class.
 *
 */
public class BetterFileChooser extends JFileChooser
{
	
	public BetterFileChooser()
	{
		super();
	}
	
	public BetterFileChooser(File file)
	{
		super(file);
	}
	
	/**
     * Called by the UI when the user hits the Approve button
     * (labeled "Open" or "Save", by default). This can also be
     * called by the programmer.
     * This method causes an action event to fire
     * with the command string equal to
     * <code>APPROVE_SELECTION</code>.
     *
     * @see #APPROVE_SELECTION
     */
	public void approveSelection()
	{
	    File file = getSelectedFile();
	    
	    // Add overwrite dialog.
	    if(getDialogType() == JFileChooser.SAVE_DIALOG && file.exists())
	    {
	        int result = JOptionPane.showConfirmDialog(this,file.getAbsolutePath() + " already exists.\n Do you want to replace it?","Save",JOptionPane.YES_NO_CANCEL_OPTION);
	        switch(result)
	        {
	            case JOptionPane.NO_OPTION:
	                return;
	            case JOptionPane.CLOSED_OPTION:
	                return;
	            case JOptionPane.CANCEL_OPTION:
	                cancelSelection();
	                return;
	            case JOptionPane.YES_OPTION:
	            default:
	        }
	    }
	    super.approveSelection();
	}
}
