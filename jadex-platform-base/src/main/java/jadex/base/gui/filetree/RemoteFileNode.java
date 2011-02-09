package jadex.base.gui.filetree;

import jadex.base.gui.asynctree.AbstractTreeNode;
import jadex.base.gui.asynctree.AsyncTreeModel;
import jadex.base.gui.asynctree.ITreeNode;
import jadex.bridge.IExternalAccess;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JTree;

/**
 * 
 */
public class RemoteFileNode  extends AbstractTreeNode
{
	//-------- attributes --------
	
	/** The file. */
	protected RemoteFile file;
	
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
	public RemoteFileNode(ITreeNode parent, AsyncTreeModel model, JTree tree, RemoteFile file, IIconCache iconcache, IExternalAccess exta)
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
		return file.getFilename();
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
	public RemoteFile getRemoteFile()
	{
		return file;
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