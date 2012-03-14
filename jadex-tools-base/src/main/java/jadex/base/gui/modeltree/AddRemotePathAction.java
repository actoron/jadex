package jadex.base.gui.modeltree;

import jadex.base.gui.asynctree.ITreeNode;
import jadex.base.gui.filechooser.RemoteFileSystemView;
import jadex.base.gui.filetree.FileTreePanel;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.types.deployment.FileData;
import jadex.commons.SUtil;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.gui.SGUI;
import jadex.commons.gui.ToolTipAction;
import jadex.commons.gui.future.SwingDefaultResultListener;
import jadex.commons.gui.future.SwingDelegationResultListener;
import jadex.xml.annotation.XMLClassname;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
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
					JPanel pan = new JPanel(new GridBagLayout());
					JButton refresh = new JButton("Refresh");
					pan.add(refresh, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.NORTHEAST,
						GridBagConstraints.NONE, new Insets(0,0,0,0), 0, 0));
					filechooser.setAccessory(pan);
					refresh.addActionListener(new ActionListener()
					{
						public void actionPerformed(ActionEvent e)
						{
							view.clearCache();
							filechooser.rescanCurrentDirectory();
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
//						final String	path	= file.getAbsolutePath();
						final String	path	= file.getPath();
						
						treepanel.getExternalAccess().scheduleStep(new IComponentStep<FileData>()
						{
							@XMLClassname("getRemoteFile")
							public IFuture<FileData> execute(IInternalAccess ia)
							{
								return new Future<FileData>(new FileData(new File(SUtil.convertPathToRelative(path))));
							}
						}).addResultListener(new SwingDefaultResultListener<FileData>()
						{
							public void customResultAvailable(FileData result)
							{
								if(treepanel.getModel().getNode(result.toString())==null)
								{
									treepanel.addTopLevelNode(result);
								}
								else
								{
									// Todo: already added to library service (remove?)
									String	msg	= SUtil.wrapText("Path can not be added twice:\n"+((FileData)result).getPath());
									JOptionPane.showMessageDialog(SGUI.getWindowParent(treepanel),
										msg, "Duplicate path", JOptionPane.INFORMATION_MESSAGE);
								}
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