package jadex.tools.security;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.bouncycastle.cert.X509CertificateHolder;

import jadex.commons.FileWatcher;
import jadex.commons.ICommand;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.commons.gui.ModulateComposite;
import jadex.commons.gui.SGUI;
import jadex.commons.gui.TreeExpansionHandler;
import jadex.commons.security.PemKeyPair;
import jadex.commons.security.SCertStore;
import jadex.commons.security.SSecurity;

/**
 *  Certificate tree.
 *
 */
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
	protected static final int ICON_SIZE = 16;
	
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
	Map<String, PemKeyPair> certmodel = new HashMap<>();;
	
	/** Lookup helper for finding tree nodes. */
	Map<String, CertTreeNode> nodelookup;
	
	/** The root node. */
	protected CertTreeNode root;
	
	/** Model listeners. */
	protected List<TreeModelListener> listeners = new ArrayList<TreeModelListener>();
	
	/** Path to certificate store. */
	protected String storrepath;
	
	/** Store save command. */
	protected ICommand<byte[]> storesavecommand;
	
	/** File watcher for the store. */
//	protected FileWatcher storewatch;
	
	/**
	 *  Creates the tree.
	 *  
	 *  @param certstorepath Certificate store path.
	 */
	public CertTree()
	{
		getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		
		root = createRootNode();
		setEditable(false);
		setShowsRootHandles(true);
		setLargeModel(true);
		setRootVisible(true);
//		setBorder(BorderFactory.createTitledBorder("Certificates"));
		
//		try
//		{
//			certmodel = SCertStore.convertToSubjectMap(SCertStore.loadCertStore(storepath));
//		}
//		catch (Exception e)
//		{
//		}
		
		this.nodelookup = new HashMap<String, CertTreeNode>();
		setModel(this);
		new TreeExpansionHandler(this);
		
		setCellRenderer(new DefaultTreeCellRenderer()
		{
			private static final long serialVersionUID = 8920125694443497650L;

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
				if (e.getButton() == MouseEvent.BUTTON3)
				{
					JPopupMenu menu = new JPopupMenu();
					menu.setLightWeightPopupEnabled(false);
					
					JMenuItem addcert = new JMenuItem(new AbstractAction("Add Certificate...")
					{
						private static final long serialVersionUID = 991210496232594322L;

						public void actionPerformed(ActionEvent e)
						{
//							final JDialog createdia = new JDialog(JOptionPane.getRootFrame(), "Add Certificate", false);
							final JFrame addwindow = new JFrame("Add Certificate");
							addwindow.getRootPane().setLayout(new BorderLayout());
//							createdia.getRootPane().add(new JButton("oisdfjogisdjf"), BorderLayout.CENTER);
//							createdia.getRootPane().add(new CertCreationPanel(CertTree.this.certmodel), BorderLayout.CENTER);
							
							final AddCertPanel certpanel = new AddCertPanel(getSelectedCert(), new AbstractAction()
							{
								private static final long serialVersionUID = 6728859103551025243L;

								public void actionPerformed(ActionEvent e)
								{
									if (e.getID() == ActionEvent.ACTION_PERFORMED)
									{
										AddCertPanel pan = (AddCertPanel) e.getSource();
										PemKeyPair cert = pan.getCertificate();
										
										String subjectid = SSecurity.getCommonName(SSecurity.readCertificateFromPEM(cert.getCertificate()).getSubject());
										
										CertTree.this.certmodel.put(subjectid, cert);
										
										updateAndSave();
									}
									addwindow.setVisible(false);
									addwindow.dispose();
								}
							});
							
							addwindow.getRootPane().add(certpanel, BorderLayout.CENTER);
							addwindow.setSize(800, 600);
							addwindow.setMinimumSize(addwindow.getRootPane().getPreferredSize());
							addwindow.setLocation(SGUI.calculateMiddlePosition(addwindow));
							addwindow.setVisible(true);
							addwindow.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
						}
					});
					
					JMenuItem delkey = new JMenuItem(new AbstractAction("Delete Key")
					{
						private static final long serialVersionUID = -7584770925534751334L;

						public void actionPerformed(ActionEvent e)
						{
							PemKeyPair cert = getSelectedCert();
							if (cert == null)
								return;
							cert.setKey(null);
							
							updateAndSave();
						}
					});
					
					JMenuItem delcert = new JMenuItem(new AbstractAction("Delete Certificate")
					{
						private static final long serialVersionUID = 3920965217865964794L;

						public void actionPerformed(ActionEvent e)
						{
							PemKeyPair cert = getSelectedCert();
							if (cert == null)
								return;
							String name = SSecurity.getCommonName(SSecurity.readCertificateFromPEM(cert.getCertificate()).getSubject());
							certmodel.remove(name);
							
							updateAndSave();
						}
					});
					
					menu.add(addcert);
					if (getSelectedCert() != null && getSelectedCert().getKey() != null)
						menu.add(delkey);
					menu.add(delcert);
					
					menu.show(CertTree.this, e.getX(), e.getY());
				}
			}
		});
		
