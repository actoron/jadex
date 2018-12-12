package jadex.platform.service.dependency.maven;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Logger;

import org.apache.maven.model.Model;
import org.apache.maven.model.building.DefaultModelBuilderFactory;
import org.apache.maven.model.building.DefaultModelBuildingRequest;
import org.apache.maven.model.building.FileModelSource;
import org.apache.maven.model.building.ModelBuilder;
import org.apache.maven.model.building.ModelBuildingException;
import org.apache.maven.model.building.ModelBuildingRequest;
import org.apache.maven.model.building.ModelBuildingResult;
import org.apache.maven.model.building.ModelSource;
import org.apache.maven.repository.internal.MavenRepositorySystemSession;
import org.apache.maven.settings.Profile;
import org.apache.maven.settings.Repository;
import org.apache.maven.settings.RepositoryPolicy;
import org.apache.maven.settings.Settings;
import org.apache.maven.settings.building.DefaultSettingsBuilder;
import org.apache.maven.settings.building.DefaultSettingsBuilderFactory;
import org.apache.maven.settings.building.DefaultSettingsBuildingRequest;
import org.apache.maven.settings.building.SettingsBuildingResult;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.collection.CollectRequest;
import org.sonatype.aether.graph.Dependency;
import org.sonatype.aether.graph.DependencyNode;
import org.sonatype.aether.repository.LocalRepository;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.resolution.ArtifactRequest;
import org.sonatype.aether.resolution.ArtifactResult;
import org.sonatype.aether.resolution.DependencyRequest;
import org.sonatype.aether.resolution.DependencyResult;
import org.sonatype.aether.util.artifact.DefaultArtifact;

import jadex.bridge.GlobalResourceIdentifier;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IGlobalResourceIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.ILocalResourceIdentifier;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.LocalResourceIdentifier;
import jadex.bridge.ResourceIdentifier;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.bridge.service.annotation.ServiceStart;
import jadex.bridge.service.types.library.IDependencyService;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 *  Handler for retrieving dependency information from maven artifacts.
 */
@Service
public class MavenDependencyResolverService	implements IDependencyService
{
	//-------- attributes --------
	
	/** The service providing component. */
	@ServiceComponent
	protected IInternalAccess	component;
	
	/** The logger. */
	protected Logger	logger;
	
	/** The component identifier to use for creating local resource IDs.
	 *  The assumption is that URLs are only valid on the local platform. */
	protected IComponentIdentifier	cid;
	
	/** Access to the aether repository system. */
	protected RepositorySystem	system;
	
	/** The shared repository system session. */
	protected MavenRepositorySystemSession	session;
	
	/** Remote repositories as specified in settings. */
	protected List<RemoteRepository>	repositories;
	
	//-------- constructors --------
	
	/**
	 *  Bean constructor for service creation.
	 */
	public MavenDependencyResolverService()
	{		
	}

	/**
	 *  Constructor for testing
	 */
	public MavenDependencyResolverService(IComponentIdentifier cid) throws Exception
	{
		this.cid	= cid;
		this.logger	= Logger.getLogger(cid.getName());
		init();
	}

	/**
	 *  Start the service.
	 */
	@ServiceStart
	public IFuture<Void> startService()
	{
		IFuture<Void>	ret	= IFuture.DONE;
		this.cid	= component.getId().getRoot();
		this.logger	= component.getLogger();
		try
		{
			// Separate method for testing from main().
			init();
		}
		catch(Exception e)
		{
			ret	= new Future<Void>(e);
		}
		return ret;
	}
	
