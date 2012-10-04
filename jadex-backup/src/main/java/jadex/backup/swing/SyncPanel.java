package jadex.backup.swing;

import jadex.backup.job.IJobService;
import jadex.backup.job.SyncJob;
import jadex.backup.resource.IResourceService;
import jadex.base.gui.filetree.DefaultNodeHandler;
import jadex.base.gui.filetree.FileTreePanel;
import jadex.base.gui.filetree.RefreshSubtreeAction;
import jadex.base.gui.idtree.IdTreeCellRenderer;
import jadex.base.gui.idtree.IdTreeModel;
import jadex.base.gui.idtree.IdTreeNode;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.commons.SUtil;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IntermediateDefaultResultListener;
import jadex.commons.gui.SGUI;
import jadex.commons.gui.future.SwingIntermediateResultListener;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.UIDefaults;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.tree.TreePath;

/**
 * 
 */
public class SyncPanel extends JPanel
{
	/** The image icons. */
	protected static final UIDefaults icons = new UIDefaults(new Object[]
	{
		"dir", SGUI.makeIcon(SourceSelectionPanel.class, "/jadex/backup/swing/images/folder_16.png"),
		"delete_dir", SGUI.makeIcon(SourceSelectionPanel.class, "/jadex/backup/swing/images/delete_folder_16.png"),
		"new_dir", SGUI.makeIcon(SourceSelectionPanel.class, "/jadex/backup/swing/images/new_folder_16.png"),
		"rename_dir", SGUI.makeIcon(SourceSelectionPanel.class, "/jadex/backup/swing/images/rename_folder_16.png")
	});
	
	protected int cnt;
	
	/**
	 * 
	 */
	public SyncPanel(final IExternalAccess ea)
	{
		setLayout(new BorderLayout());
		
		JPanel quickp = new JPanel(new GridBagLayout());
		quickp.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED), " Quick Objective Settings "));
		
		JLabel namel = new JLabel("Job name:");
		final JTextField nametf = new JTextField("job #"+cnt++);
		
		JLabel offerl = new JLabel("Local resource:");
		JLabel connectl = new JLabel("Global resource id:");
//		JLabel whenl = new JLabel("When should the data be offered:");
//		JLabel olderl = new JLabel("Should older versions be kept: ");

		final JComboBox offercb = new JComboBox();
		final JComboBox connectcb = new JComboBox();
//		JComboBox datacb = new JComboBox(new String[]{"offer new resource", "connect to existing resource"});
//		JComboBox whencb = new JComboBox(new String[]{"let the system decide"});
//		JComboBox oldercb = new JComboBox(new String[]{"no", "yes, <=3", "yes, all"});
		
//			JButton methodb = new JButton("...");
		JButton offerb = new JButton("...");
		JButton connectb = new JButton("...");
//		JButton olderb = new JButton("...");
		
		JButton okb = new JButton("OK");
		JButton cancelb = new JButton("Cancel");
		
		int x = 0;
		int y = 0;

		x=0;
		quickp.add(namel, new GridBagConstraints(x++,y,1,1,0,0,GridBagConstraints.WEST,
			GridBagConstraints.VERTICAL,new Insets(2,2,2,2),0,0));
		quickp.add(nametf, new GridBagConstraints(x,y++,2,1,1,0,GridBagConstraints.WEST,
			GridBagConstraints.BOTH,new Insets(2,2,2,2),0,0));
		x=0;
		quickp.add(offerl, new GridBagConstraints(x++,y,1,1,0,0,GridBagConstraints.WEST,
			GridBagConstraints.VERTICAL,new Insets(2,2,2,2),0,0));
		quickp.add(offercb, new GridBagConstraints(x++,y,1,1,1,0,GridBagConstraints.WEST,
			GridBagConstraints.BOTH,new Insets(2,2,2,2),0,0));
		quickp.add(offerb, new GridBagConstraints(x,y++,1,1,0,0,GridBagConstraints.WEST,
			GridBagConstraints.BOTH,new Insets(2,2,2,2),0,0));
		x=0;
		quickp.add(connectl, new GridBagConstraints(x++,y,1,1,0,0,GridBagConstraints.WEST,
			GridBagConstraints.VERTICAL,new Insets(2,2,2,2),0,0));
		quickp.add(connectcb, new GridBagConstraints(x++,y,1,1,1,0,GridBagConstraints.WEST,
			GridBagConstraints.BOTH,new Insets(2,2,2,2),0,0));
		quickp.add(connectb, new GridBagConstraints(x,y++,1,1,0,0,GridBagConstraints.WEST,
			GridBagConstraints.BOTH,new Insets(2,2,2,2),0,0));
