package jadex.base.gui.reposearch;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIDefaults;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

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
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusContainer;

import jadex.base.gui.idtree.IdTreeCellRenderer;
import jadex.base.gui.idtree.IdTreeModel;
import jadex.base.gui.idtree.IdTreeNode;
import jadex.commons.SUtil;
import jadex.commons.concurrent.IThreadPool;
import jadex.commons.concurrent.ThreadPool;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.gui.PropertiesPanel;
import jadex.commons.gui.SGUI;
import jadex.commons.gui.future.SwingDefaultResultListener;
import jadex.commons.gui.future.SwingExceptionDelegationResultListener;

/**
 *  Panel that allows for searching artifacts from maven repositories.
 */
public class RepositorySearchPanel extends JPanel
{
	/** The icons. */
	protected static final UIDefaults	icons	= new UIDefaults(new Object[]
	{
		"jar", SGUI.makeIcon(RepositorySearchPanel.class, "/jadex/base/gui/images/jar.png"),
		"folder", SGUI.makeIcon(RepositorySearchPanel.class, "/jadex/base/gui/images/folder4.png")
	});
	
	//-------- attributes --------
	
	/** The container. */
	protected PlexusContainer plexus;
	
	/** The tree model. */
	protected IdTreeModel<ArtifactInfo> tm;
	
	/** The tree. */
	protected JTree tree;
	
	/** The status. */
	protected JLabel status;
			
	/** The repo combo. */
	protected JComboBox cbrepos;
	
	/** The thread pool. */
	protected IThreadPool tp;
	
	/** The current search query text. */
	protected String curquery;
	
	/** The repository infos. */
	protected Map<String, RepositoryInfo> repos;
	
	protected JTextField tfgi;
	protected JTextField tfai;
	protected JTextField tfv;
	
	//-------- constructors --------
	
	/**
	 *  Create a new search panel.
	 */
	public RepositorySearchPanel(final PlexusContainer plexus, IThreadPool tp)
	{
		this.plexus = plexus;
		this.tp = tp;
		this.repos = new LinkedHashMap<String, RepositoryInfo>();
		addRepository("Maven Central", "http://repo1.maven.org/maven2");
		addRepository("Maven Central Snapshots", "http://oss.sonatype.org/content/repositories/snapshots/");
		
		PropertiesPanel pn = new PropertiesPanel();
		tfgi = pn.createTextField("Group Id:", null, true);
		tfai = pn.createTextField("Artifact Id:", null, true);
		tfv = pn.createTextField("Version: ", null, true);
		
		final RepoComboModel cbrm = new RepoComboModel();
		cbrepos = new JComboBox(cbrm);
		cbrepos.setEditable(false);
		cbrepos.setSelectedIndex(0);
		
		JButton badd = new JButton("Add...");
		badd.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				PropertiesPanel pp = new PropertiesPanel();
				final JTextField tfname = pp.createTextField("Name: ", null, true);
				final JTextField tfurl = pp.createTextField("Location: ", "http://", true);
				
				int res	= JOptionPane.showOptionDialog(RepositorySearchPanel.this, pp, "Repository Location", JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE, null, new Object[]{"OK", "Cancel"}, "OK");
				if(JOptionPane.YES_OPTION==res)
				{
					RepositoryInfo repo = addRepository(tfname.getText(), tfurl.getText());
					cbrm.changed();
					cbrm.setSelectedItem(repo);
				}
			}
		});
		
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
		tm.setRoot(new IdTreeNode<ArtifactInfo>("root", null, tm, false, null, null, null));
		tree = new JTree(tm);
		tree.setRootVisible(false);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.setCellRenderer(new IdTreeCellRenderer());
		tree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener()
		{
			public void valueChanged(TreeSelectionEvent e)
			{
				if(e.getNewLeadSelectionPath()!=null)
				{
					ArtifactInfo ai = null;
					ArtifactInfo ret = null;
					TreePath sel = tree.getSelectionPath();
					if(sel!=null)
					{
						IdTreeNode<ArtifactInfo> node = (IdTreeNode<ArtifactInfo>)sel.getLastPathComponent();
//						System.out.println("selected: "+node.getArtifactInfo());
						ai = node.getObject();
					}
					if(ai!=null)
					{
						tfgi.setText(ai.groupId);
						tfai.setText(ai.artifactId);
						tfv.setText(ai.version);
					}
					else
					{
						tfgi.setText(null);
						tfai.setText(null);
						tfv.setText(null);
					}
				}
			}
		});

		status = new JLabel("idle");
		
		setLayout(new GridBagLayout());
		
		int y=0;
		
		add(pn, new GridBagConstraints(0,y++,
			2,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(2,2,2,2),0,0));
		
		add(new JLabel("Select or enter repository url:"), new GridBagConstraints(0,y++,
			2,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.VERTICAL, new Insets(2,2,2,2),0,0));
			
		add(cbrepos, new GridBagConstraints(0,y,1,1,1,0,
			GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(2,2,2,2),0,0));
		add(badd, new GridBagConstraints(1,y++,1,1,0,0,
			GridBagConstraints.NORTH, GridBagConstraints.VERTICAL, new Insets(2,2,2,2),0,0));
		
		add(new JLabel("Enter group id, artifact id or pattern:"), new GridBagConstraints(0,y++,
			2,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.VERTICAL, new Insets(2,2,2,2),0,0));
		
		add(tfsearch, new GridBagConstraints(0,y++,2,1,1,0,
			GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(2,2,2,2),0,0));
		
		add(new JLabel("Search Results:"), new GridBagConstraints(0,y++,
			2,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.VERTICAL, new Insets(2,2,2,2),0,0));
		
		add(new JScrollPane(tree), new GridBagConstraints(0,y++,
			2,1,1,1,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(2,2,2,2),0,0));
		
		add(status, new GridBagConstraints(0,y++,
			2,1,1,0,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(2,2,2,2),0,0));
		
		performSearch(null);
		
