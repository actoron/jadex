package maventest;

import java.io.File;

import org.apache.maven.repository.internal.MavenRepositorySystemSession;
import org.apache.maven.settings.Settings;
import org.apache.maven.settings.building.DefaultSettingsBuilder;
import org.apache.maven.settings.building.DefaultSettingsBuilderFactory;
import org.apache.maven.settings.building.DefaultSettingsBuildingRequest;
import org.apache.maven.settings.building.SettingsBuildingResult;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.collection.CollectRequest;
import org.sonatype.aether.graph.Dependency;
import org.sonatype.aether.graph.DependencyNode;
import org.sonatype.aether.repository.LocalRepository;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.resolution.DependencyRequest;
import org.sonatype.aether.util.artifact.DefaultArtifact;
import org.sonatype.aether.util.graph.PreorderNodeListGenerator;

/**
 *  Fresh test inspired by
 *  https://docs.sonatype.org/display/AETHER/Home
 */
public class MavenTest
{
	public static void main(String[] args) throws Exception
	{
		// Non-plexus:
//        DefaultServiceLocator	locator	= new DefaultServiceLocator();
//        locator.setServices(WagonProvider.class, new ManualWagonProvider());
//        locator.addService(RepositoryConnectorFactory.class, WagonRepositoryConnectorFactory.class);
//        RepositorySystem	repo	= locator.getService( RepositorySystem.class );

        // Plexus:
		RepositorySystem	repo	= new DefaultPlexusContainer().lookup(RepositorySystem.class);
		
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
		
		// Setup session.
        MavenRepositorySystemSession	session	= new MavenRepositorySystemSession();
        LocalRepository	localRepo	= new LocalRepository(local);
        session.setLocalRepositoryManager(repo.newLocalRepositoryManager(localRepo));
        
        // Resolve some dependency
		Dependency dependency = new Dependency(new DefaultArtifact("org.apache.maven:maven-profile:2.2.1"), "compile");
		RemoteRepository central = new RemoteRepository("central", "default", "http://repo1.maven.org/maven2/");
		CollectRequest collectRequest = new CollectRequest();
		collectRequest.setRoot(dependency);
		collectRequest.addRepository(central);
		DependencyNode node = repo.collectDependencies(session, collectRequest).getRoot();
		DependencyRequest dependencyRequest = new DependencyRequest(node, null);
		repo.resolveDependencies(session, dependencyRequest);
		PreorderNodeListGenerator nlg = new PreorderNodeListGenerator();
		node.accept(nlg);
		System.out.println(nlg.getClassPath());
	}
}
