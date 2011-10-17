package maventest;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.ResourceIdentifier;
import jadex.commons.Tuple2;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

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
import org.codehaus.plexus.DefaultPlexusContainer;
import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.collection.CollectRequest;
import org.sonatype.aether.collection.CollectResult;
import org.sonatype.aether.graph.Dependency;
import org.sonatype.aether.graph.DependencyNode;
import org.sonatype.aether.repository.WorkspaceReader;
import org.sonatype.aether.repository.WorkspaceRepository;
import org.sonatype.aether.resolution.ArtifactRequest;
import org.sonatype.aether.resolution.ArtifactResult;
import org.sonatype.aether.util.artifact.DefaultArtifact;

/**
 *  Handler for retrieving dependency information from maven artifacts.
 */
public class MavenHandler
{
	//-------- attributes --------
	
	/** The component identifier to use for creating local resource IDs.
	 *  The assumption is that URLs are only valid on the local platform. */
	protected IComponentIdentifier	cid;
	
	/** Access to the aether repository system. */
	protected RepositorySystem	system;
	
	/** Maven settings including remote repositories. */
	protected MavenRepositorySettings	settings;

	/** The shared repository system session. */
	protected MavenRepositorySystemSession	session;
	
	//-------- constructors --------

	/**
	 *  Constructs new maven handler.
	 *  @param cid	The component identifier to use for creating local resource IDs.
	 */
	public MavenHandler(IComponentIdentifier cid)
	{
		this.cid	= cid;
		try
		{
			system	= new DefaultPlexusContainer().lookup(RepositorySystem.class);
		}
		catch(Exception e)
		{
			// Shouldn't happen when classpath is correct.
			throw new RuntimeException(e);
		}
		this.settings	= new MavenRepositorySettings();
		this.session	= new MavenRepositorySystemSession();
		session.setLocalRepositoryManager(system.newLocalRepositoryManager(settings.getLocalRepository()));
	}
	
	//-------- methods --------

	/**
	 *  Load dependencies from a resource identifier.
	 *  @param rid	A local or global resource identifier. If both local and global ids are present,
	 *    local takes precedence, e.g. resolving to workspace urls before fetching an older snapshot from a repository.
	 *  @return A map containing the dependencies as mapping (parent RID -> list of children RIDs).
	 */
	public Map<ResourceIdentifier, List<ResourceIdentifier>>	loadDependencies(ResourceIdentifier rid)
	{
		Map<ResourceIdentifier, List<ResourceIdentifier>>	rids	= new HashMap<ResourceIdentifier, List<ResourceIdentifier>>();
		loadDependencies(rid, rids);
		return rids;
	}
	
	/**
	 *  Get the artifact description for an url.
	 *  @param url The url.
	 *  @return The artifact description including all details.
	 */
	public String getArtifactDescription(URL url)
	{
		// todo:
		return null;
	}
	
	/**
	 * 
	 */
	protected IFuture<IResourceIdentifier> getResourceIdentifier(URL url)
	{
		// todo: get stored rid for url?!
		Tuple2<IComponentIdentifier, URL> lid = new Tuple2<IComponentIdentifier, URL>(cid, url);
		String gid = getArtifactDescription(url);
		ResourceIdentifier rid = new ResourceIdentifier(lid, gid);
		return new Future<IResourceIdentifier>(rid);
	}

	//-------- helper methods --------
	
