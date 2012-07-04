package reposearch;

import jadex.base.gui.asynctree.AbstractTreeNode;
import jadex.base.gui.asynctree.AsyncTreeModel;
import jadex.base.gui.asynctree.ITreeNode;
import jadex.commons.gui.SGUI;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.maven.index.ArtifactInfo;
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
	protected AsyncTreeModel tm;
	
	/** The tree. */
	protected JTree tree;
	
	/** The status. */
	protected JLabel status;
	
	/**
	 * 
	 */
	public RepositorySearchPanel(PlexusContainer plexus)
	{
		this.plexus = plexus;
		
		this.setLayout(new GridBagLayout());
		
		this.add(new JLabel("Enter group id, artifact id or pattern:"), new GridBagConstraints(0,0,
			1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.VERTICAL, new Insets(2,2,2,2),0,0));
		
		this.add(new JTextField(), new GridBagConstraints(0,1,1,1,1,0,
			GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(2,2,2,2),0,0));
		
		this.add(new JLabel("Search Results:"), new GridBagConstraints(0,2,
			1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.VERTICAL, new Insets(2,2,2,2),0,0));

		tm = new AsyncTreeModel();
		tree = new JTree(tm);
		tree.setRootVisible(true);
		tm.setRoot(new TreeNode("root", (ITreeNode)tm.getRoot(), tm, tree));
		
		this.add(new JScrollPane(tree), new GridBagConstraints(0,3,
			1,1,1,1,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(2,2,2,2),0,0));
		
		status = new JLabel();
		this.add(status, new GridBagConstraints(0,4,
			1,1,1,0,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(2,2,2,2),0,0));
		
		// init content
		
        File cachedir = new File("target/central-cache");
        File indexdir = new File("target/central-index");
		
		IndexingContext context = MavenArtifactSearch.createIndexingContext(plexus, cachedir, indexdir, "central-context", "central", "http://repo1.maven.org/maven2");
//		MavenArtifactSearch.updateIndex(plexus, context);
		performSearch(context, null);
	}
	
	/**
	 * 
	 */
	public void performSearch(IndexingContext context, String query)
	{
		try
		{
			final IndexReader ir = context.getIndexReader();

			for(int i=0; i<ir.maxDoc(); i++)
			{
				if(!ir.isDeleted(i))
				{
					Document doc = ir.document(i);
					ArtifactInfo ai = IndexUtils.constructArtifactInfo(doc, context);
					if(ai!=null)
					{
						final String key = ai.groupId+":"+ai.artifactId;
						
						ITreeNode node = tm.getNode(key);
						if(node==null)
						{
//							System.out.println("adding: "+key);
							TreeNode tn = new TreeNode(key, (ITreeNode)tm.getRoot(), tm, tree);
							((AbstractTreeNode)tm.getRoot()).addChild(tn);
//							tm.addNode(tn); // AsyncTreeModel bug!
							
							status.setText("building tree: "+i);
						}
					}
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 */
	public static void main(String[] args)
	{
		JFrame f = new JFrame();
		f.add(new RepositorySearchPanel(MavenArtifactSearch.createPlexus()));
		f.pack();
		f.setLocation(SGUI.calculateMiddlePosition(f));
		f.setVisible(true);
	}
	
	/**
	 * 
	 */
	public static class TreeNode extends AbstractTreeNode
	{
		String key;
		
		public TreeNode(String key, ITreeNode parent, AsyncTreeModel model, JTree tree)
		{
			super(parent, model, tree);
			this.key = key;
		}
		
		public Icon getIcon()
		{
			return null;
		}
		
		public String getTooltipText()
		{
			return null;
		}
		
		public Object getId()
		{
			return key;
		}
		
		protected void searchChildren()
		{
		}
		
		public String toString()
		{
			return key;
		}
	}
}
