package jadex.platform.service.security;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.bouncycastle.cert.X509CertificateHolder;

import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.commons.gui.ModulateComposite;
import jadex.commons.gui.SGUI;
import jadex.commons.gui.TreeExpansionHandler;
import jadex.commons.security.SSecurity;

public class CertTree extends JTree implements TreeModel
{
	public static final Icon CA_CERT_ICON;
	
	public static final Icon CERT_ICON;
	
	public static final Icon CA_CERT_ICON_KEY;
	
	public static final Icon CERT_ICON_KEY;
	
	static
	{
		BufferedImage caimg = null;
		BufferedImage certimg = null;
		BufferedImage cakeyimg = null;
		BufferedImage certkeyimg = null;
		try
		{
			String iconpath = CertTree.class.getPackage().getName().replace(".", "/") + "/cert_white.png";
			System.out.println(iconpath);
			InputStream is = SUtil.getResource(iconpath, CertTree.class.getClassLoader());
			BufferedImage baseimg = SGUI.convertBufferedImageType(ImageIO.read(is), BufferedImage.TYPE_4BYTE_ABGR_PRE);
			is.close();
			
			int sizex = baseimg.getWidth();
			int sizey = baseimg.getHeight();
			
			iconpath = CertTree.class.getPackage().getName().replace(".", "/") + "/key.png";
			is = SUtil.getResource(iconpath, CertTree.class.getClassLoader());
			BufferedImage keyimg = SGUI.convertBufferedImageType(ImageIO.read(is), BufferedImage.TYPE_4BYTE_ABGR_PRE);
			is.close();
			is = null;
			
			Color cacolor = new Color(0, 159, 107);
			caimg = new BufferedImage(sizex, sizey, BufferedImage.TYPE_4BYTE_ABGR_PRE);
			Graphics2D g = caimg.createGraphics();
			g.setComposite(new ModulateComposite(cacolor));
			g.drawImage(baseimg, 0, 0, null);
			g.dispose();
			
			cakeyimg = new BufferedImage(sizex, sizey, BufferedImage.TYPE_4BYTE_ABGR_PRE);
			g = cakeyimg.createGraphics();
			g.setComposite(AlphaComposite.Src);
			g.drawImage(caimg, 0, 0, null);
			g.setColor(new Color(1.0f, 1.0f, 1.0f, 0.0f));
			g.fillRect(128, 384, 256, 32);
			g.dispose();
			
			BufferedImage img = new BufferedImage(sizex, sizey, BufferedImage.TYPE_4BYTE_ABGR_PRE);
			g = img.createGraphics();
			g.setComposite(new ModulateComposite(cacolor));
			g.drawImage(keyimg, 0, 0, null);
			g.dispose();
			
			g = cakeyimg.createGraphics();
			g.setComposite(AlphaComposite.SrcOver);
			g.drawImage(img, 0, 0, null);
			g.dispose();
			
			Color certcolor = Color.BLACK;
			certimg = new BufferedImage(sizex, sizey, BufferedImage.TYPE_4BYTE_ABGR_PRE);
			g = certimg.createGraphics();
			g.setComposite(new ModulateComposite(certcolor));
			g.drawImage(baseimg, 0, 0, null);
			g.dispose();
			
			certkeyimg = new BufferedImage(sizex, sizey, BufferedImage.TYPE_4BYTE_ABGR_PRE);
			g = certkeyimg.createGraphics();
			g.setComposite(AlphaComposite.Src);
			g.drawImage(certimg, 0, 0, null);
			g.setColor(new Color(1.0f, 1.0f, 1.0f, 0.0f));
			g.fillRect(128, 384, 256, 32);
			g.dispose();
			
			img = new BufferedImage(sizex, sizey, BufferedImage.TYPE_4BYTE_ABGR_PRE);
			g = img.createGraphics();
			g.setComposite(new ModulateComposite(certcolor));
			g.drawImage(keyimg, 0, 0, null);
			g.dispose();
			
			g = certkeyimg.createGraphics();
			g.setComposite(AlphaComposite.SrcOver);
			g.drawImage(img, 0, 0, null);
			g.dispose();
			
			sizex = 16;
			sizey = sizex;
			
			caimg = SGUI.scaleBufferedImage(caimg, sizex, sizey);
			cakeyimg = SGUI.scaleBufferedImage(cakeyimg, sizex, sizey);
			certimg = SGUI.scaleBufferedImage(certimg, sizex, sizey);
			certkeyimg = SGUI.scaleBufferedImage(certkeyimg, sizex, sizey);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		CA_CERT_ICON = new ImageIcon(caimg);
		CERT_ICON = new ImageIcon(certimg);
		CA_CERT_ICON_KEY = new ImageIcon(cakeyimg);
		CERT_ICON_KEY = new ImageIcon(certkeyimg);
	}
	
	Map<String, Tuple2<String, String>> certmodel;
	
	Map<String, CertTreeNode> nodelookup;
	
	protected CertTreeNode root;
	
	protected List<TreeModelListener> listeners = new ArrayList<TreeModelListener>();
	
	public CertTree(Map<String, Tuple2<String, String>> certmodel)
	{
		root = createRootNode();
		setEditable(false);
		setShowsRootHandles(true);
		setLargeModel(true);
		setRootVisible(false);
		setBorder(BorderFactory.createTitledBorder("Certificates"));
		
		this.certmodel = certmodel;
		this.nodelookup = new HashMap<String, CertTreeNode>();
		setModel(this);
		new TreeExpansionHandler(this);
		
		setCellRenderer(new DefaultTreeCellRenderer()
		{
			public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus)
			{
				super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
				
				if (value instanceof CertTreeNode)
				{
					if (isCaNode((CertTreeNode) value))
					{
						if (hasKey((CertTreeNode) value))
							setIcon(CA_CERT_ICON_KEY);
						else
							setIcon(CA_CERT_ICON);
					}
					else
					{
						if (hasKey((CertTreeNode) value))
							setIcon(CERT_ICON_KEY);
						else
							setIcon(CERT_ICON);
					}
				}
				return this;
			}
		});
		
		addMouseListener(new MouseAdapter()
		{
			public void mousePressed(MouseEvent e)
			{
				if (e.isPopupTrigger())
				{
					JPopupMenu menu = new JPopupMenu();
					
					JMenuItem addcert = new JMenuItem(new AbstractAction("Create Certificate...")
					{
						public void actionPerformed(ActionEvent e)
						{
							final JDialog createdia = new JDialog(JOptionPane.getRootFrame(), "Create Certificate", false);
							createdia.getRootPane().setLayout(new BorderLayout());
//							createdia.getRootPane().add(new JButton("oisdfjogisdjf"), BorderLayout.CENTER);
//							createdia.getRootPane().add(new CertCreationPanel(CertTree.this.certmodel), BorderLayout.CENTER);
							
							final CertCreationPanel certpanel = new CertCreationPanel(getSelectedCert(), new AbstractAction()
							{
								public void actionPerformed(ActionEvent e)
								{
									if (e.getID() == ActionEvent.ACTION_PERFORMED)
									{
										CertCreationPanel pan = (CertCreationPanel) e.getSource();
										Tuple2<String, String> cert = pan.getCreatedCertificate();
										
										String subjectid = SSecurity.readCertificateFromPEM(cert.getFirstEntity()).getSubject().toString();
										
										CertTree.this.certmodel.put(subjectid, cert);
										
										updateModel();
									}
									createdia.setVisible(false);
									createdia.dispose();
								}
							});
							
							createdia.getRootPane().add(certpanel, BorderLayout.CENTER);
//							createdia.pack();
							createdia.setSize(800, 600);
							createdia.setMinimumSize(createdia.getRootPane().getPreferredSize());
							createdia.setVisible(true);
							
//							createdia.dispose();
						}
					});
					
					menu.add(addcert);
					
					menu.show(CertTree.this, e.getX(), e.getY());
				}
			}
		});
		
		JMenuItem mi = new JMenuItem("Add Root Certificate");
		mi.addActionListener(new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				
			}
		});
		