	/**
	 *  Initialize maven and aether repository system.
	 */
	protected void	init()	throws Exception
	{
		// Plexus:
		system	= new DefaultPlexusContainer().lookup(RepositorySystem.class);

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
		this.repositories	= new ArrayList<RemoteRepository>();
		// Todo: add only repositories from active profiles!?
		for(Profile profile: settings.getProfiles())
		{
			for(Repository repo: profile.getRepositories())
			{
				repositories.add(SMaven.convertRepository(repo));
			}
		}
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
		repositories.add(SMaven.convertRepository(repo));
		
		Repository	repo2	= new Repository();
		repo2.setId("Snapshots");
		repo2.setName("Maven Repository Snapshots");
		repo2.setLayout("default");
		repo2.setUrl("http://oss.sonatype.org/content/repositories/snapshots/");
		RepositoryPolicy	snapshots2	= new RepositoryPolicy();
		snapshots2.setEnabled(true);
		repo2.setSnapshots(snapshots2);
		repositories.add(SMaven.convertRepository(repo2));
		
		// Setup session.
        this.session	= new MavenRepositorySystemSession();
        LocalRepository	localRepo	= new LocalRepository(local);
        session.setLocalRepositoryManager(system.newLocalRepositoryManager(localRepo));
	}
	
	//-------- methods --------

	/**
	 *  Load dependencies from a resource identifier.
	 *  @param rid	A local or global resource identifier. If both local and global ids are present,
	 *    local takes precedence, e.g. resolving to workspace urls before fetching an older snapshot from a repository.
	 *  @return A map containing the dependencies as mapping (parent RID -> list of children RIDs).
	 */
	public IFuture<Tuple2<IResourceIdentifier, Map<IResourceIdentifier, List<IResourceIdentifier>>>>	
		loadDependencies(IResourceIdentifier rid, boolean workspace)
	{
		logger.info("Loading dependencies for: "+rid);
		
		IFuture<Tuple2<IResourceIdentifier, Map<IResourceIdentifier, List<IResourceIdentifier>>>> ret;
		try
		{
			Map<IResourceIdentifier, List<IResourceIdentifier>>	rids	= new HashMap<IResourceIdentifier, List<IResourceIdentifier>>();
			rid = loadDependencies(rid, rids, workspace);
			ret = new Future<Tuple2<IResourceIdentifier, Map<IResourceIdentifier, List<IResourceIdentifier>>>>(
				new Tuple2<IResourceIdentifier, Map<IResourceIdentifier, List<IResourceIdentifier>>>(rid, rids));
		}
		catch(Exception e)
		{
			Map<IResourceIdentifier, List<IResourceIdentifier>> res = new HashMap<IResourceIdentifier, List<IResourceIdentifier>>();
			res.put(rid, new ArrayList<IResourceIdentifier>());
			ret = new Future<Tuple2<IResourceIdentifier, Map<IResourceIdentifier, List<IResourceIdentifier>>>>(
				new Tuple2<IResourceIdentifier, Map<IResourceIdentifier, List<IResourceIdentifier>>>(rid, res));
//			ret	= new Future<Tuple2<IResourceIdentifier, Map<IResourceIdentifier,List<IResourceIdentifier>>>>(e);			
		}

//		System.out.println("loaded dep: "+rid+" "+workspace);
		return ret;
	}
	
	/**
	 *  Get the resource identifier for a url.
	 *  @param url The url.
	 *  @return The resource identifier including local RID and potentially global RID if available.
	 */
	public IFuture<IResourceIdentifier> getResourceIdentifier(URL url)
	{
		// todo: get stored rid for url?!
		ILocalResourceIdentifier lid = new LocalResourceIdentifier(cid, url);
		IGlobalResourceIdentifier gid	= null;
		Model model	= loadModelSource(url);
		if(model!=null)
		{
			String id = getCoordinates(model.getGroupId(), model.getArtifactId(), model.getVersion());
			gid = new GlobalResourceIdentifier(id, null, null); // todo: repo url
		}
		ResourceIdentifier rid = new ResourceIdentifier(lid, gid);
		logger.info("Resource identifier for "+url+" is: "+rid);
		return new Future<IResourceIdentifier>(rid);
	}

	//-------- helper methods --------
	
