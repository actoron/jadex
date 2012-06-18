package jadex.base.gui.filetree;

import jadex.base.gui.asynctree.AbstractTreeNode;
import jadex.base.gui.asynctree.AsyncTreeModel;
import jadex.base.gui.asynctree.ITreeNode;
import jadex.bridge.service.types.deployment.FileData;
import jadex.commons.SUtil;

import java.io.File;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.SwingUtilities;

/**
 *  Node object representing a service container.
 */
public class FileNode	extends AbstractTreeNode	implements IFileNode
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
		
	//-------- constructors --------
	
	/**
	 *  Create a new service container node.
	 */
	public FileNode(ITreeNode parent, AsyncTreeModel model, JTree tree, File file, IIconCache iconcache)
	{
		super(parent, model, tree);
		
		assert file!=null;
		
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
	 *  Get the icon for a node.
	 */
	public Icon	getIcon()
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
		String	name	= FileData.getDisplayName(file);
		
		// For equally named path entries (e.g. 'classes') build larger name to differentiate (e.g. 'myapp/classes').
		if(getParent() instanceof RootNode)
		{
			int	idx	= -1;
			List	siblings	= getParent().getCachedChildren();
			for(int i=0; siblings!=null && i<siblings.size(); i++)
			{
				if(siblings.get(i)!=this)
				{
					File	sib	= ((FileNode)siblings.get(i)).getFile();
					if(FileData.getDisplayName(sib).equals(name) && !sib.getPath().endsWith(file.getPath()))
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
		return name;
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
	protected String convertPathToRelative(File file)
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
		return file.isDirectory();
	}

}