//		tp.execute(new Runnable()
//		{
//			public void run()
//			{
//				while(true)
//				{
//					try
//					{
//						Thread.sleep(1000);
//						getSelectedArtifactInfo();
//					}
//					catch(Exception e)
//					{
//						e.printStackTrace();
//					}
//				}
//			}
//		});
	}
	
	//-------- methods --------
	
	/**
	 *  Add a new repository.
	 *  @param name The repo name.
	 *  @param url The repo url.
	 */
	protected RepositoryInfo addRepository(String name, String url)
	{
		RepositoryInfo repo = new RepositoryInfo(name, url);
		this.repos.put(repo.getUrl(), repo);
		return repo;
	}
	
//	/**
//	 *  Get a repository per url.
//	 *  @param url The url.
//	 *  @return The repo.
//	 */
//	protected RepositoryInfo getRepository(String url)
//	{
//		return repos.get(url);
//	}
	
	/**
	 *  Get the thread pool.
	 *  @return The thread pool.
	 */
	public IThreadPool getThreadPool()
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
//		System.out.println("perform search: "+exp);
		
		try
		{
			IdTreeNode root = (IdTreeNode)tm.getRoot();
			root.removeAllChildren();
			
			if(exp!=null && exp.length()>0)
				status.setText("searching '"+exp+"'");
							
            final int[] cnt = new int[1];
            final CounterResultListener<Void> lis = new CounterResultListener<Void>(repos.size(), new SwingDefaultResultListener<Void>()
			{
            	public void customResultAvailable(Void result)
            	{
            		status.setText("searching '"+exp+"' ("+(cnt[0])+") finished");
            	}
            	
	            public void customExceptionOccurred(Exception exception)
	            {
	            	// search aborted
	            }
			});
            
            RepositoryInfo repo = (RepositoryInfo)cbrepos.getSelectedItem();
        	getIndexingContext(plexus, repo).addResultListener(new IResultListener<IndexingContext>()
			{
				public void resultAvailable(final IndexingContext context)
				{
					if(exp==null  || exp.length()==0)
						return;
					
					getThreadPool().execute(new Runnable()
					{
						public void run()
						{
							try
							{
								NexusIndexer indexer = plexus.lookup(NexusIndexer.class);

//							    Query q = new QueryParser("title", context.getAnalyzer()).parse("(groupId:jadex OR artifactId:jad) AND packaging:jar");
					            final BooleanQuery query = new BooleanQuery();
					            BooleanQuery subquery = new BooleanQuery();
					            subquery.add(indexer.constructQuery(MAVEN.GROUP_ID, exp, SearchType.SCORED), Occur.SHOULD);
					            subquery.add(indexer.constructQuery(MAVEN.ARTIFACT_ID, exp, SearchType.SCORED), Occur.SHOULD);
					            query.add(subquery, Occur.MUST);
					            query.add(indexer.constructQuery(MAVEN.PACKAGING, "jar", SearchType.EXACT), Occur.MUST);
								
//							    FlatSearchResponse response = indexer.searchFlat(new FlatSearchRequest(query));
//							    int i=0;
//								for(ArtifactInfo ai: response.getResults())
//								{
//									addArtifactInfo(ai);
//									status.setText("searching '"+exp+"' ("+(i++)+")");
//								}
					            
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
//								e.printStackTrace();
//								lis.resultAvailable(null);
								lis.exceptionOccurred(e);
							}
						}
					});
				}
				
				public void exceptionOccurred(Exception exception)
				{
					lis.resultAvailable(null);
				}
			});
//            }
//		else
//		{
//			status.setText("idle");
//		}
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
				groupn = new IdTreeNode(groupkey, null, tm, false, icons.getIcon("folder"), null, null);
				insertNode((IdTreeNode)tm.getRoot(), groupn, true);
			}
			final String artkey = groupkey+":"+ai.artifactId;
			IdTreeNode artn = tm.getNode(artkey);
			if(artn==null)
			{
				artn = new IdTreeNode(artkey, ai.artifactId, tm, false, icons.getIcon("folder"), null, null);
//				System.out.println("add artn: "+artn);
				insertNode(groupn, artn, true);
			}
			final String vkey = artkey+":"+ai.version;
			IdTreeNode vn = tm.getNode(vkey);
			if(vn==null)
			{
				vn = new IdTreeNode(vkey, ai.version+" ["+ai.packaging+"]", tm, true, icons.getIcon("jar"), null, ai);
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
					|| (!up && tmp.toString().compareTo(child.toString())<=0))
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
	 *  Get the indexing context.
	 *  Creates it the first time called.
	 *  Checks if an index update is needed.
	 */
	public IFuture<IndexingContext> getIndexingContext(PlexusContainer plexus, final RepositoryInfo repo)
	{
		assert SwingUtilities.isEventDispatchThread();
		
		final Future<IndexingContext> ret = new Future<IndexingContext>();
		
		IndexingContext context = repo.getContext();
		
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
		        context = indexer.addIndexingContextForced(repo.getName(), 
		        	repo.getName(), new File(repo.getCachedir()), new File(repo.getIndexdir()), repo.getUrl(), null, indexers);
	    	
		        repo.setContext(context);
	    	}
	    	catch(Exception e)
	    	{
	    		e.printStackTrace();
	    		ret.setException(e);
//	    		throw new RuntimeException(e);
	    	}
		}
		
		if(context!=null)
		{
			// check up to date
			Date contime = context.getTimestamp();
			
			boolean update = contime==null && !repo.isIndexUpdating();
			if(!update && contime!=null)
			{
				Date curtime = new Date();
				long oneday = 24 * 60 * 60 * 1000;
				long days = (curtime.getTime()/oneday)-(contime.getTime()/oneday);
				update = days>3;
			}
			
    		if(update)
    		{
    			repo.setIndexUpdating(true);
    			updateIndex(plexus, context).addResultListener(new SwingExceptionDelegationResultListener<Void, IndexingContext>(ret)
				{
    				public void customResultAvailable(Void result)
    				{
    					repo.setIndexUpdating(false);
    					ret.setResult(repo.getContext());
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
	public IFuture<Void> updateIndex(final PlexusContainer plexus, final IndexingContext context)
	{
		assert SwingUtilities.isEventDispatchThread();
		
		final Future<Void> ret = new Future<Void>();
		
		status.setText("updating index: "+context.getIndexUpdateUrl()+" ");
		
		tp.execute(new Runnable()
		{
			public void run()
			{
				try
				{
//					System.out.println("Updating Index...");
					
					// Create ResourceFetcher implementation to be used with
					// IndexUpdateRequest
					// Here, we use Wagon based one as shorthand, but all we need is a
					// ResourceFetcher implementation
					Wagon wagon = plexus.lookup(Wagon.class, "http");
					TransferListener listener = new AbstractTransferListener()
					{
						public void transferStarted(TransferEvent event)
						{
							setStatus("updating index: "+context.getIndexUpdateUrl()+" downloading");
//							System.out.print(" Downloading "+event.getResource().getName());
						}
			
						public void transferProgress(TransferEvent event, byte[] buffer, int length)
						{
						}
			
						public void transferCompleted(TransferEvent event)
						{
							setStatus("updating index: "+context.getIndexUpdateUrl()+" processing");
//							System.out.println(" - Done");
						}
					};
					
					ResourceFetcher fetcher = new WagonHelper.WagonFetcher(wagon, listener, null, null);
			
					Date ts = context.getTimestamp();
					IndexUpdateRequest request = new IndexUpdateRequest(context, fetcher);
					IndexUpdater updater = plexus.lookup(IndexUpdater.class);
					IndexUpdateResult res = updater.fetchAndUpdateIndex(request);
					setStatus("updating index: "+context.getIndexUpdateUrl()+" indexing finished");
					
//					if(res.isFullUpdate())
//					{
////						setStatus("updating index: "+context.getIndexUpdateUrl()+" indexing=full upadate finished");
//						System.out.println("Full update happened!");
//					}
//					else if(res.getTimestamp().equals(ts))
//					{
////						setStatus("updating index: "+context.getIndexUpdateUrl()+" indexing=was ok");
//						System.out.println("No update needed, index is up to date!");
//					}
//					else
//					{
////						setStatus("updating index: "+context.getIndexUpdateUrl()+" indexing=incremental finished");
//						System.out.println("Incremental update happened, change covered "
//							+ ts + " - " + res.getTimestamp() + " period.");
//					}
					
					ret.setResult(null);
				}
				catch(Exception e)
		    	{
					setStatus("updating index: "+context.getIndexUpdateUrl()+" failed");
		    		e.printStackTrace();
		    		ret.setException(e);
//		    		throw new RuntimeException(e);
		    	}
			}
		});

		return ret;
	}
	
	/**
	 *  Set a status text.
	 *  @param text The text.
	 */
	protected void setStatus(final String text)
	{
		if(SwingUtilities.isEventDispatchThread())
		{
			status.setText(text);
		}
		else
		{
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					status.setText(text);
				}
			});
		}
	}
	
	/**
	 *  Get the selected artifact info.
	 */
	public ArtifactInfo getSelectedArtifactInfo()
	{
		ArtifactInfo ret = null;
		
		TreePath sel = tree.getSelectionPath();
		if(sel!=null)
		{
			IdTreeNode<ArtifactInfo> node = (IdTreeNode<ArtifactInfo>)sel.getLastPathComponent();
//			System.out.println("selected: "+node.getArtifactInfo());
			ret = node.getObject();
			
			String grid = tfgi.getText();
			String arid = tfai.getText();
			String ver = tfv.getText();
			
			if(!SUtil.equals(grid, ret.groupId) || !SUtil.equals(arid, ret.artifactId) || !SUtil.equals(ver, ret.version))
			{
				if(grid!=null && grid.length()>0 && arid!=null && arid.length()>0)
				{
					ret = new ArtifactInfo();
					ret.groupId = grid;
					ret.artifactId = arid;
					ret.version = ver;
				}
			}
			ret.remoteUrl = ((RepositoryInfo)cbrepos.getSelectedItem()).getUrl();			
		}
		
		return ret;
	}
	
	/**
     * 
     */
    protected static PlexusContainer createPlexus()
    {
    	try
    	{
	    	// Plexus:
			PlexusContainer plexus = new DefaultPlexusContainer();
//			RepositorySystem system	= plexus.lookup(RepositorySystem.class);
	
			// Load Maven settings
//			DefaultSettingsBuilderFactory	sbfac	= new DefaultSettingsBuilderFactory();
//			DefaultSettingsBuilder	sbuilder	= sbfac.newInstance();
//			DefaultSettingsBuildingRequest	brequest	= new DefaultSettingsBuildingRequest();
//			brequest.setSystemProperties(System.getProperties());
//			brequest.setUserSettingsFile(new File(new File(System.getProperty("user.home"), ".m2"), "settings.xml"));
//			if(System.getProperty("M2_HOME")!=null)
//			{
//				brequest.setGlobalSettingsFile(new File(new File(System.getProperty("M2_HOME"), "conf"), "settings.xml"));
//			}
//			SettingsBuildingResult	sbresult	= sbuilder.build(brequest);
//			Settings	settings	= sbresult.getEffectiveSettings();
//			String	local;
//			if(System.getProperty("settings.localRepository")!=null)
//			{
//				local	= System.getProperty("settings.localRepository");
//			}
//			else if(System.getProperty("maven.repo.local")!=null)
//			{		
//				// Maven 1.x backwards compatibility!?
//				local	= System.getProperty("maven.repo.local");
//			}
//			else if(settings.getLocalRepository()!=null)
//			{
//				local	= settings.getLocalRepository();
//			}
//			else
//			{
//				local	= new File(new File(System.getProperty("user.home"), ".m2"), "repository").getAbsolutePath();
//			}
			
			// Extract remote repositories
//			List<RemoteRepository>	repositories	= new ArrayList<RemoteRepository>();
			// Todo: add only repositories from active profiles!?
//			for(Profile profile: settings.getProfiles())
//			{
//				for(Repository repo: profile.getRepositories())
//				{
//					repositories.add(SMaven.convertRepository(repo));
//				}
//			}
//			Repository	repo	= new Repository();
//			repo.setId("central");
//			repo.setName("Maven Repository Switchboard");
//			repo.setLayout("default");
//			repo.setUrl("http://repo1.maven.org/maven2");
//			RepositoryPolicy	snapshots	= new RepositoryPolicy();
//			snapshots.setEnabled(false);
//			repo.setSnapshots(snapshots);
//			repositories.add(SMaven.convertRepository(repo));
			
			return plexus;
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    		throw new RuntimeException(e);
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
				// todo: use thread pool
				ArtifactInfo ai = showDialog(null, null);
				System.out.println("artifact: "+ai);
//				JFrame f = new JFrame();
//				f.add(new RepositorySearchPanel(createPlexus(), new ThreadPool()));
//				f.pack();
//				f.setLocation(SGUI.calculateMiddlePosition(f));
//				f.setVisible(true);
			}
		});
	}
	
	/**
	 *  Show a repository and artifact dialog.
	 */
	public static ArtifactInfo showDialog(IThreadPool tp, Component parent)
	{		
		assert SwingUtilities.isEventDispatchThread();

		ArtifactInfo ret = null;
		RepositorySearchPanel pan = new RepositorySearchPanel(createPlexus(), tp!=null? tp: new ThreadPool());		
		
		final JDialog dia = new JDialog((JFrame)null, "Repository and Artifact Selection", true);
		
		JButton bok = new JButton("OK");
		JButton bcancel = new JButton("Cancel");
		bok.setMinimumSize(bcancel.getMinimumSize());
		bok.setPreferredSize(bcancel.getPreferredSize());
		JPanel ps = new JPanel(new GridBagLayout());
		ps.add(bok, new GridBagConstraints(0,0,1,1,1,0,GridBagConstraints.SOUTHEAST, GridBagConstraints.VERTICAL, new Insets(2,2,2,2), 0, 0));
		ps.add(bcancel, new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.SOUTHEAST, GridBagConstraints.BOTH, new Insets(2,2,2,2), 0, 0));

		dia.getContentPane().add(pan, BorderLayout.CENTER);
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
		dia.setLocation(SGUI.calculateMiddlePosition(parent!=null? SGUI.getWindowParent(parent): null, dia));
		dia.setVisible(true);
		if(ok[0])
		{
			ret = pan.getSelectedArtifactInfo();
		}
		