//		JMenuItem mi = new JMenuItem("Add Root Certificate");
//		mi.addActionListener(new AbstractAction()
//		{
//			public void actionPerformed(ActionEvent e)
//			{
//				
//			}
//		});
		
//		setRootVisible(false);
		
		/*addHierarchyListener(new HierarchyListener()
		{
			public void hierarchyChanged(HierarchyEvent e)
			{
				boolean visible = isVisible();
				visible &= isDisplayable();
				
				Container p = getParent();
				visible &= p != null && p.isDisplayable();
				
				while (p != null)
				{
					visible &= p.isVisible();
					visible &= p.isDisplayable();
					p = p.getParent();
				}
			}
		});*/
		
//		loadAndUpdate();
	}
	
	/**
	 *  Override
	 */
	public Object getRoot()
	{
		return root;
	}

	/**
	 *  Override
	 */
	public Object getChild(Object parent, int index)
	{
		return ((CertTreeNode) parent).getChildren().get(index);
	}

	/**
	 *  Override
	 */
	public int getChildCount(Object parent)
	{
		return ((CertTreeNode) parent).getChildren().size();
	}

	/**
	 *  Override
	 */
	public boolean isLeaf(Object node)
	{
		return ((CertTreeNode) node).getChildren().isEmpty();
	}

	/**
	 *  Override
	 */
	public void valueForPathChanged(TreePath path, Object newValue)
	{
	}

	/**
	 *  Override
	 */
	public int getIndexOfChild(Object parent, Object child)
	{
		return ((CertTreeNode) parent).getChildren().indexOf(child);
	}

	/**
	 *  Override
	 */
	public void addTreeModelListener(TreeModelListener l)
	{
		listeners.add(l);
	}

	/**
	 *  Override
	 */
	public void removeTreeModelListener(TreeModelListener l)
	{
		listeners.remove(l);
	}
	
	/**
	 *  Loads and updates the model.
	 */
	public void load(byte[] storedata)
	{
		try
		{
//			certmodel = SCertStore.convertToSubjectMap(SCertStore.loadCertStore(storedata));
			certmodel = SCertStore.loadCertStore(storedata);
		}
		catch (Exception e)
		{
		}
		update();
	}
	
	/**
	 *  Creates a store watch.
	 */
