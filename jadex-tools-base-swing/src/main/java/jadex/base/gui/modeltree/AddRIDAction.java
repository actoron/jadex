package jadex.base.gui.modeltree;

import jadex.base.gui.asynctree.ITreeNode;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.ResourceIdentifier;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.threadpool.IThreadPoolService;
import jadex.commons.SReflect;
import jadex.commons.concurrent.IThreadPool;
import jadex.commons.concurrent.ThreadPool;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.gui.SGUI;
import jadex.commons.gui.ToolTipAction;
import jadex.commons.gui.future.SwingDefaultResultListener;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.lang.reflect.Method;

import javax.swing.Icon;
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
		"addrid",	SGUI.makeIcon(ModelTreePanel.class, "/jadex/base/gui/images/add_jar2.png"),
	});
	
	//-------- attributes --------
	
	/** The tree. */
	protected ModelTreePanel treepanel;
	
	/** The thread pool. */
	protected IThreadPool tp;
	
	//-------- constructors --------
	
	/**
	 *  Create a new action
	 */
	public AddRIDAction(ModelTreePanel treepanel)
	{
		this(getName(), getIcon(), getTooltipText(), treepanel);
	}
	
	/**
	 *  Create a new action 
	 */
	public AddRIDAction(String name, Icon icon, String desc, ModelTreePanel treepanel)
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
		final Class<?> cl = SReflect.findClass0("jadex.base.gui.reposearch.RepositorySearchPanel", null, null);
		if(cl!=null)
		{
			getThreadPool().addResultListener(new SwingDefaultResultListener<IThreadPool>()
			{
				public void customResultAvailable(IThreadPool result)
				{
					try
					{
						String gid = null;
//						String gid = JOptionPane.showInputDialog(treepanel, "Enter Artifact Id:");
						Method m = cl.getMethod("showDialog", new Class[]{IThreadPool.class, Component.class});
						Object o = m.invoke(null, new Object[]{tp, treepanel});
//						System.out.println("sel ai: "+o);
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
					catch(Exception ex)
					{
						ex.printStackTrace();
					}
				}
			});
		}			
	}
	
	/**
	 *  Get the thread pool.
	 */
	protected IFuture<IThreadPool> getThreadPool()
	{
		final Future<IThreadPool> ret = new  Future<IThreadPool>();
		
		if(tp==null)
		{
			SServiceProvider.getServiceUpwards(treepanel.localexta.getServiceProvider(), IThreadPoolService.class)
				.addResultListener(new SwingDefaultResultListener<IThreadPoolService>()
			{
				public void customResultAvailable(IThreadPoolService result)
				{
					tp = result;
					ret.setResult(tp);
				}
			});
		}
		else
		{
			ret.setResult(tp);
		}
		
		return ret;
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