//		int res	= JOptionPane.showOptionDialog(null, pan, "Repository Location", JOptionPane.YES_NO_CANCEL_OPTION,
//			JOptionPane.PLAIN_MESSAGE, null, new Object[]{"OK", "Cancel"}, "OK");
//		if(JOptionPane.YES_OPTION==res)
//		{
//			ret = pan.getSelectedArtifactInfo();
//		}
		return ret;
	}
	
	/**
	 * 
	 */
	public class RepoComboModel extends AbstractListModel implements ComboBoxModel
	{
		protected RepositoryInfo selection;

		/**
		 * 
		 */
		public Object getElementAt(int index)
		{
			Object[] ov = repos.values().toArray(new RepositoryInfo[repos.size()]);
			return repos.values().toArray(new RepositoryInfo[repos.size()])[index];
		}

		/**
		 * 
		 */
		public int getSize()
		{
			return repos.size();
		}

		/**
		 * 
		 */
		public void setSelectedItem(Object anItem)
		{
			selection = (RepositoryInfo)anItem; // to select and register an
		} // item from the pull-down list

		// Methods implemented from the interface ComboBoxModel
		/**
		 * 
		 */
		public Object getSelectedItem()
		{
			return selection; // to add the selection to the combo box
		}
		
		/**
		 * 
		 */
		public void changed()
		{
			fireContentsChanged(this, 0, repos.size()-1);
		}
		
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
		
		/** Flag if index update is running. */
		protected boolean idxup;
		
		//-------- constructors --------

		/**
		 *  Create a new repository info.
		 */
		public RepositoryInfo(String name, String url)
		{
			this(name, url, "./.repoindices/"+name, "./.repoindices/"+name);
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
		 *  Get the context.
		 *  @return The context.
		 */
		public IndexingContext getContext()
		{
			return context;
		}

		/**
		 *  Set the context.
		 *  @param context The context to set.
		 */
		public void setContext(IndexingContext context)
		{
			this.context = context;
		}

		/**
		 *  Get the indexUpdating.
		 *  @return The indexUpdating.
		 */
		public boolean isIndexUpdating()
		{
			return idxup;
		}

		/**
		 *  Set the indexUpdating.
		 *  @param indexUpdating The indexUpdating to set.
		 */
		public void setIndexUpdating(boolean indexUpdating)
		{
			this.idxup = indexUpdating;
		}
		
		/**
		 *  Get the string representation.
		 *  @return The string representation.
		 */
		public String toString()
		{
			return name+" - "+url;
		}

		/**
		 *  Get the hashcode.
		 */
		public int hashCode()
		{
			return 31 + ((url == null) ? 0 : url.hashCode());
		}

		/**
		 *  Test if equals.
		 */
		public boolean equals(Object obj)
		{
			return obj instanceof RepositoryInfo && SUtil.equals(getUrl(), ((RepositoryInfo)obj).getUrl());
		}
	}
}
