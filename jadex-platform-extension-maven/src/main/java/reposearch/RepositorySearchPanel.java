package reposearch;

import jadex.commons.gui.SGUI;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.HitCollector;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.maven.index.ArtifactInfo;
import org.apache.maven.index.MAVEN;
import org.apache.maven.index.NexusIndexer;
import org.apache.maven.index.SearchType;
import org.apache.maven.index.context.IndexUtils;
import org.apache.maven.index.context.IndexingContext;
import org.codehaus.plexus.PlexusContainer;

/**
 * 
 */
public class RepositorySearchPanel extends JPanel
{
	/** The container. */
	protected PlexusContainer plexus;
	
	/** The tree model. */
	protected IdTreeModel tm;
	
	/** The tree. */
	protected JTree tree;
	
	/** The status. */
	protected JLabel status;
	
	/** The indexing context. */
	protected IndexingContext context;
	
	/**
	 * 
	 */
	public RepositorySearchPanel(final PlexusContainer plexus)
	{
		this.plexus = plexus;
		
		final JTextField tfsearch = new JTextField();
		tfsearch.addKeyListener(new KeyListener()
		{
			protected boolean dirty = false;
			protected Timer t;
			
			{
				t = new Timer(500, new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						if(dirty)
						{
							dirty = false;
						}
						else
						{
							t.stop();
							performSearch(context, tfsearch.getText());
						}
					}
				});
			}
			
			public void keyTyped(KeyEvent e)
			{
				if(!t.isRunning())
				{
					t.start();
				}
				else
				{
					dirty = true;
				}
			}
			
			public void keyReleased(KeyEvent e)
			{
			}
			
			public void keyPressed(KeyEvent e)
			{
			}
		});
		
		tm = new IdTreeModel();
		tm.setRoot(new IdTreeNode("root", null, tm));
		tree = new JTree(tm);
		tree.setRootVisible(false);

		status = new JLabel("idle");
		
		setLayout(new GridBagLayout());
		
		add(new JLabel("Enter group id, artifact id or pattern:"), new GridBagConstraints(0,0,
			1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.VERTICAL, new Insets(2,2,2,2),0,0));
		
		add(tfsearch, new GridBagConstraints(0,1,1,1,1,0,
			GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(2,2,2,2),0,0));
		
		add(new JLabel("Search Results:"), new GridBagConstraints(0,2,
			1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.VERTICAL, new Insets(2,2,2,2),0,0));
		
		add(new JScrollPane(tree), new GridBagConstraints(0,3,
			1,1,1,1,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(2,2,2,2),0,0));
		
		add(status, new GridBagConstraints(0,4,
			1,1,1,0,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(2,2,2,2),0,0));
		
		// init content
		
        File cachedir = new File("target/central-cache");
        File indexdir = new File("target/central-index");
		
		context = MavenArtifactSearch.createIndexingContext(plexus, cachedir, indexdir, "central-context", "central", "http://repo1.maven.org/maven2");