	/**
	 *  Load dependencies from a resource identifier.
	 *  @param rid	A local or global resource identifier. If both local and global ids are present,
	 *    local takes precedence, e.g. resolving to workspace urls before fetching an older snapshot from a repository.
	 *  @param rids	A map for inserting the dependencies as mapping (parent RID -> list of children RIDs).
	 */
	protected IResourceIdentifier	loadDependencies(IResourceIdentifier rid, 
		Map<IResourceIdentifier, List<IResourceIdentifier>> rids, boolean workspace) throws Exception
	{
		logger.info("Loading dependencies: "+rid);
		
		// Resolve from local URL.
		if(!rids.containsKey(rid) && rid.getLocalIdentifier()!=null && cid.equals(rid.getLocalIdentifier().getComponentIdentifier()))
		{
			Model model = loadModelSource(SUtil.toURL(rid.getLocalIdentifier().getUri()));
			
			if(model!=null)
			{
				List<org.apache.maven.model.Dependency>	deps	= model.getDependencies();
				List<IResourceIdentifier>	deprids	= new ArrayList<IResourceIdentifier>();
				String id = getCoordinates(model.getGroupId(), model.getArtifactId(), model.getVersion());
				rid = new ResourceIdentifier(rid.getLocalIdentifier(), new GlobalResourceIdentifier(id, null, null));
				rids.put(rid, deprids);
				
				for(int i=0; i<deps.size(); i++)
				{
					boolean fin = false;

					if(workspace)
					{
						try
						{
							session.setWorkspaceReader(new MavenWorkspaceReader(model, this));
							ArtifactRequest	ar	= new ArtifactRequest(SMaven.convertDependency(deps.get(i)), repositories, null);
							ArtifactResult res = system.resolveArtifact(session, ar);
//							VersionRequest vr = new VersionRequest(res.getArtifact(), repositories, null);
//							VersionResult vres = system.resolveVersion(session, vr);
							Artifact	art	= res.getArtifact();
							id = getCoordinates(art.getGroupId(), art.getArtifactId(), art.getVersion());
							IGlobalResourceIdentifier gid = new GlobalResourceIdentifier(id, null, art.getVersion()); // repo url?
							ResourceIdentifier	deprid	= new ResourceIdentifier(new LocalResourceIdentifier(cid, getUrl(art.getFile())), gid);
							deprids.add(deprid);
							loadDependencies(deprid, rids, workspace);
							fin = true;
						}
						catch(Exception e)
						{
							logger.warning("Unable to resolve artifact for Dependency: "+model+", "+deps.get(i));
						}
						finally
						{
							session.setWorkspaceReader(null);
						}
					}
					
					if(!fin)
					{
						Artifact	art	= SMaven.convertDependency(deps.get(i));
						id = getCoordinates(art.getGroupId(), art.getArtifactId(), art.getVersion());
						IGlobalResourceIdentifier gid = new GlobalResourceIdentifier(id, null, null); // todo: repo url
						IResourceIdentifier	deprid	= new ResourceIdentifier(null, gid);	
						deprid	= loadDependenciesWithAether(deprid, rids);
						deprids.add(deprid);
					}
				}
			}
		}
		
		// Resolve artifact from global RID, if available.
		if(!rids.containsKey(rid) && rid.getGlobalIdentifier()!=null)
		{
			rid = loadDependenciesWithAether(rid, rids);
		}
		
		// If no dependency information avaliable, continue with empty dependency list but print warning.
		if(!rids.containsKey(rid))
		{
			logger.warning("Cannot resolve dependency for resource: "+rid);
			List<IResourceIdentifier>	empty	= Collections.emptyList();
			rids.put(rid, empty);
		}
		
		return rid;
	}
	
