package reposearch;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.maven.index.ArtifactInfo;
import org.apache.maven.index.ArtifactInfoGroup;
import org.apache.maven.index.FlatSearchRequest;
import org.apache.maven.index.FlatSearchResponse;
import org.apache.maven.index.GroupedSearchRequest;
import org.apache.maven.index.GroupedSearchResponse;
import org.apache.maven.index.Grouping;
import org.apache.maven.index.IteratorSearchRequest;
import org.apache.maven.index.IteratorSearchResponse;
import org.apache.maven.index.MAVEN;
import org.apache.maven.index.NexusIndexer;
import org.apache.maven.index.SearchType;
import org.apache.maven.index.context.IndexCreator;
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
import org.apache.maven.settings.Repository;
import org.apache.maven.settings.RepositoryPolicy;
import org.apache.maven.settings.Settings;
import org.apache.maven.settings.building.DefaultSettingsBuilder;
import org.apache.maven.settings.building.DefaultSettingsBuilderFactory;
import org.apache.maven.settings.building.DefaultSettingsBuildingRequest;
import org.apache.maven.settings.building.SettingsBuildingResult;
import org.apache.maven.wagon.Wagon;
import org.apache.maven.wagon.events.TransferEvent;
import org.apache.maven.wagon.events.TransferListener;
import org.apache.maven.wagon.observers.AbstractTransferListener;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.util.StringUtils;
//import org.sonatype.aether.RepositorySystem;
//import org.sonatype.aether.repository.LocalRepository;
//import org.sonatype.aether.repository.RemoteRepository;
//import org.sonatype.aether.util.version.GenericVersionScheme;
//import org.sonatype.aether.version.InvalidVersionSpecificationException;
//import org.sonatype.aether.version.Version;
//import org.sonatype.aether.util.version.GenericVersionScheme;

