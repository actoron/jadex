package jadex.backup.browser;

import jadex.backup.resource.FileInfo;
import jadex.backup.resource.IResourceService;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.commons.Tuple2;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.gui.SGUI;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.AgentKilled;

import java.awt.Component;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 *  Simple gui to view available resources.
 */
@Agent
public class ResourceBrowserAgent
{
	//-------- constants --------
	
	/** A date format for time stamps. */
	protected static DateFormat	TSFORMAT	= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	//-------- attributes --------
	
	/** The agent. */
	@Agent
	protected IInternalAccess	component;
	
	/** The gui frame. */
	protected JFrame	gui;
	
	//-------- constructors --------
	
	/**
	 *  Called on startup.
	 */
	@AgentCreated
	public IFuture<Void> start()
	{
		final Future<Void>	ret	= new Future<Void>();
		final IExternalAccess	ea	= component.getExternalAccess();
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				JTree	tree	= new JTree(new ResourceTreeModel(ea));
				tree.setCellRenderer(new DefaultTreeCellRenderer()
				{
					public Component getTreeCellRendererComponent(JTree tree,
							Object value, boolean sel, boolean expanded,
							boolean leaf, int row, boolean hasFocus)
					{
						if(value instanceof Tuple2)
						{
							FileInfo	fi	= (FileInfo)((Tuple2<?,?>)value).getFirstEntity();
							if("./".equals(fi.getLocation()))
							{
								List<?>	res	= (List<?>)((Tuple2<?,?>)value).getSecondEntity();
								value	= ((IResourceService)res.get(0)).getResourceId() +" ("+res.size()+" instances)";
							}
							else
							{
								value	= fi.getLocation() + " ("+TSFORMAT.format(new Date(fi.getTimeStamp()))+")";
							}
						}
						
						return super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
					}
				});
				
				gui	= new JFrame("Jadex Backup - Resource Browser");
				gui.getContentPane().add(new JScrollPane(tree));
				gui.setSize(800, 600);
				gui.setLocation(SGUI.calculateMiddlePosition(gui));
				gui.setVisible(true);
				gui.addWindowListener(new WindowAdapter()
				{
					public void windowClosing(WindowEvent e)
					{
						ea.killComponent();
					}
				});
				ret.setResult(null);
			}
		});
		return ret;
	}
	
	/**
	 *  Called on shutdown.
	 */
	@AgentKilled
	public IFuture<Void>	stop()
	{
		final Future<Void>	ret	= new Future<Void>();
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				gui.dispose();
				ret.setResult(null);
			}
		});
		return ret;
	}
}
