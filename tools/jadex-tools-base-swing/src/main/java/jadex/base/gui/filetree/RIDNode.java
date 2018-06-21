package jadex.base.gui.filetree;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JTree;

import jadex.base.JarAsDirectory;
import jadex.base.gui.asynctree.AbstractSwingTreeNode;
import jadex.base.gui.asynctree.AsyncSwingTreeModel;
import jadex.base.gui.asynctree.ISwingTreeNode;
import jadex.bridge.ILocalResourceIdentifier;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.ResourceIdentifier;
import jadex.commons.IAsyncFilter;
import jadex.commons.collection.SortedList;
import jadex.commons.future.CollectionResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.gui.future.SwingDefaultResultListener;

/**
 *  Node for rid based jar or local artifact directory.
 *  
 *  todo: move to modeltree?!
 */
public class RIDNode extends AbstractSwingTreeNode implements IFileNode
{
	//-------- attributes --------
	
	/** The file. */
	protected File file;
	
	/** The icon cache. */
	protected final IIconCache	iconcache;
	
	/** The relative file name. */
//	protected String relative;
	
	/** The resource identifier. */
	protected IResourceIdentifier rid;
	
	// hack: should belong to the model
	/** The factory. */
	protected INodeFactory factory;

	
	//-------- constructors --------

	/**
	 *  Create a new service container node.
	 */
	public RIDNode(ISwingTreeNode parent, AsyncSwingTreeModel model, JTree tree, IResourceIdentifier rid, IIconCache iconcache, INodeFactory factory)
	{
		super(parent, model, tree);
		this.rid = rid;
		
		this.iconcache = iconcache;
//		this.relative = FileNode.convertPathToRelative(file);
		this.factory = factory;
		
		model.registerNode(this);
//		System.out.println("node: "+getClass()+" "+desc.getName());
	}
	
	//-------- methods --------
	
	/**
	 *  Set the file.
	 */
	public void setFile(File file)
	{
		this.file = file instanceof JarAsDirectory? file: new JarAsDirectory(file.getPath());
	}
	
	/**
	 *  Get the rid.
	 *  @return The rid.
	 */
	public IResourceIdentifier getResourceIdentifier()
	{
		return rid;
	}

	/**
	 *  Get the file name.
	 */
	public String	getFileName()
	{
		return file!=null? file.getName(): rid.toString();
	}
	
	/**
	 *  Get the file path.
	 */
	public String	getFilePath()
	{
		return file!=null? file.getAbsolutePath(): rid.toString();
//		return rid.getGlobalIdentifier();
	}
	
	/**
	 *  Check if the file is a directory. 
	 */
	public boolean	isDirectory()
	{
		return true;
	}
	
	/**
	 *  Get the id used for lookup.
	 */
	public Object	getId()
	{
		return rid;
	}
	
	/**
	 *  Get tooltip text.
	 */
	public String getTooltipText()
	{
		String	ret;
		if(ResourceIdentifier.isHashGid(rid))
		{
			ret = file.getAbsolutePath() + "("+rid.getGlobalIdentifier().getResourceId()+")";
		}
		else
		{
			ret = rid.toString();
			if(file!=null)
				ret += "file: "+file.getAbsolutePath();
		}
		return ret;
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
	 *  Test if jar.
	 */
	public boolean isJar()
	{
		return file!=null && file instanceof JarAsDirectory;
		// file.getAbsolutePath().endsWith(".jar")
	}
	
	/**
	 *  Asynchronously search for children.
	 *  Should call setChildren() once children are found.
	 */
	protected void	searchChildren()
	{
		if(file!=null)
		{
			if(file instanceof JarAsDirectory)
			{
				((JarAsDirectory)file).refresh();
			}
			
			listFiles().addResultListener(new SwingDefaultResultListener()
			{
				public void customResultAvailable(Object result)
				{
					Collection files = (Collection)result;
					
					List	nodes	= new SortedList(DirNode.FILENODE_COMPARATOR, true);
//						List	nodes	= new ArrayList();
					for(Iterator it=files.iterator(); it.hasNext();)
					{
						File file = (File)it.next();
						ISwingTreeNode node = getModel().getNode(file);//.getAbsolutePath());
						if(node!=null)
						{
//							lis.resultAvailable(node);
							nodes.add(node);
						}
						else
						{
//								lis.resultAvailable(ModelTreePanel.createNode(DirNode.this, model, tree, file, iconcache, filter, null));
							nodes.add(factory.createNode(RIDNode.this, getModel(), tree, file, iconcache, null, factory));
						}
					}

					setChildren(nodes);
				}
			});
		}
	}
	
	/**
	 *	Get a file filter according to current file type settings. 
	 */
	protected IFuture listFiles()
	{
		if(file==null)
			return new Future(new ArrayList());
		
		final Future ret = new Future();
		final File[] files = file.listFiles();
		final CollectionResultListener lis = new CollectionResultListener(files==null? 0: files.length, true, new DelegationResultListener(ret));
		
		if(files!=null)
		{
//			System.out.println("name: "+toString()+" files length: "+files.length);
			for(int i=0; i<files.length; i++)
			{
				IAsyncFilter	filter	= factory.getFileFilter();
				if(filter==null)
				{
					lis.resultAvailable(files[i]);
				}
				else
				{
					final File file = files[i];
//					System.out.println("in: "+file);
					filter.filter(files[i]).addResultListener(new IResultListener()
					{
						public void resultAvailable(Object result)
						{
//							System.out.println("out: "+file);
							if(((Boolean)result).booleanValue())
								lis.resultAvailable(file);
							else
								lis.exceptionOccurred(null);
						}
						
						public void exceptionOccurred(Exception exception)
						{
//							System.out.println("out: "+file);
							lis.exceptionOccurred(null);
						}
					});
				}
			}
		}
		return ret;
	}

	/**
	 *  Get the string representation.
	 */
	public String toString()
	{
		String	ret;
		if(ResourceIdentifier.isHashGid(rid))
		{
			ret = rid.getGlobalIdentifier().getResourceId();
		}
		else
		{
			ILocalResourceIdentifier lrid = rid.getLocalIdentifier();
			ret = lrid.getUri().getPath();
			if(ret.indexOf('/')!=-1)
			{
				ret	= ret.substring(ret.lastIndexOf('/'));
			}
		}

		return ret;
	}
	
	/**
	 *  Get the file size.
	 */
	public long getFileSize()
	{
		return file==null? 0: file.length();
	}
	
}
