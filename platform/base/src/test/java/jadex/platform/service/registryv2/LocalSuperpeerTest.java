package jadex.platform.service.registryv2;


import jadex.base.IPlatformConfiguration;
import jadex.base.test.util.STest;

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
		IPlatformConfiguration	baseconf	= STest.getDefaultTestConfig();
		baseconf.setValue("superpeerclient.awaonly", false);
		baseconf.setValue("superpeerclient.pollingrate", WAITFACTOR/2); 	// -> 750 millis.
		// Remote only -> no simulation please
		baseconf.getExtendedPlatformConfiguration().setSimul(false);
		baseconf.getExtendedPlatformConfiguration().setSimulation(false);
//		baseconf.setLogging(true);
//		baseconf.setValue("rt.debug", true);

		
		CLIENTCONF	= baseconf.clone();
		CLIENTCONF.setPlatformName("client_*");
		
		PROCONF	= baseconf.clone();
		PROCONF.addComponent(ProviderAgent.class);
		PROCONF.addComponent(LocalProviderAgent.class);
		PROCONF.setPlatformName("provider_*");
		
		SPCONF	= baseconf.clone();
		SPCONF.addComponent(SuperpeerRegistryAgent.class);
		SPCONF.setPlatformName("SP_*");
	}

	//-------- constructors --------
	
	public LocalSuperpeerTest()
	{
		super(true, CLIENTCONF, PROCONF, SPCONF, null);
	}
}