//		setRootVisible(false);
		
		updateModel();
	}
	
	public Object getRoot()
	{
		return root;
	}

	@Override
	public Object getChild(Object parent, int index)
	{
		return ((CertTreeNode) parent).getChildren().get(index);
	}

	@Override
	public int getChildCount(Object parent)
	{
		return ((CertTreeNode) parent).getChildren().size();
	}

	@Override
	public boolean isLeaf(Object node)
	{
		return ((CertTreeNode) node).getChildren().isEmpty();
	}

	@Override
	public void valueForPathChanged(TreePath path, Object newValue)
	{
	}

	@Override
	public int getIndexOfChild(Object parent, Object child)
	{
		return ((CertTreeNode) parent).getChildren().indexOf(child);
	}

	@Override
	public void addTreeModelListener(TreeModelListener l)
	{
		listeners.add(l);
	}

	@Override
	public void removeTreeModelListener(TreeModelListener l)
	{
		listeners.remove(l);
	}
	
	public void updateModel()
	{
		System.out.println("=========");
		root.clearChildren();
		nodelookup.clear();
		clearToggledPaths();
		
		synchronized(certmodel)
		{
			for (Map.Entry<String, Tuple2<String, String>> entry : certmodel.entrySet())
			{
				List<X509CertificateHolder> certchain = SSecurity.readCertificateChainFromPEM(entry.getValue().getFirstEntity());
				Collections.reverse(certchain);
				
				for (int i = 0; i < certchain.size(); ++i)
				{
					X509CertificateHolder cert = certchain.get(i);
					
					if (!nodelookup.containsKey(cert.getSubject().toString()))
					{
					
						CertTreeNode node = new CertTreeNode(cert.getSubject().toString());
						nodelookup.put(cert.getSubject().toString(), node);
						
						if (cert.getIssuer().equals(cert.getSubject()))
						{
							root.addChild(node);
						}
						else
						{
							CertTreeNode issuernode = nodelookup.get(cert.getIssuer().toString());
							issuernode.addChild(node);
						}
					}
				}
				
			}
		}
		int numchildren = root.getChildren().size();
		int[] indices = new int[numchildren];
		for (int i = 0; i < numchildren; ++i)
			indices[i] = i;
		
//		TreeModelEvent tme = new TreeModelEvent(root, new Object[] {root}, indices, root.getChildren().toArray());
//		TreeModelEvent tme = new TreeModelEvent(this, new TreePath(root));
//		TreeModelEvent tme = new TreeModelEvent(this, new Object[] {root});
		
//		CertTreeNode sroot = root;
////		root = null;
//		
//		for (int i = 0; i < listeners.size(); ++i)
//			listeners.get(i).treeNodesRemoved(tme);
//		
//		root = sroot;
//		
//		for (int i = 0; i < listeners.size(); ++i)
//			listeners.get(i).treeNodesInserted(tme);
		
//		
//		for (int i = 0; i < listeners.size(); ++i)
//			listeners.get(i).treeNodesChanged(tme);
		
//		setModel(getModel());
		
		TreeModelEvent tme = new TreeModelEvent(this, new Object[] {root});
		for (int i = 0; i < listeners.size(); ++i)
			listeners.get(i).treeStructureChanged(tme);
		
//		for (int i = listeners.size() - 1; i >= 0; --i)
//		{
//			TreeModelEvent tme = new TreeModelEvent(this, new Object[] {root});
//			System.out.println(listeners.get(i));
//			listeners.get(i).treeStructureChanged(tme);
//		}
		
//		System.out.println(getRowHeight());
//		invalidate();
//		validate();
//		repaint();
	}
	
	protected Tuple2<String, String> getSelectedCert()
	{
		Tuple2<String, String> ret = null;
		
		if (getSelectionCount() > 0)
		{
			CertTreeNode node = (CertTreeNode) getSelectionModel().getSelectionPath().getLastPathComponent();
			ret = certmodel.get(node.getSubjectId());
		}
		
		return ret;
	}
	
	protected CertTreeNode createRootNode()
	{
		return new CertTreeNode("Certificates") 
		{
			public boolean equals(Object obj)
			{
				return (obj != null && obj.getClass().equals(getClass()));
			}
		};
	}
	
	/**
	 *  Tests of a node represents a CA certficate.
	 *  
	 *  @param node The node.
	 *  @return True, if CA.
	 */
	protected boolean isCaNode(CertTreeNode node)
	{
		boolean ret = false;
		Tuple2<String, String> cert = certmodel.get(node.getSubjectId());
		if (cert != null)
			ret = SSecurity.isCaCertificate(cert.getFirstEntity());
		return ret;
	}
	
	/**
	 *  Tests if a node has a private key.
	 *  
	 *  @param node The node.
	 *  @return True, if it has a key.
	 */
	protected boolean hasKey(CertTreeNode node)
	{
		boolean ret = false;
		Tuple2<String, String> cert = certmodel.get(node.getSubjectId());
		if (cert != null)
			ret = cert.getSecondEntity() != null;
		return ret;
	}
	
	protected class CertTreeNode
	{
		protected String subjectid;
		
		protected List<CertTreeNode> children;
		
		public CertTreeNode(String subjectid)
		{
			this.subjectid = subjectid;
			children = new ArrayList<CertTreeNode>();
		}
		
		public void addChild(CertTreeNode node)
		{
			if (!children.contains(node))
			{
				System.out.println("NODE Added: " + node);
				children.add(node);
			}
			else
				throw new RuntimeException("duplicate: " + node);
		}
		
		/**
		 *  Gets the subject ID.
		 *
		 *  @return The subject ID.
		 */
		public String getSubjectId()
		{
			return subjectid;
		}
		
		/**
		 *  Sets the subject ID.
		 *
		 *  @param subjectid The subject ID.
		 */
		public void setSubjectId(String subjectid)
		{
			this.subjectid = subjectid;
		}
		
		public List<CertTreeNode> getChildren()
		{
			return children;
		}
		
		public void clearChildren()
		{
			children.clear();
		}
		
		public int hashCode()
		{
			return subjectid.hashCode();
		}
		
		public boolean equals(Object obj)
		{
			boolean ret = false;
			if (obj instanceof CertTreeNode)
				ret = ((CertTreeNode) obj).getSubjectId().equals(subjectid);
			return ret;
		}
		
		public String toString()
		{
			return subjectid;
		}
	}
}
