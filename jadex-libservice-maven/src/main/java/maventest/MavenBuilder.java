package maventest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Stack;

import org.apache.maven.model.Model;
import org.apache.maven.model.building.ModelSource;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.collection.CollectRequest;
import org.sonatype.aether.graph.Dependency;
import org.sonatype.aether.graph.DependencyNode;
import org.sonatype.aether.graph.Exclusion;
import org.sonatype.aether.resolution.ArtifactRequest;
import org.sonatype.aether.resolution.ArtifactResult;
import org.sonatype.aether.resolution.DependencyRequest;
import org.sonatype.aether.resolution.DependencyResult;
import org.sonatype.aether.util.artifact.DefaultArtifact;


public class MavenBuilder
{
	// private static final Logger log =
	// Logger.getLogger(MavenBuilder.class.getName());

	private static final File[]				FILE_CAST	= new File[0];

	private final MavenRepositorySystem		system;

	private final RepositorySystemSession	session;

	// these are package visible, so they can be wrapped and make visible for
	// filters
	Stack<Dependency>						dependencies;

	// Map<ArtifactAsKey, MavenDependency> pomInternalDependencyManagement;

	// public Stack<MavenDependency> getDependencies()
	// {
	// return dependencies;
	// }

	// public Map<ArtifactAsKey, MavenDependency>
	// getPomInternalDependencyManagement()
	// {
	// return pomInternalDependencyManagement;
	// }

	/**
	 * Constructs new instance of MavenDependencies
	 */
	public MavenBuilder()
	{
		this.system = new MavenRepositorySystem();
		this.dependencies = new Stack<Dependency>();
		// this.pomInternalDependencyManagement = new HashMap<ArtifactAsKey,
		// MavenDependency>();
		this.session = system.getSession();
	}

	// /**
	// * Configures Maven from a settings.xml file
	// *
	// * @param path A path to a settings.xml configuration file
	// * @return A dependency builder with a configuration from given file
	// */
	// public MavenDependencyResolver configureFrom(String path)
	// {
	// Validate.isReadable(path,
	// "Path to the settings.xml must be defined and accessible");
	// File settings = new File(path);
	// system.loadSettings(settings, session);
	// return this;
	// }

	/**
	 * // * Loads remote repositories for a POM file. If repositories are
	 * defined in // * the parent of the POM file and there are accessible via
	 * local file system, // * they are set as well. // * // * These remote
	 * repositories are used to resolve the artifacts during // * dependency
	 * resolution. // * // * Additionally, it loads dependencies defined in the
	 * POM file model in an // * internal cache, which can be later used to
	 * resolve an artifact without // * explicitly specifying its version. // *
	 * // * @param path A path to the POM file, must not be {@code null} or
	 * empty // * @return A dependency builder with remote repositories set
	 * according to the // * content of POM file. // * @throws Exception //
	 */
	// public void loadReposFromPom(final String path) throws
	// ResolutionException
	// {
	// Validate.isReadable(path,
	// "Path to the pom.xml file must be defined and accessible");
	//
	// File pom = new File(path);
	// Model model = system.loadPom(pom, session);
	//
	// ArtifactTypeRegistry stereotypes = session.getArtifactTypeRegistry();
	//
	// // store all dependency information to be able to retrieve versions later
	// for (org.apache.maven.model.Dependency dependency :
	// model.getDependencies())
	// {
	// MavenDependency d = MavenConverter.fromDependency(dependency,
	// stereotypes);
	// // pomInternalDependencyManagement.put(new
	// ArtifactAsKey(d.getCoordinates()), d);
	// }
	// }

	/**
	 *  Load dependencies from the pom in the input stream.
	 *  @param pom	The pom input stream.
	 *  @param location Optional name/path of the pom used for displaying errors.
	 */
	public void loadDependenciesFromPom(final InputStream pom, final String location)
	{
		loadDependenciesFromPom(new ModelSource()
		{
			public String getLocation()
			{
				return location;
			}
			
			public InputStream getInputStream() throws IOException
			{
				return pom;
			}
		});
	}

