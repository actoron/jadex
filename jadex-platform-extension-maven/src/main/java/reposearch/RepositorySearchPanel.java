package reposearch;

import jadex.commons.SUtil;
import jadex.commons.concurrent.ThreadPool;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.gui.SGUI;
import jadex.commons.gui.future.SwingDefaultResultListener;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.HitCollector;
import org.apache.lucene.search.IndexSearcher;
import org.apache.maven.index.ArtifactInfo;
import org.apache.maven.index.MAVEN;
import org.apache.maven.index.NexusIndexer;
import org.apache.maven.index.SearchType;
import org.apache.maven.index.context.IndexCreator;
import org.apache.maven.index.context.IndexUtils;
import org.apache.maven.index.context.IndexingContext;
import org.apache.maven.index.creator.JarFileContentsIndexCreator;
import org.apache.maven.index.creator.MavenArchetypeArtifactInfoIndexCreator;
import org.apache.maven.index.creator.MavenPluginArtifactInfoIndexCreator;
import org.apache.maven.index.creator.MinimalArtifactInfoIndexCreator;
import org.apache.maven.index.updater.IndexUpdateRequest;
import org.apache.maven.index.updater.IndexUpdateResult;
import org.apache.maven.index.updater.IndexUpdater;
import org.apache.maven.index.updater.ResourceFetcher;
import org.apache.maven.index.updater.WagonHelper;
import org.apache.maven.wagon.Wagon;
import org.apache.maven.wagon.events.TransferEvent;
import org.apache.maven.wagon.events.TransferListener;
import org.apache.maven.wagon.observers.AbstractTransferListener;
import org.codehaus.plexus.PlexusContainer;

/**
 *  Panel that allows for searching artifacts from maven repositories.
 */
public class RepositorySearchPanel extends JPanel
{
	//-------- attributes --------
	
	/** The container. */
	protected PlexusContainer plexus;
	
	/** The tree model. */
	protected IdTreeModel tm;
	
	/** The tree. */
	protected JTree tree;
	
	/** The status. */
	protected JLabel status;
		
	/** The repository infos. */
	protected Map<String, RepositoryInfo> repos;
	
	/** The thread pool. */
	protected ThreadPool tp;
	
	/** The current search query text. */
	protected String curquery;
	
	//-------- constructors --------
	
	/**
	 *  Create a new search panel.
	 */
	public RepositorySearchPanel(final PlexusContainer plexus, ThreadPool tp)
	{
		this.plexus = plexus;
		this.tp = tp;
		this.repos = new HashMap<String, RepositorySearchPanel.RepositoryInfo>();
		addRepository("central", "http://repo1.maven.org/maven2");
		
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
							performSearch(tfsearch.getText());
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
		
		performSearch(null);
	}
	
	//-------- methods --------
	
	/**
	 *  Add a new repository.
	 *  @param name The repo name.
	 *  @param url The repo url.
	 */
	protected void addRepository(String name, String url)
	{
		RepositoryInfo repo = new RepositoryInfo(name, url);
		this.repos.put(repo.getUrl(), repo);
	}
	
	/**
	 *  Get a repository per url.
	 *  @param url The url.
	 *  @return The repo.
	 */
	protected RepositoryInfo getRepository(String url)
	{
		return repos.get(url);
	}
	
	/**
	 *  Get the thread pool.
	 *  @return The thread pool.
	 */
	public ThreadPool getThreadPool()
	{
		return tp;
	}

	/**
	 *  Set the current query string.
	 *  Used to abort old searches.
	 *  @param curquery The current query.
	 */
	protected synchronized void setCurrentQuery(String curquery)
	{
		this.curquery = curquery;
	}
	
	/**
	 *  Test if the search is still the current one.
	 *  @param curquery The current search text.
	 */
	protected synchronized boolean isCurrentQuery(String curquery)
	{
		return SUtil.equals(this.curquery, curquery);
	}
	
