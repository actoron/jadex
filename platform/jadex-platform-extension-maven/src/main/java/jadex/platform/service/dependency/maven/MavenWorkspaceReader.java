package jadex.platform.service.dependency.maven;

import java.io.File;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.maven.model.Model;
import org.apache.maven.model.building.FileModelSource;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.repository.WorkspaceReader;
import org.sonatype.aether.repository.WorkspaceRepository;


/**
 * Search local file system for parent and sibling module POMs.
 */
public class MavenWorkspaceReader implements WorkspaceReader
{
	// -------- attributes --------

	/** The start POM. */
	protected Model	model;
	
	/** The maven service. */
	protected MavenDependencyResolverService	service;

	// -------- constructors --------

	/**
	 * Create a maven workspace reader.
	 */
	public MavenWorkspaceReader(Model model, MavenDependencyResolverService service)
	{
		this.model = model;
		this.service	= service;
	}

	// -------- WorkspaceReader interface --------


	/**
	 *  Gets a description of the workspace repository.
	 *  @return The repository description, never {@code null}.
	 */
	public WorkspaceRepository getRepository()
	{
		// Todo: when needed!?
		return null;
	}

	/**
	 *  Locates the specified artifact.
	 *  @param artifact	The artifact to locate, must not be {@code null}.
	 *  @return The path to the artifact or {@code null} if the artifact is not available.
	 */
	public File findArtifact(Artifact artifact)
	{
		File ret = null;
		if(model.getProjectDirectory() != null) // Only for local 'classes' directories.
		{
			// Find parent pom.
			Model parent = null;
			if(model.getParent()!=null)
			{
				// Parent specified in pom.
				if(model.getParent().getRelativePath() != null)
				{
					File pfile = new File(model.getProjectDirectory(), model.getParent().getRelativePath());
					if(pfile.exists())
					{
						parent = service.loadPom(new FileModelSource(pfile));
						ret = findModuleInParent(artifact, parent);
					}
				}
				// Todo: load parent from repository!?
			}

			if(ret==null)
			{
				// Parent not specified: search in upper directories.
				File pdir = MavenDependencyResolverService.findBasedir(model.getProjectDirectory().getParentFile());
				if(pdir!=null)
				{
					parent = service.loadPom(new FileModelSource(new File(pdir,	"pom.xml")));
					ret = findModuleInParent(artifact, parent);
				}
			}

			// Not found: returns null and causes search in local or global
			// repository.
		}
		return ret;
	}

	/**
	 *  Find the artifact as a module in a parent.
	 */
	protected File findModuleInParent(Artifact artifact, Model parent)
	{
		File ret	= null;
		
		// Find dependency in available modules.
		if(parent!=null && parent.getModules() != null)
		{
			String path = null;
			for(Iterator<String> it = parent.getModules().iterator(); path==null && it.hasNext();)
			{
				String module = it.next();
				if(module.endsWith(artifact.getArtifactId()))
				{
					path = module;
				}
			}

			if(path!=null)
			{
				File mpom = new File(new File(parent.getProjectDirectory(), path), "pom.xml");
				if(mpom.exists())
				{
					Model mmodel = service.loadPom(new FileModelSource(mpom));
					String output = mmodel.getBuild().getOutputDirectory();
					ret = new File(output);
				}
			}
		}
		return ret;
	}

	/**
	 *  Determines all available versions of the specified artifact.
	 *  @param artifact	The artifact whose versions should be listed, must not be {@code null}.
	 *  @return The available versions of the artifact, must not be {@code null}.
	 */
	public List<String> findVersions(Artifact artifact)
	{
		// Todo: when needed!?
		return Collections.emptyList();
	}
}