public class MavenArtifactSearch
{
	/**
	 *  Initialize maven and aether repository system.
	 *  
	 *  From:
	 *  https://github.com/cstamas/maven-indexer-examples/blob/master/indexer-example-01/src/main/java/org/apache/maven/indexer/example/App.java
	 */
	public static void main(String[] args)	throws Exception
	{
		// Plexus:
		PlexusContainer plexus = new DefaultPlexusContainer();
//		RepositorySystem system	= plexus.lookup(RepositorySystem.class);

		// Non-plexus:
//		DefaultServiceLocator	locator	= new DefaultServiceLocator();
//		locator.setServices(WagonProvider.class, new ManualWagonProvider());
//		locator.addService(RepositoryConnectorFactory.class, WagonRepositoryConnectorFactory.class);
//		RepositorySystem	repo	= locator.getService( RepositorySystem.class );
		
		// Load Maven settings
		DefaultSettingsBuilderFactory	sbfac	= new DefaultSettingsBuilderFactory();
		DefaultSettingsBuilder	sbuilder	= sbfac.newInstance();
		DefaultSettingsBuildingRequest	brequest	= new DefaultSettingsBuildingRequest();
		brequest.setSystemProperties(System.getProperties());
		brequest.setUserSettingsFile(new File(new File(System.getProperty("user.home"), ".m2"), "settings.xml"));
		if(System.getProperty("M2_HOME")!=null)
		{
			brequest.setGlobalSettingsFile(new File(new File(System.getProperty("M2_HOME"), "conf"), "settings.xml"));
		}
		SettingsBuildingResult	sbresult	= sbuilder.build(brequest);
		Settings	settings	= sbresult.getEffectiveSettings();
		String	local;
		if(System.getProperty("settings.localRepository")!=null)
		{
			local	= System.getProperty("settings.localRepository");
		}
		else if(System.getProperty("maven.repo.local")!=null)
		{		
			// Maven 1.x backwards compatibility!?
			local	= System.getProperty("maven.repo.local");
		}
		else if(settings.getLocalRepository()!=null)
		{
			local	= settings.getLocalRepository();
		}
		else
		{
			local	= new File(new File(System.getProperty("user.home"), ".m2"), "repository").getAbsolutePath();
		}
		
		// Extract remote repositories
//		List<RemoteRepository>	repositories	= new ArrayList<RemoteRepository>();
//		// Todo: add only repositories from active profiles!?
//		for(Profile profile: settings.getProfiles())
//		{
//			for(Repository repo: profile.getRepositories())
//			{
//				repositories.add(SMaven.convertRepository(repo));
//			}
//		}
		// Add central repository as default (hack???).
//	    <repository>
//	      <id>central</id>
//	      <name>Maven Repository Switchboard</name>
//	      <layout>default</layout>
//	      <url>http://repo1.maven.org/maven2</url>
//	      <snapshots>
//	        <enabled>false</enabled>
//	      </snapshots>
//	    </repository>
		Repository	repo	= new Repository();
		repo.setId("central");
		repo.setName("Maven Repository Switchboard");
		repo.setLayout("default");
		repo.setUrl("http://repo1.maven.org/maven2");
		RepositoryPolicy	snapshots	= new RepositoryPolicy();
		snapshots.setEnabled(false);
		repo.setSnapshots(snapshots);
//		repositories.add(SMaven.convertRepository(repo));
		
		// Setup session.
//		MavenRepositorySystemSession session	= new MavenRepositorySystemSession();
//        LocalRepository	localRepo	= new LocalRepository(local);
//        session.setLocalRepositoryManager(system.newLocalRepositoryManager(localRepo));
        
        
        // Files where local cache is (if any) and Lucene Index should be located
        File cachedir = new File("target/central-cache");
        File indexdir = new File("target/central-index");

        // Creators we want to use (search for fields it defines)
        List<IndexCreator> indexers = new ArrayList<IndexCreator>();
        indexers.add(plexus.lookup(IndexCreator.class, MinimalArtifactInfoIndexCreator.ID));
        indexers.add(plexus.lookup(IndexCreator.class, JarFileContentsIndexCreator.ID));
        indexers.add(plexus.lookup(IndexCreator.class, MavenPluginArtifactInfoIndexCreator.ID));
        indexers.add(plexus.lookup(IndexCreator.class, MavenArchetypeArtifactInfoIndexCreator.ID));
               
        // lookup the indexer instance from plexus
        NexusIndexer indexer = plexus.lookup(NexusIndexer.class);

        // Create context for central repository index
        IndexingContext centralContext = indexer.addIndexingContextForced("central-context", 
        	"central", cachedir, indexdir, "http://repo1.maven.org/maven2", null, indexers);

        // Update the index (incremental update will happen if this is not 1st run and files are not deleted)
        // This whole block below should not be executed on every app start, but rather controlled by some configuration
        // since this block will always emit at least one HTTP GET. Central indexes are updated once a week, but
        // other index sources might have different index publishing frequency.
        // Preferred frequency is once a week.
        if(false)
        {
            System.out.println( "Updating Index..." );
            System.out.println( "This might take a while on first run, so please be patient!" );
            // Create ResourceFetcher implementation to be used with IndexUpdateRequest
            // Here, we use Wagon based one as shorthand, but all we need is a ResourceFetcher implementation
            Wagon wagon = plexus.lookup(Wagon.class, "http");
            TransferListener listener = new AbstractTransferListener()
            {
                public void transferStarted( TransferEvent transferEvent )
                {
                    System.out.print( " Downloading " + transferEvent.getResource().getName() );
                }

                public void transferProgress( TransferEvent transferEvent, byte[] buffer, int length )
                {
                }

                public void transferCompleted( TransferEvent transferEvent )
                {
                    System.out.println( " - Done" );
                }
            };
            ResourceFetcher fetcher = new WagonHelper.WagonFetcher(wagon, listener, null, null );

            Date centralContextCurrentTimestamp = centralContext.getTimestamp();
            IndexUpdateRequest updateRequest = new IndexUpdateRequest( centralContext, fetcher );
            IndexUpdater updater = plexus.lookup(IndexUpdater.class);
            IndexUpdateResult updateResult = updater.fetchAndUpdateIndex(updateRequest );
            if(updateResult.isFullUpdate() )
            {
                System.out.println( "Full update happened!" );
            }
            else if(updateResult.getTimestamp().equals( centralContextCurrentTimestamp))
            {
                System.out.println( "No update needed, index is up to date!" );
            }
            else
            {
                System.out.println( "Incremental update happened, change covered " + centralContextCurrentTimestamp
                   + " - " + updateResult.getTimestamp() + " period." );
            }

            System.out.println();
        }

        System.out.println();
        System.out.println( "Using index" );
        System.out.println( "===========" );
        System.out.println();

        // Case:
        // dump all the GAVs
        // will not do this below, is too long to do, but is good example
        
//        centralContext.lock();
//        try
//        {
//	        final IndexReader ir = centralContext.getIndexReader();
//	
//	        for(int i = 0; i < ir.maxDoc(); i++)
//	        {
//	        	if( !ir.isDeleted( i ) )
//	        	{
//	        		final Document doc = ir.document( i );
//	        		final ArtifactInfo ai = IndexUtils.constructArtifactInfo( doc, centralContext );
//	        
//	        		System.out.println(ai.groupId + ":" + ai.artifactId + ":" + ai.version + ":" + ai.classifier + " (sha1=" + ai.sha1+ ")" );
//	        	}
//	        }
//        }
//        finally
//        {
////        	centralContext.unlock();
//        }
        
        // Case:
        // Search for all GAVs with known G and A and having version greater than V

//        final GenericVersionScheme versionScheme = new GenericVersionScheme();
//        final String versionString = "1.8.0";
//        final Version version = versionScheme.parseVersion( versionString );

        try
        {
            // construct the query for known GA
        	final Query groupid = indexer.constructQuery(MAVEN.GROUP_ID, "org.sonatype.nexus", SearchType.EXACT);
            final Query artid = indexer.constructQuery(MAVEN.ARTIFACT_ID, "nexus-api", SearchType.EXACT);
            final BooleanQuery query = new BooleanQuery();
            query.add(groupid, Occur.MUST);
            query.add(artid, Occur.MUST);
            
            // we want "jar" artifacts only
            query.add(indexer.constructQuery(MAVEN.PACKAGING, "jar", SearchType.EXACT), Occur.MUST);
            // we want main artifacts only (no classifier)
            // Note: this below is unfinished API, needs fixing
//            query.add(indexer.constructQuery(MAVEN.CLASSIFIER, new SourcedSearchExpression(Field.NOT_PRESENT)) ,Occur.MUST_NOT );

            // construct the filter to express V greater than
//            final ArtifactInfoFilter versionFilter = new ArtifactInfoFilter()
//            {
//                public boolean accepts( final IndexingContext ctx, final ArtifactInfo ai)
//                {
//                    try
//                    {
//                        final Version aiv = versionScheme.parseVersion(ai.version);
//                        // Use ">=" if you are INCLUSIVE
//                        return aiv.compareTo(version)>0;
//                    }
//                    catch(InvalidVersionSpecificationException e)
//                    {
//                        // do something here? be safe and include?
//                        return true;
//                    }
//                }
//            };

            final IteratorSearchRequest request = new IteratorSearchRequest(query);//, versionFilter);

            final IteratorSearchResponse response = indexer.searchIterator(request);

            for(ArtifactInfo ai : response)
            {
                System.out.println(ai.toString());
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
	public static void searchAndDump(NexusIndexer indexer, String descr, Query q) throws IOException
    {
        System.out.println("Searching for " + descr);

        FlatSearchResponse response = indexer.searchFlat(new FlatSearchRequest(q));

        for(ArtifactInfo ai: response.getResults())
        {
            System.out.println( ai.toString() );
        }

        System.out.println( "------" );
        System.out.println( "Total: " + response.getTotalHits() );
        System.out.println();
    }

	/**
	 * 
	 */
    public static void searchGroupedAndDump(NexusIndexer indexer, String descr, Query q, Grouping g) throws IOException
    {
        System.out.println( "Searching for " + descr );

        GroupedSearchResponse response = indexer.searchGrouped( new GroupedSearchRequest(q, g));

        for(Map.Entry<String, ArtifactInfoGroup> entry : response.getResults().entrySet())
        {
            ArtifactInfo ai = entry.getValue().getArtifactInfos().iterator().next();
            System.out.println("* Plugin " + ai.artifactId);
            System.out.println(" Latest version: " + ai.version);
            System.out.println(StringUtils.isBlank( ai.description) ? "No description in plugin's POM."
            	: StringUtils.abbreviate( ai.description, 60));
            System.out.println();
        }

        System.out.println( "------" );
        System.out.println( "Total record hits: " + response.getTotalHits() );
        System.out.println();
    }
    
    /**
     * 
     */
    public static PlexusContainer createPlexus()
    {
    	try
    	{
	    	// Plexus:
			PlexusContainer plexus = new DefaultPlexusContainer();
//			RepositorySystem system	= plexus.lookup(RepositorySystem.class);
	
			// Load Maven settings
			DefaultSettingsBuilderFactory	sbfac	= new DefaultSettingsBuilderFactory();
			DefaultSettingsBuilder	sbuilder	= sbfac.newInstance();
			DefaultSettingsBuildingRequest	brequest	= new DefaultSettingsBuildingRequest();
			brequest.setSystemProperties(System.getProperties());
			brequest.setUserSettingsFile(new File(new File(System.getProperty("user.home"), ".m2"), "settings.xml"));
			if(System.getProperty("M2_HOME")!=null)
			{
				brequest.setGlobalSettingsFile(new File(new File(System.getProperty("M2_HOME"), "conf"), "settings.xml"));
			}
			SettingsBuildingResult	sbresult	= sbuilder.build(brequest);
			Settings	settings	= sbresult.getEffectiveSettings();
			String	local;
			if(System.getProperty("settings.localRepository")!=null)
			{
				local	= System.getProperty("settings.localRepository");
			}
			else if(System.getProperty("maven.repo.local")!=null)
			{		
				// Maven 1.x backwards compatibility!?
				local	= System.getProperty("maven.repo.local");
			}
			else if(settings.getLocalRepository()!=null)
			{
				local	= settings.getLocalRepository();
			}
			else
			{
				local	= new File(new File(System.getProperty("user.home"), ".m2"), "repository").getAbsolutePath();
			}
			
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
			Repository	repo	= new Repository();
			repo.setId("central");
			repo.setName("Maven Repository Switchboard");
			repo.setLayout("default");
			repo.setUrl("http://repo1.maven.org/maven2");
			RepositoryPolicy	snapshots	= new RepositoryPolicy();
			snapshots.setEnabled(false);
			repo.setSnapshots(snapshots);
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
     * 
     */
    public static IndexingContext createIndexingContext(PlexusContainer plexus, File cachedir, File indexdir, 
    	String id, String repoid, String repourl) 
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
	        IndexingContext context = indexer.addIndexingContextForced("central-context", // is context id
	        	"central", cachedir, indexdir, "http://repo1.maven.org/maven2", null, indexers);
	        
	        return context;
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    		throw new RuntimeException(e);
    	}
    }
    
    /**
     * 
     */
	public static void updateIndex(PlexusContainer plexus, IndexingContext context)
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
		}
		catch(Exception e)
    	{
    		e.printStackTrace();
    		throw new RuntimeException(e);
    	}
	}
}