	/**
	 *  Perform a search using a search expression.
	 */
	public void performSearch(final String exp)
	{
		assert SwingUtilities.isEventDispatchThread();
		
		setCurrentQuery(exp);
		System.out.println("perform search: "+exp);
		
		try
		{
			if(exp!=null && exp.length()>0)
			{
				IdTreeNode root = (IdTreeNode)tm.getRoot();
				root.removeAllChildren();
				
				status.setText("searching '"+exp+"'");
								
	            final int[] cnt = new int[1];
	            final CounterResultListener<Void> lis = new CounterResultListener<Void>(repos.size(), new SwingDefaultResultListener<Void>()
				{
	            	public void customResultAvailable(Void result)
	            	{
	            		status.setText("searching '"+exp+"' ("+(cnt[0])+") finished");
	            	}
				});
	            
	            for(RepositoryInfo repo: repos.values())
	            {
	            	repo.getIndexingContext(plexus, repo, getThreadPool()).addResultListener(new IResultListener<IndexingContext>()
					{
						public void resultAvailable(final IndexingContext context)
						{
							getThreadPool().execute(new Runnable()
							{
								public void run()
								{
									try
									{
										NexusIndexer indexer = plexus.lookup(NexusIndexer.class);

//							            Query q = new QueryParser("title", context.getAnalyzer()).parse("(groupId:jadex OR artifactId:jad) AND packaging:jar");
							            final BooleanQuery query = new BooleanQuery();
							            BooleanQuery subquery = new BooleanQuery();
							            subquery.add(indexer.constructQuery(MAVEN.GROUP_ID, exp, SearchType.SCORED), Occur.SHOULD);
							            subquery.add(indexer.constructQuery(MAVEN.ARTIFACT_ID, exp, SearchType.SCORED), Occur.SHOULD);
							            query.add(subquery, Occur.MUST);
							            query.add(indexer.constructQuery(MAVEN.PACKAGING, "jar", SearchType.EXACT), Occur.MUST);
										
//							            FlatSearchResponse response = indexer.searchFlat(new FlatSearchRequest(query));
//							            int i=0;
//										for(ArtifactInfo ai: response.getResults())
//										{
//											addArtifactInfo(ai);
//											status.setText("searching '"+exp+"' ("+(i++)+")");
//										}
							            
										final IndexReader reader = context.getIndexReader();
										final IndexSearcher searcher = new IndexSearcher(reader);
						//				GavCalculator calc = context.getGavCalculator();
										query.setMaxClauseCount(Integer.MAX_VALUE);
										searcher.search(query, new HitCollector()
										{
											public void collect(int doc, float score)
											{
												if(!isCurrentQuery(exp))
													throw new RuntimeException("Search aborted: "+exp);
												try
												{
													Document d = reader.document(doc);
													final ArtifactInfo ai = IndexUtils.constructArtifactInfo(d, context);
													if(ai!=null)
													{
														ai.repository = context.getRepositoryId();
														ai.context = context.getId();
														
														// System.out.println("found: " + d);
														SwingUtilities.invokeLater(new Runnable()
														{
															public void run()
															{
																addArtifactInfo(ai);
																status.setText("searching '"+exp+"' ("+(cnt[0]++)+")");
															}
														});
													}
												}
												catch(Exception e)
												{
													e.printStackTrace();
												}
											}
										});
										lis.resultAvailable(null);
									}
									catch(Exception e)
									{
//										e.printStackTrace();
										lis.resultAvailable(null);
									}
								}
							});
						}
						
						public void exceptionOccurred(Exception exception)
						{
							lis.resultAvailable(null);
						}
					});
	            }
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
	 *  Add an artifact info to the tree.
	 *  @param ai The artifact info.
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
				insertNode((IdTreeNode)tm.getRoot(), groupn, true);
			}
			final String artkey = groupkey+":"+ai.artifactId;
			IdTreeNode artn = tm.getNode(artkey);
			if(artn==null)
			{
				artn = new IdTreeNode(artkey, ai.artifactId, tm);
//				System.out.println("add artn: "+artn);
				insertNode(groupn, artn, true);
			}
			final String vkey = artkey+":"+ai.version;
			IdTreeNode vn = tm.getNode(vkey);
			if(vn==null)
			{
				vn = new IdTreeNode(vkey, ai.version+" ["+ai.packaging+"]", tm, true);
//				System.out.println("add vn: "+vn);
				insertNode(artn, vn, false);
			}
		}
	}
	