//		x=0;
//		quickp.add(olderl, new GridBagConstraints(x++,y,1,1,0,0,GridBagConstraints.WEST,
//			GridBagConstraints.VERTICAL,new Insets(2,2,2,2),0,0));
//		quickp.add(oldercb, new GridBagConstraints(x++,y,1,1,1,0,GridBagConstraints.WEST,
//			GridBagConstraints.BOTH,new Insets(2,2,2,2),0,0));
//		quickp.add(olderb, new GridBagConstraints(x,y++,1,1,0,0,GridBagConstraints.WEST,
//			GridBagConstraints.BOTH,new Insets(2,2,2,2),0,0));
		x=0;
		JPanel bup = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		bup.add(okb);
		bup.add(cancelb);
		quickp.add(bup, new GridBagConstraints(x,y++,3,1,1,1,GridBagConstraints.WEST,
			GridBagConstraints.BOTH,new Insets(2,2,2,2),0,0));
		
//		final ObjectCardLayout ocl = new ObjectCardLayout();
//		final JPanel detailp = new JPanel(ocl);
//		detailp.setMinimumSize(new Dimension(1,1));
//		detailp.setPreferredSize(new Dimension(100,100));
		
//		JSplitPane splitp = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
//		splitp.setDividerLocation(0.5);
//		splitp.setOneTouchExpandable(true);
//		splitp.add(quickp);
//		splitp.add(new JScrollPane(detailp));
//		splitp.add(detailp);
		
		okb.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				String id = SUtil.createUniqueId(nametf.getText());
				String lres = (String)offercb.getSelectedItem();
				String gres = (String)connectcb.getSelectedItem();
				final SyncJob job = new SyncJob(id, nametf.getText(), lres, gres);
				SServiceProvider.getService(ea.getServiceProvider(), IJobService.class, RequiredServiceInfo.SCOPE_PLATFORM)
					.addResultListener(new DefaultResultListener<IJobService>()
				{
					public void resultAvailable(IJobService js)
					{
						js.addJob(job).addResultListener(new DefaultResultListener<Void>()
						{
							public void resultAvailable(Void result)
							{
								System.out.println("added new job: "+job);
							}
							public void exceptionOccurred(Exception exception)
							{
								exception.printStackTrace();
								super.exceptionOccurred(exception);
							}
						});
					}
				});
			}
		});
		
		offerb.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				String res = createLocalSourceDialog(ea, SyncPanel.this);
				if(res!=null)
				{
					offercb.addItem(res);
				}
//				if(ocl.getComponent("data")==null)
//				{
//					detailp.add(new SourceSelectionPanel(null), "data");
//				}
//				ocl.show("data");
			}
		});
		
		connectb.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				String res = createGlobalIdDialog(ea, SyncPanel.this);
				if(res!=null)
				{
					connectcb.addItem(res);
				}
