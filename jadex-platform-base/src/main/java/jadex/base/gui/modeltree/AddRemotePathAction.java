package jadex.base.gui.modeltree;

import jadex.base.gui.SwingDefaultResultListener;
import jadex.base.gui.SwingDelegationResultListener;
import jadex.base.gui.asynctree.ITreeNode;
import jadex.base.gui.filechooser.RemoteFileSystemView;
import jadex.base.gui.filetree.FileData;
import jadex.base.gui.filetree.FileTreePanel;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.SServiceProvider;
import jadex.bridge.service.library.ILibraryService;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.gui.SGUI;
import jadex.commons.gui.ToolTipAction;
import jadex.xml.annotation.XMLClassname;

import java.awt.event.ActionEvent;
import java.io.File;
import java.net.MalformedURLException;

import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.UIDefaults;
import javax.swing.filechooser.FileFilter;

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
	protected FileTreePanel treepanel;
	
	/** The file chooser. */
	protected JFileChooser filechooser;
	
	//-------- constructors --------
	
	/**
	 *  Create a new action.
	 */
	public AddRemotePathAction(FileTreePanel treepanel)
	{
		this(getName(), getIcon(), getTooltipText(), treepanel);
	}
	
	/**
	 *  Create a new action. 
	 */
	public AddRemotePathAction(String name, Icon icon, String desc, FileTreePanel treepanel)
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
		ITreeNode rm = (ITreeNode)treepanel.getTree().getLastSelectedPathComponent();
		return rm==null && treepanel.isRemote();
	}
	
	/**
	 *  Action performed.
	 */
	public void actionPerformed(ActionEvent e)
	{
//		final String filename = JOptionPane.showInputDialog("Enter remote path");
		
		// todo: move to constructor, currently produces nullpointer
		IFuture	chooseravailable;
		if(filechooser==null)
		{
			final Future	fut	= new Future();
			chooseravailable	= fut;
			final RemoteFileSystemView view = new RemoteFileSystemView(treepanel.getExternalAccess());
			view.init().addResultListener(new SwingDelegationResultListener(fut)
			{
				public void customResultAvailable(Object result)
				{
					filechooser = new JFileChooser(view.getCurrentDirectory(), view);
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
					fut.setResult(null);
				}
			});
		}
		else
		{
			chooseravailable	= IFuture.DONE;
		}
		
		chooseravailable.addResultListener(new SwingDefaultResultListener()
		{
			public void customResultAvailable(Object result)
			{
				if(filechooser.showDialog(SGUI.getWindowParent(treepanel), 
					"Add Remote Path")==JFileChooser.APPROVE_OPTION)
				{
					File file = filechooser.getSelectedFile();
					if(file!=null)
					{
						final String	path	= file.getAbsolutePath();
						treepanel.getExternalAccess().scheduleStep(new IComponentStep()
						{
							@XMLClassname("getRemoteFile")
							public Object execute(IInternalAccess ia)
							{
								final Future	ret	= new Future();
								SServiceProvider.getService(ia.getServiceContainer(), ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM)
									.addResultListener(ia.createResultListener(new DelegationResultListener(ret)
								{
									public void customResultAvailable(Object result)
									{
										ILibraryService ls = (ILibraryService)result;
										File	file	= new File(path);
//										File	f = new File(file.getParentFile(), file.getName());
//										URL url = f.toURI().toURL();
//										String filename = file.getAbsolutePath();
//										if((filename.endsWith("\\") || filename.endsWith("/")) && 
//											(!url.toString().endsWith("\\") || url.toString().endsWith("/")))
//										{
//											// Hack! f.toURI().toURL() does not append when file is not local
//											// and it cannot be determined if it is a directory
//											url = new URL(url.toString()+"/");
//										}
//										ls.addURL(url);
										try
										{
											ls.addURL(file.toURI().toURL());
											ret.setResult(new FileData(file));
										}
										catch(MalformedURLException mue)
										{
											ret.setException(mue);
										}
									}
								}));

								return ret;
							}
						}).addResultListener(new SwingDefaultResultListener()
						{
							public void customResultAvailable(Object result)
							{
								treepanel.addTopLevelNode((FileData)result);
							}
						});
					}
				}
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