package jadex.base.gui.filetree;


import java.util.List;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JTree;

import jadex.base.gui.asynctree.AbstractSwingTreeNode;
import jadex.base.gui.asynctree.AsyncSwingTreeModel;
import jadex.base.gui.asynctree.ISwingTreeNode;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.types.filetransfer.FileData;

/**
 *  The remote file node.
 */
public class RemoteFileNode  extends AbstractSwingTreeNode	implements IFileNode
{
	//-------- attributes --------
	
	/** The file. */
	protected FileData file;
	
	/** The external access. */
	protected IExternalAccess exta;
	
	/** The icon cache. */
	protected final IIconCache	iconcache;
	
	/** The relative file name. */
//	protected String relative;
	
	/** The properties component (if any). */
//	protected ComponentProperties	propcomp;
		
	//-------- constructors --------
	
	/**
	 *  Create a new service container node.
	 */
	public RemoteFileNode(ISwingTreeNode parent, AsyncSwingTreeModel model, JTree tree, FileData file, IIconCache iconcache, IExternalAccess exta)
	{
		super(parent, model, tree);
		
		assert file!=null;
		
//		System.out.println("node: "+getClass()+" "+desc.getName());
		
		this.iconcache = iconcache;
		this.file = file;
		this.exta = exta;
//		this.relative = convertPathToRelative(file);
		
		model.registerNode(this);
	}
	
	//-------- AbstractComponentTreeNode methods --------
	
	/**
	 *  Get the id used for lookup.
	 */
	public Object	getId()
	{
		return file.toString();
	}

	/**
	 *  Get the icon for a node.
	 */
	public Icon	getSwingIcon()
	{
		return iconcache.getIcon(this);
	}
	
	/**
	 *  Get the icon as byte[] for a node.
	 */
	public byte[] getIcon()
	{
		return null;
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
	
	/**
	 *  Get the file name.
	 */
	public String	getFileName()
	{
		return file.getFilename();
	}
	
	/**
	 *  Get the file path.
	 */
	public String	getFilePath()
	{
		return file.getPath();
	}
	
	/**
	 *  Check if the file is a directory. 
	 */
	public boolean	isDirectory()
	{
		return file.isDirectory();
	}
	
	//-------- methods --------
	
	/**
	 *  Create a string representation.
	 */
	public String toString()
	{
		String	name	= file.getDisplayName();
		
		// For equally named path entries (e.g. 'classes') build larger name to differentiate (e.g. 'myapp/classes').
		if(getParent() instanceof RootNode)
		{
			int	idx	= -1;
			List	siblings	= getParent().getCachedChildren();
			for(int i=0; siblings!=null && i<siblings.size(); i++)
			{
				if(siblings.get(i)!=this)
				{
					FileData	sib	= ((RemoteFileNode)siblings.get(i)).getRemoteFile();
					if(sib.getDisplayName().equals(name) && !sib.getPath().endsWith(file.getPath()))
					{
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
			}
		}
		
//		if(true)
//		{
//			if(file.getFileSize()>0 && (!file.isDirectory() || file.getFilename().indexOf(".")!=-1)) // hmm zip files are dirs?
//			{
//				name += " ["+SUtil.bytesToString(file.getFileSize())+"]";
//			}
//		}
		return name;
	}
	
	/**
	 *  Get tooltip text.
	 */
	public String getTooltipText()
	{
		return file.getPath();
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
	public FileData getRemoteFile()
	{
		return file;
	}
	
	/**
	 *  Get the file size.
	 */
	public long getFileSize()
	{
		return file.getFileSize();
	}

//	
//	/**
//	 *  Get the relative path.
//	 */
//	public String	getRelativePath()
//	{
//		return this.relative;
//	}
	
//	/**
//	 *  Get the corresponding relative path for a file.
//	 *  Handles jars specially.
//	 */
//	protected String convertPathToRelative(File file)
//	{
//		String	ret;
//		if(file instanceof RemoteJarFile)
//		{
//			JarAsDirectory	jar	= (JarAsDirectory) file;
//			if(jar.getZipEntry()!=null)
//				ret	= jar.getZipEntry().getName();
//			else
//				ret	= SUtil.convertPathToRelative(jar.getJarPath());
//		}
//		else
//		{
//			ret	= file!=null ? SUtil.convertPathToRelative(file.getAbsolutePath()) : null;
//		}
//		return ret;
//	}
}