	/**
	 *  Load dependencies using aether repository access only,
	 *  because no other context information (e.g. workspace) is available.
	 *  @param rid	The resource identifier containing only a global resource id.
	 *  @param rids	A map for inserting the dependencies as mapping (parent RID -> list of children RIDs).
	 */
	protected IResourceIdentifier	loadDependenciesWithAether(IResourceIdentifier rid, Map<IResourceIdentifier, List<IResourceIdentifier>> rids)	throws Exception
	{
		Artifact	art	= new DefaultArtifact(rid.getGlobalIdentifier().getResourceId());
		// Todo: use remote repositories from settings.
		CollectRequest	crequest	= new CollectRequest(new Dependency(art, null), repositories);
		DependencyRequest	request	= new DependencyRequest(crequest, null);
		DependencyResult result = system.resolveDependencies(session, request);
		ArtifactResult ares = system.resolveArtifact(session, new ArtifactRequest(result.getRoot()));
		
//		try
//		{
//		List<MetadataRequest> metadataRequests = new ArrayList<MetadataRequest>(repositories.size());
//	    for(RemoteRepository repository: repositories)
//	    {
//	        MetadataRequest metadataRequest = new MetadataRequest(null, repository, null);
////	        metadataRequest.setDeleteLocalCopyIfMissing(true);
////	        metadataRequest.setFavorLocalRepository(true);
//	        metadataRequests.add(metadataRequest);
//	    }
//	    List<MetadataResult> metadataResults = system.resolveMetadata(session, metadataRequests);
//		}
//		catch(Exception e)
//		{
//			e.printStackTrace();
//		}
		
//		VersionRequest vr = new VersionRequest(ares.getArtifact(), repositories, null);
//		VersionResult vres = system.resolveVersion(session, vr);
//		ArtifactDescriptorResult res = system.readArtifactDescriptor(session, new ArtifactDescriptorRequest(ares.getArtifact(), repositories, null));
		
		IGlobalResourceIdentifier gid = new GlobalResourceIdentifier(rid.getGlobalIdentifier().getResourceId(), rid.getGlobalIdentifier().getRepositoryInfo(), ares.getArtifact().getVersion());
		File file = result.getRoot().getDependency().getArtifact().getFile();
		rid	= new ResourceIdentifier(new LocalResourceIdentifier(cid, getUrl(file)), gid);
		processAetherDependencies(rid, rids, result.getRoot());
		return rid;
	}
	
	/**
	 *  Recursively process the dependency graph produced by aether.
	 *  @param rid	The resource identifier containing only a global resource id.
	 *  @param rids	A map for inserting the dependencies as mapping (parent RID -> list of children RIDs).
	 *  @param node	The dependency node.
	 */
	protected void	processAetherDependencies(IResourceIdentifier rid, Map<IResourceIdentifier, List<IResourceIdentifier>> rids, DependencyNode node)	throws Exception
	{
		logger.info("Loading dependencies with aether: "+rid);
		List<DependencyNode> children = node.getChildren();
		List<IResourceIdentifier>	deps	= new ArrayList<IResourceIdentifier>();
		for(int i=0; i<children.size(); i++)
		{
			DependencyNode	depnode	= children.get(i);
			Artifact	art	= depnode.getDependency().getArtifact();
			String id = getCoordinates(art.getGroupId(), art.getArtifactId(), art.getVersion());
			// todo: more than one url?
			URL repourl = null;
			List<RemoteRepository> repos = depnode.getRepositories();
			if(repos!=null && repos.size()>0)
			{
				repourl = new URL(repos.get(0).getUrl());
			}
			IGlobalResourceIdentifier gid = new GlobalResourceIdentifier(id, repourl.toURI(), null); 
			ResourceIdentifier	deprid	= new ResourceIdentifier(new LocalResourceIdentifier(cid, getUrl(art.getFile())), gid);
			processAetherDependencies(deprid, rids, depnode);
			deps.add(deprid);
		}
		rids.put(rid, deps);
	}
	
	/**
	 *  Load a POM from a source.
	 *  @param pom	The source.
	 *  @return The loaded model.
	 */
	protected Model loadPom(ModelSource pom)
	{
		Model model;
		
		try
		{
			ModelBuildingRequest	request = new DefaultModelBuildingRequest();
			request.setModelSource(pom);
			if(pom instanceof FileModelSource)
			{
				// Required for being able to resolve project directory and parent.
				request.setPomFile(((FileModelSource)pom).getPomFile());
			}
			// Todo: use remote repositories from settings.
			request.setModelResolver(new AetherModelResolver(system, session, repositories));
			ModelBuilder	builder = new DefaultModelBuilderFactory().newInstance();
			ModelBuildingResult	result	= builder.build(request);
			model	= result.getEffectiveModel();
			
//			settings.setRemoteRepositories(model);
		}
		catch(ModelBuildingException e)
		{
			throw new RuntimeException(e);
		}

		return model;
	}
	
