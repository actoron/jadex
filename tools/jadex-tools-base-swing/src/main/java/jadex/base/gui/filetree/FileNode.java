package jadex.base.gui.filetree;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.SwingUtilities;

import jadex.base.JarAsDirectory;
import jadex.base.gui.asynctree.AbstractSwingTreeNode;
import jadex.base.gui.asynctree.AsyncSwingTreeModel;
import jadex.base.gui.asynctree.ISwingTreeNode;
import jadex.bridge.service.types.filetransfer.FileData;
import jadex.commons.SUtil;

/**
 *  Node object representing a service container.
 */
public class FileNode	extends AbstractSwingTreeNode	implements IFileNode
{
	//-------- attributes --------
	
	/** The file. */
	protected File file;
	
	/** The icon cache. */
	protected final IIconCache	iconcache;
	
	/** The relative file name. */
	protected String relative;
	
	/** The properties component (if any). */
//	protected ComponentProperties	propcomp;
	
	/** The cached display name. */
	protected String displayname;
	protected String tostring;
	
	/** The last siblings. */
	protected List	lastsiblings;
		
	//-------- constructors --------
	
	/**
	 *  Create a new service container node.
	 */
	public FileNode(ISwingTreeNode parent, AsyncSwingTreeModel model, JTree tree, File file, IIconCache iconcache)
	{
		super(parent, model, tree);
		
//		assert file!=null;
		
//		System.out.println("node: "+getClass()+" "+desc.getName());
		
		this.iconcache = iconcache;
		this.file = file;
		this.relative = convertPathToRelative(file);
		
		model.registerNode(this);
	}
	
	//-------- AbstractComponentTreeNode methods --------
	
	/**
	 *  Get the id used for lookup.
	 */
	public Object	getId()
	{
		// cannot use getAbsolutePath() due to JarAsDirectory, which produces file:jar:...
		return file;//.getAbsolutePath();
	}

	/**
	 *  Get the icon as byte[] for a node.
	 */
	public byte[] getIcon()
	{
		return null;
	}

	/**
	 *  Get the icon for a node.
	 */
	public Icon	getSwingIcon()
	{
		return iconcache.getIcon(this);
	}
	
	/**
	 *  Refresh the node.
	 *  @param recurse	Recursively refresh subnodes, if true.
	 */
	public void refresh(boolean recurse)
	{
//		cms.getComponentDescription(desc.getName()).addResultListener(new SwingDefaultResultListener()
//		{
//			public void customResultAvailable(Object result)
//			{
//				FileTreeNode.this.desc	= (IComponentDescription)result;
//				getModel().fireNodeChanged(FileTreeNode.this);
//			}
//			public void customExceptionOccurred(Exception exception)
//			{
//				// ignore
//			}
//		});

		super.refresh(recurse);
	}
	
	/**
	 *  Asynchronously search for children.
	 *  Should call setChildren() once children are found.
	 */
	protected void	searchChildren()
	{
	}
	
	//-------- methods --------
	
	/**
	 *  Create a string representation.
	 */
	public String toString()
	{
		String name	= tostring==null? getDisplayName(): tostring;
		
		// For equally named path entries (e.g. 'classes') build larger name to differentiate (e.g. 'myapp/classes').
		if(getParent() instanceof RootNode)
		{
			int	idx	= -1;
			List siblings = getParent().getCachedChildren();
			if(lastsiblings==null || !lastsiblings.equals(siblings))
			{
//				System.out.println("check: "+file.getAbsolutePath());
				lastsiblings = siblings!=null ? new ArrayList(siblings) : new ArrayList();
				
				for(int i=0; siblings!=null && i<siblings.size(); i++)
				{
					if(siblings.get(i)!=this && siblings.get(i) instanceof FileNode)
					{
						File	sib	= ((FileNode)siblings.get(i)).getFile();
//						System.out.println("vs: "+file+", "+sib);
						if(((FileNode)siblings.get(i)).getDisplayName().equals(getDisplayName()) && !sib.getPath().endsWith(file.getPath()))
						{
//							System.out.println("vs1: "+file+", "+sib);
							int	tmp	= Math.max(file.getPath().lastIndexOf("/"), file.getPath().lastIndexOf("\\"))+1;
							while(sib.getPath().endsWith(file.getPath().substring(tmp)))
							{
								tmp	= Math.max(file.getPath().lastIndexOf("/", tmp-2), file.getPath().lastIndexOf("\\", tmp-2))+1;
							}
							idx	= idx==-1 ? tmp : Math.min(idx, tmp);
						}
					}
				}
				
				if(idx>-1)
				{
					name	= file.getPath().substring(idx);
					tostring = name;
				}
			}
		}
		
//			if(true)
//			{
//				try
//				{
//					if(file.length()>0 && (!file.isDirectory() || file.getName().indexOf(".")!=-1)) // hmm zip files are dirs?
//					{
//						name += " ["+SUtil.bytesToString(file.length())+"]";
//					}
//				}
//				catch(Exception e)
//				{
//				}
//			}
		return name;
	}
	
	/**
	 *  Get the display name.
	 */
	public String	getDisplayName()
	{
		if(displayname==null)
		{
			displayname	= FileData.getDisplayName(file);
		}
		return displayname;
	}
	
	/**
	 *  Get tooltip text.
	 */
	public String getTooltipText()
	{
		return file.getAbsolutePath();
	}

	/**
	 *  True, if the node has properties that can be displayed.
	 */
	public boolean	hasProperties()
	{
		return false;
//		return true;
	}
	
	/**
	 *  Get or create a component displaying the node properties.
	 *  Only to be called if hasProperties() is true;
	 */
	public JComponent	getPropertiesComponent()
	{
		return null;
//		if(propcomp==null)
//		{
//			propcomp	= new ComponentProperties();
//		}
//		propcomp.setDescription(desc);
//		return propcomp;
	}

	/**
	 *  Get the file.
	 *  @return the file.
	 */
	public File getFile()
	{
		return file;
	}
	
	/**
	 *  Get the relative path.
	 */
	public String	getRelativePath()
	{
		return this.relative;
	}
	
	/**
	 *  Get the corresponding relative path for a file.
	 *  Handles jars specially.
	 */
	protected static String convertPathToRelative(File file)
	{
		String	ret;
		if(file instanceof JarAsDirectory)
		{
			JarAsDirectory	jar	= (JarAsDirectory)file;
			if(jar.getZipEntry()!=null)
				ret	= jar.getZipEntry().getName();
			else
				ret	= SUtil.convertPathToRelative(jar.getJarPath());
		}
		else
		{
			ret	= file!=null ? SUtil.convertPathToRelative(file.getAbsolutePath()): null;
		}
		return ret;
	}
	
	/**
	 *  Check if the node is a leaf.
	 */
	public boolean	isLeaf()
	{
		assert SwingUtilities.isEventDispatchThread();

		return true;
	}
	
	
	/**
	 *  Get the file name.
	 */
	public String	getFileName()
	{
		return file.getName();
	}
	
	/**
	 *  Get the file path.
	 */
	public String	getFilePath()
	{
		return file.getAbsolutePath();		
	}

	
	/**
	 *  Check if the file is a directory. 
	 */
	public boolean	isDirectory()
	{
//		System.out.println("isDir: "+file.getAbsolutePath()+" "+file.isDirectory());
		return false;
	}
	
	/**
	 *  Get the file size.
	 */
	public long getFileSize()
	{
		return isDirectory() ? 0 : file.length();
	}

}
