package jadex.tools.testcenter;

import jadex.bdi.interpreter.OAVAgentModel;
import jadex.bdi.interpreter.OAVBDIMetaModel;
import jadex.bridge.IJadexAgentFactory;
import jadex.bridge.IJadexModel;
import jadex.commons.SGUI;
import jadex.rules.state.IOAVState;
import jadex.tools.common.modeltree.FileNode;
import jadex.tools.common.modeltree.IExplorerTreeNode;
import jadex.tools.common.modeltree.INodeFunctionality;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.Icon;
import javax.swing.UIDefaults;

/**
 *
 */
public class FileNodeFunctionality implements INodeFunctionality
{
	//-------- constants --------

	/**
	 * The image  for (m/r) elements.
	 */
	static UIDefaults icons = new UIDefaults(new Object[]
	{
		"agent", SGUI.makeIcon(FileNode.class, "/jadex/tools/common/images/new_agent.png"),
		"agent_testable", SGUI.makeIcon(FileNode.class, "/jadex/tools/common/images/new_agent_testable.png"),
	});
	
	/**
	 *  Check if the node is valid.
	 *  @return True, is valid.
	 */
	public boolean check(IExplorerTreeNode node)
	{
		boolean	valid	= false;
		FileNode fn = (FileNode)node; 
		String	file	= fn.getFile().getAbsolutePath();
		IJadexAgentFactory fac = node.getAgentFactory();
		if(fac.isLoadable(file))
		{
			// todo: remove Hack! Let tree always load fresh models when autorefresh is off
//			if(Configuration.getConfiguration().isModelCaching() && !Configuration.getConfiguration().isModelCacheAutoRefresh())
//				SXML.clearModelCache(file);
				
			IJadexModel model = node.getAgentFactory().loadModel(file);
			
			if(model!=null)
			{
				boolean ok	= model.getReport().isEmpty();

				// HACK!!!
//				if(ok && model instanceof IMBDIAgent)
//				{
//					IMCapabilityReference[] caprefs = ((IMBDIAgent)model).getCapabilityReferences();
//					for(int i=0; !valid && i<caprefs.length; i++)
//					{
//						String name = caprefs[i].getCapability().getFullName();
//						valid = name.equals("jadex.planlib.Test");
//					}
//				}

				// HACK!!!
				// else
				if(ok && model instanceof OAVAgentModel)
				{
					IOAVState	state	= ((OAVAgentModel)model).getState();
					Object	magent	= ((OAVAgentModel)model).getHandle();
					Collection	caparefs	= state.getAttributeValues(magent, OAVBDIMetaModel.capability_has_capabilityrefs);
					if(caparefs!=null)
					{
						for(Iterator it=caparefs.iterator(); !valid && it.hasNext(); )
						{
							Object	name	= state.getAttributeValue(it.next(), OAVBDIMetaModel.capabilityref_has_file);
							valid = "jadex.bdi.planlib.test.Test".equals(name);
						}
					}
				}
			}
			// else unknown jadex file type -> ignore.
		}

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
		long	newdate	= fn.getFile().lastModified();
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
		Icon	icon	= null;
		FileNode fn = (FileNode)node;
		//if(SXML.isAgentFilename(fn.getFile().getName()))
		// Hack! todo
		if(fn.getFile().getName().endsWith(".agent.xml"))
		{
			icon	= icons.getIcon(fn.isValid()? "agent_testable": "agent");
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
