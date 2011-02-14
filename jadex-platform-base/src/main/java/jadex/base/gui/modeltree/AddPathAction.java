package jadex.base.gui.modeltree;

import jadex.base.gui.asynctree.ITreeNode;
import jadex.base.gui.filetree.FileTreePanel;
import jadex.commons.SUtil;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.gui.SGUI;
import jadex.commons.gui.ToolTipAction;
import jadex.commons.service.RequiredServiceInfo;
import jadex.commons.service.SServiceProvider;
import jadex.commons.service.library.ILibraryService;

import java.awt.event.ActionEvent;
import java.io.File;
import java.net.MalformedURLException;

import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.UIDefaults;
import javax.swing.filechooser.FileFilter;

/**
 * 
 */
public class AddPathAction extends ToolTipAction
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
	public AddPathAction(FileTreePanel treepanel)
	{
		this(getName(), getIcon(), getTooltipText(), treepanel);
	}
	
	/**
	 * 
	 */
	public AddPathAction(String name, Icon icon, String desc, FileTreePanel treepanel)
	{
		super(name, icon, desc);
		this.treepanel = treepanel;
		
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
	
	/**
	 *  Test if action is available in current context.
	 *  @return True, if available.
	 */
	public boolean isEnabled()
	{
		ITreeNode rm = (ITreeNode)treepanel.getTree().getLastSelectedPathComponent();
		return rm==null && !treepanel.isRemote();
	}
	
	/**
	 * 
	 */
	public void actionPerformed(ActionEvent e)
	{
		if(filechooser.showDialog(SGUI.getWindowParent(treepanel)
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
					// Add file/directory to tree.
//						ITreeNode	node	= getModel().getRoot().addPathEntry(file);
					
					// todo: jars
					if(treepanel.getExternalAccess()!=null)
					{
						final File fcopy = file;
						SServiceProvider.getService(treepanel.getExternalAccess().getServiceProvider(), ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM).addResultListener(new DefaultResultListener()
						{
							public void resultAvailable(Object result)
							{
								ILibraryService ls = (ILibraryService)result;
								File f = new File(fcopy.getParentFile(), fcopy.getName());
								try
								{
//									System.out.println("adding:"+f.toURI().toURL());
									ls.addURL(f.toURI().toURL());
								}
								catch(MalformedURLException ex)
								{
									ex.printStackTrace();
								}
							}
						});
					}
					
					treepanel.addTopLevelNode(file);
//					final RootNode root = (RootNode)getModel().getRoot();
//					ITreeNode node = createNode(root, model, tree, file, iconcache, filefilter, exta);
//					root.addChild(node);
				}
				else
				{
					String	msg	= SUtil.wrapText("Cannot find file or directory:\n"+file);
					JOptionPane.showMessageDialog(SGUI.getWindowParent(treepanel),
						msg, "Cannot find file or directory", JOptionPane.ERROR_MESSAGE);
				}
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
		return "Add Path";
	}
	
	/**
	 * 
	 */
	public static String getTooltipText()
	{
		return "Add a new directory path (package root) to the project structure";
	}
}
