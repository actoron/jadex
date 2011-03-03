package jadex.base.gui.modeltree;

import jadex.base.gui.asynctree.ITreeNode;
import jadex.base.gui.filechooser.RemoteFile;
import jadex.base.gui.filechooser.RemoteFileSystemView;
import jadex.base.gui.filetree.FileData;
import jadex.base.gui.filetree.FileTreePanel;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.gui.SGUI;
import jadex.commons.gui.ToolTipAction;
import jadex.commons.service.RequiredServiceInfo;
import jadex.commons.service.SServiceProvider;
import jadex.commons.service.library.ILibraryService;

import java.awt.event.ActionEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.UIDefaults;
import javax.swing.filechooser.FileFilter;

/**
 * 
 */
public class AddRemotePathAction extends ToolTipAction
{
	/** The image icons. */
	protected static final UIDefaults icons = new UIDefaults(new Object[]
	{
		"addpath",	SGUI.makeIcon(ModelTreePanel.class, "/jadex/base/gui/images/new_addfolder.png"),
	});
	
	/** The tree. */
	protected FileTreePanel treepanel;
	
	/** The file chooser. */
	protected JFileChooser filechooser;
	
	/**
	 * 
	 */
	public AddRemotePathAction(FileTreePanel treepanel)
	{
		this(getName(), getIcon(), getTooltipText(), treepanel);
	}
	
	/**
	 * 
	 */
	public AddRemotePathAction(String name, Icon icon, String desc, FileTreePanel treepanel)
	{
		super(name, icon, desc);
		this.treepanel = treepanel;
		
		
	}
	
	/**
	 *  Test if action is available in current context.
	 *  @return True, if available.
	 */
	public boolean isEnabled()
	{
		ITreeNode rm = (ITreeNode)treepanel.getTree().getLastSelectedPathComponent();
		return rm==null && treepanel.isRemote();
	}
	
	/**
	 * 
	 */
	public void actionPerformed(ActionEvent e)
	{
//		final String filename = JOptionPane.showInputDialog("Enter remote path");
		
		// todo: move to constructor, currently produces nullpointer
		if(filechooser==null)
		{
			RemoteFileSystemView view = new RemoteFileSystemView(treepanel.getExternalAccess());
			filechooser = new JFileChooser(view);
			view.setFileChooser(filechooser);
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
		
		if(filechooser.showDialog(SGUI.getWindowParent(treepanel), 
			"Add Remote Path")==JFileChooser.APPROVE_OPTION)
		{
			final File file = filechooser.getSelectedFile();
			if(file!=null)
			{
				SServiceProvider.getService(treepanel.getExternalAccess().getServiceProvider(), ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM)
					.addResultListener(new DefaultResultListener()
				{
					public void resultAvailable(Object result)
					{
						ILibraryService ls = (ILibraryService)result;
						File f = new File(file.getParentFile(), file.getName());
						try
						{
							URL url = f.toURI().toURL();
							String filename = file.getAbsolutePath();
							if((filename.endsWith("\\") || filename.endsWith("/")) && 
								(!url.toString().endsWith("\\") || url.toString().endsWith("/")))
							{
								// Hack! f.toURI().toURL() does not append when file is not local
								// and it cannot be determined if it is a directory
								url = new URL(url.toString()+"/");
							}
							ls.addURL(url);
						}
						catch(MalformedURLException ex)
						{
							ex.printStackTrace();
						}
					}
				});
				
				treepanel.addTopLevelNode(new FileData(file));
//				final RootNode root = (RootNode)getModel().getRoot();
//				ITreeNode node = ModelTreePanel.createNode(root, model, tree, new RemoteFile(file.getName(), file.getAbsolutePath(), file.isDirectory()), iconcache, filefilter, exta);
//				root.addChild(node);
			}
		}
	}
	
	/**
	 * 
	 */
	public static Icon getIcon()
	{
		return icons.getIcon("addpath");
	}
	
	/**
	 * 
	 */
	public static String getName()
	{
		return "Add Remote Path";
	}
	
	/**
	 * 
	 */
	public static String getTooltipText()
	{
		return "Add a new remote directory path (package root) to the project structure";
	}
}