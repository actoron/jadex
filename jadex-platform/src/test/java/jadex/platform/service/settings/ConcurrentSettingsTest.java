package jadex.platform.service.settings;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import jadex.base.IPlatformConfiguration;
import jadex.base.PlatformConfigurationHandler;
import jadex.base.Starter;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.settings.ISettingsService;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 *  Test if starting platforms in parallel causes corrupt settings.
 */
public class ConcurrentSettingsTest
{
	/**
	 *  Perform the test.
	 * @throws Exception 
	 */
//	@Test
	public void	testMultiplePlatforms() throws Exception
	{
		int	number	= 3;	// Number of platforms to create in parallel
//		long	timeout	= Starter.getScaledLocalDefaultTimeout(null, number);
		long	timeout	= Starter.getLocalDefaultTimeout(null);
		
		IPlatformConfiguration	conf	= PlatformConfigurationHandler.getMinimal();
		conf.getExtendedPlatformConfiguration().setSecurity(true);	// enabled to write/read password
		conf.setPlatformName("settingstest_*");
		
		// Delete settings to generate new password
		new File("settingstest"+SettingsAgent.SETTINGS_EXTENSION).delete();

		// Start platforms
		List<IFuture<IExternalAccess>>	futures	= new ArrayList<IFuture<IExternalAccess>>();
		for(int i=0; i<number; i++)
		{
			futures.add(Starter.createPlatform(conf));
		}
		
		// Wait for platforms
		IExternalAccess[]	platforms	= new IExternalAccess[number];
		for(int i=0; i<number; i++)
		{
			platforms[i]	= futures.get(i).get(timeout);
		}
		
		// Check passwords
		String	passwd	= null;
		for(int i=0; i<number; i++)
		{
			String	thepasswd	= platforms[i].scheduleStep(new IComponentStep<String>()
			{
				@Override
				public IFuture<String> execute(IInternalAccess ia)
				{
					ISettingsService	seser	=	SServiceProvider.getLocalService(ia, ISettingsService.class);
					String	ret	= seser.getProperties("securityservice").get().getStringProperty("platformsecret");
					return new Future<String>(ret);
				}
			}).get(timeout);
			System.out.println(thepasswd);
			
			if(i==0)
			{
				passwd	= thepasswd;
			}
			else
			{
				assertEquals("Passwords differ", passwd, thepasswd);
			}
		}
		
		// Kill platforms
		for(int i=0; i<number; i++)
		{
			platforms[i].killComponent().get(timeout);
		}
	}
}