//	protected void createStoreWatch()
//	{
//		if (storewatch != null)
//			storewatch.stop();
//		storewatch = new FileWatcher(storepath, new Runnable()
//		{
//			public void run()
//			{
//				SwingUtilities.invokeLater(new Runnable()
//				{
//					public void run()
//					{
//						loadAndUpdate();
//					}
//				});
//			}
//		}, true);
//	}
	
	/**
	 *  Updates the model.
	 */
	protected void update()
	{
		root.clearChildren();
		nodelookup.clear();
		clearToggledPaths();
		
		synchronized(certmodel)
		{
			for (Map.Entry<String, PemKeyPair> entry : certmodel.entrySet())
			{
				List<X509CertificateHolder> certchain = SSecurity.readCertificateChainFromPEM(entry.getValue().getCertificate());
				Collections.reverse(certchain);
				
				for (int i = 0; i < certchain.size(); ++i)
				{
					X509CertificateHolder cert = certchain.get(i);
					
					if (!nodelookup.containsKey(SSecurity.getCommonName(cert.getSubject())))
					{
						String sub = SSecurity.getCommonName(cert.getSubject());
						CertTreeNode node = new CertTreeNode(sub);
						nodelookup.put(SSecurity.getCommonName(cert.getSubject()), node);
						
						CertTreeNode issuernode = nodelookup.get(SSecurity.getCommonName(cert.getIssuer()).toString());
						
						if (cert.getIssuer().equals(cert.getSubject()) || issuernode == null)
						{
							root.addChild(node);
						}
						else
						{
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
	
	/**
	 *  Updates and saves model.
	 */
	protected byte[] save()
	{
		update();
//		SCertStore.saveCertStore(storepath, certmodel.values());
		return SCertStore.saveCertStore(certmodel.values());
	}
	
	/**
	 *  Updates and saves model.
	 */
	protected void updateAndSave()
	{
		update();
		if (storesavecommand != null)
			storesavecommand.execute(save());
	}
	
	/**
	 *  Sets the store save command.
	 *  @param command The command.
	 */
	protected void setSaveCommand(ICommand<byte[]> command)
	{
		storesavecommand = command;
	}
	
	/**
	 *  Gets the selected certificate.
	 *  @return The selected certificate.
	 */
	protected PemKeyPair getSelectedCert()
	{
		PemKeyPair ret = null;
		
		if (getSelectionCount() > 0)
		{
			Object onode = getSelectionModel().getSelectionPath().getLastPathComponent();
			if (onode == root)
				return null;
			CertTreeNode node = (CertTreeNode) onode;
			ret = certmodel.get(node.getSubjectId());
		}
		
		return ret;
	}
	
	/**
	 *  Creates the root node.
	 */
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
		PemKeyPair cert = certmodel.get(node.getSubjectId());
		if (cert != null)
			ret = SSecurity.isCaCertificate(cert.getCertificate());
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
		PemKeyPair cert = certmodel.get(node.getSubjectId());
		if (cert != null)
			ret = cert.getKey() != null;
		return ret;
	}
	
	public static final List<Tuple2<String, String>> loadCertStore(String path)
	{
		File file = new File(path);
		List<Tuple2<String, String>> ret = null;
		
		if (file.exists())
		{
			ZipInputStream zis = null;
			try
			{
				zis = new ZipInputStream(new FileInputStream(file));
				Map<String, String[]> map = new HashMap<String, String[]>();
				ret = new ArrayList<Tuple2<String,String>>();
				
				ZipEntry entry = null;
				while((entry = zis.getNextEntry()) != null)
				{
					if (entry.getName().endsWith(".crt") || entry.getName().endsWith(".key"))
					{
						String basename = entry.getName().substring(0, entry.getName().length() - 4);
						String[] tup = map.get(basename);
						if (tup == null)
						{
							tup = new String[2];
							map.put(basename, tup);
						}
						
						if (entry.getName().endsWith(".crt"))
						{
							String crt = new String(SUtil.readStream(zis), SUtil.UTF8);
							tup[0] = crt;
						}
						else if (entry.getName().endsWith(".key"))
						{
							String key = new String(SUtil.readStream(zis), SUtil.UTF8);
							tup[1] = key;
						}
					}
				}
				
				for (String[] val : map.values())
				{
					if (val[0] != null)
						ret.add(new Tuple2<String, String>(val[0], val[1]));
				}
				
				zis.close();
				zis = null;
			}
			catch (Exception e)
			{
				e.printStackTrace();
				ret = null;
			}
			finally
			{
				try
				{
					if (zis != null)
						zis.close();
				}
				catch(Exception e)
				{
				}
			}
		}
		
		return ret;
	}
	
	public static void saveCertStore(String path, Collection<Tuple2<String, String>> certs)
	{
		ZipOutputStream zos = null;
		try
		{
			File tmpfile = File.createTempFile("certstore", ".zip");
			
			zos = new ZipOutputStream(new FileOutputStream(tmpfile));
			
			for (Tuple2<String, String> cert : certs)
			{
				String name = SSecurity.getCommonName(SSecurity.readCertificateFromPEM(cert.getFirstEntity()).getSubject());
				
				ZipEntry entry = new ZipEntry(name + ".crt");
				zos.putNextEntry(entry);
				zos.write(cert.getFirstEntity().getBytes(SUtil.UTF8));
				zos.closeEntry();
				
				if (cert.getSecondEntity() != null)
				{
					entry = new ZipEntry(name + ".key");
					zos.putNextEntry(entry);
					zos.write(cert.getSecondEntity().getBytes(SUtil.UTF8));
					zos.closeEntry();
				}
			}
			
			zos.close();
			zos = null;
			
			File file = new File(path);
			SUtil.moveFile(tmpfile, file);
		}
		catch (Exception e)
		{
			throw SUtil.throwUnchecked(e);
		}
		finally
		{
			try
			{
				if (zos != null)
					zos.close();
			}
			catch(Exception e)
			{
			}
		}
	}
	
	public static final Map<String, Tuple2<String, String>> convertToSubjectMap(Collection<Tuple2<String, String>> certs)
	{
		Map<String, Tuple2<String, String>> ret = new HashMap<String, Tuple2<String,String>>();
		for (Tuple2<String, String> cert : certs)
		{
			String key = SSecurity.readCertificateFromPEM(cert.getFirstEntity()).getSubject().toString();
			ret.put(key, cert);
		}
		return ret;
	}
	
	/**
	 *  Node in the tree.
	 *
	 */
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
			PemKeyPair keypair = certmodel.get(subjectid);
			
			String name = "Unknown";
			if (keypair != null)
				name = subjectid + " (" + SSecurity.getCertSigAlg(keypair.getCertificate()) + ")";
			
			return name;
		}
	}
}
