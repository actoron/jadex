package jadex.platform.service.registryv2;


import jadex.base.IPlatformConfiguration;
import jadex.base.test.util.STest;
import jadex.platform.service.registry.SuperpeerRegistryAgent;

/**
 *  Test basic search and query managing functionality
 *  with local awareness and with local super peer.
 */
public class LocalSuperpeerTest	extends AbstractSearchQueryTest
{
	//-------- constants --------
	
	/** Client configuration for platform used for searching. */
	public static final IPlatformConfiguration	CLIENTCONF;

	/** Plain provider configuration. */
	public static final IPlatformConfiguration	PROCONF;

	/** Superpeer platform configuration. */
	public static final IPlatformConfiguration	SPCONF;

	static
	{
		IPlatformConfiguration	baseconf	= STest.getDefaultTestConfig(LocalSuperpeerTest.class);
		baseconf.setValue("superpeerclient.awaonly", false);
		baseconf.setValue("superpeerclient.contimeout", WAITFACTOR*2);
		baseconf.setValue("superpeerclient.pollingrate", WAITFACTOR*0.3333); // -> 30 sec * 0.1 * 0.3333 ~= 1 sec
		// Remote only -> no simulation please
		baseconf.getExtendedPlatformConfiguration().setSimul(false);
		baseconf.getExtendedPlatformConfiguration().setSimulation(false);
//		baseconf.setLogging(true);
//		baseconf.setValue("rt.debug", true);
		baseconf.getExtendedPlatformConfiguration().setDebugFutures(true);

		
		CLIENTCONF	= baseconf.clone();
		CLIENTCONF.setPlatformName("client");
		
		PROCONF	= baseconf.clone();
		PROCONF.addComponent(GlobalProviderAgent.class);
		PROCONF.addComponent(NetworkProviderAgent.class);
		PROCONF.addComponent(LocalProviderAgent.class);
		PROCONF.setPlatformName("provider");
		
		SPCONF	= baseconf.clone();
		SPCONF.addComponent(SuperpeerRegistryAgent.class);
		SPCONF.setPlatformName("SP");
	}

	//-------- constructors --------
	
	/**
	 *  Create the test.
	 */
	public LocalSuperpeerTest()
	{
		super(true, CLIENTCONF, PROCONF, SPCONF, null);
	}
}
