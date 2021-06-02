package jadex.platform;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Random;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.Test;

import jadex.base.IPlatformConfiguration;
import jadex.base.PlatformConfigurationHandler;
import jadex.base.Starter;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.security.ISecurityService;
import jadex.platform.service.settings.PlatformSettings;

/**
 *  Test that settings can be saved and loaded.
 */
public class SettingsTest
{
	/**
	 *  Test that two platforms with same prefix will share the default network.
	 */
	@Test
	public void	testDefaultNetwork() throws IOException
	{
		// Start (and kill) two platforms with same prefix and get their networks 
		IPlatformConfiguration	config	= PlatformConfigurationHandler.getMinimal();
		config.getExtendedPlatformConfiguration().setSecurity(true);
		config.setPlatformName("testplatform"+new Random().nextInt()+"_*");
		IExternalAccess	exta1	= Starter.createPlatform(config).get();
		ISecurityService	sec1	= exta1.searchService(new ServiceQuery<>(ISecurityService.class)).get();
		Set<String>	networks1	= sec1.getNetworkNames().get();
		IExternalAccess	exta2	= Starter.createPlatform(config).get();
		ISecurityService	sec2	= exta2.searchService(new ServiceQuery<>(ISecurityService.class)).get();
		Set<String>	networks2	= sec2.getNetworkNames().get();
		exta1.killComponent().get();
		exta2.killComponent().get();
		
		// Check test results.
		assertFalse("There should be at least one (i.e. default) network", networks1.isEmpty());
		assertEquals("Both platforms should have the same networks", networks1, networks2);
		
		// Cleanup settings folder(s) on success (i.e. keep if failed above).
		File settingsdir	= PlatformSettings.getSettingsDir(exta1.getId()); 
		if(settingsdir.exists() && settingsdir.isDirectory())
		{
			// Simplest(!???) way to delete a directory in Java:
			// https://stackoverflow.com/questions/35988192/java-nio-most-concise-recursive-directory-delete
			try (Stream<Path> walk = Files.walk(settingsdir.toPath())) {
			    walk.sorted(Comparator.reverseOrder())
			        .map(Path::toFile)
//			        .peek(System.out::println)
			        .forEach(File::delete);
			}
		}
		settingsdir	= PlatformSettings.getSettingsDir(exta2.getId()); 
		if(settingsdir.exists() && settingsdir.isDirectory())
		{
			// Simplest(!???) way to delete a directory in Java:
			// https://stackoverflow.com/questions/35988192/java-nio-most-concise-recursive-directory-delete
			try (Stream<Path> walk = Files.walk(settingsdir.toPath())) {
			    walk.sorted(Comparator.reverseOrder())
			        .map(Path::toFile)
//			        .peek(System.out::println)
			        .forEach(File::delete);
			}
		}
	}
}
