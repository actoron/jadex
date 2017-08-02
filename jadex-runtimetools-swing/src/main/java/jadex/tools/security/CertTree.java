package jadex.tools.security;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.WindowConstants;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.operator.DefaultAlgorithmNameFinder;
import org.bouncycastle.operator.DefaultSignatureAlgorithmIdentifierFinder;

import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.commons.gui.ModulateComposite;
import jadex.commons.gui.SGUI;
import jadex.commons.gui.TreeExpansionHandler;
import jadex.commons.security.SCertStore;
import jadex.commons.security.SSecurity;

public class CertTree extends JTree implements TreeModel
{
	/** CA Certificate Icon. */
	public static final Icon CA_CERT_ICON;
	
	/** Certificate Icon. */
	public static final Icon CERT_ICON;
	
	/** CA Certificate Icon with key. */
	public static final Icon CA_CERT_ICON_KEY;
	
	/** Certificate Icon with key. */
	public static final Icon CERT_ICON_KEY;
	
	/** Size of the icons. */
	protected static final int ICON_SIZE = 24;
	
	/** Intialize icons. */
	static
	{
		BufferedImage caimg = null;
		BufferedImage certimg = null;
		BufferedImage cakeyimg = null;
		BufferedImage certkeyimg = null;
		try
		{
			String pckg = CertTree.class.getPackage().getName() + ".images";
			String iconpath = pckg.replace(".", "/") + "/cert_white.png";
			InputStream is = SUtil.getResource(iconpath, CertTree.class.getClassLoader());
			BufferedImage baseimg = SGUI.convertBufferedImageType(ImageIO.read(is), BufferedImage.TYPE_4BYTE_ABGR_PRE);
			is.close();
			
			int sizex = baseimg.getWidth();
			int sizey = baseimg.getHeight();
			
			iconpath = pckg.replace(".", "/") + "/key.png";
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
			
			sizex = ICON_SIZE;
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
	
	/** The loaded certificates used as model. */
	Map<String, Tuple2<String, String>> certmodel;
	
	/** Lookup helper for finding tree nodes. */
	Map<String, CertTreeNode> nodelookup;
	
	/** The root node. */
	protected CertTreeNode root;
	
	/** Model listeners. */
	protected List<TreeModelListener> listeners = new ArrayList<TreeModelListener>();
	
	/** Path to certificate store. */
	protected String storepath;
	
	/**
	 *  Creates the tree.
	 *  
	 *  @param certstorepath Certificate store path.
	 */
	public CertTree(String certstorepath)
	{
		storepath = certstorepath;
		root = createRootNode();
		setEditable(false);
		setShowsRootHandles(true);
		setLargeModel(true);
		setRootVisible(false);
//		setBorder(BorderFactory.createTitledBorder("Certificates"));
		
		certmodel = new HashMap<String, Tuple2<String,String>>();		
		try
		{
			certmodel = SCertStore.convertToSubjectMap(SCertStore.loadCertStore(storepath));
		}
		catch (Exception e)
		{
		}
		
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
					
					JMenuItem addcert = new JMenuItem(new AbstractAction("Add Certificate...")
					{
						public void actionPerformed(ActionEvent e)
						{
//							final JDialog createdia = new JDialog(JOptionPane.getRootFrame(), "Add Certificate", false);
							final JFrame addwindow = new JFrame("Add Certificate");
							addwindow.getRootPane().setLayout(new BorderLayout());
//							createdia.getRootPane().add(new JButton("oisdfjogisdjf"), BorderLayout.CENTER);
//							createdia.getRootPane().add(new CertCreationPanel(CertTree.this.certmodel), BorderLayout.CENTER);
							
							final AddCertPanel certpanel = new AddCertPanel(getSelectedCert(), new AbstractAction()
							{
								public void actionPerformed(ActionEvent e)
								{
									if (e.getID() == ActionEvent.ACTION_PERFORMED)
									{
										AddCertPanel pan = (AddCertPanel) e.getSource();
										Tuple2<String, String> cert = pan.getCertificate();
										
										String subjectid = SSecurity.readCertificateFromPEM(cert.getFirstEntity()).getSubject().toString();
										
										CertTree.this.certmodel.put(subjectid, cert);
										
										updateModel();
									}
									addwindow.setVisible(false);
									addwindow.dispose();
								}
							});
							
							addwindow.getRootPane().add(certpanel, BorderLayout.CENTER);
							addwindow.setSize(800, 600);
							addwindow.setMinimumSize(addwindow.getRootPane().getPreferredSize());
							addwindow.setVisible(true);
							addwindow.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
						}
					});
					
					JMenuItem delcert = new JMenuItem(new AbstractAction("Delete Certificate")
					{
						public void actionPerformed(ActionEvent e)
						{
							Tuple2<String, String> cert = getSelectedCert();
							String name = SSecurity.readCertificateFromPEM(cert.getFirstEntity()).getSubject().toString();
							certmodel.remove(name);
							
							updateModel();
						}
					});
					
					menu.add(addcert);
					menu.add(delcert);
					
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
						String sub = cert.getSubject().toString();
						CertTreeNode node = new CertTreeNode(sub);
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
		
		SCertStore.saveCertStore(storepath, certmodel.values());
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
			
			public String toString()
			{
				return subjectid;
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
			Tuple2<String, String> tup = certmodel.get(subjectid);
			
//			if (tup.getSecondEntity() != null)
//			{
//				PrivateKeyInfo pki = SSecurity.readPrivateKeyFromPEM(tup.getSecondEntity());
//				DefaultAlgorithmNameFinder anf = new DefaultAlgorithmNameFinder();
//				DefaultSignatureAlgorithmIdentifierFinder
//				String alg = anf.getAlgorithmName(pki.getPrivateKeyAlgorithm().getAlgorithm());
////				alg = alg.split("WITH")[1];
//				return subjectid + "( " + alg + ")";
//			}
			
			return subjectid;
		}
	}
}