	public void loadDependenciesFromPom(final String path)
	{
//	      loadDependenciesFromPom(path, AcceptAllFilter.INSTANCE);
//	   }
//	   
//	   public void loadDependenciesFromPom(final String path, final MavenResolutionFilter filter) throws ResolutionException
//	   {
//	      Validate.isReadable(path, "Path to the pom.xml file must be defined and accessible");
		try
		{
			loadDependenciesFromPom(new FileInputStream(new File(path)), path);
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	public void loadDependenciesFromPom(ModelSource pom)
	{
		Model model = system.loadPom(pom, session);

		// ArtifactTypeRegistry stereotypes = session.getArtifactTypeRegistry();

		for(org.apache.maven.model.Dependency dependency : model
				.getDependencies())
		{
			dependencies.push(asDependency(dependency));
		}
	}

	// Adapted from MavenConverter
	public static Dependency asDependency(org.apache.maven.model.Dependency dep)
	{
		return new Dependency(new DefaultArtifact(dep.getGroupId(),
				dep.getArtifactId(), dep.getClassifier(), dep.getType(),
				dep.getVersion()), dep.getScope(), dep.isOptional(),
				asExclusions(dep.getExclusions()));
	}

	// Adapted from MavenConverter
	public static List<Exclusion> asExclusions(
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


	// /*
	// * (non-Javadoc)
	// *
	// * @see
	// * org.jboss.shrinkwrap.dependencies.DependencyBuilder#artifact(java.lang
	// * .String)
	// */
	// public MavenDependencyResolver artifact(String coordinates) throws
	// ResolutionException
	// {
	// Validate.notNullOrEmpty(coordinates,
	// "Artifact coordinates must not be null or empty");
	//
	// return new MavenArtifactBuilderImpl(this, coordinates);
	// }

	// /*
	// * (non-Javadoc)
	// *
	// * @see
	// * org.jboss.shrinkwrap.dependencies.DependencyBuilder#artifact(java.lang
	// * .String)
	// */
	// public MavenDependencyResolver artifacts(String... coordinates) throws
	// ResolutionException
	// {
	// Validate.notNullAndNoNullValues(coordinates,
	// "Artifacts coordinates must not be null or empty");
	//
	// return new MavenArtifactsBuilderImpl(this, coordinates);
	// }


	// /*
	// * (non-Javadoc)
	// *
	// * @see
	// * org.jboss.shrinkwrap.dependencies.DependencyBuilder.ArtifactBuilder
	// * #exclusion(org.sonatype.aether.graph.Exclusion)
	// */
	// public MavenDependencyResolver exclusion(String coordinates)
	// {
	// MavenDependency dependency = dependencies.peek();
	// dependency.addExclusions(coordinates);
	//
	// return this;
	// }

	// /*
	// * (non-Javadoc)
	// *
	// * @see
	// * org.jboss.shrinkwrap.dependencies.DependencyBuilder.ArtifactBuilder
	// * #exclusions(org.sonatype.aether.graph.Exclusion[])
	// */
	// public MavenDependencyResolver exclusions(String... coordinates)
	// {
	// MavenDependency dependency = dependencies.peek();
	// dependency.addExclusions(coordinates);
	// return this;
	// }

	// /*
	// * (non-Javadoc)
	// *
	// * @see
	// * org.jboss.shrinkwrap.dependencies.DependencyBuilder.ArtifactBuilder
	// * #exclusions(java.util.Collection)
	// */
	// public MavenDependencyResolver exclusions(Collection<String> coordinates)
	// {
	// MavenDependency dependency = dependencies.peek();
	// dependency.addExclusions(coordinates.toArray(new String[0]));
	// return this;
	// }

	// /*
	// * (non-Javadoc)
	// *
	// * @see
	// * org.jboss.shrinkwrap.dependencies.DependencyBuilder.ArtifactBuilder
	// * #optional(boolean)
	// */
	// public MavenDependencyResolver optional(boolean optional)
	// {
	// MavenDependency dependency = dependencies.peek();
	// dependency.setOptional(optional);
	//
	// return this;
	// }

	// /*
	// * (non-Javadoc)
	// *
	// * @see
	// * org.jboss.shrinkwrap.dependencies.DependencyBuilder.ArtifactBuilder
	// * #scope(java.lang.String)
	// */
	// public MavenDependencyResolver scope(String scope)
	// {
	// MavenDependency dependency = dependencies.peek();
	// dependency.setScope(scope);
	//
	// return this;
	// }

	/*
	 * (non-Javadoc)
	 * @see org.jboss.shrinkwrap.dependencies.DependencyBuilder.ArtifactBuilder
	 * #resolveAsFiles()
	 */
	public File[] resolveAsFiles()
	{
		// return resolveAsFiles(AcceptAllFilter.INSTANCE);
		// }
		//
		// /*
		// * (non-Javadoc)
		// *
		// * @see
		// * org.jboss.shrinkwrap.dependencies.DependencyBuilder.ArtifactBuilder
		// * #resolveAsFiles()
		// */
		// public File[] resolveAsFiles(MavenResolutionFilter filter) throws
		// ResolutionException
		// {
		// Validate.notEmpty(dependencies,
		// "No dependencies were set for resolution");

		CollectRequest crequest = new CollectRequest(dependencies, null,
				system.getRemoteRepositories());
		DependencyRequest request = new DependencyRequest(crequest, null);

		// // configure filter
		// filter.configure(Collections.unmodifiableList(dependencies));

		// wrap artifact files to archives
		// Collection<ArtifactResult> artifacts;
		Collection<File> files = new ArrayList<File>();
		try
		{
			DependencyResult result = system.resolveDependencies(session,
					request);// , filter);
			DependencyNode node = result.getRoot();
			List<DependencyNode> children = node.getChildren();
			for(int i = 0; i < children.size(); i++)
			{
				node = children.get(i);
				ArtifactResult res = system.resolveArtifact(session,
						new ArtifactRequest(node));
				files.add(res.getArtifact().getFile());
			}
		}
		catch(Exception e)
		{
			throw new RuntimeException("Unable to resolve dependencies", e);
		}
		// catch (ArtifactResolutionException e)
		// {
		// throw new RuntimeException("Unable to resolve an artifact", e);
		// }

		// Collection<File> files = new ArrayList<File>(artifacts.size());
		// for (ArtifactResult artifact : artifacts)
		// {
		// Artifact a = artifact.getArtifact();
		// // skip all pom artifacts
		// if ("pom".equals(a.getExtension()))
		// {
		// log.info("Removed POM artifact " + a.toString() +
		// " from archive, it's dependencies were fetched.");
		// continue;
		// }
		//
		// files.add(a.getFile());
		// }

		return files.toArray(FILE_CAST);
	}

	// /*
	// * (non-Javadoc)
	// *
	// * @see
	// * org.jboss.shrinkwrap.dependencies.DependencyBuilder.ArtifactBuilder
	// * #resolve()
	// */
	// public <ARCHIVEVIEW extends Assignable> Collection<ARCHIVEVIEW>
	// resolveAs(final Class<ARCHIVEVIEW> archiveView) throws
	// ResolutionException
	// {
	// return resolveAs(archiveView, AcceptAllFilter.INSTANCE);
	// }
	//
	// /*
	// * (non-Javadoc)
	// *
	// * @see
	// * org.jboss.shrinkwrap.dependencies.DependencyBuilder.ArtifactBuilder
	// * #resolve(org.sonatype.aether.graph.DependencyFilter)
	// */
	// public <ARCHIVEVIEW extends Assignable> Collection<ARCHIVEVIEW>
	// resolveAs(final Class<ARCHIVEVIEW> archiveView,
	// MavenResolutionFilter filter) throws ResolutionException
	// {
	// // Precondition checks
	// if (archiveView == null)
	// {
	// throw new IllegalArgumentException("Archive view must be specified");
	// }
	// if (filter == null)
	// {
	// throw new IllegalArgumentException("Filter must be specified");
	// }
	//
	// final File[] files = resolveAsFiles(filter);
	// final Collection<ARCHIVEVIEW> archives = new
	// ArrayList<ARCHIVEVIEW>(files.length);
	// for (final File file : files)
	// {
	// final ARCHIVEVIEW archive = ShrinkWrap.create(ZipImporter.class,
	// file.getName()).importFrom(convert(file))
	// .as(archiveView);
	// archives.add(archive);
	// }
	//
	// return archives;
	// }

	// // converts a file to a ZIP file
	// private ZipFile convert(File file) throws ResolutionException
	// {
	// try
	// {
	// return new ZipFile(file);
	// }
	// catch (ZipException e)
	// {
	// throw new ResolutionException("Unable to treat dependency artifact \"" +
	// file.getAbsolutePath() + "\" as a ZIP file", e);
	// }
	// catch (IOException e)
	// {
	// throw new ResolutionException("Unable to access artifact file at \"" +
	// file.getAbsolutePath() + "\".", e);
	// }
	// }

	// class MavenArtifactBuilderImpl implements MavenDependencyResolverInternal
	// {
	// private final MavenDependencyResolverInternal delegate;
	//
	// MavenArtifactBuilderImpl(final MavenDependencyResolverInternal
	// delegate,String coordinates) throws ResolutionException
	// {
	// assert delegate!=null:"Delegate must be specified";
	// this.delegate = delegate;
	// coordinates =
	// MavenConverter.resolveArtifactVersion(pomInternalDependencyManagement,
	// coordinates);
	// MavenDependency dependency = new MavenDependencyImpl(coordinates);
	// delegate.getDependencies().push(dependency);
	// }
	//
	// /*
	// * (non-Javadoc)
	// *
	// * @see
	// * org.jboss.shrinkwrap.dependencies.DependencyBuilder#artifact(java.lang
	// * .String)
	// */
	// @Override
	// public MavenDependencyResolver artifact(String coordinates)
	// {
	// Validate.notNullOrEmpty(coordinates,
	// "Artifact coordinates must not be null or empty");
	// return new MavenArtifactsBuilderImpl(this, coordinates);
	// }
	//
	// /*
	// * (non-Javadoc)
	// *
	// * @see
	// * org.jboss.shrinkwrap.dependencies.DependencyBuilder#artifacts(java.
	// * lang.String[])
	// */
	// @Override
	// public MavenDependencyResolver artifacts(String... coordinates) throws
	// ResolutionException
	// {
	// Validate.notNullAndNoNullValues(coordinates,
	// "Artifacts coordinates must not be null or empty");
	// return new MavenArtifactsBuilderImpl(this, coordinates);
	// }
	//
	// @Override
	// public <ARCHIVEVIEW extends Assignable> Collection<ARCHIVEVIEW>
	// resolveAs(Class<ARCHIVEVIEW> archiveView)
	// throws ResolutionException
	// {
	// return delegate.resolveAs(archiveView);
	// }
	//
	// public <ARCHIVEVIEW extends Assignable> Collection<ARCHIVEVIEW>
	// resolveAs(Class<ARCHIVEVIEW> archiveView,
	// MavenResolutionFilter filter) throws ResolutionException
	// {
	// return delegate.resolveAs(archiveView, filter);
	// }
	//
	// public File[] resolveAsFiles() throws ResolutionException
	// {
	// return delegate.resolveAsFiles();
	// }
	//
	// public MavenDependencyResolver configureFrom(String path)
	// {
	// return delegate.configureFrom(path);
	// }
	//
	// public File[] resolveAsFiles(MavenResolutionFilter filter) throws
	// ResolutionException
	// {
	// return delegate.resolveAsFiles(filter);
	// }
	//
	// @Override
	// public MavenDependencyResolver loadReposFromPom(String path) throws
	// ResolutionException
	// {
	// return delegate.loadReposFromPom(path);
	// }
	//
	// public MavenDependencyResolver scope(String scope)
	// {
	// return delegate.scope(scope);
	// }
	//
	// public MavenDependencyResolver optional(boolean optional)
	// {
	// return delegate.optional(optional);
	// }
	//
	// public MavenDependencyResolver exclusion(String exclusion)
	// {
	// return delegate.exclusion(exclusion);
	// }
	//
	// public MavenDependencyResolver exclusions(String... exclusions)
	// {
	// return delegate.exclusions(exclusions);
	// }
	//
	// public MavenDependencyResolver exclusions(Collection<String> exclusions)
	// {
	// return delegate.exclusions(exclusions);
	// }
	//
	// public Stack<MavenDependency> getDependencies()
	// {
	// return delegate.getDependencies();
	// }
	//
	// public Map<ArtifactAsKey, MavenDependency>
	// getPomInternalDependencyManagement()
	// {
	// return delegate.getPomInternalDependencyManagement();
	// }
	//
	// public MavenDependencyResolver loadDependenciesFromPom(String path)
	// throws ResolutionException
	// {
	// return delegate.loadDependenciesFromPom(path);
	// }
	//
	// public MavenDependencyResolver loadDependenciesFromPom(String path,
	// MavenResolutionFilter filter)
	// throws ResolutionException
	// {
	// return delegate.loadDependenciesFromPom(path, filter);
	// }
	//
	// }
	//
	// static class MavenArtifactsBuilderImpl implements
	// MavenDependencyResolverInternal
	// {
	// private final MavenDependencyResolverInternal delegate;
	//
	// private int size;
	//
	// MavenArtifactsBuilderImpl(final MavenDependencyResolverInternal
	// delegate,final String... coordinates)
	// {
	// assert delegate !=null:"Delegate must be specified";
	// this.delegate = delegate;
	//
	// this.size = coordinates.length;
	//
	// for (String coords : coordinates)
	// {
	// coords =
	// MavenConverter.resolveArtifactVersion(delegate.getPomInternalDependencyManagement(),
	// coords);
	// MavenDependency dependency = new MavenDependencyImpl(coords);
	// delegate.getDependencies().push(dependency);
	// }
	// }
	//
	// /**
	// * {@inheritDoc}
	// * @see
	// org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver#optional(boolean)
	// */
	// @Override
	// public MavenDependencyResolver optional(boolean optional)
	// {
	// List<MavenDependency> workplace = new ArrayList<MavenDependency>();
	//
	// int i;
	// for (i = 0; i < size; i++)
	// {
	// MavenDependency dependency = delegate.getDependencies().pop();
	// workplace.add(dependency.setOptional(optional));
	// }
	//
	// for (; i > 0; i--)
	// {
	// delegate.getDependencies().push(workplace.get(i - 1));
	// }
	//
	// return this;
	// }
	//
	// /**
	// * {@inheritDoc}
	// * @see
	// org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver#scope(java.lang.String)
	// */
	// @Override
	// public MavenDependencyResolver scope(String scope)
	// {
	// List<MavenDependency> workplace = new ArrayList<MavenDependency>();
	//
	// int i;
	// for (i = 0; i < size; i++)
	// {
	// MavenDependency dependency = delegate.getDependencies().pop();
	// workplace.add(dependency.setScope(scope));
	// }
	//
	// for (; i > 0; i--)
	// {
	// delegate.getDependencies().push(workplace.get(i - 1));
	// }
	//
	// return this;
	// }
	//
	// /**
	// * {@inheritDoc}
	// * @see
	// org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver#exclusions(java.lang.String[])
	// */
	// @Override
	// public MavenDependencyResolver exclusions(String... coordinates)
	// {
	// List<MavenDependency> workplace = new ArrayList<MavenDependency>();
	//
	// int i;
	// for (i = 0; i < size; i++)
	// {
	// MavenDependency dependency = delegate.getDependencies().pop();
	// workplace.add(dependency.addExclusions(coordinates));
	// }
	//
	// for (; i > 0; i--)
	// {
	// delegate.getDependencies().push(workplace.get(i - 1));
	// }
	//
	// return this;
	// }
	//
	// /*
	// * (non-Javadoc)
	// *
	// * @see org.jboss.shrinkwrap.dependencies.impl.MavenDependencies.
	// * MavenArtifactBuilder#exclusions(java.util.Collection)
	// */
	// @Override
	// public MavenDependencyResolver exclusions(Collection<String> coordinates)
	// {
	// List<MavenDependency> workplace = new ArrayList<MavenDependency>();
	//
	// int i;
	// for (i = 0; i < size; i++)
	// {
	// MavenDependency dependency = delegate.getDependencies().pop();
	// workplace.add(dependency.addExclusions(coordinates.toArray(new
	// String[0])));
	// }
	//
	// for (; i > 0; i--)
	// {
	// delegate.getDependencies().push(workplace.get(i - 1));
	// }
	//
	// return this;
	// }
	//
	// /*
	// * (non-Javadoc)
	// *
	// * @see org.jboss.shrinkwrap.dependencies.impl.MavenDependencies.
	// * MavenArtifactBuilder#exclusion(org.sonatype.aether.graph.Exclusion)
	// */
	// @Override
	// public MavenDependencyResolver exclusion(String exclusion)
	// {
	// List<MavenDependency> workplace = new ArrayList<MavenDependency>();
	//
	// int i;
	// for (i = 0; i < size; i++)
	// {
	// MavenDependency dependency = delegate.getDependencies().pop();
	// workplace.add(dependency.addExclusions(exclusion));
	// }
	//
	// for (; i > 0; i--)
	// {
	// delegate.getDependencies().push(workplace.get(i - 1));
	// }
	//
	// return this;
	// }
	//
	// @Override
	// public int hashCode()
	// {
	// return delegate.hashCode();
	// }
	//
	// @Override
	// public boolean equals(Object obj)
	// {
	// return delegate.equals(obj);
	// }
	//
	// @Override
	// public MavenDependencyResolver configureFrom(String path)
	// {
	// return delegate.configureFrom(path);
	// }
	//
	// @Override
	// public MavenDependencyResolver loadReposFromPom(String path) throws
	// ResolutionException
	// {
	// return delegate.loadReposFromPom(path);
	// }
	//
	// @Override
	// public MavenDependencyResolver artifact(String coordinates) throws
	// ResolutionException
	// {
	// return delegate.artifact(coordinates);
	// }
	//
	// @Override
	// public MavenDependencyResolver artifacts(String... coordinates) throws
	// ResolutionException
	// {
	// return delegate.artifacts(coordinates);
	// }
	//
	// @Override
	// public File[] resolveAsFiles() throws ResolutionException
	// {
	// return delegate.resolveAsFiles();
	// }
	//
	// @Override
	// public File[] resolveAsFiles(MavenResolutionFilter filter) throws
	// ResolutionException
	// {
	// return delegate.resolveAsFiles(filter);
	// }
	//
	// @Override
	// public String toString()
	// {
	// return delegate.toString();
	// }
	//
	// @Override
	// public <ARCHIVEVIEW extends Assignable> Collection<ARCHIVEVIEW>
	// resolveAs(Class<ARCHIVEVIEW> archiveView)
	// throws ResolutionException
	// {
	// return delegate.resolveAs(archiveView);
	// }
	//
	// @Override
	// public <ARCHIVEVIEW extends Assignable> Collection<ARCHIVEVIEW>
	// resolveAs(Class<ARCHIVEVIEW> archiveView,
	// MavenResolutionFilter filter) throws ResolutionException
	// {
	// return delegate.resolveAs(archiveView, filter);
	// }
	//
	// @Override
	// public Stack<MavenDependency> getDependencies()
	// {
	// return delegate.getDependencies();
	// }
	//
	// @Override
	// public Map<ArtifactAsKey, MavenDependency>
	// getPomInternalDependencyManagement()
	// {
	// return delegate.getPomInternalDependencyManagement();
	// }
	//
	// public MavenDependencyResolver loadDependenciesFromPom(String path)
	// throws ResolutionException
	// {
	// return delegate.loadDependenciesFromPom(path);
	// }
	//
	// public MavenDependencyResolver loadDependenciesFromPom(String path,
	// MavenResolutionFilter filter)
	// throws ResolutionException
	// {
	// return delegate.loadDependenciesFromPom(path, filter);
	// }
	//
	// }
}
