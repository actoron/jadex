package jadex.platform.service.registryv2;


import org.junit.Test;

import jadex.base.IPlatformConfiguration;
import jadex.base.test.util.STest;
import jadex.bridge.IExternalAccess;
import jadex.commons.security.PemKeyPair;
import jadex.commons.security.SSecurity;
import jadex.platform.service.security.auth.AbstractAuthenticationSecret;

/**
 *  Test registry infrastructure with one global and one local superpeer
 *  and no awareness at all.
 */
public class GlobalSuperpeerTest	extends AbstractInfrastructureTest
{
	//-------- constants --------
	
	/** Client configuration for platform used for searching. */
	public static final IPlatformConfiguration	CLIENTCONF;

	/** Plain provider configuration. */
	public static final IPlatformConfiguration	PROCONF;

	/** Local superpeer platform configuration. */
	public static final IPlatformConfiguration	SPCONF;

	/** Global superpeer and relay platform configuration. */
	public static final IPlatformConfiguration	RELAYCONF;

	static
	{
		PemKeyPair ca = SSecurity.createTestCACert();
		PemKeyPair cert = SSecurity.createTestCert(ca);
		AbstractAuthenticationSecret clientsecret = AbstractAuthenticationSecret.fromKeyPair(ca, true);
		AbstractAuthenticationSecret serversecret = AbstractAuthenticationSecret.fromKeyPair(cert, false, ca);

		
		IPlatformConfiguration	baseconf	= STest.getDefaultTestConfig();
		baseconf.setValue("superpeerclient.awaonly", false);
		baseconf.setValue("passiveawarenessintravm", false);
		baseconf.setValue("passiveawarenesscatalog", true);
		baseconf.setValue("platformurls", "ws://ssp1@localhost:23751");
		baseconf.setNetworkNames(SuperpeerClientAgent.GLOBAL_NETWORK_NAME, STest.testnetwork_name);
		baseconf.setNetworkSecrets(clientsecret.toString(), STest.testnetwork_pass);
		baseconf.setLogging(true);
			
		CLIENTCONF	= baseconf.clone();
		CLIENTCONF.setPlatformName("client_*");
		
		PROCONF	= baseconf.clone();
		PROCONF.addComponent(ProviderAgent.class);
		PROCONF.addComponent(LocalProviderAgent.class);
		PROCONF.setPlatformName("provider_*");
		
		SPCONF	= baseconf.clone();
		SPCONF.setValue("superpeer", true);
		SPCONF.setPlatformName("SP_*");
		
		RELAYCONF	= baseconf.clone();
		RELAYCONF.setValue("superpeer", true);
		RELAYCONF.setValue("supersuperpeer", true);
		RELAYCONF.setValue("rt.forwarding", true);
		RELAYCONF.setValue("wsport", 23751);
		RELAYCONF.setPlatformName("ssp1");
		RELAYCONF.setNetworkNames(SuperpeerClientAgent.GLOBAL_NETWORK_NAME);
		RELAYCONF.setNetworkSecrets(serversecret.toString());
	}
	
	//-------- test cases --------

	/**
	 *  Test connection.
	 */
//	@Test
//	public void testConnection()
//	{
//		IExternalAccess	relay	= createPlatform(RELAYCONF);
//		IExternalAccess	sp	= createPlatform(SPCONF);
////		IExternalAccess	client	= createPlatform(CLIENTCONF);
//		
//		// All connect to relay.
//		waitForSuperpeerConnections(relay, sp);//, client);
//		
//		// Client connect to local SP.
////		waitForSuperpeerConnections(sp, client);
//	}
}
