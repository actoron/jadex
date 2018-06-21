package jadex.maven;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.project.MavenProject;

public abstract class AbstractJadexMojo extends AbstractMojo
{
	
	/**
	 * The maven project.
	 * 
	 * @parameter property=project
	 * @required
	 * @readonly
	 */
	protected MavenProject project;
	
	
	/**
	 * Which dependency scopes should not be included when unpacking
	 * dependencies into the apk.
	 */
	protected static final List<String> EXCLUDED_DEPENDENCY_SCOPES = Arrays.asList("provided", "system", "import");

	/**
	 * @return a {@code Set} of dependencies which may be extracted and
	 *         otherwise included in other artifacts. Never {@code null}. This
	 *         excludes artifacts of the {@code EXCLUDED_DEPENDENCY_SCOPES}
	 *         scopes.
	 */
	protected Set<Artifact> getRelevantCompileArtifacts()
	{
		final Set<Artifact> allArtifacts = project.getArtifacts();
		final Set<Artifact> results = filterOutIrrelevantArtifacts(allArtifacts);
		return results;
	}

	/**
	 * @return a {@code Set} of direct project dependencies. Never {@code null}.
	 *         This excludes artifacts of the {@code EXCLUDED_DEPENDENCY_SCOPES}
	 *         scopes.
	 */
	protected Set<Artifact> getRelevantDependencyArtifacts()
	{
		final Set<Artifact> allArtifacts = (Set<Artifact>) project.getDependencyArtifacts();
		final Set<Artifact> results = filterOutIrrelevantArtifacts(allArtifacts);
		return results;
	}

	/**
	 * @return a {@code List} of all project dependencies. Never {@code null}.
	 *         This excludes artifacts of the {@code EXCLUDED_DEPENDENCY_SCOPES}
	 *         scopes. And This should maintain dependency order to comply with
	 *         library project resource precedence.
	 */
	protected Set<Artifact> getAllRelevantDependencyArtifacts()
	{
		final Set<Artifact> allArtifacts = (Set<Artifact>) project.getArtifacts();
		final Set<Artifact> results = filterOutIrrelevantArtifacts(allArtifacts);
		return results;
	}

	/**
	 * 
	 * @param allArtifacts
	 * @return
	 */
	private Set<Artifact> filterOutIrrelevantArtifacts(Iterable<Artifact> allArtifacts)
	{
		final Set<Artifact> results = new LinkedHashSet<Artifact>();
		for (Artifact artifact : allArtifacts)
		{
			if (artifact == null)
			{
				continue;
			}

			if (EXCLUDED_DEPENDENCY_SCOPES.contains(artifact.getScope()))
			{
				continue;
			}

			if ("apk".equalsIgnoreCase(artifact.getType()))
			{
				continue;
			}

			results.add(artifact);
		}
		return results;
	}
}
