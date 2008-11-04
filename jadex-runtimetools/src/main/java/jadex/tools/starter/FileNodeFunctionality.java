package jadex.tools.starter;

import jadex.bridge.IJadexAgentFactory;
import jadex.bridge.IJadexModel;
import jadex.commons.SGUI;
import jadex.commons.SUtil;
import jadex.tools.common.modeltree.FileNode;
import jadex.tools.common.modeltree.IExplorerTreeNode;
import jadex.tools.common.modeltree.INodeFunctionality;

import java.io.File;

import javax.swing.Icon;
import javax.swing.UIDefaults;

/**
 *
 */
public class FileNodeFunctionality implements INodeFunctionality
{
	//	-------- constants --------

	/**
	 * The image  for (m/r) elements.
	 */
	static UIDefaults icons = new UIDefaults(new Object[]
	{
		"agent", SGUI.makeIcon(FileNodeFunctionality.class, "/jadex/tools/common/images/new_agent.png"),
		"agent_broken", SGUI.makeIcon(FileNodeFunctionality.class, "/jadex/tools/common/images/new_agent_broken.png"),
		"capability", SGUI.makeIcon(FileNodeFunctionality.class, "/jadex/tools/common/images/new_capability_small.png"),
		"capability_broken", SGUI.makeIcon(FileNodeFunctionality.class, "/jadex/tools/common/images/new_capability_broken.png"),
		"src_folder", SGUI.makeIcon(FileNodeFunctionality.class, "/jadex/tools/common/images/new_src_folder.png"),
		"src_folder_broken", SGUI.makeIcon(FileNodeFunctionality.class, "/jadex/tools/common/images/new_src_folder_broken.png"),
		"src_jar", SGUI.makeIcon(FileNodeFunctionality.class, "/jadex/tools/common/images/new_src_jar.png"),
		"src_jar_broken", SGUI.makeIcon(FileNodeFunctionality.class, "/jadex/tools/common/images/new_src_jar_broken.png"),
		"package", SGUI.makeIcon(FileNodeFunctionality.class, "/jadex/tools/common/images/new_package.png"),
		"package_broken", SGUI.makeIcon(FileNodeFunctionality.class, "/jadex/tools/common/images/new_package_broken.png"),
		"java_file", SGUI.makeIcon(FileNodeFunctionality.class, "/jadex/tools/common/images/java_file.png"),
	
	});
	
	/**
	 *  Check if the node is valid.
	 *  @return True, is valid.
	 */
	public boolean check(IExplorerTreeNode node)
	{
		boolean valid = true;
		FileNode fn = (FileNode)node;
		
		//boolean	oldvalid	= this.valid;
		String	file	= fn.getFile().getAbsolutePath();
		
		// todo: other kernel extensions
		if(node.getAgentFactory().isLoadable(file))
		{
			try
			{
				// todo: remove Hack! Let tree always load fresh models when autorefresh is off
//				if(Configuration.getConfiguration().isModelCaching() && !Configuration.getConfiguration().isModelCacheAutoRefresh())
//					SXML.clearModelCache(file);

				IJadexModel model = node.getAgentFactory().loadModel(file);
				if(model!=null)
				{
					valid	= model.getReport().isEmpty();
				}
				// else unknown jadex file type -> ignore.
			}
			catch(Exception e)
			{
				valid	= false;
			}
		}
			
//		System.out.println("doCheck "+this+": "+oldvalid+"->"+valid);

		return valid;
	}
	
	/**
	 *  Perform the actual refresh.
	 *  Can be overridden by subclasses.
	 *  @return true, if the node has changed and needs to be checked.
	 */
	public boolean refresh(IExplorerTreeNode node)
	{
		FileNode fn = (FileNode)node;
		boolean	changed	= false;
		long newdate	= fn.getFile().lastModified();
		if(fn.getLastmodified()<newdate)
		{
			fn.setLastmodified(newdate);
			changed	= true;
		}
		
		return changed;
	}
	
	/**
	 *  Get the icon.
	 *  @return The icon.
	 */
	public Icon getIcon(IExplorerTreeNode node)
	{
		FileNode fn = (FileNode)node;
		Icon	icon	= null;
		boolean valid = fn.isValid() || !fn.getRootNode().isChecking();
		
		IJadexAgentFactory fac = node.getAgentFactory();
		//if(SXML.isAgentFilename(fn.getFile().getName()))
		if(fac.isLoadable(fn.getFile().getName()) && fac.isStartable(fn.getFile().getName()))
		{
			icon	= icons.getIcon(valid? "agent" : "agent_broken");
		}
		//else if(SXML.isCapabilityFilename(fn.getFile().getName()))
		else if(fac.isLoadable(fn.getFile().getName()))
		{
			icon	= icons.getIcon(valid? "capability" : "capability_broken");
		}
		else if(SUtil.isJavaSourceFilename(fn.getFile().getName()))
		{
			icon	= icons.getIcon("java_file");
		}
		return icon;
	}
	
	/**
	 *  Create a new child node.
	 *  @param file The file for the new child node.
	 *	@return The new node.
	 */
	public IExplorerTreeNode createNode(IExplorerTreeNode node, File file)
	{
		return null;
	}
}
