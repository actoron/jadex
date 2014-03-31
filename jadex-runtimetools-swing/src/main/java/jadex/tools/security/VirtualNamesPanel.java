package jadex.tools.security;

import jadex.base.gui.idtree.IdTreeModel;
import jadex.base.gui.idtree.IdTreeNode;
import jadex.bridge.service.types.security.ISecurityService;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.gui.PropertiesPanel;
import jadex.commons.gui.future.SwingExceptionDelegationResultListener;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

/**
 * 
 */
public class VirtualNamesPanel extends JPanel
{
	/** The security service. */
	protected ISecurityService secser;
	
	/** The tree model. */
	protected IdTreeModel<String> model;
	
	public static final String VIRTUAL_NAME = "Virtual name";
	public static final String PLATFORM_NAME = "Platform name";
	
	/**
	 *  Create a new panel. 
	 */
	public VirtualNamesPanel(ISecurityService secser)
	{
		this.secser = secser;
		
		setLayout(new BorderLayout());
		
		setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Virtual Name Settings"));
	
		model = new IdTreeModel<String>();
		IdTreeNode<String> root = new IdTreeNode<String>("root", "root", model, null, null, "Root", null);
		model.setRoot(root);
		
		final JTree tree = new JTree(model);
		
//		tree.setRootVisible(false);
		
		add(new JScrollPane(tree), BorderLayout.CENTER);
		
		MouseAdapter ma = new MouseAdapter()
		{
			public void mousePressed(MouseEvent e)
			{
				popup(e);
			}
			
			public void mouseReleased(MouseEvent e)
			{
				popup(e);
			}
			
			protected void	popup(MouseEvent e)
			{
				if(e.isPopupTrigger())
				{
					JPopupMenu menu = new JPopupMenu("Virtuals menu");
					
					TreePath path = tree.getPathForLocation(e.getX(), e.getY());
					if(path!=null) 
					{
					    tree.setSelectionPath(path);
					
						final IdTreeNode<String> node = (IdTreeNode<String>)tree.getLastSelectedPathComponent();
						
						Object uo = node.getObject();
						if(uo==null)
						{
							menu.add(new AbstractAction("Add virtual name")
							{
								public void actionPerformed(ActionEvent e)
								{
									PropertiesPanel pp = new PropertiesPanel();
									final JTextField tfname = pp.createTextField("Virtual name: ", null, true);
									
									int res	= JOptionPane.showOptionDialog(VirtualNamesPanel.this, pp, "Virtual Platform Name", JOptionPane.YES_NO_CANCEL_OPTION,
										JOptionPane.QUESTION_MESSAGE, null, new Object[]{"OK", "Cancel"}, "OK");
									if(JOptionPane.YES_OPTION==res)
									{
										String name = tfname.getText();
										IdTreeNode<String> cn = new IdTreeNode<String>(name, name, model, null, null, VIRTUAL_NAME, VIRTUAL_NAME);
										node.add(cn);
										tree.setSelectionPath(new TreePath(cn.getPath()));
//										tree.expandPath(new TreePath(cn.getPath()));
									}
								}
							});
						}
						if(VIRTUAL_NAME.equals(uo))
						{
							menu.add(new AbstractAction("Add platform name")
							{
								public void actionPerformed(ActionEvent e)
								{
									PropertiesPanel pp = new PropertiesPanel();
									final JTextField tfname = pp.createTextField("Platform name: ", null, true);
									
									int res	= JOptionPane.showOptionDialog(VirtualNamesPanel.this, pp, "Platform Name", JOptionPane.YES_NO_CANCEL_OPTION,
										JOptionPane.QUESTION_MESSAGE, null, new Object[]{"OK", "Cancel"}, "OK");
									if(JOptionPane.YES_OPTION==res)
									{
										String name = tfname.getText();
										IdTreeNode<String> cn = new IdTreeNode<String>(name, name, model, null, null, PLATFORM_NAME, PLATFORM_NAME);
										node.add(cn);
									}
								}
							});
							menu.add(new AbstractAction("Remove virtual name")
							{
								public void actionPerformed(ActionEvent e)
								{
									((IdTreeNode)node.getParent()).remove(node);
								}
							});
						}
						else if(PLATFORM_NAME.equals(uo))
						{
							menu.add(new AbstractAction("Remove platform name")
							{
								public void actionPerformed(ActionEvent e)
								{
									((IdTreeNode)node.getParent()).remove(node);
								}
							});
						}
						
						menu.show(tree, e.getX(), e.getY());
					}
				}
			}
		};
		
		tree.addMouseListener(ma);
		
		createVirtualsModel();
		
//		final JTextField tfname = new JTextField();
//	    final JTextField tfpass = new JTextField();
//	    JButton buadd = new JButton("Add");
//	    buadd.addActionListener(new ActionListener()
//		{
//			public void actionPerformed(ActionEvent e)
//			{
//				if(addaction!=null)
//				{
//					addaction.execute(new String[]{tfname.getText(), tfpass.getText()});
//				}
//			}
//		});
//	    
//	    JPanel padd = new JPanel(new GridBagLayout());
//	    padd.add(new JLabel(""), new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.EAST, 
//	    	GridBagConstraints.BOTH, new Insets(2,2,2,2), 0, 0));
//	    padd.add(tfname, new GridBagConstraints(1, 0, 1, 1, 1, 0, GridBagConstraints.EAST, 
//		    GridBagConstraints.BOTH, new Insets(2,2,2,2), 0, 0));
//	    padd.add(new JLabel(colnames[1]), new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.EAST, 
//		    GridBagConstraints.BOTH, new Insets(2,2,2,2), 0, 0));
//	    padd.add(tfpass, new GridBagConstraints(3, 0, 1, 1, 1, 0, GridBagConstraints.EAST, 
//		    GridBagConstraints.BOTH, new Insets(2,2,2,2), 0, 0));
//	    padd.add(buadd, new GridBagConstraints(4, 0, 1, 1, 0, 0, GridBagConstraints.EAST, 
//	    	GridBagConstraints.BOTH, new Insets(2,2,2,2), 0, 0));
	}
	