	/**
	 *  Insert a node at alphabetical order in the tree.
	 */
	protected void insertNode(IdTreeNode parent, IdTreeNode child, boolean up)
	{
		int cnt = parent.getChildCount();
		boolean done = false;
		if(cnt>0)
		{
			for(int i=0; i<cnt && !done; i++)
			{
				IdTreeNode tmp = (IdTreeNode)parent.getChildAt(i);
				if((up && tmp.toString().compareTo(child.toString())>=0) 
					|| tmp.toString().compareTo(child.toString())<=0)
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
	 *  Main for testing.
	 */
	public static void main(String[] args)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				JFrame f = new JFrame();
				f.add(new RepositorySearchPanel(MavenArtifactSearch.createPlexus(), new ThreadPool()));
				f.pack();
				f.setLocation(SGUI.calculateMiddlePosition(f));
				f.setVisible(true);
			}
		});
	}
	
	/**
	 *  Repository info class.
	 */
	public static class RepositoryInfo
	{
		//-------- attributes --------
		
		/** The name (and id). */
		protected String name;
		
		/** The repo url. */
		protected String url;
		
		/** The repo cache dir. */
		protected String cachedir;
		
		/** The repo index dir. */
		protected String indexdir;
		
		/** The indexing context. */
		protected IndexingContext context;
		
		//-------- constructors --------

		/**
		 *  Create a new repository info.
		 */
		public RepositoryInfo(String name, String url)
		{
			this(name, url, "./"+name, "./"+name);
		}
		
		/**
		 *  Create a new repository info.
		 */
		public RepositoryInfo(String name, String url, String cachedir, String indexdir)
		{
			this.name = name;
			this.url = url;
			this.cachedir = cachedir;
			this.indexdir = indexdir;
		}

		//-------- methods --------
		
		/**
		 *  Get the name.
		 *  @return The name.
		 */
		public String getName()
		{
			return name;
		}

		/**
		 *  Set the name.
		 *  @param name The name to set.
		 */
		public void setName(String name)
		{
			this.name = name;
		}

		/**
		 *  Get the url.
		 *  @return The url.
		 */
		public String getUrl()
		{
			return url;
		}

		/**
		 *  Set the url.
		 *  @param url The url to set.
		 */
		public void setUrl(String url)
		{
			this.url = url;
		}

		/**
		 *  Get the cachedir.
		 *  @return The cachedir.
		 */
		public String getCachedir()
		{
			return cachedir;
		}

		/**
		 *  Set the cachedir.
		 *  @param cachedir The cachedir to set.
		 */
		public void setCachedir(String cachedir)
		{
			this.cachedir = cachedir;
		}

		/**
		 *  Get the indexdir.
		 *  @return The indexdir.
		 */
		public String getIndexdir()
		{
			return indexdir;
		}

		/**
		 *  Set the indexdir.
		 *  @param indexdir The indexdir to set.
		 */
		public void setIndexdir(String indexdir)
		{
			this.indexdir = indexdir;
		}

		/**
		 *  Get the indexing context.
		 *  Creates it the first time called.
		 *  Checks if an index update is needed.
		 */
		public IFuture<IndexingContext> getIndexingContext(PlexusContainer plexus, RepositoryInfo repo, ThreadPool tp)
		{
			final Future<IndexingContext> ret = new Future<IndexingContext>();
			
			if(context==null)
			{
		    	try
		    	{
			        // Creators we want to use (search for fields it defines)
			        List<IndexCreator> indexers = new ArrayList<IndexCreator>();
			        indexers.add(plexus.lookup(IndexCreator.class, MinimalArtifactInfoIndexCreator.ID));
			        indexers.add(plexus.lookup(IndexCreator.class, JarFileContentsIndexCreator.ID));
			        indexers.add(plexus.lookup(IndexCreator.class, MavenPluginArtifactInfoIndexCreator.ID));
			        indexers.add(plexus.lookup(IndexCreator.class, MavenArchetypeArtifactInfoIndexCreator.ID));
			               
			        // lookup the indexer instance from plexus
			        NexusIndexer indexer = plexus.lookup(NexusIndexer.class);
			
			        // Create context for central repository index
			        context = indexer.addIndexingContextForced(getName(), 
			        	getName(), new File(getCachedir()), new File(getIndexdir()), getUrl(), null, indexers);
		    	}
		    	catch(Exception e)
		    	{
		    		e.printStackTrace();
		    		ret.setException(e);
//		    		throw new RuntimeException(e);
		    	}
			}
			
			if(context!=null)
			{
				// check up to date
				Date contime = context.getTimestamp();
				
				boolean update = contime==null;
				if(!update)
				{
					Date curtime = new Date();
					long oneday = 24 * 60 * 60 * 1000;
					long days = (curtime.getTime()/oneday)-(contime.getTime()/oneday);
					update = days>3;
				}
				
	    		if(update)
	    		{
	    			updateIndex(plexus, tp).addResultListener(new ExceptionDelegationResultListener<Void, IndexingContext>(ret)
					{
	    				public void customResultAvailable(Void result)
	    				{
	    					ret.setResult(context);
	    				}
					});
	    		}
	    		else
	    		{
	    			ret.setResult(context);
	    		}
			}
			
			return ret;
		}
		
		/**
	     *  Updates the index.
	     */
		public IFuture<Void> updateIndex(final PlexusContainer plexus, ThreadPool tp)
		{
			final Future<Void> ret = new Future<Void>();
			
			tp.execute(new Runnable()
			{
				public void run()
				{
					try
					{
						System.out.println("Updating Index...");
						// Create ResourceFetcher implementation to be used with
						// IndexUpdateRequest
						// Here, we use Wagon based one as shorthand, but all we need is a
						// ResourceFetcher implementation
						Wagon wagon = plexus.lookup(Wagon.class, "http");
						TransferListener listener = new AbstractTransferListener()
						{
							public void transferStarted(TransferEvent event)
							{
								System.out.print(" Downloading "+event.getResource().getName());
							}
				
							public void transferProgress(TransferEvent event, byte[] buffer, int length)
							{
							}
				
							public void transferCompleted(TransferEvent event)
							{
								System.out.println(" - Done");
							}
						};
						
						ResourceFetcher fetcher = new WagonHelper.WagonFetcher(wagon, listener, null, null);
				
						Date ts = context.getTimestamp();
						IndexUpdateRequest request = new IndexUpdateRequest(context, fetcher);
						IndexUpdater updater = plexus.lookup(IndexUpdater.class);
						IndexUpdateResult res = updater.fetchAndUpdateIndex(request);
						if(res.isFullUpdate())
						{
							System.out.println("Full update happened!");
						}
						else if(res.getTimestamp().equals(ts))
						{
							System.out.println("No update needed, index is up to date!");
						}
						else
						{
							System.out.println("Incremental update happened, change covered "
								+ ts + " - " + res.getTimestamp() + " period.");
						}
						
						ret.setResult(null);
					}
					catch(Exception e)
			    	{
			    		e.printStackTrace();
			    		ret.setException(e);
//			    		throw new RuntimeException(e);
			    	}
				}
			});

			return ret;
		}
	}
	
	/**
	 *  Tree model that allows looking up nodes per id.
	 */
	public static class IdTreeModel extends DefaultTreeModel
	{
		//-------- attributes --------
		
		/** The id map (id -> node). */
		protected Map<String, IdTreeNode> nodes;
		
		//-------- constructors --------
		
		/**
		 *  Create a new tree model.
		 */
		public IdTreeModel()
		{
			super(null, false);
			
			assert SwingUtilities.isEventDispatchThread();
			
			this.nodes = new HashMap<String, RepositorySearchPanel.IdTreeNode>();
		}
		
		//-------- methods --------
		
		/**
		 *  Add a new node.
		 *  @param node The node.
		 */
		public void addNode(IdTreeNode node)
		{
			assert SwingUtilities.isEventDispatchThread();
			
			nodes.put(node.getId(), node);
		}
		
		/**
		 *  Remove a node.
		 *  @param node The node.
		 */
		public void removeNode(IdTreeNode node)
		{
			assert SwingUtilities.isEventDispatchThread();
			
			deregisterAll(node);
		}
		
		/**
		 *  Deregister a node and all its children.
		 *  @param node The node.
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
		 *  Get a node per id.
		 *  @param id The node id.
		 *  @return The node.
		 */
		public IdTreeNode getNode(String id)
		{
			assert SwingUtilities.isEventDispatchThread();
			
			return nodes.get(id);
		}
		
	}
	
	/**
	 *  Id tree node.
	 */
	public static class IdTreeNode extends DefaultMutableTreeNode
	{
		//-------- attributes --------

		/** The node id. */
		protected String key;
		
		/** The node name. */
		protected String name;
		
		/** The tree model. */
		protected IdTreeModel tm;
		
		/** Flag if is leaf. */
		protected boolean leaf;
		
		//-------- constructors --------
		
		/**
		 *  Create a new node.
		 */
		public IdTreeNode(String key, String name, IdTreeModel tm)
		{
			this(key, name, tm, false);
		}
		
		/**
		 *  Create a new node.
		 */
		public IdTreeNode(String key, String name, IdTreeModel tm, boolean leaf)
		{
			this.key = key;
			this.name = name!=null? name: key;
			this.tm = tm;
			this.leaf = leaf;
		}
		
		/**
		 *  Add a new child.
		 *  @param child The child.
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
		 *  Insert a new child.
		 *  @param child The child.
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
		 *  Remove a child.
		 *  @param idx The index.
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
		 *  Get the id.
		 *  @return The id.
		 */
		public String getId()
		{
			return key;
		}

		/**
		 *  Test if node is leaf.
		 *  @return True, if is leaf.
		 */
		public boolean isLeaf()
		{
			return leaf;
		}

		/**
		 *  Get the string representation.
		 *  @return The string representation.
		 */
		public String toString()
		{
			return name;
		}
	}
}
