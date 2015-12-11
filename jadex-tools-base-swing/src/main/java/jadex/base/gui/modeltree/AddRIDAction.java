package jadex.base.gui.modeltree;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.lang.reflect.Method;
import java.net.URI;

import javax.swing.Icon;
import javax.swing.UIDefaults;

import jadex.bridge.GlobalResourceIdentifier;
import jadex.bridge.IGlobalResourceIdentifier;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.ResourceIdentifier;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.threadpool.IDaemonThreadPoolService;
import jadex.commons.SReflect;
import jadex.commons.concurrent.IThreadPool;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.gui.SGUI;
import jadex.commons.gui.ToolTipAction;
import jadex.commons.gui.future.SwingDefaultResultListener;

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
	protected ITreeAbstraction treepanel;
	
	/** The thread pool. */
	protected IThreadPool tp;
	
	//-------- constructors --------
	
	/**
	 *  Create a new action
	 */
	public AddRIDAction(ITreeAbstraction treepanel)
	{
		this(getName(), getIcon(), getTooltipText(), treepanel);
	}
	
	/**
	 *  Create a new action 
	 */
	public AddRIDAction(String name, Icon icon, String desc, ITreeAbstraction treepanel)
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
		Class cl = SReflect.findClass0("jadex.platform.service.dependency.maven.MavenDependencyResolverService", null, null);
//		return cl!=null && treepanel.getTree().getLastSelectedPathComponent()==null && !treepanel.isRemote();
		return cl!=null;
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
				public void customResultAvailable(IThreadPool tp)
				{
					try
					{
//						String gid = JOptionPane.showInputDialog(treepanel, "Enter Artifact Id:");
						Method m = cl.getMethod("showDialog", new Class[]{IThreadPool.class, Component.class});
						Object o = m.invoke(null, new Object[]{tp, treepanel.getTree()});
//						System.out.println("sel ai: "+o);
						if(o!=null)
						{
							String grid = (String)o.getClass().getField("groupId").get(o);
							String arid = (String)o.getClass().getField("artifactId").get(o);
							String ver = (String)o.getClass().getField("version").get(o);
							String url = (String)o.getClass().getField("remoteUrl").get(o);
							Long lmod = (Long)o.getClass().getField("lastModified").get(o);
							String id = grid+":"+arid+":"+ver;
							IGlobalResourceIdentifier gid = new GlobalResourceIdentifier(id, new URI(url), lmod!=null? lmod.toString(): null);
//							System.out.println("adding: "+gid);
			
			//				gid = "net.sourceforge.jadex:jadex-applications-bdi:2.1";
							IResourceIdentifier rid = new ResourceIdentifier(null, gid);
//							treepanel.addTopLevelNode(rid);
							treepanel.action(rid);
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
//			SServiceProvider.getServiceUpwards(treepanel.localexta.getServiceProvider(), IDaemonThreadPoolService.class)
			SServiceProvider.getService(treepanel.getGUIExternalAccess(), IDaemonThreadPoolService.class, RequiredServiceInfo.SCOPE_PLATFORM)
				.addResultListener(new SwingDefaultResultListener<IDaemonThreadPoolService>()
			{
				public void customResultAvailable(IDaemonThreadPoolService result)
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
