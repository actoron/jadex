package jadex.backup.swing;

import jadex.backup.job.Job;
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
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IntermediateDefaultResultListener;
import jadex.commons.gui.PropertiesPanel;
import jadex.commons.gui.SGUI;
import jadex.commons.gui.future.SwingIntermediateResultListener;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.UIDefaults;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.TreePath;

/**
 * 
 */
public class SyncJobPanel extends JPanel
{
	
	/** The image icons. */
	protected static final UIDefaults icons = new UIDefaults(new Object[]
	{
		"dir", SGUI.makeIcon(SyncJobPanel.class, "/jadex/backup/swing/images/folder_16.png"),
		"delete_dir", SGUI.makeIcon(SyncJobPanel.class, "/jadex/backup/swing/images/delete_folder_16.png"),
		"new_dir", SGUI.makeIcon(SyncJobPanel.class, "/jadex/backup/swing/images/new_folder_16.png"),
		"rename_dir", SGUI.makeIcon(SyncJobPanel.class, "/jadex/backup/swing/images/rename_folder_16.png")
	});
	
	protected static int cnt;

	protected SyncJob job;
	
	/**
	 * 
	 */
	public SyncJobPanel(final IExternalAccess ea, boolean editable, final SyncJob job)
	{
		this.job = job;
		
		PropertiesPanel pp;
		if(!editable)
		{
			pp = new PropertiesPanel("Sync Job Details");
			pp.createTextField("Name: ", getName());
			pp.createTextField("Id: ", job.getId());
			pp.createTextField("Local Ressource: ", job.getLocalResource());
			pp.createTextField("Global Ressource: ", job.getGlobalResource());
			pp.createCheckBox("Active: ", job.isActive(), false, 0);
		}
		else
		{
			pp = new PropertiesPanel("New Sync Job");
			String name = "Job #"+(++cnt);
			final JTextField ntf = pp.createTextField("Name: ", name, editable);
			job.setName(name);
			if(job.getId()==null)
				job.setId(SUtil.createUniqueId(job.getName()));
			ntf.addFocusListener(new FocusAdapter()
			{
				public void focusLost(FocusEvent e)
				{
					job.setName(ntf.getText());
				}
			});
	//		pp.createTextField("Id: ", getId());
			
			JPanel lrp = new JPanel(new GridBagLayout());
			final JTextField lrtf = new JTextField();
			lrtf.addFocusListener(new FocusAdapter()
			{
				public void focusLost(FocusEvent e)
				{
					job.setLocalResource(lrtf.getText());
				}
			});
			JButton lrb = new JButton("...");
			lrb.setMargin(new Insets(0,0,0,0));
			lrp.add(lrtf, new GridBagConstraints(0,0,1,1,1,1,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,0,0,0),0,0));
			lrp.add(lrb, new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(0,0,0,0),0,0));
			pp.addComponent("Local Ressource: ", lrp);
			
			JPanel grp = new JPanel(new GridBagLayout());
			final JTextField grtf = new JTextField();
			grtf.addFocusListener(new FocusAdapter()
			{
				public void focusLost(FocusEvent e)
				{
					job.setGlobalResource(grtf.getText());
				}
			});
			String gid = SUtil.createUniqueId("gid");
			job.setGlobalResource(gid);
			grtf.setText(gid);
			JButton grb = new JButton("...");
			grb.setMargin(new Insets(0,0,0,0));
			grp.add(grtf, new GridBagConstraints(0,0,1,1,1,1,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,0,0,0),0,0));
			grp.add(grb, new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(0,0,0,0),0,0));
			pp.addComponent("Global Ressource: ", grp);
			final JCheckBox acb = pp.createCheckBox("Active: ", true, true, 0);
			acb.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					job.setActive(acb.isSelected());
				}
			});
			
			final PropertiesPanel fpp = pp;
			lrb.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					JFileChooser fc = new JFileChooser(".");
					fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
					FileFilter filter = new FileFilter()
					{
						public boolean accept(File file)
						{
							return file.isDirectory();
						}
						
						public String getDescription()
						{
							return "Only directories";
						}
					};
					fc.setFileFilter(filter);
					int result = fc.showOpenDialog(fpp);
					if(JFileChooser.APPROVE_OPTION == result)
					{
						try
						{
							String dir = fc.getSelectedFile().getCanonicalPath();
							lrtf.setText(dir);
							job.setLocalResource(dir);
						}
						catch(Exception ex)
						{
						}
					}
				}
			});
			
			grb.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					String ret = createGlobalIdDialog(ea, fpp);
					if(ret!=null)
					{
						grtf.setText(ret);
						job.setGlobalResource(ret);
					}
				}
			});
		}
		
		setLayout(new BorderLayout());
		add(new JScrollPane(pp), BorderLayout.CENTER);
	}
	
//	/**
//	 * 
//	 */
//	public boolean isValid()
//	{
//		return job.getLocalResource()!=null && job.getId()!=null && job.getName()!=null;
//	}
	
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