	/**
	 * 
	 */
	protected IFuture<Void> createVirtualsModel()
	{
		final Future<Void> ret = new Future<Void>();
		
		secser.getVirtuals().addResultListener(new SwingExceptionDelegationResultListener<Map<String,Set<String>>, Void>(ret)
		{
			public void customResultAvailable(Map<String,Set<String>> virtuals) 
			{
				IdTreeNode<String> root = (IdTreeNode<String>)model.getRoot();
				root.removeAllChildren();
				
				for(Map.Entry<String, Set<String>> virtual: virtuals.entrySet())
				{
					String v = virtual.getKey();
					IdTreeNode<String> vn = new IdTreeNode<String>(v, v, model, null, null, VIRTUAL_NAME, VIRTUAL_NAME);
					root.add(vn);
					if(virtual.getValue()!=null)
					{
						for(String pl: virtual.getValue())
						{
							IdTreeNode<String> pn = new IdTreeNode<String>(pl, pl, model, null, null, PLATFORM_NAME, PLATFORM_NAME);
							vn.add(pn);
						}
					}
				}
				
				ret.setResult(null);
			}
		});
	
		return ret;
	}
	
	/**
	 * 
	 */
	public static void main(String[] args)
	{
		IdTreeModel<String> model = new IdTreeModel<String>();
		IdTreeNode<String> root = new IdTreeNode<String>("root", "root", model, null, null, "Root", null);
		model.setRoot(root);
		final JTree tree = new JTree(model);
		
		IdTreeNode<String> a = new IdTreeNode<String>("a", "a", model, null, null, VIRTUAL_NAME, VIRTUAL_NAME);
		IdTreeNode<String> b = new IdTreeNode<String>("b", "b", model, null, null, VIRTUAL_NAME, VIRTUAL_NAME);
		IdTreeNode<String> c = new IdTreeNode<String>("c", "c", model, null, null, VIRTUAL_NAME, VIRTUAL_NAME);
		root.add(a);
		root.add(b);
		root.add(c);
		
		JFrame f = new JFrame();
		JPanel p = new JPanel(new BorderLayout());
		p.add(new JScrollPane(tree), BorderLayout.CENTER);
		f.getContentPane().add(p, BorderLayout.CENTER);
		
		f.pack();
		f.setVisible(true);
		
//		root.remove(a);
		
		MouseAdapter ma = new MouseAdapter()
		{
			public void mousePressed(MouseEvent e)
			{
				popup(e);
			}
			
			public void mouseReleased(MouseEvent e)
			{
				popup(e);
			}
			
			protected void	popup(MouseEvent e)
			{
				if(e.isPopupTrigger())
				{
					JPopupMenu menu = new JPopupMenu("Virtuals menu");
					
					TreePath path = tree.getPathForLocation(e.getX(), e.getY());
					
					if(path!=null) 
					{
					    tree.setSelectionPath(path);
						
					    final IdTreeNode<String> node = (IdTreeNode<String>)tree.getLastSelectedPathComponent();
					    
					    System.out.println("node: "+node.getId());
					    
					    menu.add(new AbstractAction("Remove")
						{
							public void actionPerformed(ActionEvent e)
							{
								System.out.println("rem");
								System.out.println("pa: "+node.getParent()+" n: "+node);
								((IdTreeNode)node.getParent()).remove(node);
							}
						});
						
						menu.show(tree, e.getX(), e.getY());
					}
				}
			}
		};
		
		tree.addMouseListener(ma);
	}
	
	
}
