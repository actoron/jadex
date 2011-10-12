package maventest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.maven.model.Model;
import org.apache.maven.model.building.DefaultModelBuilderFactory;
import org.apache.maven.model.building.DefaultModelBuildingRequest;
import org.apache.maven.model.building.ModelBuilder;
import org.apache.maven.model.building.ModelBuildingException;
import org.apache.maven.model.building.ModelBuildingRequest;
import org.apache.maven.model.building.ModelBuildingResult;
import org.apache.maven.model.building.ModelSource;
import org.apache.maven.repository.internal.MavenRepositorySystemSession;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.collection.CollectRequest;
import org.sonatype.aether.collection.CollectResult;
import org.sonatype.aether.graph.Dependency;
import org.sonatype.aether.graph.DependencyNode;
import org.sonatype.aether.graph.Exclusion;
import org.sonatype.aether.resolution.ArtifactRequest;
import org.sonatype.aether.resolution.ArtifactResult;
import org.sonatype.aether.util.artifact.DefaultArtifact;

/**
 *  Handler for retrieving dependency information from maven artifacts.
 */
public class MavenHandler
{
	//-------- attributes --------
	
	/** Access to the aether repository system. */
	protected RepositorySystem	system;
	
	/** Maven settings including remote repositories. */
	protected MavenRepositorySettings	settings;

	/** The shared repository system session. */
	protected MavenRepositorySystemSession	session;
	
	//-------- constructors --------

	/**
	 * Constructs new maven handler.
	 */
	public MavenHandler()
	{
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
	 *  Load dependencies from a URL.
	 *  @param url	The url to a maven artifact (e.g. jar or classes directory).
	 *  @return A map containing the dependencies as mapping (parent url -> list of children urls).
	 */
	public Map<URL, List<URL>>	loadDependencies(URL url)
	{
		Map<URL, List<URL>>	urls	= new HashMap<URL, List<URL>>();
		ModelSource	pom	= findModelSource(url);
		if(pom!=null)
		{
			Model model = loadPom(pom);
			CollectRequest	request	= new CollectRequest(asDependencies(model.getDependencies()), null, settings.getRemoteRepositories());
			buildDependencyGraph(request, url, urls);
		}
		else
		{
			System.out.println("No Maven POM found for URL: "+url);
			List<URL>	empty	= Collections.emptyList();
			urls.put(url, empty);
		}
		return urls;
	}

	//-------- helper methods --------
	
	/**
	 *  Load a POM from a source.
	 *  @param pom	The source.
	 *  @return The loaded model.
	 */
	public Model loadPom(ModelSource pom)
	{
		Model model;
		
		try
		{
			ModelBuildingRequest	request = new DefaultModelBuildingRequest();
			request.setModelSource(pom);
			request.setModelResolver(new MavenModelResolver(system, session, settings.getRemoteRepositories()));
			ModelBuilder	builder = new DefaultModelBuilderFactory().newInstance();
			ModelBuildingResult	result	= builder.build(request);
			model	= result.getEffectiveModel();
			settings.setRemoteRepositories(model);
		}
		catch(ModelBuildingException e)
		{
			throw new RuntimeException(e);
		}

		return model;
	}
	
	/**
	 *  Recursively build the dependency graph.
	 *  @param dep	The start dependency.
	 *  @param url	The start url.
	 *  @param urls	The current url mappings. New dependencies are recursively added to this map.
	 */
	protected void	buildDependencyGraph(CollectRequest request, URL url, Map<URL, List<URL>> urls)
	{
		if(!urls.containsKey(url))
		{
			try
			{
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
						buildDependencyGraph(new CollectRequest(depnode.getDependency(), settings.getRemoteRepositories()), depurl, urls);
					}
					catch(Exception e)
					{
						System.out.println("Unable to resolve artifact for Dependency: "+request+", "+depnode);
//						e.printStackTrace();
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
	}
	
	/**
	 *  Find the model source (i.e. POM location) for a URL.
	 *  @param url	The url to a maven artifact (e.g. jar or classes directory).
	 */
	protected static ModelSource	findModelSource(final URL url)
	{
		InputStream pom = null;
		
		// Jar file.
		if(url.getProtocol().equals("jar"))
		{
			try
			{
				JarURLConnection con = (JarURLConnection)url.openConnection();
				JarFile jarfile = con.getJarFile();
				
				for(Enumeration<JarEntry> files =jarfile.entries(); files.hasMoreElements(); )
				{
					JarEntry entry = files.nextElement();
					String name = entry.getName();
					if(name.endsWith("pom.xml"))
					{
						pom = jarfile.getInputStream(entry);
						break;
					}
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		
		// Classes directory
		else
		{
			try
			{
				String	filename	= URLDecoder.decode(url.toString(), "UTF-8");
				File	dir	= new File(filename.substring(5));	// strip "file:"
				
				// Try to find pom.xml upwards in directory structure.
				while(pom==null && dir!=null && dir.exists())
				{
					File pomfile = new File(dir, "pom.xml");
					if(pomfile.exists())
					{
						pom = new FileInputStream(pomfile);
					}
					else
					{
						dir	= dir.getParentFile();
					}
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		
		final InputStream	fpom	= pom;
		return pom==null ? null : new ModelSource()
		{
			public String getLocation()
			{
				return url.toString();
			}
			
			public InputStream getInputStream() throws IOException
			{
				return fpom;
			}
		};
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
			ret	= file.toURI().toURL();
			if(file.getName().endsWith(".jar"))
			{
				ret	= new URL("jar:"+ret.toString()+"!/");
			}
		}
		catch(MalformedURLException e)
		{
			// Shouldn't happen for existing files!?
			throw new RuntimeException(e);
		}
		return ret;
	}
	
	//-------- Maven/Aether conversion --------
	
	// Adapted from MavenConverter
	protected static List<Dependency> asDependencies(List<org.apache.maven.model.Dependency> deps)
	{
		List<Dependency>	ret	= new ArrayList<Dependency>();
		for(int i=0; i<deps.size(); i++)
		{
			ret.add(asDependency(deps.get(i)));
		}
		return ret;
	}

	// Adapted from MavenConverter
	protected static Dependency asDependency(org.apache.maven.model.Dependency dep)
	{
		return new Dependency(new DefaultArtifact(dep.getGroupId(),
				dep.getArtifactId(), dep.getClassifier(), dep.getType(),
				dep.getVersion()), dep.getScope(), dep.isOptional(),
				asExclusions(dep.getExclusions()));
	}

	// Adapted from MavenConverter
	protected static List<Exclusion> asExclusions(
			Collection<org.apache.maven.model.Exclusion> exclusions)
	{
		List<Exclusion> list = new ArrayList<Exclusion>(exclusions.size());
		for(org.apache.maven.model.Exclusion ex : exclusions)
		{
			list.add(new Exclusion(ex.getGroupId(), ex.getArtifactId(), null,
					null));
		}
		return list;
	}
}