	/**
	 *  Load dependencies from a resource identifier.
	 *  @param rid	A local or global resource identifier. If both local and global ids are present,
	 *    local takes precedence, e.g. resolving to workspace urls before fetching an older snapshot from a repository.
	 *  @param rids	A map for inserting the dependencies as mapping (parent RID -> list of children RIDs).
	 */
	protected void	loadDependencies(final ResourceIdentifier rid, Map<ResourceIdentifier, List<ResourceIdentifier>> rids)
	{
		if(!rids.containsKey(rid))
		{
			// Resolve global RID, if necessary
			if(rid.getLocalIdentifier()==null)
			{
				
			}
			
			ModelSource	pom	= findModelSource(url);
			
			if(pom!=null)
			{
				final Model model = loadPom(pom);
				List<org.apache.maven.model.Dependency>	deps	= model.getDependencies();
				List<URL>	depurls	= new ArrayList<URL>();
				for(int i=0; i<deps.size(); i++)
				{
					try
					{
						session.setWorkspaceReader(new WorkspaceReader()
						{
							public WorkspaceRepository getRepository()
							{
								return null;
							}
							
							public List<String> findVersions(Artifact artifact)
							{
								return Collections.emptyList();
							}
							
							public File findArtifact(Artifact artifact)
							{
								File	ret	= null;
								if(model.getProjectDirectory()!=null)	// Only for local 'classes' directories.
								{
									// Find parent pom.
									Model	parent	= null;
									if(model.getParent()!=null)
									{
										// Parent specified in pom.
										if(model.getParent().getRelativePath()!=null)
										{
											File	pfile	= new File(model.getProjectDirectory(), model.getParent().getRelativePath());
											if(pfile.exists())
											{
												parent	= loadPom(new FileModelSource(pfile));
											}
										}
										else
										{
											// Todo: load parent from repository!?
										}
									}
									else
									{
										// Parent not specified: search in upper directories.
										File	pdir	= findBasedir(model.getProjectDirectory().getParentFile());
										if(pdir!=null)
										{
											parent	= loadPom(new FileModelSource(new File(pdir, "pom.xml")));
										}
									}
									
									// Find dependency in available modules.
									if(parent!=null && parent.getModules()!=null)
									{
										String	path	= null;
										for(Iterator<String> it=parent.getModules().iterator(); path==null && it.hasNext(); )
										{
											String	module	= it.next();
											if(module.endsWith(artifact.getArtifactId()))
											{
												path	= module;
											}
										}
										
										if(path!=null)
										{
											File	mpom	= new File(new File(parent.getProjectDirectory(), path), "pom.xml");
											if(mpom.exists())
											{
												Model	mmodel	= loadPom(new FileModelSource(mpom));
												String	output	= mmodel.getBuild().getOutputDirectory();
												ret	= new File(output);
											}
										}
										// Not found: returns null and causes search in local or global repository.										else
//										{
//											System.out.println("No path for module "+artifact.getArtifactId()+"in parent "+parent);
//										}
									}
								}
								return ret;
							}
						});
						ArtifactRequest	ar	= new ArtifactRequest(asArtifact(deps.get(i)), settings.getRemoteRepositories(), null);
						ArtifactResult res = system.resolveArtifact(session, ar);
						File	file	= res.getArtifact().getFile();
						URL depurl = getUrl(file);
						depurls.add(depurl);
						loadDependencies(depurl, res.getArtifact(), urls);
					}
					catch(Exception e)
					{
						System.out.println("Unable to resolve artifact for Dependency: "+model+", "+deps.get(i));
//						e.printStackTrace();
					}
				}
				urls.put(url, depurls);
			}
			// POM not contained in artifact, search dependencies from repository.
			else if(pom==null && art!=null)
			{
				try
				{
					CollectRequest	request	= new CollectRequest(new Dependency(art, null), settings.getRemoteRepositories());
					CollectResult result = system.collectDependencies(session, request);
					List<DependencyNode> children = result.getRoot().getChildren();
					List<URL>	depurls	= new ArrayList<URL>();
					for(int i=0; i<children.size(); i++)
					{
						DependencyNode	depnode	= children.get(i);
						try
						{
							ArtifactResult res = system.resolveArtifact(session, new ArtifactRequest(depnode));
							File	file	= res.getArtifact().getFile();
							URL depurl = getUrl(file);
							depurls.add(depurl);
							loadDependencies(depurl, res.getArtifact(), urls);
						}
						catch(Exception e)
						{
							System.out.println("Unable to resolve artifact for Dependency: "+request+", "+depnode);
//							e.printStackTrace();
						}
					}
					urls.put(url, depurls);
				}
				catch(Exception e)
				{
					System.out.println("Unable to resolve dependencies for URL: "+url);
					e.printStackTrace();
					List<URL>	empty	= Collections.emptyList();
					urls.put(url, empty);
				}
			}
			else
			{
				System.out.println("No Maven POM found for URL: "+url);
				List<URL>	empty	= Collections.emptyList();
				urls.put(url, empty);
			}
		}
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
			request.setModelResolver(new MavenModelResolver(system, session, settings.getRemoteRepositories()));
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
	protected static ModelSource	findModelSource(final URL url)
	{
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
			File	dir = findBasedir(getFile(url));
			if(dir!=null)
			{
				try
				{
					pom	= new FileModelSource(new File(dir, "pom.xml"));
				}
				catch(Exception e)
				{
					// Shouldn't happen for exiting files?
					throw new RuntimeException(e);
				}
			}
		}
		
		return pom;
	}

	/**
	 *  Find the base directory
	 * @param url
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
	protected static URL getUrl(File file)
	{
		URL	ret;
		try
		{
			ret	= file.getCanonicalFile().toURI().toURL();
			if(file.getName().endsWith(".jar"))
			{
				ret	= new URL("jar:"+ret.toString()+"!/");
			}
		}
		catch(Exception e)
		{
			// Shouldn't happen for existing files!?
			throw new RuntimeException(e);
		}
		return ret;
	}
	
	/**
	 *  Get the file from an URL.
	 *	@param url	The jar or file URL.
	 *  @return	The file.
	 */
	protected static File	getFile(URL url)
	{
		assert url.getProtocol().equals("file");
		
		File	file;
		try
		{
			String	filename	= URLDecoder.decode(url.toString(), "UTF-8");
			file = new File(filename.substring(5));	// strip "file:"
		}
		catch(Exception e)
		{
			// Shouldn't happen for existing files!?
			throw new RuntimeException(e);			
		}
		return file;
	}
	
	//-------- Maven/Aether conversion --------
	
//	// Adapted from MavenConverter
//	protected static List<Dependency> asDependencies(List<org.apache.maven.model.Dependency> deps)
//	{
//		List<Dependency>	ret	= new ArrayList<Dependency>();
//		for(int i=0; i<deps.size(); i++)
//		{
//			ret.add(asDependency(deps.get(i)));
//		}
//		return ret;
//	}
//
//	// Adapted from MavenConverter
//	protected static Dependency asDependency(org.apache.maven.model.Dependency dep)
//	{
//		return new Dependency(asArtifact(dep), dep.getScope(), dep.isOptional(),
//				asExclusions(dep.getExclusions()));
//	}

	// Adapted from MavenConverter
	protected static Artifact asArtifact(org.apache.maven.model.Dependency dep)
	{
		return new DefaultArtifact(dep.getGroupId(),
				dep.getArtifactId(), dep.getClassifier(), dep.getType(),
				dep.getVersion());
	}

//	// Adapted from MavenConverter
//	protected static List<Exclusion> asExclusions(
//			Collection<org.apache.maven.model.Exclusion> exclusions)
//	{
//		List<Exclusion> list = new ArrayList<Exclusion>(exclusions.size());
//		for(org.apache.maven.model.Exclusion ex : exclusions)
//		{
//			list.add(new Exclusion(ex.getGroupId(), ex.getArtifactId(), null,
//					null));
//		}
//		return list;
//	}
}
