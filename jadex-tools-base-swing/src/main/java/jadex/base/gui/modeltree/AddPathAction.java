package jadex.base.gui.modeltree;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.UIDefaults;
import javax.swing.filechooser.FileFilter;

import jadex.commons.SUtil;
import jadex.commons.gui.SGUI;
import jadex.commons.gui.ToolTipAction;

/**
 *  Action for adding a local path.
 */
public class AddPathAction extends ToolTipAction
{
	//-------- constants --------
	
	/** The image icons. */
	protected static final UIDefaults icons = new UIDefaults(new Object[]
	{
		"addpath",	SGUI.makeIcon(ModelTreePanel.class, "/jadex/base/gui/images/add_folder424.png"),
	});
	
	//-------- attributes --------
	
	/** The tree. */
	protected ITreeAbstraction treepanel;
	
	/** The file chooser. */
	protected JFileChooser filechooser;
	
	//-------- constructors --------
	
	/**
	 *  Create a new action
	 */
	public AddPathAction(ITreeAbstraction treepanel)
	{
		this(getName(), getIcon(), getTooltipText(), treepanel);
	}
	
	/**
	 *  Create a new action 
	 */
	public AddPathAction(String name, Icon icon, String desc, ITreeAbstraction treepanel)
	{
		super(name, icon, desc);
		this.treepanel = treepanel;		
	}
	
	//-------- methods --------
	
	/**
	 *  Test if action is available in current context.
	 *  @return True, if available.
	 */
	public boolean isEnabled()
	{
		return !treepanel.isRemote();
//		ITreeNode rm = (ITreeNode)treepanel.getTree().getLastSelectedPathComponent();
//		return rm==null && !treepanel.isRemote();
	}
	
	/**
	 *  Action performed.
	 */
	public void actionPerformed(ActionEvent e)
	{
		if(filechooser==null)
		{
			filechooser = new JFileChooser(".");
			filechooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			filechooser.addChoosableFileFilter(new FileFilter()
			{
				public String getDescription()
				{
					return "Paths or .jar files";
				}
	
				public boolean accept(File f)
				{
					String name = f.getName().toLowerCase();
					return f.isDirectory() || name.endsWith(".jar");
				}
			});
		}
		
		if(filechooser.showDialog(SGUI.getWindowParent(treepanel.getTree())
			, "Add Path")==JFileChooser.APPROVE_OPTION)
		{
			File file = filechooser.getSelectedFile();
			if(file!=null)
			{
				// Handle common user error of double clicking the directory to add.
				if(!file.exists() && file.getParentFile().exists() && file.getParentFile().getName().equals(file.getName()))
					file	= file.getParentFile();
				if(file.exists())
				{
					// Convert to relative file for comparability with loaded nodes.
					file	= new File(SUtil.convertPathToRelative(file.getAbsolutePath()));
//					if(treepanel.getModel().getNode(file)==null)
					treepanel.action(file);
//					if(!treepanel.containsNode(file))
//					{
//						// Add file/directory to tree.
//						treepanel.add(file);
//					}
//					else
//					{
//						String	msg	= SUtil.wrapText("Path can not be added twice:\n"+file);
//						JOptionPane.showMessageDialog(SGUI.getWindowParent(treepanel.getTree()),
//							msg, "Duplicate path", JOptionPane.INFORMATION_MESSAGE);
//					}
				}
				else
				{
					String	msg	= SUtil.wrapText("Cannot find file or directory:\n"+file);
					JOptionPane.showMessageDialog(SGUI.getWindowParent(treepanel.getTree()),
						msg, "Cannot find file or directory", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}
	
	/**
	 *  Get the icon.
	 *  @return The icon.
	 */
	public static Icon getIcon()
	{
		return icons.getIcon("addpath");
	}
	
	/**
	 *  Get the name.
	 *  @return The name.
	 */
	public static String getName()
	{
		return "Add Path";
	}
	
	/**
	 *  Get the tooltip text.
	 *  @return The tooltip text.
	 */
	public static String getTooltipText()
	{
		return "Add a new directory path (package root) to the project structure";
	}
}
