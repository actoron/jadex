package jadex.platform.service.dependency.maven;

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.model.Repository;
import org.apache.maven.model.building.FileModelSource;
import org.apache.maven.model.building.ModelSource;
import org.apache.maven.model.resolution.InvalidRepositoryException;
import org.apache.maven.model.resolution.ModelResolver;
import org.apache.maven.model.resolution.UnresolvableModelException;
import org.apache.maven.repository.internal.MavenRepositorySystemSession;
import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.resolution.ArtifactRequest;
import org.sonatype.aether.resolution.ArtifactResolutionException;
import org.sonatype.aether.resolution.ArtifactResult;
import org.sonatype.aether.util.artifact.DefaultArtifact;

import maventest.MavenModelResolver;


/**
 * Model resolver implementation using aether.
 */
public class AetherModelResolver implements ModelResolver
{
	//-------- attributes --------
	
	/** The aether repository system. */
	protected RepositorySystem	system;
	
	/** The repository system session. */
	protected MavenRepositorySystemSession	session;
	
	/** The remote repositories. */
	protected List<RemoteRepository>	repositories;
	
	//-------- constructors --------
	
	/**
	 *  Create a new aether model resolver.
	 */
	public AetherModelResolver(RepositorySystem system, MavenRepositorySystemSession session, List<RemoteRepository> repositories)
	{
		this.system	= system;
		this.session	= session;
		this.repositories	= new ArrayList<RemoteRepository>(repositories);
	}
	
	//-------- ModelResolver interface --------
	
	/**
	 *  Tries to resolve the POM for the specified coordinates.
	 * 
	 *  @param groupid The group identifier of the POM, must not be {@code null}.
	 *  @param id The artifact identifier of the POM, must not be {@code null}.
	 *  @param version The version of the POM, must not be {@code null}.
	 *  @return The source of the requested POM, never {@code null}.
	 *  @throws UnresolvableModelException If the POM could not be resolved from any configured repository.
	 */
	public ModelSource	resolveModel(String groupid, String id, String version) throws UnresolvableModelException
	{
		try
		{
			ArtifactRequest	request	= new ArtifactRequest(new DefaultArtifact(groupid, id, null, version), repositories, null);
			ArtifactResult	result	= system.resolveArtifact(session, request);
			return new FileModelSource(result.getArtifact().getFile());
		}
		catch(ArtifactResolutionException e)
		{
			throw new UnresolvableModelException("Could not resolve model.", groupid, id, version, e);
		}
	}

	/**
	 *  Adds a repository to use for subsequent resolution requests. The order in
	 *  which repositories are added matters, repositories that were added first
	 *  should also be searched first. When multiple repositories with the same
	 *  identifier are added, only the first repository being added will be used.
	 * 
	 *  @param repository The repository to add to the internal search chain, must not be {@code null}.
	 *  @throws InvalidRepositoryException If the repository could not be added (e.g. due to invalid URL or layout).
	 */
	public void addRepository(Repository repository) throws InvalidRepositoryException
	{
		repositories.add(SMaven.convertRepository(repository));
	}

	/**
	 *  Clones this resolver for usage in a forked resolution process. In
	 *  general, implementors need not provide a deep clone. The only requirement
	 *  is that invocations of {@link #addRepository(Repository)} on the clone do
	 *  not affect the state of the original resolver and vice versa.
	 * 
	 *  @return The cloned resolver, never {@code null}.
	 */
	public ModelResolver newCopy()
	{
		return new MavenModelResolver(system, session, repositories);
	}
}