//		MavenArtifactSearch.updateIndex(plexus, context);
		performSearch(context, null);
	}
	
	/**
	 * 
	 */
	public void performSearch(final IndexingContext context, final String exp)
	{
		assert SwingUtilities.isEventDispatchThread();
		
//		System.out.println("perform search: "+exp);
		
		try
		{
			if(exp!=null && exp.length()>0)
			{
				IdTreeNode root = (IdTreeNode)tm.getRoot();
				root.removeAllChildren();
				
				status.setText("searching '"+exp+"'");
				
				NexusIndexer indexer = plexus.lookup(NexusIndexer.class);
				
//	            Query q = new QueryParser("title", context.getAnalyzer()).parse("(groupId:jadex OR artifactId:jad) AND packaging:jar");
	            BooleanQuery query = new BooleanQuery();
	            BooleanQuery subquery = new BooleanQuery();
	            subquery.add(indexer.constructQuery(MAVEN.GROUP_ID, exp, SearchType.SCORED), Occur.SHOULD);
	            subquery.add(indexer.constructQuery(MAVEN.ARTIFACT_ID, exp, SearchType.SCORED), Occur.SHOULD);
	            query.add(subquery, Occur.MUST);
	            query.add(indexer.constructQuery(MAVEN.PACKAGING, "jar", SearchType.EXACT), Occur.MUST);
				
//	            FlatSearchResponse response = indexer.searchFlat(new FlatSearchRequest(query));
//	            int i=0;
//				for(ArtifactInfo ai: response.getResults())
//				{
//					addArtifactInfo(ai);
//					status.setText("searching '"+exp+"' ("+(i++)+")");
//				}
				
				final IndexReader reader = context.getIndexReader();
				final IndexSearcher searcher = new IndexSearcher(reader);
//				GavCalculator calc = context.getGavCalculator();
				final int[] cnt = new int[1];
				searcher.search(query, new HitCollector()
				{
					public void collect(int doc, float score)
					{
						try
						{
							Document d = reader.document(doc);
							ArtifactInfo ai = IndexUtils.constructArtifactInfo(d, context);
							if(ai != null)
							{
								ai.repository = context.getRepositoryId();
								ai.context = context.getId();
							}
//							System.out.println("found: " + d);
							addArtifactInfo(ai);
							status.setText("searching '"+exp+"' ("+(cnt[0]++)+")");
						}
						catch(Exception e)
						{
							e.printStackTrace();
						}
					}
				});
				status.setText("searching '"+exp+"' ("+(cnt[0])+") finished");
			}
			else
			{
				status.setText("idle");
			}
//			else
//			{
//				final IndexReader ir = context.getIndexReader();
//
//				for(int i=0; i<ir.maxDoc() && i<1; i++)
//				{
//					if(!ir.isDeleted(i))
//					{
//						Document doc = ir.document(i);
//						ArtifactInfo ai = IndexUtils.constructArtifactInfo(doc, context);
//						addArtifactInfo(ai);
//						status.setText("searching '"+exp+"' ("+(i++)+")");
//					}
//				}
//			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			status.setText("idle '"+exp+"'");
		}
	}
	
	/**
	 * 
	 */
	protected void addArtifactInfo(ArtifactInfo ai)
	{
		assert SwingUtilities.isEventDispatchThread();
		
		if(ai!=null && ai.classifier==null)
		{
			final String groupkey = ai.groupId;
			
			IdTreeNode groupn = tm.getNode(groupkey);
			if(groupn==null)
			{
				groupn = new IdTreeNode(groupkey, null, tm);
				insertNode((IdTreeNode)tm.getRoot(), groupn);
			}
			final String artkey = groupkey+":"+ai.artifactId;
			IdTreeNode artn = tm.getNode(artkey);
			if(artn==null)
			{
				artn = new IdTreeNode(artkey, ai.artifactId, tm);
//				System.out.println("add artn: "+artn);
				insertNode(groupn, artn);
			}
			final String vkey = artkey+":"+ai.version;
			IdTreeNode vn = tm.getNode(vkey);
			if(vn==null)
			{
				vn = new IdTreeNode(vkey, ai.version+" ["+ai.packaging+"]", tm, true);
//				System.out.println("add vn: "+vn);
				insertNode(artn, vn);
			}
		}
	}
	
	/**
	 * 
	 */
	protected void insertNode(IdTreeNode parent, IdTreeNode child)
	{
		int cnt = parent.getChildCount();
		boolean done = false;
		if(cnt>0)
		{
			for(int i=0; i<cnt && !done; i++)
			{
				IdTreeNode tmp = (IdTreeNode)parent.getChildAt(i);
				if(tmp.toString().compareTo(child.toString())>=0)
				{
					parent.insert(child, i);
					done = true;
				}
			}
		}
		
		if(!done)
		{
			parent.add(child);
		}
	}
	
	/**
	 * 
	 */
	public static void main(String[] args)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				JFrame f = new JFrame();
				f.add(new RepositorySearchPanel(MavenArtifactSearch.createPlexus()));
				f.pack();
				f.setLocation(SGUI.calculateMiddlePosition(f));
				f.setVisible(true);
			}
		});
	}
	
	/**
	 * 
	 */
	public static class IdTreeModel extends DefaultTreeModel
	{
		protected Map<String, IdTreeNode> nodes;
		
		/**
		 * 
		 */
		public IdTreeModel()
		{
			super(null, false);
			
			assert SwingUtilities.isEventDispatchThread();
			
			this.nodes = new HashMap<String, RepositorySearchPanel.IdTreeNode>();
		}
		
		/**
		 * 
		 */
		public void addNode(IdTreeNode node)
		{
			assert SwingUtilities.isEventDispatchThread();
			
			nodes.put(node.getId(), node);
		}
		
		/**
		 * 
		 */
		public void removeNode(IdTreeNode node)
		{
			assert SwingUtilities.isEventDispatchThread();
			
			deregisterAll(node);
		}
		
		/**
		 * 
		 */
		protected void deregisterAll(IdTreeNode node)
		{
			nodes.remove(node.getId());
			
			for(int i=node.getChildCount()-1; i>=0; i--)
			{
				deregisterAll((IdTreeNode)node.getChildAt(i));
			}
		}
		
		/**
		 * 
		 */
		public IdTreeNode getNode(String id)
		{
			assert SwingUtilities.isEventDispatchThread();
			
			return nodes.get(id);
		}
		
	}
	
	/**
	 * 
	 */
	public static class IdTreeNode extends DefaultMutableTreeNode
	{
		protected String key;
		protected String name;
		protected IdTreeModel tm;
		protected boolean leaf;
		
		/**
		 * 
		 */
		public IdTreeNode(String key, String name, IdTreeModel tm)
		{
			this(key, name, tm, false);
		}
		
		/**
		 * 
		 */
		public IdTreeNode(String key, String name, IdTreeModel tm, boolean leaf)
		{
			this.key = key;
			this.name = name!=null? name: key;
			this.tm = tm;
			this.leaf = leaf;
		}
		
		/**
		 * 
		 */
		public void add(MutableTreeNode child)
		{
			assert SwingUtilities.isEventDispatchThread();
			
			IdTreeNode itn = (IdTreeNode)child;
			tm.addNode(itn);
			super.add(itn);
			tm.nodesWereInserted(this, new int[]{getChildCount()-1});
		}
		
		/**
		 * 
		 */
		public void insert(MutableTreeNode child, int index)
		{
			assert SwingUtilities.isEventDispatchThread();
			
			IdTreeNode itn = (IdTreeNode)child;
			tm.addNode(itn);
			super.insert(child, index);
			tm.nodesWereInserted(this, new int[]{index});
		}

		/**
		 * 
		 */
		public void remove(int idx)
		{
			assert SwingUtilities.isEventDispatchThread();
			
			IdTreeNode child = (IdTreeNode)getChildAt(idx);
			tm.removeNode(child);
			super.remove(idx);
			tm.nodesWereRemoved(this, new int[]{idx}, new TreeNode[]{child});
		}
		
		/**
		 * 
		 */
		public String getId()
		{
			return key;
		}

		/**
		 * 
		 */
		public boolean isLeaf()
		{
			return leaf;
		}

		/**
		 * 
		 */
		public String toString()
		{
			return name;
		}
	}
}
