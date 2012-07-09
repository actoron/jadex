package jadex.base.gui.modeltree;

import jadex.base.gui.asynctree.ITreeNode;
import jadex.base.gui.filetree.FileTreePanel;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.ResourceIdentifier;
import jadex.commons.SReflect;
import jadex.commons.concurrent.ThreadPool;
import jadex.commons.gui.SGUI;
import jadex.commons.gui.ToolTipAction;

import java.awt.event.ActionEvent;
import java.lang.reflect.Method;

import javax.swing.Icon;
import javax.swing.JOptionPane;
import javax.swing.UIDefaults;

/**
 *  Action for adding a local path.
 */
public class AddRIDAction extends ToolTipAction
{
	//-------- constants --------
	
	/** The image icons. */
	protected static final UIDefaults icons = new UIDefaults(new Object[]
	{
		"addrid",	SGUI.makeIcon(ModelTreePanel.class, "/jadex/base/gui/images/folder24.png"),
	});
	
	//-------- attributes --------
	
	/** The tree. */
	protected FileTreePanel treepanel;
	
	//-------- constructors --------
	
	/**
	 *  Create a new action
	 */
	public AddRIDAction(FileTreePanel treepanel)
	{
		this(getName(), getIcon(), getTooltipText(), treepanel);
	}
	
	/**
	 *  Create a new action 
	 */
	public AddRIDAction(String name, Icon icon, String desc, FileTreePanel treepanel)
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
		Class cl = SReflect.findClass0("jadex.base.service.dependency.maven.MavenDependencyResolverService", null, null);
		return cl!=null && (ITreeNode)treepanel.getTree().getLastSelectedPathComponent()==null && !treepanel.isRemote();
//		return (ITreeNode)treepanel.getTree().getLastSelectedPathComponent()==null && !treepanel.isRemote();
	}
	
	/**
	 *  Action performed.
	 */
	public void actionPerformed(ActionEvent e)
	{
		try
		{
			String gid = null;
//			String gid = JOptionPane.showInputDialog(treepanel, "Enter Artifact Id:");
			Class cl = SReflect.findClass0("jadex.base.gui.reposearch.RepositorySearchPanel", null, null);
			if(cl!=null)
			{
				Method m = cl.getMethod("showDialog", new Class[]{ThreadPool.class});
				Object o = m.invoke(null, new Object[]{null});
//				System.out.println("sel ai: "+o);
				if(o!=null)
				{
					String grid = (String)o.getClass().getField("groupId").get(o);
					String arid = (String)o.getClass().getField("artifactId").get(o);
					String ver = (String)o.getClass().getField("version").get(o);
					gid = grid+":"+arid+":"+ver;
	//				System.out.println("adding: "+gid);
	
	//				gid = "net.sourceforge.jadex:jadex-applications-bdi:2.1-SNAPSHOT";
					IResourceIdentifier rid = new ResourceIdentifier(null, gid);
					treepanel.addTopLevelNode(rid);
				}
			}			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	/**
	 *  Get the icon.
	 *  @return The icon.
	 */
	public static Icon getIcon()
	{
		return icons.getIcon("addrid");
	}
	
	/**
	 *  Get the name.
	 *  @return The name.
	 */
	public static String getName()
	{
		return "Add RID";
	}
	
	/**
	 *  Get the tooltip text.
	 *  @return The tooltip text.
	 */
	public static String getTooltipText()
	{
		return "Add a new resource identifier.";
	}
}
