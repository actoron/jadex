package jadex.base.gui.modeltree;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.UIDefaults;
import javax.swing.filechooser.FileFilter;

import jadex.base.gui.RemoteFileChooser;
import jadex.bridge.service.types.filetransfer.FileData;
import jadex.commons.future.IFuture;
import jadex.commons.gui.SGUI;
import jadex.commons.gui.ToolTipAction;
import jadex.commons.gui.future.SwingDefaultResultListener;

/**
 *  Action for adding a remote path. 
 */
public class AddRemotePathAction extends ToolTipAction
{
	//-------- constants --------
	
	/** The image icons. */
	protected static final UIDefaults icons = new UIDefaults(new Object[]
	{
		"addpath",	SGUI.makeIcon(ModelTreePanel.class, "/jadex/base/gui/images/new_addfolder.png"),
	});
	
	//-------- attributes --------
	
	/** The tree. */
//	protected FileTreePanel treepanel;
	protected ITreeAbstraction treepanel;
	
	/** The file chooser. */
	protected RemoteFileChooser filechooser;
	
	//-------- constructors --------
	
	/**
	 *  Create a new action.
	 */
	public AddRemotePathAction(ITreeAbstraction treepanel)
	{
		this(getName(), getIcon(), getTooltipText(), treepanel);
	}
	
	/**
	 *  Create a new action. 
	 */
	public AddRemotePathAction(String name, Icon icon, String desc, ITreeAbstraction treepanel)
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
		return treepanel.isRemote();
//		Object rm = treepanel.getTree().getLastSelectedPathComponent();
//		return rm==null && treepanel.isRemote();
	}
	
	/**
	 *  Action performed.
	 */
	public void actionPerformed(ActionEvent e)
	{
		if(filechooser==null)
		{
			filechooser	= new RemoteFileChooser(treepanel.getExternalAccess());
		}
	
		IFuture<FileData>	file	= filechooser.chooseFile("Add Remote Path", null, treepanel.getTree(), JFileChooser.FILES_AND_DIRECTORIES, new FileFilter()
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
		
		file.addResultListener(new SwingDefaultResultListener<FileData>()
		{
			public void customResultAvailable(FileData result)
			{
				treepanel.action(result);
				
////				if(treepanel.getModel().getNode(result.toString())==null)
//				if(!treepanel.containsNode(result.toString()))
//				{
////					treepanel.addTopLevelNode(result);
//					treepanel.action(result);
//				}
//				else
//				{
//					// Todo: already added to library service (remove?)
//					String	msg	= SUtil.wrapText("Path can not be added twice:\n"+((FileData)result).getPath());
//					JOptionPane.showMessageDialog(SGUI.getWindowParent(treepanel.getTree()),
//						msg, "Duplicate path", JOptionPane.INFORMATION_MESSAGE);
//				}
			}
		});
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
		return "Add Remote Path";
	}
	
	/**
	 *  Get the tooltip text.
	 *  @return The tooltip text.
	 */
	public static String getTooltipText()
	{
		return "Add a new remote directory path (package root) to the project structure";
	}
}