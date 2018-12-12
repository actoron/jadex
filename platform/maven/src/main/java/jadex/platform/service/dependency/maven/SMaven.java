package jadex.platform.service.dependency.maven;

import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.repository.RepositoryPolicy;
import org.sonatype.aether.util.artifact.DefaultArtifact;

/**
 *  Helper class for converting between maven and aether objects.
 */
public class SMaven
{
	/**
	 *  Convert a maven model repository to an aether remote repository.
	 */
	public static RemoteRepository	convertRepository(org.apache.maven.model.Repository rep)
	{
		RemoteRepository	repo	= new RemoteRepository(rep.getId(), rep.getLayout(), rep.getUrl());
		RepositoryPolicy	snapshots	= rep.getSnapshots()==null ? null :
			new RepositoryPolicy(Boolean.getBoolean(rep.getSnapshots().getEnabled()), rep.getSnapshots().getUpdatePolicy(), rep.getSnapshots().getChecksumPolicy());
		RepositoryPolicy	releases	= rep.getReleases()==null ? null :
			new RepositoryPolicy(Boolean.getBoolean(rep.getReleases().getEnabled()), rep.getReleases().getUpdatePolicy(), rep.getReleases().getChecksumPolicy());
		repo.setPolicy(true, snapshots);
		repo.setPolicy(false, releases);	
		return repo;
	}

	/**
	 *  Convert a maven settings repository to an aether remote repository.
	 */
	public static RemoteRepository	convertRepository(org.apache.maven.settings.Repository rep)
	{
		RemoteRepository	repo	= new RemoteRepository(rep.getId(), rep.getLayout(), rep.getUrl());
		RepositoryPolicy	snapshots	= rep.getSnapshots()==null ? null :
			new RepositoryPolicy(rep.getSnapshots().isEnabled(), rep.getSnapshots().getUpdatePolicy(), rep.getSnapshots().getChecksumPolicy());
		RepositoryPolicy	releases	= rep.getReleases()==null ? null :
			new RepositoryPolicy(rep.getReleases().isEnabled(), rep.getReleases().getUpdatePolicy(), rep.getReleases().getChecksumPolicy());
		repo.setPolicy(true, snapshots);
		repo.setPolicy(false, releases);	
		return repo;
	}
	
	/**
	 *  Convert a maven model dependency to an aether artifact.
	 */
	public static Artifact convertDependency(org.apache.maven.model.Dependency dep)
	{
		return new DefaultArtifact(dep.getGroupId(), dep.getArtifactId(),
			dep.getClassifier(), dep.getType(), dep.getVersion());
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