	/**
	 *  Find the model source (i.e. POM location) for a URL.
	 *  @param url	The url to a maven artifact (e.g. jar or classes directory).
	 */
	protected Model loadModelSource(final URL url)
	{
		Model ret = null;
		ModelSource	pom = null;
		
		// Jar file.
		if(url.getProtocol().equals("jar"))
		{
			try
			{
				JarURLConnection con = (JarURLConnection)url.openConnection();
				JarFile jarfile = con.getJarFile();
				
				for(Enumeration<JarEntry> files =jarfile.entries(); pom==null && files.hasMoreElements(); )
				{
					JarEntry entry = files.nextElement();
					String name = entry.getName();
					if(name.endsWith("pom.xml"))
					{
						final InputStream	stream	= jarfile.getInputStream(entry);
						pom	= new ModelSource()
						{
							public String getLocation()
							{
								return url.toString();
							}
							
							public InputStream getInputStream() throws IOException
							{
								return stream;
							}
						};
					}
					ret = loadPom(pom);
				}
			}
			catch(Exception e)
			{
				// Shouldn't happen for exiting files?
				throw new RuntimeException(e);
			}
		}
		
		// Classes directory
		else
		{
			File dir = findBasedir(SUtil.getFile(url));
			if(dir!=null)
			{
				try
				{
					pom	= new FileModelSource(new File(dir, "pom.xml"));
					ret = loadPom(pom);
					
					File dest = SUtil.getFile(url);
					File tmp = new File(ret.getBuild().getOutputDirectory());
					if(!dest.getCanonicalPath().equals(tmp.getCanonicalPath()))
					{
						tmp = new File(ret.getBuild().getTestOutputDirectory());
						if(!dest.getCanonicalPath().equals(tmp.getCanonicalPath()))
						{
//							System.out.println("Found wrong pom: "+url);
							ret = null;
						}
					}
				}
				catch(Exception e)
				{
					// Shouldn't happen for existing files?
					throw new RuntimeException(e);
				}
			}
		}

		return ret;
	}

	/**
	 *  Find the base directory
	 * @param uri
	 * @return
	 */
	protected static File findBasedir(File dir)
	{
		// Try to find pom.xml upwards in directory structure.
		File	ret	= null;
		while(ret==null && dir!=null && dir.exists())
		{
			File pomfile = new File(dir, "pom.xml");
			if(pomfile.exists())
			{
				ret	= dir;
			}
			else
			{
				dir	= dir.getParentFile();
			}
		}
		return ret;
	}

	/**
	 *  Get the URL for a file.
	 *  @param file	The file.
	 *  @return	The file
	 */
	public static URL getUrl(File file)
	{
		URL	ret;
		try
		{
			ret	= file.getCanonicalFile().toURI().toURL();
//			if(file.getName().endsWith(".jar"))
//			{
//				ret	= new URL("jar:"+ret.toString()+"!/");
//			}
		}
		catch(Exception e)
		{
			// Shouldn't happen for existing files!?
			throw new RuntimeException(e);
		}
		return ret;
	}
	
	/**
	 *  Get the coordinates (global identifier) for an artifact.
	 *  @param	groupid The group identifier.
	 *  @param	id	The artifact id.
	 *  @param	version	The artifact version.
	 *  @return The coordinates as string ('groupid:id:version').
	 */
	public static String getCoordinates(String groupid, String id, String version)
	{
		String gid;
		StringBuffer	gidbuf	= new StringBuffer();
		gidbuf.append(groupid).append(':');
		gidbuf.append(id).append(':');
		gidbuf.append(version);
		gid	= gidbuf.toString();
		return gid;
	}
}
