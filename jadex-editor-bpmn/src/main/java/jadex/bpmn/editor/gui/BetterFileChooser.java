package jadex.bpmn.editor.gui;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *  An improved JFileChooser class.
 *
 */
public class BetterFileChooser extends JFileChooser
{
	/**
	 *  Creates a new file chooser.
	 *  @param location A suggested starting location (may be a file).
	 */
	public BetterFileChooser(File location)
	{
		while (location != null && !location.isDirectory())
		{
			location = location.getParentFile();
		}
		
		if (location == null)
		{
			String locstr = System.getProperty("user.dir");
			if (locstr != null)
			{
				location = new File(locstr);
			}
		}
		
		if (location != null)
		{
			setCurrentDirectory(location);
		}
	}
	
	/**
	 *  Sets a new file filter.
	 */
	public void setFileFilter(FileFilter filter)
	{
		File sfile = getSelectedFile();
		if (sfile != null && filter instanceof FileNameExtensionFilter)
		{
			FileFilter[] filters = getChoosableFileFilters();
			for (FileFilter fltr : filters)
			{
				if (fltr instanceof FileNameExtensionFilter && sfile.getPath().endsWith(((FileNameExtensionFilter) fltr).getExtensions()[0]))
				{
					if (fltr != filter)
					{
						String newpath = sfile.getPath();
						newpath = newpath.substring(0, newpath.length() - ((FileNameExtensionFilter) fltr).getExtensions()[0].length());
						newpath = newpath + ((FileNameExtensionFilter) filter).getExtensions()[0];
						setSelectedFile(new File(newpath));
					}
					break;
				}
			}
		}
		super.setFileFilter(filter);
	};
	
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
	    String ext = "";
	    if (getFileFilter() instanceof FileNameExtensionFilter)
	    {
	    	ext = ((FileNameExtensionFilter) getFileFilter()).getExtensions()[0];
	    }
	    File altfile = new File(file.getAbsolutePath() + "." + ext);
	    
	    // Add overwrite dialog.
	    if(getDialogType() == JFileChooser.SAVE_DIALOG && (file.exists() || altfile.exists()))
	    {
	    	if (!file.exists() && altfile.exists())
	    	{
	    		file = altfile;
	    	}
	    	
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