//				if(ocl.getComponent("data")==null)
//				{
//					detailp.add(new SourceSelectionPanel(null), "data");
//				}
//				ocl.show("data");
			}
		});
		
		add(quickp, BorderLayout.CENTER);
	}

	/**
	 *  Create a new local source dialog.
	 */
	public static String createLocalSourceDialog(IExternalAccess ea, JComponent comp)
	{
		String ret = null;
		
		final FileTreePanel srct = new FileTreePanel(ea);
		final DefaultNodeHandler nh = new DefaultNodeHandler(srct.getTree());
		
		nh.addAction(new AbstractAction("Create directory", icons.getIcon("new_dir"))
		{
			public void actionPerformed(ActionEvent e)
			{
				String[] sels = srct.getSelectionPaths();
				if(sels.length==1)
				{
					String name = JOptionPane.showInputDialog("Name: ");
					if(name!=null && name.length()!=0)
					{
						File f = new File(sels[0]+File.separator+name);
						f.mkdir();
						Action act = nh.getAction(RefreshSubtreeAction.getName());
						act.actionPerformed(e);
					}
				}
			}
			
			public boolean isEnabled()
			{
				boolean ret = false;
				String[] sels = srct.getSelectionPaths();
				if(sels.length==1)
				{
					File f =  new File(sels[0]);
					ret = f.exists() && f.isDirectory();
				}
				return ret;
			}
		}, null);
		
		nh.addAction(new AbstractAction("Delete file/dir", icons.getIcon("delete_dir"))
		{
			public void actionPerformed(ActionEvent e)
			{
				String[] sels = srct.getSelectionPaths();
				if(sels.length==1)
				{
					File f = new File(sels[0]);
					if(f.exists())
					{
						SUtil.deleteDirectory(f);
					}
					Action act = nh.getAction(RefreshSubtreeAction.getName());
					act.actionPerformed(e);
				}
			}
		}, null);
		
		nh.addAction(new AbstractAction("Rename file/dir", icons.getIcon("rename_dir"))
		{
			public void actionPerformed(ActionEvent e)
			{
				String[] sels = srct.getSelectionPaths();
				if(sels.length==1)
				{
					File f = new File(sels[0]);
					if(f.exists())
					{
						String name = JOptionPane.showInputDialog("New name: ");
						if(name!=null && name.length()!=0)
						{
							f.renameTo(new File(f.getParent(), name));
						}
					}
					Action act = nh.getAction(RefreshSubtreeAction.getName());
					act.actionPerformed(e);
				}
			}
		}, null);
		
		srct.addNodeHandler(nh);
		
		File[] roots = File.listRoots();
		for(File root: roots)
		{
			srct.addTopLevelNode(root);
		}
		
		if(createDialog("Source Folder Selection", srct, comp))
		{
			String[] sels = srct.getSelectionPaths();
			ret = sels[0];
//			if(sels!=null)
//			{
//				for(String sel: sels)
//				{
//				}
//			}
		}

		return ret;
	}
	
	/**
	 *  Create a new global id dialog.
	 */
	public static String createGlobalIdDialog(IExternalAccess ea, JComponent comp)
	{
		String ret = null;
		
		final IdTreeModel<List<IResourceService>> tm = new IdTreeModel<List<IResourceService>>();
		final JTree srct = new JTree(tm);
		srct.setCellRenderer(new IdTreeCellRenderer());
		srct.setRootVisible(false);
		
		final IdTreeNode<List<IResourceService>> root = new IdTreeNode<List<IResourceService>>("root", "root", tm, false, null, null, null);
		tm.setRoot(root);
		
		IIntermediateFuture<IResourceService> fut = SServiceProvider.getServices(ea.getServiceProvider(), IResourceService.class, RequiredServiceInfo.SCOPE_GLOBAL);
		fut.addResultListener(new SwingIntermediateResultListener<IResourceService>(new IntermediateDefaultResultListener<IResourceService>()
		{
			public void intermediateResultAvailable(IResourceService result)
			{
				IdTreeNode<List<IResourceService>> node = tm.getNode(result.getResourceId());
				if(node==null)
				{
					List<IResourceService> sers = new ArrayList<IResourceService>();
					node = new IdTreeNode<List<IResourceService>>(result.getResourceId(), result.getResourceId(), tm, false, icons.getIcon("dir"), null, sers);
					root.add(node);
				}
				node.getObject().add(result);
			}
		}));
		
		if(createDialog("Global Resource Id Selection", srct, comp))
		{
			TreePath sel = srct.getSelectionPath();
			if(sel!=null)
			{
				IdTreeNode<List<IResourceService>> n = (IdTreeNode<List<IResourceService>>)sel.getLastPathComponent();
				System.out.println("sel: "+n.getId());
				ret = n.getId();
			}
		}
		
		return ret;
	}
	
	/**
	 *  Create a dialog with a specific content panel.
	 */
	public static boolean createDialog(String title, JComponent content, JComponent comp)
	{
		final JDialog dia = new JDialog((JFrame)null, title, true);
		
		JButton bok = new JButton("OK");
		JButton bcancel = new JButton("Cancel");
		bok.setMinimumSize(bcancel.getMinimumSize());
		bok.setPreferredSize(bcancel.getPreferredSize());
		JPanel ps = new JPanel(new GridBagLayout());
		ps.add(bok, new GridBagConstraints(0,0,1,1,1,0,GridBagConstraints.SOUTHEAST, GridBagConstraints.VERTICAL, new Insets(2,2,2,2), 0, 0));
		ps.add(bcancel, new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.SOUTHEAST, GridBagConstraints.BOTH, new Insets(2,2,2,2), 0, 0));

		dia.getContentPane().add(content, BorderLayout.CENTER);
		dia.getContentPane().add(ps, BorderLayout.SOUTH);
		final boolean[] ok = new boolean[1];
		bok.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				ok[0] = true;
				dia.dispose();
			}
		});
		bcancel.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				dia.dispose();
			}
		});
		dia.pack();
		dia.setLocation(SGUI.calculateMiddlePosition(comp!=null? SGUI.getWindowParent(comp): null, dia));
		dia.setVisible(true);
		
		return ok[0];
	}
}
