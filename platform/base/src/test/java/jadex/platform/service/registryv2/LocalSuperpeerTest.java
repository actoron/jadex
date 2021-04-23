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
		IPlatformConfiguration	baseconf	= STest.createRealtimeTestConfig(LocalSuperpeerTest.class);
//		baseconf.setValue("debugservices", "ITestService");
		baseconf.setValue("superpeerclient.awaonly", false);
		baseconf.setValue("superpeerclient.contimeout", WAITFACTOR*2);
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
